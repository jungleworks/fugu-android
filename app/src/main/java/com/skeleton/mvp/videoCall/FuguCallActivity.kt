package com.skeleton.mvp.videoCall

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.CallFeedbackActivity
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.activity.VideoCallActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_NAME
import com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.fragment.SwitchToConfBottomSheetFragment
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.model.FuguSearchResult
import com.skeleton.mvp.model.turncredsresponse.TurnCredsResponse
import com.skeleton.mvp.service.VideoCallService
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.util.ProximitySensorController
import com.skeleton.mvp.utils.CallTouchListener
import com.skeleton.mvp.utils.FuguUtils.Companion.randomVideoConferenceLink
import com.skeleton.mvp.utils.StringUtil
import com.skeleton.mvp.utils.UniqueIMEIID
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.CALL_STATUS
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.DISCONNECTING
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.OFFER_TO_RECEIVE_AUDIO
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.OFFER_TO_RECEIVE_VIDEO
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.ONGOING_AUDIO_CALL
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.ONGOING_VIDEO_CALL
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.REJECTED
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.RINGING
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.USER_BUSY
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.VIDEO_CALL_HUNGUP_FROM_NOTIFICATION
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_video_call.*
import org.json.JSONArray
import org.json.JSONObject
import org.webrtc.*
import kotlin.system.exitProcess

/**
 * Created by rajatdhamija
 * 20/09/18.
 */

class FuguCallActivity : BaseActivity(), View.OnClickListener, WebRTCFayeCallbacks, WebRTCCallCallbacks, CallTouchListener.OnCallItemTouchListener {


    var userIdsSearch = ArrayList<Long>()
    var multiMemberAddGroupMap = java.util.LinkedHashMap<Long, FuguSearchResult>()
    var ivCalledPersonImage: CircleImageView? = null
    var ivIncomingPersonImage: CircleImageView? = null
    var ivIncomingPersonImageBig: AppCompatImageView? = null
    var tvCalledPersonName: AppCompatTextView? = null
    var tvCallType: AppCompatTextView? = null
    var tvCallTypeIncoming: AppCompatTextView? = null
    var ivHangUp: AppCompatImageView? = null
    var ivMuteAudio: AppCompatImageView? = null
    var ivSpeaker: AppCompatImageView? = null
    var ivSwitchCamera: AppCompatImageView? = null
    var ivMuteVideo: AppCompatImageView? = null
    var localSurfaceView: SurfaceViewRenderer? = null
    var tvIncomingPersonName: AppCompatTextView? = null
    var ivRejectCall: AppCompatImageView? = null
    var ivAnswerCall: AppCompatImageView? = null
    var remoteSurfaceview: SurfaceViewRenderer? = null
    var activitylaunchState: String? = ""
    var signal: Signal? = null
    private var rootEglBase: EglBase? = null
    var connection: Connection? = null
    private var sdpConstraints: MediaConstraints? = null
    private var videoStream: MediaStream? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var remoteVideoTrack: VideoTrack? = null
    private var videoCapturer: VideoCapturer? = null
    private var videoCallModel: VideoCallModel? = null
    private var remoteVideoStream: MediaStream? = null
    private var videoOfferjson: JSONObject? = null
    private var isAudioEnabled = true
    private var isVideoEnabled = true
    private var isFrontFacingCamera = true
    private var isLocalViewSmall = true
    private var isCallOptionsVisible = true
    var mediaPlayer: MediaPlayer? = null
    var callStatus: String = ""
    var isAlreadyHungUp = false
    var mBounded = false
    var videoCallService: VideoCallService? = null
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var callDisconnectTimer: CountDownTimer? = null
    private var wiredHeadsetReceiver: BroadcastReceiver? = null
    private val STATE_UNPLUGGED = 0
    private val STATE_PLUGGED = 1
    private val HAS_NO_MIC = 0
    private val HAS_MIC = 1
    private var isWirelessHeadSetConnected = false
    private var tvCallTimer: AppCompatTextView? = null
    private var ivBack: AppCompatImageView? = null
    private var ivSwitchToConf: AppCompatImageView? = null
    private var outgoingCallLayout: RelativeLayout? = null
    private var incomingCallLayout: RelativeLayout? = null
    private var vibrate: Vibrator? = null
    private var isOnSpeaker = false
    private var isOnBluetooth = false
    private var mProximityController: ProximitySensorController? = null
    private var mPowerManager: PowerManager? = null
    private var mWakeLoack: PowerManager.WakeLock? = null
    private var mProximityWakeLock: PowerManager.WakeLock? = null
    private var mPartialWakeLock: PowerManager.WakeLock? = null
    private var field = 0x00000020
    private var isCallAnswered = false
    private var mainRoot: RelativeLayout? = null
    private var isAnswerClicked = false

    private var wirelessContext: Context? = null
    private var wirelessIntent: Intent? = null
    private var otherUserId = -1L

    var ivReplyCall: AppCompatImageView? = null
    var llReject: LinearLayout? = null
    var llReply: LinearLayout? = null
    var llAnswer: LinearLayout? = null
    var answerRoot: LinearLayout? = null
    private val animTime = 150
    var answerImagesList = ArrayList<AppCompatImageView>()
    var rejectImagesList = ArrayList<AppCompatImageView>()
    var replyImagesList = ArrayList<AppCompatImageView>()

    var tvRejectCall: AppCompatTextView? = null
    var tvReplyCall: AppCompatTextView? = null
    var tvAnswerCall: AppCompatTextView? = null

    var image: ImageView? = null

    var countDownTimer: CountDownTimer? = null
    val set = AnimatorSet()
    var stopAudioVideo = false

    var ivBluetooth: AppCompatImageView? = null
    var rlCallingButtons: RelativeLayout? = null

    var llConnectivityIssues: LinearLayout? = null

    var ivHungupCallTimer: CountDownTimer? = null

