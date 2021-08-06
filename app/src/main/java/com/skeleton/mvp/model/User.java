package com.skeleton.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {

    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_image")
    @Expose
    private String userImage;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

}