
package com.skeleton.mvp.data.model.fcCommon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("any_user_can_invite")
    @Expose
    private String anyUserCanInvite;

    public String getAnyUserCanInvite() {
        return anyUserCanInvite;
    }

    public void setAnyUserCanInvite(String anyUserCanInvite) {
        this.anyUserCanInvite = anyUserCanInvite;
    }

    @SerializedName("hide_contact_number")
    @Expose
    private String hideContactNumber = "0";

    @SerializedName("hide_email")
    @Expose
    private String hideEmail = "0";

    @SerializedName("clear_chat_history")
    @Expose
    private int clearChatHistory = 1;

    @SerializedName("delete_message")
    @Expose
    private int deleteMessage = 1;

    @SerializedName("edit_message")
    @Expose
    private int editMessage = 1;

    @SerializedName("delete_message_duration")
    @Expose
    private int deleteMessageDuration = 900;

    @SerializedName("edit_message_duration")
    @Expose
    private int editMessageDuration = 900;

    @SerializedName("enable_public_invite")
    @Expose
    private String enablePublicInvite = "0";

    @SerializedName("delete_message_role")
    @Expose
    private String deleteMessageRole;

    @SerializedName("edit_message_role")
    @Expose
    private String editMessageRole;

    @SerializedName("video_call_enabled")
    @Expose
    private String videoCallEnabled = "0";

    @SerializedName("audio_call_enabled")
    @Expose
    private String audioCallEnabled = "0";

    @SerializedName("is_guest_allowed")
    @Expose
    private String isGuestAllowed = "0";

    @SerializedName("create_workspace_permission")
    @Expose
    private String createWorkspacePermisson = "[\"ADMIN\",\"OWNER\",\"USER\"]";

    @SerializedName("create_meet_permission")
    @Expose
    private String createMeetPermission = "[\"ADMIN\",\"OWNER\"]";

    @SerializedName("livestream_permission")
    @Expose
    private String liveStreamPermissionRoles;

    @SerializedName("enable_create_group")
    @Expose
    private String enableCreateGroup = "[\"ADMIN\",\"OWNER\",\"USER\",\"GUEST\"]";

    @SerializedName("is_google_meet_enabled")
    @Expose
    private Boolean isGoogleMeetEnabled = false;

    @SerializedName("is_tasks_enabled")
    @Expose
    private Boolean isTasksEnabled = false;

    @SerializedName("is_group_live_stream_enabled")
    @Expose
    private String isGroupLiveStreamEnabled = "0";

    @SerializedName("enable_one_to_one_chat")
    @Expose
    private String enableOneToOneChat = "[\"ADMIN\",\"OWNER\",\"USER\",\"GUEST\"]";

    @SerializedName("max_conference_participants")
    @Expose
    private int max_conference_participants = 10;


    public String getHideContactNumber() {
        return hideContactNumber;
    }

    public void setHideContactNumber(String hideContactNumber) {
        this.hideContactNumber = hideContactNumber;
    }

    public String getHideEmail() {
        return hideEmail;
    }

    public void setHideEmail(String hideEmail) {
        this.hideEmail = hideEmail;
    }

    public int getDeleteMessage() {
        return deleteMessage;
    }

    public void setDeleteMessage(int deleteMessage) {
        this.deleteMessage = deleteMessage;
    }

    public int getDeleteMessageDuration() {
        return deleteMessageDuration;
    }

    public void setDeleteMessageDuration(int deleteMessageDuration) {
        this.deleteMessageDuration = deleteMessageDuration;
    }

    public String getDeleteMessageRole() {
        return deleteMessageRole;
    }

    public void setDeleteMessageRole(String deleteMessageRole) {
        this.deleteMessageRole = deleteMessageRole;
    }

    public int getClearChatHistory() {
        return clearChatHistory;
    }

    public void setClearChatHistory(int clearChatHistory) {
        this.clearChatHistory = clearChatHistory;
    }

    public String getEnablePublicInvite() {
        return enablePublicInvite;
    }

    public void setEnablePublicInvite(String enablePublicInvite) {
        this.enablePublicInvite = enablePublicInvite;
    }

    public String getVideoCallEnabled() {
        return videoCallEnabled;
    }

    public void setVideoCallEnabled(String videoCallEnabled) {
        this.videoCallEnabled = videoCallEnabled;
    }

    public String getAudioCallEnabled() {
        return audioCallEnabled;
    }

    public void setAudioCallEnabled(String audioCallEnabled) {
        this.audioCallEnabled = audioCallEnabled;
    }

    public int getEditMessageDuration() {
        return editMessageDuration;
    }

    public void setEditMessageDuration(int editMessageDuration) {
        this.editMessageDuration = editMessageDuration;
    }

    public String getEditMessageRole() {
        return editMessageRole;
    }

    public void setEditMessageRole(String editMessageRole) {
        this.editMessageRole = editMessageRole;
    }

    public int getEditMessage() {
        return editMessage;
    }

    public void setEditMessage(int editMessage) {
        this.editMessage = editMessage;
    }

    public String getIsGuestAllowed() {
        return isGuestAllowed;
    }

    public void setIsGuestAllowed(String isGuestAllowed) {
        this.isGuestAllowed = isGuestAllowed;
    }

    public String getEnableCreateGroup() {
        return enableCreateGroup;
    }

    public void setEnableCreateGroup(String enableCreateGroup) {
        this.enableCreateGroup = enableCreateGroup;
    }

    public String getEnableOneToOneChat() {
        return enableOneToOneChat;
    }

    public void setEnableOneToOneChat(String enableOneToOneChat) {
        this.enableOneToOneChat = enableOneToOneChat;
    }

    public String getCreateWorkspacePermisson() {
        return createWorkspacePermisson;
    }

    public void setCreateWorkspacePermisson(String createWorkspacePermisson) {
        this.createWorkspacePermisson = createWorkspacePermisson;
    }

    public String getCreateMeetPermission() {
        return createMeetPermission;
    }

    public void setCreateMeetPermission(String createMeetPermission) {
        this.createMeetPermission = createMeetPermission;
    }

    public Boolean getGoogleMeetEnabled() {
        return isGoogleMeetEnabled;
    }

    public void setGoogleMeetEnabled(Boolean googleMeetEnabled) {
        isGoogleMeetEnabled = googleMeetEnabled;
    }

    public Boolean getTasksEnabled() {
        return isTasksEnabled;
    }

    public void setTasksEnabled(Boolean tasksEnabled) {
        isTasksEnabled = tasksEnabled;
    }

    public String getIsGroupLiveStreamEnabled() {
        return isGroupLiveStreamEnabled;
    }

    public void setIsGroupLiveStreamEnabled(String isGroupLiveStreamEnabled) {
        this.isGroupLiveStreamEnabled = isGroupLiveStreamEnabled;
    }

    public String getLiveStreamPermissionRoles() {
        return liveStreamPermissionRoles;
    }

    public void setLiveStreamPermissionRoles(String liveStreamPermissionRoles) {
        this.liveStreamPermissionRoles = liveStreamPermissionRoles;
    }

    public int getMax_conference_participants() {
        return max_conference_participants;
    }

    public void setMax_conference_participants(int max_conference_participants) {
        this.max_conference_participants = max_conference_participants;
    }
}
