package com.skeleton.mvp.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.WindowManager
import android.widget.RemoteViews
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.VideoConfActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.pushNotification.NotificationImageManager
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.util.Utils
import com.skeleton.mvp.videoCall.*
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.ONGOING_AUDIO_CALL
import org.json.JSONObject
import org.webrtc.*

@Suppress("DEPRECATION")
/**
 * Created by rajatdhamija
 * 06/09/18.
 */

class VideoCallService : Service(), WebRTCFayeCallbacks, WebRTCCallCallbacks {

    private var signal: Signal? = null
    private var connection: Connection? = null
    private var videoCallModel: VideoCallModel? = null
    private var mBinder: IBinder = LocalBinder()
    private var status: String = ""
    var peerConnection: PeerConnection? = null
    private var remoteVideoStream: MediaStream? = null
    private var localVieoStream: MediaStream? = null
    var webRTCSignallingClient: WebRTCSignallingClient? = null
    var webRTCCallClient: WebRTCCallClient? = null
    var isCallConnected: Boolean? = false
    var isReadyForConnection: Boolean? = false
    var isCallInitiated: Boolean? = false
    var fuguCallActivity: FuguCallActivity? = null
    private var rootEglBase: EglBase? = null
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var intent: Intent? = null
    var callDisconnectTime: CountDownTimer? = null
    var startTime: Long? = null
    var callTimer: CountDownTimer? = null
    var mediaPlayer: MediaPlayer? = null
    var isCallFailed = true
    var mListener: AudioManager.OnAudioFocusChangeListener? = null
    override fun onVideoConfInitiated(jsonObject: JSONObject?) {
        webRTCSignallingClient?.sendVideoConfJson(jsonObject!!)
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        val serverInstance: VideoCallService
            get() = this@VideoCallService
    }


    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoCallReciever)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val hungupIntent = Intent(VIDEO_CALL_HUNGUP)
        LocalBroadcastManager.getInstance(this).sendBroadcast(hungupIntent)
        CommonData.setVideoCallModel(null)
