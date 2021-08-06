package com.skeleton.mvp.data.model.fcCommon;

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WhitelabelDetails(
        @SerializedName("android_app_link")
        @Expose
        val androidAppLink: String,
        @SerializedName("android_critical_version")
        @Expose
        val androidCriticalVersion: Int,
        @SerializedName("android_latest_version")
        @Expose
        val androidLatestVersion: Int,
        @SerializedName("app_name")
        @Expose
        val appName: String,
        @SerializedName("colors")
        @Expose
        val colors: Colors,
        @SerializedName("default_manager_fugu_user_id")
        @Expose
        val defaultManagerFuguUserId: Int,
        @SerializedName("domain")
        @Expose
        val domain: String,
        @SerializedName("domain_id")
        @Expose
        val domainId: Int,
        @SerializedName("fav_icon")
        @Expose
        val favIcon: String,
        @SerializedName("full_domain")
        @Expose
        val fullDomain: String,
        @SerializedName("google_client_id")
        @Expose
        val googleClientId: String,
        @SerializedName("ios_app_link")
        @Expose
        val iosAppLink: String,
        @SerializedName("ios_critical_version")
        @Expose
        val iosCriticalVersion: Int,
        @SerializedName("ios_latest_version")
        @Expose
        val iosLatestVersion: Int,
        @SerializedName("logo")
        @Expose
        val logo: String,
        @SerializedName("properties")
        @Expose
        val properties: Properties,
        @SerializedName("show_meet_tab")
        @Expose
        val showMeetTab: Int,
        @SerializedName("workspace")
        @Expose
        val workspace: String,
        @SerializedName("workspace_id")
        @Expose
        val workspaceId: Int,
        @SerializedName("workspace_name")
        @Expose
        val workspaceName: String
)

data class Colors(
        @SerializedName("app_color_highlight") @Expose val appColorHighlight: String,
        @SerializedName("date_divider_color") @Expose val dateDividerColor: String,
        @SerializedName("header_color") @Expose val headerColor: String,
        @SerializedName("icon_color") @Expose val iconColor: String,
        @SerializedName("loader_color") @Expose val loaderColor: String,
        @SerializedName("scroll_color") @Expose val scrollColor: String,
        @SerializedName("sender_chat_bubble_color") @Expose val senderChatBubbleColor: String,
        @SerializedName("theme_color") @Expose val themeColor: String,
        @SerializedName("theme_color_light") @Expose val themeColorLight: String
)

data class Properties(
        @SerializedName("conference_link") @Expose val conferenceLink: String,
        @SerializedName("is_create_workspace_enabled") @Expose val isCreateWorkspaceEnabled: Boolean,
        @SerializedName("is_old_flow") @Expose val isOldFlow: Boolean,
        @SerializedName("is_self_chat_enabled") @Expose val isSelfChatEnabled: Boolean,
        @SerializedName("is_white_labelled") @Expose val isWhiteLabelled: Boolean,
        @SerializedName("signup_mode") @Expose val signupMode: Int
)