
package com.skeleton.mvp.model.alreadyInvited;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvitedUser {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("contact_number")
    @Expose
    private String contactNumber;
    @SerializedName("status")
    @Expose
    private String expired;
    @SerializedName("date_time")
    @Expose
    private String dateTime;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
