package com.skeleton.mvp.activity


class VideoCallingActivity  {
//    private val MY_PERMISSIONS_REQUEST_CAMERA = 100
//    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101
//    private val MY_PERMISSIONS_REQUEST = 102
//    private lateinit var localVideoView: SurfaceViewRenderer
//    private lateinit var remoteVideoView: SurfaceViewRenderer
//    private var localAudioTrack: AudioTrack? = null
//    private var localVideoTrack: VideoTrack? = null
//    private var peerConnectionFactory: PeerConnectionFactory? = null
//    private lateinit var switchCamera: ImageButton
//    private lateinit var muteAudio: ImageButton
//    private lateinit var muteVideo: ImageButton
//    private var localRenderer: VideoRenderer? = null
//    var dX: Float = 0.toFloat()
//    var dY: Float = 0.toFloat()
//    var lastAction: Int = 0
//    var isFrontCamera = true
//    var isAudioMuted = false
//    var isVideoMuted = false
//    private var localStream: MediaStream? = null
//    private var peerConnection: PeerConnection? = null
//    private var mClient: FayeClient? = null
//    private lateinit var fuguConversation: FuguConversation
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_video_calling)
//        askForPermissions()
//        setUpFayeConnection()
//        fuguConversation = Gson().fromJson(intent.getStringExtra(FuguAppConstant.CONVERSATION),
//                FuguConversation::class.java)
//        fuguConversation.userId = java.lang.Long.valueOf(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userId)
////        Handler().postDelayed({
////            val animation = AnimationUtils.loadAnimation(this, R.anim.video_shrink)
////            animation?.setAnimationListener(this)
////            localVideoView.startAnimation(animation)
////        }, 1000)
//    }
//
//    private fun setUpFayeConnection() {
//        FuguConfig.getClient { fayeClient ->
//            mClient = fayeClient
//            mClient?.connectServer()
//            mClient?.listener = object : FayeClientListener {
//                override fun onConnectedServer(fc: FayeClient?) {
//                    mClient = fc
//                    fc?.subscribeChannel("/" + fuguConversation.channelId)
//                    runOnUiThread { initViews() }
//                }
//
//                override fun onDisconnectedServer(fc: FayeClient?) {
//                }
//
//                override fun onReceivedMessage(fc: FayeClient?, msg: String?, channel: String?) {
//                }
//
//            }
//        }
//    }
//
//    fun askForPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
//                    MY_PERMISSIONS_REQUEST)
//        } else if (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.RECORD_AUDIO),
//                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO)
//
//        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.CAMERA),
//                    MY_PERMISSIONS_REQUEST_CAMERA)
//        }
//    }
//
//    private fun initViews() {
//        localVideoView = findViewById(R.id.sv_local)
//        remoteVideoView = findViewById(R.id.sv_remote)
//        switchCamera = findViewById(R.id.switchCamera)
//        muteAudio = findViewById(R.id.muteAudio)
//        muteVideo = findViewById(R.id.muteVideo)
//
//
//        val rootEglBase = EglBase.create()
//        localVideoView.init(rootEglBase.eglBaseContext, null)
//        localVideoView.setZOrderMediaOverlay(true)
//        remoteVideoView.init(rootEglBase.eglBaseContext, null)
//        remoteVideoView.setZOrderMediaOverlay(true)
//        localVideoView.setMirror(true)
//        remoteVideoView.setMirror(false)
//        localVideoView.setOnTouchListener(this)
//        val sdpConstraints = MediaConstraints()
//        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"))
//        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveVideo", "true"))
//        createLocalPeerConnection(sdpConstraints)
//        initiateSelfView()
//
//        switchCamera.setOnClickListener {
//            val videoGrabberAndroid = createVideoGrabber(isFrontCamera)
//            if (localRenderer != null) {
//                localVideoTrack?.removeRenderer(localRenderer)
//            }
//            setLocalRendering(videoGrabberAndroid)
//        }
//        muteAudio.setOnClickListener {
//            if (isAudioMuted) {
//                localStream?.audioTracks?.first?.setEnabled(true)
//            } else {
//                localStream?.audioTracks?.first?.setEnabled(false)
//            }
//            isAudioMuted = !isAudioMuted
//        }
//        muteVideo.setOnClickListener {
//            if (isVideoMuted) {
//                localStream?.videoTracks?.first?.setEnabled(true)
//            } else {
//                localStream?.videoTracks?.first?.setEnabled(false)
//            }
//            isVideoMuted = !isVideoMuted
//        }
//    }
//
//    private fun initiateSelfView() {
//        val videoGrabberAndroid = createVideoGrabber(true)
//        setLocalRendering(videoGrabberAndroid)
//        localStream = peerConnectionFactory?.createLocalMediaStream("102")
//        localStream?.addTrack(localAudioTrack)
//        localStream?.addTrack(localVideoTrack)
//        peerConnection?.addStream(localStream)
//    }
//
//    private fun setLocalRendering(videoGrabberAndroid: VideoCapturer?) {
//        val constraints = MediaConstraints()
//
//        val videoSource = peerConnectionFactory?.createVideoSource(videoGrabberAndroid)
//        localVideoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)
//
//        val audioSource = peerConnectionFactory?.createAudioSource(constraints)
//        localAudioTrack = peerConnectionFactory?.createAudioTrack("101", audioSource)
//
//        videoGrabberAndroid?.startCapture(1000, 1000, 30)
//
//        localRenderer = VideoRenderer(localVideoView)
//        localVideoTrack?.addRenderer(localRenderer)
//    }
//
//    fun createVideoGrabber(isFrontCameraa: Boolean): VideoCapturer? {
//        isFrontCamera = !isFrontCamera
//        val videoCapturer: VideoCapturer?
//        videoCapturer = createCameraGrabber(Camera1Enumerator(false), isFrontCameraa)
//        return videoCapturer
//    }
//
//    fun createCameraGrabber(enumerator: CameraEnumerator, isFrontCamera: Boolean): VideoCapturer? {
//        val deviceNames = enumerator.deviceNames
//
//        if (isFrontCamera) {
//            for (deviceName in deviceNames) {
//                if (enumerator.isFrontFacing(deviceName)) {
//                    val videoCapturer = enumerator.createCapturer(deviceName, null)
//                    if (videoCapturer != null) {
//                        return videoCapturer
//                    }
//                }
//            }
//
//            for (deviceName in deviceNames) {
//                if (!enumerator.isFrontFacing(deviceName)) {
//                    val videoCapturer = enumerator.createCapturer(deviceName, null)
//                    if (videoCapturer != null) {
//                        return videoCapturer
//                    }
//                }
//            }
//        } else {
//            for (deviceName in deviceNames) {
//                if (!enumerator.isFrontFacing(deviceName)) {
//                    val videoCapturer = enumerator.createCapturer(deviceName, null)
//                    if (videoCapturer != null) {
//                        return videoCapturer
//                    }
//                }
//            }
//            for (deviceName in deviceNames) {
//                if (enumerator.isFrontFacing(deviceName)) {
//                    val videoCapturer = enumerator.createCapturer(deviceName, null)
//                    if (videoCapturer != null) {
//                        return videoCapturer
//                    }
//                }
//            }
//        }
//        return null
//    }
//
//    fun createLocalPeerConnection(sdpConstraints: MediaConstraints) {
//        if (peerConnection == null) {
//            val iceServers = ArrayList<PeerConnection.IceServer>()
//            val stunIceServer = PeerConnection.IceServer("stun:staging.fuguchat.com:3007")
//            val turnIceServer = PeerConnection.IceServer("turn:staging.fuguchat.com:3007", "admin", "password")
//            iceServers.add(stunIceServer)
//            iceServers.add(turnIceServer)
//
//
//            peerConnection = peerConnectionFactory?.createPeerConnection(iceServers, sdpConstraints, object : CustomPeerConnectionObserver("localPeerCreation") {
//                override fun onIceCandidate(iceCandidate: IceCandidate) {
//                    super.onIceCandidate(iceCandidate)
//                    try {
//                        Log.e("IceServer", iceCandidate.sdp)
//                        val json = JSONObject()
//                        json.put("type", "candidate")
//                        json.put("label", iceCandidate.sdpMLineIndex)
//                        json.put("id", iceCandidate.sdpMid)
//                        json.put("candidate", iceCandidate.sdp)
//                        json.put("user_id", fuguConversation.getUserId())
//                        mClient?.publish(fuguConversation.getChannelId(), "/" + fuguConversation.getChannelId()!!, json)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//
//                }
//            })
//        }
//    }
//
//
//    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//        val displaymetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displaymetrics)
//        val screenHight = displaymetrics.heightPixels
//        val screenWidth = displaymetrics.widthPixels
//        when (event?.actionMasked) {
//            MotionEvent.ACTION_DOWN -> {
//                dX = v!!.getX() - event.getRawX()
//                dY = v.getY() - event.getRawY()
//                lastAction = MotionEvent.ACTION_DOWN
//            }
//
//            MotionEvent.ACTION_MOVE -> {
//
//                if (((event.getRawX() + dX) <= 0
//                                || (event.getRawX() + dX) >= screenWidth - v!!.getWidth())
//                        || ((event.getRawY() + dY) <= 0
//                                || (event.getRawY() + dY) >= screenHight - v.getHeight())) {
//                    lastAction = MotionEvent.ACTION_MOVE
//                    return false
//                }
//                v.setY(event.getRawY() + dY)
//                v.setX(event.getRawX() + dX)
//                lastAction = MotionEvent.ACTION_MOVE;
//            }
//
//            MotionEvent.ACTION_UP -> {
//                if (lastAction == MotionEvent.ACTION_DOWN)
//                    Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
//            }
//
//            else -> {
//                return false
//            }
//        }
//        return true
//    }
//
//    override fun onAnimationRepeat(animation: Animation?) {
//    }
//
//    override fun onAnimationEnd(animation: Animation?) {
//    }
//
//    override fun onAnimationStart(animation: Animation?) {
//    }


}
