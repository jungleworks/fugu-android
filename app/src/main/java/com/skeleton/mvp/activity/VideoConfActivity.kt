package com.skeleton.mvp.activity

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_ID
import com.skeleton.mvp.constant.FuguAppConstant.INVITE_LINK
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.CommonResponse
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.service.ConferenceCallService
import com.skeleton.mvp.service.OngoingCallService
import com.skeleton.mvp.ui.customview.CustomRatingBar
import com.skeleton.mvp.utils.emitAnswerEvent
import com.skeleton.mvp.utils.stopCallForegroundService
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL

class VideoConfActivity : AppCompatActivity() {

    private var wiredHeadsetReceiver: BroadcastReceiver? = null
    private val STATE_UNPLUGGED = 0
    private val STATE_PLUGGED = 1
    private val HAS_NO_MIC = 0
    private val HAS_MIC = 1
    private var isWirelessHeadSetConnected = false
    private var dialog: Dialog? = null
    private var savedAudioMode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_black)
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        stopCallForegroundService(false)
        // Initialize default options for Jitsi Meet conferences.
        OngoingCallService.NotificationServiceState.isConferenceConnected = true
        if (intent.hasExtra(CHANNEL_ID) && !intent.hasExtra("no_answer")) {
            emitAnswerEvent(intent, this@VideoConfActivity)
        }
        val serverURL: URL = getServerUrl()
        initCall(serverURL)
        stopService(Intent(this, ConferenceCallService::class.java))
//        if (intent.hasExtra(CHANNEL_ID)) {
        startOngoingCallService()
