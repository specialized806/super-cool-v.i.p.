/*
 * Copyright (c) 2017 Proton Technologies AG
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
package com.protonvpn.android.utils

import android.content.Context
import android.graphics.RectF
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import com.protonvpn.android.BuildConfig
import com.protonvpn.android.ProtonApplication
import com.protonvpn.android.R
import java.util.Locale

object CountryTools {

    @VisibleForTesting
    val supportedLanguages by lazy {
        BuildConfig.SUPPORTED_LOCALES.map {
            // Get the language for comparisons via a Locale object, see:
            // https://developer.android.com/reference/java/util/Locale#getLanguage()
            Locale(it.split("-r")[0]).language
        }
    }

    @JvmStatic
    fun getFlagResource(context: Context, flag: String?): Int =
        flag
            ?.let { getDrawableRes(context, "flag_${flagCode(flag)}") }
            ?: getDrawableRes(context, "zz")
            ?: 0

    /**
     * Returns a large and detailed flag resource.
     * Falls back to getFlagResource which returns drawables of a different size so don't rely on the intrinsic size of
     * the returned drawable.
     */
    fun getLargeFlagResource(context: Context, flag: String?): Int =
        flag?.let { getDrawableRes(context, "flag_large_${flagCode(flag)}") }
            ?: getFlagResource(context, flag)

    private fun getDrawableRes(context: Context, resString: String): Int? =
        context.resources.getIdentifier(resString, "drawable", context.packageName).takeIf { it > 0 }

    private fun flagCode(flag: String) = when (val code = flag.lowercase(Locale.ROOT)) {
        "uk" -> "gb"
        else -> code
    }

    fun getPreferredLocale(): Locale {
        val context = ProtonApplication.getAppContext()
        val configuration = context.resources.configuration
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            configuration.locales[0] else configuration.locale
        return if (locale?.language in supportedLanguages) locale else Locale.US
    }

    @Deprecated("Prefer the version that accepts a locale")
    fun getFullName(country: String?): String {
        val locale = Locale("", country)
        val localized = locale.getDisplayCountry(getPreferredLocale())
        return if (localized.length < MAX_LOCALIZED_LENGTH)
            localized
        else
            locale.getDisplayCountry(Locale.US)
    }

    fun getFullName(locale: Locale, country: String): String {
        val countryLocale = Locale("", country)
        val localized = countryLocale.getDisplayCountry(locale)
        return if (localized.length < MAX_LOCALIZED_LENGTH) {
            localized
        } else {
            locale.getDisplayCountry(Locale.US)
        }
    }

    enum class Continent(@StringRes val nameRes: Int, @DrawableRes val iconRes: Int) {
        Europe(R.string.nameEurope, R.drawable.europe),
        America(R.string.nameAmerica, R.drawable.america),
        Asia(R.string.nameAsia, R.drawable.asia),
        AfricaAndMiddleEast(R.string.nameAfricaAndMiddleEast, R.drawable.africa_middleeast),
        Oceania(R.string.nameOceania, R.drawable.oceania)
    }

    data class CountryData(val x: Double, val y: Double, val continent: Continent)

    // TODO: old map is now gone, we'll need to translate those to new map coordinates
    //   for (-1, -1) coordinates new map will use center of bounding box instead
    val oldMapLocations = mapOf(
        "AE" to CountryData(3103.0, 976.0, Continent.AfricaAndMiddleEast),
        "AF" to CountryData(-1.0, -1.0, Continent.Asia),
        "AL" to CountryData(2560.0, 665.0, Continent.Europe),
        "AO" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "AR" to CountryData(1300.0, 2000.0, Continent.America),
        "AT" to CountryData(2485.0, 550.0, Continent.Europe),
        "AU" to CountryData(4355.0, 1855.0, Continent.Oceania),
        "AZ" to CountryData(-1.0, -1.0, Continent.Europe),
        "BA" to CountryData(2527.0, 661.0, Continent.Europe),
        "BD" to CountryData(-1.0, -1.0, Continent.Asia),
        "BE" to CountryData(2343.0, 495.0, Continent.Europe),
        "BG" to CountryData(2660.0, 631.0, Continent.Europe),
        "BH" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "BN" to CountryData(-1.0, -1.0, Continent.Asia),
        "BR" to CountryData(1469.0, 1577.0, Continent.America),
        "BT" to CountryData(-1.0, -1.0, Continent.Asia),
        "BY" to CountryData(-1.0, -1.0, Continent.Europe),
        "CA" to CountryData(875.0, 400.0, Continent.America),
        "CH" to CountryData(2390.0, 564.0, Continent.Europe),
        "CL" to CountryData(1170.0, 1951.0, Continent.America),
        "CO" to CountryData(1100.0, 1339.0, Continent.America),
        "CR" to CountryData(925.0, 1231.0, Continent.America),
        "CU" to CountryData(-1.0, -1.0, Continent.America),
        "CY" to CountryData(2759.0, 777.0, Continent.Europe),
        "CZ" to CountryData(2482.0, 509.0, Continent.Europe),
        "DE" to CountryData(2420.0, 495.0, Continent.Europe),
        "DK" to CountryData(2413.0, 401.0, Continent.Europe),
        "DO" to CountryData(-1.0, -1.0, Continent.America),
        "DZ" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "EC" to CountryData(1010.0, 1440.0, Continent.America),
        "EE" to CountryData(2615.0, 356.0, Continent.Europe),
        "EG" to CountryData(2742.0, 863.0, Continent.AfricaAndMiddleEast),
        "ER" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "ES" to CountryData(2215.0, 690.0, Continent.Europe),
        "ET" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "FI" to CountryData(2615.0, 295.0, Continent.Europe),
        "FR" to CountryData(2310.0, 567.0, Continent.Europe),
        "GB" to CountryData(2265.0, 475.0, Continent.Europe),
        "GE" to CountryData(2915.0, 648.0, Continent.Asia),
        "GR" to CountryData(2600.0, 720.0, Continent.Europe),
        "GT" to CountryData(-1.0, -1.0, Continent.America),
        "HK" to CountryData(4033.0, 999.0, Continent.Asia),
        "HN" to CountryData(-1.0, -1.0, Continent.America),
        "HR" to CountryData(2495.0, 608.0, Continent.Europe),
        "HU" to CountryData(2550.0, 558.0, Continent.Europe),
        "ID" to CountryData(4159.0, 1481.0, Continent.Asia),
        "IE" to CountryData(2176.0, 458.0, Continent.Europe),
        "IL" to CountryData(2793.0, 830.0, Continent.AfricaAndMiddleEast),
        "IN" to CountryData(3483.0, 1071.0, Continent.Asia),
        "IS" to CountryData(2080.0, 260.0, Continent.Europe),
        "IT" to CountryData(2456.0, 647.0, Continent.Europe),
        "IQ" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "JO" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "JP" to CountryData(4330.0, 755.0, Continent.Asia),
        "KE" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "KH" to CountryData(3911.0, 1194.0, Continent.Asia),
        "KM" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "KR" to CountryData(4171.0, 743.0, Continent.Asia),
        "KZ" to CountryData(-1.0, -1.0, Continent.Asia),
        "LA" to CountryData(-1.0, -1.0, Continent.Asia),
        "LK" to CountryData(-1.0, -1.0, Continent.Asia),
        "LT" to CountryData(2604.0, 420.0, Continent.Europe),
        "LU" to CountryData(2363.0, 513.0, Continent.Europe),
        "LV" to CountryData(2612.0, 388.0, Continent.Europe),
        "LY" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "MA" to CountryData(2145.0, 860.0, Continent.AfricaAndMiddleEast),
        "MD" to CountryData(2679.0, 561.0, Continent.Europe),
        "ME" to CountryData(-1.0, -1.0, Continent.Europe),
        "MK" to CountryData(2585.0, 657.0, Continent.Europe),
        "MM" to CountryData(3755.0, 1032.0, Continent.Asia),
        "MR" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "MT" to CountryData(2483.0, 765.0, Continent.Europe),
        "MU" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "MX" to CountryData(667.0, 976.0, Continent.America),
        "MY" to CountryData(3878.0, 1335.0, Continent.Asia),
        "MZ" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "NG" to CountryData(2385.0, 1235.0, Continent.AfricaAndMiddleEast),
        "NL" to CountryData(2355.0, 466.0, Continent.Europe),
        "NO" to CountryData(2411.0, 311.0, Continent.Europe),
        "NP" to CountryData(-1.0, -1.0, Continent.Asia),
        "NZ" to CountryData(4760.0, 2171.0, Continent.Oceania),
        "OM" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "PE" to CountryData(1056.0, 1589.0, Continent.America),
        "PH" to CountryData(4159.0, 1135.0, Continent.Asia),
        "PK" to CountryData(3330.0, 860.0, Continent.Asia),
        "PL" to CountryData(2554.0, 472.0, Continent.Europe),
        "PR" to CountryData(1216.0, 1076.0, Continent.America),
        "PT" to CountryData(2148.0, 688.0, Continent.Europe),
        "QA" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "RO" to CountryData(2636.0, 583.0, Continent.Europe),
        "RS" to CountryData(2569.0, 607.0, Continent.Europe),
        "RU" to CountryData(2833.0, 366.0, Continent.Europe),
        "RW" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "SA" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "SD" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "SE" to CountryData(2485.0, 300.0, Continent.Europe),
        "SG" to CountryData(3905.0, 1379.0, Continent.Asia),
        "SI" to CountryData(2481.0, 578.0, Continent.Europe),
        "SK" to CountryData(2552.0, 527.0, Continent.Europe),
        "SL" to CountryData(2483.0, 575.0, Continent.AfricaAndMiddleEast),
        "SN" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "SO" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "SS" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "SY" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "SV" to CountryData(-1.0, -1.0, Continent.America),
        "TD" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "TG" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "TH" to CountryData(3848.0, 1128.0, Continent.Asia),
        "TJ" to CountryData(-1.0, -1.0, Continent.Asia),
        "TM" to CountryData(-1.0, -1.0, Continent.Asia),
        "TN" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "TR" to CountryData(2779.0, 696.0, Continent.AfricaAndMiddleEast),
        "TW" to CountryData(4135.0, 975.0, Continent.Asia),
        "TZ" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "UA" to CountryData(2715.0, 517.0, Continent.Europe),
        "UG" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "UK" to CountryData(2265.0, 475.0, Continent.Europe),
        "US" to CountryData(760.0, 700.0, Continent.America),
        "UZ" to CountryData(-1.0, -1.0, Continent.Asia),
        "VE" to CountryData(-1.0, -1.0, Continent.America),
        "VN" to CountryData(3961.0, 1144.0, Continent.Asia),
        "YE" to CountryData(-1.0, -1.0, Continent.AfricaAndMiddleEast),
        "ZA" to CountryData(2629.0, 1950.0, Continent.AfricaAndMiddleEast),
    )

    val codeToMapCountryName = mapOf(
        "AD" to "Andorra",
        "AE" to "UnitedArabEmirates",
        "AF" to "Afghanistan",
        "AG" to "AntiguaandBarbuda",
        "AI" to "Anguilla",
        "AL" to "Albania",
        "AM" to "Armenia",
        "AO" to "Angola",
        "AR" to "Argentina",
        "AS" to "AmericanSamoa",
        "AT" to "Austria",
        "AU" to "Australia",
        "AW" to "Aruba",
        "AZ" to "Azerbaijan",
        "BA" to "Bosnia_Herz",
        "BB" to "Barbados",
        "BD" to "Bangladesh",
        "BE" to "Belgium",
        "BF" to "BurkinaFaso",
        "BG" to "Bulgaria",
        "BH" to "Bahrain",
        "BI" to "Burundi",
        "BJ" to "Benin",
        "BM" to "Bermuda",
        "BN" to "Brunei",
        "BO" to "Bolivia",
        "BR" to "Brazil",
        "BS" to "Bahamas",
        "BT" to "Bhutan",
        "BW" to "Botswana",
        "BY" to "Belarus",
        "BZ" to "Belize",
        "CA" to "Canada",
        "CD" to "DemRepofCongo",
        "CF" to "CentralAfricanRep",
        "CG" to "Congo",
        "CH" to "Switzerland",
        "CI" to "IvoryCoast",
        "CL" to "Chile",
        "CM" to "Cameroon",
        "CN" to "China",
        "CO" to "Colombia",
        "CR" to "CostaRica",
        "CU" to "Cuba",
        "CV" to "CapeVerde",
        "CW" to "Curacao",
        "CY" to "Cyprus",
        "CZ" to "CzechRep",
        "DE" to "Germany",
        "DJ" to "Djibouti",
        "DK" to "Denmark",
        "DM" to "Dominica",
        "DO" to "DominicanRep",
        "DZ" to "Algeria",
        "EC" to "Ecuador",
        "EE" to "Estonia",
        "EG" to "Egypt",
        "EH" to "WesternSahara",
        "ER" to "Eritrea",
        "ES" to "Spain",
        "ET" to "Ethiopia",
        "FI" to "Finland",
        "FJ" to "Fiji",
        "FK" to "FalklandIslands",
        "FO" to "FaroeIslands",
        "FR" to "France",
        "GA" to "Gabon",
        "GB" to "UnitedKingdom",
        "GD" to "Grenada",
        "GE" to "Georgia",
        "GF" to "France",
        "GF" to "FrenchGuiana",
        "GH" to "Ghana",
        "GL" to "Greenland",
        "GM" to "Gambia",
        "GN" to "Guinea",
        "GP" to "Guadeloupe",
        "GQ" to "EqGuinea",
        "GR" to "Greece",
        "GT" to "Guatemala",
        "GY" to "Guyana",
        "HK" to "HongKong",
        "HN" to "Honduras",
        "HR" to "Croatia",
        "HT" to "Haiti",
        "HU" to "Hungary",
        "ID" to "Indonesia",
        "IE" to "Ireland",
        "IL" to "Israel",
        "IN" to "India",
        "IQ" to "Iraq",
        "IR" to "Iran",
        "IS" to "Iceland",
        "IT" to "Italy",
        "JM" to "Jamaica",
        "JO" to "Jordan",
        "JP" to "Japan",
        "KE" to "Kenya",
        "KG" to "Kyrgyzstan",
        "KH" to "Cambodia",
        "KM" to "Comoros",
        "KN" to "StKittsandNevis",
        "KP" to "NorthKorea",
        "KR" to "SouthKorea",
        "KW" to "Kuwait",
        "KY" to "CaymanIslands",
        "KZ" to "Kazakhstan",
        "LA" to "Laos",
        "LB" to "Lebanon",
        "LC" to "StLucia",
        "LI" to "Liechtenstein",
        "LK" to "SriLanka",
        "LR" to "Liberia",
        "LS" to "Lesotho",
        "LT" to "Lithuania",
        "LU" to "Luxembourg",
        "LV" to "Latvia",
        "LY" to "Libya",
        "MA" to "Morocco",
        "MC" to "Monaco",
        "MD" to "Moldova",
        "ME" to "Montenegro",
        "MF" to "StMartin",
        "MG" to "Madagascar",
        "MK" to "Macedonia",
        "ML" to "Mali",
        "MM" to "Myanmar",
        "MN" to "Mongolia",
        "MO" to "Macao",
        "MQ" to "Martinique",
        "MR" to "Mauritania",
        "MS" to "Montserrat",
        "MT" to "Malta",
        "MU" to "Mauritius",
        "MV" to "Maldives",
        "MW" to "Malawi",
        "MX" to "Mexico",
        "MY" to "Malaysia",
        "MZ" to "Mozambique",
        "NA" to "Namibia",
        "NC" to "NewCaledonia",
        "NE" to "Niger",
        "NG" to "Nigeria",
        "NI" to "Nicaragua",
        "NL" to "Netherlands",
        "NO" to "Norway",
        "NP" to "Nepal",
        "NR" to "Nauru",
        "NZ" to "NewZealand",
        "OM" to "Oman",
        "PA" to "Panama",
        "PE" to "Peru",
        "PF" to "FrenchPolynesia",
        "PG" to "PapuaNewGuinea",
        "PH" to "Phillipines", // The path in SVG has a typo.
        "PK" to "Pakistan",
        "PK" to "Pakistan",
        "PL" to "Poland",
        "PN" to "PitcairnIslands",
        "PR" to "PuertoRico",
        "PS" to "Palestine",
        "PT" to "Portugal",
        "PY" to "Paraguay",
        "QA" to "Qatar",
        "RE" to "Reunion",
        "RO" to "Romania",
        "RS" to "Serbia",
        "RU" to "Russia",
        "RW" to "Rwanda",
        "SA" to "SaudiArabia",
        "SB" to "SolomonIslands",
        "SC" to "Seychelles",
        "SD" to "Sudan",
        "SE" to "Sweden",
        "SG" to "Singapore",
        "SI" to "Slovenia",
        "SK" to "Slovakia",
        "SL" to "SierraLeone",
        "SM" to "SanMarino",
        "SN" to "Senegal",
        "SO" to "Somalia",
        "SR" to "Suriname",
        "SS" to "SouthSudan",
        "ST" to "SaoTomeandPrincipe",
        "SV" to "ElSalvador",
        "SY" to "Syria",
        "SZ" to "Swaziland",
        "TC" to "TurksandCaicos",
        "TD" to "Chad",
        "TG" to "Togo",
        "TH" to "Thailand",
        "TJ" to "Tajikistan",
        "TL" to "EastTimor",
        "TM" to "Turkmenistan",
        "TN" to "Tunisia",
        "TO" to "Tonga",
        "TR" to "Turkey",
        "TT" to "TrinidadandTobago",
        "TW" to "Taiwan",
        "TZ" to "Tanzania",
        "UA" to "Ukraine",
        "UG" to "Uganda",
        "UK" to "UnitedKingdom",
        "US" to "UnitedStatesofAmerica",
        "UY" to "Uruguay",
        "UZ" to "Uzbekistan",
        "VA" to "Vatican",
        "VC" to "StVincentandtheGrendaines",
        "VE" to "Venezuela",
        "VG" to "BritishVirginIslands",
        "VI" to "USVirginIslands",
        "VN" to "Vietnam",
        "VU" to "Vanuatu",
        "XK" to "Kosovo",
        "YE" to "Yemen",
        "YT" to "Mayotte",
        "ZA" to "SouthAfrica",
        "ZM" to "Zambia",
        "ZW" to "Zimbabwe",
    )

    val tvMapNameToBounds = mapOf(
        "Afghanistan" to RectF(953.3968f, 199.4880f, 1010.7480f, 245.5879f),
        "AlandIslands" to RectF(774.6121f, 92.8440f, 778.5470f, 94.6760f),
        "Albania" to RectF(777.9768f, 178.9820f, 785.4478f, 193.4160f),
        "Algeria" to RectF(659.2271f, 206.5010f, 748.5139f, 298.2070f),
        "AmericanSamoa" to RectF(1532.8369f, 466.2410f, 1534.0000f, 466.7590f),
        "Andorra" to RectF(705.3571f, 179.0060f, 706.6841f, 180.0210f),
        "Angola" to RectF(747.5631f, 416.5509f, 802.2094f, 485.2549f),
        "Anguilla" to RectF(417.9520f, 301.8250f, 418.7960f, 302.3221f),
        "AntiguaandBarbuda" to RectF(422.9990f, 304.6350f, 423.8930f, 308.2580f),
        "Argentina" to RectF(396.0918f, 504.3880f, 464.6610f, 670.2539f),
        "Armenia" to RectF(877.0279f, 185.6490f, 891.3170f, 197.3840f),
        "Aruba" to RectF(384.7570f, 330.4140f, 385.4650f, 331.3791f),
        "Australia" to RectF(1183.4761f, 444.9870f, 1362.9520f, 668.9041f),
        "Austria" to RectF(738.4578f, 147.3410f, 768.4167f, 160.1931f),
        "Azerbaijan" to RectF(883.2840f, 182.6960f, 906.0871f, 199.7700f),
        "Bahamas" to RectF(354.7890f, 257.9890f, 378.3920f, 288.3391f),
        "Bahrain" to RectF(915.2470f, 261.5000f, 916.0471f, 263.7240f),
        "Bangladesh" to RectF(1078.5698f, 259.8290f, 1101.5741f, 289.0801f),
        "Barbados" to RectF(431.1960f, 326.8570f, 432.1500f, 328.1470f),
        "Belarus" to RectF(790.6941f, 112.7940f, 826.3413f, 136.2260f),
        "Belgium" to RectF(711.6890f, 135.1300f, 726.3570f, 144.8191f),
        "Belize" to RectF(301.6310f, 300.7491f, 309.2970f, 313.8631f),
        "Benin" to RectF(698.7841f, 331.5821f, 712.4232f, 362.7451f),
        "Bermuda" to RectF(421.5130f, 230.5150f, 422.4640f, 231.1580f),
        "Bhutan" to RectF(1080.3191f, 251.0590f, 1094.9681f, 259.1960f),
        "Bolivia" to RectF(386.3380f, 443.2379f, 442.8969f, 509.8789f),
        "Boniare" to RectF(392.1660f, 331.9920f, 392.8560f, 333.3570f),
        "Bosnia_Herz" to RectF(763.2628f, 165.7250f, 778.8359f, 179.4180f),
        "Botswana" to RectF(783.3259f, 484.0709f, 824.5419f, 529.8970f),
        "Brazil" to RectF(365.7050f, 367.5682f, 540.2469f, 564.6563f),
        "BritishIndianOceanTerritory" to RectF(1017.5661f, 430.6710f, 1018.2231f, 431.7570f),
        "BritishVirginIslands" to RectF(411.3320f, 299.3850f, 413.3479f, 301.1711f),
        "Brunei" to RectF(1204.0369f, 368.7860f, 1209.7112f, 373.8360f),
        "Bulgaria" to RectF(790.0829f, 171.2150f, 815.1920f, 185.8900f),
        "BurkinaFaso" to RectF(670.8389f, 317.9490f, 706.0509f, 346.5331f),
        "Burundi" to RectF(824.6879f, 405.8650f, 832.6990f, 416.7021f),
        "Cambodia" to RectF(1148.8190f, 319.8440f, 1172.4958f, 341.5490f),
        "Cameroon" to RectF(733.2628f, 328.0640f, 767.3988f, 385.7193f),
        "Canada" to RectF(200.6940f, 1.3660f, 551.5890f, 182.4753f),
        "CapeVerde" to RectF(584.0820f, 307.2620f, 595.5860f, 319.2740f),
        "CaymanIslands" to RectF(338.4260f, 294.2630f, 346.0450f, 296.7580f),
        "CentralAfricanRep" to RectF(759.5640f, 338.5732f, 817.4299f, 382.6802f),
        "Chad" to RectF(755.1300f, 275.5981f, 801.6561f, 356.3170f),
        "Chile" to RectF(350.5960f, 482.6690f, 455.9510f, 674.3409f),
        "China" to RectF(1002.7844f, 125.1471f, 1232.6929f, 302.0810f),
        "CocosandChristmasIsland" to RectF(1125.0820f, 446.8990f, 1165.2280f, 455.8369f),
        "Colombia" to RectF(342.2650f, 331.3247f, 396.4891f, 415.5551f),
        "Comoros" to RectF(887.3199f, 451.6400f, 893.0200f, 456.6909f),
        "Congo" to RectF(744.8530f, 375.5351f, 778.2928f, 419.4680f),
        "CostaRica" to RectF(313.7960f, 337.6210f, 328.1740f, 353.3820f),
        "Croatia" to RectF(754.3350f, 159.5200f, 777.8799f, 180.0380f),
        "Cuba" to RectF(324.9880f, 276.9470f, 370.9481f, 293.7989f),
        "Curacao" to RectF(388.7090f, 331.5960f, 390.4090f, 333.2880f),
        "Cyprus" to RectF(833.9230f, 213.8650f, 843.1880f, 219.4530f),
        "CzechRep" to RectF(748.5081f, 137.3450f, 774.7451f, 149.4489f),
        "DemRepofCongo" to RectF(749.6871f, 367.3048f, 834.8172f, 462.1551f),
        "Denmark" to RectF(733.6250f, 105.2830f, 759.6920f, 120.0180f),
        "Djibouti" to RectF(880.8600f, 329.9359f, 888.0659f, 338.8690f),
        "Dominica" to RectF(424.0290f, 315.1520f, 424.9760f, 317.2069f),
        "DominicanRep" to RectF(379.3290f, 293.5140f, 395.4419f, 305.0330f),
        "EastTimor" to RectF(1247.0720f, 435.3200f, 1261.9189f, 442.2570f),
        "Ecuador" to RectF(285.8020f, 386.8250f, 359.0979f, 419.3969f),
        "Egypt" to RectF(802.9929f, 234.1840f, 857.3770f, 282.9700f),
        "ElSalvador" to RectF(296.5170f, 321.2300f, 307.1260f, 327.6310f),
        "EqGuinea" to RectF(732.8320f, 375.1780f, 745.7921f, 389.3220f),
        "Eritrea" to RectF(856.6800f, 303.1610f, 886.6829f, 331.6049f),
        "Estonia" to RectF(783.5709f, 96.3950f, 805.8314f, 106.2660f),
        "Ethiopia" to RectF(842.2330f, 319.0960f, 908.9171f, 376.7193f),
        "FalklandIslands" to RectF(468.3570f, 652.1440f, 480.7730f, 657.1830f),
        "FaroeIslands" to RectF(679.9170f, 83.9140f, 683.5390f, 88.2120f),
        "Fiji" to RectF(1459.8120f, 457.2400f, 1495.9041f, 503.8890f),
        "Finland" to RectF(775.5179f, 50.0680f, 815.4690f, 95.5460f),
        "France" to RectF(682.5427f, 137.0430f, 738.4230f, 185.1750f),
        "FrenchGuiana" to RectF(451.3200f, 364.9461f, 464.7320f, 383.4530f),
        "FrenchPolynesia" to RectF(27.3840f, 438.5590f, 96.5430f, 499.6960f),
        "FrenchSouthernandAntarcticLands" to RectF(905.6721f, 627.7990f, 976.4140f, 644.4880f),
        "Gabon" to RectF(734.0140f, 382.5340f, 759.8230f, 413.9700f),
        "Gambia" to RectF(620.9890f, 324.3579f, 634.2860f, 328.1350f),
        "Georgia" to RectF(861.3540f, 174.4740f, 890.1160f, 186.7260f),
        "Germany" to RectF(724.4791f, 117.9650f, 759.6792f, 155.8471f),
        "Ghana" to RectF(680.8099f, 337.7461f, 700.5388f, 370.1302f),
        "Greece" to RectF(779.9139f, 183.4290f, 816.4100f, 217.5951f),
        "Greenland" to RectF(502.3360f, -0.0040f, 683.5747f, 95.5459f),
        "Grenada" to RectF(421.3109f, 332.3200f, 422.1630f, 333.4750f),
        "Guadeloupe" to RectF(423.0140f, 310.7370f, 425.7360f, 313.8720f),
        "Guam" to RectF(1336.4059f, 325.3180f, 1337.5958f, 327.1670f),
        "Guatemala" to RectF(287.5490f, 304.1200f, 305.9970f, 324.7420f),
        "Guernsey" to RectF(691.1860f, 144.8440f, 691.7159f, 145.2270f),
        "Guinea" to RectF(628.5360f, 330.1041f, 661.1290f, 357.6951f),
        "Guyana" to RectF(421.5489f, 350.9689f, 442.9500f, 388.1121f),
        "Haiti" to RectF(368.3270f, 292.6070f, 381.2250f, 302.9860f),
        "HeardandMcDonaldIslands" to RectF(980.2661f, 660.3530f, 982.2470f, 661.4020f),
        "Honduras" to RectF(300.1690f, 310.7010f, 327.9741f, 328.5669f),
        "HongKong" to RectF(1193.6440f, 280.1190f, 1195.6860f, 281.9880f),
        "Hungary" to RectF(764.4309f, 149.5460f, 790.9579f, 163.3810f),
        "Iceland" to RectF(624.5690f, 65.2990f, 661.2050f, 79.1509f),
        "India" to RectF(993.3101f, 213.8570f, 1116.7290f, 360.0700f),
        "Indonesia" to RectF(1119.7272f, 364.3139f, 1324.5229f, 449.3200f),
        "Iran" to RectF(880.6329f, 192.8539f, 970.4191f, 267.3179f),
        "Iraq" to RectF(861.9750f, 205.0730f, 905.2890f, 247.2570f),
        "Ireland" to RectF(662.5389f, 116.5109f, 679.7780f, 135.2278f),
        "IsleofMan" to RectF(684.9570f, 121.0780f, 686.7340f, 122.7440f),
        "Israel" to RectF(843.5510f, 225.2410f, 850.0191f, 245.1710f),
        "Italy" to RectF(726.7981f, 156.8180f, 775.0891f, 208.5800f),
        "IvoryCoast" to RectF(656.9230f, 339.9710f, 684.1678f, 372.1830f),
        "Jamaica" to RectF(351.2400f, 300.5470f, 360.3710f, 304.6250f),
        "Japan" to RectF(1234.6680f, 164.5880f, 1312.2219f, 271.5190f),
        "Jersey" to RectF(692.6810f, 146.0250f, 693.5630f, 146.5010f),
        "Jordan" to RectF(847.1821f, 225.5350f, 864.6692f, 246.6020f),
        "Kazakhstan" to RectF(883.6282f, 116.3949f, 1041.9971f, 189.0230f),
        "Kenya" to RectF(846.5470f, 366.4110f, 882.1051f, 417.8670f),
        "Kiribati" to RectF(0.0040f, 378.2570f, 1538.4331f, 452.0860f),
        "Kosovo" to RectF(780.8979f, 175.9840f, 787.9081f, 182.8730f),
        "Kuwait" to RectF(897.0451f, 242.0490f, 905.5241f, 249.9350f),
        "Kyrgyzstan" to RectF(984.2231f, 176.0840f, 1026.2019f, 195.6660f),
        "Laos" to RectF(1135.0032f, 280.4809f, 1171.4454f, 323.8239f),
        "Latvia" to RectF(781.1628f, 103.7510f, 807.7517f, 115.0720f),
        "Lebanon" to RectF(846.5480f, 218.9000f, 852.3450f, 227.0420f),
        "Lesotho" to RectF(813.1799f, 538.6639f, 823.3130f, 549.0509f),
        "Liberia" to RectF(643.9821f, 351.0139f, 662.2231f, 372.1660f),
        "Libya" to RectF(737.0389f, 226.5030f, 805.5891f, 295.6941f),
        "Liechtenstein" to RectF(738.2690f, 155.8860f, 738.7881f, 156.9440f),
        "Lithuania" to RectF(781.1089f, 111.5490f, 802.9690f, 123.5481f),
        "Luxembourg" to RectF(723.8110f, 141.5960f, 726.7861f, 145.1380f),
        "Macao" to RectF(1192.1008f, 281.7270f, 1192.3899f, 281.9820f),
        "Macedonia" to RectF(782.8640f, 180.3900f, 793.2540f, 187.8150f),
        "Madagascar" to RectF(885.2229f, 455.2330f, 918.7569f, 523.4321f),
        "Malawi" to RectF(840.2751f, 441.6571f, 854.3600f, 480.7522f),
        "Malaysia" to RectF(1139.3459f, 357.0150f, 1227.0774f, 389.8119f),
        "Maldives" to RectF(1022.7131f, 372.7020f, 1023.2480f, 377.8490f),
        "Mali" to RectF(641.3110f, 267.8191f, 714.5601f, 342.9309f),
        "Malta" to RectF(757.6111f, 211.7370f, 759.2380f, 213.0520f),
        "MarshallIslands" to RectF(1436.4109f, 337.7169f, 1460.4980f, 364.8570f),
        "Martinique" to RectF(424.8660f, 318.9840f, 426.4740f, 321.2530f),
        "Mauritania" to RectF(621.4069f, 256.2519f, 675.5190f, 319.6299f),
        "Mauritius" to RectF(947.3161f, 495.2169f, 949.4671f, 497.8639f),
        "Mayotte" to RectF(895.2280f, 458.1280f, 896.0200f, 459.8059f),
        "Mexico" to RectF(187.9360f, 228.8440f, 316.5830f, 320.6327f),
        "Micronesia" to RectF(1309.4141f, 345.6860f, 1422.0800f, 367.4980f),
        "Moldova" to RectF(805.5360f, 149.9230f, 820.1810f, 164.8690f),
        "Monaco" to RectF(729.6700f, 173.4970f, 729.9170f, 173.6830f),
        "Mongolia" to RectF(1043.7570f, 132.1050f, 1175.6232f, 184.1631f),
        "Montenegro" to RectF(774.4269f, 174.6140f, 782.1561f, 182.7960f),
        "Montserrat" to RectF(421.3250f, 309.2060f, 421.6679f, 309.8540f),
        "Morocco" to RectF(621.7380f, 212.4840f, 692.9810f, 285.9100f),
        "Mozambique" to RectF(829.2820f, 447.0710f, 876.2889f, 529.9630f),
        "Myanmar" to RectF(1099.6160f, 250.0000f, 1138.6818f, 344.2570f),
        "Namibia" to RectF(747.4598f, 479.9510f, 807.0679f, 540.4688f),
        "Nauru" to RectF(1440.5870f, 396.6480f, 1440.8160f, 396.9580f),
        "Nepal" to RectF(1041.3760f, 240.5830f, 1078.2249f, 260.9270f),
        "Netherlands" to RectF(714.9170f, 124.8230f, 729.9021f, 138.7359f),
        "NewCaledonia" to RectF(1398.3660f, 490.7870f, 1431.8931f, 508.7119f),
        "NewZealand" to RectF(1341.2169f, 437.3740f, 1535.2009f, 658.4430f),
        "Nicaragua" to RectF(306.8640f, 318.3130f, 327.9280f, 339.8979f),
        "Niger" to RectF(696.3340f, 275.3010f, 766.0380f, 335.0608f),
        "Nigeria" to RectF(707.2449f, 324.0470f, 760.3759f, 372.5559f),
        "Niue" to RectF(1530.9719f, 490.0430f, 1531.6959f, 490.9120f),
        "NorfolkIsland" to RectF(1419.0039f, 540.8420f, 1419.3889f, 541.2520f),
        "NorthKorea" to RectF(1210.7449f, 177.2680f, 1230.9020f, 203.2729f),
        "NortthernMarianaIslands" to RectF(1336.1310f, 299.1129f, 1340.1890f, 322.8460f),
        "Norway" to RectF(680.4290f, 9.6210f, 808.5273f, 103.9641f),
        "Oman" to RectF(924.3721f, 260.9500f, 957.6412f, 309.9969f),
        "Pakistan" to RectF(958.0673f, 206.7940f, 1022.2393f, 274.0960f),
        "Palestine" to RectF(843.3090f, 229.7670f, 848.9540f, 236.4510f),
        "Palua" to RectF(1280.5330f, 355.1909f, 1294.9810f, 378.8999f),
        "Panama" to RectF(325.7290f, 345.6570f, 351.5989f, 357.6819f),
        "PapuaNewGuinea" to RectF(1322.5190f, 401.0169f, 1390.2241f, 452.9670f),
        "Paraguay" to RectF(422.4510f, 491.6649f, 460.1859f, 533.4619f),
        "Peru" to RectF(332.3840f, 394.3871f, 391.9140f, 486.9030f),
        "Phillipines" to RectF(1216.1191f, 288.8400f, 1259.1555f, 368.5979f),
        "PitcairnIslands" to RectF(137.8420f, 517.1179f, 138.1830f, 517.5699f),
        "Poland" to RectF(756.1528f, 119.0100f, 794.8150f, 147.2181f),
        "Portugal" to RectF(569.7160f, 181.4770f, 674.0070f, 229.1940f),
        "PuertoRico" to RectF(396.8930f, 300.5490f, 408.5400f, 303.4541f),
        "Qatar" to RectF(916.8859f, 261.9680f, 920.7289f, 269.9970f),
        "Reunion" to RectF(937.9650f, 499.6400f, 940.5910f, 502.1890f),
        "Romania" to RectF(781.0290f, 150.9770f, 819.0482f, 173.9879f),
        "Russia" to RectF(776.5129f, 5.1650f, 1347.1597f, 186.1269f),
        "Rwanda" to RectF(823.9971f, 399.5479f, 833.0190f, 408.3719f),
        "Samoa" to RectF(1524.9390f, 462.2330f, 1530.3379f, 465.1720f),
        "SanMarino" to RectF(749.9041f, 172.4290f, 750.3781f, 172.8990f),
        "SaoTomeandPrincipe" to RectF(724.0390f, 385.5860f, 728.4420f, 393.9330f),
        "SaudiArabia" to RectF(846.0958f, 231.8450f, 939.4351f, 311.4290f),
        "Senegal" to RectF(618.0660f, 309.8640f, 645.0220f, 331.8430f),
        "Serbia" to RectF(775.4900f, 161.3270f, 792.7161f, 180.9810f),
        "Seychelles" to RectF(942.2661f, 417.2180f, 942.9610f, 418.3610f),
        "SierraLeone" to RectF(636.1851f, 343.6410f, 649.5382f, 359.2529f),
        "Singapore" to RectF(1158.0310f, 386.8549f, 1159.5741f, 387.7690f),
        "Slovakia" to RectF(767.2579f, 144.3870f, 789.3060f, 153.4500f),
        "Slovenia" to RectF(753.7300f, 157.9000f, 766.1541f, 164.9770f),
        "SolomonIslands" to RectF(1388.8131f, 427.5861f, 1436.4399f, 453.9831f),
        "Somalia" to RectF(878.0410f, 333.6050f, 923.7770f, 402.7491f),
        "SouthAfrica" to RectF(767.7308f, 506.1070f, 850.6570f, 630.9420f),
        "SouthGeorgiaandtheSandwichIslands" to RectF(559.9771f, 665.2520f, 609.5031f, 686.6019f),
        "SouthKorea" to RectF(1224.2739f, 198.6370f, 1243.8589f, 226.4020f),
        "SouthSudan" to RectF(802.7910f, 332.3940f, 852.4910f, 376.5480f),
        "Spain" to RectF(618.4191f, 173.5330f, 716.7820f, 254.4190f),
        "SriLanka" to RectF(1050.1300f, 344.5730f, 1060.0339f, 364.0990f),
        "StBarthelemy" to RectF(419.0670f, 303.5830f, 419.3969f, 303.8210f),
        "StEustiusandSaba" to RectF(417.2489f, 304.9490f, 418.5750f, 305.8440f),
        "StHelena" to RectF(631.1219f, 434.0180f, 670.7200f, 475.0700f),
        "StKittsandNevis" to RectF(418.9210f, 306.2080f, 420.1800f, 307.7340f),
        "StLucia" to RectF(425.1290f, 322.9370f, 426.0119f, 324.8330f),
        "StMaarten" to RectF(418.0510f, 302.8390f, 418.5530f, 303.0910f),
        "StMartin" to RectF(418.0609f, 302.6070f, 418.5800f, 302.8410f),
        "StPeirreandMiquelon" to RectF(477.5399f, 156.7330f, 478.4860f, 158.4440f),
        "StVincentandtheGrendaines" to RectF(423.4519f, 326.6499f, 424.6880f, 330.0020f),
        "Sudan" to RectF(792.2671f, 281.9481f, 865.8090f, 350.3810f),
        "Suriname" to RectF(436.1660f, 363.8741f, 454.3140f, 384.8471f),
        "Swaziland" to RectF(829.9440f, 524.3030f, 835.6809f, 532.2319f),
        "Sweden" to RectF(744.8058f, 54.4280f, 788.5458f, 116.5910f),
        "Switzerland" to RectF(724.3099f, 153.3880f, 742.1210f, 163.0020f),
        "Syria" to RectF(848.2889f, 205.4450f, 875.1360f, 230.8700f),
        "Taiwan" to RectF(1211.0430f, 266.4040f, 1226.3870f, 283.3530f),
        "Tajikistan" to RectF(977.0911f, 186.9050f, 1011.5002f, 208.6161f),
        "Tanzania" to RectF(825.9900f, 399.2270f, 875.1790f, 453.4250f),
        "Thailand" to RectF(1124.1492f, 290.9480f, 1162.3911f, 365.6991f),
        "Togo" to RectF(695.0079f, 337.9910f, 703.1750f, 363.3789f),
        "Tonga" to RectF(1504.5410f, 488.0160f, 1514.1379f, 502.5981f),
        "TrinidadandTobago" to RectF(420.1779f, 336.9300f, 426.6750f, 343.3030f),
        "Tunisia" to RectF(729.4390f, 205.2280f, 746.5601f, 241.3770f),
        "Turkey" to RectF(804.6140f, 181.6940f, 885.2080f, 212.9928f),
        "Turkmenistan" to RectF(913.4781f, 178.3470f, 975.7014f, 216.4041f),
        "TurksandCaicos" to RectF(379.8110f, 283.2160f, 382.8910f, 284.2210f),
        "USVirginIslands" to RectF(409.8620f, 301.2430f, 411.4770f, 304.6980f),
        "Uganda" to RectF(827.1910f, 372.8420f, 851.3790f, 401.6000f),
        "Ukraine" to RectF(787.8859f, 130.9510f, 857.8768f, 170.4529f),
        "UnitedArabEmirates" to RectF(920.8321f, 262.4020f, 941.5073f, 279.8479f),
        "UnitedKingdom" to RectF(672.5562f, 90.8860f, 709.0702f, 142.3049f),
        "UnitedStatesofAmerica" to RectF(61.6110f, 44.4350f, 432.8538f, 270.1190f),
        "Uruguay" to RectF(449.5420f, 546.3269f, 471.9079f, 570.7469f),
        "Uzbekistan" to RectF(924.3080f, 164.3480f, 998.9641f, 206.0860f),
        "Vanuatu" to RectF(1432.0332f, 463.4650f, 1441.0940f, 496.4870f),
        "Vatican" to RectF(750.0950f, 182.6140f, 750.1420f, 182.6530f),
        "Venezuela" to RectF(368.9470f, 332.6330f, 428.9870f, 390.6930f),
        "Vietnam" to RectF(1142.3848f, 276.1580f, 1180.8430f, 350.7600f),
        "WallisandFortuna" to RectF(1500.2532f, 461.0050f, 1510.3240f, 466.5830f),
        "WesternSahara" to RectF(621.1951f, 254.3499f, 659.2939f, 288.9629f),
        "Yemen" to RectF(883.7130f, 298.1800f, 937.2451f, 331.9070f),
        "Zambia" to RectF(792.7610f, 435.5870f, 844.9771f, 485.3811f),
        "Zimbabwe" to RectF(806.8928f, 473.2450f, 841.1140f, 507.4102f),
    )

    private const val MAX_LOCALIZED_LENGTH = 60
}
