package com.skeleton.mvp.activity

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.CONFERENCE_CALL
import com.skeleton.mvp.constant.FuguAppConstant.VIDEO_CALL_TYPE
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.receiver.HungUpBroadcast
import com.skeleton.mvp.service.ConferenceCallService
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.utils.UniqueIMEIID
import com.skeleton.mvp.utils.joinHangoutsCall
import com.skeleton.mvp.videoCall.WebRTCCallConstants
import com.skyfishjy.library.RippleBackground
import org.json.JSONObject

class IncomingVideoConferenceActivity : AppCompatActivity() {
    var mediaPlayer: MediaPlayer? = null
    private var vibrate: Vibrator? = null

    private var ivAnswerCall: AppCompatImageView? = null
    private var ivRejectCall: AppCompatImageView? = null
    private var roomName = ""
    private var isHangoutsCall = false
    private var tvIncomingPersonName: AppCompatTextView? = null
    private var incomingRippleView: RippleBackground? = null

    object IncomingCall {
        var incomingConferenceStatus: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.action == Intent.ACTION_ANSWER) {
            answerCall()
        }
        setContentView(R.layout.activity_incoming_video_conference)
//        IncomingCall.incomingConferenceStatus = true
        val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val taskList = mngr!!.getRunningTasks(10)
        for (task in taskList) {
            Log.e("Task", task.topActivity!!.className)
        }
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        roomName = intent.getStringExtra("room_name")!!
        mediaPlayer = MediaPlayer.create(this, R.raw.ringing)
        initiateIncomingRinging()
        tvIncomingPersonName = findViewById(R.id.tvIncomingPersonName)
        ivAnswerCall = findViewById(R.id.ivAnswerCall)
        ivRejectCall = findViewById(R.id.ivRejectCall)
        incomingRippleView = findViewById(R.id.incomingRippleView)
        val inviteLink = intent!!.getStringExtra(FuguAppConstant.INVITE_LINK)
        if (inviteLink != null && inviteLink.contains("meet.google.com")) {
            isHangoutsCall = true
        }
        if (intent.hasExtra("caller_text")) {
            tvIncomingPersonName?.text = intent.getStringExtra("caller_text")
        }

        incomingRippleView?.startRippleAnimation()

        ivAnswerCall?.setOnClickListener {
            answerCall()
            mediaPlayer?.stop()
            vibrate?.cancel()
            finish()
            val inviteLink = intent!!.getStringExtra(FuguAppConstant.INVITE_LINK)
            if (inviteLink != null && inviteLink.contains("meet.google.com")) {
                joinHangoutsCall(inviteLink)
                stopService(Intent(this@IncomingVideoConferenceActivity, ConferenceCallService::class.java))
                finish()
            } else {
                val videoIntent = Intent(this@IncomingVideoConferenceActivity, VideoConfActivity::class.java)
                videoIntent.putExtra("base_url", CommonData.getConferenceUrl())
                videoIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                videoIntent.putExtra(FuguAppConstant.INVITE_LINK, inviteLink)
                videoIntent.putExtra(FuguAppConstant.CHANNEL_ID, intent?.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L))
                videoIntent.putExtra("room_name", roomName)
                startActivity(videoIntent)
            }
        }

        ivRejectCall?.setOnClickListener {
            rejectCall()
            //            IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus = false
            val hungupIntent = Intent(applicationContext, HungUpBroadcast::class.java)
            hungupIntent.putExtra("action", "rejectCall")
            hungupIntent.putExtra(FuguAppConstant.INVITE_LINK, intent?.getStringExtra(FuguAppConstant.INVITE_LINK))
            hungupIntent.putExtra(FuguAppConstant.CHANNEL_ID, intent?.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L))
            hungupIntent.putExtra(FuguAppConstant.DEVICE_PAYLOAD, getDeviceDetails().toString())
            sendBroadcast(hungupIntent)
            val mIntent = Intent("CALL_HANGUP")
            LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent)
            mediaPlayer?.stop()
            vibrate?.cancel()
            finish()
        }
        try {
            Handler().postDelayed({
                try {
//                    IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus = false
                    mediaPlayer?.stop()
                    vibrate?.cancel()
                    finish()
                } catch (e: Exception) {

                }
            }, 30000)
        } catch (e: Exception) {

        }

    }

    private fun answerCall() {
        try {
            val startCallJson = JSONObject()
            startCallJson.put(FuguAppConstant.USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)
            startCallJson.put(FuguAppConstant.USER_UNIQUE_KEY, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userId)
            startCallJson.put(FuguAppConstant.MESSAGE_TYPE, CONFERENCE_CALL)
            startCallJson.put(VIDEO_CALL_TYPE, "ANSWER_MULTI_CALL")
            startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, getDeviceDetails())
            if (isHangoutsCall)
                SocketConnection.sendHangoutsEvent(startCallJson)
            else
                SocketConnection.sendConferenceEvent(startCallJson)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rejectCall() {
        try {
            val startCallJson = JSONObject()
            startCallJson.put(FuguAppConstant.USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)
            startCallJson.put(FuguAppConstant.USER_UNIQUE_KEY, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userId)
            startCallJson.put(FuguAppConstant.MESSAGE_TYPE, CONFERENCE_CALL)
            startCallJson.put(VIDEO_CALL_TYPE, "REJECT_MULTI_CALL")
            startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, getDeviceDetails())
            if (isHangoutsCall)
                SocketConnection.sendHangoutsEvent(startCallJson)
            else
                SocketConnection.sendConferenceEvent(startCallJson)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoConferenceHungup)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mVideoConferenceHungup,
                IntentFilter(FuguAppConstant.VIDEO_CONFERENCE_HUNGUP_INTENT)
        )
    }

    private val mVideoConferenceHungup = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mediaPlayer?.stop()
            vibrate?.cancel()
            finish()
        }
    }

    override fun onStop() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoConferenceHungup)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onStop()
    }

    override fun onDestroy() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoConferenceHungup)
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }


    private fun initiateIncomingRinging() {
        Handler().postDelayed({
            try {
                val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                when (audio.ringerMode) {
                    AudioManager.RINGER_MODE_NORMAL -> {
                        mediaPlayer = MediaPlayer.create(this@IncomingVideoConferenceActivity,
                                Settings.System.DEFAULT_RINGTONE_URI)
                        mediaPlayer?.isLooping = true
                        mediaPlayer?.start()
                    }
                    AudioManager.RINGER_MODE_SILENT -> {

                    }
                    AudioManager.RINGER_MODE_VIBRATE -> {
                        vibrate = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
                        val pattern = longArrayOf(0, 1000, 1000)
                        vibrate?.vibrate(pattern, 0)
                    }
                }
            } catch (e: Exception) {
                mediaPlayer = MediaPlayer.create(this@IncomingVideoConferenceActivity,
                        R.raw.video_call_ringtone)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            }
//            }
        }, 0)
    }

    private fun getDeviceDetails(): JSONObject {
        val devicePayload = JSONObject()
        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this))
        return devicePayload
    }

}
