
package com.skeleton.mvp.data.model.fcCommon;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("access_token")
    @Expose
    public String accessToken;
    @SerializedName("contact_number")
    @Expose
    private String contactNumber;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("attributes")
    @Expose
    private Attributes attributes;
    @SerializedName("app_update_config")
    @Expose
    private AppUpdateConfig appUpdateConfig;
    @SerializedName("user_channel")
    @Expose
    private String userChannel = "";
    @SerializedName("push_token")
    @Expose
    private String pushToken = "";
    @SerializedName("unread_notification_count")
    @Expose
    private int unreadNotificationCount;
    @SerializedName("has_google_contacts")
    @Expose
    private boolean hasGoogleContacts = false;
    @SerializedName("is_calendar_linked")
    @Expose
    private boolean isCalendarLinked = false;
    @SerializedName("is_test_fairy_enabled")
    @Expose
    private boolean isTestFairyEnabled = false;
    @SerializedName("test_fairy_app_token")
    @Expose
    private String testFairyAppToken = "SDK-eb7tOUrK";
    @SerializedName("notification_snooze_time")
    @Expose
    private List<NotificationSnoozeTime> notificationSnoozeTime = new ArrayList<>();
    @SerializedName("workspace_properties")
    @Expose
    private WhitelabelProperties whitelabelProperties;

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public AppUpdateConfig getAppUpdateConfig() {
        return appUpdateConfig;
    }

    public void setAppUpdateConfig(AppUpdateConfig appUpdateConfig) {
        this.appUpdateConfig = appUpdateConfig;
    }

    public String getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(String userChannel) {
        this.userChannel = userChannel;
    }

    public int getUnreadNotificationCount() {
        return unreadNotificationCount;
    }

    public void setUnreadNotificationCount(int unreadNotificationCount) {
        this.unreadNotificationCount = unreadNotificationCount;
    }

    public boolean isHasGoogleContacts() {
        return hasGoogleContacts;
    }

    public void setHasGoogleContacts(boolean hasGoogleContacts) {
        this.hasGoogleContacts = hasGoogleContacts;
    }

    public boolean isCalendarLinked() {
        return isCalendarLinked;
    }

    public void setCalendarLinked(boolean calendarLinked) {
        isCalendarLinked = calendarLinked;
    }

    public boolean isTestFairyEnabled() {
        return isTestFairyEnabled;
    }

    public void setTestFairyEnabled(boolean testFairyEnabled) {
        isTestFairyEnabled = testFairyEnabled;
    }

    public String getTestFairyAppToken() {
        return testFairyAppToken;
    }

    public void setTestFairyAppToken(String testFairyAppToken) {
        this.testFairyAppToken = testFairyAppToken;
    }

    public List<NotificationSnoozeTime> getNotificationSnoozeTime() {
        return notificationSnoozeTime;
    }

    public void setNotificationSnoozeTime(List<NotificationSnoozeTime> notificationSnoozeTime) {
        this.notificationSnoozeTime = notificationSnoozeTime;
    }

    @Nullable
    public WhitelabelProperties getWhitelabelProperties() {
        return whitelabelProperties;
    }

    public void setWhitelabelProperties(WhitelabelProperties whitelabelProperties) {
        this.whitelabelProperties = whitelabelProperties;
    }

    public static class WhitelabelProperties {
        @SerializedName("is_old_flow")
        @Expose
        boolean isOldFlow;
        @SerializedName("signup_mode")
        @Expose
        int signupMode;
        @SerializedName("conference_link")
        @Expose
        String conferenceLink;
        @SerializedName("is_white_labelled")
        @Expose
        boolean isWhiteLabelled;
        @SerializedName("is_self_chat_enabled")
        @Expose
        boolean isSelfChatEnabled;
        @SerializedName("is_create_workspace_enabled")
        @Expose
        boolean isCreateWorkspaceEnabled;

        public boolean isOldFlow() {
            return isOldFlow;
        }

        public void setOldFlow(boolean oldFlow) {
            isOldFlow = oldFlow;
        }

        public int getSignupMode() {
            return signupMode;
        }

        public void setSignupMode(int signupMode) {
            this.signupMode = signupMode;
        }

        public String getConferenceLink() {
            return conferenceLink;
        }

        public void setConferenceLink(String conferenceLink) {
            this.conferenceLink = conferenceLink;
        }

        public boolean isWhiteLabelled() {
            return isWhiteLabelled;
        }

        public void setWhiteLabelled(boolean whiteLabelled) {
            isWhiteLabelled = whiteLabelled;
        }

        public boolean isSelfChatEnabled() {
            return isSelfChatEnabled;
        }

        public void setSelfChatEnabled(boolean selfChatEnabled) {
            isSelfChatEnabled = selfChatEnabled;
        }

        public boolean isCreateWorkspaceEnabled() {
            return isCreateWorkspaceEnabled;
        }

        public void setCreateWorkspaceEnabled(boolean createWorkspaceEnabled) {
            isCreateWorkspaceEnabled = createWorkspaceEnabled;
        }
    }
}