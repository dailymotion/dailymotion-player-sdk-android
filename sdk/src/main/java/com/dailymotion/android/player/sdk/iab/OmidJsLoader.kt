package com.dailymotion.android.player.sdk.iab

import android.content.Context
import com.dailymotion.android.R
import java.io.IOException

/**
 * OmidJsLoader - utility for loading the Omid JavaScript resource
 */
object OmidJsLoader {
    /**
     * getOmidJs - gets the Omid JS resource as a string
     * @param context - used to access the JS resource
     * @return - the Omid JS resource as a string
     */
    fun getOmidJs(context: Context): String {
        try {
            context.resources.openRawResource(R.raw.omsdk_v1).use { inputStream ->
                return inputStream.readBytes().toString(Charsets.UTF_8)
            }
        } catch (e: IOException) {
            throw UnsupportedOperationException("Hum, omid resource not found", e)
        }
    }
}