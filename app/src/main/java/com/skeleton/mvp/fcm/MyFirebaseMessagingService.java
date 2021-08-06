package com.skeleton.mvp.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.CallFeedbackActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.model.fayeVideoCall.FayeVideoCallResponse;
import com.skeleton.mvp.model.fayeVideoCall.Message;
import com.skeleton.mvp.pushNotification.PushReceiver;
import com.skeleton.mvp.receiver.HungUpBroadcast;
import com.skeleton.mvp.service.OngoingCallService;
import com.skeleton.mvp.service.VideoCallService;
import com.skeleton.mvp.socket.SocketConnection;
import com.skeleton.mvp.ui.splash.SplashActivity;
import com.skeleton.mvp.util.AppConstant;
import com.skeleton.mvp.util.GeneralFunctions;
import com.skeleton.mvp.util.Utils;
import com.skeleton.mvp.utils.DateUtils;
import com.skeleton.mvp.utils.UniqueIMEIID;
import com.skeleton.mvp.videoCall.FuguCallActivity;
import com.skeleton.mvp.videoCall.VideoCallModel;
import com.skeleton.mvp.videoCall.WebRTCCallConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.PeerConnection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.socket.client.Socket;

import static com.skeleton.mvp.constant.FuguAppConstant.ANDROID_USER;
import static com.skeleton.mvp.constant.FuguAppConstant.APP_SECRET_KEY;
import static com.skeleton.mvp.constant.FuguAppConstant.APP_VERSION;
import static com.skeleton.mvp.constant.FuguAppConstant.AUDIO_CALL_NOTIFICATION;
import static com.skeleton.mvp.constant.FuguAppConstant.CALL_STATUS;
import static com.skeleton.mvp.constant.FuguAppConstant.CALL_TYPE;
import static com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_NAME;
import static com.skeleton.mvp.constant.FuguAppConstant.CREDENTIAL;
import static com.skeleton.mvp.constant.FuguAppConstant.DEVICE_DETAILS;
import static com.skeleton.mvp.constant.FuguAppConstant.DEVICE_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.DEVICE_PAYLOAD;
import static com.skeleton.mvp.constant.FuguAppConstant.DEVICE_TYPE;
import static com.skeleton.mvp.constant.FuguAppConstant.FULL_NAME;
import static com.skeleton.mvp.constant.FuguAppConstant.INCOMING_CALL_NOTIFICATION;
import static com.skeleton.mvp.constant.FuguAppConstant.INIT_FULL_SCREEN_SERVICE;
import static com.skeleton.mvp.constant.FuguAppConstant.INVALID_VIDEO_CALL_CREDENTIALS;
import static com.skeleton.mvp.constant.FuguAppConstant.INVITE_LINK;
import static com.skeleton.mvp.constant.FuguAppConstant.IS_SILENT;
import static com.skeleton.mvp.constant.FuguAppConstant.IS_TYPING;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_TYPE;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_UNIQUE_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.NOTIFICATION_TYPE;
import static com.skeleton.mvp.constant.FuguAppConstant.TITLE;
import static com.skeleton.mvp.constant.FuguAppConstant.TURN_API_KEY;
import static com.skeleton.mvp.constant.FuguAppConstant.TYPING_SHOW_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.USER_NAME;
import static com.skeleton.mvp.constant.FuguAppConstant.VIDEO_CALL;
import static com.skeleton.mvp.constant.FuguAppConstant.VIDEO_CALL_MODEL;
import static com.skeleton.mvp.constant.FuguAppConstant.VIDEO_CALL_NOTIFICATION;
import static com.skeleton.mvp.constant.FuguAppConstant.VIDEO_CALL_TYPE;
import static com.skeleton.mvp.constant.FuguAppConstant.VIDEO_CONFERENCE_HUNGUP_INTENT;

