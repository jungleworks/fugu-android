package com.skeleton.mvp.data.model.getPublicInfo;

/********************************
 Created by Amandeep Chauhan     *
 Date :- 29/06/2020              *
 ********************************/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("public_invite_enabled")
    @Expose
    private boolean publicInviteEnabled;
    @SerializedName("registered_users")
    @Expose
    private int registeredUsers;
    @SerializedName("open_email_domains")
    @Expose
    private String[] openEmailDomains;
    @SerializedName("workspace_name")
    @Expose
    private String workspaceName;
    @SerializedName("user_already_exist")
    @Expose
    private boolean userAlreadyExist;
    @SerializedName("invitation_token")
    @Expose
    private String emailToken;

    public boolean getPublicInviteEnabled() {
        return publicInviteEnabled;
    }

    public void setPublicInviteEnabled(boolean value) {
        this.publicInviteEnabled = value;
    }

    public int getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(int value) {
        this.registeredUsers = value;
    }

    public String[] getOpenEmailDomains() {
        return openEmailDomains;
    }

    public void setOpenEmailDomains(String[] value) {
        this.openEmailDomains = value;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String value) {
        this.workspaceName = value;
    }

    public boolean isUserAlreadyExist() {
        return userAlreadyExist;
    }

    public void setUserAlreadyExist(boolean userAlreadyExist) {
        this.userAlreadyExist = userAlreadyExist;
    }

    public String getEmailToken() {
        return emailToken;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }
}
