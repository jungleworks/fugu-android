package com.skeleton.mvp.pushNotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import android.view.View
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.BUSINESS_ID
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.model.MissedCallNotification
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.CHANNEL_ONE_ID
import com.skeleton.mvp.service.FuguPushIntentService
import org.json.JSONObject

class MissCallNotification {
    var notificationManager: NotificationManager? = null
    fun missedCallNotification(data: Map<String, String>, context: Context,
                               messageJson: JSONObject, priority: Int,
                               smallIcon: Int) {
        var missedCallNotificationList = ArrayList<MissedCallNotification>()
        missedCallNotificationList = ChatDatabase.getCallNotifications(messageJson.getLong(BUSINESS_ID))

        missedCallNotificationList.add(MissedCallNotification(messageJson.getString(FuguAppConstant.FULL_NAME), false,
                messageJson.getString(FuguAppConstant.DATE_TIME), false, messageJson.getLong(FuguAppConstant.USER_ID)))

        ChatDatabase.setCallNotification(messageJson.getLong(BUSINESS_ID), missedCallNotificationList)
        createNotificationChannel(context)
        try {
            Thread {
                kotlin.run {
                    var notificationsMap = HashMap<Int, ArrayList<Int>>()
                    var notificationsMapList = ArrayList<Int>()
                    if (messageJson.has(BUSINESS_ID) && ChatDatabase.getCallNotificationsMap() != null) {
                        notificationsMap = ChatDatabase.getCallNotificationsMap()
                        if (notificationsMap[messageJson.getInt(BUSINESS_ID)] != null) {
                            notificationsMapList = notificationsMap[messageJson.getInt(BUSINESS_ID)]!!
                        }
                        if (!notificationsMapList.contains(messageJson.getLong(FuguAppConstant.CHANNEL_ID).toInt())) {
                            notificationsMapList.add(messageJson.getLong(FuguAppConstant.CHANNEL_ID).toInt())
                        }
                        notificationsMap.put(messageJson.getInt(BUSINESS_ID), notificationsMapList)
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
        var messageDisplayed = messageJson.getString(FuguAppConstant.FULL_NAME)
        var title = ""

        if (messageJson.has(FuguAppConstant.CALL_TYPE) && messageJson.getString(FuguAppConstant.CALL_TYPE).equals("AUDIO")) {
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
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_fugu))
                .setContentTitle(title)
                .setContentText(messageDisplayed)
                .setContentIntent(pi)
                .setGroup(PushReceiver.PushChannel.GROUP_KEY_WORK_EMAIL)
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
        notificationManager?.notify("CALL", messageJson.getInt(BUSINESS_ID), notification)
    }

    private fun createNotificationChannel(context: Context) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager?.createNotificationChannel(NotificationChannel(CHANNEL_ONE_ID,
                        PushReceiver.PushChannel.CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH))
            }
        }
    }
}