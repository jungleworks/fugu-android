package com.skeleton.mvp.pushNotification

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.activity.IncomingJitsiCallActivity
import com.skeleton.mvp.activity.IncomingVideoConferenceActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.UnreadCount
import com.skeleton.mvp.receiver.HungUpBroadcast
import com.skeleton.mvp.service.ConferenceCallService
import com.skeleton.mvp.service.NotificationSockets
import com.skeleton.mvp.service.OngoingCallService
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.util.FormatStringUtil
import com.skeleton.mvp.utils.UniqueIMEIID
import io.paperdb.Paper
import org.json.JSONObject

class PushReceiver {
    object PushChannel {
        var pushChannelId: Long? = -2L
        var pushMuid = ""
        val CHANNEL_ONE_ID = "ONE"
        val CHANNEL_ONE_NAME = "Default notification"
        var replyLabel = "Enter your reply here"
        val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"
        var SUMMARY_NOTIFICATION_ID: Int = 12332

        @JvmField
        var isEmailVerificationScreen = false
    }

    private var smallIcon = -1
    private var pushReceiver: PushReceiver? = null

    fun getInstance(): PushReceiver {
        return if (pushReceiver == null) {
            pushReceiver = PushReceiver()
            pushReceiver as PushReceiver
        } else {
            pushReceiver as PushReceiver
        }
    }

