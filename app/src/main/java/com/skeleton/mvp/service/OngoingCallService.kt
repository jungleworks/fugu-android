package com.skeleton.mvp.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.CallFeedbackActivity
import com.skeleton.mvp.activity.IncomingJitsiCallActivity
import com.skeleton.mvp.activity.StartHangoutsActivity
import com.skeleton.mvp.activity.VideoConfActivity
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.pushNotification.NotificationImageManager
import com.skeleton.mvp.receiver.HungUpBroadcast
import com.skeleton.mvp.service.OngoingCallService.NotificationServiceState.appSecretKey
import com.skeleton.mvp.service.OngoingCallService.NotificationServiceState.channelId
import com.skeleton.mvp.service.OngoingCallService.NotificationServiceState.inviteLink
import com.skeleton.mvp.service.OngoingCallService.NotificationServiceState.isConferenceConnected
import com.skeleton.mvp.service.OngoingCallService.NotificationServiceState.isConferenceServiceRunning
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.util.Utils
import com.skeleton.mvp.utils.getDeviceDetails
import com.skeleton.mvp.videoCall.WebRTCCallConstants
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.json.JSONObject

class OngoingCallService : Service() {
    private var mBinder: IBinder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    object NotificationServiceState {
        var isConferenceServiceRunning = false
        var isConferenceConnected = false
        var inviteLink = ""
        var channelId = -1L
        var muid = ""
        var appSecretKey = ""
    }

