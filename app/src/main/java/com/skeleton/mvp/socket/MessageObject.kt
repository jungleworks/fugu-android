package com.skeleton.mvp.socket

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MessageObject {
    @SerializedName("notification_type")
    @Expose
    var notificationType: Int? = null
    @SerializedName("server_push")
    @Expose
    var serverPush: Boolean? = null
    @SerializedName("channel_id")
    @Expose
    var channelId: Int? = null
    @SerializedName("muid")
    @Expose
    var muid: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("full_name")
    @Expose
    var fullName: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: Int? = null
    @SerializedName("user_unique_key")
    @Expose
    var userUniqueKey: String? = null
    @SerializedName("user_type")
    @Expose
    var userType: Int? = null
    @SerializedName("last_sent_by_id")
    @Expose
    var lastSentById: Int? = null
    @SerializedName("last_sent_by_full_name")
    @Expose
    var lastSentByFullName: String? = null
    @SerializedName("last_sent_by_user_type")
    @Expose
    var lastSentByUserType: Int? = null
    @SerializedName("label")
    @Expose
    var label: String? = null
    @SerializedName("channel_image")
    @Expose
    var channelImage: String? = null
    @SerializedName("channel_thumbnail_url")
    @Expose
    var channelThumbnailUrl: String? = null
    @SerializedName("chat_status")
    @Expose
    var chatStatus: Int? = null
    @SerializedName("bot_channel_name")
    @Expose
    var botChannelName: String? = null
    @SerializedName("date_time")
    @Expose
    var dateTime: String? = null
    @SerializedName("message_type")
    @Expose
    var messageType: Int? = null
    @SerializedName("chat_type")
    @Expose
    var chatType: Int? = null
    @SerializedName("isTyping")
    @Expose
    var isTyping: Int? = null
    @SerializedName("type")
    @Expose
    var type: Int? = null
    @SerializedName("user_thumbnail_image")
    @Expose
    var userThumbnailImage: String? = null
    @SerializedName("is_thread_message")
    @Expose
    var isThreadMessage: Boolean? = null
    @SerializedName("unread_notification_count")
    @Expose
    var unreadNotificationCount: Int? = null
    @SerializedName("app_secret_key")
    @Expose
    var appSecretKey: String? = null
    @SerializedName("update_notification_count")
    @Expose
    var updateNotificationCount: Boolean? = null
    @SerializedName("domain")
    @Expose
    var domain: String? = null
    @SerializedName("is_web")
    @Expose
    var isWeb: Boolean? = null
}