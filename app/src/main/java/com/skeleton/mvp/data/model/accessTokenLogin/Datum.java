
package com.skeleton.mvp.data.model.accessTokenLogin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("workspace_email")
    @Expose
    private String workspaceEmail;
    @SerializedName("workspace")
    @Expose
    private String workspace;
    @SerializedName("workspace_id")
    @Expose
    private Integer workspaceId;
    @SerializedName("fugu_secret_key")
    @Expose
    private String fuguSecretKey;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("online_status")
    @Expose
    private String onlineStatus;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("contact_number")
    @Expose
    private String contactNumber;
    @SerializedName("app_update_message")
    @Expose
    private String appUpdateMessage;
    @SerializedName("is_admin")
    @Expose
    private Boolean isAdmin;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    public String getWorkspaceEmail() {
        return workspaceEmail;
    }

    public void setWorkspaceEmail(String workspaceEmail) {
        this.workspaceEmail = workspaceEmail;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getFuguSecretKey() {
        return fuguSecretKey;
    }

    public void setFuguSecretKey(String fuguSecretKey) {
        this.fuguSecretKey = fuguSecretKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAppUpdateMessage() {
        return appUpdateMessage;
    }

    public void setAppUpdateMessage(String appUpdateMessage) {
        this.appUpdateMessage = appUpdateMessage;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
