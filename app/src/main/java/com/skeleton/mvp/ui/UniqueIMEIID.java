package com.skeleton.mvp.ui;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by rajatdhamija on 29/12/17.
 */

public class UniqueIMEIID {

    public static String getUniqueIMEIId(Context activity) {
        String android_id = "";
        try {

            try {
                try {
                    android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                android_id = TextUtils.isEmpty(android_id) ? Build.SERIAL : android_id;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return android_id;
    }
}

