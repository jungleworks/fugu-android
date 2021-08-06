package com.skeleton.mvp.model.getAllMembers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllMember {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("fugu_user_id")
    @Expose
    private Long fuguUserId;
    @SerializedName("full_name")
    @Expose
    private String fullName="";
    @SerializedName("email")
    @Expose
    private String email="";
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("user_thumbnail_image")
    @Expose
    private String userThumbnailImage;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("contact_number")
    @Expose
    private String contactNumber="";
    @SerializedName("leave_type")
    @Expose
    private String leaveType = "";

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getFuguUserId() {
        return fuguUserId;
    }

    public void setFuguUserId(Long fuguUserId) {
        this.fuguUserId = fuguUserId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserThumbnailImage() {
        return userThumbnailImage;
    }

    public void setUserThumbnailImage(String userThumbnailImage) {
        this.userThumbnailImage = userThumbnailImage;
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

}
