package com.skeleton.mvp.data.model;

/**
 * Raj v Dhamija
 * 29/12/17.
 */

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.gson.annotations.SerializedName;


/**
 * The type Office chat device details.
 */
public class OfficeChatDeviceDetails {
    private Context context;
    @SerializedName("android_operating_system")
    private String operating_system = "Android";
    @SerializedName("android_model")
    private String model = android.os.Build.MODEL;
    @SerializedName("android_manufacturer")
    private String manufacturer = android.os.Build.MANUFACTURER;
    @SerializedName("android_app_version")
    private int app_version = 0;
    @SerializedName("android_os_version")
    private String os_version = android.os.Build.VERSION.RELEASE;

    /**
     * Instantiates a new Office chat device details.
     *
     * @param appVersion the app version
     */
    public OfficeChatDeviceDetails(int appVersion) {
        this.app_version = appVersion;
    }

    /**
     * Gets device details.
     *
     * @return the device details
     */
    public OfficeChatDeviceDetails getDeviceDetails() throws PackageManager.NameNotFoundException {
        return new OfficeChatDeviceDetails(app_version);
    }
}
