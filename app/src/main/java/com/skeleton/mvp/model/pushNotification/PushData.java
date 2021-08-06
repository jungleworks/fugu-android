package com.skeleton.mvp.model.pushNotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PushData {

    @SerializedName("push_notifications")
    @Expose
    private List<Datum> pushNotifications;

    @SerializedName("last_notification_id")
    @Expose
    private Long lastNotificationId;

    public List<Datum> getPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(List<Datum> pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public Long getLastNotificationId() {
        return lastNotificationId;
    }

    public void setLastNotificationId(Long lastNotificationId) {
        this.lastNotificationId = lastNotificationId;
    }
}
