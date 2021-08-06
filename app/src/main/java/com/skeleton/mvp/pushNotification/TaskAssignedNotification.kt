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
import android.view.View
import androidx.core.app.NotificationCompat
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.model.UnreadCount
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.CHANNEL_ONE_ID
import com.skeleton.mvp.service.FuguPushIntentService
import org.json.JSONObject

class TaskAssignedNotification {
    var notificationManager: NotificationManager? = null
    fun taskAssignedNotification(data: Map<String, String>, context: Context,
                                 messageJson: JSONObject, priority: Int,
                                 smallIcon: Int) {
        createNotificationChannel(context)
        val notificationIntent = Intent(context, FuguPushIntentService::class.java)
//        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        notificationIntent.putExtra("openMeetTab", true)
//        val workspacesInfo = CommonData.getCommonResponse().getData().workspacesInfo as java.util.ArrayList<WorkspacesInfo>
//        var appSecretKey = ""
//        try {
//            appSecretKey = messageJson.getString(FuguAppConstant.APP_SECRET_KEY)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        notificationIntent.putExtra(FuguAppConstant.APP_SECRET_KEY, appSecretKey)
//        if (workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey != appSecretKey)
//            for (i in workspacesInfo.indices) {
//                if (workspacesInfo[i].fuguSecretKey == appSecretKey) {
//                    disconnectSocket()
//                    setInitApi(false)
//                    initSocketConnection(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
//                            CommonData.getCommonResponse().getData().workspacesInfo[i].enUserId, CommonData.getCommonResponse().getData().getUserInfo().userId,
//                            CommonData.getCommonResponse().getData().getUserInfo().userChannel, "ChangeBusiness", false,
//                            CommonData.getCommonResponse().getData().getUserInfo().pushToken)
//                    CommonData.setCurrentSignedInPosition(i)
//                    break
//                }
//            }
        val mBundle = Bundle()
        for (key in data.keys) {
            mBundle.putString(key, data[key])
        }
        notificationIntent.putExtra("data", mBundle)
        val reqId = (Math.random() * 10000).toInt()
        val pi = PendingIntent.getService(context, reqId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationDefaults = Notification.DEFAULT_ALL
        val notification: Notification?
        val mBuilder: NotificationCompat.Builder
        NotificationCompat.InboxStyle()
        val messageDisplayed = messageJson.getString(FuguAppConstant.NOTI_MSG)
        val title = if (messageJson.has("title")) {
            messageJson.getString("title")
        } else {
            "New Task Assigned"
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
                if (notification.contentIntent != null && notification.headsUpContentView != null)
                    notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)
                if (notification.bigContentView != null)
                    notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)
            }
        }
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.notify(reqId, notification)
        val unreadCountList = com.skeleton.mvp.fugudatabase.CommonData.getNotificationCountList()
        unreadCountList.add(
                UnreadCount(
                        messageJson.getLong(FuguAppConstant.CHANNEL_ID),
                        messageJson.getString(FuguAppConstant.NOTI_MSG),
                        messageJson.getInt(FuguAppConstant.NOTIFICATION_TYPE),
                        false, false
                )
        )
        com.skeleton.mvp.fugudatabase.CommonData.setNotificationsCountList(unreadCountList)
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