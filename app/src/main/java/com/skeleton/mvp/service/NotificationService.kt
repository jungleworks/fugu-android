package com.skeleton.mvp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.*
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.model.pushNotification.PushNotifications
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.service.NotificationService.NotificationServiceState.isServiceRunning
import com.skeleton.mvp.ui.AppConstants.*
import com.skeleton.mvp.utils.UniqueIMEIID


class NotificationService : Service() {
    private var mBinder: IBinder = LocalBinder()
    private var fuguNotificationConfig = PushReceiver().getInstance()
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    object NotificationServiceState {
        var isServiceRunning = false
    }

    inner class LocalBinder : Binder() {
        val serverInstance: NotificationService
            get() = this@NotificationService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isServiceRunning = true
        val notification: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = NotificationCompat.Builder(this, "NotificationService")
            notification.setContentTitle(BuildConfig.APP_NAME)
            notification.setContentText("Checking for new messages")
            notification.setSmallIcon(R.drawable.notification_white)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setOngoing(true)
            notification.priority = getPriority()
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel("NotificationService",
                        "NotificationService", NotificationManager.IMPORTANCE_LOW)
                notificationChannel.setSound(null, null)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            startForeground(1124, notification.build())

        } else {
            notification = NotificationCompat.Builder(this)
            notification.setContentTitle(BuildConfig.APP_NAME)
            notification.setContentText("Checking for new messages")
            notification.setSmallIcon(R.drawable.notification_white)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setOngoing(true)
            notification.priority = getPriority()
            startForeground(1124, notification.build())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fuguNotificationConfig.setSmallIcon(R.drawable.notification_white)
        } else {
            fuguNotificationConfig.setSmallIcon(R.drawable.ic_fugu)
        }
        getPushNotifications()

        return START_STICKY
    }

    private fun getPriority(): Int {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            Notification.PRIORITY_MAX
        }
    }

    private fun getPushNotifications() {
        val commonParams = CommonParams.Builder()
                .add(ACCESS_TOKEN, CommonData.getCommonResponse().getData().getUserInfo().accessToken)
                .add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this))
                .add(LAST_NOTIFICATION_ID, CommonData.getLocalNotificationId())
        RestClient.getApiInterface(true).getPushNotifications(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey,
                1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<PushNotifications>() {
                    override fun onSuccess(pushNotifications: PushNotifications) {
                        try {
                            CommonData.setLocalNotificationId(pushNotifications.data.lastNotificationId)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        for (notification in pushNotifications.data.pushNotifications) {
                            val gson = Gson()
                            val json = gson.toJson(notification.data)
                            val pushMap = LinkedHashMap<String, String>()
                            pushMap.put("message", json)
                            fuguNotificationConfig.pushRedirectionNew(this@NotificationService, pushMap)
                        }

                        NotificationSockets.onNotificationsPublished()
                        Handler(Looper.getMainLooper()).postDelayed({
                            isServiceRunning = false
                            stopSelf()
                        }, 500)
                    }

                    override fun onError(error: ApiError?) {
                        isServiceRunning = false
                        stopSelf()
                    }

                    override fun onFailure(throwable: Throwable?) {
                        isServiceRunning = false
                        stopSelf()
                    }

                })
    }

    interface NotificationHandler {
        fun onNotificationsPublished()
    }

}