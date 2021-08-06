package com.skeleton.mvp.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.ChatDatabase;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.Message;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.utils.FuguLog;

import java.util.LinkedHashMap;
import java.util.List;

import io.paperdb.Paper;

import static android.content.Context.ACTIVITY_SERVICE;

public class FuguNetworkStateReceiver extends BroadcastReceiver implements FuguAppConstant {

    private String TAG = FuguAppConstant.APP_NAME_SHORT + "NetworkStateReceiver";
    // Initial Meta FuguMessage
//    private static MetaMessage meta = new MetaMessage();
//
//    private static FayeClient mClient;
    private Context mContext;
    private String networkConnected = "";
    private boolean isConnected;
    private Long channelid = -1L;
    private String muid = "";
    private boolean isThreadMessage = false;
    @NonNull
    private static LinkedHashMap<Long, LinkedHashMap<String, Message>> unsentMessageMap = new LinkedHashMap<>();


    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Main On Rec");
        mContext = context;
        Paper.init(context);
        int status = NetworkUtil.getConnectivityStatusCode(context);
        try {
            unsentMessageMap = ChatDatabase.INSTANCE.getUnsentMessageMap();
            FuguLog.d("app", "Network connectivity change");
            if (intent.getExtras() != null) {
                boolean isEnabled;
                NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    networkConnected = networkInfo.getState().toString();
                    isEnabled = true;
                    mContext = context;
                    FuguLog.i("app", "Network " + networkInfo.getTypeName() + " connected");
                    if (!CommonData.getFuguServerUrl().isEmpty()) {
//                        if (CommonData.getFuguServerUrl().equals(LIVE_SERVER)) {
//                            mClient = new FayeClient(FAYE_SERVER_LIVE, meta);
//                        } else if (CommonData.getFuguServerUrl().equals(TEST_SERVER) || CommonData.getFuguServerUrl().equals(BETA_LIVE_SERVER)) {
//                            mClient = new FayeClient(FAYE_SERVER_TEST, meta);
//                        }else {
//                            mClient = new FayeClient(FAYE_SERVER_LIVE, meta);
//                        }
//                        mClient = new FayeClient(CommonData.getFayeUrl(), meta);
                        FuguLog.i("server url == ", CommonData.getFuguServerUrl());
                        ActivityManager mngr = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
                        if (!taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.activity.ChatActivity") && !taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.activity.FuguInnerChatActivity")) {
//                            if (!isConnected) {
//                                setUpFayeConnection();
//                            }
                            isConnected = true;
                        }
                    } else {
//                        mClient = new FayeClient(CommonData.getFayeUrl(), meta);
                        FuguLog.i("server url == ", CommonData.getFuguServerUrl());
                        ActivityManager mngr = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
                        if (!taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.activity.ChatActivity") && !taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.activity.FuguInnerChatActivity")) {
//                            if (!isConnected) {
//                                setUpFayeConnection();
//                            }
                            isConnected = true;
                        }
                    }
                } else {
                    isEnabled = false;
                }
                Intent mIntent = new Intent(NETWORK_STATE_INTENT);
                mIntent.putExtra("isConnected", isEnabled);
                mIntent.putExtra("status", status);
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent);
            }
        } catch (Exception e) {

        }
    }

