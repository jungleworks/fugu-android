
package com.skeleton.mvp.data.model.fcCommon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenWorkspacesToJoin {

    @SerializedName("workspace_id")
    @Expose
    private Integer workspaceId;
    @SerializedName("email_domain")
    @Expose
    private String emailDomain;
    @SerializedName("fugu_secret_key")
    @Expose
    private String fuguSecretKey;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("workspace")
    @Expose
    private String workspace;
    @SerializedName("workspace_name")
    @Expose
    private String workspaceName;

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

}
