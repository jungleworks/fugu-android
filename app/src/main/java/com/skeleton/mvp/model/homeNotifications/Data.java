
package com.skeleton.mvp.model.homeNotifications;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("notifications")
    @Expose
    private List<Notification> notifications = null;

    @SerializedName("notification_page_size")
    @Expose
    private int notificationpageSize = 0;

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public int getNotificationpageSize() {
        return notificationpageSize;
    }

    public void setNotificationpageSize(int notificationpageSize) {
        this.notificationpageSize = notificationpageSize;
    }
}
