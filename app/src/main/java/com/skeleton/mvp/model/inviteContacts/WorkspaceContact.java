
package com.skeleton.mvp.model.inviteContacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkspaceContact {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email="";
    @SerializedName("contact_number")
    @Expose
    private String contactNumber="";

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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

}
