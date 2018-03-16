package com.dailymotion.android.player.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by hugo
 * on 14/03/2018.
 */

public class Utils {

    public static boolean hasFireTV(Context context) {
        PackageManager mgr = context.getPackageManager();
        return mgr.hasSystemFeature("amazon.hardware.fire_tv");
    }

    public static boolean hasLeanback(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PackageManager mgr = context.getPackageManager();
            return mgr.hasSystemFeature(PackageManager.FEATURE_LEANBACK);
        } else {
            return false;
        }
    }

}
