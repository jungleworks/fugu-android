package com.skeleton.mvp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.IncomingVideoConferenceActivity
import com.skeleton.mvp.activity.StartHangoutsActivity
import com.skeleton.mvp.activity.VideoConfActivity
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.pushNotification.NotificationImageManager
import com.skeleton.mvp.receiver.HungUpBroadcast
import com.skeleton.mvp.service.OngoingCallService.NotificationServiceState.appSecretKey
import com.skeleton.mvp.service.OngoingCallService.NotificationServiceState.channelId
import com.skeleton.mvp.service.OngoingCallService.NotificationServiceState.inviteLink
import com.skeleton.mvp.service.OngoingCallService.NotificationServiceState.isConferenceServiceRunning
import com.skeleton.mvp.util.Utils
import com.skeleton.mvp.utils.getDeviceDetails

class ConferenceCallService : Service() {
    private var mBinder: IBinder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        val serverInstance: ConferenceCallService
            get() = this@ConferenceCallService
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel("IncomingCall",
                    "IncomingCall", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isConferenceServiceRunning = true
        if (intent != null) {
            createIncomingCallNotification(intent)
        }
        return START_STICKY
    }

    private fun createIncomingCallNotification(intent: Intent) {
        val notificationIntent = Intent(this, IncomingVideoConferenceActivity::class.java)
        val customView = RemoteViews(packageName, R.layout.cutom_call_notification)
        notificationIntent.action = Intent.ACTION_MAIN
        customView.setTextViewText(R.id.name, BuildConfig.APP_NAME)
        inviteLink = intent.getStringExtra(INVITE_LINK)!!
        channelId = intent.getLongExtra(CHANNEL_ID, -1L)
        notificationIntent.putExtra(INVITE_LINK, inviteLink)
        notificationIntent.putExtra(CHANNEL_ID, channelId)
        notificationIntent.putExtra(APP_SECRET_KEY, appSecretKey)
        notificationIntent.putExtra(BASE_URL, intent.getStringExtra(BASE_URL))
        notificationIntent.putExtra(ROOM_NAME, intent.getStringExtra(ROOM_NAME))
        if (intent.hasExtra("caller_text")) {
            notificationIntent.putExtra("caller_text", intent.getStringExtra("caller_text"))
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

        val hungupIntent = Intent(this, HungUpBroadcast::class.java)
        hungupIntent.putExtra("action", "rejectCall")
        hungupIntent.putExtra(INVITE_LINK, inviteLink)
        hungupIntent.putExtra(CHANNEL_ID, channelId)
        hungupIntent.putExtra(APP_SECRET_KEY, appSecretKey)
        hungupIntent.putExtra(DEVICE_PAYLOAD, getDeviceDetails().toString())

        var answerIntent = Intent(this, VideoConfActivity::class.java)
        if (inviteLink.contains("meet.google.com")) {
            answerIntent = Intent(this, StartHangoutsActivity::class.java)
        }
        answerIntent.action = Intent.ACTION_ANSWER
        answerIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        answerIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        answerIntent.putExtra(INVITE_LINK, inviteLink)
        answerIntent.putExtra(CHANNEL_ID, channelId)
        answerIntent.putExtra(APP_SECRET_KEY, appSecretKey)
        answerIntent.putExtra(BASE_URL, intent.getStringExtra(BASE_URL))
        answerIntent.putExtra(ROOM_NAME, intent.getStringExtra(ROOM_NAME))
        if (intent.hasExtra("caller_text")) {
            answerIntent.putExtra("caller_text", intent.getStringExtra("caller_text"))
            customView.setTextViewText(R.id.callType, intent.getStringExtra("caller_text"))
        } else
            customView.setTextViewText(R.id.callType, intent.getStringExtra("Incoming Hangouts Call"))


        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val hungupPendingIntent = PendingIntent.getBroadcast(this, 0, hungupIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val answerPendingIntent = PendingIntent.getActivity(this, 0, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        customView.setImageViewBitmap(R.id.photo, Utils.getCircleBitmap(NotificationImageManager().getImageBitmap(intent.getStringExtra(USER_THUMBNAIL_IMAGE)!!)))
        customView.setOnClickPendingIntent(R.id.btnAnswer, answerPendingIntent)
        customView.setOnClickPendingIntent(R.id.btnDecline, hungupPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationCompat.Builder(this, "IncomingCall")
            notification.setContentTitle(BuildConfig.APP_NAME)
            notification.setTicker("Call_STATUS")
            notification.setContentText("IncomingCall")
            notification.setSmallIcon(R.drawable.notification_white)
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setCategory(NotificationCompat.CATEGORY_CALL)
            notification.setVibrate(null)
            notification.setOngoing(true)
            notification.setFullScreenIntent(pendingIntent, true)
            notification.priority = getPriority()
            notification.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            notification.setCustomContentView(customView)
            notification.setCustomBigContentView(customView)

            startForeground(1124, notification.build())
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel("IncomingCall", "IncomingCall", NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.setSound(null, null)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        } else {
            val notification = NotificationCompat.Builder(this)
            notification.setContentTitle(BuildConfig.APP_NAME)
            notification.setTicker("Call_STATUS")
            notification.setContentText("IncomingCall")
            notification.setSmallIcon(R.drawable.notification_white)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setContentIntent(pendingIntent)
            notification.setOngoing(true)
            notification.setCategory(NotificationCompat.CATEGORY_CALL)
            notification.priority = getPriority()
            val hangupAction = NotificationCompat.Action.Builder(android.R.drawable.sym_action_chat, "HANG UP", hungupPendingIntent)
                    .build()
            notification.addAction(hangupAction)
            startForeground(1124, notification.build())
        }
    }

    private fun getPriority(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            Notification.PRIORITY_MAX
        }
    }
}