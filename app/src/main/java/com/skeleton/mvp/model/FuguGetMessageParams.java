package com.skeleton.mvp.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.BuildConfig;

/**
 * Created by bhavya on 05/07/17.
 */

public class FuguGetMessageParams {
    @SerializedName("page_start")
    @Expose
    private Integer pageStart;
    @SerializedName("page_end")
    @Expose
    private Integer pageEnd;
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("en_user_id")
    @Expose
    private String userId;
    @SerializedName("custom_label")
    @Expose
    private String channelName = null;
    @SerializedName("device_type")
    @Expose
    private int deviceType = 1;
    @SerializedName("app_version")
    @Expose
    private String appVersion = BuildConfig.VERSION_NAME;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("device_details")
    @Expose
    private JsonObject deviceDetails;

    public FuguGetMessageParams(String appSecretKey, Long channelId,
                                String userId, Integer pageStart, Integer pageEnd, String channelName) {
        this.appSecretKey = appSecretKey;
        this.channelId = channelId;
        this.userId = userId;
        this.pageStart = pageStart;
        this.deviceType = 1;
        this.appVersion = BuildConfig.VERSION_NAME;
        this.pageEnd = pageEnd;
        this.deviceId = deviceId;
        this.deviceDetails = deviceDetails;
    }
    public FuguGetMessageParams(String appSecretKey, Long channelId,
                                String userId, Integer pageStart, Integer pageEnd, String channelName, String deviceId,
                                JsonObject deviceDetails) {
        this.appSecretKey = appSecretKey;
        this.channelId = channelId;
        this.userId = userId;
        this.pageStart = pageStart;
        this.deviceType = 1;
        this.appVersion = BuildConfig.VERSION_NAME;
        this.pageEnd = pageEnd;
        this.deviceId = deviceId;
        this.deviceDetails = deviceDetails;
    }
}
