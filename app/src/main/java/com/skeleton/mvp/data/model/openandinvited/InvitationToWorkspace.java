
package com.skeleton.mvp.data.model.openandinvited;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvitationToWorkspace {

    @SerializedName("workspace_id")
    @Expose
    private Integer workspaceId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("invitation_token")
    @Expose
    private String invitationToken;
    @SerializedName("workspace")
    @Expose
    private String workspace;
    @SerializedName("workspace_name")
    @Expose
    private String workspaceName;
    @SerializedName("fugu_secret_key")
    @Expose
    private String fuguSecretKey;

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getFuguSecretKey() {
        return fuguSecretKey;
    }

    public void setFuguSecretKey(String fuguSecretKey) {
        this.fuguSecretKey = fuguSecretKey;
    }

}
