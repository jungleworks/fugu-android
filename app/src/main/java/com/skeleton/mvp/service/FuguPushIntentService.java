package com.skeleton.mvp.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.activity.FuguInnerChatActivity;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.ChatDatabase;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.model.Message;
import com.skeleton.mvp.pushNotification.MultipleMessageNotification;
import com.skeleton.mvp.pushNotification.NotificationReciever;
import com.skeleton.mvp.pushNotification.PushReceiver;
import com.skeleton.mvp.retrofit.CommonParams;
import com.skeleton.mvp.retrofit.CommonResponse;
import com.skeleton.mvp.retrofit.RestClient;
import com.skeleton.mvp.socket.SocketConnection;
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity;
import com.skeleton.mvp.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static com.skeleton.mvp.constant.FuguAppConstant.ANDROID_USER;
import static com.skeleton.mvp.constant.FuguAppConstant.APP_SECRET_KEY;
import static com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.DATA;
import static com.skeleton.mvp.constant.FuguAppConstant.DATE_TIME;
import static com.skeleton.mvp.constant.FuguAppConstant.DEVICE_TYPE;
import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.FULL_NAME;
import static com.skeleton.mvp.constant.FuguAppConstant.IS_THREAD_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.IS_TYPING;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_TYPE;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_UNIQUE_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.NOTIFICATION_TAPPED;
import static com.skeleton.mvp.constant.FuguAppConstant.THREAD_MUID;
import static com.skeleton.mvp.constant.FuguAppConstant.TITLE;
import static com.skeleton.mvp.constant.FuguAppConstant.TYPING_SHOW_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.USER_TYPE;
import static com.skeleton.mvp.ui.AppConstants.APP_VERSION;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_ALREADY_MEMBER;


