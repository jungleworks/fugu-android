
package com.skeleton.mvp.data.model.fcCommon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.model.fayeVideoCall.Message;

import java.util.List;

public class Data {

    @SerializedName("invitation_to_workspaces")
    @Expose
    private List<InvitationToWorkspace> invitationToWorkspaces = null;
    @SerializedName("open_workspaces_to_join")
    @Expose
    private List<OpenWorkspacesToJoin> openWorkspacesToJoin = null;
    @SerializedName("workspaces_info")
    @Expose
    private List<WorkspacesInfo> workspacesInfo = null;
    @SerializedName("user_info")
    @Expose
    public UserInfo userInfo;
    @SerializedName("supported_file_type")
    @Expose
    private List<String> supportedFileType = null;
    @SerializedName("fugu_config")
    @Expose
    private FuguConfig fuguConfig;
    @SerializedName("turn_credentials")
    @Expose
    private Message turnCredentials;
    // Commented because userLogin sends data as String and tokenLogin sends Parsed so it gives error in userLogin case
//    @SerializedName("whitelabel_details")
//    @Expose
//    private WhitelabelDetails whitelabelDetails;
    @SerializedName("last_notification_id")
    @Expose
    private long lastNotificationId = 0L;
    @SerializedName("signup_type") // 1 for Login, 2 for SignUp
    @Expose
    private int signupType;
    @SerializedName("unread_notification_count")
    @Expose
    private int unread_notification_count = 0;
    @SerializedName("invitation_token")
    @Expose
    private String setPasswordEmailToken = "";
    @SerializedName("workspace")
    @Expose
    private String setPasswordWorkspace = "";

    public String getSetPasswordEmailToken() {
        return setPasswordEmailToken;
    }

    public void setSetPasswordEmailToken(String setPasswordEmailToken) {
        this.setPasswordEmailToken = setPasswordEmailToken;
    }

    public String getSetPasswordWorkspace() {
        return setPasswordWorkspace;
    }

    public void setSetPasswordWorkspace(String setPasswordWorkspace) {
        this.setPasswordWorkspace = setPasswordWorkspace;
    }

    public int getUnread_notification_count() {
        return unread_notification_count;
    }

    public void setUnread_notification_count(int unread_notification_count) {
        this.unread_notification_count = unread_notification_count;
    }

    public long getLastNotificationId() {
        return lastNotificationId;
    }

    public void setLastNotificationId(long lastNotificationId) {
        this.lastNotificationId = lastNotificationId;
    }

    /**
    * signupType = 1 for Login, 2 for SignUp
    */
    public int getSignupType() {
        return signupType;
    }

    public void setSignupType(int signupType) {
        this.signupType = signupType;
    }


    public FuguConfig getFuguConfig() {
        return fuguConfig;
    }

    public void setFuguConfig(FuguConfig fuguConfig) {
        this.fuguConfig = fuguConfig;
    }

    public List<InvitationToWorkspace> getInvitationToWorkspaces() {
        return invitationToWorkspaces;
    }

    public void setInvitationToWorkspaces(List<InvitationToWorkspace> invitationToWorkspaces) {
        this.invitationToWorkspaces = invitationToWorkspaces;
    }

    public List<OpenWorkspacesToJoin> getOpenWorkspacesToJoin() {
        return openWorkspacesToJoin;
    }

    public void setOpenWorkspacesToJoin(List<OpenWorkspacesToJoin> openWorkspacesToJoin) {
        this.openWorkspacesToJoin = openWorkspacesToJoin;
    }

    public List<WorkspacesInfo> getWorkspacesInfo() {
        return workspacesInfo;
    }

    public void setWorkspacesInfo(List<WorkspacesInfo> workspacesInfo) {
        this.workspacesInfo = workspacesInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<String> getSupportedFileType() {
        return supportedFileType;
    }

    public void setSupportedFileType(List<String> supportedFileType) {
        this.supportedFileType = supportedFileType;
    }

    public Message getTurnCredentials() {
        if (turnCredentials == null) {
            return new Message();
        }
        return turnCredentials;
    }

    public void setTurnCredentials(Message turnCredentials) {
        this.turnCredentials = turnCredentials;
    }

    public class FuguConfig {

        @SerializedName("socket_timeout")
        @Expose
        private int socketTimeout = 90;
        @SerializedName("supported_file_type")
        @Expose
        private List<String> supportedFileType = null;
        @SerializedName("max_upload_file_size")
        @Expose
        private Long max_upload_file_size = 0L;
        @SerializedName("is_new_conference_enabled")
        @Expose
        private int isNewConferenceEnabled = 0;

        public int getIsNewConferenceEnabled() {
            return isNewConferenceEnabled;
        }

        public void setIsNewConferenceEnabled(int isNewConferenceEnabled) {
            this.isNewConferenceEnabled = isNewConferenceEnabled;
        }

        public List<String> getSupportedFileType() {
            return supportedFileType;
        }

        public void setSupportedFileType(List<String> supportedFileType) {
            this.supportedFileType = supportedFileType;
        }

        public Long getMax_upload_file_size() {
            return max_upload_file_size;
        }

        public void setMax_upload_file_size(Long max_upload_file_size) {
            this.max_upload_file_size = max_upload_file_size;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }
    }
}