/*
    private void setUpFayeConnection() {
        // Set FayeClient listener
        mClient.setListener(new FayeClientListener() {
                                @Override
                                public void onConnectedServer(FayeClient fc) {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            FuguLog.e(TAG, "==" + "FayeConnected");
                                            ActivityManager mngr = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
                                            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
                                            FuguLog.e(TAG, "==" + taskList.get(0).topActivity.getClassName());
                                            if (!taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.activity.ChatActivity") && !taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.activity.FuguInnerChatActivity")
                                                    && taskList.get(0).topActivity.getClassName().contains("com.skeleton")) {
                                                final ArrayList<Long> keys = new ArrayList<>(unsentMessageMap.keySet());
                                                if (keys.size() > 0) {
                                                    fc.subscribeChannel("/" + String.valueOf(keys.get(0)));
                                                    mClient.connectServer();
                                                    sendFirstUnsentMessageOfList(fc, keys.get(0), unsentMessageMap.get(keys.get(0)));
                                                    if (unsentMessageMap.size() == 0) {
                                                        Intent mIntent = new Intent(BACKGROUND_SENDING_COMPLETE);
                                                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                                                    }
                                                }
                                            }
                                        }
                                    }, 1000);

                                }

                                @Override
                                public void onDisconnectedServer(FayeClient fc) {
                                    FuguLog.e(TAG, "Disconnected");
                                    isConnected = false;
                                }

                                @Override
                                public void onReceivedMessage(FayeClient fc, String msg, String channel) {
                                    FuguLog.e(TAG, FuguAppConstant.APP_NAME_SHORT + "Message: " + msg);
                                    final Long channelId = Long.parseLong(channel.substring(1));
                                    try {
                                        final JSONObject messageJson = new JSONObject(msg);
                                        if (messageJson.has("message") && !messageJson.getString("message").isEmpty()) {
                                            if (unsentMessageMap.get(channelId) != null) {
                                                try {
                                                    ArrayList<Message> fuguMessageList = new ArrayList<>();
                                                    fuguMessageList = ChatDatabase.INSTANCE.getMessageList(channelId);
                                                    Message message = null;
                                                    try {
                                                        message = new Message(fuguMessageList.size(),
                                                                messageJson.getString(FULL_NAME),
                                                                messageJson.getLong(USER_ID),
                                                                messageJson.getString(MESSAGE),
                                                                messageJson.getString(DATE_TIME),
                                                                1,
                                                                MESSAGE_SENT,
                                                                messageJson.getInt(MESSAGE_INDEX),
                                                                "",
                                                                "",
                                                                TEXT_MESSAGE,
                                                                true,
                                                                messageJson.getString(MESSAGE_UNIQUE_ID),
                                                                2,
                                                                "",
                                                                "");
                                                        if (!messageJson.getBoolean("is_thread_message")) {
                                                            fuguMessageList.add(message);
                                                            ChatDatabase.INSTANCE.setMessageList(fuguMessageList, channelId);
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                if (messageJson.getBoolean("is_thread_message")) {
                                                    unsentMessageMap.get(channelId).remove(messageJson.getString("thread_muid"));
                                                } else {
                                                    unsentMessageMap.get(channelId).remove(messageJson.getString(MESSAGE_UNIQUE_ID));
                                                }
                                                ChatDatabase.INSTANCE.setUnsentMessageMapByChannel(channelId, unsentMessageMap.get(channelId));
                                                if (unsentMessageMap.size() > 0) {

                                                    sendFirstUnsentMessageOfList(fc, channelId, unsentMessageMap.get(channelId));
                                                } else {
                                                    try {
                                                        ArrayList<Message> fuguMessageList = new ArrayList<>();
                                                        fuguMessageList = ChatDatabase.INSTANCE.getMessageList(channelId);
                                                        Message message = null;
                                                        try {
                                                            message = new Message(fuguMessageList.size(),
                                                                    messageJson.getString(FULL_NAME),
                                                                    messageJson.getLong(USER_ID),
                                                                    messageJson.getString(MESSAGE),
                                                                    messageJson.getString(DATE_TIME),
                                                                    1,
                                                                    MESSAGE_SENT,
                                                                    fuguMessageList.size() - 1,
                                                                    "",
                                                                    "",
                                                                    TEXT_MESSAGE,
                                                                    true,
                                                                    messageJson.getString(MESSAGE_UNIQUE_ID),
                                                                    2,
                                                                    "",
                                                                    "");
                                                            fuguMessageList.add(message);
                                                            ChatDatabase.INSTANCE.setMessageList(fuguMessageList, channelId);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    Intent mIntent = new Intent(BACKGROUND_SENDING_COMPLETE);
                                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                                                    fc.unsubscribeAll();
                                                }
                                            } else {
                                                if (unsentMessageMap.size() > 0) {
                                                    final ArrayList<Long> keys = new ArrayList<>(unsentMessageMap.keySet());
                                                    fc.subscribeChannel("/" + String.valueOf(keys.get(0)));
                                                    sendFirstUnsentMessageOfList(fc, keys.get(0), unsentMessageMap.get(keys.get(0)));
                                                } else {
                                                    try {
                                                        ArrayList<Message> fuguMessageList = new ArrayList<>();
                                                        fuguMessageList = ChatDatabase.INSTANCE.getMessageList(channelId);
                                                        Message message = null;
                                                        try {
                                                            message = new Message(fuguMessageList.size(),
                                                                    messageJson.getString(FULL_NAME),
                                                                    messageJson.getLong(USER_ID),
                                                                    messageJson.getString(MESSAGE),
                                                                    messageJson.getString(DATE_TIME),
                                                                    1,
                                                                    MESSAGE_SENT,
                                                                    fuguMessageList.size() - 1,
                                                                    "",
                                                                    "",
                                                                    TEXT_MESSAGE,
                                                                    true,
                                                                    messageJson.getString(MESSAGE_UNIQUE_ID),
                                                                    2,
                                                                    "",
                                                                    "");
                                                            fuguMessageList.add(message);
                                                            ChatDatabase.INSTANCE.setMessageList(fuguMessageList, channelId);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    Intent mIntent = new Intent(BACKGROUND_SENDING_COMPLETE);
                                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                                                    fc.unsubscribeAll();
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onErrorReceived(FayeClient fc, String msg, String channel) {
                                    FayeDuplicateEntryResponse fayeDuplicateEntryResponse = new Gson().fromJson(msg, FayeDuplicateEntryResponse.class);
                                    if (fayeDuplicateEntryResponse.getStatusCode() == DUPLICATE_ENTRY) {
                                        try {
                                            unsentMessageMap.get(channelid).remove(muid);
                                            ChatDatabase.INSTANCE.setUnsentMessageMapByChannel(channelid, unsentMessageMap.get(channelid));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
        );

        // Connect to server
        if (!mClient.isConnectedServer()) {
            mClient.connectServer();
        }
    }
*/

