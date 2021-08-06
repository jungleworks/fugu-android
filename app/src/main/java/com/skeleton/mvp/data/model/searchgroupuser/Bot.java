package com.skeleton.mvp.data.model.searchgroupuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bot {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("user_image")
    @Expose
    private String userImage;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

}