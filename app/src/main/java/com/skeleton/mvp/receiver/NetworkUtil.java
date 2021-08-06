package com.skeleton.mvp.receiver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rajatdhamija on 26/10/17.
 */

public final class NetworkUtil {
    private static final int TYPE_WIFI = 1;
    private static final int TYPE_MOBILE = 2;
    private static final int TYPE_NOT_CONNECTED = 0;

    /**
     * Empty Constructor
     * not called
     */
    private NetworkUtil() {
    }


    /**
     * Gets connectivity status.
     *
     * @param context the context
     * @return the connectivity status
     */
    public static int getConnectivityStatus(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    /**
     * Gets connectivity status string.
     *
     * @param context the context
     * @return the connectivity status string
     */
    public static int getConnectivityStatusCode(final Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        int status = 0;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = 1;
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = 2;
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = 0;
        }
        return status;
    }
}