/**
 * Created by Bhavya Rattan on 26/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguPushIntentService extends IntentService {

    private static final String TAG = FuguAppConstant.APP_NAME_SHORT + "PushIntentService";

    public FuguPushIntentService() {
        super(TAG);
    }

    Long defaultId = -1L;
    Intent notificationIntent = null;
    Message messageToBeAdded;

    @Override
    protected void onHandleIntent(final Intent intent) {
        String message = (String) NotificationReciever.Reply.INSTANCE.getReplyMessage(intent);
        if (TextUtils.isEmpty(message)) {
            if ((intent.hasExtra("data") && intent.getBundleExtra("data").get("message").toString().contains("\"notification_type\":5"))) {
                Intent intent1 = new Intent(this, YourSpacesActivity.class);
                intent1.putExtra("API_HIT", true);
                intent1.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            } else if ((intent.hasExtra("data") && intent.getBundleExtra("data").get("message").toString().contains("\"notification_type\":17"))) {
                ChatDatabase.INSTANCE.removeCallNotifications((long) intent.getBundleExtra("data").getInt("business_id"));
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            } else if (intent.hasExtra("data") && intent.getBundleExtra("data").get("message").toString().contains("\"notification_type\":22")) {
                ChatDatabase.INSTANCE.removeCallNotifications((long) intent.getBundleExtra("data").getInt("business_id"));
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("openTasksTab", true);
                ArrayList<WorkspacesInfo> workspacesInfos = (ArrayList<WorkspacesInfo>) com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo();
                String appSecretKey = "";
                try {
                    JSONObject messageJson = new JSONObject(intent.getBundleExtra("data").getString("message"));
                    appSecretKey = messageJson.getString("app_secret_key");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < workspacesInfos.size(); i++) {
                    if (workspacesInfos.get(i).getFuguSecretKey().equals(appSecretKey)) {
                        SocketConnection.INSTANCE.disconnectSocket();
                        NotificationSockets.INSTANCE.setInitApi(false);
                        SocketConnection.INSTANCE.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(i).getEnUserId(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserId(),
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserChannel(), "ChangeBusiness", false,
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getPushToken());
                        com.skeleton.mvp.data.db.CommonData.setCurrentSignedInPosition(i);
                        break;
                    }
                }
                startActivity(intent1);
            } else if (intent.hasExtra("data") && intent.getBundleExtra("data").get("message").toString().contains("\"notification_type\":23")) {
                ChatDatabase.INSTANCE.removeCallNotifications((long) intent.getBundleExtra("data").getInt("business_id"));
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("openMeetTab", true);
                ArrayList<WorkspacesInfo> workspacesInfos = (ArrayList<WorkspacesInfo>) com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo();
                String appSecretKey = "";
                try {
                    JSONObject messageJson = new JSONObject(intent.getBundleExtra("data").getString("message"));
                    appSecretKey = messageJson.getString("app_secret_key");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < workspacesInfos.size(); i++) {
                    if (workspacesInfos.get(i).getFuguSecretKey().equals(appSecretKey)) {
                        SocketConnection.INSTANCE.disconnectSocket();
                        NotificationSockets.INSTANCE.setInitApi(false);
                        SocketConnection.INSTANCE.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(i).getEnUserId(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserId(),
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserChannel(), "ChangeBusiness", false,
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getPushToken());
                        com.skeleton.mvp.data.db.CommonData.setCurrentSignedInPosition(i);
                        break;
                    }
                }
                startActivity(intent1);
            } else if (intent.hasExtra("summary_notification")) {
                if (!(new MultipleMessageNotification()).foregrounded()) {
                    Intent intent1 = new Intent(this, MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
            } else {
                defaultId = intent.getLongExtra("channelId", -1L);
                try {
                    final JSONObject messageJson = new JSONObject(intent.getBundleExtra(DATA).getString(MESSAGE));
                    normalFlow(intent, messageJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                final JSONObject messageJson = new JSONObject(intent.getBundleExtra(DATA).getString(MESSAGE));
                String appSecretKey = messageJson.getString(APP_SECRET_KEY);
                CommonParams.Builder commonParams = new CommonParams.Builder();
                JSONObject jsonObject = new JSONObject();
                if (new JSONObject(intent.getBundleExtra(DATA).getString(MESSAGE)).getBoolean(IS_THREAD_MESSAGE)) {
                    jsonObject.put(THREAD_MUID, UUID.randomUUID().toString());
                    jsonObject.put(IS_THREAD_MESSAGE, true);
                    jsonObject.put(MESSAGE_UNIQUE_ID, new JSONObject(intent.getBundleExtra(DATA).getString(MESSAGE)).getString(MESSAGE_UNIQUE_ID));

                } else {
                    jsonObject.put(MESSAGE_UNIQUE_ID, UUID.randomUUID().toString());
                    jsonObject.put(IS_THREAD_MESSAGE, false);
                }
                jsonObject.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                jsonObject.put(USER_TYPE, FuguAppConstant.ANDROID_USER);
                jsonObject.put(USER_ID, CommonData.getWorkspaceResponse(appSecretKey).getUserId());
                jsonObject.put(FULL_NAME, CommonData.getWorkspaceResponse(appSecretKey).getFullName());
                jsonObject.put(DATE_TIME, DateUtils.getInstance().convertToUTC(DateUtils.getFormattedDate(new Date())));
                jsonObject.put(MESSAGE_TYPE, 1);
                jsonObject.put(MESSAGE, message.trim());
                commonParams.add(EN_USER_ID, CommonData.getWorkspaceResponse(appSecretKey).getEnUserId());
                commonParams.add(CHANNEL_ID, messageJson.getLong(CHANNEL_ID));
                commonParams.add(DATA, jsonObject.toString());
                RestClient.getApiInterface().sendMessage(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), appSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().getMap())
                        .enqueue(new ResponseResolver<CommonResponse>() {
                            @Override
                            public void onSuccess(CommonResponse commonResponse) {
                                try {
                                    ChatDatabase.INSTANCE.removeNotifications(messageJson.getLong(CHANNEL_ID));
                                    ChatDatabase.INSTANCE.setPushCount(messageJson.getLong(CHANNEL_ID), 0);
                                    NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    nMgr.cancel((int) messageJson.getLong(CHANNEL_ID));
                                    if (intent.hasExtra("timeStamp")) {
                                        nMgr.cancel(intent.getIntExtra("timeStamp", 0));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ApiError error) {

                            }

                            @Override
                            public void onFailure(Throwable throwable) {

                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void normalFlow(final Intent intent, JSONObject messageJsonObject) {
        try {
            messageToBeAdded = new Message(0,
                    "",
                    0L,
                    ".",
                    messageJsonObject.getString(FuguAppConstant.DATE_TIME),
                    1,
                    FuguAppConstant.MESSAGE_READ,
                    0,
                    "",
                    "",
                    1,
                    true,
                    messageJsonObject.getString(FuguAppConstant.MESSAGE_UNIQUE_ID),
                    messageJsonObject.getInt(FuguAppConstant.CHAT_TYPE),
                    "",
                    "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long id = intent.getLongExtra("channelId", -1L);
        ArrayList<WorkspacesInfo> workspacesInfos = (ArrayList<WorkspacesInfo>) com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo();
        if (FuguConfig.getInstance() != null && !FuguConfig.getInstance().isDataCleared() && id.compareTo(-1L) != 0) {
            notificationIntent = new Intent(this, ChatActivity.class);
            JSONObject messageJson = new JSONObject();
            String appSecretKey = "";
            int userType = FuguAppConstant.UserType.CUSTOMER;
            try {
                messageJson = new JSONObject(intent.getBundleExtra("data").getString("message"));
                appSecretKey = messageJson.getString("app_secret_key");
                userType = messageJson.getInt("user_type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < workspacesInfos.size(); i++) {
                if (workspacesInfos.get(i).getFuguSecretKey().equals(appSecretKey)) {
                    SocketConnection.INSTANCE.disconnectSocket();
                    NotificationSockets.INSTANCE.setInitApi(false);
                    SocketConnection.INSTANCE.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(i).getEnUserId(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserId(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserChannel(), "ChangeBusiness", false,
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getPushToken());
                    com.skeleton.mvp.data.db.CommonData.setCurrentSignedInPosition(i);
                    break;
                }
            }
            final FuguConversation conversation = new FuguConversation();
            conversation.setChannelId(intent.getLongExtra("channelId", -1l));
            conversation.setEnUserId(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId());
            conversation.setUserId(Long.valueOf(CommonData.getWorkspaceResponse(appSecretKey).getUserId()));
            conversation.setChat_type(intent.getIntExtra("chat_type", 0));
            conversation.setUserName(intent.getStringExtra("full_name"));
            conversation.setOtherUserType(userType);
            conversation.setBusinessName(CommonData.getWorkspaceResponse(appSecretKey).getWorkspaceName());
            try {
                conversation.setLabel(messageJson.getString(TITLE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            conversation.setJoined(true);
            CommonParams commonParams = new CommonParams.Builder()
                    .add("app_secret_key", appSecretKey)
                    .add("en_user_id", CommonData.getWorkspaceResponse(appSecretKey).getEnUserId())
                    .add("channel_id", intent.getLongExtra("channelId", -1l))
                    .add(DEVICE_TYPE, ANDROID_USER)
                    .add(APP_VERSION, BuildConfig.VERSION_NAME)
                    .build();
            final Long finalDefaultId = defaultId;
            if (intent.hasExtra(IS_THREAD_MESSAGE) && intent.getBooleanExtra(IS_THREAD_MESSAGE, false)) {
                notificationIntent = new Intent(this, FuguInnerChatActivity.class);
                notificationIntent.putExtra("muid", intent.getStringExtra(MESSAGE_UNIQUE_ID));
                notificationIntent.putExtra(CHANNEL_ID, defaultId);
                notificationIntent.putExtra("BUSINESS_NAME", conversation.getLabel());
                notificationIntent.putExtra("chatType", intent.getIntExtra("chat_type", 0));
                notificationIntent.putExtra("scroll", true);
                notificationIntent.putExtra(MESSAGE, messageToBeAdded);
            }
            final Intent finalNotificationIntent = notificationIntent;
            final String finalAppSecretKey = appSecretKey;
            try {
                conversation.setUnreadCount(ChatDatabase.INSTANCE.getPushCount(messageJson.getLong("channel_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ChatDatabase.INSTANCE.removeNotifications(intent.getLongExtra("channelId", -1L));
            ChatDatabase.INSTANCE.setPushCount(intent.getLongExtra("channelId", -1L), 0);
            if (PushReceiver.PushChannel.INSTANCE.getPushChannelId().compareTo(-2l) == 0) {
                finalNotificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                finalNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    if (!finalAppSecretKey.equals(messageJson.getString(APP_SECRET_KEY))) {
                        finalNotificationIntent.putExtra("show_loader", "show_loader");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (finalDefaultId.compareTo(-1L) != 0) {
                    if (CommonData.getConversationList(appSecretKey) != null) {
                        LinkedHashMap<Long, FuguConversation> conversationMap = CommonData.getConversationList(String.valueOf(intent.getLongExtra("channelId", -1L)));
                        List<FuguConversation> fuguConversation = new ArrayList<>(conversationMap.values());
                        for (int i = 0; i < fuguConversation.size(); i++) {
                            if (fuguConversation.get(i).getChannelId().compareTo(intent.getLongExtra("channelId", -1l)) == 0) {
                                fuguConversation.get(i).setUnreadCount(0);
                                conversationMap.put(fuguConversation.get(i).getChannelId(), fuguConversation.get(i));
                                break;
                            }
                        }
                        CommonData.setConversationList(appSecretKey, conversationMap);
                    }
                    final String finalAppSecretKey1 = appSecretKey;
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            LinkedHashMap<Long, FuguConversation> conversationMap = CommonData.getConversationList(finalAppSecretKey1);
                            List<FuguConversation> conversation = new ArrayList<>(conversationMap.values());
                            for (int i = 0; i < conversation.size(); i++) {
                                if (conversation.get(i).getChannelId().compareTo(intent.getLongExtra("channelId", -1l)) == 0) {
                                    conversation.get(i).setUnreadCount(0);
                                    conversationMap.put(conversation.get(i).getChannelId(), conversation.get(i));
                                    break;
                                }
                            }
                            CommonData.setConversationList(finalAppSecretKey1, conversationMap);
                        }
                    }.start();
                    finalNotificationIntent.putExtra("label", conversation.getLabel());
                    startActivity(finalNotificationIntent);
                }
            } else {
                Intent mIntent = new Intent(NOTIFICATION_TAPPED);
                mIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                LocalBroadcastManager.getInstance(FuguPushIntentService.this).sendBroadcast(mIntent);
            }
        } else if (id.compareTo(-1L) != 0) {
//            PackageManager pm = this.getPackageManager();
//            notificationIntent = pm.getLaunchIntentForPackage(this.getPackageName());
            notificationIntent = new Intent(this, ChatActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            final FuguConversation conversation = new FuguConversation();
            JSONObject messageJson = new JSONObject();
            String appSecretKey = "";
            int userType = FuguAppConstant.UserType.CUSTOMER;
            try {
                messageJson = new JSONObject(intent.getBundleExtra("data").getString("message"));
                appSecretKey = messageJson.getString("app_secret_key");
                userType = messageJson.getInt("user_type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < workspacesInfos.size(); i++) {
                if (workspacesInfos.get(i).getFuguSecretKey().equals(appSecretKey)) {
                    SocketConnection.INSTANCE.disconnectSocket();
                    NotificationSockets.INSTANCE.setInitApi(false);
                    SocketConnection.INSTANCE.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().userInfo.accessToken,
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(i).getEnUserId(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserId(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getUserChannel(), "ChangeBusiness", false,
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getPushToken());
                    com.skeleton.mvp.data.db.CommonData.setCurrentSignedInPosition(i);
                    break;
                }
            }
            conversation.setChannelId(intent.getLongExtra("channelId", -1l));
            conversation.setLabel("");
            //conversation.setOpenChat(true);
            conversation.setEnUserId(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId());
            conversation.setUserId(Long.valueOf(CommonData.getWorkspaceResponse(appSecretKey).getUserId()));
            conversation.setChat_type(intent.getIntExtra("chat_type", 0));
            conversation.setUserName(intent.getStringExtra("full_name"));
            conversation.setBusinessName(CommonData.getWorkspaceResponse(appSecretKey).getWorkspaceName());
            conversation.setOtherUserType(userType);
            try {
                conversation.setLabel(messageJson.getString(TITLE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            conversation.setJoined(true);
            conversation.setStartChannelsActivity(true);
            CommonParams commonParams = new CommonParams.Builder()
                    .add("app_secret_key", appSecretKey)
                    .add("en_user_id", CommonData.getWorkspaceResponse(appSecretKey).getEnUserId())
                    .add("channel_id", intent.getLongExtra("channelId", -1l))
                    .add(DEVICE_TYPE, ANDROID_USER)
                    .add(APP_VERSION, BuildConfig.VERSION_NAME)
                    .build();
            final Long finalDefaultId1 = defaultId;
            Intent backstack = new Intent(this, MainActivity.class);
            backstack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent backstack2 = new Intent(this, ChatActivity.class);
            backstack2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final Intent finalNotificationIntent1 = notificationIntent;
            final String finalAppSecretKey = appSecretKey;
            try {
                if (ChatDatabase.INSTANCE.getNotifications(messageJson.getLong("channel_id")) != null) {
                    conversation.setUnreadCount(ChatDatabase.INSTANCE.getPushCount(messageJson.getLong("channel_id")));
                } else {
//                    conversation.setUnreadCount(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ChatDatabase.INSTANCE.removeNotifications(intent.getLongExtra("channelId", -1L));
            ChatDatabase.INSTANCE.setPushCount(intent.getLongExtra("channelId", -1L), 0);
            finalNotificationIntent1.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
            if (!finalAppSecretKey.equals(intent.getStringExtra(APP_SECRET_KEY))) {
                finalNotificationIntent1.putExtra("show_loader", "show_loader");
            }
            if (finalDefaultId1.compareTo(-1L) != 0) {
                if (CommonData.getConversationList(appSecretKey) != null) {
                    LinkedHashMap<Long, FuguConversation> conversationMap = CommonData.getConversationList(appSecretKey);
                    List<FuguConversation> fuguConversation = new ArrayList<>(conversationMap.values());
                    for (int i = 0; i < fuguConversation.size(); i++) {
                        if (fuguConversation.get(i).getChannelId().compareTo(intent.getLongExtra("channelId", -1l)) == 0) {
                            fuguConversation.get(i).setUnreadCount(0);
                            conversationMap.put(fuguConversation.get(i).getChannelId(), fuguConversation.get(i));
                            break;
                        }
                    }
                    CommonData.setConversationList(finalAppSecretKey, conversationMap);
                }
                ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
                final String finalAppSecretKey1 = appSecretKey;
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        LinkedHashMap<Long, FuguConversation> conversationMap = CommonData.getConversationList(finalAppSecretKey1);
                        List<FuguConversation> conversation = new ArrayList<>(conversationMap.values());
                        for (int i = 0; i < conversation.size(); i++) {
                            if (conversation.get(i).getChannelId().compareTo(intent.getLongExtra("channelId", -1l)) == 0) {
                                conversation.get(i).setUnreadCount(0);
                                conversationMap.put(conversation.get(i).getChannelId(), conversation.get(i));
                                break;
                            }
                        }
                        CommonData.setConversationList(finalAppSecretKey1, conversationMap);
                    }
                }.start();
                if ((taskList.size() > 0 && taskList.get(0).topActivity.getClassName().contains("com.skeleton.mvp")) || (taskList.size() > 1 && taskList.get(1).topActivity.getClassName().contains("com.skeleton.mvp"))) {
                    if (intent.hasExtra(IS_THREAD_MESSAGE) && intent.getBooleanExtra(IS_THREAD_MESSAGE, false)) {
                        notificationIntent = new Intent(this, FuguInnerChatActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        notificationIntent.putExtra("muid", intent.getStringExtra(MESSAGE_UNIQUE_ID));
                        notificationIntent.putExtra(CHANNEL_ID, defaultId);
                        notificationIntent.putExtra("scroll", true);
                        notificationIntent.putExtra(MESSAGE, messageToBeAdded);
                    }
                    notificationIntent.putExtra("chatType", intent.getIntExtra("chat_type", 0));
                    notificationIntent.putExtra("label", conversation.getLabel());
                    notificationIntent.putExtra("BUSINESS_NAME", conversation.getLabel());
                    startActivity(new Intent(notificationIntent));
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            LinkedHashMap<Long, FuguConversation> conversationMap = CommonData.getConversationList(finalAppSecretKey1);
                            List<FuguConversation> conversation = new ArrayList<>(conversationMap.values());
                            for (int i = 0; i < conversation.size(); i++) {
                                if (conversation.get(i).getChannelId().compareTo(intent.getLongExtra("channelId", -1l)) == 0) {
                                    conversation.get(i).setUnreadCount(0);
                                    conversationMap.put(conversation.get(i).getChannelId(), conversation.get(i));
                                    break;
                                }
                            }
                            CommonData.setConversationList(finalAppSecretKey1, conversationMap);
                        }
                    }.start();
                    if (intent.hasExtra(IS_THREAD_MESSAGE) && intent.getBooleanExtra(IS_THREAD_MESSAGE, false)) {
                        notificationIntent = new Intent(this, FuguInnerChatActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        notificationIntent.putExtra("muid", intent.getStringExtra(MESSAGE_UNIQUE_ID));
                        notificationIntent.putExtra(CHANNEL_ID, defaultId);
                        notificationIntent.putExtra("scroll", true);
                        notificationIntent.putExtra("label", conversation.getLabel());
                        notificationIntent.putExtra("BUSINESS_NAME", conversation.getLabel());
                        notificationIntent.putExtra("chatType", intent.getIntExtra("chat_type", 0));
                        notificationIntent.putExtra(MESSAGE, messageToBeAdded);
                    }
                    startActivities(new Intent[]{backstack, notificationIntent});
                }
            }
        }
    }

}