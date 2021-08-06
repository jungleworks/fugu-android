
package com.skeleton.mvp.data.model.openandinvited;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.data.model.fcCommon.UserInfo;

public class Data {

    @SerializedName("invitation_to_workspaces")
    @Expose
    private List<com.skeleton.mvp.data.model.fcCommon.InvitationToWorkspace> invitationToWorkspaces = null;
    @SerializedName("open_workspaces_to_join")
    @Expose
    private List<com.skeleton.mvp.data.model.fcCommon.OpenWorkspacesToJoin> openWorkspacesToJoin = null;
    @SerializedName("whitelabel_properties")
    @Expose
    UserInfo.WhitelabelProperties whitelabelProperties;

    public List<com.skeleton.mvp.data.model.fcCommon.InvitationToWorkspace> getInvitationToWorkspaces() {
        return invitationToWorkspaces;
    }

    public void setInvitationToWorkspaces(List<com.skeleton.mvp.data.model.fcCommon.InvitationToWorkspace> invitationToWorkspaces) {
        this.invitationToWorkspaces = invitationToWorkspaces;
    }

    public List<com.skeleton.mvp.data.model.fcCommon.OpenWorkspacesToJoin> getOpenWorkspacesToJoin() {
        return openWorkspacesToJoin;
    }

    public void setOpenWorkspacesToJoin(List<com.skeleton.mvp.data.model.fcCommon.OpenWorkspacesToJoin> openWorkspacesToJoin) {
        this.openWorkspacesToJoin = openWorkspacesToJoin;
    }

    public UserInfo.WhitelabelProperties getWhitelabelProperties() {
        return whitelabelProperties;
    }

    public void setWhitelabelProperties(UserInfo.WhitelabelProperties whitelabelProperties) {
        this.whitelabelProperties = whitelabelProperties;
    }
}
