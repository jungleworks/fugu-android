package com.skeleton.mvp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.utils.FuguLog

/**
 * Created by bhavya on 10/07/17.
 */

class FuguOnClearFromRecentService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        FuguLog.d("ClearFromRecentService", "Service Started")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notification = NotificationCompat.Builder(this, "VideoCall")
            notification.setContentTitle("Conference")
            notification.setContentText(FuguAppConstant.APP_NAME_SHORT )
            notification.setSmallIcon(R.drawable.notification_white)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setOngoing(true)

            notification.priority = getPriority()
//            val hangupAction = NotificationCompat.Action.Builder(
//                    android.R.drawable.sym_action_chat, "HANG UP", hungupPendingIntent)
//                    .build()
//            notification.addAction(hangupAction)

            startForeground(1122, notification.build())
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var notificationChannel = NotificationChannel("VideoCall",
                        "VideoCall", NotificationManager.IMPORTANCE_LOW)
                notificationChannel.setSound(null, null)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationManager.notify(1122, notification.build())
        } else {

            val notification = NotificationCompat.Builder(this)
            notification.setContentTitle("Conference")
            notification.setContentText(FuguAppConstant.APP_NAME_SHORT )

            notification.setSmallIcon(R.drawable.notification_white)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setOngoing(true)

            notification.priority = getPriority()
//            val hangupAction = NotificationCompat.Action.Builder(
//                    android.R.drawable.sym_action_chat, "HANG UP", hungupPendingIntent)
//                    .build()
//            notification.addAction(hangupAction)
            startForeground(1122, notification.build())
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1122, notification.build())
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        FuguLog.d("ClearFromRecentService", "Service Destroyed")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        FuguLog.e("ClearFromRecentService", "END")
        //Code here
        stopSelf()
    }

    private fun getPriority(): Int {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            Notification.PRIORITY_MAX
        }
    }
}
