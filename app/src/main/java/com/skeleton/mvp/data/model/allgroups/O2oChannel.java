package com.skeleton.mvp.data.model.allgroups;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class O2oChannel {

    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("label")
    @Expose
    private String label = "";
    @SerializedName("user_image")
    @Expose
    private Object userImage;
    @SerializedName("last_activity")
    @Expose
    private String lastActivity;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return label;
    }

    public void setFullName(String fullName) {
        this.label = fullName;
    }

    public Object getUserImage() {
        return userImage;
    }

    public void setUserImage(Object userImage) {
        this.userImage = userImage;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
