package com.dailymotion.sdk.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper class around android.util.log so that we can filter more easily
 */
public final class DMLog {
    private static final String TAG = "DMLog";

    private static final int DEBUG = 0;
    private static final int ERROR = 1;

    private static boolean sIsEnabled = false;
    private static Map<String, Boolean> sEnabledLogs = new HashMap<>();

    public static final String STUFF = "Stuff";
    public static final String SYNC = "Sync";
    public static final String GATEKEEPER = "Gatekeeper";
    public static final String LIFECYCLE = "Lifecycle";
    public static final String INTERSTITIAL = "Interstitial";
    public static final String REQUEST = "Request";

    static {
        sEnabledLogs.put(SYNC, false);
    }

    private DMLog() {
        throw new UnsupportedOperationException("This is an utility class that should not be instantiated.");
    }

    public static void setEnabled(boolean enabled) {
        sIsEnabled = enabled;
    }

    public static boolean isEnabled() {
        return sIsEnabled;
    }

    public static void d(String feature, String msg) {
        doLog(feature, msg, DEBUG);
    }

    public static void d(String feature, String format, Object... args) {
        doLog(feature, String.format(format, args), DEBUG);
    }

    public static void d(String feature, String msg, Throwable tr) {
        doLog(feature, msg + '\n' + Log.getStackTraceString(tr), DEBUG);
    }

    public static void e(String feature, String msg) {
        doLog(feature, msg, ERROR);
    }

    public static void e(String feature, String format, Object... args) {
        doLog(feature, String.format(format, args), ERROR);
    }

    public static void e(String feature, String msg, Throwable tr) {
        doLog(feature, msg + '\n' + Log.getStackTraceString(tr), ERROR);
    }

    private static void doLog(String feature, String msg, int level) {
        if (feature == null) {
            feature = "";
        }

        if (!sIsEnabled) {
            // globally disabled
            return;
        }

        if (sEnabledLogs.get(feature) != null && !sEnabledLogs.get(feature)) {
            // individually disabled
            return;
        }

        msg = feature + ": " + msg;

        switch(level) {
            case DEBUG:
                Log.d(TAG, msg);
                break;
            case ERROR:
                Log.e(TAG, msg);
                break;
        }
    }
}
