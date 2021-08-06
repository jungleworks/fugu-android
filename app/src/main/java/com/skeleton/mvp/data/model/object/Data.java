
package com.skeleton.mvp.data.model.object;


import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("workspace_name")
    @Expose
    private String workspaceName;
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
    @SerializedName("config")
    @Expose
    private com.skeleton.mvp.data.model.fcCommon.Config config;

    @SerializedName("isCurrentLogin")
    @Expose
    private Boolean isCurrentLogin;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
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
        if (!TextUtils.isEmpty(status)){
            onlineStatus="ENABLED";
        }
        this.status = status;
    }

    public String getOnlineStatus() {
        if (!TextUtils.isEmpty(onlineStatus)){
            onlineStatus="ONLINE";
        }
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getLocation() {
        if (!TextUtils.isEmpty(location)){
            location="";
        }
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDepartment() {
        if (!TextUtils.isEmpty(department)){
            department="";
        }
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        if (!TextUtils.isEmpty(designation)){
            designation="";
        }
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

    public com.skeleton.mvp.data.model.fcCommon.Config getConfig() {
        return config;
    }

    public void setConfig(com.skeleton.mvp.data.model.fcCommon.Config config) {
        this.config = config;
    }

    public Boolean getCurrentLogin() {
        return isCurrentLogin;
    }

    public void setCurrentLogin(Boolean currentLogin) {
        isCurrentLogin = currentLogin;
    }
}
