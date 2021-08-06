
package com.skeleton.mvp.data.model.getInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("attributes")
    @Expose
    private Attributes attributes;
    @SerializedName("notification_level")
    @Expose
    private String notificationLevel;
    @SerializedName("user_properties")
    @Expose
    private UserProperties userProperties;

    @SerializedName("notification_snooze_time")
    @Expose
    private String notification_snooze_time = "";

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public String getNotificationLevel() {
        return notificationLevel;
    }

    public void setNotificationLevel(String notificationLevel) {
        this.notificationLevel = notificationLevel;
    }

    public UserProperties getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(UserProperties userProperties) {
        this.userProperties = userProperties;
    }

    public String getNotification_snooze_time() {
        if (notification_snooze_time==null){
            return "";
        }else {
            return notification_snooze_time;
        }
    }

    public void setNotification_snooze_time(String notification_snooze_time) {
        this.notification_snooze_time = notification_snooze_time;
    }
}
