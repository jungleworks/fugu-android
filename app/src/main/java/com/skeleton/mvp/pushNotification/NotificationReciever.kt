package com.skeleton.mvp.pushNotification

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.google.gson.Gson
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.IncomingVideoConferenceActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.model.creategroup.MembersInfo
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.fugudatabase.CommonData.isVibration
import com.skeleton.mvp.model.*
import com.skeleton.mvp.model.channelResponse.ChannelResponse
import com.skeleton.mvp.pushNotification.NotificationReciever.Reply.KEY_REPLY
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.service.ConferenceCallService
import com.skeleton.mvp.service.FuguPushIntentService
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.CUSTOM_LABEL
import com.skeleton.mvp.ui.AppConstants.LABEL
import com.skeleton.mvp.util.FormatStringUtil
import com.skeleton.mvp.util.Utils
import com.skeleton.mvp.utils.FuguLog
import io.paperdb.Paper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("NAME_SHADOWING", "DEPRECATION")
/**
 * Created by rajatdhamija
 * 08/08/18.
 */
class NotificationReciever : FuguAppConstant {
    private val CHANNEL_ONE_ID = "ONE"
    private var replyLabel = "Enter your reply here"
    private val CHANNEL_ONE_NAME = "Default notification"
    private var mFuguMessageSet = LinkedHashMap<String, Message>()
    private var isThread = false
    private var smallIcon = -1
    private var largeIcon: Int = 0
    private var notificationManager: NotificationManager? = null
    private var isSpecialPush = false
    private var TEXT_MESSGAE_SELF: Int = 0
    private var IMAGE_MESSGAE_SELF: Int = 2
    private var FILE_MESSGAE_SELF: Int = 4
    private var MESSAGE_DELETED_SELF = 9
    private var MESSAGE_DELETED_OTHER = 10
    private var VIDEO_MESSGAE_SELF: Int = 11

    //    object PushChannel {
    var pushChannelId: Long? = -2L
    var pushMuid = ""
//    }

    fun setSmallIcon(smallIcon: Int) {
        this.smallIcon = smallIcon
    }

    fun setLargeIcon(largeIcon: Int) {
        this.largeIcon = largeIcon
    }

