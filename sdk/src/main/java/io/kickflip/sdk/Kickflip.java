/*
 * removed most of the functionnality to only keep the isKitKat() method
 */
package io.kickflip.sdk;

import android.os.Build;

public class Kickflip {
    /**
     * Returns whether the current device is running Android 4.4, KitKat, or newer
     *
     * KitKat is required for certain Kickflip features like Adaptive bitrate streaming
     */
    public static boolean isKitKat() {
        return Build.VERSION.SDK_INT >= 19;
    }

}
