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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.View
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.MESSAGE
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.CHANNEL_ONE_ID
import com.skeleton.mvp.service.FuguPushIntentService
import com.skeleton.mvp.ui.AppConstants.BUSINESS_NAME
import org.json.JSONObject
import java.util.*

class NormalNotification {
    var notificationManager: NotificationManager? = null
    fun publishNotification(data: Map<String, String>, context: Context, messageJson: JSONObject,
                            smallIcon: Int) {
        try {
            createNotificationChannel(context)
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
            val pi = PendingIntent.getService(context, 12345,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationDefaults = Notification.DEFAULT_ALL
            val notification: Notification?
            val mBuilder: NotificationCompat.Builder
            mBuilder = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(messageJson.getString(MESSAGE)))
                    .setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                    .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_fugu))
                    .setContentTitle(messageJson.getString(FuguAppConstant.TITLE))
                    .setContentText(messageJson.getString(MESSAGE))
                    .setContentIntent(pi)
                    .setGroup(PushReceiver.PushChannel.GROUP_KEY_WORK_EMAIL)
                    .setPriority(priority)
            if (messageJson.has(BUSINESS_NAME)) {
                mBuilder.setSubText(messageJson.getString(BUSINESS_NAME))
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