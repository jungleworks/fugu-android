package com.skeleton.mvp.activity;

import android.content.BroadcastReceiver;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONObject;
import org.webrtc.AudioTrack;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class VideoCallActivity extends BaseActivity  {

    private PeerConnection peerConnection;
    private PeerConnectionFactory peerConnectionFactory;
    private VideoTrack localVideoTrack, remoteVideoTrack;
    private AudioTrack localAudioTrack;
    private SurfaceViewRenderer remoteVideoView, localVideoView;
//    private FayeClient mClient;
    private MediaStream videoStream, remoteVideoStream;
    private VideoCapturer videoCapturer;
    private MediaConstraints sdpConstraints;
    private String TAG = this.getClass().getSimpleName();
    private MediaPlayer mediaPlayer;

    private String fullName = "";
    private Long userId;
    private Long channelId;
    private String userImage, dialerUserImage;
    private ImageView ivUserImage, ivDialerImage;
    private RippleBackground rippleBackground, dialerCallView;
    private LinearLayout llIncomingCallOptions;
    private LinearLayout llVideoCall;
    private RelativeLayout llDialingCallActions;

    private ImageView acceptCall, muteAudio, muteVideo;
    private ImageView rejectCall;
    private ImageButton hungUpCall, switchCamera;
    private JSONObject saveJsonAnswer = new JSONObject();
    private RelativeLayout incomingCalllayout, outgoingCallLayout;
    private String muid;
    private TextView tvDialerName, tvCallerName, tvStatus;

    private String deviceId = "";
    private int numberOfMaxInitCalls = 14;
    private int initialCalls = 1;
    private BroadcastReceiver wiredHeadsetReceiver;
    private LinearLayout lowerCallOptions;
    private RelativeLayout rlLocalView;
    private Animation slideDownLowerCallOptions, slideDownHungUp;
    private Animation slideUpLowerCallOptions, slideUpHungUp;

    private CountDownTimer animCountDownTimer;
    private TextView tvConnecting;
    private CountDownTimer callDisconnectTimer;
    private List<PeerConnection.IceServer> iceServers = new ArrayList<>();
    private String turnApiKey;
    private String username;
    private String credentail;
    private ArrayList<String> stunServersList;
    private ArrayList<String> turnServersList;
    private String channelName;
    private Long startTimer = 1L;
    private JSONObject videoOffer = null;
    private EglBase rootEglBase;
    private boolean isSelfCalling = true;
    private boolean isAudioEnabled = true;
    private boolean isVideoEnabled = true;
    private boolean isFrontFacingCamera = true;
    private boolean isLocalViewSmall = true;
    private boolean isCallRejected = false;
    private boolean isCallInitiated = false;


    private boolean callAnswered = false;
    private boolean isCallConnected = false;
    private boolean isCallOptionsVisible = true;
    private boolean isWirelessHeadSetConnected = false;
    private boolean isReadyForConnection = false;
    private boolean isVideocallErrorEncountered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
//        final Window win = getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        askForPermissions();
//        fetchIntentData();
    }

