package com.skeleton.mvp.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

public class UniqueIMEIID {
    public static String getUniqueIMEIId(Context activity) {
        String android_id = "";
        try {
            android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        android_id = TextUtils.isEmpty(android_id) ? Build.SERIAL : android_id;
        return android_id;
    }
}
