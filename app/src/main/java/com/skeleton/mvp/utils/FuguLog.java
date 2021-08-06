package com.skeleton.mvp.utils;

/**
 * Custom log class overrides Android FuguLog (android.util.FuguLog)
 *
 * Created by ramangoyal on 09/04/15.
 */
public class FuguLog {

    private static final boolean PRINT = true; // true for printing and false
    // for not

    public FuguLog() {
    }

    public static void i(String tag, String message) {
        if (PRINT) {
            android.util.Log.i(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (PRINT) {
            android.util.Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (PRINT) {
            android.util.Log.e(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (PRINT) {
            android.util.Log.v(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (PRINT) {
            android.util.Log.w(tag, message);
        }
    }
}