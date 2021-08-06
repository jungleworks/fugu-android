
package com.skeleton.mvp.model.media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.constant.FuguAppConstant;

import java.io.Serializable;

public class ChatMember implements Serializable {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("user_type")
    @Expose
    private Integer userType;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("app_name")
    @Expose
    private String appName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("role")
    @Expose
    private String role = FuguAppConstant.Role.USER.toString();
    @SerializedName("notification")
    @Expose
    private String notification = "UNMUTED";

    @SerializedName("leave_type")
    @Expose
    private String leaveType;

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
