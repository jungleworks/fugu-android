package com.skeleton.mvp.util;

/**
 * Developer: Click Labs
 */

/**
 * API for sending log output.
 * <p>
 * <p>Generally, use the Log.v() Log.d() Log.i() Log.w() and Log.e()
 * methods.
 * <p>
 * <p>The order in terms of verbosity, from least to most is
 * ERROR, WARN, INFO, DEBUG, VERBOSE.  Verbose should never be compiled
 * into an application except during development.  Debug logs are compiled
 * in but stripped at runtime.  Error, warning and info logs are always kept.
 * <p>
 * <p><b>Tip:</b> A good convention is to declare a <code>TAG</code> constant
 * in your class:
 * <p>
 * <pre>private static final String TAG = "MyActivity";</pre>
 *
 * and use that in subsequent calls to the log methods.
 * </p>
 *
 * <p><b>Tip:</b> Don't forget that when you make a call like
 * <pre>Log.v(TAG, "index=" + i);</pre>
 * that when you're building the string to pass into Log.d, the compiler uses a
 * StringBuilder and at least three allocations occur: the StringBuilder
 * itself, the buffer, and the String object.  Realistically, there is also
 * another buffer allocation and copy, and even more pressure on the gc.
 * That means that if your log message is filtered out, you might be doing
 * significant work and incurring significant overhead.
 */
public final class Log {
    private static final boolean PRINT = true;

    /**
     * Empty Constructor
     * not called
     */
    private Log() {
    }

    /**
     * Send a INFO log message.
     *
     * @param tag     Used to identify the source of a log message.  It usually identifies
     *                the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void i(final String tag, final String message) {
        if (PRINT) {
            android.util.Log.i(tag, message);
        }
    }

    /**
     * Send a DEBUG log message.
     *
     * @param tag     Used to identify the source of a log message.  It usually identifies
     *                the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void d(final String tag, final String message) {
        if (PRINT) {
            android.util.Log.d(tag, message);
        }
    }

    /**
     * Send an ERROR log message.
     *
     * @param tag     Used to identify the source of a log message.  It usually identifies
     *                the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void e(final String tag, final String message) {
        if (PRINT) {
            android.util.Log.e(tag, message);
        }
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param tag     Used to identify the source of a log message.  It usually identifies
     *                the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void v(final String tag, final String message) {
        if (PRINT) {
            android.util.Log.v(tag, message);
        }
    }

    /**
     * Send a WARN log message.
     *
     * @param tag     Used to identify the source of a log message.  It usually identifies
     *                the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void w(final String tag, final String message) {
        if (PRINT) {
            android.util.Log.w(tag, message);
        }
    }
}
