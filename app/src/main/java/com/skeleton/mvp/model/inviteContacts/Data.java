
package com.skeleton.mvp.model.inviteContacts;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("contacts")
    @Expose
    private List<Contact> contacts = new ArrayList<>();
    @SerializedName("workspace_contacts")
    @Expose
    private List<WorkspaceContact> workspaceContacts = new ArrayList<>();
    @SerializedName("user_groups")
    @Expose
    private List<UserGroups> userGroups = new ArrayList<>();


    @SerializedName("invite_emails")
    @Expose
    private List<String> googleContacts=new ArrayList<>();


    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<WorkspaceContact> getWorkspaceContacts() {
        return workspaceContacts;
    }

    public void setWorkspaceContacts(List<WorkspaceContact> workspaceContacts) {
        this.workspaceContacts = workspaceContacts;
    }

    public List<UserGroups> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroups> userGroups) {
        this.userGroups = userGroups;
    }

    public List<String> getGoogleContacts() {
        return googleContacts;
    }

    public void setGoogleContacts(List<String> googleContacts) {
        this.googleContacts = googleContacts;
    }
}


