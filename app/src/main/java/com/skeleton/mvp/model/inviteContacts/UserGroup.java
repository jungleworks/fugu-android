
package com.skeleton.mvp.model.inviteContacts;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserGroup {

    @SerializedName("custom_label")
    @Expose
    private String customLabel;
    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("emails")
    @Expose
    private List<String> emails = null;
    @SerializedName("contact_numbers")
    @Expose
    private List<String> contactNumbers = null;

    public String getCustomLabel() {
        return customLabel;
    }

    public void setCustomLabel(String customLabel) {
        this.customLabel = customLabel;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getContactNumbers() {
        return contactNumbers;
    }

    public void setContactNumbers(List<String> contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

}
