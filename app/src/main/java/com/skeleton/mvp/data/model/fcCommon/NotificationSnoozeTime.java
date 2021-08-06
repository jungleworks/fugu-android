package com.skeleton.mvp.data.model.fcCommon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationSnoozeTime {
    @SerializedName("time_slot")
    @Expose
    private String time_slot;
    @SerializedName("description")
    @Expose
    private String description;

    public String getTime_slot() {
        return time_slot;
    }

    public void setTime_slot(String time_slot) {
        this.time_slot = time_slot;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
