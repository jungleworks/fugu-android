package com.skeleton.mvp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Developer: Saurabh Verma
 * Dated: 19-02-2017.
 */

/**
 * Usage:
 * <p>
 * A. Create Application class
 * B. Insert Foreground.init(this); to onCreate() of Application class
 * C. call Foreground.get().isForeground() where you want.
 * <p>
 * 1. Get the Foreground Singleton, passing a Context or Application object unless you
 * are sure that the Singleton has definitely already been initialised elsewhere.
 * <p>
 * 2.a) Perform a direct, synchronous check: Foreground.isForeground() / .isBackground()
 * <p>
 * or
 * <p>
 * 2.b) Register to be notified (useful in Service or other non-UI components):
 * <p>
 * Foreground.Listener myListener = new Foreground.Listener(){
 * public void onBecameForeground(){
 * // ... whatever you want to do
 * }
 * public void onBecameBackground(){
 * // ... whatever you want to do
 * }
 * }
 * <p>
 * public void onCreate(){
 * super.onCreate();
 * Foreground.get(this).addListener(listener);
 * }
 * <p>
 * public void onDestroy(){
 * super.onCreate();
 * Foreground.get(this).removeListener(listener);
 * }
 */
public class Foreground implements Application.ActivityLifecycleCallbacks {

    private static final long CHECK_DELAY = 500;
    private static final String TAG = Foreground.class.getName();
    private static Foreground instance;
    private boolean foreground = false, paused = true;
//    private Handler handler = new Handler();
    private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    private Runnable check;

    /**
     * Its not strictly necessary to use this method - _usually_ invoking
     * get with a Context gives us a path to retrieve the Application and
     * initialise, but sometimes (e.g. in test harness) the ApplicationContext
     * is != the Application, and the docs make no guarantees.
     *
     * @param application Instance of application class of app
     * @return an initialised Foreground instance
     */
    public static Foreground init(final Application application) {
        if (instance == null) {
            instance = new Foreground();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    /**
     * Get foreground.
     *
     * @param application the application
     * @return the foreground
     */
    public static Foreground get(final Application application) {
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    /**
     * Get foreground.
     *
     * @param ctx the ctx
     * @return the foreground
     */
    public static Foreground get(final Context ctx) {
        if (instance == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                return init((Application) appCtx);
            }
            throw new IllegalStateException("Foreground is not initialised and cannot obtain the Application object");
        }
        return instance;
    }

    /**
     * Get foreground.
     *
     * @return the foreground
     */
    public static Foreground get() {
        if (instance == null) {
            throw new IllegalStateException("Foreground is not initialised - invoke at least once with parameterised init/get");
        }
        return instance;
    }

    /**
     * Is foreground boolean.
     *
     * @return the boolean
     */
    public boolean isForeground() {
        return foreground;
    }

    /**
     * Is background boolean.
     *
     * @return the boolean
     */
    public boolean isBackground() {
        return !foreground;
    }

    /**
     * Add listener.
     *
     * @param listener the listener
     */
    public void addListener(final Listener listener) {
        listeners.add(listener);
    }

    /**
     * Remove listener.
     *
     * @param listener the listener
     */
    public void removeListener(final Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityResumed(final Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;
        if (check != null) {
//            handler.removeCallbacks(check);
        }
        if (wasBackground) {
            FuguLog.i(TAG, "went foreground");
            for (Listener l : listeners) {
                if (l != null) {
                    l.onBecameForeground();
                }
            }
        } else {
            FuguLog.i(TAG, "still foreground");
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        paused = true;
        if (check != null) {
//            handler.removeCallbacks(check);
        }
        check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    FuguLog.i(TAG, "went background");
                    for (Listener l : listeners) {
                        if (l != null) {
                            l.onBecameBackground();
                        }
                    }
                } else {
                    FuguLog.i(TAG, "still foreground");
                }
            }
        };
//        handler.postDelayed(check, CHECK_DELAY);
    }

    @Override
    public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(final Activity activity) {
    }

    @Override
    public void onActivityStopped(final Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(final Activity activity) {
    }

    /**
     * The interface Listener.
     */
    public interface Listener {
        /**
         * On became foreground.
         */
        void onBecameForeground();

        /**
         * On became background.
         */
        void onBecameBackground();
    }
}
