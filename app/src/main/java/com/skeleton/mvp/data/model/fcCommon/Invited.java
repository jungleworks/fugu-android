package com.skeleton.mvp.data.model.fcCommon;

/**
 * Created by rajatdhamija on 03/03/18.
 */

public class Invited {

    private Integer workspaceId;
    private String emailDomain;
    private String fuguSecretKey;
    private String status;
    private String workspace;
    private String workspaceName;
    private String invitationToken;

    public Invited(Integer workspaceId, String emailDomain, String fuguSecretKey, String status, String workspace, String workspaceName, String invitationToken) {
        this.workspaceId = workspaceId;
        this.emailDomain = emailDomain;
        this.fuguSecretKey = fuguSecretKey;
        this.status = status;
        this.workspace = workspace;
        this.workspaceName = workspaceName;
        this.invitationToken = invitationToken;
    }

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getEmailDomain() {
        return emailDomain;
    }

    public void setEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    public String getFuguSecretKey() {
        return fuguSecretKey;
    }

    public void setFuguSecretKey(String fuguSecretKey) {
        this.fuguSecretKey = fuguSecretKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }
}
