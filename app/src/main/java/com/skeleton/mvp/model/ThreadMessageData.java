package com.skeleton.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Set;

public class ThreadMessageData implements Serializable {
    @SerializedName("full_name")
    @Expose
    String fullName = "";

    @SerializedName("user_image_50x50")
    @Expose
    String userImage50x50 = "";

    @SerializedName("user_id")
    @Expose
    Long userId = -1L;

    public ThreadMessageData(String fullName, String userImage50x50, Long userId) {
        this.fullName = fullName;
        this.userImage50x50 = userImage50x50;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserImage50x50() {
        return userImage50x50;
    }

    public void setUserImage50x50(String userImage50x50) {
        this.userImage50x50 = userImage50x50;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
