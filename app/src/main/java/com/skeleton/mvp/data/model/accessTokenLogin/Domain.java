
package com.skeleton.mvp.data.model.accessTokenLogin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Domain {

    @SerializedName("business_id")
    @Expose
    private Integer businessId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("fugu_secret_key")
    @Expose
    private String fuguSecretKey;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFuguSecretKey() {
        return fuguSecretKey;
    }

    public void setFuguSecretKey(String fuguSecretKey) {
        this.fuguSecretKey = fuguSecretKey;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
