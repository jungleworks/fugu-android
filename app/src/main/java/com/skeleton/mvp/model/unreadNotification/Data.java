
package com.skeleton.mvp.model.unreadNotification;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("unread_notification")
    @Expose
    private List<UnreadNotification> unreadNotification = null;

    public List<UnreadNotification> getUnreadNotification() {
        return unreadNotification;
    }

    public void setUnreadNotification(List<UnreadNotification> unreadNotification) {
        this.unreadNotification = unreadNotification;
    }

}
