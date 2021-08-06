
package com.skeleton.mvp.model.userSearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_type")
    @Expose
    private Integer userType;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("contact_number")
    @Expose
    private String contactNumber;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("user_thumbnail_image")
    @Expose
    private String userThumbnailImage;

    @SerializedName("role")
    @Expose
    private String role = "USER";

    @SerializedName("leave_type")
    @Expose
    private String leaveType;

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserThumbnailImage() {
        return userThumbnailImage;
    }

    public void setUserThumbnailImage(String userThumbnailImage) {
        this.userThumbnailImage = userThumbnailImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmails(String email) {
        this.email = email;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
