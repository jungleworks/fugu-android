package com.skeleton.mvp.model;

/**
 * Created by Bhavya Rattan on 10/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.gson.annotations.SerializedName;


public class FuguDeviceDetails {
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

    public FuguDeviceDetails(int appVersion) {
        this.app_version = appVersion;
    }

    public FuguDeviceDetails getDeviceDetails() throws PackageManager.NameNotFoundException {
        return new FuguDeviceDetails(app_version);
    }
}
