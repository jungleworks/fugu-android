package com.skeleton.mvp.pushNotification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.PushNotification
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.CHANNEL_ONE_ID
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.GROUP_KEY_WORK_EMAIL
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.SUMMARY_NOTIFICATION_ID
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.replyLabel
import com.skeleton.mvp.service.FuguPushIntentService
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.BUSINESS_NAME
import com.skeleton.mvp.util.FormatStringUtil
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MultipleMessageNotification {
    var notificationManager: NotificationManager? = null
    fun publishNotification(
            messageJson: JSONObject, context: Context,
            message: String, data: Map<String, String>, priority: Int,
            smallIcon: Int, isSpecialPush: Boolean
    ) {
        createNotificationChannel(context)
        var isSilent = false
        if (messageJson.has(SHOW_PUSH) && messageJson.getInt(SHOW_PUSH) == 0) {
            isSilent = true
        }
        var notificationList = ArrayList<PushNotification>()
        notificationList = ChatDatabase.getNotifications(messageJson.getLong(CHANNEL_ID))
        if (!isSpecialPush) {

            val senderName = if (messageJson.has(LAST_SENT_BY_FULL_NAME)) {
                messageJson.getString(LAST_SENT_BY_FULL_NAME)
            } else {
                messageJson.getString(TITLE)
            }
            val senderImage = if (messageJson.has(USER_THUMBNAIL_IMAGE)) {
                messageJson.getString(USER_THUMBNAIL_IMAGE)
            } else {
                ""
            }
            notificationList.add(
                    PushNotification(
                            message, true,
                            messageJson.getString(DATE_TIME),
                            isSilent, senderName,
                            senderImage
                    )
            )
            ChatDatabase.setNotification(messageJson.getLong(CHANNEL_ID), notificationList)
        }
        ChatDatabase.setPushCount(messageJson.getLong(FuguAppConstant.CHANNEL_ID), ChatDatabase.getPushCount(messageJson.getLong(CHANNEL_ID)) + 1)

        val notificationIntent = Intent(context, FuguPushIntentService::class.java)
        notificationIntent.putExtra("muid", messageJson.getString(MESSAGE_UNIQUE_ID))
        CommonData.setPushMuid(messageJson.getString(MESSAGE_UNIQUE_ID))
        notificationIntent.putExtra("appsecretkey", messageJson.getString(AppConstants.APP_SECRET_KEY))
        notificationIntent.putExtra("channelId", messageJson.getLong(CHANNEL_ID))
        notificationIntent.putExtra("unreadCount", ChatDatabase.getNotifications(messageJson.getLong(CHANNEL_ID)).size)

        if (messageJson.has(IS_THREAD_MESSAGE) && messageJson.getBoolean(IS_THREAD_MESSAGE)) {
            notificationIntent.putExtra(IS_THREAD_MESSAGE, messageJson.getBoolean(IS_THREAD_MESSAGE))
        }
        if (messageJson.has(APP_SECRET_KEY)) {
            notificationIntent.putExtra("userId", CommonData.getWorkspaceResponse(messageJson.getString(APP_SECRET_KEY)).userId)
            notificationIntent.putExtra(FULL_NAME, CommonData.getWorkspaceResponse(messageJson.getString(APP_SECRET_KEY)).fullName)
        }
        if (messageJson.has(CHAT_TYPE)) {
            notificationIntent.putExtra(CHAT_TYPE, messageJson.getInt(CHAT_TYPE))
        } else {
            notificationIntent.putExtra(CHAT_TYPE, 2)
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

        val pi = PendingIntent.getService(
                context,
                messageJson.getLong(CHANNEL_ID).toInt(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        var notificationDefaults = Notification.DEFAULT_ALL
        var mBuilder: NotificationCompat.Builder
        mBuilder = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
        mBuilder.priority = priority
        val notificationListToBeDisplayed = ArrayList<PushNotification>()
        var image = try {
            com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userImage
        } catch (e: IOException) {
            ""
        }

        var user: androidx.core.app.Person? = null
        val targetImageBitmap: Bitmap? = NotificationImageManager().getImageBitmap(image)
        user = if (targetImageBitmap == null) {
            androidx.core.app.Person.Builder()
                    .setIcon(null)
                    .setName("You").build()
        } else {
            androidx.core.app.Person.Builder()
                    .setIcon(IconCompat.createWithBitmap(targetImageBitmap))
                    .setName("You").build()
        }
        val messagingStyle =
                if (messageJson.has(BUSINESS_NAME) && messageJson.has(TITLE) && messageJson.getInt(CHAT_TYPE) != 2 && messageJson.getInt(CHAT_TYPE) != 7) {
                    try {
                        if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.size > 1) {
                            NotificationCompat.MessagingStyle(user).setConversationTitle(
                                    messageJson.getString(TITLE) + "(" + messageJson.getString(BUSINESS_NAME) + ")"
                            )
                        } else {
                            NotificationCompat.MessagingStyle(user).setConversationTitle(
                                    messageJson.getString(TITLE)
                            )
                        }
                    } catch (e: java.lang.Exception) {
                        NotificationCompat.MessagingStyle(user).setConversationTitle(
                                messageJson.getString(TITLE) + "(" + messageJson.getString(BUSINESS_NAME) + ")"
                        )
                    }

                } else if (messageJson.has(BUSINESS_NAME)) {
                    try {
                        if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.size > 1) {
                            NotificationCompat.MessagingStyle(user)
                                    .setConversationTitle(messageJson.getString(BUSINESS_NAME))
                        } else {
                            NotificationCompat.MessagingStyle(user)
                        }
                    } catch (e: java.lang.Exception) {
                        NotificationCompat.MessagingStyle(user)
                                .setConversationTitle(messageJson.getString(BUSINESS_NAME))
                    }
                } else {
                    NotificationCompat.MessagingStyle(user)
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
                targetImageBitmap = try {
                    if (!TextUtils.isEmpty(notificationListToBeDisplayed[i].senderImage)) {
                        NotificationImageManager().getImageBitmap(notificationListToBeDisplayed[i].senderImage)
                    } else {
                        BitmapFactory.decodeResource(context.resources, R.drawable.user)
                    }
                } catch (e: IOException) {
                    BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.profile_placeholder
                    )
                }


                user = try {
                    androidx.core.app.Person.Builder()
                            .setUri(notificationListToBeDisplayed[i].senderImage)
                            .setIcon(IconCompat.createWithBitmap(targetImageBitmap))
                            .setName(notificationListToBeDisplayed[i].senderName).build()
                } catch (e: Exception) {
                    androidx.core.app.Person.Builder()
                            .setUri(notificationListToBeDisplayed[i].senderImage)
                            .setIcon(null)
                            .setName(notificationListToBeDisplayed[i].senderName).build()
                }
                val myDate = notificationListToBeDisplayed[i].dateTime
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                val date = sdf.parse(myDate)
                val millis = date.time
                messagingStyle.addMessage(NotificationCompat.MessagingStyle.Message(notificationListToBeDisplayed[i].message, millis, user))
            }
            mBuilder.setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                    .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_fugu))
                    .setGroup(PushReceiver.PushChannel.GROUP_KEY_WORK_EMAIL)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)

            if (notificationListToBeDisplayed.size > 1) {
                mBuilder.setContentTitle(messageJson.getString(TITLE) + " (" + notificationListToBeDisplayed.size + " messages)")
            } else {
                mBuilder.setContentTitle(messageJson.getString(TITLE))
            }
            mBuilder.setStyle(messagingStyle)

            val remoteInput = RemoteInput.Builder(NotificationReciever.Reply.KEY_REPLY)
                    .setLabel(PushReceiver.PushChannel.replyLabel)
                    .build()
            val replyAction = NotificationCompat.Action.Builder(
                    android.R.drawable.sym_action_chat, "REPLY", pi
            )
                    .addRemoteInput(remoteInput)
                    .setAllowGeneratedReplies(true)
                    .build()

            if (messageJson.has(IS_THREAD_MESSAGE)) {
                if (messageJson.has(USER_TYPE)) {
                    if (messageJson.getInt(USER_TYPE) == 4) {
                        mBuilder = builderWithoutReply(mBuilder, pi, notificationDefaults, priority, message)
                    } else {
                        mBuilder = builderWithReply(mBuilder, pi, notificationDefaults, priority, replyAction, message)
                    }
                } else {
                    mBuilder = builderWithReply(mBuilder, pi, notificationDefaults, priority, replyAction, message)
                }
            } else {
                mBuilder =
                        builderWithoutReply(mBuilder, pi, notificationDefaults, priority, message)
                mBuilder.priority = priority
            }
        } else {
            val remoteInput = RemoteInput.Builder(NotificationReciever.Reply.KEY_REPLY)
                    .setLabel(replyLabel)
                    .build()
            val replyAction = NotificationCompat.Action.Builder(
                    android.R.drawable.sym_action_chat, "REPLY", pi
            )
                    .addRemoteInput(remoteInput)
                    .setAllowGeneratedReplies(true)
                    .build()
            val image = try {
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userImage
            } catch (e: IOException) {
                ""
            }
            var user: androidx.core.app.Person? = null
            val targetImageBitmap: Bitmap? = NotificationImageManager().getImageBitmap(image)
            user = androidx.core.app.Person.Builder()
                    .setIcon(IconCompat.createWithBitmap(targetImageBitmap))
                    .setName("You").build()
            val messagingStyle =
                    if (messageJson.has(BUSINESS_NAME) && messageJson.has(TITLE) && messageJson.getInt(CHAT_TYPE) != 2 && messageJson.getInt(CHAT_TYPE) != 7) {
                        try {
                            if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.size > 1) {
                                NotificationCompat.MessagingStyle(user).setConversationTitle(
                                        messageJson.getString(TITLE) + "(" + messageJson.getString(BUSINESS_NAME) + ")"
                                )
                            } else {
                                NotificationCompat.MessagingStyle(user).setConversationTitle(
                                        messageJson.getString(TITLE)
                                )
                            }
                        } catch (e: java.lang.Exception) {
                            NotificationCompat.MessagingStyle(user).setConversationTitle(
                                    messageJson.getString(TITLE) + "(" + messageJson.getString(BUSINESS_NAME) + ")"
                            )
                        }

                    } else if (messageJson.has(BUSINESS_NAME)) {
                        try {
                            if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.size > 1) {
                                NotificationCompat.MessagingStyle(user)
                                        .setConversationTitle(messageJson.getString(BUSINESS_NAME))
                            } else {
                                NotificationCompat.MessagingStyle(user)
                            }
                        } catch (e: java.lang.Exception) {
                            NotificationCompat.MessagingStyle(user)
                                    .setConversationTitle(messageJson.getString(BUSINESS_NAME))
                        }
                    } else {
                        NotificationCompat.MessagingStyle(user)
                    }
            messagingStyle.isGroupConversation = true
            val title = if (messageJson.has(TITLE)) messageJson.getString(TITLE) else ""
            val senderName = if (messageJson.has(LAST_SENT_BY_FULL_NAME)) {
                messageJson.getString(LAST_SENT_BY_FULL_NAME)
            } else {
                title
            }
            val senderImage = if (messageJson.has(USER_THUMBNAIL_IMAGE)) {
                messageJson.getString(USER_THUMBNAIL_IMAGE)
            } else {
                ""
            }
            val targetImageBitmap2: Bitmap? = NotificationImageManager().getImageBitmap(senderImage)
            user = androidx.core.app.Person.Builder()
                    .setIcon(IconCompat.createWithBitmap(targetImageBitmap2))
                    .setName(senderName).build()
            val myDate = messageJson.getString("date_time")
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            val date = sdf.parse(myDate)
            val millis = date.time
            messagingStyle.addMessage(NotificationCompat.MessagingStyle.Message(messageJson.getString(NOTI_MSG), millis, user))
            mBuilder = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                    .setStyle(messagingStyle)
                    .setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                    .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                    .setLargeIcon(
                            BitmapFactory.decodeResource(
                                    context.resources,
                                    R.drawable.ic_fugu
                            )
                    )
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .addAction(replyAction)
                    .setPriority(priority)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                    .setGroup(PushReceiver.PushChannel.GROUP_KEY_WORK_EMAIL)

        }


        notificationDefaults = if (CommonData.isVibration()!!) {
            Notification.DEFAULT_ALL
        } else {
            Notification.DEFAULT_SOUND
        }
        mBuilder.setDefaults(notificationDefaults)
        mBuilder.setChannelId(CHANNEL_ONE_ID)


        var notification: Notification? = null
        if (messageJson.has(SHOW_PUSH) && messageJson.getInt(SHOW_PUSH) == 1) {
            if (isSpecialPush) {
                mBuilder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
            }
            notification = mBuilder.build()
        } else {
            Log.e("SilentPush", "SilentPush")
        }

        localStorage(messageJson, context, message, data, notificationList)
        notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (!isSpecialPush) {
            if (notification != null) {
                val notificationIntent2 = Intent(context, MainActivity::class.java)
                notificationIntent2.putExtra("summary_notification", "summary_notification")

                val pi2 = PendingIntent.getActivity(
                        context,
                        messageJson.getLong(CHANNEL_ID).toInt(),
                        notificationIntent2,
                        PendingIntent.FLAG_UPDATE_CURRENT
                )

                notificationManager?.notify(messageJson.getLong(CHANNEL_ID).toInt(), notification)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                            .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                            .setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                            .setLargeIcon(
                                    BitmapFactory.decodeResource(
                                            context.resources,
                                            R.drawable.ic_fugu
                                    )
                            )
                    summaryNotification.setContentTitle(messageJson.getString(TITLE))
                    if (messageJson.has(LAST_SENT_BY_FULL_NAME)) {
                        summaryNotification.setContentText(messageJson.getString(LAST_SENT_BY_FULL_NAME) + ": " + message)
                    } else {
                        summaryNotification.setContentText(message)
                    }
//                    summaryNotification.setContentText(BuildConfig.APP_NAME)
                    summaryNotification.setGroup(GROUP_KEY_WORK_EMAIL)
                    summaryNotification.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                    summaryNotification.setGroupSummary(true)
                    summaryNotification.setAutoCancel(true)
                    summaryNotification.setContentIntent(pi2)

                    notificationManager?.notify(
                            SUMMARY_NOTIFICATION_ID,
                            summaryNotification.build()
                    )
                }
            }
            try {
                Thread {
                    kotlin.run {
                        val notificationsMap: HashMap<Int, java.util.ArrayList<Int>>
                        var notificationsMapList = ArrayList<Int>()
                        if (messageJson.has("business_id") && ChatDatabase.getNotificationsMap() != null) {
                            notificationsMap = ChatDatabase.getNotificationsMap()
                            if (notificationsMap[messageJson.getInt("business_id")] != null) {
                                notificationsMapList = notificationsMap[messageJson.getInt("business_id")]!!
                            }
                            if (!notificationsMapList.contains(messageJson.getLong(CHANNEL_ID).toInt())) {
                                notificationsMapList.add(messageJson.getLong(CHANNEL_ID).toInt())
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

    }

    private fun localStorage(messageJson: JSONObject, context: Context, message: String, data: Map<String, String>, notificationList: ArrayList<PushNotification>) {
        val messageList: ArrayList<Message> = ChatDatabase.getMessageList(messageJson.getLong(CHANNEL_ID))
        val mFuguMessageSet = ChatDatabase.getMessageMap(messageJson.getLong(CHANNEL_ID))
        for (i in 0 until messageList.size - 1) {
            if (messageList[i].rowType == 8) {
                messageList.removeAt(i)
                mFuguMessageSet.remove("Unread")
            }
        }
        var messageToBeAdded: Message? = null
        if (messageJson.has(MESSAGE_UNIQUE_ID) && messageJson.has(MESSAGE_TYPE)
                && !TextUtils.isEmpty(messageJson.getString(MESSAGE_UNIQUE_ID))
                && messageJson.has(NEW_MESSAGE)
        ) {
            if (messageJson.getInt(MESSAGE_TYPE) == TEXT_MESSAGE) {
                messageToBeAdded = Message(
                        messageList.size.toLong(),
                        messageJson.getString(LAST_SENT_BY_FULL_NAME),
                        messageJson.getLong(LAST_SENT_BY_ID),
                        messageJson.getString(NEW_MESSAGE),
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

                messageToBeAdded.alteredMessage = FormatStringUtil.FormatString.getFormattedString(
                        messageJson.getString(NEW_MESSAGE)
                )[0]
                messageToBeAdded.formattedMessage =
                        FormatStringUtil.FormatString.getFormattedString(
                                messageJson.getString(NEW_MESSAGE)
                        )[1]
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
                            if (messageJson.has(IS_THREAD_MESSAGE) && !messageJson.getBoolean(
                                            IS_THREAD_MESSAGE
                                    )
                            ) {
                                messageList.add(messageToBeAdded)
                                messageList.add(
                                        messageList.size - notificationList.size,
                                        unreadMessage
                                )
                            }
                        }

                        if (messageJson.has(IS_THREAD_MESSAGE) && !messageJson.getBoolean(IS_THREAD_MESSAGE)) {

                            object : Thread() {
                                override fun run() {
                                    super.run()
                                    try {
                                        val conversationMap = CommonData.getConversationList(messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                                        val conversations = ArrayList(conversationMap.values)
                                        for (i in conversations.indices) {
                                            try {
                                                if (conversations[i].channelId!!.compareTo(messageJson.getLong(CHANNEL_ID)) == 0) {
                                                    conversations[i].unreadCount =
                                                            notificationList.size
                                                    conversations[i].message =
                                                            messageJson.getString(NEW_MESSAGE)
                                                    conversations[i].dateTime =
                                                            messageJson.getString(DATE_TIME)
                                                    conversations[i].last_sent_by_id =
                                                            messageJson.getLong(LAST_SENT_BY_ID)
                                                    conversationMap[conversations[i].channelId] =
                                                            conversations[i]
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
                    mFuguMessageSet[UUID.randomUUID().toString()] =
                            messageList[messageList.size - 1]
                }
            }
        }
        if (messageJson.has("is_thread_message") && !messageJson.getBoolean("is_thread_message")) {
            ChatDatabase.setMessageList(messageList, messageJson.getLong(CHANNEL_ID))
            ChatDatabase.setMessageMap(mFuguMessageSet, messageJson.getLong(CHANNEL_ID))
        }

    }


    private fun builderWithReply(
            mBuilder: NotificationCompat.Builder,
            pi: PendingIntent?, notificationDefaults: Int,
            priority: Int, replyAction: NotificationCompat.Action?,
            message: String
    ): NotificationCompat.Builder {
        mBuilder.setContentText(Html.fromHtml(message))
                .setContentIntent(pi)
                .setDefaults(notificationDefaults)
                .setPriority(priority)
                .addAction(replyAction)
        return mBuilder
    }

    private fun builderWithoutReply(
            mBuilder: NotificationCompat.Builder, pi: PendingIntent?,
            notificationDefaults: Int,
            priority: Int, message: String
    ): NotificationCompat.Builder {
        mBuilder.setContentText(Html.fromHtml(message))
                .setContentIntent(pi)
                .setDefaults(notificationDefaults).priority = priority
        return mBuilder
    }


    private fun createNotificationChannel(context: Context) {
        notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                val notificationChannel = NotificationChannel(
                        PushReceiver.PushChannel.CHANNEL_ONE_ID,
                        PushReceiver.PushChannel.CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.setShowBadge(true)
                notificationManager?.createNotificationChannel(notificationChannel)
            }
        }
    }

    fun foregrounded(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
    }
}