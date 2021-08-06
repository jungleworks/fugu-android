package com.skeleton.mvp.data.model.fcCommon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.constant.FuguAppConstant;

public class WorkspacesInfo {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("role")
    @Expose
    private String role;
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
    @SerializedName("workspace_status")
    @Expose
    private String workspaceStatus;
    @SerializedName("online_status")
    @Expose
    private String onlineStatus;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("contact_number")
    @Expose
    private String contactNumber;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user_status")
    @Expose
    private String userStatus;
    @SerializedName("config")
    @Expose
    private Config config;
    @SerializedName("isCurrentLogin")
    @Expose
    private Boolean isCurrentLogin;

    @SerializedName("en_user_id")
    @Expose
    private String enUserId;

    @SerializedName("fugu_user_id")
    @Expose
    private String userId;

    @SerializedName("unread_count")
    @Expose
    private int unreadCount;

    @SerializedName("installed_apps")
    @Expose
    private int[] installedApps = {};

    @SerializedName("auto_download_level")
    @Expose
    private String autoDownloadLevel = FuguAppConstant.AutoDownloadLevel.BOTH.toString();

    @SerializedName("user_attendance_config")
    @Expose
    private UserAttendanceConfig userAttendanceConfig = new UserAttendanceConfig();

    @SerializedName("business_id")
    @Expose
    private Integer businessId = 0;

    @SerializedName("gallery_media_visibility")
    @Expose
    private int isMediaVisibility = 1;

    @SerializedName("is_conferencing_enabled")
    @Expose

    private int isConferencingEnabled=0;

    @SerializedName("billing_url")
    @Expose
    private String billingUrl="";

    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey="";

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public String getBillingUrl() {
        return billingUrl;
    }

    public void setBillingUrl(String billingUrl) {
        this.billingUrl = billingUrl;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getWorkspaceStatus() {
        return workspaceStatus;
    }

    public void setWorkspaceStatus(String workspaceStatus) {
        this.workspaceStatus = workspaceStatus;
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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public String getEnUserId() {
        return enUserId;
    }

    public void setEnUserId(String enUserId) {
        this.enUserId = enUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Boolean getCurrentLogin() {
        if (isCurrentLogin == null) {
            isCurrentLogin = false;
        }
        return isCurrentLogin;
    }

    public void setCurrentLogin(Boolean currentLogin) {
        isCurrentLogin = currentLogin;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getAutoDownloadLevel() {
        return autoDownloadLevel;
    }

    public void setAutoDownloadLevel(String autoDownloadLevel) {
        this.autoDownloadLevel = autoDownloadLevel;
    }

    public UserAttendanceConfig getUserAttendanceConfig() {
        return userAttendanceConfig;
    }

    public void setUserAttendanceConfig(UserAttendanceConfig userAttendanceConfig) {
        this.userAttendanceConfig = userAttendanceConfig;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public void setMediaVisibility(int isMediaVisibility){this.isMediaVisibility = isMediaVisibility;}

    public int getMediaVisibility(){return isMediaVisibility;}

    public int getIsConferencingEnabled() {
        return isConferencingEnabled;
    }

    public void setIsConferencingEnabled(int isConferencingEnabled) {
        this.isConferencingEnabled = isConferencingEnabled;
    }

    public int[] getInstalledApps() {
        return installedApps;
    }

    public void setInstalledApps(int[] installedApps) {
        this.installedApps = installedApps;
    }

    public int getIsMediaVisibility() {
        return isMediaVisibility;
    }

    public void setIsMediaVisibility(int isMediaVisibility) {
        this.isMediaVisibility = isMediaVisibility;
    }
}
