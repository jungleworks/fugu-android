package com.skeleton.mvp.data.model.scheduleMeets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Attendee implements Serializable {
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_thumbnail_image")
    @Expose
    private String userThumbnailImage;
    @SerializedName("user_id")
    @Expose
    private long userId;
    @SerializedName("self")
    @Expose
    private boolean self = false;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String value) {
        this.fullName = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public String getUserThumbnailImage() {
        return userThumbnailImage;
    }

    public void setUserThumbnailImage(String value) {
        this.userThumbnailImage = value;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long value) {
        this.userId = value;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }
}
