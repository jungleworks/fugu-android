package com.skeleton.mvp.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.receiver.HungUpBroadcast
import com.skeleton.mvp.service.OngoingCallService
import com.skeleton.mvp.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject

class IncomingJitsiCallActivity : AppCompatActivity(), CallTouchListener.OnCallItemTouchListener {

    private var tvCallType: AppCompatTextView? = null
    private var tvIncomingPersonName: AppCompatTextView? = null
    private var tvDummyImage: AppCompatTextView? = null
    private var ivIncomingPersonImage: CircleImageView? = null
    private var ivIncomingPersonImageBig: AppCompatImageView? = null
    var llReject: LinearLayout? = null
    var llReply: LinearLayout? = null
    var llAnswer: LinearLayout? = null
    var answerRoot: LinearLayout? = null
    private val animTime = 150
    var answerImagesList = ArrayList<AppCompatImageView>()
    var rejectImagesList = ArrayList<AppCompatImageView>()
    var replyImagesList = ArrayList<AppCompatImageView>()
    val set = AnimatorSet()
    var tvRejectCall: AppCompatTextView? = null
    var tvReplyCall: AppCompatTextView? = null
    var tvAnswerCall: AppCompatTextView? = null
    var ivRejectCall: AppCompatImageView? = null
    var ivReplyCall: AppCompatImageView? = null
    var ivAnswerCall: AppCompatImageView? = null
    var image: ImageView? = null
    var countDownTimer: CountDownTimer? = null
    var dialog: Dialog? = null
    var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_jitsi_call)
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        initViews()

        when {
            intent?.getStringExtra(CALL_TYPE).equals("VIDEO") -> {
                tvCallType?.text = "$APP_NAME_SHORT Video Call"
            }
            intent?.getStringExtra(CALL_TYPE).equals("HANGOUTS") -> {
                tvCallType?.text = "$APP_NAME_SHORT Hangouts Call"
            }
            else -> {
                tvCallType?.text = "$APP_NAME_SHORT Audio Call"
            }
        }
        val name = intent?.getStringExtra(FULL_NAME)
        tvIncomingPersonName?.text = if (!TextUtils.isEmpty(name)) {
            name
        } else
            ""
        if (!TextUtils.isEmpty(intent?.getStringExtra(USER_THUMBNAIL_IMAGE))) {
            Glide.with(this)
                    .load(intent?.getStringExtra(USER_THUMBNAIL_IMAGE))
                    .into(ivIncomingPersonImage!!)

            Glide.with(this)
                    .load(intent?.getStringExtra(USER_THUMBNAIL_IMAGE))
                    .into(ivIncomingPersonImageBig!!)
        } else {
            try {
                tvDummyImage!!.text = if (!TextUtils.isEmpty(name))
                    name!!.substring(0, 1)
                else
                    ""
                when (intent!!.getLongExtra(CHANNEL_ID, 0L) % 5) {
                    1L -> tvDummyImage!!.setBackgroundResource(R.drawable.ring_purple)
                    2L -> tvDummyImage!!.setBackgroundResource(R.drawable.ring_teal)
                    3L -> tvDummyImage!!.setBackgroundResource(R.drawable.ring_red)
                    4L -> tvDummyImage!!.setBackgroundResource(R.drawable.ring_indigo)
                    else -> {
                        tvDummyImage!!.setBackgroundResource(R.drawable.ring_red)
                    }
                }
                ivIncomingPersonImage!!.visibility = View.GONE
                tvDummyImage!!.visibility = View.VISIBLE
            } catch (e: java.lang.Exception) {
                Log.e("DummyImageException", e.message ?: "")
                e.printStackTrace()
            }
        }
        val audio: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.isSpeakerphoneOn = true
        mediaPlayer = MediaPlayer.create(this@IncomingJitsiCallActivity, R.raw.video_call_ringtone)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        val audio: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.abandonAudioFocus(null)
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoConferenceHungup)
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
                IntentFilter(VIDEO_CONFERENCE_HUNGUP_INTENT)
        )
    }

    private val mVideoConferenceHungup = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mediaPlayer?.stop()
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

    private fun initViews() {
        tvCallType = findViewById(R.id.tvCallType)
        tvIncomingPersonName = findViewById(R.id.tvIncomingPersonName)
        ivIncomingPersonImage = findViewById(R.id.ivIncomingPersonImage)
        ivIncomingPersonImageBig = findViewById(R.id.ivIncomingPersonImageBig)
        tvDummyImage = findViewById(R.id.tvDummyImage)
        llReject = findViewById(R.id.llReject)
        llReply = findViewById(R.id.llReply)
        llAnswer = findViewById(R.id.llAnswer)

        tvReplyCall = findViewById(R.id.tvReply)
        tvRejectCall = findViewById(R.id.tvReject)
        tvAnswerCall = findViewById(R.id.tvAnswer)

        answerRoot = findViewById(R.id.answerRoot)

        answerImagesList.add(findViewById(R.id.pick_call_arrow_up_one))
        answerImagesList.add(findViewById(R.id.pick_call_arrow_up_two))
        answerImagesList.add(findViewById(R.id.pick_call_arrow_up_three))
        answerImagesList.add(findViewById(R.id.pick_call_arrow_up_four))

        replyImagesList.add(findViewById(R.id.reply_call_arrow_up_one))
        replyImagesList.add(findViewById(R.id.reply_call_arrow_up_two))
        replyImagesList.add(findViewById(R.id.reply_call_arrow_up_three))
        replyImagesList.add(findViewById(R.id.reply_call_arrow_up_four))

        rejectImagesList.add(findViewById(R.id.reject_call_arrow_up_one))
        rejectImagesList.add(findViewById(R.id.reject_call_arrow_up_two))
        rejectImagesList.add(findViewById(R.id.reject_call_arrow_up_three))
        rejectImagesList.add(findViewById(R.id.reject_call_arrow_up_four))

        ivRejectCall = findViewById(R.id.ivRejectCall)
        ivAnswerCall = findViewById(R.id.ivAnswerCall)
        ivReplyCall = findViewById(R.id.ivReplyCall)

        startAcceptAnimation(answerImagesList)
        startRejectAnimation(rejectImagesList)
        startReplyAnimation(replyImagesList)
        ivAnswerCall?.setOnTouchListener(CallTouchListener(answerRoot, ivAnswerCall, this))
        ivReplyCall?.setOnTouchListener(CallTouchListener(answerRoot, ivReplyCall, this))
        ivRejectCall?.setOnTouchListener(CallTouchListener(answerRoot, ivRejectCall, this))
        onShakeImage()

        object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                Log.e("Timer", "Done")
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {
            }

        }.start()
    }

    private fun startAcceptAnimation(imagesList: ArrayList<AppCompatImageView>) {

        try {
            set.cancel()
        } catch (e: Exception) {

        }

        for (image in imagesList) {
            image.clearAnimation()
        }
        val arrowOneFadeIn = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 0f, 1f)
        arrowOneFadeIn.duration = animTime.toLong()
        val arrowTwoFadeIn = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 0f, 1f)
        arrowTwoFadeIn.setDuration(animTime.toLong()).startDelay = animTime.toLong()
        val arrowOneFadeOut = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 1f, 0f)
        arrowOneFadeOut.setDuration(animTime.toLong()).startDelay = 375
        val arrowThreeFadeIn = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 0f, 1f)
        arrowThreeFadeIn.setDuration(animTime.toLong()).startDelay = 300
        val arrowTwoFadeOut = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 1f, 0f)
        arrowTwoFadeOut.setDuration(animTime.toLong()).startDelay = 525
        val arrowFourFadeIn = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 0f, 1f)
        arrowFourFadeIn.setDuration(animTime.toLong()).startDelay = 450
        val arrowThreeFadeOut = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 1f, 0f)
        arrowThreeFadeOut.setDuration(animTime.toLong()).startDelay = 675
        val arrowFourFadeOut = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 1f, 0f)
        arrowFourFadeOut.setDuration(animTime.toLong()).startDelay = 825
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                set.start()
            }
        })

        set.play(arrowOneFadeIn)
        set.play(arrowTwoFadeIn)
        set.play(arrowOneFadeOut)
        set.play(arrowThreeFadeIn)
        set.play(arrowTwoFadeOut)
        set.play(arrowFourFadeIn)
        set.play(arrowThreeFadeOut)
        set.play(arrowFourFadeOut)
        set.start()
    }

    private fun startReplyAnimation(imagesList: ArrayList<AppCompatImageView>) {

        try {
            set.cancel()
        } catch (e: Exception) {

        }

        for (image in imagesList) {
            image.clearAnimation()
        }
        val arrowOneFadeIn = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 0f, 1f)
        arrowOneFadeIn.duration = animTime.toLong()
        val arrowTwoFadeIn = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 0f, 1f)
        arrowTwoFadeIn.setDuration(animTime.toLong()).startDelay = animTime.toLong()
        val arrowOneFadeOut = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 1f, 0f)
        arrowOneFadeOut.setDuration(animTime.toLong()).startDelay = 375
        val arrowThreeFadeIn = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 0f, 1f)
        arrowThreeFadeIn.setDuration(animTime.toLong()).startDelay = 300
        val arrowTwoFadeOut = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 1f, 0f)
        arrowTwoFadeOut.setDuration(animTime.toLong()).startDelay = 525
        val arrowFourFadeIn = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 0f, 1f)
        arrowFourFadeIn.setDuration(animTime.toLong()).startDelay = 450
        val arrowThreeFadeOut = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 1f, 0f)
        arrowThreeFadeOut.setDuration(animTime.toLong()).startDelay = 675
        val arrowFourFadeOut = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 1f, 0f)
        arrowFourFadeOut.setDuration(animTime.toLong()).startDelay = 825
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                set.start()
            }
        })

        set.play(arrowOneFadeIn)
        set.play(arrowTwoFadeIn)
        set.play(arrowOneFadeOut)
        set.play(arrowThreeFadeIn)
        set.play(arrowTwoFadeOut)
        set.play(arrowFourFadeIn)
        set.play(arrowThreeFadeOut)
        set.play(arrowFourFadeOut)
        set.start()
    }

    private fun startRejectAnimation(imagesList: ArrayList<AppCompatImageView>) {

        try {
            set.cancel()
        } catch (e: Exception) {

        }

        for (image in imagesList) {
            image.clearAnimation()
        }
        val arrowOneFadeIn = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 0f, 1f)
        arrowOneFadeIn.duration = animTime.toLong()
        val arrowTwoFadeIn = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 0f, 1f)
        arrowTwoFadeIn.setDuration(animTime.toLong()).startDelay = animTime.toLong()
        val arrowOneFadeOut = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 1f, 0f)
        arrowOneFadeOut.setDuration(animTime.toLong()).startDelay = 375
        val arrowThreeFadeIn = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 0f, 1f)
        arrowThreeFadeIn.setDuration(animTime.toLong()).startDelay = 300
        val arrowTwoFadeOut = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 1f, 0f)
        arrowTwoFadeOut.setDuration(animTime.toLong()).startDelay = 525
        val arrowFourFadeIn = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 0f, 1f)
        arrowFourFadeIn.setDuration(animTime.toLong()).startDelay = 450
        val arrowThreeFadeOut = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 1f, 0f)
        arrowThreeFadeOut.setDuration(animTime.toLong()).startDelay = 675
        val arrowFourFadeOut = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 1f, 0f)
        arrowFourFadeOut.setDuration(animTime.toLong()).startDelay = 825
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                set.start()
            }
        })

        set.play(arrowOneFadeIn)
        set.play(arrowTwoFadeIn)
        set.play(arrowOneFadeOut)
        set.play(arrowThreeFadeIn)
        set.play(arrowTwoFadeOut)
        set.play(arrowFourFadeIn)
        set.play(arrowThreeFadeOut)
        set.play(arrowFourFadeOut)
        set.start()
    }

    private fun onShakeImage() {
        val shake: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.shake)
        val slideUp: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up_call)
        image = findViewById(R.id.ivAnswerCall)
        image?.startAnimation(slideUp)

        Handler().postDelayed({
            image?.clearAnimation()
            image?.startAnimation(shake)
        }, 1500)

        Handler().postDelayed({ onShakeImage() }, 2700)

    }

    override fun onItemTouch(swipeView: View) {

        when (swipeView.id) {
            R.id.ivAnswerCall -> {
                llAnswer?.visibility = View.INVISIBLE
                image?.clearAnimation()
            }
            R.id.ivRejectCall -> {
                llAnswer?.visibility = View.INVISIBLE
                llReply?.visibility = View.INVISIBLE
                llReject?.visibility = View.VISIBLE
                tvRejectCall?.visibility = View.VISIBLE
                tvReplyCall?.visibility = View.INVISIBLE
                tvAnswerCall?.visibility = View.INVISIBLE
                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(2000, 1000) {
                    override fun onFinish() {
                        llAnswer?.visibility = View.VISIBLE
                        llReject?.visibility = View.INVISIBLE
                        llReply?.visibility = View.INVISIBLE
                        tvRejectCall?.visibility = View.INVISIBLE
                        tvReplyCall?.visibility = View.INVISIBLE
                        tvAnswerCall?.visibility = View.VISIBLE
                    }

                    override fun onTick(millisUntilFinished: Long) {
                    }

                }
                countDownTimer?.start()
            }

            R.id.ivReplyCall -> {
                llAnswer?.visibility = View.INVISIBLE
                llReply?.visibility = View.VISIBLE
                llReject?.visibility = View.INVISIBLE
                tvReplyCall?.visibility = View.VISIBLE
                tvAnswerCall?.visibility = View.INVISIBLE
                tvRejectCall?.visibility = View.INVISIBLE
                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(2000, 1000) {
                    override fun onFinish() {
                        llAnswer?.visibility = View.VISIBLE
                        llReject?.visibility = View.INVISIBLE
                        llReply?.visibility = View.INVISIBLE
                        tvReplyCall?.visibility = View.INVISIBLE
                        tvAnswerCall?.visibility = View.VISIBLE
                        tvRejectCall?.visibility = View.INVISIBLE
                    }

                    override fun onTick(millisUntilFinished: Long) {
                    }

                }
                countDownTimer?.start()
            }
        }
    }

    override fun onItemTouchReleased(swipeView: View) {
        when (swipeView.id) {
            R.id.ivAnswerCall -> {
                llAnswer?.visibility = View.VISIBLE
                onShakeImage()
            }
        }
    }

    fun addCallExtras(inviteLink: String, isOldMeetApp: Boolean): Intent {
        var videoIntent = Intent(this@IncomingJitsiCallActivity, VideoConfActivity::class.java)
        if (inviteLink.contains("meet.google.com")) {
            videoIntent = getHangoutsIntent(inviteLink, isOldMeetApp)
            stopCallForegroundService(false)
        }
        videoIntent.putExtra("base_url", CommonData.getConferenceUrl())
        videoIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        videoIntent.putExtra(INVITE_LINK, inviteLink)
        videoIntent.putExtra(APP_SECRET_KEY, intent?.getStringExtra(APP_SECRET_KEY))
        videoIntent.putExtra(CHANNEL_ID, intent?.getLongExtra(CHANNEL_ID, -1L))
        videoIntent.putExtra(ROOM_NAME, intent?.getStringExtra(ROOM_NAME))
        videoIntent.putExtra(CALL_TYPE, intent?.getStringExtra(CALL_TYPE))
        return videoIntent
    }

    override fun onItemAnswered(swipeView: View) {
        when (swipeView.id) {
            R.id.ivAnswerCall -> {
                val inviteLink = intent.getStringExtra(INVITE_LINK) ?: ""
                val videoIntent = addCallExtras(inviteLink, false)
                if (inviteLink.contains("meet.google.com")) {
                    emitAnswerEvent(videoIntent, this@IncomingJitsiCallActivity)
                    stopService(Intent(this@IncomingJitsiCallActivity, OngoingCallService::class.java))
                    stopCallForegroundService(false)
                }
                try {
                    startActivity(videoIntent)
                } catch (e: Exception) {
                    try {
                        startActivity(addCallExtras(inviteLink, true))
                    } catch (e: Exception) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(inviteLink)
                        startActivity(intent)
                    }
                }
                finish()
            }
            R.id.ivReplyCall -> {
                runOnUiThread {
                    dialog = Dialog(this@IncomingJitsiCallActivity, android.R.style.Theme_Translucent_NoTitleBar)
                    dialog?.setContentView(R.layout.dialog_call_options)
                    val lp = dialog?.window!!.attributes
                    lp.dimAmount = 0.5f
                    dialog?.window!!.attributes = lp
                    dialog?.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                    dialog?.setCancelable(true)
                    dialog?.setCanceledOnTouchOutside(true)

                    val optionOne = dialog?.findViewById<AppCompatTextView>(R.id.option_one)
                    val optionTwo = dialog?.findViewById<AppCompatTextView>(R.id.option_two)
                    val optionThree = dialog?.findViewById<AppCompatTextView>(R.id.option_three)
                    val optionFour = dialog?.findViewById<AppCompatTextView>(R.id.option_four)
                    val optionCustom = dialog?.findViewById<AppCompatTextView>(R.id.custom_action)
                    val chatIntent = Intent(this@IncomingJitsiCallActivity, ChatActivity::class.java)
                    val conversation = FuguConversation()
                    conversation.businessName = ""
                    conversation.label = ""
                    conversation.isOpenChat = true
                    conversation.channelId = intent?.getLongExtra(CHANNEL_ID, -1L)
                    conversation.chat_type = 2
                    conversation.userName = StringUtil.toCamelCase(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fullName)
                    conversation.userId = java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)
                    conversation.enUserId = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId
                    conversation.unreadCount = 0
                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, Gson().toJson(conversation, FuguConversation::class.java))
                    chatIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    optionOne?.setOnClickListener {
                        startChatActivity(chatIntent, "Can't talk now. What's up?")
                    }
                    optionTwo?.setOnClickListener {
                        startChatActivity(chatIntent, "I'll call you right back.")
                    }
                    optionThree?.setOnClickListener {
                        startChatActivity(chatIntent, "I'll call you later.")
                    }
                    optionFour?.setOnClickListener {
                        startChatActivity(chatIntent, "Can't talk now. Call me later?")
                    }
                    optionCustom?.setOnClickListener {
                        startChatActivity(chatIntent, "")
                    }
                    dialog?.show()
                }
            }
            R.id.ivRejectCall -> {
                val hungupIntent = Intent(applicationContext, HungUpBroadcast::class.java)
                hungupIntent.putExtra("action", "rejectCall")
                hungupIntent.putExtra(DEVICE_PAYLOAD, getDeviceDetails().toString())
                hungupIntent.putExtra(INVITE_LINK, intent?.getStringExtra(INVITE_LINK))
                hungupIntent.putExtra(CHANNEL_ID, intent?.getLongExtra(CHANNEL_ID, -1L))
                hungupIntent.putExtra(APP_SECRET_KEY, intent?.getStringExtra(APP_SECRET_KEY))
                sendBroadcast(hungupIntent)
                finish()
            }
        }
    }

    private fun startChatActivity(intent1: Intent, text: String) {
        dialog?.dismiss()
        val hungupIntent = Intent(applicationContext, HungUpBroadcast::class.java)
        hungupIntent.putExtra("action", "rejectCall")
        hungupIntent.putExtra(DEVICE_PAYLOAD, getDeviceDetails().toString())
        hungupIntent.putExtra(INVITE_LINK, intent?.getStringExtra(INVITE_LINK))
        hungupIntent.putExtra(CHANNEL_ID, intent?.getLongExtra(CHANNEL_ID, -1L))
        sendBroadcast(hungupIntent)

        CommonData.setCustomText(text)
        Handler().postDelayed({
            finish()
            intent1.putExtra("SendCustomMessage", text)
            startActivity(intent1)
        }, 300)
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