    fun notificationController(context: Context, data: Map<String, String>) {
        Paper.init(context)
        val priority: Int = getPriority()
        try {
            val messageJson = JSONObject(data[FuguAppConstant.MESSAGE])
            if (messageJson.has(NOTIFICATION_TYPE))
                when (messageJson.getInt(NOTIFICATION_TYPE)) {
                    CLEAR_NOTIFICATION -> clearConversation(context, messageJson)
                    DELETE_NOTIFICATION -> deleteMessage(context, messageJson)
                    GROUP_INFO_NOTIFICATION -> groupInfoChanged(context, messageJson)
                    ADD_MEMBER_NOTIFICATION -> {
                        memberAddedToGroup(context, messageJson)
                        showPush(messageJson, context, messageJson.getString(FuguAppConstant.NOTI_MSG), data, priority)
                    }
                    READ_ALL_NOTIFICATION -> {
                        try {
                            Thread(Runnable {
                                try {
                                    val businessId = messageJson.getInt("business_id")
                                    val channelId = messageJson.getLong("channel_id")
                                    Thread {
                                        kotlin.run {
                                            try {
                                                ChatDatabase.removeNotifications(channelId)
                                                ChatDatabase.setPushCount(channelId, 0)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }.start()
                                    val notificationsList = ChatDatabase.getNotificationsMap()[businessId]
                                    runOnUiThread(Runnable {
                                        if (notificationsList != null) {
                                            for (notification in notificationsList) {
                                                try {
                                                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                                    nm.cancel(notification)
                                                } catch (e: Exception) {

                                                }

                                            }
                                        }
                                    })
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }).start()
                        } catch (e: Exception) {

                        }

                    }
                    REMOVE_MEMBER_NOTIFICATION -> groupMembersRemoved(context, messageJson)
                    UPDATE_COUNTER_NOTIFICATION -> {
                        if (messageJson.has("domain") && messageJson.getString("domain").equals(CommonData.getDomain())) {
                            updateNotificationCounter(context, messageJson)
                        } else {
                            updateNotificationCounter(context, messageJson)
                        }
                    }
                    TEST_NOTIFICATION -> testNotification(data, context, messageJson)
                    NEW_WORKSPACE_NOTIFICATION -> addNewSpace(data, context, messageJson, priority)
                    MESSAGE_NOTIFICATION -> {
                        newMessageNotification(data, context, messageJson, priority)
                        storeMessageToLocal(context, messageJson)
                    }
                    EDIT_MESSAGE_NOTIFICATION -> editNotification(data, context, messageJson, priority)
                    VIDEO_CALL_NOTIFICATION,
                    AUDIO_CALL_NOTIFICATION -> {
                        Handler(Looper.getMainLooper()).postDelayed({

                            newVideoCall(context, messageJson)
                        }, 2000)
                    }
                    DEACTIVATE_USER_NOTIFICATION -> deactivateUserNotification(context, messageJson)
                    VIDEO_CONFERENCE_NOTIFICATION -> {
                        if (!IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus) {
                            val linkArray = messageJson.getString("invite_link").split("/")
                            val intent = Intent(context, IncomingVideoConferenceActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                            if (messageJson.has("caller_text")) {
                                intent.putExtra("caller_text", messageJson.getString("caller_text"))
                            }
                            intent.putExtra("base_url", CommonData.getConferenceUrl())
                            intent.putExtra("room_name", linkArray[linkArray.size - 1])
                            context.startActivity(intent)
                        }
                    }
                    HANGOUTS_CALL_NOTIFICATION -> {
                        if (!IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus) {
                            val intent = Intent(context, ConferenceCallService::class.java)
                            val linkArray = messageJson.getString("invite_link").split("/")
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
                    MISSED_CALL_NOTIFICATION -> {
                        try {
                            missedCallNotification(data, context, messageJson, priority)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun deactivateUserNotification(context: Context, messageJson: JSONObject) {

        if (foregrounded()) {
            val deactivateUserIntent = Intent(DEACTIVATE_USER_INTENT)
            deactivateUserIntent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
            LocalBroadcastManager.getInstance(context).sendBroadcast(deactivateUserIntent)
        } else {
            if (messageJson.has(FuguAppConstant.APP_SECRET_KEY)) {
                val fcCommonResponse = com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey == messageJson.getString(FuguAppConstant.APP_SECRET_KEY)) {
                    if (fcCommonResponse.getData().workspacesInfo.size > 0) {
                        fcCommonResponse.data.workspacesInfo.remove(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()])
                        com.skeleton.mvp.data.db.CommonData.setCommonResponse(fcCommonResponse)
                    } else {
                        FuguConfig.clearFuguData(null)
                        com.skeleton.mvp.data.db.CommonData.clearData()
                    }
                } else {
                    val fcCommonResponse = com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                    if (fcCommonResponse.getData().workspacesInfo.size > 0) {
                        fcCommonResponse.data.workspacesInfo.remove(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()])
                        com.skeleton.mvp.data.db.CommonData.setCommonResponse(fcCommonResponse)
                    } else {
                        FuguConfig.clearFuguData(null)
                        com.skeleton.mvp.data.db.CommonData.clearData()
                    }
                }
            }
        }
    }

    private fun editNotification(data: Map<String, String>, context: Context, messageJson: JSONObject, priority: Int) {
        val editMessageIntent = Intent(EDIT_MESSAGE_INTENT)
        editMessageIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
        editMessageIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
        if (messageJson.has(THREAD_MUID)) {
            editMessageIntent.putExtra(THREAD_MUID, messageJson.getString(THREAD_MUID))
        }
        editMessageIntent.putExtra(MESSAGE, messageJson.getString(MESSAGE).trim())
        editMessageIntent.putExtra(IS_THREAD_MESSAGE, messageJson.getBoolean(IS_THREAD_MESSAGE))
        LocalBroadcastManager.getInstance(context).sendBroadcast(editMessageIntent)
        if (messageJson.has(FuguAppConstant.TAGGED_USERS)) {
            for (i in 0 until messageJson.getJSONArray(FuguAppConstant.TAGGED_USERS).length()) {
                if (messageJson.getJSONArray(FuguAppConstant.TAGGED_USERS).optInt(i)
                        == CommonData.getWorkspaceResponse(messageJson.getString(AppConstants.APP_SECRET_KEY)).userId.toInt()
                        || messageJson.getJSONArray(FuguAppConstant.TAGGED_USERS).optInt(i) == -1) {
                    isSpecialPush = true
                    object : Thread() {
                        override fun run() {
                            super.run()
                            val unreadCountArrayList = CommonData.getNotificationCountList()
                            try {
                                var isThread = false
                                if (messageJson.has(IS_THREAD_MESSAGE) && messageJson.getBoolean(IS_THREAD_MESSAGE)) {
                                    isThread = true
                                }
                                if (messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT) && messageJson.getBoolean(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                                    val unreadCount = UnreadCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID),
                                            messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID), FuguAppConstant.TEXT_MESSAGE,
                                            true, isThread)
                                    unreadCountArrayList.add(unreadCount)
                                    CommonData.setNotificationsCountList(unreadCountArrayList)
                                    val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                                } else if (!messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                                    val unreadCount = UnreadCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID),
                                            messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID),
                                            FuguAppConstant.TEXT_MESSAGE, true, isThread)
                                    unreadCountArrayList.add(unreadCount)
                                    CommonData.setNotificationsCountList(unreadCountArrayList)
                                    val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }.start()

                    break
                }
            }
        } else {
            isSpecialPush = false
        }
        var isSilent = false
        try {
            if (messageJson.has(FuguAppConstant.SHOW_PUSH) && messageJson.getInt(FuguAppConstant.SHOW_PUSH) == 0) {
                isSilent = true
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (!isSilent) {
            showPush(messageJson, context, messageJson.getString(FuguAppConstant.NOTI_MSG), data, priority)
        }
    }


    private fun newVideoCall(context: Context, messageJson: JSONObject) {
        val videoCallIntent = Intent(VIDEO_CALL_INTENT)
        videoCallIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
        videoCallIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
        videoCallIntent.putExtra(VIDEO_CALL_TYPE, messageJson.getString(VIDEO_CALL_TYPE))
        if (messageJson.has(HUNGUP_TYPE)) {
            videoCallIntent.putExtra(HUNGUP_TYPE, messageJson.getString(HUNGUP_TYPE))
        }
        val workspacesInfos = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo as java.util.ArrayList<WorkspacesInfo>
        var userId = -1L
        for (i in workspacesInfos.indices) {

            if (workspacesInfos[i].fuguSecretKey == messageJson.getString(APP_SECRET_KEY)) {
                userId = java.lang.Long.valueOf(workspacesInfos[i].userId)
                break
            }
        }
        videoCallIntent.putExtra(USER_ID, userId)

        Log.e("MUID", messageJson.getString(MESSAGE_UNIQUE_ID))
        LocalBroadcastManager.getInstance(context).sendBroadcast(videoCallIntent)
    }

    private fun newMessageNotification(data: Map<String, String>, context: Context, messageJson: JSONObject, priority: Int) {
        FuguLog.e(FuguAppConstant.MESSAGE, messageJson.toString())
        val mIntent = Intent(FuguAppConstant.NOTIFICATION_INTENT)
        val dataBundle = Bundle()
        for (key in data.keys) {
            dataBundle.putString(key, data[key])
        }

        mIntent.putExtras(dataBundle)
        if (messageJson.has(IS_THREAD_MESSAGE) && !messageJson.getBoolean(IS_THREAD_MESSAGE)) {
            isThread = true
            object : Thread() {
                override fun run() {
                    super.run()
                    val unreadCountArrayList = CommonData.getNotificationCountList()
                    var isChannelPresent = false
                    for (i in unreadCountArrayList.indices) {
                        try {
                            if (unreadCountArrayList[i].channelId!!.compareTo(messageJson.getLong(FuguAppConstant.CHANNEL_ID)) == 0
                                    && !unreadCountArrayList[i].isThreadMessage && !unreadCountArrayList[i].isTagged) {
                                isChannelPresent = true
                                return
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    try {
                        if (!isChannelPresent && !messageJson.has(FuguAppConstant.TAGGED_USERS)) {
                            if (messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT) && messageJson.getBoolean(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                                val unreadCount = UnreadCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID), messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID), FuguAppConstant.TEXT_MESSAGE, false, false)
                                unreadCountArrayList.add(unreadCount)
                                CommonData.setNotificationsCountList(unreadCountArrayList)
                                val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                            } else if (!messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                                val unreadCount = UnreadCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID), messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID), FuguAppConstant.TEXT_MESSAGE, false, false)
                                unreadCountArrayList.add(unreadCount)
                                CommonData.setNotificationsCountList(unreadCountArrayList)
                                val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }.start()


            try {
                Thread {
                    kotlin.run {
                        val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                        val currentConversation: FuguConversation?
                        currentConversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                        if (currentConversation != null) {
                            var new_message: String
                            if (messageJson.has(FuguAppConstant.NEW_MESSAGE)) {
                                new_message = messageJson.getString(FuguAppConstant.NEW_MESSAGE)
                            } else {
                                new_message = ""
                            }

                            currentConversation.message = new_message
                            currentConversation.message = FormatStringUtil.FormatString.getFormattedString(new_message)[1]
                            currentConversation.dateTime = messageJson.getString(FuguAppConstant.DATE_TIME).replace("+00:00", ".000Z")
                            currentConversation.message_type = messageJson.getInt(MESSAGE_TYPE)
                            currentConversation.chat_type = messageJson.getInt(CHAT_TYPE)
                            if (messageJson.getInt(MESSAGE_TYPE) == PUBLIC_NOTE) {
                                currentConversation.unreadCount = currentConversation.unreadCount
                            } else {
                                if (pushChannelId?.compareTo(messageJson.getLong(CHANNEL_ID)) != 0) {
                                    currentConversation.unreadCount = currentConversation.unreadCount.plus(1)
                                }
                            }
                            currentConversation.last_sent_by_id = messageJson.getLong(LAST_SENT_BY_ID)
                            currentConversation.last_sent_by_full_name = messageJson.getString(LAST_SENT_BY_FULL_NAME)
                            currentConversation.messageState = 1
                            currentConversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                            val membersInfoList = currentConversation.membersInfo
                            var isPersonAlreadyPresent = false
                            if (membersInfoList != null && membersInfoList.size > 0) {
                                for (i in 0 until membersInfoList.size) {
                                    if (membersInfoList[i].userId.compareTo(messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID)) == 0) {
                                        isPersonAlreadyPresent = true
                                        val memberInfo = membersInfoList[i]
                                        membersInfoList[i] = membersInfoList[0]
                                        membersInfoList[0] = memberInfo
                                    }
                                }
                                if (!isPersonAlreadyPresent) {
                                    when (membersInfoList.size) {
                                        2 -> {
                                            membersInfoList[1] = membersInfoList[0]
                                            val membersInfo = MembersInfo()
                                            membersInfo.userId = messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID)
                                            membersInfo.fullName = messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME)
                                            membersInfo.userImage = messageJson.getString(USER_THUMBNAIL_IMAGE)
                                            membersInfoList[0] = membersInfo
                                        }
                                        3 -> {
                                            membersInfoList[2] = membersInfoList[1]
                                            membersInfoList[1] = membersInfoList[0]
                                            val membersInfo = MembersInfo()
                                            membersInfo.userId = messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID)
                                            membersInfo.fullName = messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME)
                                            membersInfo.userImage = messageJson.getString(USER_THUMBNAIL_IMAGE)
                                            membersInfoList[0] = membersInfo
                                        }
                                        else -> {
                                        }
                                    }
                                }
                            }
                            Thread {
                                kotlin.run {
                                    conversationMap[messageJson.getLong(CHANNEL_ID)] = currentConversation
                                    ChatDatabase.setConversationMap(conversationMap, messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                                    val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                                    try {
                                        mIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                                }
                            }.start()

                        } else {
                            apiGetuserChannelInfo(getChannelInfoRequestParams(messageJson), messageJson, context)
                        }
                    }
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }



            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
        } else if (messageJson.has(IS_THREAD_MESSAGE) && messageJson.getBoolean(IS_THREAD_MESSAGE)) {
            object : Thread() {
                override fun run() {
                    super.run()
                    val unreadCountArrayList = CommonData.getNotificationCountList()
                    var isChannelPresent = false
                    for (i in unreadCountArrayList.indices) {
                        try {
                            if (unreadCountArrayList[i].channelId!!.compareTo(messageJson.getLong(FuguAppConstant.CHANNEL_ID)) == 0
                                    && unreadCountArrayList[i].muid == messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID)
                                    && !unreadCountArrayList[i].isTagged) {
                                isChannelPresent = true
                                return
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                    try {
                        if (!isChannelPresent && !messageJson.has(FuguAppConstant.TAGGED_USERS)) {
                            if (messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT) && messageJson.getBoolean(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                                val unreadCount = UnreadCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID), messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID), FuguAppConstant.TEXT_MESSAGE, false, true)
                                unreadCountArrayList.add(unreadCount)
                                CommonData.setNotificationsCountList(unreadCountArrayList)
                                val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                            } else if (!messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                                val unreadCount = UnreadCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID), messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID), FuguAppConstant.TEXT_MESSAGE, false, true)
                                unreadCountArrayList.add(unreadCount)
                                CommonData.setNotificationsCountList(unreadCountArrayList)
                                val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }.start()
        } else {
            if (messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.PUBLIC_NOTE) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
            }
        }
        val message = messageJson.getString(FuguAppConstant.NOTI_MSG)
        if (messageJson.has(FuguAppConstant.TAGGED_USERS)) {
            for (i in 0 until messageJson.getJSONArray(FuguAppConstant.TAGGED_USERS).length()) {
                if (messageJson.getJSONArray(FuguAppConstant.TAGGED_USERS).optInt(i)
                        == CommonData.getWorkspaceResponse(messageJson.getString(AppConstants.APP_SECRET_KEY)).userId.toInt()
                        || messageJson.getJSONArray(FuguAppConstant.TAGGED_USERS).optInt(i) == -1) {
                    isSpecialPush = true
                    object : Thread() {
                        override fun run() {
                            super.run()
                            val unreadCountArrayList = CommonData.getNotificationCountList()
                            try {
                                var isThread = false
                                if (messageJson.has(IS_THREAD_MESSAGE) && messageJson.getBoolean(IS_THREAD_MESSAGE)) {
                                    isThread = true
                                }
                                if (messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT) && messageJson.getBoolean(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                                    val unreadCount = UnreadCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID),
                                            messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID), FuguAppConstant.TEXT_MESSAGE,
                                            true, isThread)
                                    unreadCountArrayList.add(unreadCount)
                                    CommonData.setNotificationsCountList(unreadCountArrayList)
                                    val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                                } else if (!messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                                    val unreadCount = UnreadCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID),
                                            messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID),
                                            FuguAppConstant.TEXT_MESSAGE, true, isThread)
                                    unreadCountArrayList.add(unreadCount)
                                    CommonData.setNotificationsCountList(unreadCountArrayList)
                                    val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }.start()

                    break
                }
            }
        } else {
            isSpecialPush = false
        }
        val mngr = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val taskList = mngr.getRunningTasks(10)
        if (taskList[0].topActivity!!.className == "com.skeleton.mvp.activity.MainActivity") {
            pushChannelId = -2L
        }

        if (pushChannelId != null) {
            val mngr = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val taskList = mngr.getRunningTasks(10)

            if (messageJson.has(IS_THREAD_MESSAGE) && messageJson.getBoolean(IS_THREAD_MESSAGE)) {
                if (taskList[0].topActivity!!.className == "com.skeleton.mvp.activity.FuguInnerChatActivity") {
                    if (pushMuid == messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID)) {

                    } else {
                        showPush(messageJson, context, message, data, priority)
                    }
                } else {
                    showPush(messageJson, context, message, data, priority)
                }
            } else if (pushChannelId!!.compareTo(messageJson.getLong(FuguAppConstant.CHANNEL_ID)) != 0) {
                showPush(messageJson, context, message, data, priority)
            } else if (pushChannelId!!.compareTo(messageJson.getLong(FuguAppConstant.CHANNEL_ID)) == 0) {
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager!!.createNotificationChannel(NotificationChannel(CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH))
            }
        }

    }

    private fun addNewSpace(data: Map<String, String>, context: Context, messageJson: JSONObject, priority: Int) {
        val notificationIntent = Intent(context, FuguPushIntentService::class.java)
        val mBundle = Bundle()
        for (key in data.keys) {
            mBundle.putString(key, data[key])
        }
        if (messageJson.has("domain") && messageJson.getString("domain").equals(CommonData.getDomain())) {
            addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.NEW_WORKSPACE_NOTIFICATION, context, messageJson)
        } else {
            addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.GROUP_INFO_NOTIFICATION, context, messageJson)
        }
        val mIntent = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
        notificationIntent.putExtra("data", mBundle)
        val pi = PendingIntent.getService(context, 12345, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationDefaults = Notification.DEFAULT_ALL
        val notification: Notification?
        val mBuilder: NotificationCompat.Builder
        val messagingStyle = NotificationCompat.MessagingStyle("You")

        val senderName = if (messageJson.has(LAST_SENT_BY_FULL_NAME)) {
            messageJson.getString(LAST_SENT_BY_FULL_NAME)
        } else {
            messageJson.getString(TITLE)
        }

        val senderImage = if (messageJson.has("user_thumbnail_image")) {
            messageJson.getString("user_thumbnail_image")
        } else {
            ""
        }
        var targetImageBitmap: Bitmap? = null
        try {
            val url = URL(senderImage)
            targetImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            targetImageBitmap = Utils.getCircleBitmap(targetImageBitmap)
        } catch (e: IOException) {
            targetImageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.profile_placeholder)
        }
        val user = androidx.core.app.Person.Builder()
                .setUri(senderImage)
                .setIcon(IconCompat.createWithBitmap(targetImageBitmap))
                .setName(senderName).build()
        val myDate = messageJson.getString("date_time")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        val date = sdf.parse(myDate)
        val millis = date.time
        messagingStyle.addMessage(NotificationCompat.MessagingStyle.Message(Html.fromHtml(messageJson.getString(NOTI_MSG)),
                millis,
                user))