    var hasAlreadyInitializedViews = false
    var mListener: AudioManager.OnAudioFocusChangeListener? = null
    var lowerCallOptions: LinearLayout? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        mListener = AudioManager.OnAudioFocusChangeListener { }
        if (intent.hasExtra("videoCallModel")) {
            videoCallModel = intent.extras?.getParcelable("videoCallModel")
            val turnCreds = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().turnCredentials
            videoCallModel?.turnServers = turnCreds.iceServers.turn as ArrayList<String>
            videoCallModel?.stunServers = turnCreds.iceServers.stun as ArrayList<String>
            videoCallModel?.turnCredential = turnCreds.credentials
            videoCallModel?.turnApiKey = turnCreds.turnApiKey
            videoCallModel?.turnUserName = turnCreds.username
            activitylaunchState = videoCallModel?.activityLaunchState
            askForPermissions()
            initializeViews()
            if (WebRTCCallConstants.AcitivityLaunchState.OTHER.toString() == activitylaunchState) {
                outgoingCallLayout?.visibility = View.GONE
                incomingCallLayout?.visibility = View.VISIBLE
                tvIncomingPersonName?.visibility = View.VISIBLE
                ivIncomingPersonImage?.visibility = View.VISIBLE
                ivIncomingPersonImageBig?.visibility = View.VISIBLE
                ivAnswerCall?.visibility = View.VISIBLE
                ivRejectCall?.visibility = View.VISIBLE
                tvIncomingPersonName?.text = videoCallModel?.channelName
                initiateIncomingRinging()
                if (videoCallModel?.callType == "VIDEO") {
                    tvCallTypeIncoming?.text = FuguAppConstant.APP_NAME_SHORT + " Video Call"
                    ivMuteVideo?.visibility = View.VISIBLE
                    ivSwitchCamera?.visibility = View.VISIBLE
                    ivSpeaker?.visibility = View.GONE
//                    if (videoCallModel?.callType.equals("VIDEO")) {
//                        tvCallTimer?.visibility = View.GONE
//                    }
                } else {
                    tvCallTypeIncoming?.text = FuguAppConstant.APP_NAME_SHORT + " Voice Call"
                    ivMuteVideo?.visibility = View.GONE
                    ivSwitchCamera?.visibility = View.GONE
                    ivSpeaker?.visibility = View.VISIBLE
                }
                tvCalledPersonName?.text = videoCallModel?.channelName


                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(CenterCrop(), RoundedCorners(1000))

                val optionsBigImage = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(CenterCrop())
                if (!isFinishing) {
                    Glide.with(this)
                            .asBitmap()
                            .apply(options)
                            .load(videoCallModel?.userThumbnailImage)
                            .into(ivIncomingPersonImage!!)
                }
                if (!isFinishing) {
                    Glide.with(this)
                            .asBitmap()
                            .apply(optionsBigImage)
                            .load(videoCallModel?.userThumbnailImage)
                            .into(ivIncomingPersonImageBig!!)
                }
            } else if (WebRTCCallConstants.AcitivityLaunchState.SELF.toString() == activitylaunchState) {
                outgoingCallLayout?.visibility = View.VISIBLE
                incomingCallLayout?.visibility = View.GONE
                tvCalledPersonName?.visibility = View.VISIBLE
                ivCalledPersonImage?.visibility = View.VISIBLE
                ivMuteAudio?.visibility = View.VISIBLE
                if (videoCallModel?.callType == "VIDEO") {
                    tvCallType?.text = FuguAppConstant.APP_NAME_SHORT + " Video Call"
                    ivMuteVideo?.visibility = View.VISIBLE
                    ivSwitchCamera?.visibility = View.VISIBLE
                    ivSpeaker?.visibility = View.GONE
//                    if (videoCallModel?.callType.equals("VIDEO")) {
//                        tvCallTimer?.visibility = View.GONE
//                    }
                } else {
                    tvCallType?.text = FuguAppConstant.APP_NAME_SHORT + " Voice Call"
                    ivMuteVideo?.visibility = View.GONE
                    ivSwitchCamera?.visibility = View.GONE
                    ivSpeaker?.visibility = View.VISIBLE
                }

                ivHangUp?.visibility = View.VISIBLE

//                if (videoCallModel?.callType == "VIDEO") {
//                    localSurfaceView?.visibility = View.VISIBLE
//                }

                initiateOutgoingRinging()
                callStatus = WebRTCCallConstants.CallStatus.OUTGOING_CALL.toString()
                com.skeleton.mvp.data.db.CommonData.setCallStatus(callStatus)

                tvCalledPersonName?.text = videoCallModel?.channelName


                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(CenterCrop(), RoundedCorners(1000))

                if (!isFinishing) {
                    Glide.with(this)
                            .asBitmap()
                            .apply(options)
                            .load(videoCallModel?.userThumbnailImage)
                            .into(ivCalledPersonImage!!)
                }
            }
        } else {
            askForPermissions()
        }

    }

    private fun fetchIntentData() {
        videoCallModel = intent.extras?.getParcelable("videoCallModel")
        val turnCreds = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().turnCredentials
        videoCallModel?.turnServers = turnCreds.iceServers.turn as ArrayList<String>
        videoCallModel?.stunServers = turnCreds.iceServers.stun as ArrayList<String>
        videoCallModel?.turnCredential = turnCreds.credentials
        videoCallModel?.turnApiKey = turnCreds.turnApiKey
        videoCallModel?.turnUserName = turnCreds.username
        videoCallService?.setVideoModel(videoCallModel)
        activitylaunchState = videoCallModel?.activityLaunchState
//        videoCallService?.createWebRTCCallConnection()
        val devicePayload = JSONObject()
        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this))
        signal = Signal(videoCallModel?.userId, videoCallModel?.signalUniqueId, videoCallModel?.fullName,
                videoCallModel?.turnApiKey, videoCallModel?.turnUserName, videoCallModel?.turnCredential
                , videoCallModel?.stunServers, videoCallModel?.turnServers, devicePayload, videoCallModel?.callType!!)
        videoCallService?.setSignalModel(signal)
        if (videoCallModel?.callType == "VIDEO") {
            Handler().postDelayed({
                if (isCallOptionsVisible) {
                    isCallOptionsVisible = !isCallOptionsVisible
                    hidecallOptionsAnimation()
                }
            }, 3000)
        }
        when (activitylaunchState) {
            WebRTCCallConstants.AcitivityLaunchState.SELF.toString() -> {
                outgoingCallLayout?.visibility = View.VISIBLE
                incomingCallLayout?.visibility = View.GONE
                tvCalledPersonName?.visibility = View.VISIBLE
                ivCalledPersonImage?.visibility = View.VISIBLE
                ivMuteAudio?.visibility = View.VISIBLE
                if (videoCallModel?.callType == "VIDEO") {
                    tvCallType?.text = FuguAppConstant.APP_NAME_SHORT + " Video Call"
                    ivMuteVideo?.visibility = View.VISIBLE
                    ivSwitchCamera?.visibility = View.VISIBLE
                    ivSpeaker?.visibility = View.GONE
//                    mainRoot?.setBackgroundColor(Color.BLACK)

//                    if (videoCallModel?.callType.equals("VIDEO")) {
//                        tvCallTimer?.visibility = View.GONE
//                    }
                } else {
                    tvCallType?.text = FuguAppConstant.APP_NAME_SHORT + " Voice Call"
                    ivMuteVideo?.visibility = View.GONE
                    ivSwitchCamera?.visibility = View.GONE
                    ivSpeaker?.visibility = View.VISIBLE
                }

                ivHangUp?.visibility = View.VISIBLE

                if (videoCallModel?.callType == "VIDEO") {
                    localSurfaceView?.visibility = View.VISIBLE
                }

//                initiateOutgoingRinging()
                callStatus = WebRTCCallConstants.CallStatus.OUTGOING_CALL.toString()
                com.skeleton.mvp.data.db.CommonData.setCallStatus(callStatus)

                tvCalledPersonName?.text = videoCallModel?.channelName


                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(CenterCrop(), RoundedCorners(1000))

                if (!isFinishing) {
                    Glide.with(this)
                            .asBitmap()
                            .apply(options)
                            .load(videoCallModel?.userThumbnailImage)
                            .into(ivCalledPersonImage!!)
                }
            }
            WebRTCCallConstants.AcitivityLaunchState.OTHER.toString() -> {
                outgoingCallLayout?.visibility = View.GONE
                incomingCallLayout?.visibility = View.VISIBLE
                tvIncomingPersonName?.visibility = View.VISIBLE
                ivIncomingPersonImage?.visibility = View.VISIBLE
                ivIncomingPersonImageBig?.visibility = View.VISIBLE
                ivAnswerCall?.visibility = View.VISIBLE
                ivRejectCall?.visibility = View.VISIBLE
                tvIncomingPersonName?.text = videoCallModel?.channelName
//                initiateIncomingRinging()
                if (videoCallModel?.callType == "VIDEO") {
                    tvCallTypeIncoming?.text = FuguAppConstant.APP_NAME_SHORT + " Video Call"
                    ivMuteVideo?.visibility = View.VISIBLE
                    ivSwitchCamera?.visibility = View.VISIBLE
                    ivSpeaker?.visibility = View.GONE
//                    mainRoot?.setBackgroundColor(Color.BLACK)
//                    if (videoCallModel?.callType.equals("VIDEO")) {
//                        tvCallTimer?.visibility = View.GONE
//                    }
                } else {
                    tvCallTypeIncoming?.text = FuguAppConstant.APP_NAME_SHORT + " Voice Call"
                    ivMuteVideo?.visibility = View.GONE
                    ivSwitchCamera?.visibility = View.GONE
                    ivSpeaker?.visibility = View.VISIBLE
                }
                callStatus = WebRTCCallConstants.CallStatus.INCOMING_CALL.toString()
                com.skeleton.mvp.data.db.CommonData.setCallStatus(callStatus)
                tvCalledPersonName?.text = videoCallModel?.channelName


                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(CenterCrop(), RoundedCorners(1000))

                val optionsBigImage = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(CenterCrop())
                if (!isFinishing) {
                    Glide.with(this)
                            .asBitmap()
                            .apply(options)
                            .load(videoCallModel?.userThumbnailImage)
                            .into(ivIncomingPersonImage!!)
                }
                if (!isFinishing) {
                    Glide.with(this)
                            .asBitmap()
                            .apply(optionsBigImage)
                            .load(videoCallModel?.userThumbnailImage)
                            .into(ivIncomingPersonImageBig!!)
                }
            }
        }
        videoCallService?.createWebRTCSignallingConnection(videoCallModel, signal)
    }

    /**
     * Initialization of Views
     */
    private fun initializeViews() {
        llConnectivityIssues = findViewById(R.id.llConnectivityIssues)
        ivCalledPersonImage = findViewById(R.id.ivCalledPersonImage)
        ivIncomingPersonImage = findViewById(R.id.ivIncomingPersonImage)
        ivIncomingPersonImageBig = findViewById(R.id.ivIncomingPersonImageBig)
        tvCalledPersonName = findViewById(R.id.tvCalledPersonName)
        tvCallTimer = findViewById(R.id.tvCallTimer)
        tvCallType = findViewById(R.id.tvCallType)
        tvCallTypeIncoming = findViewById(R.id.tvCallTypeIncoming)
        ivHangUp = findViewById(R.id.ivHangUp)
        ivMuteAudio = findViewById(R.id.ivMuteAudio)
        ivSpeaker = findViewById(R.id.ivSpeaker)
        ivSwitchCamera = findViewById(R.id.ivSwitchCamera)
        ivMuteVideo = findViewById(R.id.ivMuteVideo)
        localSurfaceView = findViewById(R.id.localSurfaceView)
        tvIncomingPersonName = findViewById(R.id.tvIncomingPersonName)
        ivRejectCall = findViewById(R.id.ivRejectCall)
        ivAnswerCall = findViewById(R.id.ivAnswerCall)
        remoteSurfaceview = findViewById(R.id.remoteSurfaceview)
        ivBack = findViewById(R.id.ivBack)
        ivSwitchToConf = findViewById(R.id.ivSwitchToConf)
        outgoingCallLayout = findViewById(R.id.outgoingCallLayout)
        incomingCallLayout = findViewById(R.id.incomingCallLayout)
        mainRoot = findViewById(R.id.mainRoot)
        wiredHeadsetReceiver = WiredHeadsetReceiver()
        remoteSurfaceview?.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)

        if (videoCallModel?.callType.equals("AUDIO")) {
            lowerCallOptions = findViewById(R.id.lowerCallOptions)
            lowerCallOptions?.setBackgroundColor(Color.parseColor("#ffffff"))
        }

        ivReplyCall = findViewById(R.id.ivReplyCall)
        ivBluetooth = findViewById(R.id.ivBluetooth)
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

        startAcceptAnimation(answerImagesList)
        startRejectAnimation(rejectImagesList)
        startReplyAnimation(replyImagesList)
        rlCallingButtons = findViewById(R.id.rlCallingButtons)
        ivAnswerCall?.setOnTouchListener(CallTouchListener(answerRoot, ivAnswerCall, this))
        ivReplyCall?.setOnTouchListener(CallTouchListener(answerRoot, ivReplyCall, this))
        ivRejectCall?.setOnTouchListener(CallTouchListener(answerRoot, ivRejectCall, this))
        onShakeImage()
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

    /**
     * Add Click listeners to views
     */
    private fun initializeClickListeners() {
        ivHangUp?.setOnClickListener(this)
        ivMuteAudio?.setOnClickListener(this)
        ivSwitchCamera?.setOnClickListener(this)
        ivMuteVideo?.setOnClickListener(this)
        localSurfaceView?.setOnClickListener(this)
        ivRejectCall?.setOnClickListener(this)
        ivAnswerCall?.setOnClickListener(this)
        remoteSurfaceview?.setOnClickListener(this)
        ivBack?.setOnClickListener(this)
        ivSwitchToConf?.setOnClickListener(this)
        ivSpeaker?.setOnClickListener(this)
        mainRoot?.setOnClickListener(this)
        ivBluetooth?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.ivSwitchToConf -> {
                val newFragment = SwitchToConfBottomSheetFragment.newInstance(0, this@FuguCallActivity, videoCallModel?.otherUserId!!)
                newFragment.show(supportFragmentManager, "SwitchToConfBottomSheetFragment")
            }

            R.id.ivHangUp -> {
                tvCallTimer?.text = DISCONNECTING
                videoCallService?.isReadyForConnection = true
                videoCallService?.hungUpCall(true)
                if (ivHungupCallTimer == null) {
                    ivHungupCallTimer = object : CountDownTimer(2000, 1000) {
                        override fun onFinish() {
                            onHungupSent()
                        }

                        override fun onTick(millisUntilFinished: Long) {
                        }

                    }.start()

                }
            }
            R.id.ivMuteAudio -> {
                videoStream?.audioTracks?.get(0)?.setEnabled(!isAudioEnabled)
                val drawable = if (isAudioEnabled) R.drawable.ic_mute_microphone_disabled else R.drawable.ic_mute_microphone_no_bg
                ivMuteAudio?.setImageResource(drawable)
                isAudioEnabled = !isAudioEnabled
            }
            R.id.ivSwitchCamera -> {
                switchCameraRecorder()
            }
            R.id.ivMuteVideo -> {
                videoStream?.videoTracks?.get(0)?.setEnabled(!isVideoEnabled)
                val drawable = if (isVideoEnabled) R.drawable.ic_mute_video_disabled else R.drawable.ic_mute_video_no_bg
                ivMuteVideo?.setImageResource(drawable)
                isVideoEnabled = !isVideoEnabled
            }
            R.id.ivSpeaker -> {

                if (isOnSpeaker) {
                    isOnSpeaker = false
                    isOnBluetooth = false
                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    am.mode = AudioManager.STREAM_MUSIC
                    am.stopBluetoothSco()
                    am.isSpeakerphoneOn = false
                    am.isBluetoothScoOn = false
                    ivSpeaker?.setImageResource(R.drawable.ic_audio_speaker_no_bg)
                    mProximityController = null
                    setUpProximitySensor()
                } else if (isOnBluetooth) {
                    isOnSpeaker = true
                    isOnBluetooth = false
                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    am.mode = AudioManager.STREAM_MUSIC
                    am.stopBluetoothSco()
                    am.isBluetoothScoOn = false
                    am.isSpeakerphoneOn = true
                    val drawable = R.drawable.bluetooth_without_bg
                    ivBluetooth?.setImageResource(drawable)
                    ivSpeaker?.setImageResource(R.drawable.ic_audio_speaker_disabled)
                    unregisterProximitySensor()
                } else {
                    isOnSpeaker = true
                    isOnBluetooth = false
                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    am.mode = AudioManager.STREAM_MUSIC
                    am.stopBluetoothSco()
                    am.isBluetoothScoOn = false
                    am.isSpeakerphoneOn = true
                    val drawable = R.drawable.bluetooth_without_bg
                    ivBluetooth?.setImageResource(drawable)
                    ivSpeaker?.setImageResource(R.drawable.ic_audio_speaker_disabled)
                    unregisterProximitySensor()
                }
            }

            R.id.ivBluetooth -> {
                if (isOnBluetooth) {
                    isOnSpeaker = false
                    isOnBluetooth = false
                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    am.mode = AudioManager.STREAM_MUSIC
                    am.stopBluetoothSco()
                    am.isBluetoothScoOn = false
                    am.isSpeakerphoneOn = !videoCallModel?.callType.equals("AUDIO")
                    val drawable = R.drawable.bluetooth_without_bg
                    ivBluetooth?.setImageResource(drawable)
                    unregisterProximitySensor()
                } else if (isOnSpeaker) {
                    Toast.makeText(this@FuguCallActivity, "Connecting to Bluetooth", Toast.LENGTH_SHORT).show()
                    isOnSpeaker = false
                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    am.mode = AudioManager.STREAM_MUSIC
                    setBluetooth(am)
                    ivSpeaker?.setImageResource(R.drawable.ic_audio_speaker_no_bg)
                    mProximityController = null
                    unregisterProximitySensor()
                } else {
                    Toast.makeText(this@FuguCallActivity, "Connecting to Bluetooth", Toast.LENGTH_SHORT).show()
                    isOnSpeaker = false
                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    am.mode = AudioManager.STREAM_MUSIC
                    setBluetooth(am)
                    ivSpeaker?.setImageResource(R.drawable.ic_audio_speaker_no_bg)
                    mProximityController = null
                    unregisterProximitySensor()
                }
            }

            R.id.localSurfaceView -> {
                if (isLocalViewSmall) {
                    localVideoTrack?.removeSink(localSurfaceView)
                    remoteVideoTrack?.removeSink(remoteSurfaceview)
                    localVideoTrack?.addSink(remoteSurfaceview)
                    remoteVideoTrack?.addSink(localSurfaceView)
                } else {
                    localVideoTrack?.removeSink(remoteSurfaceview)
                    remoteVideoTrack?.removeSink(localSurfaceView)
                    localVideoTrack?.addSink(localSurfaceView)
                    remoteVideoTrack?.addSink(remoteSurfaceview)
                }
                isLocalViewSmall = !isLocalViewSmall
            }
            R.id.remoteSurfaceview -> {
                if (videoCallModel?.callType == "VIDEO") {
                    if (isCallOptionsVisible) {
                        if (callDisconnectTimer != null) {
                            callDisconnectTimer?.cancel()
                        }
                        hidecallOptionsAnimation()
                    } else {
                        callDisconnectTimer = object : CountDownTimer(3000, 1000) {

                            override fun onTick(millisUntilFinished: Long) {}

                            override fun onFinish() {
                                if (isCallOptionsVisible) {
                                    hidecallOptionsAnimation()
                                    isCallOptionsVisible = !isCallOptionsVisible
                                }
                            }
                        }.start()
                        showCallOptionsAnimation()
                    }
                    isCallOptionsVisible = !isCallOptionsVisible
                }
            }
            R.id.mainRoot -> {
                if (videoCallModel?.callType == "VIDEO") {
                    if (isCallOptionsVisible) {
                        if (callDisconnectTimer != null) {
                            callDisconnectTimer?.cancel()
                        }
                        hidecallOptionsAnimation()
                    } else {
                        callDisconnectTimer = object : CountDownTimer(3000, 1000) {

                            override fun onTick(millisUntilFinished: Long) {}

                            override fun onFinish() {
                                if (isCallOptionsVisible) {
                                    hidecallOptionsAnimation()
                                    isCallOptionsVisible = !isCallOptionsVisible
                                }
                            }
                        }.start()
                        showCallOptionsAnimation()
                    }
                    isCallOptionsVisible = !isCallOptionsVisible
                }
            }
            R.id.ivBack -> onBackPressed()
            else -> {

            }
        }
    }

    override fun onBackPressed() {
//        if (!callStatus.equals(WebRTCCallConstants.CallStatus.IN_CALL.toString())) {
//            ivHangUp.performClick()
//            callHungup()
//        }
//        hasAlreadyInitializedViews = false
        if (callStatus.equals(WebRTCCallConstants.CallStatus.IN_CALL.toString())) {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        unregisterrecievers()
        if (!isOnSpeaker) {
            setUpProximitySensor()
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mVideoCallReciever,
                IntentFilter(FuguAppConstant.VIDEO_CALL_INTENT))
        if (wiredHeadsetReceiver != null) {

            val filter1 = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
            val filter2 = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
            val filter3 = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            val filter4 = IntentFilter(Intent.ACTION_HEADSET_PLUG)

            registerReceiver(wiredHeadsetReceiver, filter1)
            registerReceiver(wiredHeadsetReceiver, filter2)
            registerReceiver(wiredHeadsetReceiver, filter3)
            registerReceiver(wiredHeadsetReceiver, filter4)
        }
    }

    private fun unregisterrecievers() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoCallReciever)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(wiredHeadsetReceiver!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        unregisterProximitySensor()
        try {
            mProximityController = null
            mWakeLoack?.release()
        } catch (e: Exception) {
        }
        try {
            mProximityController = null
            mPartialWakeLock?.release()
        } catch (e: Exception) {
        }
        try {
            mProximityController = null
            mProximityWakeLock?.release()
        } catch (e: Exception) {
        }
    }

    private val mVideoCallReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            videoCallService?.onBroadCastrecieved(intent)
        }
    }


    private fun hidecallOptionsAnimation() {
        val hungupGone = ObjectAnimator.ofFloat(ivHangUp, "translationY", 0f, 500f)
        hungupGone.duration = 300

        val muteAudioGone = ObjectAnimator.ofFloat(ivMuteAudio, "translationY", 0f, 250f)
        muteAudioGone.duration = 180

        val muteVideoGone = ObjectAnimator.ofFloat(ivMuteVideo, "translationY", 0f, 250f)
        muteVideoGone.duration = 180

        val switchCameraGone = ObjectAnimator.ofFloat(ivSwitchCamera, "translationY", 0f, 250f)
        switchCameraGone.duration = 180

        val bluetoothGone = ObjectAnimator.ofFloat(ivBluetooth, "translationY", 0f, 250f)
        bluetoothGone.duration = 180

        val localView = ObjectAnimator.ofFloat(localSurfaceView, "translationY", 0f, 250f)
        localView.duration = 180

        hungupGone.start()
        muteAudioGone.start()
        muteVideoGone.start()
        switchCameraGone.start()
        bluetoothGone.start()
        localView.start()
    }

    private fun showCallOptionsAnimation() {
        val hungupVisible = ObjectAnimator.ofFloat(ivHangUp, "translationY", 500f, 0f)
        hungupVisible.duration = 300

        val muteAudioVisible = ObjectAnimator.ofFloat(ivMuteAudio, "translationY", 250f, 0f)
        muteAudioVisible.duration = 180

        val muteVideoVisible = ObjectAnimator.ofFloat(ivMuteVideo, "translationY", 250f, 0f)
        muteVideoVisible.duration = 180

        val switchCameraVisible = ObjectAnimator.ofFloat(ivSwitchCamera, "translationY", 250f, 0f)
        switchCameraVisible.duration = 180

        val bluetoothVisible = ObjectAnimator.ofFloat(ivBluetooth, "translationY", 250f, 0f)
        bluetoothVisible.duration = 180

        val localView = ObjectAnimator.ofFloat(localSurfaceView, "translationY", 250f, 0f)
        localView.duration = 180

        hungupVisible.start()
        muteAudioVisible.start()
        muteVideoVisible.start()
        switchCameraVisible.start()
        bluetoothVisible.start()
        localView.start()
    }

    private fun stopServiceCloseActivity(showFeedback: Boolean) {
        stopForegroundService(false)
        hangupVideoCall()



        Handler(Looper.getMainLooper()).postDelayed({
            if (showFeedback) {
                val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                am.mode = AudioManager.MODE_RINGTONE
                if (am.isBluetoothScoOn) {
                    am.startBluetoothSco()
                    am.stopBluetoothSco()
                }
                am.abandonAudioFocus(mListener)
                val intent = Intent(applicationContext, CallFeedbackActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val taskList = mngr.getRunningTasks(10)
            if (taskList[0].baseActivity!!.className.equals("com.skeleton.mvp.videoCall.FuguCallActivity")) {
                finishAndRemoveTask()
                finishAffinity()
                if (showFeedback) {
                } else {
                    exitProcess(0)
                }
            } else {
                finish()
            }
        }, 300)
    }

    private fun stopServiceAndStartConf() {
        stopForegroundService(false)
        hangupVideoCall()
        Handler().postDelayed({
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.mode = AudioManager.MODE_RINGTONE
            if (am.isBluetoothScoOn) {
                am.startBluetoothSco()
                am.stopBluetoothSco()
            }
            am.abandonAudioFocus(mListener)
            finish()
        }, 300)
    }


    private fun stopServiceAndCloseConnection() {


        stopForegroundService(true)
        hangupVideoCall()
        Handler().postDelayed({
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.mode = AudioManager.MODE_RINGTONE
            if (am.isBluetoothScoOn) {
                am.startBluetoothSco()
                am.stopBluetoothSco()
            }
            am.abandonAudioFocus(mListener)
            val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val taskList = mngr.getRunningTasks(10)
            if (taskList[0].baseActivity!!.className.equals("com.skeleton.mvp.videoCall.FuguCallActivity")) {
                finishAndRemoveTask()
                finishAffinity()
                exitProcess(0)
            } else {
                finish()
            }
        }, 500)
    }


    private fun switchCameraRecorder() {
        try {
            if (videoCallService?.getPeerconnection() != null) {
                ivSwitchCamera?.setOnClickListener(null)
                ivSwitchCamera?.alpha = 0.5f
                if (isLocalViewSmall) {
                    localVideoTrack?.removeSink(localSurfaceView)
                } else {
                    localVideoTrack?.removeSink(remoteSurfaceview)
                }
                videoCallService?.getPeerconnection()?.removeStream(videoStream)
                try {
                    videoCapturer?.stopCapture()

                    Handler().postDelayed({
                        ivSwitchCamera?.setOnClickListener(this)
                        ivSwitchCamera?.alpha = 1f
                        ivMuteVideo?.setImageResource(R.drawable.ic_mute_video_no_bg)
                        ivMuteAudio?.setImageResource(R.drawable.ic_mute_microphone_no_bg)
                        isVideoEnabled = true
                        isAudioEnabled = true
                        val videoGrabberAndroid = createVideoGrabber(!isFrontFacingCamera)
                        val videoSource = peerConnectionFactory?.createVideoSource(false)
                        val cameraVideoCapturer = videoGrabberAndroid as CameraVideoCapturer
                        localVideoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)
                        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase?.eglBaseContext)
                        cameraVideoCapturer.initialize(surfaceTextureHelper, this@FuguCallActivity, videoSource?.capturerObserver)
                        cameraVideoCapturer.startCapture(1000, 1000, 30)
                        sdpConstraints = MediaConstraints()
                        sdpConstraints?.mandatory?.add(MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_AUDIO, "true"))
                        sdpConstraints?.mandatory?.add(MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_VIDEO, "true"))
                        val audioSource = peerConnectionFactory?.createAudioSource(sdpConstraints)
                        localAudioTrack = peerConnectionFactory?.createAudioTrack("101", audioSource)
                        videoStream = peerConnectionFactory?.createLocalMediaStream("102")
                        if (videoCallModel?.callType == "VIDEO") {
                            videoStream?.addTrack(localVideoTrack)
                        }
                        videoStream?.addTrack(localAudioTrack)
                        videoCallService?.getPeerconnection()?.addStream(videoStream)
                        if (isLocalViewSmall) {
                            localVideoTrack?.addSink(localSurfaceView)
                        } else {
                            localVideoTrack?.addSink(remoteSurfaceview)
                        }
                        isFrontFacingCamera = !isFrontFacingCamera
                    }, 1000)
                } catch (e: Exception) {

                }
            }
        } catch (e: Exception) {
        }
    }


    override fun onIceCandidateRecieved(jsonObject: JSONObject?) {
        //TODO Ui changes
        videoStream = peerConnectionFactory?.createLocalMediaStream("102")
        if (videoCallModel?.callType == "VIDEO") {
            videoStream?.addTrack(localVideoTrack)
        }
        videoStream?.addTrack(localAudioTrack)
        videoCallService?.getPeerconnection()?.addStream(videoStream)
        videoCallService?.saveIceCandidate(jsonObject)
    }

    override fun onVideoOfferRecieved(jsonObject: JSONObject?) {
        //TODO ui changes
//        sdpConstraints = MediaConstraints()
//        sdpConstraints?.mandatory?.add(MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_AUDIO, "true"))
//        sdpConstraints?.mandatory?.add(MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_VIDEO, "true"))
//        connection = Connection(videoCallModel?.stunServers, videoCallModel?.turnServers,
//                sdpConstraints, videoCallModel?.turnUserName, videoCallModel?.turnCredential, peerConnectionFactory)
//        connection?.sdpConstraints = sdpConstraints
//        videoCallService?.setConnectionModel(connection)
//        videoCallService?.createPeerConnection(connection)
        if (videoStream == null) {
            videoStream = peerConnectionFactory?.createLocalMediaStream("102")
            videoStream?.addTrack(localAudioTrack)
            if (videoCallModel?.callType.equals("VIDEO")) {
                videoStream?.addTrack(localVideoTrack)
            }

        }
        videoCallService?.getPeerconnection()?.addStream(videoStream)
        videoOfferjson = jsonObject
        runOnUiThread {
            Handler().postDelayed({
                if (wirelessContext != null && wirelessIntent != null) {
                    WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
                }
            }, 500)
            Handler().postDelayed({
                if (wirelessContext != null && wirelessIntent != null) {
                    WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
                }
            }, 1500)
            onFayeConnected()
        }
    }

    override fun onVideoOfferScreenSharingRecieved(jsonObject: JSONObject?) {
        if (!isLocalViewSmall) {
            localVideoTrack?.removeSink(remoteSurfaceview)
            remoteVideoTrack?.removeSink(localSurfaceView)
            localVideoTrack?.addSink(localSurfaceView)
            remoteVideoTrack?.addSink(remoteSurfaceview)
            isLocalViewSmall = !isLocalViewSmall
        }

        videoCallService?.saveOfferAndAnswer(jsonObject)
        Handler().postDelayed({
            if (wirelessContext != null && wirelessIntent != null) {
                WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
            }
        }, 500)
        Handler().postDelayed({
            if (wirelessContext != null && wirelessIntent != null) {
                WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
            }
        }, 1500)
    }

    override fun onVideoAnswerRecieved(jsonObject: JSONObject?) {
        runOnUiThread {
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
                if (videoCallModel?.callType == "VIDEO") {
                    ivBack?.visibility = View.VISIBLE
                    ivSwitchToConf?.visibility = View.VISIBLE
                    tvCallTimer?.visibility = View.GONE
//                    callerRippleView?.visibility = View.GONE
//                    callerRippleView?.stopRippleAnimation()
                    tvCalledPersonName?.visibility = View.GONE
                    ivCalledPersonImage?.visibility = View.GONE
                } else {
//                    callerRippleView?.stopRippleAnimation()
                }
            }
        }
    }

    override fun onReadyToConnectRecieved(jsonObject: JSONObject?) {
//        sdpConstraints = MediaConstraints()
//        sdpConstraints?.mandatory?.add(MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_AUDIO, "true"))
//        sdpConstraints?.mandatory?.add(MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_VIDEO, "true"))
//        connection = Connection(videoCallModel?.stunServers, videoCallModel?.turnServers,
//                sdpConstraints, videoCallModel?.turnUserName, videoCallModel?.turnCredential, peerConnectionFactory)
//        videoCallService?.setConnectionModel(connection)
//        videoCallService?.createPeerConnection(connection)
        videoStream = peerConnectionFactory?.createLocalMediaStream("102")
        if (videoCallModel?.callType == "VIDEO") {
            videoStream?.addTrack(localVideoTrack)
        }
        videoStream?.addTrack(localAudioTrack)
        videoStream?.audioTracks!![0].setEnabled(true)
        videoCallService?.getPeerconnection()?.addStream(videoStream)
        videoCallService?.createOffer(connection)
        runOnUiThread {
            tvCallTimer?.text = RINGING
        }
    }

    override fun onCallHungUp(jsonObject: JSONObject?, showFeedback: Boolean) {
        stopServiceCloseActivity(showFeedback)
    }

    fun onCalSwitched(jsonObject: JSONObject?) {
        stopServiceAndStartConf()
    }

    override fun onCallRejected(jsonObject: JSONObject?) {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            var m_amAudioManager: AudioManager? = null
            m_amAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            m_amAudioManager.setMode(AudioManager.STREAM_MUSIC)
            m_amAudioManager.isSpeakerphoneOn = videoCallModel?.callType.equals("VIDEO")
            var aa = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            if (videoCallModel?.callType.equals("VIDEO")) {
                mediaPlayer = MediaPlayer.create(this, R.raw.busy_tone)
            } else {
                mediaPlayer = MediaPlayer.create(this, R.raw.busy_tone, aa, 1)
            }
            mediaPlayer?.isLooping = false
            mediaPlayer?.start()
        }


        tvCallTimer?.text = REJECTED

        runOnUiThread {
            Handler().postDelayed({
                stopServiceCloseActivity(false)
            }, 3000)
        }
    }

    override fun onUserBusyRecieved(jsonObject: JSONObject?) {
        videoCallService?.cancelCallDisconnectTimer()
        runOnUiThread {
            tvCallTimer?.text = USER_BUSY
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
                mediaPlayer = MediaPlayer.create(this@FuguCallActivity, R.raw.busy_tone)
                mediaPlayer?.isLooping = false
                mediaPlayer?.start()
            }
            Handler().postDelayed({
                stopServiceCloseActivity(false)
            }, 3000)
        }
    }

    override fun onAddStream(mediaStream: MediaStream?) {
        remoteVideoStream = mediaStream
        videoCallService?.setRemoteStream(mediaStream)
        if (mediaStream?.videoTracks != null && mediaStream.videoTracks?.size!! > 0) {
            remoteVideoTrack = mediaStream.videoTracks?.get(0)
        }
        runOnUiThread {
            try {
                callStatus = WebRTCCallConstants.CallStatus.IN_CALL.toString()
                com.skeleton.mvp.data.db.CommonData.setCallStatus(WebRTCCallConstants.CallStatus.IN_CALL.toString())
                if (videoCallModel?.callType.equals("VIDEO")) {
                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    am.mode = AudioManager.STREAM_MUSIC
                    setBluetooth(am)

                } else {
                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    am.mode = AudioManager.STREAM_MUSIC
                    am.isSpeakerphoneOn = false
                    setBluetooth(am)
                }

                if (remoteVideoTrack != null) {
                    remoteVideoTrack?.addSink(remoteSurfaceview)
                }
                outgoingCallLayout?.visibility = View.VISIBLE
                incomingCallLayout?.visibility = View.GONE

                if (videoCallModel?.callType == "VIDEO") {
                    remoteSurfaceview?.visibility = View.VISIBLE
                    remoteSurfaceview?.setBackgroundColor(Color.TRANSPARENT)
                    localSurfaceView?.visibility = View.VISIBLE
                }
                ivBack?.visibility = View.VISIBLE
                ivSwitchToConf?.visibility = View.VISIBLE
//                incomingRippleView?.visibility = View.GONE
                tvIncomingPersonName?.visibility = View.GONE
                ivIncomingPersonImage?.visibility = View.GONE
                ivIncomingPersonImageBig?.visibility = View.GONE
                ivRejectCall?.visibility = View.GONE
                ivAnswerCall?.visibility = View.GONE
                ivHangUp?.visibility = View.VISIBLE
                ivMuteAudio?.visibility = View.VISIBLE
                if (videoCallModel?.callType == "VIDEO") {
                    ivMuteVideo?.visibility = View.VISIBLE
                    ivSwitchCamera?.visibility = View.VISIBLE
                    ivSpeaker?.visibility = View.GONE
                } else {
                    ivMuteVideo?.visibility = View.GONE
                    ivSwitchCamera?.visibility = View.GONE
                    ivSpeaker?.visibility = View.VISIBLE
                }
                if (videoCallModel?.callType == "VIDEO") {
                    tvCallType?.visibility = View.VISIBLE
                    tvCallType?.text = FuguAppConstant.APP_NAME_SHORT + " Video Call"
                    ivMuteVideo?.visibility = View.VISIBLE
                    ivSwitchCamera?.visibility = View.VISIBLE
                    ivSpeaker?.visibility = View.GONE
                    ivCalledPersonImage?.visibility = View.GONE
                    tvCalledPersonName?.visibility = View.GONE
                    tvCallType?.visibility = View.GONE
//                    ivFugu?.visibility = View.GONE
                } else {
                    tvCallType?.visibility = View.VISIBLE
                    tvCallType?.text = FuguAppConstant.APP_NAME_SHORT + " Voice Call"
                    ivMuteVideo?.visibility = View.GONE
                    ivSwitchCamera?.visibility = View.GONE
                    ivSpeaker?.visibility = View.VISIBLE

                    val options = RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.man)
                            .error(R.drawable.man)
                            .fitCenter()
                            .priority(Priority.HIGH)
                            .transforms(CenterCrop(), RoundedCorners(1000))
                    if (!isFinishing) {
                        Glide.with(this)
                                .asBitmap()
                                .apply(options)
                                .load(videoCallModel?.userThumbnailImage)
                                .into(ivCalledPersonImage!!)
                    }
                }

//                llVideoCall?.visibility = View.GONE
                isCallAnswered = true
                if (videoCallModel?.callType == "VIDEO") {
                    startForeGroundService(ONGOING_VIDEO_CALL)
                } else {
                    startForeGroundService(ONGOING_AUDIO_CALL)
                }
                EglRenderer.FrameListener { bitmap ->
                    Log.e("Resolution", bitmap.width.toString())
                }
                videoCallService?.isCallConnected = true
                localSurfaceView?.setZOrderOnTop(true)
                remoteSurfaceview?.setZOrderMediaOverlay(true)
                Log.e("Resolution", rootEglBase?.surfaceHeight().toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onIceCandidate(iceCandidate: IceCandidate?) {

    }

    override fun onErrorRecieved(error: String?) {

    }


    private fun initiateOutgoingRinging() {
        Handler().postDelayed({
            val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audio.mode = AudioManager.STREAM_MUSIC
            if (audio.isBluetoothA2dpOn) {
                audio.startBluetoothSco()
                audio.isSpeakerphoneOn = false
                audio.isBluetoothScoOn = true
                audio.requestAudioFocus(mListener, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN)
            } else {
                audio.stopBluetoothSco()
                audio.isBluetoothScoOn = false
                audio.isSpeakerphoneOn = videoCallModel?.callType.equals("VIDEO")
            }

            var aa = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            if (videoCallModel?.callType.equals("VIDEO")) {
                mediaPlayer = MediaPlayer.create(this, R.raw.ringing)
            } else {
                mediaPlayer = MediaPlayer.create(this, R.raw.ringing, aa, 1)
            }
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }, 100)
    }

    private fun initiateIncomingRinging() {
        Handler().postDelayed({
            try {
                val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager

                if (audio.isBluetoothA2dpOn) {
                    audio.isSpeakerphoneOn = true
                    audio.isBluetoothScoOn = false
                    audio.isWiredHeadsetOn = false
                    audio.requestAudioFocus(mListener, AudioManager.MODE_IN_CALL,
                            AudioManager.AUDIOFOCUS_GAIN)
                } else {
                    audio.stopBluetoothSco()
                    audio.isBluetoothScoOn = false
                    audio.isSpeakerphoneOn = videoCallModel?.callType.equals("VIDEO")
                }

                when (audio.ringerMode) {

                    AudioManager.RINGER_MODE_NORMAL -> {
                        mediaPlayer = MediaPlayer.create(this@FuguCallActivity,
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
                mediaPlayer = MediaPlayer.create(this@FuguCallActivity,
                        R.raw.video_call_ringtone)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            }
        }, 100)
    }

    private fun setUpWebRTCViews() {
        hasAlreadyInitializedViews = true
        remoteSurfaceview = findViewById(R.id.remoteSurfaceview)
        localSurfaceView = findViewById(R.id.localSurfaceView)
        rootEglBase = EglBase.create()
        videoCallService?.setEgl(rootEglBase)
        localSurfaceView?.setMirror(true)
        remoteSurfaceview?.setMirror(false)
        localSurfaceView?.init(rootEglBase?.eglBaseContext, null)
        localSurfaceView?.setZOrderOnTop(true)
        remoteSurfaceview?.setZOrderMediaOverlay(true)
        remoteSurfaceview?.init(rootEglBase?.eglBaseContext, null)
        peerConnectionFactory = videoCallService?.createPeerConnectionFactory(rootEglBase)

        videoCallService?.setVideoModel(videoCallModel)
        videoCallService?.createWebRTCCallConnection()
        sdpConstraints = MediaConstraints()
        sdpConstraints?.mandatory?.add(MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_AUDIO, "true"))
        sdpConstraints?.mandatory?.add(MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_VIDEO, "true"))
        connection = Connection(videoCallModel?.stunServers, videoCallModel?.turnServers,
                sdpConstraints, videoCallModel?.turnUserName, videoCallModel?.turnCredential, peerConnectionFactory)
        connection?.sdpConstraints = sdpConstraints
        videoCallService?.setConnectionModel(connection)
        videoCallService?.createPeerConnection(connection)

        val videoGrabberAndroid = createVideoGrabber(isFrontFacingCamera)
        val constraints = MediaConstraints()
        val videoSource = peerConnectionFactory?.createVideoSource(false)
        val cameraVideoCapturer = videoGrabberAndroid as CameraVideoCapturer
        localVideoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)
        val audioSource = peerConnectionFactory?.createAudioSource(constraints)
        localAudioTrack = peerConnectionFactory?.createAudioTrack("101", audioSource)
        if (videoCallModel?.callType == "VIDEO") {
            val surfaceTextureHelper = SurfaceTextureHelper
                    .create("CaptureThread", rootEglBase?.eglBaseContext)
            cameraVideoCapturer.initialize(surfaceTextureHelper,
                    this,
                    videoSource?.capturerObserver)
            cameraVideoCapturer.startCapture(1000, 1000, 30)
            videoStream = peerConnectionFactory?.createLocalMediaStream("102")
            videoCallService?.setLocalVideoStream(videoStream)
            videoStream?.addTrack(localVideoTrack)
        }
        videoStream?.addTrack(localAudioTrack)
        if (videoCallModel?.callType == "VIDEO") {
            localVideoTrack?.addSink(localSurfaceView)
        }
    }

    private fun setUpWebRTCViewsKilled() {
        rootEglBase = videoCallService?.getEgl()
        localSurfaceView?.setMirror(true)
        localSurfaceView?.init(rootEglBase?.eglBaseContext, null)
        localSurfaceView?.setZOrderOnTop(true)
        peerConnectionFactory = videoCallService?.createPeerConnectionFactory(rootEglBase)
        videoStream = videoCallService?.getLocalVideoStream()
        if (videoCallModel?.callType == "VIDEO") {
            localSurfaceView?.visibility = View.VISIBLE
            localVideoTrack = videoStream?.videoTracks?.get(0)
            localVideoTrack?.addSink(localSurfaceView)
        }
    }

    private fun createVideoGrabber(isFrontFacing: Boolean): VideoCapturer? {
        videoCapturer = createCameraGrabber(Camera1Enumerator(false), isFrontFacing)
        return videoCapturer
    }

    fun createCameraGrabber(enumerator: CameraEnumerator, isFrontFacing: Boolean): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        if (isFrontFacing) {
            for (deviceName in deviceNames) {
                if (enumerator.isFrontFacing(deviceName)) {
                    val videoCapturer = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
            for (deviceName in deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    val videoCapturer = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
        } else {
            for (deviceName in deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    val videoCapturer = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
            for (deviceName in deviceNames) {
                if (enumerator.isFrontFacing(deviceName)) {
                    val videoCapturer = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
        }
        return null
    }


    fun askForPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            val MY_PERMISSIONS_REQUEST = 102
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_REQUEST)
        } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO)

        } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val MY_PERMISSIONS_REQUEST_CAMERA = 100
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    MY_PERMISSIONS_REQUEST_CAMERA)
        } else {
            initCall()
        }
    }

    private fun answerCall() {
        Log.e("Faye Connection", videoCallService?.isFayeConnected().toString())
        vibrate?.cancel()
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
        }
//        if (videoCallModel?.callType.equals("VIDEO")) {
//            mainRoot?.setBackgroundColor(Color.BLACK)
//        }else{
//            mainRoot?.setBackgroundColor(Color.WHITE)
//        }

        try {
            if (videoCallService?.isFayeConnected()!!) {
                videoCallService?.saveOfferAndAnswer(videoOfferjson)
                ivBack?.visibility = View.VISIBLE
                ivSwitchToConf?.visibility = View.VISIBLE

                Handler().postDelayed({
                    if (wirelessContext != null && wirelessIntent != null) {
                        WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
                    }
                }, 500)
                Handler().postDelayed({
                    if (wirelessContext != null && wirelessIntent != null) {
                        WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
                    }
                }, 1500)

            } else {
                rlCallingButtons?.visibility = View.INVISIBLE
                tvConnecting?.visibility = View.VISIBLE
                isAnswerClicked = true
            }

            if (videoCallModel?.callType.equals("VIDEO")) {
                tvCallTimer?.visibility = View.GONE
            }

        } catch (e: java.lang.Exception) {
            videoCallService?.saveOfferAndAnswer(videoOfferjson)
            ivBack?.visibility = View.VISIBLE
            ivSwitchToConf?.visibility = View.VISIBLE
            Handler().postDelayed({
                if (wirelessContext != null && wirelessIntent != null) {
                    WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
                }
            }, 500)
            Handler().postDelayed({
                if (wirelessContext != null && wirelessIntent != null) {
                    WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
                }
            }, 1500)
        }
    }

    private fun startForeGroundService(status: String) {
        val startIntent = Intent(this@FuguCallActivity, VideoCallService::class.java)
        var channelName = ""
        if (!intent.hasExtra("videoCallModel")) {
            channelName = try {
                if (intent.hasExtra(CHANNEL_NAME)) {
                    intent.getStringExtra(CHANNEL_NAME) ?: ""
                } else {
                    FuguAppConstant.APP_NAME_SHORT + " Video Call"
                }
            } catch (e: Exception) {
                FuguAppConstant.APP_NAME_SHORT + " Call"
            }
        } else {
            videoCallModel = intent.extras?.getParcelable("videoCallModel")
            val turnCreds = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().turnCredentials
            videoCallModel?.turnServers = turnCreds.iceServers.turn as ArrayList<String>
            videoCallModel?.stunServers = turnCreds.iceServers.stun as ArrayList<String>
            videoCallModel?.turnCredential = turnCreds.credentials
            videoCallModel?.turnApiKey = turnCreds.turnApiKey
            videoCallModel?.turnUserName = turnCreds.username
            channelName = videoCallModel?.channelName!!
        }
        startIntent.action = "com.officechat.start"
        startIntent.putExtra(CALL_STATUS, status)
        startIntent.putExtra(CHANNEL_NAME, channelName)
        try {
            ContextCompat.startForegroundService(this, startIntent)
            bindService(startIntent, mConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun stopForegroundService(isHungUpToBeSent: Boolean?) {
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.mode = AudioManager.MODE_RINGTONE
        if (am.isBluetoothScoOn) {
            am.startBluetoothSco()
            am.stopBluetoothSco()
        }
        am.abandonAudioFocus(mListener)
        this@FuguCallActivity.stopForegroundService(isHungUpToBeSent)
    }

    fun hangupVideoCall() {
        runOnUiThread {
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
            } else {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.stop()
            }
            if (videoCallModel?.callType.equals("VIDEO")) {
                localVideoTrack?.removeSink(localSurfaceView)
            }
            videoCallService?.closePeerConnection()

            try {

                videoCapturer?.stopCapture()
                videoCapturer?.dispose()

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (!isAlreadyHungUp) {
                isAlreadyHungUp = true
                var audio: AudioManager? = null
                Handler().postDelayed({
                    audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    audio?.mode = AudioManager.STREAM_MUSIC
//                    if (audio!!.isBluetoothA2dpOn) {
//                        audio?.startBluetoothSco()
//                        audio?.isSpeakerphoneOn = false
//                        audio?.isBluetoothScoOn = true
//                    } else {
//
//                    }
                    audio?.stopBluetoothSco()
                    audio?.isSpeakerphoneOn = videoCallModel?.callType.equals("VIDEO")
                    val aa = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    if (videoCallModel?.callType.equals("VIDEO")) {
                        mediaPlayer = MediaPlayer.create(this, R.raw.disconnet_call)
                    } else {
                        audio?.mode = AudioManager.MODE_IN_CALL
                        mediaPlayer = MediaPlayer.create(this, R.raw.disconnet_call, aa, 1)
                    }

                    mediaPlayer?.isLooping = false
                    mediaPlayer?.start()
                }, 300)
            }
        }
    }


    fun stopVideoAudio() {
        runOnUiThread {
            stopAudioVideo = true
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
            } else {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.stop()
            }
            videoCallService?.closePeerConnection()
            try {
                videoCapturer?.stopCapture()
                videoCapturer?.dispose()

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            try {

            } catch (e: Exception) {

            }
        }


    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
        videoCapturer?.stopCapture()
        unregisterrecievers()
        try {
            vibrate?.cancel()

        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (mBounded) {
            unbindService(mConnection)
            mBounded = false
        }
        if (isLocalViewSmall) {
            remoteVideoTrack?.removeSink(remoteSurfaceview)
            localVideoTrack?.removeSink(localSurfaceView)
        } else {
            remoteVideoTrack?.removeSink(localSurfaceView)
            localVideoTrack?.removeSink(remoteSurfaceview)
        }

        localSurfaceView?.release()
        localSurfaceView?.pauseVideo()
        remoteSurfaceview?.release()
        remoteSurfaceview?.pauseVideo()

//        localAudioTrack?.dispose()
//        videoStream?.dispose()

        videoCallService?.setRemoteStream(remoteVideoStream)
        if (!callStatus.equals(WebRTCCallConstants.CallStatus.IN_CALL.toString())) {
            videoCallService?.cancelStartCallTimer()
            videoCallService?.cancelCalltimer()
            videoCallService?.cancelCallDisconnectTimer()

            stopServiceAndCloseConnection()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mBounded) {
            unbindService(mConnection)
            mBounded = false
        }
    }

    var mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mBounded = false
            videoCallService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {

            runOnUiThread {
                mBounded = true
                val mLocalBinder = service as VideoCallService.LocalBinder
                videoCallService = mLocalBinder.serverInstance




                if (!intent.hasExtra("videoCallModel")) {
                    videoCallService?.setActivityContext(this@FuguCallActivity)
                    setUpWebRTCViewsKilled()
                    remoteSurfaceview?.setMirror(false)
                    remoteSurfaceview?.setZOrderMediaOverlay(true)
                    remoteSurfaceview?.init(rootEglBase?.eglBaseContext, null)
                    connection = videoCallService?.getConnectionModel()
                    signal = videoCallService?.getSignal()
                    videoCallModel = videoCallService?.getVideoModel()
                    if (videoCallModel?.callType == "VIDEO") {
                        remoteVideoStream = videoCallService?.getRemoteVideoStream()
                        remoteVideoTrack = remoteVideoStream?.videoTracks?.get(0)
                        remoteSurfaceview?.visibility = View.VISIBLE
                        remoteSurfaceview?.setBackgroundColor(Color.TRANSPARENT)
                        remoteVideoTrack?.addSink(remoteSurfaceview)
                    }
                } else {
                    videoCallService?.setActivityContext(this@FuguCallActivity)
                    if (!hasAlreadyInitializedViews) {
                        setUpWebRTCViews()
                        fetchIntentData()
                    }
                }

                if (intent.action == Intent.ACTION_ANSWER) {
                    answerCall()
                }

                videoCallService?.reInitSocket()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        try {
            if (intent.action == Intent.ACTION_DELETE) {
                stopServiceAndCloseConnection()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class WiredHeadsetReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            wirelessContext = context
            wirelessIntent = intent

            val state = intent.getIntExtra("state", STATE_UNPLUGGED)
            val microphone = intent.getIntExtra("microphone", HAS_NO_MIC)
            val name = intent.getStringExtra("name")
            android.util.Log.d("", "WiredHeadsetReceiver.onReceive"
                    + VideoCallActivity.AppRTCUtils.getThreadInfo() + ": "
                    + "a=" + intent.action + ", s="
                    + (if (state == STATE_UNPLUGGED) "unplugged" else "plugged") + ", m="
                    + (if (microphone == HAS_MIC) "mic" else "no mic") + ", n=" + name + ", sb="
                    + isInitialStickyBroadcast)

            isWirelessHeadSetConnected = state == STATE_PLUGGED

            try {
                if (videoCallService?.isCallConnected!!) {
                    if (isWirelessHeadSetConnected) {
                        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        am.mode = AudioManager.STREAM_MUSIC
                        am.isWiredHeadsetOn = true
                        setBluetooth(am)

                    } else {
                        if (videoCallModel?.callType.equals("VIDEO")) {
                            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                            am.mode = AudioManager.STREAM_MUSIC
                            setBluetooth(am)
                        } else {
                            if (isOnSpeaker) {
                                val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                am.mode = AudioManager.STREAM_MUSIC
                                setBluetooth(am)
                            } else {
                                val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                am.mode = AudioManager.STREAM_MUSIC
                                am.isSpeakerphoneOn = false
                                setBluetooth(am)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun updateCallTimer(calltimer: String) {
        runOnUiThread {

            if (calltimer.equals("0:00")) {
                vibrate = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
                val pattern = longArrayOf(0, 100, 100)
                vibrate?.vibrate(pattern, 0)
                Handler().postDelayed({
                    vibrate?.cancel()
                }, 100)

            }

            if (videoCallModel?.callType.equals("AUDIO") && !stopAudioVideo) {
                tvCallTimer!!.visibility = View.VISIBLE
                tvCallTimer!!.text = calltimer
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterrecievers()
    }

    fun setUpProximitySensor() {
        if (mProximityController == null) {
            mProximityController = ProximitySensorController(applicationContext,
                    object : ProximitySensorController.onProximitySensorCallback {
                        override fun onError(errorCode: Int, message: String?) {
                            Log.e("ProximitySensorControllerCallBacks", String.format("onError() : %2s", message))
                        }

                        override fun onSensorRegister() {
                            Log.i("ProximitySensorControllerCallBacks", String.format("onSensorUnregister"))
                        }

                        override fun onSensorUnregister() {
                            Log.i("ProximitySensorControllerCallBacks", String.format("onSensorRegister"))
                        }

                        override fun onPlay() {
                            Log.i("ProximitySensorControllerCallBacks", String.format("near"))
                            try {
                                if (!mWakeLoack!!.isHeld && isCallAnswered && videoCallModel?.callType.equals("AUDIO")) {
                                    try {
                                        mWakeLoack?.acquire()
                                    } catch (e: java.lang.Exception) {
                                    }
                                    try {
                                        mPartialWakeLock?.acquire()
                                    } catch (e: java.lang.Exception) {
                                    }
                                    try {
                                        mProximityWakeLock?.acquire()
                                    } catch (e: java.lang.Exception) {

                                    }
                                }
                            } catch (e: java.lang.Exception) {
                            }
                        }

                        override fun onPause() {
                            Log.i("ProximitySensorControllerCallBacks", String.format("far"))
                        }
                    })
        }
        registerProximitySensor()
    }


    /**
     * register the proximity sensor using proximity controller
     */
    private fun registerProximitySensor() {
        mProximityController?.registerListener()
    }

    /**
     * Unregister the proximity sensor using proximity controller
     */
    private fun unregisterProximitySensor() {
        mProximityController?.unregisterListener()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == 100 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                initCall()
            } else if (requestCode == 101 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                initCall()
            } else if (requestCode == 102 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED &&
                    grantResults[1] == PermissionChecker.PERMISSION_GRANTED) {
                initCall()
            } else if (requestCode == 100 && grantResults[0] == PermissionChecker.PERMISSION_DENIED) {
                showCouldntPlaceError()
            } else if (requestCode == 101 && grantResults[0] == PermissionChecker.PERMISSION_DENIED) {
                showCouldntPlaceError()
            } else if (requestCode == 102 && grantResults[0] == PermissionChecker.PERMISSION_DENIED ||
                    grantResults[1] == PermissionChecker.PERMISSION_DENIED) {
                showCouldntPlaceError()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    private fun showCouldntPlaceError() {
        showErrorMessage(resources.getString(R.string.error_couldnt_place_call), {
            rejectCall()
            onCallHungUp(null, false)
        }, { }, "Ok", "")

    }

    private fun initCall() {

        val workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition())
        val turnCredentials = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().turnCredentials
        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add(EN_USER_ID, workspaceInfo.enUserId)
                .add("username", turnCredentials.username)
                .add("credentials", turnCredentials.credentials)
                .add("turn_api_key", turnCredentials.turnApiKey)
                .build()
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).verifyTurnCreds(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.fuguSecretKey, WebRTCCallConstants.ANDROID_USER, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<TurnCredsResponse>() {
                    override fun onSuccess(turnCreds: TurnCredsResponse?) {
                        val commonResponse = com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                        val turnCredential = commonResponse.data.turnCredentials
                        turnCredential.credentials = turnCreds?.data?.credential
                        turnCredential.username = turnCreds?.data?.username
                        turnCredential.turnApiKey = turnCreds?.data?.turnApiKey
                        turnCredential.iceServers.stun = turnCreds?.data?.iceServers?.stun
                        turnCredential.iceServers.turn = turnCreds?.data?.iceServers?.turn
                        com.skeleton.mvp.data.db.CommonData.setCommonResponse(commonResponse)

                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val notificationChannel = NotificationChannel("VideoCall",
                                    "VideoCall", NotificationManager.IMPORTANCE_LOW)
                            notificationChannel.setSound(null, null)
                            notificationManager.createNotificationChannel(notificationChannel)
                        }

                        initializeViews()
                        initializeClickListeners()

                        try {
                            field = PowerManager::class.java.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null)
                        } catch (ignored: Throwable) {
                        }
                        mPowerManager = getSystemService(POWER_SERVICE) as PowerManager
                        mWakeLoack = mPowerManager?.newWakeLock(PowerManager.FULL_WAKE_LOCK
                                or PowerManager.ACQUIRE_CAUSES_WAKEUP, localClassName)
                        mWakeLoack?.acquire()
                        mPartialWakeLock = mPowerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                                or PowerManager.ON_AFTER_RELEASE, localClassName)
                        mPartialWakeLock?.acquire()
                        if (PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK != 0x0) {
                            mProximityWakeLock = mPowerManager?.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, localClassName)
                        }
                        mPartialWakeLock?.acquire()


                        if (intent.action == Intent.ACTION_DELETE) {
                            rejectCall()
                            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                            am.mode = AudioManager.MODE_RINGTONE
                            if (am.isBluetoothScoOn) {
                                am.startBluetoothSco()
                                am.stopBluetoothSco()
                            }
                            am.abandonAudioFocus(mListener)
                            Handler().postDelayed({
                                stopForegroundService(false)
                            }, 500)
                            val hungUpIntent = Intent(VIDEO_CALL_HUNGUP_FROM_NOTIFICATION)
                            LocalBroadcastManager.getInstance(this@FuguCallActivity).sendBroadcast(hungUpIntent)
                            val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                            val taskList = mngr.getRunningTasks(10)
                            Handler().postDelayed({
                                if (taskList[0].baseActivity!!.className.equals("com.skeleton.mvp.videoCall.FuguCallActivity")) {
                                    finishAffinity()
                                    System.exit(0)
                                } else {
                                    finish()
                                }
                            }, 600)
                        }

                        if (!intent.hasExtra("videoCallModel")) {

                            ivBack?.visibility = View.VISIBLE
                            ivSwitchToConf?.visibility = View.VISIBLE
                            tvCallTimer?.visibility = View.GONE
                            tvCalledPersonName?.visibility = View.GONE
                            tvIncomingPersonName?.visibility = View.GONE
                            if (com.skeleton.mvp.data.db.CommonData.getVideoCallModel() != null) {
                                videoCallModel = com.skeleton.mvp.data.db.CommonData.getVideoCallModel()
                            } else {
                                try {
                                    stopServiceAndCloseConnection()
                                } catch (e: Exception) {
                                }
                            }
                            callStatus = com.skeleton.mvp.data.db.CommonData.getCallStatus()
                            when (callStatus) {
                                WebRTCCallConstants.CallStatus.IN_CALL.toString() -> {
                                    isCallAnswered = true
                                    incomingCallLayout?.visibility = View.GONE
                                    if (videoCallModel?.callType == "VIDEO") {
                                        startForeGroundService(ONGOING_VIDEO_CALL)
                                    } else {
                                        startForeGroundService(ONGOING_AUDIO_CALL)
                                    }
                                    ivRejectCall?.visibility = View.GONE
                                    ivAnswerCall?.visibility = View.GONE
                                    ivHangUp?.visibility = View.VISIBLE
                                    ivMuteAudio?.visibility = View.VISIBLE
                                    if (videoCallModel?.callType == "VIDEO") {
                                        tvCallType?.text = FuguAppConstant.APP_NAME_SHORT + " Video Call"
                                        ivMuteVideo?.visibility = View.VISIBLE
                                        ivSwitchCamera?.visibility = View.VISIBLE
                                        ivSpeaker?.visibility = View.GONE
                                        ivIncomingPersonImage?.visibility = View.GONE
                                        ivIncomingPersonImageBig?.visibility = View.GONE
                                        tvIncomingPersonName?.visibility = View.GONE
                                        ivCalledPersonImage?.visibility = View.GONE
                                        tvIncomingPersonName?.visibility = View.GONE
                                        tvCallType?.visibility = View.GONE
                                        tvCallTypeIncoming?.visibility = View.GONE

                                    } else {
                                        tvCallType?.text = FuguAppConstant.APP_NAME_SHORT + " Voice Call"
                                        ivMuteVideo?.visibility = View.GONE
                                        ivSwitchCamera?.visibility = View.GONE
                                        ivSpeaker?.visibility = View.VISIBLE
                                        ivIncomingPersonImage?.visibility = View.VISIBLE
                                        ivIncomingPersonImageBig?.visibility = View.VISIBLE
                                        tvIncomingPersonName?.visibility = View.VISIBLE
                                        tvCalledPersonName?.visibility = View.VISIBLE
                                    }
                                    if (videoCallModel?.callType.equals("AUDIO")) {
                                        tvIncomingPersonName?.text = videoCallModel?.channelName
                                        tvCalledPersonName?.text = videoCallModel?.channelName

                                        val options = RequestOptions()
                                                .centerCrop()
                                                .dontAnimate()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .placeholder(R.drawable.man)
                                                .error(R.drawable.man)
                                                .fitCenter()
                                                .priority(Priority.HIGH)
                                                .transforms(CenterCrop(), RoundedCorners(1000))
                                        if (!isFinishing) {
                                            Glide.with(this@FuguCallActivity)
                                                    .asBitmap()
                                                    .apply(options)
                                                    .load(videoCallModel?.userThumbnailImage)
                                                    .into(ivCalledPersonImage!!)
                                        }
                                    } else {

                                    }
                                    videoCallService?.createWebRTCSignallingConnection(videoCallModel, signal)
                                    Handler().postDelayed({

                                        isFrontFacingCamera = !isFrontFacingCamera
                                        switchCameraRecorder()
                                    }, 1000)

                                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                    am.mode = AudioManager.STREAM_MUSIC
                                    setBluetooth(am)
                                }

                                WebRTCCallConstants.CallStatus.INCOMING_CALL.toString() -> {

                                }
                                WebRTCCallConstants.CallStatus.OUTGOING_CALL.toString() -> {
                                }


                            }
                        } else {
                            startForeGroundService(RINGING)
                        }

                    }

                    override fun onError(error: ApiError?) {
                        showErrorMessage(resources.getString(R.string.error_couldnt_place_call), {
                            finish()
                        }, { }, "Ok", "")
                    }

                    override fun onFailure(throwable: Throwable?) {
                        showErrorMessage(resources.getString(R.string.error_couldnt_place_call), {
                            finish()
                        }, { }, "Ok", "")
                    }

                })
    }


    override fun onFayeConnected() {
        if (isAnswerClicked) {
            Log.e("Faye Connection", videoCallService?.isFayeConnected().toString())
            vibrate?.cancel()
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
            }
            try {
                if (videoCallService?.isFayeConnected()!!) {
                    videoCallService?.saveOfferAndAnswer(videoOfferjson)
                    ivBack?.visibility = View.VISIBLE
                    ivSwitchToConf?.visibility = View.VISIBLE
                    tvConnecting?.visibility = View.GONE
                } else {
                    ivAnswerCall?.visibility = View.INVISIBLE
                    ivRejectCall?.visibility = View.INVISIBLE
                    isAnswerClicked = true
                }
            } catch (e: java.lang.Exception) {
                videoCallService?.saveOfferAndAnswer(videoOfferjson)
                ivBack?.visibility = View.VISIBLE
                ivSwitchToConf?.visibility = View.VISIBLE
                tvConnecting?.visibility = View.GONE
            }
            if (isWirelessHeadSetConnected) {
                val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                am.mode = AudioManager.STREAM_MUSIC
                am.isSpeakerphoneOn = false
                am.isWiredHeadsetOn = true
                setBluetooth(am)
            }
            Handler().postDelayed({
                if (wirelessContext != null && wirelessIntent != null) {
                    WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
                }
            }, 500)
            Handler().postDelayed({
                if (wirelessContext != null && wirelessIntent != null) {
                    WiredHeadsetReceiver().onReceive(wirelessContext!!, wirelessIntent!!)
                }
            }, 1500)

        }
    }

    private fun setBluetooth(am: AudioManager) {
        Handler().postDelayed({
            try {
                if (am.isBluetoothA2dpOn) {
                    am.startBluetoothSco()
                    am.isBluetoothScoOn = true
                    am.isSpeakerphoneOn = false
                    val drawable = R.drawable.bluetooth_disabled
                    ivBluetooth?.setImageResource(drawable)
                    ivBluetooth?.visibility = View.VISIBLE
                    am.requestAudioFocus(mListener, AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN)
                    isOnBluetooth = true
                } else {
                    am.stopBluetoothSco()
                    am.isBluetoothScoOn = false
                    if (videoCallModel?.callType.equals("AUDIO")) {
                        am.isSpeakerphoneOn = isOnSpeaker
                    } else {
                        am.isSpeakerphoneOn = true
                    }
                    ivBluetooth?.visibility = View.GONE
                    isOnBluetooth = true
                }
            } catch (e: Exception) {
            }

//            Handler().postDelayed({
//                setBluetoothAgain(am)
//            }, 4000)

        }, 1000)

    }

    private fun setBluetoothAgain(am: AudioManager) {
        try {
            if (am.isBluetoothA2dpOn) {
                am.startBluetoothSco()
                am.isBluetoothScoOn = true
                val drawable = R.drawable.bluetooth_disabled
                ivBluetooth?.setImageResource(drawable)
                ivBluetooth?.visibility = View.VISIBLE
                am.requestAudioFocus(mListener, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN)
                isOnBluetooth = true
            } else {
                am.stopBluetoothSco()
                am.isBluetoothScoOn = false
                if (videoCallModel?.callType.equals("AUDIO")) {
                    am.isSpeakerphoneOn = isOnSpeaker
                } else {
                    am.isSpeakerphoneOn = true
                }
                ivBluetooth?.visibility = View.GONE
                isOnBluetooth = true
            }
        } catch (e: Exception) {
        }

    }

    fun setRecyclerViewAddedMembers(allMembers: FuguSearchResult?) {
        if (allMembers != null) {
            userIdsSearch = ArrayList(multiMemberAddGroupMap.keys)
            if (multiMemberAddGroupMap.size != 0) {
                if (multiMemberAddGroupMap[allMembers.user_id] != null) {
                    multiMemberAddGroupMap.remove(allMembers.user_id)
                    userIdsSearch.remove(allMembers.user_id)
                    val switchToConfBottomSheetFragment = supportFragmentManager.findFragmentByTag("SwitchToConfBottomSheetFragment") as SwitchToConfBottomSheetFragment
                    switchToConfBottomSheetFragment.updateBottomSheet(userIdsSearch, multiMemberAddGroupMap)
                } else {
                    addMemberToList(allMembers)
                }
            } else {
                addMemberToList(allMembers)
            }
        } else {
            val switchToConfBottomSheetFragment = supportFragmentManager.findFragmentByTag("SwitchToConfBottomSheetFragment") as SwitchToConfBottomSheetFragment
            switchToConfBottomSheetFragment.updateBottomSheet(userIdsSearch, multiMemberAddGroupMap)
        }
    }

    fun addMemberToList(getAllMembers: FuguSearchResult) {
        userIdsSearch.add(getAllMembers.user_id)
        multiMemberAddGroupMap[getAllMembers.user_id] = getAllMembers
        val switchToConfBottomSheetFragment = supportFragmentManager.findFragmentByTag("SwitchToConfBottomSheetFragment") as SwitchToConfBottomSheetFragment
        if (userIdsSearch.size > 0) {
            switchToConfBottomSheetFragment.updateBottomSheet(userIdsSearch, multiMemberAddGroupMap)
        } else {
            switchToConfBottomSheetFragment.updateBottomSheet(userIdsSearch, multiMemberAddGroupMap)
        }
    }

    fun updateAllMemberAdapter(userId: Long) {
        if (userIdsSearch.contains(userId)) {
            userIdsSearch.remove(userId)
        }
        val switchToConfBottomSheetFragment = supportFragmentManager.findFragmentByTag("SwitchToConfBottomSheetFragment") as SwitchToConfBottomSheetFragment
        switchToConfBottomSheetFragment.updateBottomSheet(userIdsSearch, multiMemberAddGroupMap)
    }

    fun apiInitiateVideoConference(userIds: ArrayList<Long>) {
        videoCallService?.switchConf()
        val linkArray = randomVideoConferenceLink(10)
        val link = linkArray[0] + "/" + linkArray[1]
        val inviteArray = JSONArray(userIds)

        val jsonObject = JSONObject()
        jsonObject.put("invite_link", link)
        jsonObject.put(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).enUserId)
        jsonObject.put("invite_user_ids", inviteArray)
        jsonObject.put(WebRTCCallConstants.VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.SWITCH_TO_CONFERENCE.toString())
        jsonObject.put(WebRTCCallConstants.REASON, WebRTCCallConstants.CallHangupType.CONF_INVITE.toString())
        videoCallService?.onVideoConfInitiated(jsonObject)


    }

    override fun onVideoConfInitiated(jsonObject: JSONObject?) {

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

    override fun onItemAnswered(swipeView: View) {
        when (swipeView.id) {
            R.id.ivAnswerCall -> {
                answerCall()
            }
            R.id.ivReplyCall -> {
                runOnUiThread {
                    var dialog = Dialog(this@FuguCallActivity, android.R.style.Theme_Translucent_NoTitleBar)
                    dialog.setContentView(R.layout.dialog_call_options)
                    val lp = dialog.window!!.attributes
                    lp.dimAmount = 0.5f
                    dialog.window!!.attributes = lp
                    dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                    dialog.setCancelable(true)
                    dialog.setCanceledOnTouchOutside(true)

                    var optionOne = dialog.findViewById<AppCompatTextView>(R.id.option_one)
                    var optionTwo = dialog.findViewById<AppCompatTextView>(R.id.option_two)
                    var optionThree = dialog.findViewById<AppCompatTextView>(R.id.option_three)
                    var optionFour = dialog.findViewById<AppCompatTextView>(R.id.option_four)
                    var optionCustom = dialog.findViewById<AppCompatTextView>(R.id.custom_action)

                    val intent = Intent(this@FuguCallActivity, ChatActivity::class.java)

                    val conversation = FuguConversation()
                    conversation.businessName = ""
                    conversation.label = ""
                    conversation.isOpenChat = true
                    conversation.channelId = videoCallModel?.channelId
                    conversation.chat_type = 2
                    conversation.userName = StringUtil.toCamelCase(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fullName)
                    conversation.userId = java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)
                    conversation.enUserId = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId
                    conversation.unreadCount = 0
                    intent.putExtra(FuguAppConstant.CONVERSATION, Gson().toJson(conversation, FuguConversation::class.java))
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    optionOne.setOnClickListener {
                        startChatActivity(intent, "Can't talk now. What's up?")

                    }
                    optionTwo.setOnClickListener {
                        startChatActivity(intent, "I'll call you right back.")
                    }
                    optionThree.setOnClickListener {
                        startChatActivity(intent, "I'll call you later.")
                    }
                    optionFour.setOnClickListener {
                        startChatActivity(intent, "Can't talk now. Call me later?")
                    }
                    optionCustom.setOnClickListener {
                        startChatActivity(intent, "")
                    }
                    dialog.show()
                }
            }
            R.id.ivRejectCall -> {
                videoCallService?.rejectCall()
                Handler().postDelayed({
                    stopServiceAndCloseConnection()
                }, 500)
            }
        }
    }

    private fun startChatActivity(intent: Intent, text: String) {
        videoCallService?.rejectCall()
        stopServiceAndStartConf()
        CommonData.setCustomText(text)
        Handler().postDelayed({
            intent.putExtra("SendCustomMessage", text)
            startActivity(intent)
        }, 300)
    }

    fun unbindServiceConnection() {
        if (mBounded) {
            unbindService(mConnection)
            mBounded = false
        }
    }

    fun onCallDisconnectEvent() {
        runOnUiThread {
            llConnectivityIssues?.visibility = View.VISIBLE
        }

    }

    fun onCallConnectEvent() {
        runOnUiThread {
            llConnectivityIssues?.visibility = View.GONE
        }
    }

    fun onHungupSent() {
        ivHungupCallTimer?.cancel()
        videoCallService?.cancelCalltimer()
        val jsonObject: JSONObject? = null
        jsonObject?.put("call_type", videoCallModel?.callType)
        videoCallService?.onCallHungUp(jsonObject, false)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            if (videoCallModel?.activityLaunchState!!.equals(WebRTCCallConstants.AcitivityLaunchState.OTHER.toString())) {
                mediaPlayer?.stop()
                vibrate?.cancel()
            }
        } catch (e: Exception) {
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            if (videoCallModel?.activityLaunchState!!.equals(WebRTCCallConstants.AcitivityLaunchState.OTHER.toString())) {
                mediaPlayer?.stop()
            }
        } catch (e: Exception) {
        }
        return super.onKeyDown(keyCode, event)
    }

    fun rejectCall() {
        val devicePayload = JSONObject()
        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this))

        if (videoCallModel == null) {
            videoCallModel = com.skeleton.mvp.data.db.CommonData.getVideoCallModel()
        }

        signal = Signal(videoCallModel?.userId, videoCallModel?.signalUniqueId, videoCallModel?.fullName,
                videoCallModel?.turnApiKey, videoCallModel?.turnUserName, videoCallModel?.turnCredential
                , videoCallModel?.stunServers, videoCallModel?.turnServers, devicePayload, videoCallModel?.callType!!)
        val jsonObject = JSONObject()
        jsonObject.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.VideoCallType.CALL_REJECTED.toString())
        jsonObject.put(WebRTCCallConstants.REASON, WebRTCCallConstants.CallHangupType.GET_USER_MEDIA.toString())
        val rejectedJson = addCommonUserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(rejectedJson)
    }

    fun callHungup() {
        val devicePayload = JSONObject()
        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this))
        signal = Signal(videoCallModel?.userId, videoCallModel?.signalUniqueId, videoCallModel?.fullName,
                videoCallModel?.turnApiKey, videoCallModel?.turnUserName, videoCallModel?.turnCredential
                , videoCallModel?.stunServers, videoCallModel?.turnServers, devicePayload, videoCallModel?.callType!!)
        val jsonObject = JSONObject()
        jsonObject.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.VideoCallType.CALL_HUNG_UP.toString())
        jsonObject.put(FuguAppConstant.HUNGUP_TYPE, "DEFAULT")
        val rejectedJson = addCommonUserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(rejectedJson)
    }

    private fun addCommonUserDetails(jsonObject: JSONObject): JSONObject {
        jsonObject.put(WebRTCCallConstants.IS_SILENT, true)
        jsonObject.put(WebRTCCallConstants.USER_ID, signal?.signalUniqueUserId)
        jsonObject.put(WebRTCCallConstants.MESSAGE_TYPE, WebRTCCallConstants.VIDEO_CALL)
        jsonObject.put(WebRTCCallConstants.IS_TYPING, 0)
        jsonObject.put(WebRTCCallConstants.MESSAGE_UNIQUE_ID, signal?.signalUniqueId)
        return jsonObject
    }

    private fun addTurnCredentialsAndDeviceDetails(jsonObject: JSONObject) {
        val stunServers = JSONArray()
        val turnServers = JSONArray()
        val videoCallCredentials = JSONObject()

        videoCallCredentials.put(WebRTCCallConstants.TURN_API_KEY, signal?.turnApiKey)
        videoCallCredentials.put(WebRTCCallConstants.USER_NAME, signal?.turnUserName)
        videoCallCredentials.put(WebRTCCallConstants.CREDENTIAL, signal?.turnCredential)
        for (i in signal?.stunServers!!.indices) {
            stunServers.put(signal?.stunServers!!.get(i))
        }
        for (i in signal?.turnServers!!.indices) {
            turnServers.put(signal?.turnServers!!.get(i))
        }

        videoCallCredentials.put(WebRTCCallConstants.STUN, stunServers)
        videoCallCredentials.put(WebRTCCallConstants.TURN, turnServers)
        jsonObject.put(FuguAppConstant.CHANNEL_ID, videoCallModel?.channelId)
        jsonObject.put(WebRTCCallConstants.TURN_CREDENTIALS, videoCallCredentials)
        jsonObject.put(WebRTCCallConstants.DEVICE_PAYLOAD, signal?.deviceDetails)
        jsonObject.put("call_type", signal?.callType)
        SocketConnection.sendMessage(jsonObject)
    }


}
