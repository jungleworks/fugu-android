package com.skeleton.mvp.model;

public class InvitedMembers {
    private String data;
    private boolean isEmail;
    private String dateTime;
    private String isExpired;
    private boolean isExpanded;

    public InvitedMembers(String data, boolean isEmail, String dateTime, String isExpired, boolean isExpanded) {
        this.data = data;
        this.isEmail = isEmail;
        this.dateTime = dateTime;
        this.isExpired = isExpired;
        this.isExpanded = isExpanded;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isEmail() {
        return isEmail;
    }

    public void setEmail(boolean email) {
        isEmail = email;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(String isExpired) {
        this.isExpired = isExpired;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