//        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        am.mode = AudioManager.MODE_RINGTONE
//        if (am.isBluetoothScoOn) {
//            am.startBluetoothSco()
//            am.stopBluetoothSco()
//        }
//        am.abandonAudioFocus(mListener)
    }

    private val mVideoCallReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onBroadCastrecieved(intent)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (mListener == null) {
            mListener = AudioManager.OnAudioFocusChangeListener { }
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoCallReciever)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mVideoCallReciever,
                IntentFilter(VIDEO_CALL_INTENT))

        if (intent.getStringExtra(CALL_STATUS) == ONGOING_AUDIO_CALL || intent.getStringExtra(CALL_STATUS) == ONGOING_VIDEO_CALL) {
            if (startTime == null) {
                startTime = System.currentTimeMillis()
            }
            callTimer = object : CountDownTimer(90000000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    val currentTime = System.currentTimeMillis()
                    val diff = (currentTime - startTime!!) / 1000
                    val seconds = diff % 60
                    val minutes = diff / 60
                    val hours = minutes / 60
                    var secondstext = ""
                    if (seconds < 10) {
                        secondstext = "0$seconds"
                    } else {
                        secondstext = "$seconds"
                    }
                    if (fuguCallActivity != null) {
                        if (hours > 0) {
                            fuguCallActivity!!.updateCallTimer("$hours:$minutes:$secondstext")
                        } else {
                            fuguCallActivity!!.updateCallTimer("$minutes:$secondstext")
                        }
                    }
                }

                override fun onFinish() {
//                    hungUpCall()
//                    fuguCallActivity?.onCallHungUp(null)
                }
            }.start()
        }
        this.intent = intent

        if (intent.hasExtra(INIT_FULL_SCREEN_SERVICE)) {
            val notificationIntent = Intent(this, FuguCallActivity::class.java)
            if (videoCallModel == null) {
                videoCallModel = CommonData.getVideoCallModel()
            }
            val customView = RemoteViews(packageName, R.layout.cutom_call_notification)
            customView.setTextViewText(R.id.name, videoCallModel?.channelName)
            customView.setImageViewBitmap(R.id.photo,  Utils.getCircleBitmap(NotificationImageManager().getImageBitmap(videoCallModel?.userThumbnailImage!!)))

            notificationIntent.action = Intent.ACTION_MAIN
            notificationIntent.putExtra(CHANNEL_NAME, videoCallModel?.channelName)
            notificationIntent.putExtra("videoCallModel", videoCallModel)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            if (status == WebRTCCallConstants.AcitivityLaunchState.KILLED.toString()) {
                notificationIntent.putExtra("activitylaunchState", status)
            }

            val hungupIntent = Intent(this, FuguCallActivity::class.java)
            hungupIntent.action = Intent.ACTION_DELETE
            hungupIntent.putExtra(CHANNEL_NAME, videoCallModel?.channelName)
            hungupIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            hungupIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)


            val answerIntent = Intent(this, FuguCallActivity::class.java)
            answerIntent.action = Intent.ACTION_ANSWER
            answerIntent.putExtra(CHANNEL_NAME, videoCallModel?.channelName)
            answerIntent.putExtra("videoCallModel", videoCallModel)
            answerIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            answerIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

            val pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val hungupPendingIntent = PendingIntent.getActivity(this, 0,
                    hungupIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val answerPendingIntent = PendingIntent.getActivity(this, 0,
                    answerIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            customView.setOnClickPendingIntent(R.id.btnAnswer, answerPendingIntent)
            customView.setOnClickPendingIntent(R.id.btnDecline, hungupPendingIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val notification = NotificationCompat.Builder(this, "IncomingCall")
                notification.setContentTitle(intent.getStringExtra(CHANNEL_NAME))
                if (intent.getStringExtra(CALL_STATUS) == ONGOING_VIDEO_CALL || intent.getStringExtra(CALL_STATUS) == ONGOING_AUDIO_CALL) {
                    notification.setUsesChronometer(true)
                    notification.setShowWhen(false)
                }
                notification.setTicker(intent.getStringExtra(CALL_STATUS))
                notification.setContentText(intent.getStringExtra(CALL_STATUS))
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

                val answerAction = NotificationCompat.Action.Builder(
                        android.R.drawable.sym_action_chat, "Answer", hungupPendingIntent)
                        .build()

                val rejectAction = NotificationCompat.Action.Builder(
                        android.R.drawable.sym_action_chat, "Reject", hungupPendingIntent)
                        .build()

//                notification.addAction(answerAction)
//                notification.addAction(rejectAction)

                startForeground(1122, notification.build())
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationChannel = NotificationChannel("IncomingCall",
                            "IncomingCall", NotificationManager.IMPORTANCE_HIGH)
                    notificationChannel.setSound(null, null)
                    notificationManager.createNotificationChannel(notificationChannel)
                }
            } else {

                val notification = NotificationCompat.Builder(this)
                notification.setContentTitle(intent.getStringExtra(CHANNEL_NAME))
                notification.setTicker(intent.getStringExtra(CALL_STATUS))
                notification.setContentText(intent.getStringExtra(CALL_STATUS))
                if (intent.getStringExtra(CALL_STATUS) == ONGOING_VIDEO_CALL || intent.getStringExtra(CALL_STATUS) == ONGOING_AUDIO_CALL) {
                    notification.setUsesChronometer(true)
                    notification.setShowWhen(false)
                }
                notification.setSmallIcon(R.drawable.notification_white)
                notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
                notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
                notification.setVibrate(null)
                notification.setContentIntent(pendingIntent)
                notification.setOngoing(true)
                notification.setCategory(NotificationCompat.CATEGORY_CALL)
                notification.priority = getPriority()
                val hangupAction = NotificationCompat.Action.Builder(
                        android.R.drawable.sym_action_chat, "HANG UP", hungupPendingIntent)
                        .build()
                notification.addAction(hangupAction)
                startForeground(1122, notification.build())
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
        } else {
            val notificationIntent = Intent(this, FuguCallActivity::class.java)

            if (videoCallModel == null) {
                videoCallModel = CommonData.getVideoCallModel()
            }

            notificationIntent.action = Intent.ACTION_MAIN
            notificationIntent.putExtra(CHANNEL_NAME, videoCallModel?.channelName)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            if (status == WebRTCCallConstants.AcitivityLaunchState.KILLED.toString()) {
                notificationIntent.putExtra("activitylaunchState", status)
            }

            val hungupIntent = Intent(this, FuguCallActivity::class.java)
            hungupIntent.action = Intent.ACTION_DELETE
            hungupIntent.putExtra(CHANNEL_NAME, videoCallModel?.channelName)
            hungupIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            hungupIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            val pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val hungupPendingIntent = PendingIntent.getActivity(this, 0,
                    hungupIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val notification = NotificationCompat.Builder(this, "VideoCall")
                notification.setContentTitle(intent.getStringExtra(CHANNEL_NAME))
                if (intent.getStringExtra(CALL_STATUS) == ONGOING_VIDEO_CALL || intent.getStringExtra(CALL_STATUS) == ONGOING_AUDIO_CALL) {
                    notification.setUsesChronometer(true)
                    notification.setShowWhen(false)
                }
                notification.setTicker(intent.getStringExtra(CALL_STATUS))
                notification.setContentText(intent.getStringExtra(CALL_STATUS))
                notification.setSmallIcon(R.drawable.notification_white)
                notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
                notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
                notification.setCategory(NotificationCompat.CATEGORY_CALL)
                notification.setVibrate(null)
                notification.setOngoing(true)
                notification.setContentIntent(pendingIntent)

                notification.priority = getPriority()
                val hangupAction = NotificationCompat.Action.Builder(
                        android.R.drawable.sym_action_chat, "HANG UP", hungupPendingIntent)
                        .build()
                notification.addAction(hangupAction)

                startForeground(1122, notification.build())
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    var notificationChannel = NotificationChannel("VideoCall",
                            "VideoCall", NotificationManager.IMPORTANCE_LOW)
                    notificationChannel.setSound(null, null)
                    notificationManager.createNotificationChannel(notificationChannel)
                }
            } else {

                val notification = NotificationCompat.Builder(this)
                notification.setContentTitle(intent.getStringExtra(CHANNEL_NAME))
                notification.setTicker(intent.getStringExtra(CALL_STATUS))
                notification.setContentText(intent.getStringExtra(CALL_STATUS))
                if (intent.getStringExtra(CALL_STATUS) == ONGOING_VIDEO_CALL || intent.getStringExtra(CALL_STATUS) == ONGOING_AUDIO_CALL) {
                    notification.setUsesChronometer(true)
                    notification.setShowWhen(false)
                }
                notification.setSmallIcon(R.drawable.notification_white)
                notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
                notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
                notification.setVibrate(null)
                notification.setContentIntent(pendingIntent)
                notification.setOngoing(true)
                notification.setCategory(NotificationCompat.CATEGORY_CALL)
                notification.priority = getPriority()
                val hangupAction = NotificationCompat.Action.Builder(
                        android.R.drawable.sym_action_chat, "HANG UP", hungupPendingIntent)
                        .build()
                notification.addAction(hangupAction)
                startForeground(1122, notification.build())
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
        }

        if (callDisconnectTime == null) {
            callDisconnectTime = object : CountDownTimer(30000, 1000) {

                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    if (!isCallConnected!!) {
                        webRTCSignallingClient?.cancelCounter()
                        hungUpCall(false)
                        fuguCallActivity?.onCallHungUp(null, false)
                    }
                }
            }.start()
        } else {
            callDisconnectTime?.cancel()
        }
        return Service.START_STICKY
    }

    private fun getPriority(): Int {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            Notification.PRIORITY_MAX
        }
    }

    fun createWebRTCSignallingConnection(videoCallModel: VideoCallModel?, signal: Signal?) {
        this.videoCallModel = videoCallModel
        this.signal = signal
        webRTCSignallingClient = WebRTCSignallingClient(this, videoCallModel?.channelId, videoCallModel?.activityLaunchState)
        webRTCSignallingClient?.setSignalRequirementModel(signal)
        webRTCSignallingClient?.setUpFayeConnection(true)
    }

    fun setConnectionModel(connection: Connection?) {
        this.connection = connection
    }

    fun setSignalModel(signal: Signal?) {
        this.signal = signal
    }

    fun setActivityContext(fuguCallActivity: FuguCallActivity?) {
        this.fuguCallActivity = fuguCallActivity
    }

    fun createWebRTCCallConnection() {
        webRTCCallClient = WebRTCCallClient(this)
        webRTCSignallingClient = WebRTCSignallingClient(this, videoCallModel?.channelId, videoCallModel?.activityLaunchState)
    }

    fun isFayeConnected(): Boolean {
        if (webRTCSignallingClient != null) {
            return webRTCSignallingClient!!.isFayeConnected()
        } else {
            return false
        }
    }

    fun hungUpCall(isCallHungUp: Boolean) {
        isCallFailed = false
        if (callDisconnectTime != null) {
            callDisconnectTime?.cancel()
        }
        webRTCSignallingClient?.hangUpCallDefault(isCallHungUp)
    }

    fun switchConf() {
        webRTCSignallingClient?.hangUpCallSwitched()
    }

    fun hungUpCallLocally() {
        isCallFailed = false
        if (callDisconnectTime != null) {
            callDisconnectTime?.cancel()
        }
    }

    fun rejectCall() {
        webRTCSignallingClient?.rejectCall()
    }

    fun saveOfferAndAnswer(videoOfferjson: JSONObject?) {

        webRTCCallClient?.saveOfferAndAnswer(videoOfferjson, connection)
    }

    fun saveIceCandidate(jsonObject: JSONObject?) {
        webRTCCallClient?.saveIceCandidate(jsonObject)
    }

    fun createPeerConnection(connection: Connection?) {
        webRTCCallClient = WebRTCCallClient(this)
        peerConnection = webRTCCallClient?.createPeerConnection(connection)
        Log.e("peerConnection>>>>>>>>>>", peerConnection.toString())
    }

    fun getRemoteVideoStream(): MediaStream? {
        return remoteVideoStream
    }

    fun setLocalVideoStream(localVideoStream: MediaStream?) {
        this.localVieoStream = localVideoStream
    }

    fun getLocalVideoStream(): MediaStream? {
        return localVieoStream
    }

    fun getPeerconnection(): PeerConnection? {
        return peerConnection
    }

    fun createOffer(connection: Connection?) {
        webRTCCallClient?.createOffer(connection)
    }

    fun setVideoModel(videoCallModel: VideoCallModel?) {
        this.videoCallModel = videoCallModel
        Thread {
            kotlin.run {
                CommonData.setVideoCallModel(videoCallModel)
            }
        }.start()
    }

    fun closePeerConnection() {
        if (peerConnection != null) {
            peerConnection?.close()
            peerConnection?.dispose()
//            peerConnectionFactory?.dispose()
            peerConnection = null
        }
    }

    fun closePeerSwitchConnection() {
        if (peerConnection != null) {
            peerConnection?.close()
            peerConnection?.dispose()
            peerConnection = null
        }
    }

    override fun onIceCandidateRecieved(jsonObject: JSONObject?) {
        fuguCallActivity?.onIceCandidateRecieved(jsonObject)
    }

    override fun onVideoOfferRecieved(jsonObject: JSONObject?) {
        fuguCallActivity?.onVideoOfferRecieved(jsonObject)
    }

    override fun onVideoAnswerRecieved(jsonObject: JSONObject?) {
        if (callDisconnectTime != null) {
            callDisconnectTime?.cancel()
        }
        fuguCallActivity?.onVideoAnswerRecieved(jsonObject)
    }

    override fun onReadyToConnectRecieved(jsonObject: JSONObject?) {
        fuguCallActivity?.onReadyToConnectRecieved(jsonObject)
    }

    override fun onCallHungUp(jsonObject: JSONObject?, showFeedback: Boolean) {
        isCallFailed = false
        if (!foregrounded() || !isCallConnected!!) {
            fuguCallActivity?.onCallHungUp(jsonObject, false)
        } else {
            fuguCallActivity?.onCallHungUp(jsonObject, true)
//            fuguCallActivity?.runOnUiThread {
//                if (dialog == null) {
//                    fuguCallActivity?.stopForegroundService(false)
//                    stopSelf()
//                    fuguCallActivity?.unbindServiceConnection()
//                    fuguCallActivity?.stopVideoAudio()
//                    showFeedBackDialog(jsonObject)
//                }
//            }
        }

    }

    fun onCallFailed() {
        mediaPlayer?.stop()
        if (isCallFailed) {
            fuguCallActivity?.runOnUiThread {
                fuguCallActivity?.stopForegroundService(false)
                stopSelf()
                webRTCSignallingClient?.callFailed()
                fuguCallActivity?.unbindServiceConnection()
                fuguCallActivity?.stopVideoAudio()
                fuguCallActivity?.onCallHungUp(null, false)
            }
        }
    }


    fun onDisconnected() {
        if (isCallFailed) {
            val aa = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()

            if (videoCallModel?.callType.equals("VIDEO")) {
                mediaPlayer = MediaPlayer.create(this, R.raw.busy_tone)
            } else {
                mediaPlayer = MediaPlayer.create(this, R.raw.busy_tone, aa, 1)
            }
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()

            fuguCallActivity?.onCallDisconnectEvent()
        }

    }

    fun onConnected() {
        mediaPlayer?.stop()
        fuguCallActivity?.onCallConnectEvent()
    }

