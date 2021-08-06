
package com.skeleton.mvp.data.model.getdomains;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The type Datum.
 */
public class Datum {

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

    /**
     * Gets business id.
     *
     * @return the business id
     */
    public Integer getBusinessId() {
        return businessId;
    }

    /**
     * Sets business id.
     *
     * @param businessId the business id
     */
    public void setBusinessId(final Integer businessId) {
        this.businessId = businessId;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets business name.
     *
     * @return the business name
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * Sets business name.
     *
     * @param businessName the business name
     */
    public void setBusinessName(final String businessName) {
        this.businessName = businessName;
    }

    /**
     * Gets domain.
     *
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets domain.
     *
     * @param domain the domain
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * Gets fugu secret key.
     *
     * @return the fugu secret key
     */
    public String getFuguSecretKey() {
        return fuguSecretKey;
    }

    /**
     * Sets fugu secret key.
     *
     * @param fuguSecretKey the fugu secret key
     */
    public void setFuguSecretKey(final String fuguSecretKey) {
        this.fuguSecretKey = fuguSecretKey;
    }

    /**
     * Gets created at.
     *
     * @return the created at
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets created at.
     *
     * @param createdAt the created at
     */
    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets updated at.
     *
     * @return the updated at
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets updated at.
     *
     * @param updatedAt the updated at
     */
    public void setUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
