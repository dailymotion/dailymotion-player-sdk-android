package com.dailymotion.android.player.sdk

import android.content.Context
import android.preference.PreferenceManager
import android.webkit.CookieManager
import timber.log.Timber
import java.net.URLEncoder
import java.util.*

class TCF2Handler {

    /**
     * Load IAB Consent string as a cookie WebView to make it available to the JS Player.
     * This method will try to read the consent string from the location described by IAB specs.
     *
     * @param context
     * @param consentString fallback value if the consent string found following IAB specs is not present
     * @param consentStringCookieMaxAge cookie max age if set otherwise it will default to 6 month
     *
     * @return true if consent string was correctly loaded, false otherwise.
     */
    fun loadConsentString(context: Context,
                          consentString: String? = null,
                          consentStringCookieMaxAge: Long? = null): Boolean {

        val savedConsentString = PreferenceManager.getDefaultSharedPreferences(context).getString("IABTCF_TCString", consentString)
        if (savedConsentString == null) {
            Timber.e("Loaded consent string is null")
            return false
        }

        /* Make the cookie expires in 6 months by default if not overridden */
        val maxAge = consentStringCookieMaxAge ?: getCookieDefaultMaxAge()
        val name = "dm-euconsent-v2"
        val url = ".dailymotion.com"

        return try {
            val cookie = "$name=${URLEncoder.encode(savedConsentString, "UTF-8")}; max-age=$maxAge; path=/; domain=$url"
            CookieManager.getInstance().setCookie(url, cookie)
            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    /**
     * Get the consent string cookie default max age which is 6 month
     *
     * @return cookie max age
     */
    private fun getCookieDefaultMaxAge(): Long {
        val today = Date()
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.add(Calendar.MONTH, 6)
        return calendar.time.time
    }
}