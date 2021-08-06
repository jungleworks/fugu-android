
package com.skeleton.mvp.model.getChannelInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("notification")
    @Expose
    private String notification;
    @SerializedName("label")
    @Expose
    private String label;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