        mBuilder = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                .setStyle(messagingStyle)
                .setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
                .setContentTitle(messageJson.getString(FuguAppConstant.TITLE))
                .setContentText(messageJson.getString("message"))
                .setContentIntent(pi)
                .setPriority(priority)
        if (messageJson.has("business_name")) {
            mBuilder.setSubText(messageJson.getString("business_name"))
        }
        mBuilder.setAutoCancel(true)
        mBuilder.setDefaults(notificationDefaults)
        mBuilder.setChannelId(CHANNEL_ONE_ID)
        notification = mBuilder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val smallIconViewId = context.resources.getIdentifier("right_icon", "id", "android")
            if (notification != null && smallIconViewId != 0) {
                if (notification.contentIntent != null)
                    if (notification.headsUpContentView != null)
                        notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)


                if (notification.bigContentView != null)
                    notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)
            }
        }
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager!!.notify((Date().time / 1000L % Integer.MAX_VALUE).toInt(), notification)


    }


    private fun missedCallNotification(data: Map<String, String>, context: Context, messageJson: JSONObject, priority: Int) {

        var missedCallNotificationList = ArrayList<MissedCallNotification>()
        missedCallNotificationList = ChatDatabase.getCallNotifications(messageJson.getLong("business_id"))

        missedCallNotificationList.add(MissedCallNotification(messageJson.getString(FULL_NAME), false,
                messageJson.getString(DATE_TIME), false, messageJson.getLong(USER_ID)))

        ChatDatabase.setCallNotification(messageJson.getLong("business_id"), missedCallNotificationList)

        try {
            Thread {
                kotlin.run {
                    var notificationsMap = HashMap<Int, ArrayList<Int>>()
                    var notificationsMapList = ArrayList<Int>()
                    if (messageJson.has("business_id") && ChatDatabase.getCallNotificationsMap() != null) {
                        notificationsMap = ChatDatabase.getCallNotificationsMap()
                        if (notificationsMap[messageJson.getInt("business_id")] != null) {
                            notificationsMapList = notificationsMap[messageJson.getInt("business_id")]!!
                        }
                        if (!notificationsMapList.contains(messageJson.getLong(FuguAppConstant.CHANNEL_ID).toInt())) {
                            notificationsMapList.add(messageJson.getLong(FuguAppConstant.CHANNEL_ID).toInt())
                        }
                        notificationsMap.put(messageJson.getInt("business_id"), notificationsMapList)
                        ChatDatabase.setCallNotificationsMap(notificationsMap)
                    }
                }
            }.start()
        } catch (e: java.lang.Exception) {

        }

        val notificationIntent = Intent(context, FuguPushIntentService::class.java)
        val mBundle = Bundle()
        for (key in data.keys) {
            mBundle.putString(key, data[key])
        }

        notificationIntent.putExtra("data", mBundle)
        val pi = PendingIntent.getService(context, 12345, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationDefaults = Notification.DEFAULT_ALL
        val notification: Notification?
        val mBuilder: NotificationCompat.Builder
        NotificationCompat.InboxStyle()
        var messageDisplayed = messageJson.getString(FULL_NAME)
        var title = ""

        if (messageJson.has(CALL_TYPE) && messageJson.getString(CALL_TYPE).equals("AUDIO")) {
            title = "Missed Audio Call"
        } else {
            title = "Missed Video Call"
        }
        if (missedCallNotificationList.size > 1) {
            title = missedCallNotificationList.size.toString() + " missed calls"
        }
        val userIdMap = HashMap<Long, Long>()
        val userIdList = ArrayList<Long>()
        userIdList.add(missedCallNotificationList[0].userId)
        for (notification in missedCallNotificationList.indices) {
            userIdMap[missedCallNotificationList[notification].userId] = missedCallNotificationList[notification].userId
        }

        if (userIdMap.size > 0) {
            for (notification in userIdMap.keys.indices) {
                if (notification in 1..1) {
                    messageDisplayed = messageDisplayed + ", " + missedCallNotificationList[notification].fullName
                } else if (notification > 0 && notification > 1) {
                    messageDisplayed = messageDisplayed + " and " + (userIdMap.size - 2) + " others"
                    break
                }
            }
        }
        mBuilder = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageDisplayed))
                .setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
                .setContentTitle(title)
                .setContentText(messageDisplayed)
                .setContentIntent(pi)
                .setPriority(priority)
        if (messageJson.has("business_name")) {
            mBuilder.setSubText(messageJson.getString("business_name"))
        }
        mBuilder.setAutoCancel(true)
        mBuilder.setDefaults(notificationDefaults)
        mBuilder.setChannelId(CHANNEL_ONE_ID)
        notification = mBuilder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val smallIconViewId = context.resources.getIdentifier("right_icon", "id", "android")
            if (notification != null && smallIconViewId != 0) {
                if (notification.contentIntent != null)
                    if (notification.headsUpContentView != null)
                        notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)


                if (notification.bigContentView != null)
                    notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)
            }
        }
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager!!.notify("CALL", messageJson.getInt("business_id"), notification)
    }

    private fun memberAddedToGroup(context: Context, messageJson: JSONObject) {
        try {
            val mIntent = Intent(FuguAppConstant.USER_ADDED_INTENT)
            mIntent.putExtra(FuguAppConstant.APP_SECRET_KEY, messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
            mIntent.putExtra(FuguAppConstant.CHANNEL_ID, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
            mIntent.putExtra(FuguAppConstant.MEMBERS_INFO, messageJson.getJSONArray(FuguAppConstant.MEMBERS_INFO).toString())
            mIntent.putExtra(FuguAppConstant.MESSAGE_UNIQUE_ID, messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID))
            mIntent.putExtra(FuguAppConstant.NOTI_MSG, messageJson.getString(FuguAppConstant.MESSAGE))
            mIntent.putExtra(FuguAppConstant.CHAT_TYPE, messageJson.getInt(FuguAppConstant.CHAT_TYPE))
            try {
                if (messageJson.has("added_member_info") && messageJson.getJSONObject("added_member_info").getString("user_unique_key") == com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().userId) {
                    if (messageJson.has("domain") && messageJson.getString("domain").equals(CommonData.getDomain())) {
                        addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.ADD_MEMBER_NOTIFICATION, context, messageJson)
                    } else {
                        addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.GROUP_INFO_NOTIFICATION, context, messageJson)
                    }
                } else if (!messageJson.has("added_member_info")) {
                    if (messageJson.has("domain") && messageJson.getString("domain").equals(CommonData.getDomain())) {
                        addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.ADD_MEMBER_NOTIFICATION, context, messageJson)
                    } else {
                        addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.GROUP_INFO_NOTIFICATION, context, messageJson)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (messageJson.has(CUSTOM_LABEL)) {
                mIntent.putExtra(CUSTOM_LABEL, messageJson.getString(CUSTOM_LABEL))
            }
            if (messageJson.has(LABEL)) {
                mIntent.putExtra(LABEL, messageJson.getString(LABEL))
            }

            if (messageJson.has("added_member_info")) {
                mIntent.putExtra("added_user_id", messageJson.getJSONObject("added_member_info").getLong("user_id"))
                mIntent.putExtra("added_user_name", messageJson.getJSONObject("added_member_info").getString("full_name"))
                mIntent.putExtra("added_user_image", messageJson.getJSONObject("added_member_info").getString("user_image"))
            }

            //            mIntent.putExtra(CHANNEL_THUMBNAIL_URL, messageJson.getString(CHANNEL_THUMBNAIL_URL));
            mIntent.putExtra(FuguAppConstant.DATE_TIME, messageJson.getString(FuguAppConstant.DATE_TIME))
            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }




        try {
            Thread {
                kotlin.run {
                    val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                    val conversation: FuguConversation?
                    conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                    if (conversation != null) {
                        conversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                        conversation.message = messageJson.getString(MESSAGE)
                        conversation.message_type = 5
                        conversation.messageState = 1
                        conversation.dateTime = messageJson.getString(DATE_TIME).replace("+00:00", ".000Z")
                        conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                        ChatDatabase.setConversationMap(conversationMap, messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                        val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    } else {
                        apiGetuserChannelInfo(getChannelInfoRequestParams(messageJson), messageJson, context)
                    }
                }
            }.start()
            try {
                if (messageJson.has("added_member_info") && messageJson.getJSONObject("added_member_info").getString("user_unique_key") == com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().userId) {
                    if (messageJson.has("domain") && messageJson.getString("domain").equals(CommonData.getDomain())) {
                        addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.ADD_MEMBER_NOTIFICATION, context, messageJson)
                    } else {
                        addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.GROUP_INFO_NOTIFICATION, context, messageJson)
                    }
                } else if (!messageJson.has("added_member_info")) {
                    if (messageJson.has("domain") && messageJson.getString("domain").equals(CommonData.getDomain())) {
                        addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.ADD_MEMBER_NOTIFICATION, context, messageJson)
                    } else {
                        addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.GROUP_INFO_NOTIFICATION, context, messageJson)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun apiGetuserChannelInfo(channelInfoRequestParams: com.skeleton.mvp.retrofit.CommonParams?, messageJson: JSONObject, context: Context) {
        com.skeleton.mvp.retrofit.RestClient.getApiInterface().getChannelInfo(
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey,
                1, BuildConfig.VERSION_CODE, channelInfoRequestParams?.map).enqueue(object : ResponseResolver<ChannelResponse>() {
            override fun onSuccess(channelResponse: ChannelResponse?) {
                val conversation = FuguConversation()
                conversation.membersInfo = channelResponse?.data?.membersInfo as java.util.ArrayList<MembersInfo>?
                if (!TextUtils.isEmpty(channelResponse?.data?.customLabel)) {
                    conversation.customLabel = channelResponse?.data?.customLabel
                    conversation.label = channelResponse?.data?.label
                } else {
                    conversation.customLabel = ""
                    conversation.label = channelResponse?.data?.label
                }
                conversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                conversation.message = messageJson.getString(MESSAGE)
                conversation.message_type = messageJson.getInt(MESSAGE_TYPE)
                conversation.messageState = 1
                conversation.chat_type = messageJson.getInt(CHAT_TYPE)
                if (messageJson.has(LAST_SENT_BY_FULL_NAME)) {
                    conversation.last_sent_by_full_name = messageJson.getString(LAST_SENT_BY_FULL_NAME)
                }
                if (messageJson.has(LAST_SENT_BY_ID)) {
                    conversation.last_sent_by_id = messageJson.getLong(LAST_SENT_BY_ID)
                }
                conversation.dateTime = messageJson.getString(DATE_TIME).replace("+00:00", ".000Z")
                conversation.channelId = messageJson.getLong(CHANNEL_ID)

                if (messageJson.getInt(CHAT_TYPE) == 2) {
                    conversation.thumbnailUrl = messageJson.getString("channel_image")
                    conversation.thumbnailUrl = messageJson.getString("channel_image")
                    if (messageJson.getString("channel_image") == "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png") {
                        conversation.thumbnailUrl = ""
                    }
                    conversation.label = messageJson.getString(LABEL)
                } else {
                    conversation.thumbnailUrl = channelResponse?.data?.channelThumbnailUrl
                }


                try {
                    if (messageJson.getInt(MESSAGE_TYPE) == 5) {
                        conversation.unreadCount = 0
                    } else {
                        conversation.unreadCount = channelResponse?.data?.unreadCount!!
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Thread {
                    kotlin.run {
                        val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                        conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                        ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                        val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    }
                }.start()
            }

            override fun onError(error: ApiError?) {
            }

            override fun onFailure(throwable: Throwable?) {
            }

        })
    }

    private fun getChannelInfoRequestParams(messageJson: JSONObject): com.skeleton.mvp.retrofit.CommonParams? {
        return CommonParams.Builder()
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId)
                .add(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                .build()
    }

    private fun updateNotificationCounter(context: Context, messageJson: JSONObject) {

        if (messageJson.has(CHANNEL_ID)) {
            try {
                val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                val conversation = conversationMap[messageJson.getLong(FuguAppConstant.CHANNEL_ID)]
                if (conversation != null) {
                    conversation.unreadCount = 0
                    conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                    ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                    val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.cancel(messageJson.getLong(FuguAppConstant.CHANNEL_ID).toInt())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            if (messageJson.has("unread_notification_count") && messageJson.getInt("unread_notification_count") == 0) {
                CommonData.setNotificationsCountList(ArrayList())
                ChatDatabase.setNotification(messageJson.getLong(FuguAppConstant.CHANNEL_ID), ArrayList())
                ChatDatabase.setPushCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID), 0)
            } else {
                val unreadCountArrayList = CommonData.getNotificationCountList()
                val unreadCountArrayListFinal = ArrayList<UnreadCount>()
                if (messageJson.has(FuguAppConstant.CHANNEL_ID) && !messageJson.has(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                    for (i in unreadCountArrayList.indices) {
                        if (unreadCountArrayList[i].channelId!!.compareTo(messageJson.getLong(FuguAppConstant.CHANNEL_ID)) == 0 &&
                                !unreadCountArrayList[i].isTagged &&
                                !unreadCountArrayList[i].isThreadMessage) {

                        } else {
                            unreadCountArrayListFinal.add(unreadCountArrayList[i])
                        }
                    }
                } else if (messageJson.has(FuguAppConstant.CHANNEL_ID) && messageJson.has(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                    for (i in unreadCountArrayList.indices) {
                        if (unreadCountArrayList[i].channelId!!.compareTo(messageJson.getLong(FuguAppConstant.CHANNEL_ID)) == 0 &&
                                unreadCountArrayList[i].muid == messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID) && !unreadCountArrayList[i].isTagged
                                && unreadCountArrayList[i].isThreadMessage) {

                        } else {
                            unreadCountArrayListFinal.add(unreadCountArrayList[i])
                        }
                    }
                }
                CommonData.setNotificationsCountList(unreadCountArrayListFinal)
            }
            val mIntent = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun testNotification(data: Map<String, String>, context: Context, messageJson: JSONObject) {
        try {
            val priority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NotificationManager.IMPORTANCE_HIGH
            } else {
                Notification.PRIORITY_MAX
            }
            val notificationIntent = Intent(context, FuguPushIntentService::class.java)
            val mBundle = Bundle()
            for (key in data.keys) {
                mBundle.putString(key, data[key])
            }

            val mIntent = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
            notificationIntent.putExtra("data", mBundle)
            val pi = PendingIntent.getService(context, 12345, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationDefaults = Notification.DEFAULT_ALL
            val notification: Notification?
            val mBuilder: NotificationCompat.Builder
            mBuilder = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(messageJson.getString("message")))
                    .setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                    .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
                    .setContentTitle(messageJson.getString(FuguAppConstant.TITLE))
                    .setContentText(messageJson.getString("message"))
                    .setContentIntent(pi)
                    .setPriority(priority)
            if (messageJson.has("business_name")) {
                mBuilder.setSubText(messageJson.getString("business_name"))
            }
            mBuilder.setAutoCancel(true)
            mBuilder.setDefaults(notificationDefaults)
            mBuilder.setChannelId(CHANNEL_ONE_ID)
            notification = mBuilder.build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val smallIconViewId = context.resources.getIdentifier("right_icon", "id", "android")
                if (notification != null && smallIconViewId != 0) {
                    if (notification.contentIntent != null)
                        if (notification.headsUpContentView != null)
                            notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)

                    if (notification.bigContentView != null)
                        notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)
                }
            }
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.notify((Date().time / 1000L % Integer.MAX_VALUE).toInt(), notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun groupMembersRemoved(context: Context, messageJson: JSONObject) {

        try {
            val mIntent = Intent(FuguAppConstant.USER_REMOVED_INTENT)
            mIntent.putExtra(FuguAppConstant.APP_SECRET_KEY, messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
            mIntent.putExtra(FuguAppConstant.CHANNEL_ID, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
            mIntent.putExtra(FuguAppConstant.MEMBERS_INFO, messageJson.getJSONArray(FuguAppConstant.MEMBERS_INFO).toString())
            mIntent.putExtra(FuguAppConstant.MESSAGE_UNIQUE_ID, messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID))
            mIntent.putExtra(FuguAppConstant.DATE_TIME, messageJson.getString(FuguAppConstant.DATE_TIME))
            mIntent.putExtra(FuguAppConstant.NOTI_MSG, messageJson.getString("message"))
            mIntent.putExtra("removed_user_id", messageJson.getLong("removed_user_id"))

            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            Thread {
                kotlin.run {
                    val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                    val conversation: FuguConversation?
                    conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                    if (conversation != null) {
                        val removedUserId = messageJson.getLong("removed_user_id")
                        if (removedUserId.compareTo(com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                                        .data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toLong()) == 0) {
                            Thread {
                                kotlin.run {
                                    conversationMap.remove(messageJson.getLong(CHANNEL_ID))
                                    ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                                    val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                                }
                            }.start()
                        } else {
                            val jsonArray: JSONArray?
                            val membersInfos = java.util.ArrayList<MembersInfo>()
                            jsonArray = JSONArray(messageJson.getJSONArray(FuguAppConstant.MEMBERS_INFO).toString())
                            for (i in 0 until jsonArray.length()) {
                                membersInfos.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(),
                                        MembersInfo::class.java))
                            }
                            conversation.membersInfo = membersInfos
                            conversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                            conversation.message = messageJson.getString(MESSAGE)
                            conversation.message_type = 5
                            conversation.messageState = 1
                            conversation.dateTime = messageJson.getString(DATE_TIME)
                            conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                            ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                            val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                        }
                    } else {

                        val removedUserId = messageJson.getLong("removed_user_id")
                        if (removedUserId.compareTo(com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                                        .data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toLong()) == 0) {
                        } else {
                            apiGetuserChannelInfo(getChannelInfoRequestParams(messageJson), messageJson, context)
                        }
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun groupInfoChanged(context: Context, messageJson: JSONObject) {
        try {
            Thread {
                kotlin.run {
                    val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                    val conversation: FuguConversation?
                    conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]

                    if (messageJson.has("is_deleted_group")) {
                        val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                        mIntent.putExtra("is_deleted_group", true)
                        mIntent.putExtra(CHANNEL_ID, conversation?.channelId)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)

                        val mIntent2 = Intent(FuguAppConstant.PUBLIC_INTENT)
                        mIntent2.putExtra("is_deleted_group", true)
                        mIntent2.putExtra(CHANNEL_ID, conversation?.channelId)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                    } else {
                        if (conversation != null) {
                            if (messageJson.has(CUSTOM_LABEL)) {
                                conversation.label = messageJson.getString(CUSTOM_LABEL)
                                conversation.customLabel = ""
                            }
                            if (messageJson.has(CHANNEL_THUMBNAIL_URL)) {
                                if (messageJson.getString(CHANNEL_THUMBNAIL_URL) != "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png") {
                                    conversation.thumbnailUrl = messageJson.getString(CHANNEL_THUMBNAIL_URL)
                                }
                            }
                            conversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                            conversation.message = messageJson.getString(MESSAGE)
                            conversation.message_type = 5
                            conversation.messageState = 1
                            conversation.dateTime = messageJson.getString(DATE_TIME).replace("+00:00", ".000Z")
                            conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                            ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                            val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                            val mIntent2 = Intent(FuguAppConstant.PUBLIC_INTENT)
                            mIntent2.putExtra(MESSAGE_UNIQUE_ID, conversation.muid)
                            mIntent2.putExtra(CHANNEL_ID, conversation.channelId)
                            mIntent2.putExtra(MESSAGE, conversation.message)
                            mIntent2.putExtra(MESSAGE_TYPE, conversation.message_type)
                            mIntent2.putExtra(DATE_TIME, conversation.dateTime)
                            mIntent2.putExtra(USER_ID, messageJson.getLong(USER_ID))

                            if (messageJson.has(USER_IDS_TO_MAKE_ADMIN)) {
                                val usersToMakeAdmin = ArrayList<Int>()
                                for (i in 0 until messageJson.getJSONArray(FuguAppConstant.USER_IDS_TO_MAKE_ADMIN).length()) {
                                    usersToMakeAdmin.add(messageJson.getJSONArray(FuguAppConstant.USER_IDS_TO_MAKE_ADMIN).optInt(i))
                                }
                                mIntent2.putExtra(USER_IDS_TO_MAKE_ADMIN, usersToMakeAdmin)
                            }
                            if (messageJson.has(USER_IDS_TO_REMOVE_ADMIN)) {
                                val usersToRemoveAdmin = ArrayList<Int>()
                                for (i in 0 until messageJson.getJSONArray(FuguAppConstant.USER_IDS_TO_REMOVE_ADMIN).length()) {
                                    usersToRemoveAdmin.add(messageJson.getJSONArray(FuguAppConstant.USER_IDS_TO_REMOVE_ADMIN).optInt(i))
                                }
                                mIntent2.putExtra(USER_IDS_TO_REMOVE_ADMIN, usersToRemoveAdmin)
                            }
                            if (messageJson.has(CHAT_TYPE)) {
                                mIntent2.putExtra(CHAT_TYPE, messageJson.getInt(CHAT_TYPE))
                                Log.e("Push Chat_type", messageJson.getInt(CHAT_TYPE).toString())
                            }

                            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                        } else {
                            apiGetuserChannelInfo(getChannelInfoRequestParams(messageJson), messageJson, context)
                        }
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val mIntent = Intent(FuguAppConstant.GROUP_INFO_INTENT)
            mIntent.putExtra(FuguAppConstant.APP_SECRET_KEY, messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
            mIntent.putExtra(FuguAppConstant.CHANNEL_ID, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
            if (messageJson.has("custom_label")) {
                mIntent.putExtra(FuguAppConstant.TITLE, messageJson.getString("custom_label"))
            }
            mIntent.putExtra(FuguAppConstant.MESSAGE_UNIQUE_ID, messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID))
            mIntent.putExtra(FuguAppConstant.DATE_TIME, messageJson.getString(FuguAppConstant.DATE_TIME))
            mIntent.putExtra(FuguAppConstant.NOTI_MSG, messageJson.getString("message"))
            if (messageJson.has(FuguAppConstant.CHANNEL_THUMBNAIL_URL)) {
                mIntent.putExtra(FuguAppConstant.CHANNEL_THUMBNAIL_URL, messageJson.getString(FuguAppConstant.CHANNEL_THUMBNAIL_URL))
            }
            if (messageJson.has("user_unique_key") && messageJson.getString("user_unique_key") != com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().userId) {
                if (messageJson.has("domain") && messageJson.getString("domain").equals(CommonData.getDomain())) {
                    addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.GROUP_INFO_NOTIFICATION, context, messageJson)
                } else {
                    addCountToLoacal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), FuguAppConstant.GROUP_INFO_NOTIFICATION, context, messageJson)
                }
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Clear Conversation Broadcast
     */
    private fun clearConversation(context: Context, messageJson: JSONObject) {
        Thread {
            kotlin.run {
                val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                val conversation: FuguConversation?
                conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                if (conversation != null) {
                    conversationMap.remove(messageJson.getLong(CHANNEL_ID))
                    ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                    val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                } else {

                }
            }
        }.start()
        var pos = 0
        try {
            ChatDatabase.setMessageList(ArrayList(), messageJson.getLong(FuguAppConstant.CHANNEL_ID))
            if (CommonData.getConversationList(messageJson.getString(FuguAppConstant.APP_SECRET_KEY)) != null) {
                val conversationmap = CommonData.getConversationList(messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                val conversations = ArrayList(CommonData.getConversationList(messageJson.getString(FuguAppConstant.APP_SECRET_KEY)).values)

                for (i in conversations.indices) {
                    if (conversations[i].channelId!!.compareTo(messageJson.getLong(FuguAppConstant.CHANNEL_ID)) == 0) {
                        conversations.removeAt(i)
                        conversationmap.remove(messageJson.getLong(FuguAppConstant.CHANNEL_ID))
                        CommonData.setConversationList(messageJson.getString(FuguAppConstant.APP_SECRET_KEY), conversationmap)
                        pos = i
                        break
                    }
                }
                val mIntent = Intent(CLEAR_INTENT)
                mIntent.putExtra(FuguAppConstant.APP_SECRET_KEY, messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                mIntent.putExtra(FuguAppConstant.POSITION, pos)
                mIntent.putExtra(FuguAppConstant.CHANNEL_ID, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Delete Message Broadcast
     */
    private fun deleteMessage(context: Context, messageJson: JSONObject) {
        Thread {
            kotlin.run {
                val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                val conversation: FuguConversation?
                conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                if (messageJson.has(MESSAGE_UNIQUE_ID) && messageJson.getString(MESSAGE_UNIQUE_ID).equals(conversation?.muid)) {
                    if (conversation != null) {
                        if (conversation.last_sent_by_id.toLong().compareTo(com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                                        .data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toLong()) == 0) {
                            conversation.message = "You deleted this message"
                        } else {
                            conversation.message = "This message was deleted"
                        }
                        conversation.message_type = 5
                        conversation.messageState = 0
                        conversation.dateTime = messageJson.getString(DATE_TIME).replace("+00:00", ".000Z")
                        conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                        ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                        val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    } else {

                    }
                }
            }
        }.start()

        try {
            if (messageJson.has(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                val pos = 0
                if (CommonData.getConversationList(messageJson.getString(FuguAppConstant.APP_SECRET_KEY)) != null) {
                    val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                    val conversation: FuguConversation?
                    conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                    if (conversation?.last_sent_by_id!!.compareTo(java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)) == 0) {
                        conversation.message = "You deleted this message "
                    } else {
                        conversation.message = "This message was deleted "
                    }
                    conversation.messageState = 0
                    conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                    ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                }
                val messageList = ChatDatabase.getMessageList(messageJson.getLong(FuguAppConstant.CHANNEL_ID))
                var text: String
                var rowtype: Int
                for (i in messageList.indices) {
                    if (messageList[i].uuid == messageJson.getString(MESSAGE_UNIQUE_ID)) {
                        text = if (messageList[i].rowType == TEXT_MESSGAE_SELF ||
                                messageList[i].rowType == IMAGE_MESSGAE_SELF ||
                                messageList[i].rowType == FILE_MESSGAE_SELF ||
                                messageList[i].rowType == VIDEO_MESSGAE_SELF ||
                                messageList[i].rowType == MESSAGE_DELETED_SELF) {
                            "You deleted this message "
                        } else {
                            "This message was deleted "
                        }
                        rowtype = if (messageList[i].rowType == TEXT_MESSGAE_SELF ||
                                messageList[i].rowType == IMAGE_MESSGAE_SELF ||
                                messageList[i].rowType == FILE_MESSGAE_SELF ||
                                messageList[i].rowType == VIDEO_MESSGAE_SELF ||
                                messageList[i].rowType == MESSAGE_DELETED_SELF) {
                            MESSAGE_DELETED_SELF
                        } else {
                            MESSAGE_DELETED_OTHER
                        }
                        messageList[i].message = text
                        messageList[i].rowType = rowtype
                    }
                }
                ChatDatabase.setMessageList(messageList, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
                val mIntent = Intent(FuguAppConstant.DELETE_INTENT)
                mIntent.putExtra(FuguAppConstant.APP_SECRET_KEY, messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                mIntent.putExtra(FuguAppConstant.CHANNEL_ID, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
                mIntent.putExtra(FuguAppConstant.MESSAGE_UNIQUE_ID, messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID))
                mIntent.putExtra(FuguAppConstant.POSITION, pos)
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
            } else {
                val mIntent = Intent(FuguAppConstant.THREAD_DELETE_INTENT)
                mIntent.putExtra(FuguAppConstant.APP_SECRET_KEY, messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                mIntent.putExtra(FuguAppConstant.CHANNEL_ID, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
                mIntent.putExtra("thread_muid", messageJson.getString("thread_muid"))
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Set notification priority as per API level of device
     */
    private fun getPriority(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_MAX
        } else {
            Notification.PRIORITY_MAX
        }
    }

    /**
     * Check if the notification is from fugu or not( i.e push_source should be FUGU)
     */
    fun isFuguNotification(data: Map<String, String>): Boolean {
        return data.containsKey(FuguAppConstant.PUSH_SOURCE)
                && data[FuguAppConstant.PUSH_SOURCE].equals(FuguAppConstant.FUGU, ignoreCase = true)
    }

    object Reply {
        internal var KEY_REPLY = "key_reply"
        fun getReplyMessage(intent: Intent): CharSequence? {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            return remoteInput?.getCharSequence(KEY_REPLY)
        }
    }


    private fun addCountToLoacal(channelId: Long, muid: String, notificationType: Int, context: Context, messageJson: JSONObject) {
        object : Thread() {
            override fun run() {
                super.run()
                try {
                    if (messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT) && messageJson.getBoolean(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                        val unreadCountArrayList = CommonData.getNotificationCountList()
                        val unreadCount = UnreadCount(channelId, muid, notificationType, false, false)
                        unreadCountArrayList.add(unreadCount)
                        CommonData.setNotificationsCountList(unreadCountArrayList)
                        val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                    } else if (!messageJson.has(FuguAppConstant.UPDATE_NOTIFICATION_COUNT)) {
                        val unreadCountArrayList = CommonData.getNotificationCountList()
                        val unreadCount = UnreadCount(channelId, muid, notificationType, false, false)
                        unreadCountArrayList.add(unreadCount)
                        CommonData.setNotificationsCountList(unreadCountArrayList)
                        val mIntent2 = Intent(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }.start()
    }

    private fun showPush(messageJson: JSONObject, context: Context, message: String, data: Map<String, String>, priority: Int) {
        var isSilent = false
        try {
            if (messageJson.has(FuguAppConstant.SHOW_PUSH) && messageJson.getInt(FuguAppConstant.SHOW_PUSH) == 0) {
                isSilent = true
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        try {
            var notificationList = ArrayList<PushNotification>()
            notificationList = ChatDatabase.getNotifications(messageJson.getLong(FuguAppConstant.CHANNEL_ID))
            if (!isSpecialPush) {
                val senderName = if (messageJson.has(LAST_SENT_BY_FULL_NAME)) {
                    messageJson.getString(LAST_SENT_BY_FULL_NAME)
                } else {
                    messageJson.getString(TITLE)
                }
                val senderImage = if (messageJson.has("user_thumbnail_image")) {
                    messageJson.getString("user_thumbnail_image")
                } else {
                    ""
                }

                notificationList.add(PushNotification(message, true,
                        messageJson.getString("date_time"),
                        isSilent, senderName,
                        senderImage))
                ChatDatabase.setNotification(messageJson.getLong(FuguAppConstant.CHANNEL_ID), notificationList)
            }
            ChatDatabase.setPushCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID), ChatDatabase.getPushCount(messageJson.getLong(CHANNEL_ID)) + 1)

            Paper.init(context)
            val notificationIntent = Intent(context, FuguPushIntentService::class.java)
            notificationIntent.putExtra("muid", messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID))
            CommonData.setPushMuid(messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID))
            notificationIntent.putExtra("appsecretkey", messageJson.getString(AppConstants.APP_SECRET_KEY))
            notificationIntent.putExtra("channelId", messageJson.getLong(FuguAppConstant.CHANNEL_ID))
            notificationIntent.putExtra("unreadCount", ChatDatabase.getNotifications(messageJson.getLong(FuguAppConstant.CHANNEL_ID)).size)

            if (messageJson.has(IS_THREAD_MESSAGE) && messageJson.getBoolean(IS_THREAD_MESSAGE)) {
                notificationIntent.putExtra(IS_THREAD_MESSAGE, messageJson.getBoolean(IS_THREAD_MESSAGE))
            }
            if (messageJson.has(FuguAppConstant.APP_SECRET_KEY)) {
                notificationIntent.putExtra("userId", CommonData.getWorkspaceResponse(messageJson.getString(FuguAppConstant.APP_SECRET_KEY)).userId)
                notificationIntent.putExtra(FuguAppConstant.FULL_NAME, CommonData.getWorkspaceResponse(messageJson.getString(FuguAppConstant.APP_SECRET_KEY)).fullName)
            }
            if (messageJson.has(FuguAppConstant.CHAT_TYPE)) {
                notificationIntent.putExtra(FuguAppConstant.CHAT_TYPE, messageJson.getInt(FuguAppConstant.CHAT_TYPE))
            } else {
                notificationIntent.putExtra(FuguAppConstant.CHAT_TYPE, 2)
            }

            val mBundle = Bundle()
            for (key in data.keys) {
                mBundle.putString(key, data[key])
            }
            notificationIntent.putExtra("data", mBundle)
            var timeStamp = 0
            if (isSpecialPush) {
                timeStamp = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
                notificationIntent.putExtra("timeStamp", timeStamp)
            }
            val pi = PendingIntent.getService(context, messageJson.getLong(FuguAppConstant.CHANNEL_ID).toInt(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            var notificationDefaults = Notification.DEFAULT_ALL
            var mBuilder: NotificationCompat.Builder
            mBuilder = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
            Log.e("list", notificationList.toString())
            val notificationListToBeDisplayed = ArrayList<PushNotification>()
            var image = ""
            try {
                try {
                    image = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userImage
                } catch (e: IOException) {
                    image = ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            var user: androidx.core.app.Person? = null
            var targetImageBitmap: Bitmap? = null
            try {
                val url = URL(image)
                targetImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                targetImageBitmap = Utils.getCircleBitmap(targetImageBitmap)
            } catch (e: IOException) {
                targetImageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.profile_placeholder)
            }
            user = androidx.core.app.Person.Builder()
                    .setIcon(IconCompat.createWithBitmap(targetImageBitmap))
                    .setName("You").build()
            val messagingStyle = if (messageJson.has("business_name")) {
                NotificationCompat.MessagingStyle(user).setConversationTitle(messageJson.getString("title") + "(" + messageJson.getString("business_name") + ")")
            } else {
                NotificationCompat.MessagingStyle(user).setConversationTitle(messageJson.getString("title"))
            }
            messagingStyle.isGroupConversation = true
            if (!isSpecialPush) {
                var maxLines = 10

                for (i in notificationList.indices) {
                    if (!notificationList[i].isSilent) {
                        notificationListToBeDisplayed.add(notificationList[i])
                    }
                }

                for (i in notificationListToBeDisplayed.indices) {
                    var user: androidx.core.app.Person? = null
                    var targetImageBitmap: Bitmap? = null
                    try {
                        if (!TextUtils.isEmpty(notificationListToBeDisplayed[i].senderImage)) {

                            val bmOptions = BitmapFactory.Options()
                            val bitmap = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/IMG_20180621_110203.jpg", bmOptions)

//                            val url = URL(notificationListToBeDisplayed[i].senderImage)
//                            targetImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                            targetImageBitmap = Utils.getCircleBitmap(bitmap)
                        } else {
                            targetImageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.user)
                        }
                    } catch (e: IOException) {
                        targetImageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.profile_placeholder)
                    }


                    user = androidx.core.app.Person.Builder()
                            .setUri(notificationListToBeDisplayed[i].senderImage)
                            .setIcon(IconCompat.createWithBitmap(targetImageBitmap))
                            .setName(notificationListToBeDisplayed[i].senderName).build()
                    val myDate = notificationListToBeDisplayed[i].dateTime
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                    val date = sdf.parse(myDate)
                    val millis = date.time
                    messagingStyle.addMessage(NotificationCompat.MessagingStyle.Message(Html.fromHtml(notificationListToBeDisplayed[i].message),
                            millis,
                            user))
                }
                mBuilder.setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                        .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                        .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
                        .setGroup(FuguAppConstant.APP_NAME_SHORT )
                        .setGroupSummary(true)
                if (notificationListToBeDisplayed.size > 1) {
                    mBuilder.setContentTitle(messageJson.getString(FuguAppConstant.TITLE) + " (" + notificationListToBeDisplayed.size + " messages)")
                } else {
                    mBuilder.setContentTitle(messageJson.getString(FuguAppConstant.TITLE))
                }
                mBuilder.setStyle(messagingStyle)

                val remoteInput = RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build()
                val replyAction = NotificationCompat.Action.Builder(
                        android.R.drawable.sym_action_chat, "REPLY", pi)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build()

                if (messageJson.has("is_thread_message")) {
                    if (messageJson.has("user_type")) {
                        if (messageJson.getInt("user_type") == 4) {
                            mBuilder.setContentText(Html.fromHtml(message))
                                    .setContentIntent(pi)
                                    .setDefaults(notificationDefaults).priority = priority
                            mBuilder.setAutoCancel(true)
                        } else {
                            mBuilder.setContentText(Html.fromHtml(message))
                                    .setContentIntent(pi)
                                    .setDefaults(notificationDefaults)
                                    .setPriority(priority)
                                    .addAction(replyAction)
                            mBuilder.setAutoCancel(true)
                        }
                    } else {
                        mBuilder.setContentText(Html.fromHtml(message))
                                .setContentIntent(pi)
                                .setDefaults(notificationDefaults)
                                .setPriority(priority)
                                .addAction(replyAction)
                        mBuilder.setAutoCancel(true)
                    }
                } else {
                    mBuilder.setContentText(Html.fromHtml(message))
                            .setContentIntent(pi)
                            .setDefaults(notificationDefaults).priority = priority
                    mBuilder.setAutoCancel(true)
                }
            } else {
                val remoteInput = RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build()
                val replyAction = NotificationCompat.Action.Builder(
                        android.R.drawable.sym_action_chat, "REPLY", pi)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build()
                var image = ""
                try {
                    try {
                        image = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userImage
                    } catch (e: IOException) {
                        image = ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                var user: androidx.core.app.Person? = null
                var targetImageBitmap: Bitmap? = null
                try {
                    val url = URL(image)
                    targetImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    targetImageBitmap = Utils.getCircleBitmap(targetImageBitmap)
                } catch (e: IOException) {
                    targetImageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.profile_placeholder)
                }
                user = androidx.core.app.Person.Builder()
                        .setIcon(IconCompat.createWithBitmap(targetImageBitmap))
                        .setName("You").build()
                val messagingStyle = NotificationCompat.MessagingStyle(user)


                val senderName = if (messageJson.has(LAST_SENT_BY_FULL_NAME)) {
                    messageJson.getString(LAST_SENT_BY_FULL_NAME)
                } else {
                    messageJson.getString(TITLE)
                }

                val senderImage = if (messageJson.has("user_thumbnail_image")) {
                    messageJson.getString("user_thumbnail_image")
                } else {
                    ""
                }

                var targetImageBitmap2: Bitmap? = null
                try {
                    val url = URL(senderImage)
                    targetImageBitmap2 = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    targetImageBitmap2 = Utils.getCircleBitmap(targetImageBitmap2)
                } catch (e: IOException) {
                    targetImageBitmap2 = BitmapFactory.decodeResource(context.resources, R.drawable.profile_placeholder)
                }

                user = androidx.core.app.Person.Builder()
                        .setIcon(IconCompat.createWithBitmap(targetImageBitmap2))
                        .setName(senderName).build()
                val myDate = messageJson.getString("date_time")
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                val date = sdf.parse(myDate)
                val millis = date.time
                messagingStyle.addMessage(NotificationCompat.MessagingStyle.Message(messageJson.getString(NOTI_MSG),
                        millis,
                        user))

                mBuilder = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                        .setStyle(messagingStyle)
                        .setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                        .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                        .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
                        .setContentTitle(messageJson.getString(FuguAppConstant.TITLE))
                        .setContentText(message)
                        .setContentIntent(pi)
                        .addAction(replyAction)
                        .setPriority(priority)
                if (messageJson.has("business_name")) {
                    mBuilder.setSubText(messageJson.getString("business_name") + "(" + messageJson.getString(TITLE) + ")")
                }
                mBuilder.setAutoCancel(true)
            }

            notificationDefaults = if (isVibration()!!) {
                Notification.DEFAULT_ALL
            } else {
                Notification.DEFAULT_SOUND
            }
            mBuilder.setDefaults(notificationDefaults)
            mBuilder.setChannelId(CHANNEL_ONE_ID)
            var notification: Notification? = null
            if (messageJson.has(FuguAppConstant.SHOW_PUSH) && messageJson.getInt(FuguAppConstant.SHOW_PUSH) == 0) {
                Log.e("SilentPush", "SilentPush")
            } else {
                notification = mBuilder.build()
            }
            val messageList: ArrayList<Message> = ChatDatabase.getMessageList(messageJson.getLong(FuguAppConstant.CHANNEL_ID))
            mFuguMessageSet = ChatDatabase.getMessageMap(messageJson.getLong(FuguAppConstant.CHANNEL_ID))
            for (i in 0 until messageList.size - 1) {
                if (messageList[i].rowType == 8) {
                    messageList.removeAt(i)
                    mFuguMessageSet.remove("Unread")
                }
            }
            var messageToBeAdded: Message? = null

            if (messageJson.has(FuguAppConstant.MESSAGE_UNIQUE_ID) && messageJson.has(FuguAppConstant.MESSAGE_TYPE)
                    && !TextUtils.isEmpty(messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID))
                    && messageJson.has(NEW_MESSAGE)) {
                if (messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.TEXT_MESSAGE) {
                    messageToBeAdded = Message(messageList.size.toLong(),
                            messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME),
                            messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID),
                            messageJson.getString(FuguAppConstant.NEW_MESSAGE),
                            messageJson.getString(FuguAppConstant.DATE_TIME),
                            1,
                            FuguAppConstant.MESSAGE_READ,
                            messageList.size,
                            "",
                            "",
                            messageJson.getInt(FuguAppConstant.MESSAGE_TYPE),
                            true,
                            messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID),
                            messageJson.getInt(FuguAppConstant.CHAT_TYPE),
                            "",
                            "")

                    messageToBeAdded.alteredMessage = FormatStringUtil.FormatString.getFormattedString(messageJson.getString(FuguAppConstant.NEW_MESSAGE))[0]
                    messageToBeAdded.formattedMessage = FormatStringUtil.FormatString.getFormattedString(messageJson.getString(FuguAppConstant.NEW_MESSAGE))[1]
                }

                if (messageToBeAdded != null) {
                    if (messageList.size - notificationList.size > 0) {
                        if (notificationList.size != 0) {
                            val unreadMessage = Message()
                            unreadMessage.count = notificationList.size
                            unreadMessage.rowType = 8
                            unreadMessage.messageIndex = messageList.size - 1
                            unreadMessage.muid = UUID.randomUUID().toString()
                            if (messageList.size > 0) {
                                if (messageJson.has(IS_THREAD_MESSAGE) && !messageJson.getBoolean(IS_THREAD_MESSAGE)) {
//                                    messageList.add(messageList.size - notificationList.size, unreadMessage)
                                }
                            }
                            val finalNotificationList = notificationList

                            if (messageJson.has(IS_THREAD_MESSAGE) && !messageJson.getBoolean(IS_THREAD_MESSAGE)) {

                                object : Thread() {
                                    override fun run() {
                                        super.run()
                                        try {
                                            val conversationMap = CommonData.getConversationList(messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                                            val conversations = ArrayList(conversationMap.values)
                                            for (i in conversations.indices) {
                                                try {
                                                    if (conversations[i].channelId!!.compareTo(messageJson.getLong(FuguAppConstant.CHANNEL_ID)) == 0) {
                                                        conversations[i].unreadCount = finalNotificationList.size
                                                        conversations[i].message = messageJson.getString(FuguAppConstant.NEW_MESSAGE)
                                                        conversations[i].dateTime = messageJson.getString(FuguAppConstant.DATE_TIME)
                                                        conversations[i].last_sent_by_id = messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID)
                                                        conversationMap[conversations[i].channelId] = conversations[i]
                                                        CommonData.setConversationList(messageJson.getString(FuguAppConstant.APP_SECRET_KEY), conversationMap)
                                                        break
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }

                                            }
                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }

                                    }
                                }.start()
                            }
                        }
                        mFuguMessageSet[UUID.randomUUID().toString()] = messageList[messageList.size - 1]
                    }
                }
            }
            if (messageJson.has("is_thread_message") && !messageJson.getBoolean("is_thread_message")) {
                ChatDatabase.setMessageList(messageList, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
                ChatDatabase.setMessageMap(mFuguMessageSet, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val smallIconViewId = context.resources.getIdentifier("right_icon", "id", "android")
                if (notification != null && smallIconViewId != 0) {
                    if (notification.contentIntent != null)
                        if (notification.headsUpContentView != null)
                            notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)

                    if (notification.bigContentView != null)
                        notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)
                }
            }

            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!isSpecialPush) {
                if (notification != null) {
                    notificationManager?.notify(messageJson.getLong(FuguAppConstant.CHANNEL_ID).toInt(), notification)
                }
                try {
                    Thread {
                        kotlin.run {
                            var notificationsMap = HashMap<Int, ArrayList<Int>>()
                            var notificationsMapList = ArrayList<Int>()
                            if (messageJson.has("business_id") && ChatDatabase.getNotificationsMap() != null) {
                                notificationsMap = ChatDatabase.getNotificationsMap()
                                if (notificationsMap[messageJson.getInt("business_id")] != null) {
                                    notificationsMapList = notificationsMap[messageJson.getInt("business_id")]!!
                                }
                                if (!notificationsMapList.contains(messageJson.getLong(FuguAppConstant.CHANNEL_ID).toInt())) {
                                    notificationsMapList.add(messageJson.getLong(FuguAppConstant.CHANNEL_ID).toInt())
                                }
                                notificationsMap.put(messageJson.getInt("business_id"), notificationsMapList)
                                ChatDatabase.setNotificationsMap(notificationsMap)
                            }
                        }
                    }.start()
                } catch (e: java.lang.Exception) {

                }
            } else {
                notificationManager?.notify(timeStamp, notification)
                try {
                    Thread {
                        kotlin.run {
                            var notificationsMap = HashMap<Int, ArrayList<Int>>()
                            var notificationsMapList = ArrayList<Int>()
                            if (messageJson.has("business_id") && ChatDatabase.getNotificationsMap() != null) {
                                notificationsMap = ChatDatabase.getNotificationsMap()
                                if (notificationsMap[messageJson.getInt("business_id")] != null) {
                                    notificationsMapList = notificationsMap[messageJson.getInt("business_id")]!!
                                }
                                notificationsMapList.add(timeStamp)

                                notificationsMap.put(messageJson.getInt("business_id"), notificationsMapList)
                                ChatDatabase.setNotificationsMap(notificationsMap)
                            }
                        }
                    }.start()
                } catch (e: java.lang.Exception) {

                }

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun storeMessageToLocal(context: Context, messageJson: JSONObject) {
        val messageList: ArrayList<Message> = ChatDatabase.getMessageList(messageJson.getLong(FuguAppConstant.CHANNEL_ID))
        mFuguMessageSet = ChatDatabase.getMessageMap(messageJson.getLong(FuguAppConstant.CHANNEL_ID))
        for (i in 0 until messageList.size - 1) {
            if (messageList[i].rowType == 8) {
                messageList.removeAt(i)
                mFuguMessageSet.remove("Unread")
            }
        }
        var messageToBeAdded: Message? = null
        if (messageJson.has(FuguAppConstant.MESSAGE_UNIQUE_ID) && messageJson.has(FuguAppConstant.MESSAGE_TYPE)
                && !TextUtils.isEmpty(messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID))
                && messageJson.has(MESSAGE)) {
            if (messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.TEXT_MESSAGE) {
                messageToBeAdded = Message(messageList.size.toLong(),
                        messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME),
                        messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID),
                        messageJson.getString(FuguAppConstant.MESSAGE),
                        messageJson.getString(FuguAppConstant.DATE_TIME),
                        1,
                        FuguAppConstant.MESSAGE_READ,
                        messageList.size,
                        "",
                        "",
                        messageJson.getInt(FuguAppConstant.MESSAGE_TYPE),
                        true,
                        messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID),
                        messageJson.getInt(FuguAppConstant.CHAT_TYPE),
                        "",
                        "")

                messageToBeAdded.alteredMessage = FormatStringUtil.FormatString.getFormattedString(messageJson.getString(FuguAppConstant.MESSAGE))[0]
                messageToBeAdded.formattedMessage = FormatStringUtil.FormatString.getFormattedString(messageJson.getString(FuguAppConstant.MESSAGE))[1]
            } else if (messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.IMAGE_MESSAGE) {
                var messageText = ""
                if (messageJson.getBoolean("hasCaption")) {
                    messageText = messageJson.getString(FuguAppConstant.MESSAGE)
                }
                messageToBeAdded = Message(messageList.size.toLong(),
                        messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME),
                        messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID),
                        messageText,
                        messageJson.getString(FuguAppConstant.DATE_TIME),
                        3,
                        FuguAppConstant.MESSAGE_READ,
                        messageList.size,
                        messageJson.getString("image_url"),
                        messageJson.getString("thumbnail_url"),
                        messageJson.getInt(FuguAppConstant.MESSAGE_TYPE),
                        true,
                        messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID),
                        messageJson.getInt(FuguAppConstant.CHAT_TYPE),
                        "",
                        "")

                var extension = messageJson.getString("image_url").split(".")[messageJson.getString("image_url").split(".").size - 1]
                messageToBeAdded.fileExtension = extension
                if (!extension.equals("gif")) {
                    messageToBeAdded.fileSize = messageJson.getString("file_size")
                    messageToBeAdded.image_url_100x100 = messageJson.getString("image_url_100x100")
                }
                messageToBeAdded.imageWidth = messageJson.getInt("image_width")
                messageToBeAdded.imageHeight = messageJson.getInt("image_height")
                messageToBeAdded.id = messageJson.getLong("message_id")
            } else if (messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.VIDEO_MESSAGE) {
                messageToBeAdded = Message(messageList.size.toLong(),
                        messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME),
                        messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID),
                        "",
                        messageJson.getString(FuguAppConstant.DATE_TIME),
                        12,
                        FuguAppConstant.MESSAGE_READ,
                        messageList.size,
                        "",
                        messageJson.getString("thumbnail_url"),
                        messageJson.getInt(FuguAppConstant.MESSAGE_TYPE),
                        true,
                        messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID),
                        messageJson.getInt(FuguAppConstant.CHAT_TYPE),
                        "",
                        messageJson.getString("url"))
                messageToBeAdded.fileSize = messageJson.getString("file_size")
                messageToBeAdded.fileName = messageJson.getString("file_name")
                messageToBeAdded.downloadStatus = DOWNLOAD_FAILED
                var extension = messageJson.getString("url").split(".")[messageJson.getString("url").split(".").size - 1]
                messageToBeAdded.fileExtension = extension

            } else if (messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.FILE_MESSAGE) {
                messageToBeAdded = Message(messageList.size.toLong(),
                        messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME),
                        messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID),
                        "",
                        messageJson.getString(FuguAppConstant.DATE_TIME),
                        5,
                        FuguAppConstant.MESSAGE_READ,
                        messageList.size,
                        "",
                        messageJson.getString("thumbnail_url"),
                        messageJson.getInt(FuguAppConstant.MESSAGE_TYPE),
                        true,
                        messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID),
                        messageJson.getInt(FuguAppConstant.CHAT_TYPE),
                        "",
                        messageJson.getString("url"))
                messageToBeAdded.fileSize = messageJson.getString("file_size")
                messageToBeAdded.fileName = messageJson.getString("file_name")
                messageToBeAdded.downloadStatus = DOWNLOAD_FAILED
                var extension = messageJson.getString("url").split(".")[messageJson.getString("url").split(".").size - 1]
                messageToBeAdded.fileExtension = extension

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
                            mFuguMessageSet[messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID)] = messageList[messageList.size - 1]
                        }
                    } else {
                        for (messageIndex in 0..messageList.size - 1) {
                            if (messageList.get(messageIndex).muid.equals(messageToBeAdded.muid)) {
                                val message = messageList.get(messageIndex)
                                message.isThreadMessage = true
                                message.threadMessage = true
                                message.threadMessageCount = message.threadMessageCount + 1
                                messageList.set(messageIndex, message)
                                break
                            }
                        }
                    }
                }

                val messageIntent = Intent(MESSAGE_NOTIFICATION_INTENT)
                messageIntent.putExtra(MESSAGE, messageToBeAdded)
                messageIntent.putExtra(CHANNEL_ID, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
                messageIntent.putExtra("is_thread_message", messageJson.has("is_thread_message") && messageJson.getBoolean("is_thread_message"))
                messageIntent.putExtra(MESSAGE_UNIQUE_ID, messageToBeAdded.muid)
                LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent)

                ChatDatabase.setMessageList(messageList, messageJson.getLong(FuguAppConstant.CHANNEL_ID))
                ChatDatabase.setMessageMap(mFuguMessageSet, messageJson.getLong(FuguAppConstant.CHANNEL_ID))

            }
        }
    }

    fun foregrounded(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE
    }
}