
package com.skeleton.mvp.data.model.accessTokenLogin;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("business_id")
    @Expose
    private Integer businessId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("is_admin")
    @Expose
    private Boolean isAdmin;
    @SerializedName("business_email")
    @Expose
    private String businessEmail;
    @SerializedName("business_status")
    @Expose
    private String businessStatus;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("online_status")
    @Expose
    private String onlineStatus;
    @SerializedName("app_update_message")
    @Expose
    private String appUpdateMessage;
    @SerializedName("fugu_secret_key")
    @Expose
    private String fuguSecretKey;
    @SerializedName("domains")
    @Expose
    private List<Domain> domains = null;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
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

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getAppUpdateMessage() {
        return appUpdateMessage;
    }

    public void setAppUpdateMessage(String appUpdateMessage) {
        this.appUpdateMessage = appUpdateMessage;
    }

    public String getFuguSecretKey() {
        return fuguSecretKey;
    }

    public void setFuguSecretKey(String fuguSecretKey) {
        this.fuguSecretKey = fuguSecretKey;
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

}