    inner class LocalBinder : Binder() {
        val serverInstance: OngoingCallService
            get() = this@OngoingCallService
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notificationChannel = NotificationChannel("IncomingCall",
                    "IncomingCall", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        LocalBroadcastManager.getInstance(this@OngoingCallService)
                .registerReceiver(mCallTerminated, IntentFilter("CALL TERMINATED"))
        LocalBroadcastManager.getInstance(this@OngoingCallService)
                .registerReceiver(mInternetIssue, IntentFilter("INTERNET_ISSUE"))

    }


    private val mInternetIssue = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var mediaPlayer: MediaPlayer? = null
            var aa = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            mediaPlayer = MediaPlayer.create(this@OngoingCallService, R.raw.busy_tone)
//            if (videoCallModel?.callType.equals("VIDEO")) {
//
//            } else {
//                mediaPlayer = MediaPlayer.create(this, R.raw.busy_tone, aa, 1)
//            }
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }, 3000)
        }
    }

    private val mCallTerminated = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("CALL_TERMINATED", "CALL_TERMINATED")
            val startCallJson = JSONObject()
            startCallJson.put(IS_SILENT, true)
            startCallJson.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.JitsiCallType.HUNGUP_CONFERENCE.toString())
            try {
                val workspaceInfoList = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
                startCallJson.put(USER_ID, workspaceInfoList[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)
            } catch (e: Exception) {
                startCallJson.put(USER_ID, "12345678")
            }
            startCallJson.put(CHANNEL_ID, channelId)
            startCallJson.put(MESSAGE_TYPE, WebRTCCallConstants.VIDEO_CALL)
            startCallJson.put(CALL_TYPE, "VIDEO")
            startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, getDeviceDetails())
            startCallJson.put(INVITE_LINK, inviteLink)
            startCallJson.put(MESSAGE_UNIQUE_ID, NotificationServiceState.muid)
            SocketConnection.sendMessage(startCallJson)
            if (isConferenceConnected) {
                val intent = Intent(applicationContext, CallFeedbackActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            isConferenceServiceRunning = false
            isConferenceConnected = false
            stopSelf()
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isConferenceServiceRunning = true
        if (intent != null && intent.hasExtra(INCOMING_VIDEO_CONFERENCE)) {
            createIncomingCallNotification(intent)
        } else {
            createOngoingCallNotification(intent)
        }
        return START_STICKY
    }

    private fun createIncomingCallNotification(intent: Intent) {
        val notificationIntent = Intent(this, IncomingJitsiCallActivity::class.java)
        val customView = RemoteViews(packageName, R.layout.cutom_call_notification)
        customView.setTextViewText(R.id.name, BuildConfig.APP_NAME)
        notificationIntent.putExtra("room_name", intent.getStringExtra("room_name"))
        inviteLink = intent.getStringExtra(INVITE_LINK)!!
        channelId = intent.getLongExtra(CHANNEL_ID, -1L)
        notificationIntent.putExtra(INVITE_LINK, inviteLink)
        notificationIntent.putExtra(CHANNEL_ID, channelId)
        notificationIntent.putExtra(APP_SECRET_KEY, appSecretKey)
        notificationIntent.putExtra(FULL_NAME, intent.getStringExtra(FULL_NAME))
        notificationIntent.putExtra(USER_THUMBNAIL_IMAGE, intent.getStringExtra(USER_THUMBNAIL_IMAGE))
        notificationIntent.putExtra(CALL_TYPE, intent.getStringExtra(CALL_TYPE))
        notificationIntent.putExtra(MESSAGE_UNIQUE_ID, intent.getStringExtra(MESSAGE_UNIQUE_ID))
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

        val hungupIntent = Intent(this, HungUpBroadcast::class.java)
        hungupIntent.putExtra("action", "rejectCall")
        hungupIntent.putExtra(INVITE_LINK, inviteLink)
        hungupIntent.putExtra(CHANNEL_ID, channelId)
        hungupIntent.putExtra(APP_SECRET_KEY, appSecretKey)
        notificationIntent.putExtra(MESSAGE_UNIQUE_ID, intent.getStringExtra(MESSAGE_UNIQUE_ID))
        hungupIntent.putExtra(DEVICE_PAYLOAD, getDeviceDetails().toString())

        var answerIntent = Intent(this, VideoConfActivity::class.java)
        if (inviteLink.contains("meet.google.com")) {
            answerIntent = Intent(this, StartHangoutsActivity::class.java)
        }
        answerIntent.action = Intent.ACTION_ANSWER
        answerIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        answerIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        answerIntent.putExtra("room_name", intent.getStringExtra("room_name"))
        answerIntent.putExtra("call_type", intent.getStringExtra("call_type"))
        answerIntent.putExtra(APP_SECRET_KEY, appSecretKey)
        notificationIntent.putExtra(MESSAGE_UNIQUE_ID, intent.getStringExtra(MESSAGE_UNIQUE_ID))
        answerIntent.putExtra(INVITE_LINK, inviteLink)
        answerIntent.putExtra(CHANNEL_ID, channelId)


        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val hungupPendingIntent = PendingIntent.getBroadcast(this, 0, hungupIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val answerPendingIntent = PendingIntent.getActivity(this, 0, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        customView.setTextViewText(R.id.name, intent.getStringExtra(FULL_NAME))
        when {
            intent.getStringExtra(CALL_TYPE) == "VIDEO" -> {
                customView.setTextViewText(R.id.callType, "Incoming Video Call")
            }
            intent.getStringExtra(CALL_TYPE) == "HANGOUTS" -> {
                customView.setTextViewText(R.id.callType, "Incoming Hangouts Call")
            }
            else -> {
                customView.setTextViewText(R.id.callType, "Incoming Audio Call")
            }
        }
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

            startForeground(1122, notification.build())
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
            startForeground(1122, notification.build())
        }
    }

    private fun createOngoingCallNotification(intent: Intent?) {
        val openCall = Intent(this@OngoingCallService, JitsiMeetActivity::class.java)
        openCall.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        try {
            inviteLink = intent?.getStringExtra(INVITE_LINK)!!
            channelId = intent.getLongExtra(CHANNEL_ID, -1L)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, openCall, 0)
        val notification: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel("NotificationService",
                        "NotificationService", NotificationManager.IMPORTANCE_LOW)
                notificationChannel.setSound(null, null)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notification = NotificationCompat.Builder(this, "NotificationService")
            notification.setContentTitle("Ongoing Call")
            notification.setContentText("Tap to return to call screen")
            notification.setSmallIcon(R.drawable.notification_white)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setOngoing(true)
            notification.priority = getPriority()
            notification.setFullScreenIntent(pendingIntent, true)
            startForeground(1124, notification.build())
        } else {
            notification = NotificationCompat.Builder(this)
            notification.setContentTitle(BuildConfig.APP_NAME)
            notification.setContentText("Ongoing Call")
            notification.setSmallIcon(R.drawable.notification_white)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setOngoing(true)
            notification.priority = getPriority()
            notification.setContentIntent(pendingIntent)
            startForeground(1124, notification.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isConferenceServiceRunning = false
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCallTerminated)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mInternetIssue)
    }

    private fun getPriority(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            Notification.PRIORITY_MAX
        }
    }
}