/**
 * Developer: Click Labs
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getName();
    private static final String DEFAULT_CHANNEL_ID = "default_01";
    private static final long[] NOTIFICATION_VIBRATION_PATTERN = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400};
    private static NotificationManager mNotificationManager;
    private PushReceiver fuguNotificationConfig = new PushReceiver().getInstance();
    private CountDownTimer countDown;
    private String INCOMING_VIDEO_CALL = "incoming Video Call...";
    private String INCOMING_AUDIO_CALL = "incoming Audio Call...";

    /**
     * Clear notifications
     */
    public static void clearNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        // Handle data payload of FCM messages.
        Log.d(TAG, getString(R.string.log_fcm_message_id) + remoteMessage.getMessageId());
        Log.d(TAG, getString(R.string.log_fcm_notification_message) + remoteMessage.getNotification());
        Log.d(TAG, getString(R.string.log_fcm_data) + remoteMessage.getData());
        Log.d(TAG, getString(R.string.log_fcm_data_message) + remoteMessage.getData().get(AppConstant.MESSAGE));
        /*
         * Foreground.get(getApplication()).isForeground() checks if the app is in foreground i.e visible not minimized or killed
         * if it is killed or minimized show notification
         */
        try {
            if (fuguNotificationConfig.isFuguNotification(remoteMessage.getData())) {
//                fuguNotificationConfig.setLargeIcon(R.drawable.notification_blue);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fuguNotificationConfig.setSmallIcon(R.drawable.notification_white);
                } else {
                    fuguNotificationConfig.setSmallIcon(R.drawable.ic_fugu);
                }
                ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
                JSONObject jsonObject = new JSONObject(remoteMessage.getData().get(MESSAGE));

//                if (jsonObject.getString(MESSAGE).toLowerCase().equals("hellotest")) {
//                    showPush();
//                }


                ArrayList<WorkspacesInfo> workspacesInfos = (ArrayList<WorkspacesInfo>) CommonData.getCommonResponse().data.getWorkspacesInfo();
                Long userId = -1L;
                String fullName = "";
                for (int i = 0; i < workspacesInfos.size(); i++) {

                    if (jsonObject.has(APP_SECRET_KEY) && workspacesInfos.get(i).getFuguSecretKey().equals(jsonObject.getString(APP_SECRET_KEY))) {
                        userId = Long.valueOf(workspacesInfos.get(i).getUserId());
                        fullName = workspacesInfos.get(0).getFullName();
                        break;
                    }
                }

                if (!taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.videoCall.FuguCallActivity")
                        && (jsonObject.getInt(NOTIFICATION_TYPE) == VIDEO_CALL_NOTIFICATION || jsonObject.getInt(NOTIFICATION_TYPE) == AUDIO_CALL_NOTIFICATION)
                        && jsonObject.has(VIDEO_CALL_TYPE)
                        && (jsonObject.getString(VIDEO_CALL_TYPE).equals("START_CALL"))
                        && jsonObject.has("is_silent")
                        && !jsonObject.getBoolean("is_silent")
                        && userId.compareTo(-1L) != 0
                        && !jsonObject.getString(DEVICE_ID).equals(UniqueIMEIID.getUniqueIMEIId(this))
                        && userId.compareTo(jsonObject.getLong("user_id")) != 0
                        && System.currentTimeMillis() - timeInMillis(jsonObject.getString("date_time")) < 30000) {
                    Message turnCredentials = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getTurnCredentials();
                    Intent videoIntent = new Intent(this, FuguCallActivity.class);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        countDown = new CountDownTimer(30000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                Log.e("Socket Connection Timer", millisUntilFinished + "");
                            }

                            @Override
                            public void onFinish() {

                                ActivityManager mngr1 = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                List<ActivityManager.RunningTaskInfo> taskList1 = mngr1.getRunningTasks(10);

                                if (!taskList1.get(0).topActivity.getClassName().contains("skeleton")) {
                                    SocketConnection.INSTANCE.disconnectSocket();
                                }
                            }
                        };
                        countDown.start();
                    }, 0);


                    String callType = "VIDEO";
                    if (jsonObject.has("call_type")) {
                        callType = jsonObject.getString("call_type");
                    }
                    VideoCallModel videoCallModel = new VideoCallModel(jsonObject.getLong("channel_id"),
                            jsonObject.getString("user_thumbnail_image"),
                            jsonObject.getString(TITLE),
                            userId, jsonObject.getLong("user_id"), fullName, turnCredentials.getTurnApiKey(),
                            turnCredentials.getUsername(), turnCredentials.getCredentials(),
                            (ArrayList<String>) (turnCredentials.getIceServers().getStun()),
                            (ArrayList<String>) (turnCredentials.getIceServers().getTurn()),
                            WebRTCCallConstants.AcitivityLaunchState.OTHER.toString(),
                            jsonObject.getString("muid"),
                            callType);
                    videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    videoIntent.putExtra("videoCallModel", videoCallModel);


                    SocketConnection.INSTANCE.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserId(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserChannel(), "Notification Reciever", true,
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getPushToken());


                    Long finalUserId = userId;
                    SocketConnection.INSTANCE.setSocketListeners(new SocketConnection.SocketClientCallback() {
                        @Override
                        public void onUnpinChat(@NotNull String messageJson) {
                        }

                        @Override
                        public void onPinChat(@NotNull String messageJson) {
                        }

                        @Override
                        public void onCalling(@NotNull String messageJson) {
                        }

                        @Override
                        public void onPresent(@NotNull String messageJson) {
                        }

                        @Override
                        public void onMessageSent(@NotNull String messageJson) {
                        }

                        @Override
                        public void onThreadMessageSent(@NotNull String messageJson) {
                        }

                        @Override
                        public void onMessageReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onTypingStarted(@NotNull String messageJson) {
                        }

                        @Override
                        public void onTypingStopped(@NotNull String messageJson) {
                        }

                        @Override
                        public void onThreadMessageReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onReadAll(@NotNull String messageJson) {
                        }

                        @Override
                        public void onPollVoteReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onReactionReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onVideoCallReceived(@NotNull String messageJson) {
                            callRecieved(messageJson, videoIntent, jsonObject, videoCallModel, finalUserId);
                        }

                        @Override
                        public void onAudioCallReceived(@NotNull String messageJson) {
                            Log.e("AUDIO CALL", messageJson);
                            callRecieved(messageJson, videoIntent, jsonObject, videoCallModel, finalUserId);
                        }

                        @Override
                        public void onChannelSubscribed() {
                        }

                        @Override
                        public void onConnect() {
                        }

                        @Override
                        public void onDisconnect() {
                        }

                        @Override
                        public void onConnectError(@NotNull Socket socket, @NotNull String message) {
                        }

                        @Override
                        public void onErrorReceived(@NotNull String messageJson) {

                            try {
                                FayeVideoCallResponse fayeVideoCallResponse = new Gson().fromJson(messageJson, FayeVideoCallResponse.class);
                                if (fayeVideoCallResponse.getStatusCode() == INVALID_VIDEO_CALL_CREDENTIALS) {

                                    ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<PeerConnection.IceServer>();
                                    FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
                                    Message turnCreds = fcCommonResponse.getData().getTurnCredentials();
                                    turnCreds.setCredentials(fayeVideoCallResponse.getMessage().getCredentials());
                                    turnCreds.setUsername(fayeVideoCallResponse.getMessage().getUsername());
                                    turnCreds.setTurnApiKey(fayeVideoCallResponse.getMessage().getTurnApiKey());
                                    turnCreds.getIceServers().setStun(fayeVideoCallResponse.getMessage().getIceServers().getStun());
                                    turnCreds.getIceServers().setTurn(fayeVideoCallResponse.getMessage().getIceServers().getTurn());
                                    fcCommonResponse.getData().setTurnCredentials(turnCreds);
                                    CommonData.setCommonResponse(fcCommonResponse);

                                    if (videoCallModel.getActivityLaunchState().equals(WebRTCCallConstants.AcitivityLaunchState.OTHER.toString())) {
                                        try {
                                            Message turnCredentials = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getTurnCredentials();
                                            videoCallModel.setTurnApiKey(turnCredentials.getTurnApiKey());
                                            videoCallModel.setStunServers((ArrayList<String>) turnCredentials.getIceServers().getStun());
                                            videoCallModel.setTurnServers((ArrayList<String>) turnCredentials.getIceServers().getTurn());
                                            videoCallModel.setTurnUserName(turnCredentials.getUsername());
                                            videoCallModel.setTurnCredential(turnCredentials.getCredentials());
                                            JSONObject readyToConnectJson = new JSONObject();
                                            readyToConnectJson.put(VIDEO_CALL_TYPE, "READY_TO_CONNECT");
                                            readyToConnectJson.put(IS_SILENT, true);
                                            readyToConnectJson.put(USER_ID, videoCallModel.getUserId());
                                            readyToConnectJson.put(FULL_NAME, videoCallModel.getFullName());
                                            readyToConnectJson.put(MESSAGE_TYPE, VIDEO_CALL);
                                            readyToConnectJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                                            readyToConnectJson.put(MESSAGE_UNIQUE_ID, videoCallModel.getSignalUniqueId());
                                            addTurnCredentialsAndDeviceDetails(readyToConnectJson, videoCallModel, videoCallModel.getChannelId() + "");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onTaskAssigned(@NotNull String messageJson) {

                        }

                        @Override
                        public void onMeetScheduled(@NotNull String messageJson) {

                        }

                        @Override
                        public void onUpdateNotificationCount(@NotNull String messageJson) {

                        }
                    });

//                    startActivity(videoIntent);
                } else if (jsonObject.getInt(NOTIFICATION_TYPE) == INCOMING_CALL_NOTIFICATION) {


                    if (jsonObject.has(VIDEO_CALL_TYPE) && jsonObject.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.JitsiCallType.START_CONFERENCE.toString())) {
                        if (OngoingCallService.NotificationServiceState.INSTANCE.isConferenceConnected()) {
                            emitUserBusy(jsonObject, userId);
                        }
                    }

                    if (jsonObject.has(VIDEO_CALL_TYPE) && jsonObject.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.JitsiCallType.HUNGUP_CONFERENCE.toString())
                            && OngoingCallService.NotificationServiceState.INSTANCE.getInviteLink().equals(jsonObject.getString("invite_link"))) {
                        if (OngoingCallService.NotificationServiceState.INSTANCE.isConferenceConnected()) {
                            Intent intent = new Intent(getApplicationContext(), CallFeedbackActivity.class);
                            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        }
                        Intent hungupIntent = new Intent(getApplicationContext(), HungUpBroadcast.class);
                        hungupIntent.putExtra("action", "rejectCall");
                        hungupIntent.putExtra(MESSAGE_UNIQUE_ID, jsonObject.getString(MESSAGE_UNIQUE_ID));
                        sendBroadcast(hungupIntent);
                        Intent mIntent = new Intent("CALL_HANGUP");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);

                        Intent mIntent2 = new Intent(VIDEO_CONFERENCE_HUNGUP_INTENT);
                        mIntent2.putExtra(INVITE_LINK, jsonObject.getString(INVITE_LINK));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent2);
                    }
                    if (jsonObject.has(VIDEO_CALL_TYPE) && jsonObject.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.JitsiCallType.REJECT_CONFERENCE.toString())
                            && OngoingCallService.NotificationServiceState.INSTANCE.getInviteLink().equals(jsonObject.getString("invite_link"))) {

                        Intent hungupIntent = new Intent(getApplicationContext(), HungUpBroadcast.class);
                        hungupIntent.putExtra("action", "rejectCall");
                        hungupIntent.putExtra(MESSAGE_UNIQUE_ID, jsonObject.getString(MESSAGE_UNIQUE_ID));
                        sendBroadcast(hungupIntent);
                        Intent mIntent = new Intent("CALL_HANGUP");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);


                        Intent mIntent2 = new Intent(VIDEO_CONFERENCE_HUNGUP_INTENT);
                        mIntent2.putExtra(INVITE_LINK, jsonObject.getString(INVITE_LINK));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent2);
                    }

                    SocketConnection.INSTANCE.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserId(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserChannel(), "Notification Receiver", true,
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getPushToken());
                    Long finalUserId1 = userId;
                    SocketConnection.INSTANCE.setSocketListeners(new SocketConnection.SocketClientCallback() {

                        @Override
                        public void onUnpinChat(@NotNull String messageJson) {
                        }

                        @Override
                        public void onPinChat(@NotNull String messageJson) {
                        }

                        @Override
                        public void onCalling(@NotNull String messageJson) {
                            try {
                                JSONObject data = new JSONObject(messageJson);

                                if (data.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.JitsiCallType.START_CONFERENCE.toString())
                                        && finalUserId1.compareTo(jsonObject.getLong("user_id")) != 0 && !OngoingCallService.NotificationServiceState.INSTANCE.isConferenceConnected()) {

                                    if (data.has("user_unique_key")
                                            && !data.getString("user_unique_key").equals(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserId())) {
                                        emitReadyToConnect(data, finalUserId1);
                                    }
                                } else if (data.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.JitsiCallType.OFFER_CONFERENCE.toString())
                                        && finalUserId1.compareTo(jsonObject.getLong("user_id")) != 0 && !OngoingCallService.NotificationServiceState.INSTANCE.isConferenceConnected()) {
                                    OngoingCallService.NotificationServiceState.INSTANCE.setMuid(data.getString(MESSAGE_UNIQUE_ID));
                                    OngoingCallService.NotificationServiceState.INSTANCE.setInviteLink(data.getString("invite_link"));
                                    OngoingCallService.NotificationServiceState.INSTANCE.setAppSecretKey(data.getString("app_secret_key"));
                                    fuguNotificationConfig.incomingCallNotification(getApplicationContext(), data);
                                } else if (data.getString(VIDEO_CALL_TYPE).equals(FuguAppConstant.JitsiCallType.REJECT_CONFERENCE.toString())
                                        && finalUserId1.compareTo(jsonObject.getLong("user_id")) != 0 && !OngoingCallService.NotificationServiceState.INSTANCE.isConferenceConnected()) {
                                    Intent startIntent = new Intent(getApplicationContext(), OngoingCallService.class);
                                    stopService(startIntent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onPresent(@NotNull String messageJson) {
                        }

                        @Override
                        public void onMessageSent(@NotNull String messageJson) {
                        }

                        @Override
                        public void onThreadMessageSent(@NotNull String messageJson) {
                        }

                        @Override
                        public void onMessageReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onTypingStarted(@NotNull String messageJson) {
                        }

                        @Override
                        public void onTypingStopped(@NotNull String messageJson) {
                        }

                        @Override
                        public void onThreadMessageReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onReadAll(@NotNull String messageJson) {
                        }

                        @Override
                        public void onPollVoteReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onReactionReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onVideoCallReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onAudioCallReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onChannelSubscribed() {
                        }

                        @Override
                        public void onConnect() {
                        }

                        @Override
                        public void onDisconnect() {
                        }

                        @Override
                        public void onConnectError(@NotNull Socket socket, @NotNull String message) {
                        }

                        @Override
                        public void onErrorReceived(@NotNull String messageJson) {
                        }

                        @Override
                        public void onTaskAssigned(@NotNull String messageJson) {

                        }

                        @Override
                        public void onMeetScheduled(@NotNull String messageJson) {

                        }

                        @Override
                        public void onUpdateNotificationCount(@NotNull String messageJson) {

                        }
                    });

                    Log.e("json", jsonObject.toString());
                } else {
                    fuguNotificationConfig.pushRedirection(getApplicationContext(), remoteMessage.getData(), false);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void emitReadyToConnect(JSONObject data, Long userId) {
        try {
            JSONObject startCallJson = new JSONObject();
            startCallJson.put(IS_SILENT, false);
            startCallJson.put(VIDEO_CALL_TYPE, FuguAppConstant.JitsiCallType.READY_TO_CONNECT_CONFERENCE);
            startCallJson.put(USER_ID, userId);
            startCallJson.put(CHANNEL_ID, data.getString(CHANNEL_ID));
            startCallJson.put(MESSAGE_TYPE, VIDEO_CALL);
            startCallJson.put(CALL_TYPE, data.getString(CALL_TYPE));
            startCallJson.put(DEVICE_PAYLOAD, getDeviceDetails());
            startCallJson.put(INVITE_LINK, data.getString(INVITE_LINK));
            startCallJson.put(MESSAGE_UNIQUE_ID, data.getString(MESSAGE_UNIQUE_ID));
            SocketConnection.INSTANCE.sendMessage(startCallJson);
            Log.e("Video_CONF-->", startCallJson.toString());
        } catch (Exception e) {

        }
    }

    private void emitUserBusy(JSONObject data, Long userId) {
        try {
            JSONObject startCallJson = new JSONObject();
            startCallJson.put(IS_SILENT, false);
            startCallJson.put(VIDEO_CALL_TYPE, FuguAppConstant.JitsiCallType.USER_BUSY_CONFERENCE);
            startCallJson.put(USER_ID, userId);
            startCallJson.put(CHANNEL_ID, data.getString(CHANNEL_ID));
            startCallJson.put(MESSAGE_TYPE, VIDEO_CALL);
            startCallJson.put(CALL_TYPE, data.getString(CALL_TYPE));
            startCallJson.put(DEVICE_PAYLOAD, getDeviceDetails());
            startCallJson.put(INVITE_LINK, data.getString(INVITE_LINK));
            startCallJson.put(MESSAGE_UNIQUE_ID, data.getString(MESSAGE_UNIQUE_ID));
            SocketConnection.INSTANCE.sendMessage(startCallJson);
            Log.e("Video_CONF-->", startCallJson.toString());
        } catch (Exception e) {

        }
    }


    private JSONObject getDeviceDetails() {
        try {
            JSONObject devicePayload = new JSONObject();
            devicePayload.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this));
            devicePayload.put(DEVICE_TYPE, ANDROID_USER);
            devicePayload.put(APP_VERSION, BuildConfig.VERSION_NAME);
            devicePayload.put(DEVICE_DETAILS, CommonData.deviceDetails(this));
            return devicePayload;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

//    private void showPush() {
//        if (!SocketConnection.INSTANCE.isSocketConnected()) {
//            SocketConnection.INSTANCE.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
//                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId(),
//                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserId(),
//                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserChannel(), "Notification Reciever", true);
//
//            SocketConnection.INSTANCE.setSocketListeners(new SocketConnection.SocketClientCallback() {
//                @Override
//                public void onMessageSent(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onThreadMessageSent(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onMessageReceived(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onTypingStarted(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onTypingStopped(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onThreadMessageReceived(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onReadAll(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onPollVoteReceived(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onReactionReceived(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onVideoCallReceived(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onAudioCallReceived(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onChannelSubscribed() {
//
//                }
//
//                @Override
//                public void onConnect() {
//                    showNotification();
//                    new Handler(Looper.getMainLooper()).postDelayed(() -> Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show(), 0);
//                    new Handler(Looper.getMainLooper()).postDelayed(() -> showNotification(), 30000);
//                    new Handler(Looper.getMainLooper()).postDelayed(SocketConnection.INSTANCE::disconnectSocket, 120000);
//                }
//
//                @Override
//                public void onDisconnect() {
//                    new Handler(Looper.getMainLooper()).postDelayed(() -> Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_LONG).show(), 0);
//                }
//
//                @Override
//                public void onConnectError(@NotNull Socket socket, @NotNull String message) {
//
//                }
//
//                @Override
//                public void onErrorRecieved(@NotNull String messageJson) {
//
//                }
//
//                @Override
//                public void onPresent(@NotNull String messageJson) {
//
//                }
//            });
//        } else {
//
//            showNotification();
//        }
//    }

    private Long timeInMillis(String timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        Long timeInMilliseconds = 0L;
        try {
            Date mDate = sdf.parse(DateUtils.getInstance().convertToLocal(timeStamp));
            timeInMilliseconds = mDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    private void callRecieved(String messageJson, Intent videoIntent, JSONObject
            notificationJson, VideoCallModel videoCallModel, Long myUserId) {

        try {
            JSONObject json = new JSONObject(messageJson);
            if (json.getString("channel_id").equals(notificationJson.getString("channel_id"))
                    && json.getString("muid").equals(notificationJson.getString("muid"))) {
                Log.e("SocketConnection---->", json.toString());

                if (json.getString(VIDEO_CALL_TYPE).equals("START_CALL")) {
                    JSONObject readyToConnectJson = new JSONObject();
                    readyToConnectJson.put(VIDEO_CALL_TYPE, "READY_TO_CONNECT");
                    readyToConnectJson.put(IS_SILENT, true);
                    readyToConnectJson.put("user_id", myUserId);
                    readyToConnectJson.put(MESSAGE_TYPE, VIDEO_CALL);
                    readyToConnectJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                    readyToConnectJson.put("muid", notificationJson.getString("muid"));
                    addTurnCredentialsAndDeviceDetails(readyToConnectJson, videoCallModel, notificationJson.getString("channel_id"));
                } else if (json.getString(VIDEO_CALL_TYPE).equals("VIDEO_OFFER")) {
                    if (!isCallActive(this)) {
                        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
                        countDown.cancel();
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                            if (!taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.activity.FuguCallActivity") && !taskList.get(0).topActivity.getClassName().contains("GrantPermissionsActivity")) {
                                videoIntent.putExtra("video_offer", messageJson);
                                if (!new GeneralFunctions().isMyServiceRunning(VideoCallService.class.getSimpleName(), getApplicationContext())) {
                                    startCallForegroundService(INCOMING_AUDIO_CALL, videoCallModel);
                                }
                            }
                        } else {
                            startActivity(videoIntent);
                        }
                    } else {
                        JSONObject readyToConnectJson = new JSONObject();
                        readyToConnectJson.put(VIDEO_CALL_TYPE, "USER_BUSY");
                        readyToConnectJson.put(IS_SILENT, true);
                        readyToConnectJson.put("user_id", myUserId);
                        readyToConnectJson.put(MESSAGE_TYPE, VIDEO_CALL);
                        readyToConnectJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                        readyToConnectJson.put("muid", notificationJson.getString("muid"));
                        addTurnCredentialsAndDeviceDetails(readyToConnectJson, videoCallModel, notificationJson.getString("channel_id"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startCallForegroundService(String status, VideoCallModel videoCallModel) {
        Intent startIntent = new Intent(getApplicationContext(), VideoCallService.class);
        String channelName = "";
//        Message turnCreds = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getTurnCredentials();
        channelName = videoCallModel.getChannelName();
        startIntent.setAction("com.officechat.start");
        startIntent.putExtra(CALL_STATUS, status);
        startIntent.putExtra(CHANNEL_NAME, channelName);
        startIntent.putExtra(VIDEO_CALL_MODEL, videoCallModel);
        startIntent.putExtra(INIT_FULL_SCREEN_SERVICE, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                CommonData.setVideoCallModel(videoCallModel);
            }
        }).start();
        try {
            ContextCompat.startForegroundService(this, startIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCallActive(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }

    private void addTurnCredentialsAndDeviceDetails(JSONObject
                                                            readyToConnectJson, VideoCallModel videoCallModel, String channelId) {
        try {
            JSONArray stunServers = new JSONArray();
            JSONArray turnServers = new JSONArray();
            JSONObject videoCallCredentials = new JSONObject();

            videoCallCredentials.put(TURN_API_KEY, videoCallModel.getTurnApiKey());
            videoCallCredentials.put(USER_NAME, videoCallModel.getTurnUserName());
            videoCallCredentials.put(CREDENTIAL, videoCallModel.getTurnCredential());
            for (int i = 0; i < videoCallModel.getStunServers().size(); i++) {
                stunServers.put(videoCallModel.getStunServers().get(i));
            }
            for (int i = 0; i < videoCallModel.getTurnServers().size(); i++) {
                turnServers.put(videoCallModel.getTurnServers().get(i));
            }


            JSONObject devicePayload = new JSONObject();
            devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this));
            devicePayload.put(DEVICE_TYPE, ANDROID_USER);
            devicePayload.put(APP_VERSION, BuildConfig.VERSION_NAME);
            devicePayload.put(DEVICE_DETAILS, CommonData.deviceDetails(this));

            videoCallCredentials.put("stun", stunServers);
            videoCallCredentials.put("turn", turnServers);
            readyToConnectJson.put("channel_id", channelId);
            readyToConnectJson.put("turn_creds", videoCallCredentials);
            readyToConnectJson.put(DEVICE_PAYLOAD, devicePayload);
            readyToConnectJson.put("call_type", videoCallModel.getCallType());
            SocketConnection.INSTANCE.sendMessage(readyToConnectJson);
        } catch (Exception e) {

        }
    }

    /**
     * Show notification
     */
    public void showNotification() {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.notification_channel_default);
            // The user-visible description of the channel.
            String description = getString(R.string.notification_channel_description_default);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(DEFAULT_CHANNEL_ID, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(NOTIFICATION_VIBRATION_PATTERN);
            mNotificationManager.createNotificationChannel(mChannel);
        }


        Notification mNotification = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Big Text"))
                .setSmallIcon(R.drawable.ic_fugu)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_fugu))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Message Text")
                .setContentIntent(pi)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setChannelId(DEFAULT_CHANNEL_ID)
                .build();
        mNotificationManager.notify(Utils.getRandomNumberBetween(1000, 50), mNotification);
    }
}