//    private void setUpfayeConnection() {
//        FuguConfig.getClient(fayeClient -> {
//            mClient = fayeClient;
//            mClient.connectServer();
//            fayeClient.setListener(new FayeClientListener() {
//                @Override
//                public void onConnectedServer(FayeClient fc) {
//                    mClient = fc;
//                    fc.subscribeChannel("/" + channelId);
//                    new Handler().postDelayed(() -> {
//                        if (isSelfCalling) {
//                            initiateCall(false);
//                            lateInitCall();
//                        } else {
//                            replyForOffer();
//                        }
//                    }, 1000);
//                }
//
//                @Override
//                public void onDisconnectedServer(FayeClient fc) {
//                }
//
//                @Override
//                public void onReceivedMessage(FayeClient fc, String msg, String channel) {
//                    try {
//                        Log.e(TAG, msg);
//                        JSONObject json = new JSONObject(msg);
//                        Long myUserId = json.getLong(USER_ID);
//                        if (channel.equals("/" + channelId) &&
//                                json.has(MESSAGE_TYPE) && json.getInt(MESSAGE_TYPE) == 13
//                                && myUserId.compareTo(userId) != 0
//                                && json.getString(MESSAGE_UNIQUE_ID).equals(muid)) {
//                            if (json.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.VideoCallType.NEW_ICE_CANDIDATE.toString())) {
//                                if (peerConnection == null) {
//                                    createLocalPeerConnection(sdpConstraints);
//                                    videoStream = peerConnectionFactory.createLocalMediaStream("102");
//                                    videoStream.addTrack(localVideoTrack);
//                                    videoStream.addTrack(localAudioTrack);
//                                    peerConnection.addStream(videoStream);
//                                    saveIceCandidate(json, peerConnection);
//                                } else {
//                                    videoStream = peerConnectionFactory.createLocalMediaStream("102");
//                                    videoStream.addTrack(localVideoTrack);
//                                    videoStream.addTrack(localAudioTrack);
//                                    peerConnection.addStream(videoStream);
//                                    saveIceCandidate(json, peerConnection);
//                                }
//                            } else if (json.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.VideoCallType.VIDEO_OFFER.toString())) {
//                                if (!isCallConnected) {
//                                    if (peerConnection == null) {
//                                        createLocalPeerConnection(sdpConstraints);
//                                        videoStream = peerConnectionFactory.createLocalMediaStream("102");
//                                        videoStream.addTrack(localVideoTrack);
//                                        videoStream.addTrack(localAudioTrack);
//                                        peerConnection.addStream(videoStream);
//                                        if (callAnswered) {
//                                            runOnUiThread(() -> {
//                                                incomingCalllayout.setVisibility(View.GONE);
//                                                outgoingCallLayout.setVisibility(View.VISIBLE);
//                                                dialerCallView.setVisibility(View.GONE);
//                                                if (mediaPlayer != null) {
//                                                    mediaPlayer.stop();
//                                                }
//                                            });
//                                            saveJsonAnswer = json;
//                                            saveOfferAndAnswer(json, peerConnection);
//                                        } else {
//                                            saveJsonAnswer = json;
//                                        }
//                                    } else {
//                                        videoStream = peerConnectionFactory.createLocalMediaStream("102");
//                                        videoStream.addTrack(localVideoTrack);
//                                        videoStream.addTrack(localAudioTrack);
//                                        peerConnection.addStream(videoStream);
//                                        if (callAnswered) {
//                                            runOnUiThread(() -> {
//                                                incomingCalllayout.setVisibility(View.GONE);
//                                                outgoingCallLayout.setVisibility(View.VISIBLE);
//                                                dialerCallView.setVisibility(View.GONE);
//                                                if (mediaPlayer != null) {
//                                                    mediaPlayer.stop();
//                                                }
//                                            });
//                                            saveOfferAndAnswer(json, peerConnection);
//                                        } else {
//                                            saveJsonAnswer = json;
//                                        }
//                                    }
//                                }
//                            } else if (json.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.VideoCallType.VIDEO_ANSWER.toString())) {
//                                if (!isCallConnected) {
//                                    isCallConnected = true;
//                                    runOnUiThread(() -> {
//                                        incomingCalllayout.setVisibility(View.GONE);
//                                        outgoingCallLayout.setVisibility(View.VISIBLE);
//                                        dialerCallView.setVisibility(View.GONE);
//                                        if (mediaPlayer != null) {
//                                            mediaPlayer.stop();
//                                        }
//                                    });
//                                    saveAnswer(json, peerConnection);
//                                }
//                            } else if (json.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.VideoCallType.READY_TO_CONNECT.toString())) {
//                                isReadyForConnection = true;
//                                if (!isCallConnected && isSelfCalling) {
//                                    if (videoOffer == null && !isCallInitiated) {
//                                        isCallInitiated = true;
//                                        start();
//                                    } else {
//                                        if (videoOffer != null) {
//                                            mClient.publish(channelId, "/" + channelId, videoOffer);
//                                        }
//                                    }
//                                }
//                            } else if (json.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.VideoCallType.CALL_HUNG_UP.toString())) {
//                                mClient.unsubscribeAll();
//                                finish();
//                            } else if (json.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.VideoCallType.USER_BUSY.toString())) {
//                                if (!isCallConnected) {
//                                    hangUpCallAndDisplayMessage("busy on another call");
//                                }
//                            } else if (json.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.VideoCallType.CALL_REJECTED.toString())) {
//                                if (!isCallConnected) {
//                                    hangUpCallAndDisplayMessage("call declined");
//                                }
//                            }
//                        } else if (channel.equals("/" + channelId) &&
//                                json.has(MESSAGE_TYPE) && json.getInt(MESSAGE_TYPE) == 13
//                                && myUserId.compareTo(userId) == 0
//                                && json.getString(MESSAGE_UNIQUE_ID).equals(muid)) {
//                            if (json.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.VideoCallType.CALL_REJECTED.toString())) {
//                                finish();
//                            } else if (json.getString(VIDEO_CALL_TYPE).equals(VideoCallType.USER_BUSY.toString())) {
//                                if (!isCallConnected) {
//                                    hangUpCallAndDisplayMessage("busy on another call");
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onErrorReceived(FayeClient fc, String msg, String channel) {
//                    Log.e("Faye Message Error", msg);
//                    try {
//                        FayeVideoCallResponse fayeVideoCallResponse = new Gson().fromJson(msg, FayeVideoCallResponse.class);
//                        if (fayeVideoCallResponse.getStatusCode() == INVALID_VIDEO_CALL_CREDENTIALS) {
//                            isVideocallErrorEncountered = true;
//                            iceServers = new ArrayList<>();
//                            turnApiKey = fayeVideoCallResponse.getMessage().getTurnApiKey();
//                            username = fayeVideoCallResponse.getMessage().getUsername();
//                            credentail = fayeVideoCallResponse.getMessage().getCredentials();
//                            stunServersList = (ArrayList<String>) fayeVideoCallResponse.getMessage().getIceServers().getStun();
//                            turnServersList = (ArrayList<String>) fayeVideoCallResponse.getMessage().getIceServers().getTurn();
//
//                            new Thread() {
//                                @Override
//                                public void run() {
//                                    super.run();
//                                    Message turnCreds = CommonData.getCommonResponse().getData().getTurnCredentials();
//                                    turnCreds.setCredentials(credentail);
//                                    turnCreds.setUsername(username);
//                                    turnCreds.setTurnApiKey(turnApiKey);
//                                    turnCreds.getIceServers().setStun(stunServersList);
//                                    turnCreds.getIceServers().setTurn(turnServersList);
//                                }
//                            }.start();
//                            for (int i = 0; i < stunServersList.size(); i++) {
//                                PeerConnection.IceServer stunIceServer = PeerConnection.IceServer.builder(stunServersList.get(i))
//                                        .createIceServer();
//                                iceServers.add(stunIceServer);
//                            }
//                            for (int i = 0; i < turnServersList.size(); i++) {
//                                PeerConnection.IceServer turnIceServer = PeerConnection.IceServer.builder(turnServersList.get(i))
//                                        .setUsername(fayeVideoCallResponse.getMessage().getUsername())
//                                        .setPassword(fayeVideoCallResponse.getMessage().getCredentials())
//                                        .createIceServer();
//                                iceServers.add(turnIceServer);
//                            }
//                            if (isSelfCalling) {
//                                isVideocallErrorEncountered = false;
//                                initialCalls = 1;
//                                initiateCall(false);
//                            } else {
//                                replyForOffer();
//                            }
//                        }
//                    } catch (Exception e) {
//
//                    }
//                }
//            });
//        });
//    }
//
//    private void lateInitCall() {
//        new Handler().postDelayed(() -> {
//            if (!isVideocallErrorEncountered && !isReadyForConnection && initialCalls < numberOfMaxInitCalls) {
//                initiateCall(true);
//                initialCalls++;
//                lateInitCall();
//            }
//        }, 2000);
//    }
//
//    private void hangUpCallAndDisplayMessage(String message) {
//        tvStatus.formatString(message);
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//        }
//        mediaPlayer = MediaPlayer.create(VideoCallActivity.this, R.raw.busy_tone);
//        mediaPlayer.setLooping(false);
//        mediaPlayer.start();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hangup(false);
//                finish();
//            }
//        }, 3000);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (intent.getAction().equals(Intent.ACTION_DELETE)) {
//            hangup(false);
//            finish();
//        }
//    }
//
//    private void replyForOffer() {
//        try {
//            JSONObject json = new JSONObject();
//            json.put(VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.READY_TO_CONNECT.toString());
//            json.put(IS_SILENT, true);
//            json.put(USER_ID, userId);
//            json.put(FULL_NAME, fullName);
//            json.put(MESSAGE_TYPE, VIDEO_CALL);
//            json.put(IS_TYPING, TYPING_SHOW_MESSAGE);
//            json.put(MESSAGE_UNIQUE_ID, muid);
//            JSONObject devicePayload = new JSONObject();
//            devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(VideoCallActivity.this));
//            devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER);
//            devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME);
//            devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(VideoCallActivity.this));
//            json.put("device_payload", devicePayload);
//            json.put(MESSAGE_UNIQUE_ID, muid);
//
//            JSONObject videoCallCredentials = new JSONObject();
//            videoCallCredentials.put("turn_api_key", turnApiKey);
//            videoCallCredentials.put("username", username);
//            videoCallCredentials.put("credential", credentail);
//            JSONArray stunServers = new JSONArray();
//            for (int i = 0; i < stunServersList.size(); i++) {
//                stunServers.put(stunServersList.get(i));
//            }
//
//            JSONArray turnServers = new JSONArray();
//            for (int i = 0; i < turnServersList.size(); i++) {
//                turnServers.put(turnServersList.get(i));
//            }
//            videoCallCredentials.put("stun", stunServers);
//            videoCallCredentials.put("turn", turnServers);
//
//            json.put("turn_creds", videoCallCredentials);
//
//            mClient.publish(channelId, "/" + channelId, json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void initiateCall(boolean isSilent) {
//        try {
//            JSONObject json = new JSONObject();
//            json.put(VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.START_CALL.toString());
//            json.put(IS_SILENT, isSilent);
//            json.put(USER_ID, userId);
//            json.put(FULL_NAME, fullName);
//            json.put(MESSAGE_TYPE, VIDEO_CALL);
//            json.put(IS_TYPING, TYPING_SHOW_MESSAGE);
//            json.put(MESSAGE_UNIQUE_ID, muid);
//            JSONObject devicePayload = new JSONObject();
//            devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(VideoCallActivity.this));
//            devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER);
//            devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME);
//            devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(VideoCallActivity.this));
//
//            JSONObject videoCallCredentials = new JSONObject();
//            videoCallCredentials.put("turn_api_key", turnApiKey);
//            videoCallCredentials.put("username", username);
//            videoCallCredentials.put("credential", credentail);
//            JSONArray stunServers = new JSONArray();
//            for (int i = 0; i < stunServersList.size(); i++) {
//                stunServers.put(stunServersList.get(i));
//            }
//
//            JSONArray turnServers = new JSONArray();
//            for (int i = 0; i < turnServersList.size(); i++) {
//                turnServers.put(turnServersList.get(i));
//            }
//            videoCallCredentials.put("stun", stunServers);
//            videoCallCredentials.put("turn", turnServers);
//
//            json.put("turn_creds", videoCallCredentials);
//            json.put("device_payload", devicePayload);
//            mClient.publish(channelId, "/" + channelId, json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void saveIceCandidate(JSONObject json, PeerConnection peerConnection) throws JSONException {
//        if (peerConnection != null) {
//            JSONObject rtc_candidate = json.getJSONObject("rtc_candidate");
//            IceCandidate iceCandidate = new IceCandidate(rtc_candidate.getString(SDP_MID),
//                    Integer.parseInt(rtc_candidate.getString(SDP_M_LINE_INDEX)),
//                    rtc_candidate.getString(CANDIDATE));
//            peerConnection.addIceCandidate(iceCandidate);
//        }
//    }
//
//    public void saveAnswer(JSONObject json, PeerConnection peerConnection) throws JSONException {
//        JSONObject jsonObject = json.getJSONObject(SDP);
//        SessionDescription sessionDescription;
//        sessionDescription = new SessionDescription(SessionDescription.Type.ANSWER, jsonObject.getString(SDP));
//        setRemoteDescription(sessionDescription);
//    }
//
//    public void saveOfferAndAnswer(JSONObject json, PeerConnection peerConnection) throws JSONException {
//        JSONObject jsonObject = json.getJSONObject(SDP);
//        if (peerConnection != null) {
//            SessionDescription sessionDescription = new SessionDescription(SessionDescription.Type.OFFER, jsonObject.getString(SDP));
//            peerConnection.setRemoteDescription(new CustomSdpObserver(REMOTE_SET_REMOTE_DESC), sessionDescription);
//            MediaConstraints sdpConstraints = new MediaConstraints();
//            sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_AUDIO, "true"));
//            sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_VIDEO, "true"));
//            peerConnection.createAnswer(new CustomSdpObserver(REMOTE_CREATE_OFFER) {
//                @Override
//                public void onCreateSuccess(SessionDescription sessionDescription) {
//                    super.onCreateSuccess(sessionDescription);
//                    peerConnection.setLocalDescription(new CustomSdpObserver(REMOTE_SET_LOCAL_DESC), sessionDescription);
//                    try {
//                        JSONObject json = new JSONObject();
//                        json.put(VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.VIDEO_ANSWER);
//                        JSONObject sdpObject = new JSONObject();
//                        sdpObject.put("type", "answer");
//                        sdpObject.put(SDP, sessionDescription.description);
//                        json.put(SDP, sdpObject);
//                        json.put(USER_ID, userId);
//                        json.put(MESSAGE_TYPE, VIDEO_CALL);
//                        json.put(IS_SILENT, true);
//                        json.put(IS_TYPING, 0);
//                        JSONObject devicePayload = new JSONObject();
//                        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(VideoCallActivity.this));
//                        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER);
//                        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME);
//                        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(VideoCallActivity.this));
//                        json.put("device_payload", devicePayload);
//                        json.put(MESSAGE_UNIQUE_ID, muid);
//
//                        mClient.publish(channelId, "/" + channelId, json);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, sdpConstraints);
//        }
//    }
//
//    private void fetchIntentData() {
//        userId = getIntent().getLongExtra(USER_ID, -1L);
//        fullName = getIntent().getStringExtra(FULL_NAME);
//        channelId = getIntent().getLongExtra(CHANNEL_ID, -1L);
//        userImage = getIntent().getStringExtra("user_thumbnail_image");
//        isSelfCalling = !getIntent().hasExtra("INCOMING_VIDEO_CALL");
//        username = getIntent().getStringExtra(USER_NAME);
//        credentail = getIntent().getStringExtra(CREDENTIAL);
//        turnApiKey = getIntent().getStringExtra(TURN_API_KEY);
//        stunServersList = getIntent().getStringArrayListExtra(STUN_SERVERS);
//        turnServersList = getIntent().getStringArrayListExtra(TURN_SERVERS);
//        if (getIntent().hasExtra(CHANNEL_NAME)) {
//            channelName = getIntent().getStringExtra(CHANNEL_NAME);
//        }
//        if (getIntent().hasExtra(USER_THUMBNAIL_IMAGE)) {
//            dialerUserImage = getIntent().getStringExtra(USER_THUMBNAIL_IMAGE);
//        }
//        Intent startIntent = new Intent(VideoCallActivity.this, VideoCallService.class);
//        startIntent.setAction("com.fuguchat.start");
//        startIntent.putExtra(CHANNEL_NAME, channelName);
//        startIntent.putExtra(CALL_STATUS, "Ringing...");
//        ContextCompat.startForegroundService(this, startIntent);
//        callDisconnectTimer = new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//                if (!isCallConnected) {
//                    hangup(false);
//                    finish();
//                }
//            }
//        }.start();
//        initViews();
//        clickListneres();
//        setUpfayeConnection();
//        wiredHeadsetReceiver = new WiredHeadsetReceiver();
//    }
//
//    public void setRemoteDescription(SessionDescription sessionDescription) {
//        peerConnection.setRemoteDescription(new CustomSdpObserver(LOCAL_SET_REMOTE_DESC), sessionDescription);
//    }
//
//    public void askForPermissions() {
//        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) &&
//                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//                        != PackageManager.PERMISSION_GRANTED)) {
//            int MY_PERMISSIONS_REQUEST = 102;
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
//                    MY_PERMISSIONS_REQUEST);
//        } else if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECORD_AUDIO},
//                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
//
//        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            int MY_PERMISSIONS_REQUEST_CAMERA = 100;
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA},
//                    MY_PERMISSIONS_REQUEST_CAMERA);
//        }
//    }
//
//    public void initViews() {
//        localVideoView = findViewById(R.id.local_gl_surface_view);
//        remoteVideoView = findViewById(R.id.remote_gl_surface_view);
//        rippleBackground = findViewById(R.id.mainCallView);
//        dialerCallView = findViewById(R.id.dialerCallView);
//        llIncomingCallOptions = findViewById(R.id.llIncomingCallActions);
//        llDialingCallActions = findViewById(R.id.llDialingCallActions);
//        ivUserImage = findViewById(R.id.ivCallerImage);
//        acceptCall = findViewById(R.id.acceptCall);
//        incomingCalllayout = findViewById(R.id.incomingCallLayout);
//        outgoingCallLayout = findViewById(R.id.outgoingCallLayout);
//        hungUpCall = findViewById(R.id.hungUpCall);
//        rejectCall = findViewById(R.id.rejectCall);
//        muteAudio = findViewById(R.id.muteAudio);
//        muteVideo = findViewById(R.id.muteVideo);
//        switchCamera = findViewById(R.id.switchCamera);
//        ivDialerImage = findViewById(R.id.ivDialerImage);
//        tvDialerName = findViewById(R.id.tvDialerName);
//        tvCallerName = findViewById(R.id.tvCallerName);
//        rlLocalView = findViewById(R.id.rlLocalView);
//        tvConnecting = findViewById(R.id.tvConnecting);
//        lowerCallOptions = findViewById(R.id.lowerCallOptions);
//        tvStatus = findViewById(R.id.tvStatus);
//        rippleBackground.startRippleAnimation();
//        dialerCallView.startRippleAnimation();
//        tvDialerName.formatString(channelName);
//        tvCallerName.formatString(channelName);
//        Glide.with(this).load(userImage).asBitmap()
//                .centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .placeholder(ContextCompat.getDrawable(this, R.drawable.man))
//                .error(ContextCompat.getDrawable(this, R.drawable.man))
//                .into(new BitmapImageViewTarget(ivUserImage) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        RoundedBitmapDrawable circularBitmapDrawable =
//                                RoundedBitmapDrawableFactory.create(getResources(), resource);
//                        circularBitmapDrawable.setCircular(true);
//                        ivUserImage.setImageDrawable(circularBitmapDrawable);
//                    }
//                });
//        if (!TextUtils.isEmpty(dialerUserImage)) {
//            Glide.with(this).load(dialerUserImage).asBitmap()
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                    .placeholder(ContextCompat.getDrawable(this, R.drawable.man))
//                    .error(ContextCompat.getDrawable(this, R.drawable.man))
//                    .into(new BitmapImageViewTarget(ivDialerImage) {
//                        @Override
//                        protected void setResource(Bitmap resource) {
//                            RoundedBitmapDrawable circularBitmapDrawable =
//                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
//                            circularBitmapDrawable.setCircular(true);
//                            ivDialerImage.setImageDrawable(circularBitmapDrawable);
//                        }
//                    });
//        }
//        if (isSelfCalling) {
//            incomingCalllayout.setVisibility(View.GONE);
//            outgoingCallLayout.setVisibility(View.VISIBLE);
//            initiateOutgoingRinging();
//            muid = UUID.randomUUID().toString();
//        } else {
//            incomingCalllayout.setVisibility(View.VISIBLE);
//            outgoingCallLayout.setVisibility(View.GONE);
//            if (getIntent().hasExtra(MESSAGE_UNIQUE_ID)) {
//                muid = getIntent().getStringExtra(MESSAGE_UNIQUE_ID);
//            } else {
//                finish();
//            }
//            initiateIncomingRinging();
//        }
//        localVideoView.setMirror(true);
//        remoteVideoView.setMirror(false);
//        rootEglBase = EglBase.create();
//        localVideoView.init(rootEglBase.getEglBaseContext(), null);
//        localVideoView.setZOrderOnTop(true);
//        remoteVideoView.setZOrderMediaOverlay(true);
//        remoteVideoView.init(rootEglBase.getEglBaseContext(), null);
//        PeerConnectionFactory.InitializationOptions initializationOptions =
//                PeerConnectionFactory.InitializationOptions.builder(this)
//                        .createInitializationOptions();
//        PeerConnectionFactory.initialize(initializationOptions);
//        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(
//                rootEglBase.getEglBaseContext(),  /* enableIntelVp8Encoder */true,  /* enableH264HighProfile */true);
//        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
//        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
//        peerConnectionFactory = PeerConnectionFactory.builder()
//                .setOptions(options)
//                .setVideoEncoderFactory(defaultVideoEncoderFactory)
//                .setVideoDecoderFactory(defaultVideoDecoderFactory)
//                .createPeerConnectionFactory();
//
//
//        VideoCapturer videoGrabberAndroid = createVideoGrabber(true);
//        MediaConstraints constraints = new MediaConstraints();
//        VideoSource videoSource = peerConnectionFactory.createVideoSource(false);
//        CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) videoGrabberAndroid;
//        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
//        AudioSource audioSource = peerConnectionFactory.createAudioSource(constraints);
//        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
//        SurfaceTextureHelper surfaceTextureHelper =
//                SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
//        cameraVideoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
//        cameraVideoCapturer.startCapture(1000, 1000, 30);
//        videoStream = peerConnectionFactory.createLocalMediaStream("102");
//        videoStream.addTrack(localVideoTrack);
//        videoStream.addTrack(localAudioTrack);
//        localVideoTrack.addSink(localVideoView);
//        new Handler().postDelayed(() -> {
//            if (isCallOptionsVisible) {
//                isCallOptionsVisible = !isCallOptionsVisible;
//                lowerCallOptions.setVisibility(View.GONE);
//                hungUpCall.setVisibility(View.GONE);
//            }
//        }, 3000);
//    }
//
//    private void clickListneres() {
//        muteAudio.setOnClickListener(this);
//        muteVideo.setOnClickListener(this);
//        rejectCall.setOnClickListener(v -> {
//            hangup(true);
//            finish();
//        });
//        hungUpCall.setOnClickListener(this);
//        acceptCall.setOnClickListener(v -> {
//            callAnswered = true;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    llIncomingCallOptions.setVisibility(View.GONE);
//                    tvConnecting.setVisibility(View.VISIBLE);
//                }
//            });
//            try {
//                saveOfferAndAnswer(saveJsonAnswer, peerConnection);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        });
//        switchCamera.setOnClickListener(this);
//        llVideoCall = findViewById(R.id.llVideoCall);
//        remoteVideoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isCallOptionsVisible) {
//                    lowerCallOptions.setVisibility(View.GONE);
//                    hungUpCall.setVisibility(View.GONE);
//                    if (animCountDownTimer != null) {
//                        animCountDownTimer.cancel();
//                    } else {
//                        animCountDownTimer = new CountDownTimer(3000, 1000) {
//
//                            public void onTick(long millisUntilFinished) {
//                            }
//
//                            public void onFinish() {
//                                isCallOptionsVisible = !isCallOptionsVisible;
//                                lowerCallOptions.setVisibility(View.GONE);
//                                hungUpCall.setVisibility(View.GONE);
//                            }
//                        }.start();
//                    }
//                } else {
//                    lowerCallOptions.setVisibility(View.VISIBLE);
//                    hungUpCall.setVisibility(View.VISIBLE);
//                    animCountDownTimer = new CountDownTimer(3000, 1000) {
//
//                        public void onTick(long millisUntilFinished) {
//                        }
//
//                        public void onFinish() {
//                            isCallOptionsVisible = !isCallOptionsVisible;
//                            lowerCallOptions.setVisibility(View.GONE);
//                            hungUpCall.setVisibility(View.GONE);
//                        }
//                    }.start();
//                }
//                isCallOptionsVisible = !isCallOptionsVisible;
//            }
//        });
//        localVideoView.setOnClickListener(v -> {
//            if (isLocalViewSmall) {
//                localVideoTrack.removeSink(localVideoView);
//                remoteVideoTrack.removeSink(remoteVideoView);
//                localVideoTrack.addSink(remoteVideoView);
//                remoteVideoTrack.addSink(localVideoView);
//            } else {
//                localVideoTrack.removeSink(remoteVideoView);
//                remoteVideoTrack.removeSink(localVideoView);
//                localVideoTrack.addSink(localVideoView);
//                remoteVideoTrack.addSink(remoteVideoView);
//            }
//            isLocalViewSmall = !isLocalViewSmall;
//        });
//    }
//
//    private void enableListeners() {
//        hungUpCall.setOnClickListener(this);
//        muteVideo.setOnClickListener(this);
//        muteAudio.setOnClickListener(this);
//        switchCamera.setOnClickListener(this);
//    }
//
//    private void disableListeners() {
//        hungUpCall.setOnClickListener(null);
//        muteVideo.setOnClickListener(null);
//        muteAudio.setOnClickListener(null);
//        switchCamera.setOnClickListener(null);
//    }
//
//    private void clearAndSetAnimations(Animation animation, Animation animation2) {
//        lowerCallOptions.clearAnimation();
//        rlLocalView.clearAnimation();
//        hungUpCall.clearAnimation();
//        lowerCallOptions.startAnimation(animation);
//        rlLocalView.startAnimation(animation);
//        hungUpCall.startAnimation(animation2);
//    }
//
//    private void switchCamerarecorder() {
//        if (peerConnection != null) {
//            switchCamera.setOnClickListener(null);
//            switchCamera.setAlpha(0.5f);
//            isFrontFacingCamera = !isFrontFacingCamera;
//
//            videoStream.removeTrack(localVideoTrack);
//            if (isLocalViewSmall) {
//                localVideoTrack.removeSink(localVideoView);
//            } else {
//                localVideoTrack.removeSink(remoteVideoView);
//            }
//
//            peerConnection.removeStream(videoStream);
//            EglBase rootEglBase = EglBase.create();
//            try {
//                videoCapturer.stopCapture();
//                new Handler().postDelayed(() -> {
//                    switchCamera.setOnClickListener(this);
//                    switchCamera.setAlpha(1f);
//                    VideoCapturer videoGrabberAndroid = createVideoGrabber(isFrontFacingCamera);
//                    VideoSource videoSource = peerConnectionFactory.createVideoSource(false);
//                    CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) videoGrabberAndroid;
//                    localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
//                    SurfaceTextureHelper surfaceTextureHelper =
//                            SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
//                    cameraVideoCapturer.initialize(surfaceTextureHelper, VideoCallActivity.this, videoSource.getCapturerObserver());
//                    cameraVideoCapturer.startCapture(1000, 1000, 30);
//
//                    videoStream.addTrack(localVideoTrack);
//                    peerConnection.addStream(videoStream);
//                    if (isLocalViewSmall) {
//                        localVideoTrack.addSink(localVideoView);
//                    } else {
//                        localVideoTrack.addSink(remoteVideoView);
//                    }
//                }, 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void initiateOutgoingRinging() {
//        new Handler().postDelayed(() -> {
//            mediaPlayer = MediaPlayer.create(VideoCallActivity.this, R.raw.ringing);
//            mediaPlayer.setLooping(true);
//            mediaPlayer.start();
//        }, 1000);
//    }
//
//    private void initiateIncomingRinging() {
//        new Handler().postDelayed(() -> {
//            mediaPlayer = MediaPlayer.create(VideoCallActivity.this, R.raw.video_call_ringtone);
//            mediaPlayer.setLooping(true);
//            mediaPlayer.start();
//        }, 1000);
//    }
//
//    public void start() {
//        sdpConstraints = new MediaConstraints();
//        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_AUDIO, "true"));
//        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_VIDEO, "true"));
//        createLocalPeerConnection(sdpConstraints);
//        videoStream = peerConnectionFactory.createLocalMediaStream("102");
//        videoStream.addTrack(localVideoTrack);
//        videoStream.addTrack(localAudioTrack);
//        videoStream.audioTracks.get(0).setEnabled(true);
//        peerConnection.addStream(videoStream);
//        createLocalOffer(sdpConstraints);
//    }
//
//    public void createLocalPeerConnection(MediaConstraints sdpConstraints) {
//        PeerConnection.IceServer stunIceServer = PeerConnection.IceServer.builder("stun:turnserver.fuguchat.com:3478")
//                .createIceServer();
//        PeerConnection.IceServer turnIceServer = PeerConnection.IceServer.builder("turn:turnserver.fuguchat.com:19305?transport=UDP")
//                .setUsername("fuguadmin")
//                .setPassword("3FXCGBCnDfqsrOqs")
//                .createIceServer();
//        PeerConnection.IceServer turnIceServerTcp = PeerConnection.IceServer.builder("turn:turnserver.fuguchat.com:19305?transport=TCP")
//                .setUsername("fuguadmin")
//                .setPassword("3FXCGBCnDfqsrOqs")
//                .createIceServer();
////        PeerConnection.IceServer turnIceServerSecure = PeerConnection.IceServer.builder("turns:turnserver.fuguchat.com:5349?transport=UDP")
////                .setUsername("fuguadmin")
////                .setPassword("3FXCGBCnDfqsrOqs")
////                .createIceServer();
////        PeerConnection.IceServer turnIceServerTcpSecure = PeerConnection.IceServer.builder("turns:turnserver.fuguchat.com:5349?transport=TCP")
////                .setUsername("fuguadmin")
////                .setPassword("3FXCGBCnDfqsrOqs")
////                .createIceServer();
//
//
////        PeerConnection.IceServer stunIceServer = PeerConnection.IceServer.builder("stun:74.125.200.127:19302")
////                .createIceServer();
////        PeerConnection.IceServer turnIceServer = PeerConnection.IceServer.builder("turn:64.233.188.127:19305?transport=udp")
////                .setUsername("CIjR8twFEgYhlX3HtQoYzc/s6OMTIICjBQ")
////                .setPassword("b64oHkaVuv7YapRLyCApUiFiraw=")
////                .createIceServer();
////        PeerConnection.IceServer turnIceServerTcp = PeerConnection.IceServer.builder("turn:[2404:6800:4008:c06::7f]:19305?transport=udp")
////                .setUsername("CIjR8twFEgYhlX3HtQoYzc/s6OMTIICjBQ")
////                .setPassword("b64oHkaVuv7YapRLyCApUiFiraw=")
////                .createIceServer();
////        PeerConnection.IceServer turnIceServerSecure = PeerConnection.IceServer.builder("turn:64.233.188.127:19305?transport=tcp")
////                .setUsername("CIjR8twFEgYhlX3HtQoYzc/s6OMTIICjBQ")
////                .setPassword("b64oHkaVuv7YapRLyCApUiFiraw=")
////                .createIceServer();
////        PeerConnection.IceServer turnIceServerTcpSecure = PeerConnection.IceServer.builder("turn:[2404:6800:4008:c06::7f]:19305?transport=tcp")
////                .setUsername("CIjR8twFEgYhlX3HtQoYzc/s6OMTIICjBQ")
////                .setPassword("b64oHkaVuv7YapRLyCApUiFiraw=")
////                .createIceServer();
//
//
//        iceServers.add(stunIceServer);
//        iceServers.add(turnIceServer);
//        iceServers.add(turnIceServerTcp);
////        iceServers.add(turnIceServerSecure);
////        iceServers.add(turnIceServerTcpSecure);
//
//        PeerConnection.RTCConfiguration rtcConfiguration = new PeerConnection.RTCConfiguration(iceServers);
//        rtcConfiguration.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
//        rtcConfiguration.sdpSemantics = PeerConnection.SdpSemantics.PLAN_B;
//
//
//        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfiguration, new CustomPeerConnectionObserver("localPeerCreation", this) {
//            @Override
//            public void onIceCandidate(IceCandidate iceCandidate) {
//                super.onIceCandidate(iceCandidate);
//                try {
//                    JSONObject json = new JSONObject();
//                    json.put(VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.NEW_ICE_CANDIDATE.toString());
//                    JSONObject rtc_candidate = new JSONObject();
//                    rtc_candidate.put(SDP_M_LINE_INDEX, iceCandidate.sdpMLineIndex);
//                    rtc_candidate.put(SDP_MID, iceCandidate.sdpMid);
//                    rtc_candidate.put(CANDIDATE, iceCandidate.sdp);
//                    json.put("rtc_candidate", rtc_candidate);
//                    json.put(IS_SILENT, true);
//                    json.put(USER_ID, userId);
//                    json.put(MESSAGE_TYPE, VIDEO_CALL);
//                    json.put(IS_TYPING, 0);
//                    json.put(MESSAGE_UNIQUE_ID, muid);
//                    JSONObject devicePayload = new JSONObject();
//                    devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(VideoCallActivity.this));
//                    devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER);
//                    devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME);
//                    devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(VideoCallActivity.this));
//                    json.put("device_payload", devicePayload);
//                    json.put(MESSAGE_UNIQUE_ID, muid);
//                    mClient.publish(channelId, "/" + channelId, json);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
//                super.onSignalingChange(signalingState);
//                if (signalingState == PeerConnection.SignalingState.CLOSED) {
//                    finish();
//                }
//            }
//
//            public void onAddStream(MediaStream mediaStream) {
//                super.onAddStream(mediaStream);
//                runOnUiThread(() -> {
//                    incomingCalllayout.setVisibility(View.GONE);
//                    outgoingCallLayout.setVisibility(View.VISIBLE);
//                    dialerCallView.setVisibility(View.GONE);
//                    if (mediaPlayer != null) {
//                        mediaPlayer.stop();
//                    }
//                });
//                isCallConnected = true;
//                gotRemoteStream(mediaStream);
//            }
//        });
//    }
//
//    public void createLocalOffer(MediaConstraints sdpConstraints) {
//        if (videoOffer == null) {
//            peerConnection.createOffer(new CustomSdpObserver(LOCAL_CREATE_OFFER) {
//                @Override
//                public void onCreateSuccess(SessionDescription sessionDescription) {
//                    super.onCreateSuccess(sessionDescription);
//                    peerConnection.setLocalDescription(new CustomSdpObserver(LOCAL_SET_REMOTE_DESC), sessionDescription);
//                    try {
//                        JSONObject json = new JSONObject();
//                        json.put(VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.VIDEO_OFFER);
//
//                        JSONObject sdpObject = new JSONObject();
//                        sdpObject.put(SDP, sessionDescription.description);
//                        sdpObject.put("type", "offer");
//                        json.put(SDP, sdpObject);
//                        json.put(USER_ID, userId);
//                        json.put(MESSAGE_TYPE, VIDEO_CALL);
//                        json.put(IS_TYPING, 0);
//                        json.put(IS_SILENT, true);
//                        json.put(MESSAGE_UNIQUE_ID, muid);
//                        JSONObject devicePayload = new JSONObject();
//                        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(VideoCallActivity.this));
//                        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER);
//                        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME);
//                        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(VideoCallActivity.this));
//                        json.put("device_payload", devicePayload);
//                        json.put(MESSAGE_UNIQUE_ID, muid);
//                        videoOffer = json;
//                        mClient.publish(channelId, "/" + channelId, json);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, sdpConstraints);
//        } else {
//            mClient.publish(channelId, "/" + channelId, videoOffer);
//        }
//    }
//
//    public void hangup(boolean isCallRejected) {
//
//        try {
//            JSONObject json = new JSONObject();
//            if (isCallRejected) {
//                json.put(VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.CALL_REJECTED.toString());
//            } else {
//                json.put(VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.CALL_HUNG_UP.toString());
//            }
//            json.put(IS_SILENT, true);
//            json.put(USER_ID, userId);
//            json.put(FULL_NAME, fullName);
//            json.put(MESSAGE_TYPE, VIDEO_CALL);
//            json.put(IS_TYPING, TYPING_SHOW_MESSAGE);
//            json.put(MESSAGE_UNIQUE_ID, muid);
//            JSONObject devicePayload = new JSONObject();
//            devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(VideoCallActivity.this));
//            devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER);
//            devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME);
//            devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(VideoCallActivity.this));
//            json.put("device_payload", devicePayload);
//            json.put(MESSAGE_UNIQUE_ID, muid);
//            mClient.publish(channelId, "/" + channelId, json);
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public VideoCapturer createVideoGrabber(boolean isFrontFacing) {
//        videoCapturer = createCameraGrabber(new Camera1Enumerator(false), isFrontFacing);
//        return videoCapturer;
//    }
//
//    public VideoCapturer createCameraGrabber(CameraEnumerator enumerator, boolean isFrontFacing) {
//        final String[] deviceNames = enumerator.getDeviceNames();
//        if (isFrontFacing) {
//            for (String deviceName : deviceNames) {
//                if (enumerator.isFrontFacing(deviceName)) {
//                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
//                    if (videoCapturer != null) {
//                        return videoCapturer;
//                    }
//                }
//            }
//            for (String deviceName : deviceNames) {
//                if (!enumerator.isFrontFacing(deviceName)) {
//                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
//                    if (videoCapturer != null) {
//                        return videoCapturer;
//                    }
//                }
//            }
//        } else {
//            for (String deviceName : deviceNames) {
//                if (!enumerator.isFrontFacing(deviceName)) {
//                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
//                    if (videoCapturer != null) {
//                        return videoCapturer;
//                    }
//                }
//            }
//            for (String deviceName : deviceNames) {
//                if (enumerator.isFrontFacing(deviceName)) {
//                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
//                    if (videoCapturer != null) {
//                        return videoCapturer;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    private void gotRemoteStream(MediaStream stream) {
//        final VideoTrack videoTrack = stream.videoTracks.get(0);
//        remoteVideoStream = stream;
//        remoteVideoTrack = stream.videoTracks.get(0);
//
//        runOnUiThread(() -> {
//            try {
//                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                am.setMode(AudioManager.MODE_IN_CALL);
//                am.setSpeakerphoneOn(true);
//                videoTrack.addSink(remoteVideoView);
//                Intent startIntent = new Intent(VideoCallActivity.this, VideoCallService.class);
//                startIntent.setAction("com.fuguchat.start");
//                startIntent.putExtra(CHANNEL_NAME, channelName);
//                startIntent.putExtra(CALL_STATUS, ONGOING_VIDEO_CALL);
//                startIntent.putExtra(CALL_TIMER, startTimer);
//                ContextCompat.startForegroundService(this, startIntent);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        unregisterrecievers();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mVideoCallReciever,
//                new IntentFilter(FuguAppConstant.VIDEO_CALL_INTENT));
//        registerReceiver(wiredHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
//    }
//
//    private BroadcastReceiver mVideoCallReciever = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.hasExtra(CHANNEL_ID)
//                    && !intent.getStringExtra(MESSAGE_UNIQUE_ID).equals(muid)) {
//                if (intent.getStringExtra(VIDEO_CALL_TYPE).equals(VideoCallType.START_CALL.toString())) {
//                    try {
//                        JSONObject json = new JSONObject();
//                        json.put(VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.USER_BUSY.toString());
//                        json.put(IS_SILENT, true);
//                        json.put(USER_ID, intent.getLongExtra(USER_ID, -1L));
//                        json.put(FULL_NAME, fullName);
//                        json.put(MESSAGE_TYPE, VIDEO_CALL);
//                        json.put(IS_TYPING, TYPING_SHOW_MESSAGE);
//                        json.put(MESSAGE_UNIQUE_ID, intent.getStringExtra(MESSAGE_UNIQUE_ID));
//                        Log.e("MUID", intent.getStringExtra(MESSAGE_UNIQUE_ID));
//                        JSONObject devicePayload = new JSONObject();
//                        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(VideoCallActivity.this));
//                        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER);
//                        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME);
//                        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(VideoCallActivity.this));
//                        json.put("device_payload", devicePayload);
//                        mClient.publish(intent.getLongExtra(CHANNEL_ID, -1L), "/" + intent.getLongExtra(CHANNEL_ID, -1L), json);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else if (intent.getStringExtra(VIDEO_CALL_TYPE).equals(FuguAppConstant.VideoCallType.CALL_HUNG_UP.toString())) {
//                mClient.unsubscribeAll();
//                finish();
//            }
//        }
//    };
//
//    @Override
//
//    protected void onPause() {
//        super.onPause();
//    }
//
//    private void unregisterrecievers() {
//        try {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoCallReciever);
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(wiredHeadsetReceiver);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        isVideocallErrorEncountered = true;
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//        }
//        if (peerConnection != null) {
//            peerConnection.close();
//            peerConnection = null;
//        }
//        try {
//            videoCapturer.stopCapture();
//            videoCapturer.dispose();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer = MediaPlayer.create(VideoCallActivity.this, R.raw.disconnet_call);
//        mediaPlayer.setLooping(false);
//        mediaPlayer.start();
//        if (callDisconnectTimer != null) {
//            callDisconnectTimer.cancel();
//        }
//        mClient.unsubscribeAll();
//        unregisterrecievers();
//        Intent startIntent = new Intent(VideoCallActivity.this, VideoCallService.class);
//        startIntent.setAction("com.fuguchat.start");
//        stopService(startIntent);
//
//    }
//
//    @Override
//    public void onBackPressed() {
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.switchCamera:
//                switchCamerarecorder();
//                break;
//            case R.id.hungUpCall:
//                isCallRejected = true;
//                hangup(false);
//                finish();
//                break;
//            case R.id.muteAudio:
//                if (isAudioEnabled) {
//                    videoStream.audioTracks.get(0).setEnabled(false);
//                    muteAudio.setImageResource(R.drawable.ic_mute_microphone_enabled);
//                } else {
//                    videoStream.audioTracks.get(0).setEnabled(true);
//                    muteAudio.setImageResource(R.drawable.ic_mute_microphone_disabled);
//                }
//                isAudioEnabled = !isAudioEnabled;
//                break;
//            case R.id.muteVideo:
//                if (isVideoEnabled) {
//                    videoStream.videoTracks.get(0).setEnabled(false);
//                    muteVideo.setImageResource(R.drawable.ic_mute_video_enabled);
//                } else {
//                    videoStream.videoTracks.get(0).setEnabled(true);
//                    muteVideo.setImageResource(R.drawable.ic_mute_video_disabled);
//                }
//                isVideoEnabled = !isVideoEnabled;
//                break;
//        }
//    }
//
//    private class WiredHeadsetReceiver extends BroadcastReceiver {
//        private static final int STATE_UNPLUGGED = 0;
//        private static final int STATE_PLUGGED = 1;
//        private static final int HAS_NO_MIC = 0;
//        private static final int HAS_MIC = 1;
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int state = intent.getIntExtra("state", STATE_UNPLUGGED);
//            int microphone = intent.getIntExtra("microphone", HAS_NO_MIC);
//            String name = intent.getStringExtra("name");
//            android.util.Log.d(TAG, "WiredHeadsetReceiver.onReceive" + AppRTCUtils.getThreadInfo() + ": "
//                    + "a=" + intent.getAction() + ", s="
//                    + (state == STATE_UNPLUGGED ? "unplugged" : "plugged") + ", m="
//                    + (microphone == HAS_MIC ? "mic" : "no mic") + ", n=" + name + ", sb="
//                    + isInitialStickyBroadcast());
//            isWirelessHeadSetConnected = (state == STATE_PLUGGED);
//            if (isWirelessHeadSetConnected) {
//                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                am.setMode(AudioManager.MODE_IN_CALL);
//                am.setSpeakerphoneOn(false);
//                am.setWiredHeadsetOn(true);
//            } else {
//                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                am.setMode(AudioManager.MODE_IN_CALL);
//                am.setSpeakerphoneOn(true);
//            }
//        }
//    }
//
    public static final class AppRTCUtils {
        private AppRTCUtils() {
        }

        /**
         * Helper method which throws an exception  when an assertion has failed.
         */
        public static void assertIsTrue(boolean condition) {
            if (!condition) {
                throw new AssertionError("Expected condition to be true");
            }
        }

        /**
         * Helper method for building a string of thread information.
         */
        public static String getThreadInfo() {
            return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId()
                    + "]";
        }

        /**
         * Information about the current build, taken from system properties.
         */
        public static void logDeviceInfo(String tag) {
            android.util.Log.d(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", "
                    + "Release: " + Build.VERSION.RELEASE + ", "
                    + "Brand: " + Build.BRAND + ", "
                    + "Device: " + Build.DEVICE + ", "
                    + "Id: " + Build.ID + ", "
                    + "Hardware: " + Build.HARDWARE + ", "
                    + "Manufacturer: " + Build.MANUFACTURER + ", "
                    + "Model: " + Build.MODEL + ", "
                    + "Product: " + Build.PRODUCT);
        }
    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            VideoCapturer videoGrabberAndroid = createVideoGrabber(true);
//            MediaConstraints constraints = new MediaConstraints();
//            VideoSource videoSource = peerConnectionFactory.createVideoSource(false);
//            CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) videoGrabberAndroid;
//            localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
//            AudioSource audioSource = peerConnectionFactory.createAudioSource(constraints);
//            localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
//            SurfaceTextureHelper surfaceTextureHelper =
//                    SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
//            cameraVideoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
//            cameraVideoCapturer.startCapture(1000, 1000, 30);
//            videoStream = peerConnectionFactory.createLocalMediaStream("102");
//            videoStream.addTrack(localVideoTrack);
//            videoStream.addTrack(localAudioTrack);
//            localVideoTrack.addSink(localVideoView);
//            new Handler().postDelayed(() -> {
//                if (isCallOptionsVisible) {
//                    isCallOptionsVisible = !isCallOptionsVisible;
//                    lowerCallOptions.setVisibility(View.GONE);
//                    hungUpCall.setVisibility(View.GONE);
//                }
//            }, 3000);
//        }
//    }
}