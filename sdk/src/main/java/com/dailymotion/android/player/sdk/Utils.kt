package com.dailymotion.android.player.sdk

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * Created by hugo
 * on 14/03/2018.
 */
object Utils {
    fun hasFireTV(context: Context): Boolean {
        val mgr = context.packageManager
        return mgr.hasSystemFeature("amazon.hardware.fire_tv")
    }

    fun hasLeanback(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mgr = context.packageManager
            mgr.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        } else {
            false
        }
    }
}