/*
    public void sendFirstUnsentMessageOfList(FayeClient fc, final Long key, LinkedHashMap<String, Message> mUnsentMessageMapByChannel) {
        try {
            String localDate = DateUtils.getFormattedDate(new Date());
            LinkedHashMap<String, Message> myUnsentMessagemapByChannel = new LinkedHashMap<>();
            for (String name : mUnsentMessageMapByChannel.keySet()) {
                final JSONObject messageJson = new JSONObject();
                final Message messageObj = mUnsentMessageMapByChannel.get(name);
                int newTime = DateUtils.getTimeInMinutes(DateUtils.getInstance().convertToUTC(localDate));
                int oldTime = DateUtils.getTimeInMinutes(messageObj.getSentAtUtc());
                if (mUnsentMessageMapByChannel.get(name).getMessageType() == TEXT_MESSAGE && !messageObj.isExpired() && (newTime - oldTime) < 10) {
                    if (TextUtils.isEmpty((mUnsentMessageMapByChannel.get(name).getThreadMuid()))) {
                        mUnsentMessageMapByChannel.get(name).setIsThreadMessage(false);
                    } else {
                        mUnsentMessageMapByChannel.get(name).setIsThreadMessage(true);
                    }
                    myUnsentMessagemapByChannel.put(name, mUnsentMessageMapByChannel.get(name));
                } else {
                    if (mUnsentMessageMapByChannel.get(name).getMessageType() == TEXT_MESSAGE) {
                        messageObj.setExpired(true);
                    }
                }
            }
            final ArrayList<String> keys = new ArrayList<>(myUnsentMessagemapByChannel.keySet());
            ArrayList<Message> fuguMessageList = ChatDatabase.INSTANCE.getMessageList(key);
            final JSONObject messageJson = new JSONObject();
            if (keys.size() > 0) {
                final Message messageObj = myUnsentMessagemapByChannel.get(keys.get(0));
                try {
                    messageJson.put(USER_ID, String.valueOf(messageObj.getUserId()));
                    messageJson.put(FULL_NAME, messageObj.getfromName());
                    messageJson.put(MESSAGE, messageObj.getMessage());
                    if (messageObj.getImage_url().isEmpty()) {
                        messageJson.put("message_type", messageObj.getMessageType());

                    } else {
                        messageJson.put("message_type", messageObj.getMessageType());
                        try {
                            Message messageObj2 = fuguMessageList.get(0);
                            messageObj2.setMessageStatus(MESSAGE_UNSENT);
                            messageObj2.setSent(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    String date = DateUtils.getFormattedDate(Calendar.getInstance().getTime());
                    messageJson.put(USER_TYPE, ANDROID_USER);
                    messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
                    messageJson.put(MESSAGE_INDEX, messageObj.getMessageIndex());
                    messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                    messageJson.put(MESSAGE_STATUS, messageObj.getMessageStatus());
                    if (messageObj.getIsThreadMessage()) {
                        messageJson.put(MESSAGE_UNIQUE_ID, messageObj.getThreadMuid());
                        messageJson.put("thread_muid", messageObj.getUuid());
                        messageJson.put("is_thread_message", true);
                    } else {
                        messageJson.put(MESSAGE_UNIQUE_ID, messageObj.getUuid());
                        messageJson.put("is_thread_message", false);
                    }
                    messageJson.put(EMAIL, "");
                    ActivityManager mngr = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);

                    List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

                    if (!taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.activity.ChatActivity") && !taskList.get(0).topActivity.getClassName().equals("com.skeleton.mvp.activity.FuguInnerChatActivity")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (messageObj.getMessageType() == TEXT_MESSAGE) {
                                    muid = messageObj.getUuid();
                                    channelid = key;
                                    mClient.publish(key, "/" + String.valueOf(key), messageJson);
                                }
                            }
                        }, 500);
                    } else {
                        mClient.unsubscribeAll();
                        mClient.setListener(null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 */
}
