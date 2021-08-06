package com.skeleton.mvp.model.inviteContacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserGroups {


    @SerializedName("workspace_name")
    @Expose
    private String workspaceName;
    @SerializedName("groups")
    @Expose
    private List<Group> groups = null;

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

}
