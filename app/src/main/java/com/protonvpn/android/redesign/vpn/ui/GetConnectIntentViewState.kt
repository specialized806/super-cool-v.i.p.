/*
 * Copyright (c) 2023. Proton AG
 *
 * This file is part of ProtonVPN.
 *
 * ProtonVPN is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ProtonVPN is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ProtonVPN.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.protonvpn.android.redesign.vpn.ui

import com.protonvpn.android.models.vpn.Server
import com.protonvpn.android.redesign.CountryId
import com.protonvpn.android.redesign.countries.Translator
import com.protonvpn.android.redesign.vpn.ConnectIntent
import com.protonvpn.android.redesign.vpn.ServerFeature
import com.protonvpn.android.servers.ServerManager2
import dagger.Reusable
import javax.inject.Inject

@Reusable
class GetConnectIntentViewState @Inject constructor(
    private val serverManager: ServerManager2,
    private val translator: Translator,
) {

    // Note: this is a suspending function being called in a loop which makes it potentially slow.
    // See RecentListViewStateFlow.createRecentsViewState
    suspend operator fun invoke(connectIntent: ConnectIntent, isFreeUser: Boolean, connectedServer: Server? = null): ConnectIntentViewState =
        when (connectIntent) {
            is ConnectIntent.FastestInCountry -> {
                if (isFreeUser && connectIntent.country.isFastest) fastestFreeServer(connectedServer)
                else fastestInCountry(connectIntent, connectedServer)
            }
            is ConnectIntent.FastestInCity -> fastestInCity(connectIntent, connectedServer)
            is ConnectIntent.FastestInState -> fastestInState(connectIntent, connectedServer)
            is ConnectIntent.SecureCore -> secureCore(connectIntent, connectedServer)
            is ConnectIntent.Gateway -> gateway(connectIntent, connectedServer)
            is ConnectIntent.Server -> specificServer(connectIntent, connectedServer)
        }

    private fun fastestInCountry(
        connectIntent: ConnectIntent.FastestInCountry,
        connectedServer: Server? = null
    ): ConnectIntentViewState {
        val primary = if (connectIntent.country.isFastest) {
            ConnectIntentPrimaryLabel.Fastest(connectedServer?.exitCountryId(), isSecureCore = false, isFree = false)
        } else {
            ConnectIntentPrimaryLabel.Country(
                exitCountry = fastestOrConnectedOrIntent(connectIntent.country, connectedServer, Server::exitCountry),
                entryCountry = null,
            )
        }
        val secondary = connectedCountryIfFastest(connectIntent.country, connectedServer, Server::entryCountry)?.let {
            ConnectIntentSecondaryLabel.Country(it, null)
        }
        return ConnectIntentViewState(primary, secondary, effectiveServerFeatures(connectIntent, connectedServer))
    }

    private suspend fun fastestFreeServer(connectedServer: Server? = null) =
        ConnectIntentViewState(
            primaryLabel = ConnectIntentPrimaryLabel.Fastest(
                connectedServer?.exitCountryId(),
                isSecureCore = false,
                isFree = true
            ),
            secondaryLabel = if (connectedServer != null) {
                countryWithServerNumberSecondaryLabel(connectedServer)
            } else {
                ConnectIntentSecondaryLabel.FastestFreeServer(serverManager.getFreeCountries().size)
            },
            serverFeatures = emptySet()
        )

    private fun fastestInState(connectIntent: ConnectIntent.FastestInState, connectedServer: Server? = null) =
        ConnectIntentViewState(
            primaryLabel = ConnectIntentPrimaryLabel.Country(
                exitCountry = connectedServer?.entryCountry?.let { CountryId(it) } ?: connectIntent.country,
                entryCountry = null,
            ),
            secondaryLabel = ConnectIntentSecondaryLabel.RawText(
                connectedServer?.displayState ?: connectedServer?.displayCity ?: translator.getState(connectIntent.stateEn)
            ),
            serverFeatures = effectiveServerFeatures(connectIntent, connectedServer)
        )

    private fun fastestInCity(connectIntent: ConnectIntent.FastestInCity, connectedServer: Server? = null) =
        ConnectIntentViewState(
            primaryLabel = ConnectIntentPrimaryLabel.Country(
                exitCountry = connectedServer?.entryCountry?.let { CountryId(it) } ?: connectIntent.country,
                entryCountry = null,
            ),
            secondaryLabel = ConnectIntentSecondaryLabel.RawText(
                connectedServer?.displayCity ?: translator.getCity(connectIntent.cityEn)
            ),
            serverFeatures = effectiveServerFeatures(connectIntent, connectedServer)
        )

    private fun secureCore(
        connectIntent: ConnectIntent.SecureCore,
        connectedServer: Server? = null
    ): ConnectIntentViewState {
        val isFastestFastest = with(connectIntent) { exitCountry.isFastest && entryCountry.isFastest }
        val primaryLabel = if (isFastestFastest) {
            ConnectIntentPrimaryLabel.Fastest(connectedServer?.exitCountryId(), isSecureCore = true, isFree = false)
        } else {
            ConnectIntentPrimaryLabel.Country(
                exitCountry = fastestOrConnectedOrIntent(
                    connectIntent.exitCountry,
                    connectedServer,
                    Server::exitCountry
                ),
                entryCountry = fastestOrConnectedOrIntent(
                    connectIntent.entryCountry,
                    connectedServer,
                    Server::entryCountry
                ),
            )
        }
        val secondaryLabel = ConnectIntentSecondaryLabel.SecureCore(
            exit = connectedCountryIfFastest(connectIntent.exitCountry, connectedServer, Server::exitCountry),
            entry = connectedServer?.entryCountry?.let { CountryId(it) } ?: connectIntent.entryCountry
        )
        return ConnectIntentViewState(primaryLabel, secondaryLabel, effectiveServerFeatures(connectIntent, connectedServer))
    }

    private suspend fun gateway(connectIntent: ConnectIntent.Gateway, connectedServer: Server?): ConnectIntentViewState {
        val labelServer = connectIntent.serverId?.let { connectedServer ?: serverManager.getServerById(it) }
        val secondaryLabel = labelServer?.let { countryWithServerNumberSecondaryLabel(it) }
        val intentServer = connectIntent.serverId?.let { serverManager.getServerById(it) }
        val country = connectedServer?.exitCountryId() ?: intentServer?.let { CountryId(it.exitCountry) }
        return ConnectIntentViewState(
            primaryLabel = ConnectIntentPrimaryLabel.Gateway(connectIntent.gatewayName, country),
            secondaryLabel = secondaryLabel,
            serverFeatures = emptySet()
        )
    }

    private suspend fun specificServer(
        connectIntent: ConnectIntent.Server,
        connectedServer: Server? = null
    ): ConnectIntentViewState {
        val server = connectedServer ?: serverManager.getServerById(connectIntent.serverId)
        return if (server != null) {
            ConnectIntentViewState(
                primaryLabel = ConnectIntentPrimaryLabel.Country(
                    exitCountry = CountryId(server.exitCountry),
                    entryCountry = CountryId(server.entryCountry).takeIf { server.isSecureCoreServer },
                ),
                secondaryLabel = serverSecondaryLabel(server),
                serverFeatures = effectiveServerFeatures(connectIntent, connectedServer)
            )
        } else {
            // TODO: how do we handle this case?
            ConnectIntentViewState(ConnectIntentPrimaryLabel.Country(CountryId.fastest, null), null, emptySet())
        }
    }

    private fun fastestOrConnectedOrIntent(
        connectIntentCountry: CountryId,
        connectedServer: Server?,
        getCountry: (Server) -> String
    ): CountryId =
        if (connectIntentCountry.isFastest || connectedServer == null) {
            connectIntentCountry
        } else {
            CountryId(getCountry(connectedServer))
        }

    private fun connectedCountryIfFastest(
        connectIntentCountry: CountryId,
        connectedServer: Server?,
        getCountry: (Server) -> String
    ): CountryId? =
        if (connectIntentCountry.isFastest && connectedServer != null) {
            CountryId(getCountry(connectedServer))
        } else {
            null
        }

    private fun effectiveServerFeatures(
        connectIntent: ConnectIntent,
        connectedServer: Server?
    ) = if (connectedServer != null) {
        connectIntent.features.intersect(ServerFeature.fromServer(connectedServer))
    } else {
        connectIntent.features
    }

    private fun serverSecondaryLabel(server: Server): ConnectIntentSecondaryLabel.RawText = with(server) {
        val text = if (isFreeServer) {
            val dashIndex = serverName.indexOf('-')
            if (dashIndex != -1) {
                serverName.drop(dashIndex + 1)
            } else {
                serverName
            }
        } else {
            listOfNotNull(
                displayState ?: displayCity,
                serverName.dropWhile { it != '#' }
            ).joinToString(" ")
        }
        ConnectIntentSecondaryLabel.RawText(text)
    }

    private fun countryWithServerNumberSecondaryLabel(server: Server): ConnectIntentSecondaryLabel =
        ConnectIntentSecondaryLabel.Country(CountryId(server.exitCountry), server.serverName.dropWhile { it != '#' })

    private fun Server.exitCountryId() = CountryId(exitCountry)
}
