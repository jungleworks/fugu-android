package com.skeleton.mvp.data.model.getnotifications;

import com.skeleton.mvp.data.model.CommonResponse;

import java.io.Serializable;

/**
 * Created by bhavya on 16/11/17.
 */

public class NotificationsResponse extends CommonResponse implements Serializable {

    public NotificationData getNotificationData() {
        return toResponseModel(NotificationData.class);
    }
}
