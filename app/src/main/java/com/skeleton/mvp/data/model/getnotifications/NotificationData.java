package com.skeleton.mvp.data.model.getnotifications;

/**
 * Created by bhavya on 26/12/17.
 * juggernaut-android-mvp
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Data class to fetch notification details data
 */
public class NotificationData implements Serializable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("notifications")
    @Expose
    private ArrayList<Notification> notifications = new ArrayList<>();

    /**
     * Parameterized constructor
     * @param count notification count
     * @param notifications list of notifications
     */
    public NotificationData(final int count, final ArrayList<Notification> notifications) {
        this.count = count;
        this.notifications = notifications;
    }

    public int getCount() {
        return count;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }
}
