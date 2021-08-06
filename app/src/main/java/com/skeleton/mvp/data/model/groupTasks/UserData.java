package com.skeleton.mvp.data.model.groupTasks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserData implements Serializable {
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
    private long userID;
    @SerializedName("is_completed")
    @Expose
    private long isCompleted;

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

    public long getUserID() {
        return userID;
    }

    public void setUserID(long value) {
        this.userID = value;
    }

    public long getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(long value) {
        this.isCompleted = value;
    }
}
