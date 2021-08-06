package com.skeleton.mvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.fayeVideoCall.FayeVideoCallResponse
import com.skeleton.mvp.service.OngoingCallService
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.utils.FuguUtils.Companion.randomVideoConferenceLink
import com.skeleton.mvp.utils.UniqueIMEIID
import com.skeleton.mvp.utils.joinHangoutsCall
import com.skeleton.mvp.videoCall.VideoCallModel
import com.skeleton.mvp.videoCall.WebRTCCallConstants
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class HangoutsCallActivity : com.skeleton.mvp.ui.base.BaseActivity() {
    private var videoCallModel: VideoCallModel? = null
    private var ivHangUp: AppCompatImageView? = null
    private var inviteLink = ""
    private var mInitiateStartCallTimer: CountDownTimer? = null
    private var tvDummyImage: AppCompatTextView? = null
    private var initialCalls = 1
    private var initialCallsIOS = 1
    private var maxCalls = 30
    private var isReadyForConnection = false
    private var isReadyForConnectionIOS = false
    private var tvCallStatus: AppCompatTextView? = null
    private var tvCalledPersonName: AppCompatTextView? = null
    private var tvCallType: AppCompatTextView? = null
    private var ivCalledPersonImage: CircleImageView? = null
    var mListener: AudioManager.OnAudioFocusChangeListener? = null
    var mediaPlayer: MediaPlayer? = null
    var muid = UUID.randomUUID().toString()
    private var savedAudioMode = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jitsi_call)
        initViews()
        OngoingCallService.NotificationServiceState.muid = muid
        val linkArray = randomVideoConferenceLink()
        if (intent.hasExtra("videoCallModel")) {
            videoCallModel = intent.extras?.getParcelable("videoCallModel")
            val name = videoCallModel?.channelName
            tvCalledPersonName?.text = if (!TextUtils.isEmpty(name)) {
                name
            } else
                ""
            tvCallType?.text = "Hangouts Call"
            Glide.with(this)
                    .load(videoCallModel?.userThumbnailImage)
                    .into(ivCalledPersonImage!!)
            if (!TextUtils.isEmpty(videoCallModel?.userThumbnailImage)) {
                Glide.with(this)
                        .load(videoCallModel?.userThumbnailImage)
                        .into(ivCalledPersonImage!!)
            } else {
                try {
                    tvDummyImage!!.text = if (!TextUtils.isEmpty(name)) {
                        name!!.substring(0, 1)
                    } else
                        ""
                    when (videoCallModel!!.channelId % 5) {
                        1L -> tvDummyImage!!.setBackgroundResource(R.drawable.ring_purple)
                        2L -> tvDummyImage!!.setBackgroundResource(R.drawable.ring_teal)
                        3L -> tvDummyImage!!.setBackgroundResource(R.drawable.ring_red)
                        4L -> tvDummyImage!!.setBackgroundResource(R.drawable.ring_indigo)
                        else -> {
                            tvDummyImage!!.setBackgroundResource(R.drawable.ring_red)
                        }
                    }
                    ivCalledPersonImage!!.visibility = View.GONE
                    tvDummyImage!!.visibility = View.VISIBLE
                } catch (e: java.lang.Exception) {
                    Log.e("DummyImageException", e.message ?: "")
                    e.printStackTrace()
                }
            }
        }

        initCall(intent.getStringExtra("hangoutLink")!!)
        initiateOutgoingRinging()

        object : CountDownTimer(60000, 60000) {
            override fun onFinish() {
                Log.e("Timer", "Done")
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {
            }

        }.start()
        SocketConnection.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().userId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().userChannel, "Jitsi Call Reciever", true,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().pushToken)
        SocketConnection.setCallSocketListeners(object : SocketConnection.SocketClientCallback {

            override fun onCalling(messageJson: String) {
                try {
                    val data = JSONObject(messageJson)
                    Log.e("Video_CONF Reply-->", messageJson)
                    if (data.getString(VIDEO_CALL_TYPE) == FuguAppConstant.JitsiCallType.READY_TO_CONNECT_CONFERENCE.toString()
                            && (inviteLink == data.getString(INVITE_LINK))
                            && !data.getString(USER_ID).equals(videoCallModel?.userId)) {

                        val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                        val taskList = mngr.getRunningTasks(10)
                        if (taskList[0].topActivity!!.className != "com.skeleton.mvp.activity.IncomingJitsiCallActivity" && !taskList[0].topActivity!!.className.contains("GrantPermissionsActivity")) {
                            OngoingCallService.NotificationServiceState.inviteLink = data.getString(INVITE_LINK)
                        }
                        runOnUiThread {
                            tvCallStatus?.text = "Ringing..."
                        }
                        isReadyForConnection = true
                        val startCallJson = JSONObject()
                        startCallJson.put(IS_SILENT, true)
                        startCallJson.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.JitsiCallType.OFFER_CONFERENCE.toString())
                        startCallJson.put(USER_ID, videoCallModel?.userId)
                        startCallJson.put(CHANNEL_ID, videoCallModel?.channelId)
                        startCallJson.put(MESSAGE_TYPE, WebRTCCallConstants.VIDEO_CALL)
                        startCallJson.put(CALL_TYPE, videoCallModel?.callType)
                        startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, getDeviceDetails())
                        startCallJson.put(MESSAGE_UNIQUE_ID, muid)
                        startCallJson.put(INVITE_LINK, inviteLink)
                        SocketConnection.sendMessage(startCallJson)
                        Log.e("Video_CONF-->", startCallJson.toString())
                    } else if (data.getString(VIDEO_CALL_TYPE) == FuguAppConstant.JitsiCallType.READY_TO_CONNECT_CONFERENCE_IOS.toString()
                            && (inviteLink == data.getString(INVITE_LINK))
                            && !data.getString(USER_ID).equals(videoCallModel?.userId)) {
                        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                        val taskList = manager.getRunningTasks(10)
                        if (taskList[0].topActivity!!.className != "com.skeleton.mvp.activity.IncomingJitsiCallActivity" && !taskList[0].topActivity!!.className.contains("GrantPermissionsActivity")) {
                            OngoingCallService.NotificationServiceState.inviteLink = data.getString(INVITE_LINK)
                        }

                        runOnUiThread {
                            tvCallStatus?.text = "Ringing..."
                        }
                        isReadyForConnectionIOS = true
                        val startCallJson = JSONObject()
                        startCallJson.put(IS_SILENT, true)
                        startCallJson.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.JitsiCallType.OFFER_CONFERENCE.toString())
                        startCallJson.put(USER_ID, videoCallModel?.userId)
                        startCallJson.put(CHANNEL_ID, videoCallModel?.channelId)
                        startCallJson.put(MESSAGE_TYPE, WebRTCCallConstants.VIDEO_CALL)
                        startCallJson.put(CALL_TYPE, videoCallModel?.callType)
                        startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, getDeviceDetails())
                        startCallJson.put(MESSAGE_UNIQUE_ID, muid)
                        startCallJson.put(INVITE_LINK, inviteLink)
                        SocketConnection.sendMessage(startCallJson)
                        Log.e("Video_CONF-->", startCallJson.toString())
                    } else if (data.getString(VIDEO_CALL_TYPE) == FuguAppConstant.JitsiCallType.ANSWER_CONFERENCE.toString()
                            && (inviteLink == data.getString(INVITE_LINK))
                            && !data.getString(USER_ID).equals(videoCallModel?.userId)) {
                        if (data.getString(INVITE_LINK).contains("meet.google.com")) {
                            joinHangoutsCall(inviteLink)
                            finish()
                        } else {
                            isReadyForConnectionIOS = true
                            isReadyForConnection = true
                            val linkArray = data.getString("invite_link").replace("#config.startWithVideoMuted=true", "").split("/")
                            if (!OngoingCallService.NotificationServiceState.isConferenceServiceRunning) {
                                OngoingCallService.NotificationServiceState.isConferenceServiceRunning = true
                                val answerIntent = Intent(this@HangoutsCallActivity, VideoConfActivity::class.java)
                                answerIntent.action = Intent.ACTION_ANSWER
                                answerIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                answerIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                answerIntent.putExtra("room_name", linkArray[linkArray.size - 1])
                                answerIntent.putExtra("call_type", videoCallModel?.callType)
                                answerIntent.putExtra("no_answer", "no_answer")
                                answerIntent.putExtra(INVITE_LINK, inviteLink)
                                answerIntent.putExtra(MESSAGE_UNIQUE_ID, muid)
                                answerIntent.putExtra(CHANNEL_ID, videoCallModel?.channelId)
                                startActivity(answerIntent)
                                finish()
                            }
                        }
                    } else if (data.getString(VIDEO_CALL_TYPE) == FuguAppConstant.JitsiCallType.REJECT_CONFERENCE.toString()
                            && (inviteLink == data.getString(INVITE_LINK))
                            && !data.getString(USER_ID).equals(videoCallModel?.userId)) {
                        mInitiateStartCallTimer?.cancel()
                        runOnUiThread {
                            tvCallStatus?.text = "Call Rejected"
                        }
                        mediaPlayer?.stop()
                        val aa = AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                        mediaPlayer = if (videoCallModel?.callType.equals("VIDEO")) {
                            MediaPlayer.create(this@HangoutsCallActivity, R.raw.busy_tone)
                        } else {
                            MediaPlayer.create(this@HangoutsCallActivity, R.raw.busy_tone, aa, 1)
                        }

                        mediaPlayer?.isLooping = false
                        mediaPlayer?.start()

                        Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 3000)
                    } else if (data.getString(VIDEO_CALL_TYPE) == FuguAppConstant.JitsiCallType.USER_BUSY_CONFERENCE.toString()
                            && (inviteLink == data.getString(INVITE_LINK))
                            && !data.getString(USER_ID).equals(videoCallModel?.userId)) {
                        runOnUiThread {
                            tvCallStatus?.text = "User Busy on another Call"
                            if (mediaPlayer != null) {
                                mediaPlayer?.stop()
                                mediaPlayer = MediaPlayer.create(this@HangoutsCallActivity, R.raw.busy_tone)
                                mediaPlayer?.isLooping = false
                                mediaPlayer?.start()
                            }
                        }
                        Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 3000)

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onPresent(messageJson: String) {}

            override fun onMessageSent(messageJson: String) {}

            override fun onThreadMessageSent(messageJson: String) {}

            override fun onMessageReceived(messageJson: String) {}

            override fun onTypingStarted(messageJson: String) {}

            override fun onTypingStopped(messageJson: String) {}

            override fun onThreadMessageReceived(messageJson: String) {}

            override fun onReadAll(messageJson: String) {}

            override fun onPinChat(messageJson: String) {}

            override fun onUnpinChat(messageJson: String) {}

            override fun onPollVoteReceived(messageJson: String) {}

            override fun onReactionReceived(messageJson: String) {}

            override fun onVideoCallReceived(messageJson: String) {}

            override fun onAudioCallReceived(messageJson: String) {}

            override fun onChannelSubscribed() {}

            override fun onConnect() {}

            override fun onDisconnect() {}

            override fun onConnectError(socket: Socket, message: String) {}


            override fun onErrorReceived(messageJson: String) {
                runOnUiThread {

                    try {
                        val fayeVideoCallResponse = Gson().fromJson(messageJson, FayeVideoCallResponse::class.java)
                        if (fayeVideoCallResponse.statusCode == 415) {
                            Log.e("Jitsi Error", messageJson)
                            mInitiateStartCallTimer?.cancel()
                            val name = tvCalledPersonName?.text
                            Toast.makeText(this@HangoutsCallActivity, "$name does not have the updated app, Calling using old SDK... ", Toast.LENGTH_LONG).show()
                            object : CountDownTimer(2000, 1000) {
                                override fun onFinish() {
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }

                                override fun onTick(millisUntilFinished: Long) {
                                }

                            }.start()
                        }
                    } catch (e: Exception) {

                    }

                }


            }

            override fun onTaskAssigned(messageJson: String) {}

            override fun onMeetScheduled(messageJson: String) {}

            override fun onUpdateNotificationCount(messageJson: String) {}
        })

    }

    private fun initCall(link: String) {
        Handler(Looper.getMainLooper()).post {
            mInitiateStartCallTimer = object : CountDownTimer(300000, 2000) {
                override fun onFinish() {
                    finish()
                }

                override fun onTick(millisUntilFinished: Long) {
                    if (initialCalls <= maxCalls && !isReadyForConnection) {
                        if (initialCalls == 1) {
                            startCall(false, link)
                        } else {
                            startCall(true, link)
                        }
                        initialCalls += 1
                    }

                    if (initialCallsIOS <= maxCalls && !isReadyForConnectionIOS) {
                        if (initialCallsIOS == 1) {
                            startCallIOS(true, link)
                        } else {
                            startCallIOS(true, link)
                        }
                        initialCallsIOS += 1
                    }
                }

            }.start()
        }
    }

    override fun onStop() {
        isReadyForConnection = true
        isReadyForConnectionIOS = true
        mInitiateStartCallTimer?.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = savedAudioMode
        audioManager.abandonAudioFocus(null)
        isReadyForConnection = true
        isReadyForConnectionIOS = true
        mInitiateStartCallTimer?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun startCall(isSilent: Boolean, link: String) {
        val startCallJson = JSONObject()
        startCallJson.put(IS_SILENT, isSilent)
        startCallJson.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.JitsiCallType.START_CONFERENCE.toString())
        startCallJson.put(USER_ID, videoCallModel?.userId)
        startCallJson.put(CHANNEL_ID, videoCallModel?.channelId)
        startCallJson.put(MESSAGE_TYPE, WebRTCCallConstants.VIDEO_CALL)
        startCallJson.put(CALL_TYPE, videoCallModel?.callType)
        startCallJson.put(MESSAGE_UNIQUE_ID, muid)
        startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, getDeviceDetails())
        inviteLink = if (videoCallModel?.callType!! == "AUDIO") {
            link
        } else {
            link
        }
        startCallJson.put(INVITE_LINK, inviteLink)
        SocketConnection.sendMessage(startCallJson)
    }

    private fun startCallIOS(isSilent: Boolean, link: String) {
        val startCallJson = JSONObject()
        startCallJson.put(IS_SILENT, isSilent)
        startCallJson.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.JitsiCallType.START_CONFERENCE_IOS.toString())
        startCallJson.put(USER_ID, videoCallModel?.userId)
        startCallJson.put(CHANNEL_ID, videoCallModel?.channelId)
        startCallJson.put(MESSAGE_TYPE, WebRTCCallConstants.VIDEO_CALL)
        startCallJson.put(CALL_TYPE, videoCallModel?.callType)
        startCallJson.put(MESSAGE_UNIQUE_ID, muid)
        startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, getDeviceDetails())
        inviteLink = if (videoCallModel?.callType!! == "AUDIO") {
            link
        } else {
            link
        }
        startCallJson.put(INVITE_LINK, inviteLink)
        SocketConnection.sendMessage(startCallJson)
    }

    private fun hangupCall(isSilent: Boolean) {
        val startCallJson = JSONObject()
        startCallJson.put(IS_SILENT, isSilent)
        startCallJson.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.JitsiCallType.HUNGUP_CONFERENCE.toString())
        startCallJson.put(USER_ID, videoCallModel?.userId)
        startCallJson.put(CHANNEL_ID, videoCallModel?.channelId)
        startCallJson.put(MESSAGE_TYPE, WebRTCCallConstants.VIDEO_CALL)
        startCallJson.put(CALL_TYPE, videoCallModel?.callType)
        startCallJson.put(MESSAGE_UNIQUE_ID, muid)
        startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, getDeviceDetails())
        startCallJson.put(INVITE_LINK, inviteLink)
        SocketConnection.sendMessage(startCallJson)
    }


    private fun initViews() {
        ivHangUp = findViewById(R.id.ivHangUp)
        tvCallStatus = findViewById(R.id.tvCallStatus)
        tvCalledPersonName = findViewById(R.id.tvCalledPersonName)
        tvCallType = findViewById(R.id.tvCallType)
        tvDummyImage = findViewById(R.id.tvDummyImage)
        ivCalledPersonImage = findViewById(R.id.ivCalledPersonImage)
        ivHangUp?.setOnClickListener {
            hangupCall(true)
            finish()
        }
    }

    private fun initiateOutgoingRinging() {
        mListener = AudioManager.OnAudioFocusChangeListener { }
        Handler().postDelayed({
            val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            savedAudioMode = audio.mode
            audio.mode = AudioManager.MODE_IN_COMMUNICATION
            val aa = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            mediaPlayer = if (videoCallModel?.callType.equals("VIDEO")) {
                MediaPlayer.create(this, R.raw.ringing)
            } else {
                MediaPlayer.create(this, R.raw.ringing, aa, 1)
            }
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }, 100)
    }

    private fun getDeviceDetails(): JSONObject {
        val devicePayload = JSONObject()
        devicePayload.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this))
        devicePayload.put(DEVICE_TYPE, ANDROID_USER)
        devicePayload.put(APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(DEVICE_DETAILS, CommonData.deviceDetails(this))
        return devicePayload
    }
}