//        }
        try {
            apiUpdateConferenceCall()
        } catch (e: Exception) {

        }

        finish()
        wiredHeadsetReceiver = WiredHeadsetReceiver()
    }

    private fun apiUpdateConferenceCall() {
        val commonParams = CommonParams.Builder()
        if (intent.hasExtra(INVITE_LINK)) {
            commonParams.add("calling_link", intent.getStringExtra(INVITE_LINK))
        } else {
            commonParams.add("calling_link", intent.data.toString().replace("fuguChat://", "https://"))
        }
        try {
            commonParams.add("user_id_in_call", CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].userId)
        } catch (e: java.lang.Exception) {

        }

        RestClient.getApiInterface(true).updateConferenceCall(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<CommonResponse>() {
                    override fun success(t: CommonResponse?) {
                    }

                    override fun failure(error: APIError?) {
                    }

                })
    }

    private fun startOngoingCallService() {
        val startIntent = Intent(this, OngoingCallService::class.java)
        startIntent.action = "com.officechat.notification.start"
        startIntent.putExtra(INVITE_LINK, intent.getStringExtra(INVITE_LINK))
        startIntent.putExtra(CHANNEL_ID, intent.getLongExtra(CHANNEL_ID, -1L))
        ContextCompat.startForegroundService(this, startIntent)
    }

    private fun initCall(serverURL: URL) {
        val userInfo = JitsiMeetUserInfo()
        try {
            userInfo.displayName = CommonData.getCommonResponse().data.userInfo.fullName
            try {
                userInfo.avatar = URL(CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].userImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            userInfo.displayName = "Fellow User"
        }


        val roomName: String = if (!TextUtils.isEmpty(intent.getStringExtra("room_name"))) {
            intent.getStringExtra("room_name")!!
        } else {
            intent?.data?.lastPathSegment ?: ""
        }
        val isVideoMuted = (intent.hasExtra(INVITE_LINK) && intent.getStringExtra(INVITE_LINK)!!.contains("config.startWithVideoMuted=true"))
                || roomName.contains("config.startWithVideoMuted=true") || (intent.hasExtra("is_video_muted") && intent.getBooleanExtra("is_video_muted", false))
        val isAudioMuted = (intent.hasExtra(INVITE_LINK) && intent.getStringExtra(INVITE_LINK)!!.contains("config.startWithAudioMuted=true"))
                || roomName.contains("config.startWithAudioMuted=true") || (intent.hasExtra("is_audio_muted") && intent.getBooleanExtra("is_audio_muted", false))
        try {
            if ((intent.hasExtra("call_type") && intent.getStringExtra("call_type") == "AUDIO")) {
                val defaultOptions = JitsiMeetConferenceOptions.Builder()
                        .setServerURL(serverURL)
                        .setWelcomePageEnabled(false)
                        .setAudioOnly(true)
                        .setFeatureFlag("chat.enabled", false)
//                        .setFeatureFlag("recording.enabled", false)
                        .setUserInfo(userInfo)
                        .build()
                JitsiMeet.setDefaultConferenceOptions(defaultOptions)
            } else {
                if (isAudioMuted || isVideoMuted) {
                    val defaultOptions = JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .setAudioOnly(false)
                            .setAudioMuted(isAudioMuted)
                            .setVideoMuted(isVideoMuted)
                            .setFeatureFlag("chat.enabled", false)
//                            .setFeatureFlag("recording.enabled", false)
                            .setUserInfo(userInfo)
                            .build()
                    JitsiMeet.setDefaultConferenceOptions(defaultOptions)
                } else {
                    val defaultOptions = JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .setAudioOnly(false)
                            .setFeatureFlag("chat.enabled", false)
//                            .setFeatureFlag("recording.enabled", false)
                            .setUserInfo(userInfo)
                            .build()
                    JitsiMeet.setDefaultConferenceOptions(defaultOptions)
                }
            }
        } catch (e: Exception) {
            val defaultOptions = JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    .setWelcomePageEnabled(false)
                    .setAudioOnly(false)
                    .setFeatureFlag("chat.enabled", false)
//                    .setFeatureFlag("recording.enabled", false)
                    .setUserInfo(userInfo)
                    .build()
            JitsiMeet.setDefaultConferenceOptions(defaultOptions)
        }
//        roomName = roomName.replace("#config.startWithVideoMuted=true", "")
        val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(roomName)
                .build()
        JitsiMeetActivity.launch(this, options)
    }

    private fun getServerUrl(): URL {
        val serverURL: URL
        try {
//            if (CommonData.getSocketUrl().equals(FuguAppConstant.SOCKET_TEST_SERVER)) {
//                serverURL = URL(FuguAppConstant.CONFERENCING_TEST)
//            } else {
            serverURL = if (!TextUtils.isEmpty(intent?.data?.host)) {
                URL("https://" + intent?.data?.host)
            } else if (intent.hasExtra("invite_link"))
                URL("https://" + intent.getStringExtra("invite_link")!!.split("/")[2])
            else {
                URL(com.skeleton.mvp.fugudatabase.CommonData.getConferenceUrl())
            }
//            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }
        return serverURL

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this@VideoConfActivity).registerReceiver(mCallTerminated, IntentFilter("CALL TERMINATED"))

    }

    private val mCallTerminated = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            showFeedBackDialog()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@VideoConfActivity).unregisterReceiver(mCallTerminated)
    }

    private fun showFeedBackDialog() {
        try {
            dialog = Dialog(this@VideoConfActivity, R.style.Theme_AppCompat_Translucent)
            dialog?.setContentView(R.layout.activity_calling_feed_back)
            val lp = dialog?.window!!.attributes
            lp.dimAmount = 0.5f
            dialog?.window!!.attributes = lp
            dialog?.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog?.setCancelable(false)
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            val ratingBar: CustomRatingBar? = dialog?.findViewById(R.id.ratingBar)
            val tvRating: TextView? = dialog?.findViewById(R.id.tvRating)
            val etFeedback: EditText? = dialog?.findViewById(R.id.etFeedback)
            val btnNotNow: AppCompatButton? = dialog?.findViewById(R.id.btnNotNow)
            val btnSubmit: AppCompatButton? = dialog?.findViewById(R.id.btnSubmit)

            ratingBar?.setOnScoreChanged { score ->

                if (score >= 0f) {
                    tvRating?.visibility = View.VISIBLE
                } else {
                    tvRating?.visibility = View.GONE
                }
                when (score) {
                    1f -> {
                        tvRating?.text = "Very Bad"
                    }
                    2f -> {
                        tvRating?.text = "Bad"
                    }
                    3f -> {
                        tvRating?.text = "Average"
                    }
                    4f -> {
                        tvRating?.text = "Good"
                    }
                    5f -> {
                        tvRating?.text = "Excellent"
                    }
                    else -> {

                    }
                }
            }

            btnNotNow?.setOnClickListener {
                dialog?.dismiss()
                IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus = false
                finish()
            }

            btnSubmit?.setOnClickListener {
                apiSendFeedback(ratingBar?.score!!, FuguAppConstant.Feedback.VIDEO_CONFERENCE.toString(), etFeedback?.text.toString().trim())
                dialog = null

            }

            dialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun apiSendFeedback(rating: Float, type: String, feedback: String) {

        val jsonObject = JSONObject()
//        val gson = Gson()
//        val json = gson.toJson(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo())
        try {
            jsonObject.put("workspace_name", CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceName)
            jsonObject.put("workspace_id", CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceId)
            jsonObject.put("type", type)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val commonParams = CommonParams.Builder()
        if (!TextUtils.isEmpty(feedback)) {
            commonParams.add("feedback", feedback)
        }
        commonParams.add("workspace_id", CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceId)
        commonParams.add("type", type)
        commonParams.add("rating", rating.toInt())
        commonParams.add("extra_details", jsonObject.toString())

        RestClient.getApiInterface(true).sendFeedback(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<CommonResponse>() {
                    override fun success(commonResponse: CommonResponse) {
                        Toast.makeText(this@VideoConfActivity, "Feedback Submitted", Toast.LENGTH_LONG).show()
                        IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus = false
                        finishAndRemoveTask()
                    }

                    override fun failure(error: APIError) {
                        Toast.makeText(this@VideoConfActivity, error.message, Toast.LENGTH_LONG).show()
                        IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus = false
                        finishAndRemoveTask()
                    }
                })
    }

    internal inner class WiredHeadsetReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra("state", STATE_UNPLUGGED)
//            val microphone = intent.getIntExtra("microphone", HAS_NO_MIC)
//            val name = intent.getStringExtra("name")

            isWirelessHeadSetConnected = state == STATE_PLUGGED
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            savedAudioMode = am.mode
            am.mode = AudioManager.MODE_IN_CALL
            if (isWirelessHeadSetConnected) {
                am.isSpeakerphoneOn = false
                am.isWiredHeadsetOn = true
            } else {
                am.isSpeakerphoneOn = true
            }
        }
    }
}
