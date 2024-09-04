/*
 * Copyright (c) 2024 Proton AG
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

package com.protonvpn.android.profiles.usecases

import com.protonvpn.android.auth.usecase.CurrentUser
import dagger.Reusable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.proton.core.domain.entity.UserId
import me.proton.core.featureflag.domain.ExperimentalProtonFeatureFlag
import me.proton.core.featureflag.domain.FeatureFlagManager
import me.proton.core.featureflag.domain.entity.FeatureId
import me.proton.core.network.domain.ApiException
import javax.inject.Inject

@Reusable
class NewProfilesMvpEnabled @Inject constructor(
    private val currentUser: CurrentUser,
    private val featureFlagManager: FeatureFlagManager
) {
    @OptIn(ExperimentalProtonFeatureFlag::class)
    suspend operator fun invoke(): Boolean =
        featureFlagManager.getValue(currentUser.user()?.userId, FeatureId(NEW_PROFILES_MVP_ENABLED_FLAG))

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() =
        currentUser.vpnUserFlow.flatMapLatest { vpnUser ->
            featureFlagManager.safeObserve(vpnUser?.userId, FeatureId(NEW_PROFILES_MVP_ENABLED_FLAG))
        }.distinctUntilChanged()

    companion object {
        const val NEW_PROFILES_MVP_ENABLED_FLAG = "NewProfilesMvpEnabled"
    }
}

fun FeatureFlagManager.safeObserve(userId: UserId?, featureId: FeatureId) =
    observe(userId, featureId, refresh = false)
        .map { flag -> flag?.value ?: false }
        .catch { e ->
            if (e !is ApiException) throw e
            emit(false)
        }