//    private fun showFeedBackDialog(jsonObject: JSONObject?) {
//        try {
//            dialog = Dialog(fuguCallActivity!!, android.R.style.Theme_Translucent_NoTitleBar)
//            dialog?.setContentView(R.layout.activity_calling_feed_back)
//            val lp = dialog?.window!!.attributes
//            lp.dimAmount = 0.5f
//            dialog?.window!!.attributes = lp
//            dialog?.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//            dialog?.setCancelable(false)
//            dialog?.setCanceledOnTouchOutside(false)
//            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//            val ratingBar: CustomRatingBar? = dialog?.findViewById(R.id.ratingBar)
//            val tvRating: TextView? = dialog?.findViewById(R.id.tvRating)
//            val etFeedback: EditText? = dialog?.findViewById(R.id.etFeedback)
//            val btnNotNow: AppCompatButton? = dialog?.findViewById(R.id.btnNotNow)
//            val btnSubmit: AppCompatButton? = dialog?.findViewById(R.id.btnSubmit)
//
//            ratingBar?.setOnScoreChanged { score ->
//
//                if (score >= 0f) {
//                    tvRating?.visibility = View.VISIBLE
//                } else {
//                    tvRating?.visibility = View.GONE
//                }
//                when (score) {
//
//                    1f -> {
//                        tvRating?.text = "Very Bad"
//                    }
//                    2f -> {
//                        tvRating?.text = "Bad"
//                    }
//                    3f -> {
//                        tvRating?.text = "Average"
//                    }
//                    4f -> {
//                        tvRating?.text = "Good"
//                    }
//                    5f -> {
//                        tvRating?.text = "Excellent"
//                    }
//                    else -> {
//
//                    }
//                }
//            }
//
//
//            btnNotNow?.setOnClickListener {
//                fuguCallActivity?.onCallHungUp(jsonObject)
//                dialog?.dismiss()
//            }
//
//            btnSubmit?.setOnClickListener {
//                if (jsonObject?.getString("call_type").equals("VIDEO")) {
//                    apiSendFeedback(ratingBar?.score!!, Feedback.VIDEO_CALL.toString(), etFeedback?.text.toString().trim())
//                    dialog = null
//                } else {
//                    apiSendFeedback(ratingBar?.score!!, Feedback.AUDIO_CALL.toString(), etFeedback?.text.toString().trim())
//                    dialog = null
//                }
//
//                fuguCallActivity?.onCallHungUp(jsonObject)
//            }
//
//            dialog?.show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    override fun onCallRejected(jsonObject: JSONObject?) {
        fuguCallActivity?.runOnUiThread {
            fuguCallActivity?.onCallRejected(jsonObject)
            if (callDisconnectTime != null) {
                callDisconnectTime?.cancel()
            }
        }

    }

    override fun onUserBusyRecieved(jsonObject: JSONObject?) {
        fuguCallActivity?.onUserBusyRecieved(jsonObject)
    }

    override fun onErrorRecieved(error: String?) {
        fuguCallActivity?.onErrorRecieved(error)
    }

    override fun onAddStream(mediaStream: MediaStream?) {
        fuguCallActivity?.onAddStream(mediaStream)
    }

    override fun onIceCandidate(iceCandidate: IceCandidate?) {
        onIceCandidate(iceCandidate)
    }


    override fun onVideoOfferScreenSharingRecieved(jsonObject: JSONObject?) {

        fuguCallActivity?.onVideoOfferScreenSharingRecieved(jsonObject)
    }

    fun getConnectionModel(): Connection? {
        return connection
    }

    fun getSignal(): Signal? {
        return signal
    }

    fun getVideoModel(): VideoCallModel? {
        return videoCallModel
    }

    fun createPeerConnectionFactory(rootEglBase: EglBase?): PeerConnectionFactory? {
        if (peerConnectionFactory == null) {
            val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(this)
                    .createInitializationOptions()
            PeerConnectionFactory.initialize(initializationOptions)
            val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(

                    rootEglBase?.eglBaseContext, /* enableIntelVp8Encoder */true, /* enableH264HighProfile */true)
            val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(rootEglBase?.eglBaseContext)
            val options = PeerConnectionFactory.Options()
            peerConnectionFactory = PeerConnectionFactory.builder()
                    .setOptions(options)
                    .setVideoEncoderFactory(defaultVideoEncoderFactory)
                    .setVideoDecoderFactory(defaultVideoDecoderFactory)
                    .createPeerConnectionFactory()
            return peerConnectionFactory
        } else {
            return peerConnectionFactory
        }

    }

    fun setRemoteStream(mediaStream: MediaStream?) {
        this.remoteVideoStream = mediaStream
    }

    fun setEgl(eglBase: EglBase?) {
        this.rootEglBase = eglBase
    }

    fun getEgl(): EglBase? {
        return rootEglBase
    }

    fun onBroadCastrecieved(intent: Intent) {
        webRTCSignallingClient?.onBroadcastRecieved(intent)
    }

    override fun onFayeConnected() {
        fuguCallActivity?.onFayeConnected()
    }

    fun initiateConferenceCall(jsonObject: JSONObject?) {
        fuguCallActivity?.runOnUiThread {
            try {
                fuguCallActivity?.onCalSwitched(jsonObject)
                Handler().postDelayed({
                    val linkArray = jsonObject?.getString("invite_link")?.split("/")!!
                    val intent = Intent(fuguCallActivity, VideoConfActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("base_url", com.skeleton.mvp.fugudatabase.CommonData.getConferenceUrl())
                    intent.putExtra("room_name", linkArray[linkArray.size - 1])
                    startActivity(intent)
                }, 100)

            } catch (e: Exception) {

            }
        }

    }

    fun videoConfReceived(videoConfInvitationJson: JSONObject?) {
        fuguCallActivity?.runOnUiThread {
            if (foregrounded()) {
                isCallFailed = false
                showVideoConfDialog(videoConfInvitationJson!!)
            } else {
                fireNotification(videoConfInvitationJson!!)
                Handler().postDelayed({
                    fuguCallActivity?.finish()
                }, 1000)
            }

//            AlertDialog.Builder(fuguCallActivity!!)
//                    .setMessage(videoConfInvitationJson?.getString(FULL_NAME) + fuguCallActivity?.resources?.getString(R.string.video_conference_invitation))
//                    .setPositiveButton("Join") { dialog, which ->
//                        fuguCallActivity?.onCallHungUp(videoConfInvitationJson)
//                        Handler().postDelayed({
//                            val linkArray = videoConfInvitationJson?.getString("invite_link")?.split("/")!!
//                            val intent = Intent(fuguCallActivity, VideoConfActivity::class.java)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            intent.putExtra("base_url", CONFERENCING_LIVE)
//                            intent.putExtra("room_name", linkArray[linkArray.size - 1])
//                            startActivity(intent)
//                        }, 500)
//
//                    }
//                    .setNegativeButton("Cancel") { dialog, which ->
//                        fuguCallActivity?.onCallHungUp(videoConfInvitationJson)
//                    }
//                    .setCancelable(false)
//                    .show()
        }
    }

    private fun fireNotification(videoConfInvitationJson: JSONObject) {
        val linkArray = videoConfInvitationJson.getString("invite_link").split("/")
        val notificationIntent = Intent(applicationContext, VideoConfActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        notificationIntent.putExtra("base_url", com.skeleton.mvp.fugudatabase.CommonData.getConferenceUrl())
        notificationIntent.putExtra("room_name", linkArray[linkArray.size - 1])

        val joinIntent = Intent(applicationContext, VideoConfActivity::class.java)
        joinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        joinIntent.putExtra("base_url", com.skeleton.mvp.fugudatabase.CommonData.getConferenceUrl())
        joinIntent.putExtra("room_name", linkArray[linkArray.size - 1])

        val hungupIntent = Intent(applicationContext, FuguCallActivity::class.java)
        hungupIntent.action = Intent.ACTION_DELETE
        hungupIntent.putExtra(CHANNEL_NAME, videoCallModel?.channelName)
        hungupIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)


        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val joinPendingIntent = PendingIntent.getActivity(this, 0,
                joinIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val hungupPendingIntent = PendingIntent.getActivity(this, 0,
                hungupIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationCompat.Builder(this, "VideoConf")
            notification.setContentTitle("Video Conference Invitation")
            notification.setContentText(videoConfInvitationJson.getString(FULL_NAME) + " " + fuguCallActivity?.resources?.getString(R.string.video_conference_invitation))
            notification.setSmallIcon(R.drawable.notification_white)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
            notification.setContentIntent(pendingIntent)
            notification.priority = getPriority()
            notification.setDefaults(Notification.DEFAULT_VIBRATE)
            val joinAction = NotificationCompat.Action.Builder(
                    android.R.drawable.sym_action_chat, "JOIN", joinPendingIntent)
                    .build()

            val cancelAction = NotificationCompat.Action.Builder(
                    android.R.drawable.sym_action_chat, "CANCEL", hungupPendingIntent)
                    .build()

            notification.addAction(joinAction)
            notification.addAction(cancelAction)

            startForeground(1223, notification.build())
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel("VideoConf",
                        "VideoConf", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationManager.notify(1223, notification.build())
        } else {

            val notification = NotificationCompat.Builder(this)
            notification.setContentTitle("Video Conference Invitation")
            notification.setContentText(videoConfInvitationJson.getString(FULL_NAME) + " " + fuguCallActivity?.resources?.getString(R.string.video_conference_invitation))
            notification.setSmallIcon(R.drawable.notification_white)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_fugu))
            notification.setContentIntent(pendingIntent)
            notification.setDefaults(Notification.DEFAULT_VIBRATE)
            notification.priority = getPriority()
            val hangupAction = NotificationCompat.Action.Builder(
                    android.R.drawable.sym_action_chat, "HANG UP", hungupPendingIntent)
                    .build()
            notification.addAction(hangupAction)
            startForeground(1223, notification.build())
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1223, notification.build())
        }
    }

    fun foregrounded(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
    }

    private fun showVideoConfDialog(videoConfInvitationJson: JSONObject) {
        try {
            val dialog = Dialog(fuguCallActivity!!, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.setContentView(R.layout.fugu_video_conf_dialog)
            val lp = dialog.window!!.attributes
            lp.dimAmount = 0.5f
            dialog.window?.attributes = lp
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

            val join: AppCompatButton = dialog.findViewById(R.id.join)
            val cancel: AppCompatButton = dialog.findViewById(R.id.cancel)
            val tvConfInvitation: AppCompatTextView = dialog.findViewById(R.id.tvConfInvitation)

            tvConfInvitation.text = videoConfInvitationJson.getString(FULL_NAME) + fuguCallActivity?.resources?.getString(R.string.video_conference_invitation)

            val spanText = SpannableStringBuilder()
            spanText.append(videoConfInvitationJson.getString(FULL_NAME))
            spanText.append(" ")
            spanText.append(fuguCallActivity?.resources?.getString(R.string.video_conference_invitation))
            val txtSpannable = SpannableString(spanText)
            val boldSpan = StyleSpan(Typeface.BOLD)
            txtSpannable.setSpan(boldSpan, spanText.length - 15, spanText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvConfInvitation.setText(txtSpannable, TextView.BufferType.SPANNABLE)
            join.setOnClickListener {
                webRTCSignallingClient?.acceptConf()
                fuguCallActivity?.onCalSwitched(videoConfInvitationJson)
                Handler().postDelayed({
                    val linkArray = videoConfInvitationJson.getString("invite_link").split("/")
                    val intent = Intent(fuguCallActivity, VideoConfActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("base_url", com.skeleton.mvp.fugudatabase.CommonData.getConferenceUrl())
                    intent.putExtra("room_name", linkArray[linkArray.size - 1])
                    startActivity(intent)
                }, 100)
            }

            cancel.setOnClickListener {
                fuguCallActivity?.onCallHungUp(videoConfInvitationJson, false)
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


//    private fun apiSendFeedback(rating: Float, type: String, feedback: String) {
//
//        val jsonObject = JSONObject()
//        val gson = Gson()
//        val json = gson.toJson(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo())
//        try {
//            jsonObject.put("workspace_name", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceName)
//            jsonObject.put("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceId)
//            jsonObject.put("type", type)
//            jsonObject
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        val commonParams = com.skeleton.mvp.data.network.CommonParams.Builder()
//        if (!TextUtils.isEmpty(feedback)) {
//            commonParams.add("feedback", feedback)
//        }
//        commonParams.add("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceId)
//        commonParams.add("type", type)
//        commonParams.add("rating", rating.toInt())
//        commonParams.add("extra_details", jsonObject.toString())
//
//        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).sendFeedback(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().map)
//                .enqueue(object : ResponseResolver<CommonResponse>() {
//                    override fun success(commonResponse: CommonResponse) {
//                        Toast.makeText(this@VideoCallService, "Feedback Submitted", Toast.LENGTH_LONG).show()
//                        fuguCallActivity?.onCallHungUp(null)
//                    }
//
//                    override fun failure(error: APIError) {
//                        Toast.makeText(this@VideoCallService, error.message, Toast.LENGTH_LONG).show()
//                        fuguCallActivity?.onCallHungUp(null)
//                    }
//                })
//    }

    fun onHungupSent() {
        fuguCallActivity?.onHungupSent()
    }

    fun cancelCallDisconnectTimer() {
        callDisconnectTime?.cancel()
    }

    fun cancelStartCallTimer() {
        webRTCSignallingClient?.cancelCounter()
    }

    fun reInitSocket() {
        webRTCSignallingClient?.setUpFayeConnection(false)
    }

    fun cancelCalltimer() {
        callTimer?.cancel()
    }


//    fun setVideoSource() {
//
//    }
//
//    fun getVideoSource(): VideoSource? {
//
//    }
}
