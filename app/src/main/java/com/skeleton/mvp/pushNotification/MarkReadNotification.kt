package com.skeleton.mvp.pushNotification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import com.facebook.react.bridge.UiThreadUtil
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.BUSINESS_ID
import com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_ID
import com.skeleton.mvp.data.db.ChatDatabase
import org.json.JSONObject

class MarkReadNotification {
    fun markNotificationsRead(context: Context, messageJson: JSONObject) {
        Thread(Runnable {
            try {
                val businessId = messageJson.getInt(BUSINESS_ID)
                val channelId = messageJson.getLong(CHANNEL_ID)
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
                UiThreadUtil.runOnUiThread(Runnable {
                    if (notificationsList != null) {
                        for (notification in notificationsList) {
                            try {
                                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                nm.cancel(notification)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Handler().postDelayed({
                        val nm =
                                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (nm.activeNotifications.size == 1) {
                                val notifications = nm.activeNotifications
                                for (notification in notifications) {
                                    if (notification.id == PushReceiver.PushChannel.SUMMARY_NOTIFICATION_ID) {
                                        nm.cancel(PushReceiver.PushChannel.SUMMARY_NOTIFICATION_ID)
                                        break
                                    }
                                }
                            }
                        }
                    }, 100)
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }
}