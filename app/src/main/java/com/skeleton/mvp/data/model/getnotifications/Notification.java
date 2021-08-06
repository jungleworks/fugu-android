package com.skeleton.mvp.data.model.getnotifications;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.data.model.BaseRecyclerModel;

import java.io.Serializable;

/**
 * Created by bhavya on 16/11/17.
 *
 */

public class Notification extends BaseRecyclerModel implements Serializable {

    @SerializedName("notificationType")
    @Expose
    private String notificationType;
    @SerializedName("message")
    @Expose
    private String message;

    public String getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }
}