    fun pushRedirection(context: Context, data: Map<String, String>, showPush: Boolean) {
        Paper.init(context)

        try {
            var messageJson: JSONObject
            try {
                messageJson = JSONObject(data[MESSAGE])
            } catch (e: java.lang.Exception) {
                messageJson = JSONObject(data)
            }
            if (messageJson.has(NOTIFICATION_TYPE)) {
                when (messageJson.getInt(NOTIFICATION_TYPE)) {
                    MESSAGE_NOTIFICATION -> {
                        if (showPush) {
                            MessagingNotification().showNotification(data, context, messageJson, getPriority(), smallIcon)
                            storeMessageToLocal(context, messageJson)
                        }
                    }
                    CLEAR_NOTIFICATION -> {
                        ClearConversationNotification().clearConversation(context, messageJson)
                    }
                    DELETE_NOTIFICATION -> {
                        DeleteMessageNotification().deleteMessage(context, messageJson)
                    }
                    NEW_WORKSPACE_NOTIFICATION -> {
                        NewSpaceNotification().addedToNewSpace(data, context, messageJson, getPriority(), smallIcon)
                    }
                    READ_ALL_NOTIFICATION -> {
                        MarkReadNotification().markNotificationsRead(context, messageJson)
                    }
                    REMOVE_MEMBER_NOTIFICATION -> {
                        RemoveMemberNotification().removeMember(context, messageJson)
                    }
                    GROUP_INFO_NOTIFICATION -> {
                        GroupInformationNotification().groupInfoChanged(context, messageJson)
                    }
                    ADD_MEMBER_NOTIFICATION -> {
                        AddMemberNotification().memberAddedToGroup(context, messageJson)
                        MultipleMessageNotification().publishNotification(messageJson, context, messageJson.getString(NOTI_MSG), data, getPriority(), smallIcon, false)
                        //Show Notification
                    }
                    UPDATE_COUNTER_NOTIFICATION -> {
                        updateNotificationCounter(context, messageJson)
                    }
                    TEST_NOTIFICATION -> {
                        NormalNotification().publishNotification(data, context, messageJson, smallIcon)
                    }
                    VIDEO_CALL_NOTIFICATION,
                    AUDIO_CALL_NOTIFICATION -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            CallNotification().newVideoCall(context, messageJson)
                        }, 2000)
                    }
                    EDIT_MESSAGE_NOTIFICATION -> {
                        EditMessageNotification().editNotification(data, context, messageJson, getPriority(), smallIcon)
                    }
                    DEACTIVATE_USER_NOTIFICATION -> {
                        DeactivateUserNotification().deactivateUserNotification(context, messageJson)
                    }
                    VIDEO_CONFERENCE_NOTIFICATION -> {
                        initVideoConference(context, messageJson)
                    }
                    HANGOUTS_CALL_NOTIFICATION -> {
                        initVideoConference(context, messageJson)
                    }
                    MISSED_CALL_NOTIFICATION -> {
                        MissCallNotification().missedCallNotification(data, context, messageJson, getPriority(), smallIcon)
                    }
                    NOTIFICATION_SYNC_SEVICE -> {
                        NotificationSockets.init(context, true)
                    }
                    VIDEO_CONFERENCE_HUNGUP_NOTIFICATION -> {
                        val messageIntent = Intent(VIDEO_CONFERENCE_HUNGUP_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent)
                    }
                    INCOMING_CALL_NOTIFICATION -> {
                        incomingCallNotification(context, messageJson)
                    }
                    TASK_ASSIGNED_NOTIFICATION -> {
                        TaskAssignedNotification().taskAssignedNotification(data, context, messageJson, getPriority(), smallIcon)
                    }
                    MEET_SCHEDULED_NOTIFICATION -> {
                        ScheduledMeetNotification().scheduledMeetNotification(data, context, messageJson, getPriority(), smallIcon)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun pushRedirectionNew(context: Context, data: Map<String, String>) {
        Paper.init(context)

        try {
            val messageJson = try {
                JSONObject(data[MESSAGE])
            } catch (e: java.lang.Exception) {
                JSONObject(data)
            }
            if (messageJson.has(PUSH_TYPE)) {
                when (messageJson.getInt(PUSH_TYPE)) {
                    MESSAGE_NOTIFICATION -> {
                        MessagingNotification().showNotification(data, context, messageJson, getPriority(), smallIcon)
                        storeMessageToLocal(context, messageJson)
                    }
                    CLEAR_NOTIFICATION -> {
                        ClearConversationNotification().clearConversation(context, messageJson)
                    }
                    DELETE_NOTIFICATION -> {
                        DeleteMessageNotification().deleteMessage(context, messageJson)
                    }
                    NEW_WORKSPACE_NOTIFICATION -> {
                        NewSpaceNotification().addedToNewSpace(data, context, messageJson, getPriority(), smallIcon)
                    }
                    READ_ALL_NOTIFICATION -> {
                        MarkReadNotification().markNotificationsRead(context, messageJson)
                    }
                    REMOVE_MEMBER_NOTIFICATION -> {
                        RemoveMemberNotification().removeMember(context, messageJson)
                    }
                    GROUP_INFO_NOTIFICATION -> {
                        GroupInformationNotification().groupInfoChanged(context, messageJson)
                    }
                    ADD_MEMBER_NOTIFICATION -> {
                        AddMemberNotification().memberAddedToGroup(context, messageJson)
                        MultipleMessageNotification().publishNotification(messageJson, context, messageJson.getString(NOTI_MSG), data, getPriority(), smallIcon, false)
                        //Show Notification
                    }
                    UPDATE_COUNTER_NOTIFICATION -> {
                        updateNotificationCounter(context, messageJson)
                    }
                    TEST_NOTIFICATION -> {
                        NormalNotification().publishNotification(
                                data, context,
                                messageJson, smallIcon
                        )
                    }
                    VIDEO_CALL_NOTIFICATION,
                    AUDIO_CALL_NOTIFICATION -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            CallNotification().newVideoCall(context, messageJson)
                        }, 2000)
                    }
                    EDIT_MESSAGE_NOTIFICATION -> {
                        EditMessageNotification().editNotification(
                                data, context,
                                messageJson, getPriority(), smallIcon
                        )
                    }
                    DEACTIVATE_USER_NOTIFICATION -> {
                        DeactivateUserNotification().deactivateUserNotification(
                                context,
                                messageJson
                        )
                    }
                    VIDEO_CONFERENCE_NOTIFICATION -> {
//                        initVideoConference(context, messageJson)
                    }
                    HANGOUTS_CALL_NOTIFICATION -> {
//                        initVideoConference(context, messageJson)
                    }
                    MISSED_CALL_NOTIFICATION -> {
                        MissCallNotification().missedCallNotification(
                                data, context,
                                messageJson, getPriority(), smallIcon
                        )
                    }
                    NOTIFICATION_SYNC_SEVICE -> {
                        NotificationSockets.init(context, true)
                    }
                    VIDEO_CONFERENCE_HUNGUP_NOTIFICATION -> {
                        val messageIntent = Intent(VIDEO_CONFERENCE_HUNGUP_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun storeMessageToLocal(context: Context, messageJson: JSONObject) {
        val messageList: ArrayList<Message> = ChatDatabase.getMessageList(messageJson.getLong(CHANNEL_ID))
        val mFuguMessageSet = ChatDatabase.getMessageMap(messageJson.getLong(CHANNEL_ID))
        for (i in 0 until messageList.size - 1) {
            if (messageList[i].rowType == 8) {
                messageList.removeAt(i)
                mFuguMessageSet.remove("Unread")
            }
        }
        var messageToBeAdded: Message? = null
        if (messageJson.has(MESSAGE_UNIQUE_ID) && messageJson.has(FuguAppConstant.MESSAGE_TYPE)
                && !TextUtils.isEmpty(messageJson.getString(MESSAGE_UNIQUE_ID))
                && messageJson.has(MESSAGE)
        ) {
            when {
                messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.TEXT_MESSAGE -> {
                    messageToBeAdded = Message(
                            messageList.size.toLong(),
                            messageJson.getString(LAST_SENT_BY_FULL_NAME),
                            messageJson.getLong(LAST_SENT_BY_ID),
                            messageJson.getString(MESSAGE),
                            messageJson.getString(DATE_TIME),
                            1,
                            MESSAGE_READ,
                            messageList.size,
                            "",
                            "",
                            messageJson.getInt(MESSAGE_TYPE),
                            true,
                            messageJson.getString(MESSAGE_UNIQUE_ID),
                            messageJson.getInt(CHAT_TYPE),
                            "",
                            ""
                    )
                    messageToBeAdded.alteredMessage = FormatStringUtil.FormatString.getFormattedString(messageJson.getString(FuguAppConstant.MESSAGE))[0]
                    messageToBeAdded.formattedMessage = FormatStringUtil.FormatString.getFormattedString(messageJson.getString(FuguAppConstant.MESSAGE))[1]
                }
                messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.IMAGE_MESSAGE -> {
                    var messageText = ""
                    if (messageJson.getBoolean("hasCaption")) {
                        messageText = messageJson.getString(FuguAppConstant.MESSAGE)
                    }
                    messageToBeAdded = Message(
                            messageList.size.toLong(),
                            messageJson.getString(LAST_SENT_BY_FULL_NAME),
                            messageJson.getLong(LAST_SENT_BY_ID),
                            messageText,
                            messageJson.getString(DATE_TIME),
                            3,
                            MESSAGE_READ,
                            messageList.size,
                            messageJson.getString("image_url"),
                            messageJson.getString("thumbnail_url"),
                            messageJson.getInt(MESSAGE_TYPE),
                            true,
                            messageJson.getString(MESSAGE_UNIQUE_ID),
                            messageJson.getInt(CHAT_TYPE),
                            "",
                            ""
                    )
                    val extension = messageJson.getString("image_url").split(".")[messageJson.getString("image_url").split(".").size - 1]
                    messageToBeAdded.fileExtension = extension
                    if (!extension.equals("gif")) {
                        messageToBeAdded.fileSize = messageJson.getString("file_size")
                        if (messageJson.has("image_url_100x100")) {
                            messageToBeAdded.image_url_100x100 = messageJson.getString("image_url_100x100")
                        }
                    }
                    messageToBeAdded.imageWidth = messageJson.getInt("image_width")
                    messageToBeAdded.imageHeight = messageJson.getInt("image_height")
                    messageToBeAdded.id = messageJson.getLong("message_id")
                }
                messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.VIDEO_MESSAGE -> {
                    messageToBeAdded = Message(
                            messageList.size.toLong(),
                            messageJson.getString(LAST_SENT_BY_FULL_NAME),
                            messageJson.getLong(LAST_SENT_BY_ID),
                            "",
                            messageJson.getString(DATE_TIME),
                            12,
                            MESSAGE_READ,
                            messageList.size,
                            "",
                            messageJson.getString("thumbnail_url"),
                            messageJson.getInt(MESSAGE_TYPE),
                            true,
                            messageJson.getString(MESSAGE_UNIQUE_ID),
                            messageJson.getInt(CHAT_TYPE),
                            "",
                            messageJson.getString("url")
                    )
                    messageToBeAdded.fileSize = messageJson.getString("file_size")
                    messageToBeAdded.fileName = messageJson.getString("file_name")
                    messageToBeAdded.downloadStatus = DOWNLOAD_FAILED
                    val extension = messageJson.getString("url").split(".")[messageJson.getString("url").split(".").size - 1]
                    messageToBeAdded.fileExtension = extension

                }
                messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.FILE_MESSAGE -> {
                    messageToBeAdded = Message(
                            messageList.size.toLong(),
                            messageJson.getString(LAST_SENT_BY_FULL_NAME),
                            messageJson.getLong(LAST_SENT_BY_ID),
                            "",
                            messageJson.getString(DATE_TIME),
                            5,
                            MESSAGE_READ,
                            messageList.size,
                            "",
                            messageJson.getString("thumbnail_url"),
                            messageJson.getInt(MESSAGE_TYPE),
                            true,
                            messageJson.getString(MESSAGE_UNIQUE_ID),
                            messageJson.getInt(CHAT_TYPE),
                            "",
                            messageJson.getString("url")
                    )
                    messageToBeAdded.fileSize = messageJson.getString("file_size")
                    messageToBeAdded.fileName = messageJson.getString("file_name")
                    messageToBeAdded.downloadStatus = DOWNLOAD_FAILED
                    val extension = messageJson.getString("url").split(".")[messageJson.getString("url").split(".").size - 1]
                    messageToBeAdded.fileExtension = extension
                }
            }
            if (messageToBeAdded != null) {
                if (messageList.size > 0) {
                    if (messageJson.has("is_thread_message") && !messageJson.getBoolean("is_thread_message")) {
                        var isMessageToBeAdded = false
                        for (message in messageList.reversed()) {
                            if (message.muid.equals(messageToBeAdded.muid)) {
                                isMessageToBeAdded = true
                                break
                            }
                        }
                        if (!isMessageToBeAdded) {
                            messageList.add(messageToBeAdded)
                            mFuguMessageSet[messageJson.getString(MESSAGE_UNIQUE_ID)] =
                                    messageList[messageList.size - 1]
                        }
                    } else {
                        for (messageIndex in 0 until messageList.size) {
                            if (messageList.get(messageIndex).muid.equals(messageToBeAdded.muid)) {
                                val message = messageList.get(messageIndex)
                                message.isThreadMessage = true
                                message.threadMessage = true
                                message.threadMessageCount = message.threadMessageCount + 1
                                messageList[messageIndex] = message
                                break
                            }
                        }
                    }
                }
                val messageIntent = Intent(MESSAGE_NOTIFICATION_INTENT)
                messageIntent.putExtra(MESSAGE, messageToBeAdded)
                messageIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                messageIntent.putExtra("is_thread_message", messageJson.has("is_thread_message") && messageJson.getBoolean("is_thread_message"))
                messageIntent.putExtra(MESSAGE_UNIQUE_ID, messageToBeAdded.muid)
                LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent)
                ChatDatabase.setMessageList(messageList, messageJson.getLong(CHANNEL_ID))
                ChatDatabase.setMessageMap(mFuguMessageSet, messageJson.getLong(CHANNEL_ID))
            }
        }
    }

    private fun initVideoConference(context: Context, messageJson: JSONObject) {
        if (!IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus) {
            val intent = Intent(context, ConferenceCallService::class.java)
            val linkArray = messageJson.getString("invite_link").split("invite_link=")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            if (messageJson.has("caller_text")) {
                intent.putExtra("caller_text", messageJson.getString("caller_text"))
            }
            intent.putExtra(BASE_URL, CommonData.getConferenceUrl())
            intent.putExtra(ROOM_NAME, linkArray[linkArray.size - 1])
            intent.putExtra(INVITE_LINK, messageJson.getString("invite_link"))
            intent.putExtra(USER_THUMBNAIL_IMAGE, messageJson.getString("channel_image"))
            intent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
            intent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
            ContextCompat.startForegroundService(context, intent)
        }
    }

    fun incomingCallNotification(context: Context, messageJson: JSONObject) {
        if (messageJson.has("user_unique_key")
                && messageJson.getString("user_unique_key") != com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().userId) {
            val linkArray = messageJson.getString("invite_link").replace("#config.startWithVideoMuted=true", "").split("/")
            if (messageJson.getString(VIDEO_CALL_TYPE) == JitsiCallType.HUNGUP_CONFERENCE.toString()
                    || messageJson.getString(VIDEO_CALL_TYPE) == JitsiCallType.REJECT_CONFERENCE.toString()) {
                val hungupIntent = Intent(context, HungUpBroadcast::class.java)
                hungupIntent.putExtra("action", "rejectCall")
                hungupIntent.putExtra(DEVICE_PAYLOAD, getDeviceDetails(context).toString())
                hungupIntent.putExtra(INVITE_LINK, messageJson.getString(INVITE_LINK))
                hungupIntent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
                hungupIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                context.sendBroadcast(hungupIntent)
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    if (!OngoingCallService.NotificationServiceState.isConferenceServiceRunning
                            && messageJson.getString(VIDEO_CALL_TYPE) == JitsiCallType.OFFER_CONFERENCE.toString()
                            && messageJson.getString(INVITE_LINK) == OngoingCallService.NotificationServiceState.inviteLink) {
                        val startIntent = Intent(context, OngoingCallService::class.java)
                        startIntent.action = "com.officechat.notification.start"
                        startIntent.putExtra(INCOMING_VIDEO_CONFERENCE, true)
                        startIntent.putExtra(BASE_URL, CommonData.getConferenceUrl())
                        startIntent.putExtra(ROOM_NAME, linkArray[linkArray.size - 1])
                        startIntent.putExtra(CALL_TYPE, messageJson.getString(CALL_TYPE))
                        startIntent.putExtra(FULL_NAME, messageJson.getString(FULL_NAME))
                        startIntent.putExtra(USER_THUMBNAIL_IMAGE, messageJson.getString(USER_THUMBNAIL_IMAGE))
                        startIntent.putExtra(INVITE_LINK, messageJson.getString(INVITE_LINK))
                        startIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                        startIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
                        OngoingCallService.NotificationServiceState.appSecretKey = messageJson.getString(APP_SECRET_KEY)
                        ContextCompat.startForegroundService(context, startIntent)
                    }
                } else {
                    val mngr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    val taskList = mngr.getRunningTasks(10)
                    if (taskList[0].topActivity!!.className != "com.skeleton.mvp.activity.IncomingJitsiCallActivity"
                            && !taskList[0].topActivity!!.className.contains("GrantPermissionsActivity")
                            && messageJson.getString(VIDEO_CALL_TYPE).equals(JitsiCallType.OFFER_CONFERENCE.toString())
                            && messageJson.getString(INVITE_LINK).equals(OngoingCallService.NotificationServiceState.inviteLink)) {
                        val startIntent = Intent(context, IncomingJitsiCallActivity::class.java)
                        startIntent.action = "com.officechat.notification.start"
                        startIntent.putExtra(INCOMING_VIDEO_CONFERENCE, true)
                        startIntent.putExtra(BASE_URL, CommonData.getConferenceUrl())
                        startIntent.putExtra(ROOM_NAME, linkArray[linkArray.size - 1])
                        startIntent.putExtra(CALL_TYPE, messageJson.getString(CALL_TYPE))
                        startIntent.putExtra(FULL_NAME, messageJson.getString(FULL_NAME))
                        startIntent.putExtra(USER_THUMBNAIL_IMAGE, messageJson.getString(USER_THUMBNAIL_IMAGE))
                        startIntent.putExtra(INVITE_LINK, messageJson.getString(INVITE_LINK))
                        startIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                        startIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        context.startActivity(startIntent)
                    }
                }
            }
        }
    }

    private fun updateNotificationCounter(context: Context, messageJson: JSONObject) {
        if (messageJson.has("domain") && messageJson.getString("domain").equals(CommonData.getDomain())) {
            updateCounter(context, messageJson)
        } else {
            updateCounter(context, messageJson)
        }
    }

    private fun getDeviceDetails(context: Context): JSONObject {
        val devicePayload = JSONObject()
        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(context))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(context))
        return devicePayload
    }

    private fun updateCounter(context: Context, messageJson: JSONObject) {
        if (messageJson.has(CHANNEL_ID)) {
            try {
                val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                val conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                if (conversation != null) {
                    conversation.unreadCount = 0
                    conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                    ChatDatabase.setConversationMap(
                            conversationMap,
                            messageJson.getString(APP_SECRET_KEY)
                    )
                    val mIntent = Intent(CHANNEL_INTENT)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.cancel(messageJson.getLong(CHANNEL_ID).toInt())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            if (messageJson.has("unread_notification_count") && messageJson.getInt("unread_notification_count") == 0) {
                CommonData.setNotificationsCountList(ArrayList())
                val channelId = if(messageJson.has(CHANNEL_ID)) messageJson.getLong(CHANNEL_ID) else null
                ChatDatabase.setNotification(channelId, ArrayList())
                ChatDatabase.setPushCount(channelId, 0)
            } else {
                val unreadCountArrayList = CommonData.getNotificationCountList()
                val unreadCountArrayListFinal = ArrayList<UnreadCount>()
                if (messageJson.has(CHANNEL_ID) && !messageJson.has(MESSAGE_UNIQUE_ID)) {
                    for (i in unreadCountArrayList.indices) {
                        if (unreadCountArrayList[i].channelId!!.compareTo(messageJson.getLong(CHANNEL_ID)) != 0 || unreadCountArrayList[i].isTagged || unreadCountArrayList[i].isThreadMessage) {
                            unreadCountArrayListFinal.add(unreadCountArrayList[i])
                        }
                    }
                } else if (messageJson.has(CHANNEL_ID) && messageJson.has(MESSAGE_UNIQUE_ID)) {
                    for (i in unreadCountArrayList.indices) {
                        if (unreadCountArrayList[i].channelId!!.compareTo(messageJson.getLong(CHANNEL_ID)) != 0 || unreadCountArrayList[i].muid != messageJson.getString(MESSAGE_UNIQUE_ID) || unreadCountArrayList[i].isTagged || !unreadCountArrayList[i].isThreadMessage) {
                            unreadCountArrayListFinal.add(unreadCountArrayList[i])
                        }
                    }
                }
                CommonData.setNotificationsCountList(unreadCountArrayListFinal)
            }
            val mIntent = Intent(NOTIFICATION_COUNTER_INTENT)
            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun isFuguNotification(data: Map<String, String>): Boolean {
        return data.containsKey(PUSH_SOURCE)
                && data[PUSH_SOURCE].equals(FUGU, ignoreCase = true)
    }

    fun setSmallIcon(smallIcon: Int) {
        this.smallIcon = smallIcon
    }

    private fun getPriority(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_MAX
        } else {
            Notification.PRIORITY_MAX
        }
    }
}