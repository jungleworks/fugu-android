package com.skeleton.mvp.fugudatabase;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguColorConfig;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.model.ContactsList;
import com.skeleton.mvp.model.FuguCacheSearchResult;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.model.FuguDeviceDetails;
import com.skeleton.mvp.model.FuguGetMessageResponse;
import com.skeleton.mvp.model.FuguPutUserDetailsResponse;
import com.skeleton.mvp.model.GetAllMembers;
import com.skeleton.mvp.model.GroupMember;
import com.skeleton.mvp.model.Message;
import com.skeleton.mvp.model.PushNotification;
import com.skeleton.mvp.model.UnreadCount;
import com.skeleton.mvp.model.VideoCall;
import com.skeleton.mvp.utils.FuguLog;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import io.paperdb.Paper;

import static com.skeleton.mvp.constant.FuguAppConstant.CONFERENCING_LIVE_NEW;
import static com.skeleton.mvp.constant.FuguAppConstant.CONFERENCING_TEST;
import static com.skeleton.mvp.constant.FuguAppConstant.DOMAIN_URL_LIVE;
import static com.skeleton.mvp.constant.FuguAppConstant.DOMAIN_URL_TEST;
import static com.skeleton.mvp.constant.FuguAppConstant.LIVE_SERVER;
import static com.skeleton.mvp.constant.FuguAppConstant.SOCKET_LIVE_SERVER;
import static com.skeleton.mvp.constant.FuguAppConstant.SOCKET_TEST_SERVER;
import static com.skeleton.mvp.constant.FuguAppConstant.TEST_SERVER;

/**
 * Created by Bhavya Rattan on 15/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */
public final class CommonData implements PaperDbConstant {

    private static TreeMap<String, FuguPutUserDetailsResponse> USER_DETAILS_NEW = new TreeMap<>();
    private static TreeMap<String, WorkspacesInfo> WORKSPACE_DETAILS = new TreeMap<>();
    /**
     * The constant APP_SECRET_KEY.
     */
    public static String APP_SECRET_KEY = "";
    public static String APP_SECRET_KEY_NEW = "";
    public static String APP_SECRET_KEY_OLD = "";
    private static int APP_TYPE = 1;
    private static TreeMap<Long, FuguGetMessageResponse> GET_MESSAGE_RESPONSE_MAP = new TreeMap<>();
    private static TreeMap<Long, FuguGetMessageResponse> GET_LABEL_ID_RESPONSE_MAP = new TreeMap<>();
    private static TreeMap<Long, ArrayList<PushNotification>> STACk_NOTIFICATIONS_MAP = new TreeMap<>();
    private static TreeMap<String, ArrayList<FuguCacheSearchResult>> CACHED_SEARCh_RESULTS = new TreeMap<>();
    private static FuguColorConfig COLOR_CONFIG = new FuguColorConfig();
    private static String isNewChatKey = "IS_NEW_CHAT";
    private static String providerKey = "PROVIDER_KEY";
    private static String pushKey = "PUSH_KEY";
    private static String pushChannelKey = "PUSH_CHANNEL_KEY";
    private static String isAppOpenKey = "isAppOpen";
    private static TreeMap<Long, LinkedHashMap<String, Integer>> FUGU_INDEX_SET = new TreeMap<>();
    private static final String clearFuguDataKey = "clearFuguData";
    private static final String clearFuguDataOfficeLiveKey = "clearFuguDataKeyOfficeLiveKey";
    private static String SEARCH_RESULTS = "cached_search_results";
    private static String MEMBERS_LIST = "members_list_updated";
    private static final TreeMap<String, String> PAPER_NAMES = new TreeMap<>();
    private static final String PAPER_TIME = "";
    private static final String PAPER_IMAGE_URI = "paper_image_uri";
    private static final String PAPER_IMAGE_URI_MAIN = "paper_image_uri_main";
    private static final String PAPER_SHARED_TEXT = "paper_shared_text";
    private static final String PAPER_CUSTOM_TEXT = "paper_custom_text";
    private static TreeMap<String, String> urlMap = new TreeMap<>();
    private static TreeMap<String, String> filesMap = new TreeMap<>();
    private static TreeMap<String, String> localMap = new TreeMap<>();
    private static TreeMap<String, Integer> downloadMap = new TreeMap<>();
    private static TreeMap<String, String> notificationImagesMap = new TreeMap<>();
    private static List<String> supportedFileTypes = new ArrayList<>();
    private static String PUSH_MUID = "";
    private static Long CLICKED_CHANNEL_ID = -1L;
    private static String PAPER_ALL_MEMBERS_CHAT_MAP = "PAPER_ALL_MEMBERS_CHAT_MAP";
    private static String PAPER_UPDATE_NOTIFICATION_COUNT = "PAPER_UPDATE_NOTIFICATION_COUNT";
    private static String PAPER_UPDATE_NOTIFICATION_COUNT_LIST = "PAPER_UPDATE_NOTIFICATION_COUNT_LIST";
    private static String PAPER_ALL_MEMBERS_MAP = "PAPER_ALL_MEMBERS_MAP";
    private static String PAPER_CACHE = "PAPER_CACHE";
    private static String PAPER_VIDEO_CALL = "PAPER_VIDEO_CALL";
    private static String SERVER_CACHE = "SERVER";
    private static String PAPER_OTHER_FILES_URI = "paper_other_files_uri";
    private static String PAPER_OTHER_FILES_URI_STRING = "paper_other_files_uri_string";
    private static String PAPER_VIDEO_URI = "paper_video_uri";
    private static String PAPER_VIDEO_URI_MAIN = "paper_video_uri_main";
    private static String PAPER_MULTIPLE_IMAGE_LIST = "paper_multiple_image_list";
    private static String PAPER_MULTIPLE_IMAGE = "paper_multiple_image";
    private static String PAPER_NOTIFICATION_IMAGES_MAP = "paper_notification_images_map";
    private static String PAPER_CONFERENCE_URL = "paper_conf_url";
    private static String ONBOARDING_FLOW = "paper_onboarding_flow";

    /**
     * The constant UserName.
     */
    public static String UserName = "user_name";
    private static String CHAT_TYPE = "chat_type";
    private static String VIBRATE = "vibrate";
    private static String STACK_NOTIFICATIONS = "stack_notifications";
    /**
     * The constant EN_USER_ID.
     */
//    public static String EN_USER_ID = "en_user_id";
//    public static String USER_ID = "user_id";
    /**
     * The constant EMAIL.
     */
    public static String EMAIL = "email";

    /**
     * Empty Constructor
     * not called
     */
    private CommonData() {
    }


    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param channelId          the channel id
     * @param getMessageResponse the get message response
     */
    public static void setMessageResponse(Long channelId, FuguGetMessageResponse getMessageResponse) {
        GET_MESSAGE_RESPONSE_MAP.put(channelId, getMessageResponse);
        Paper.book().write(PAPER_GET_MESSAGE_RESPONSE_MAP, CommonData.GET_MESSAGE_RESPONSE_MAP);
    }


    /**
     * Sets fugu message list.
     *
     * @param channelId       the channel id
     * @param fuguMessageList the fugu message list
     */
    public static void setFuguIndexSet(Long channelId, LinkedHashMap<String, Integer> fuguMessageList) {
        FUGU_INDEX_SET = Paper.book().read(PAPER_FUGU_INDEX_SET, new TreeMap<Long, LinkedHashMap<String, Integer>>());
        CommonData.FUGU_INDEX_SET.put(channelId, fuguMessageList);
        Paper.book().write(PAPER_FUGU_INDEX_SET, CommonData.FUGU_INDEX_SET);
    }


    /**
     * Save PAPER_GET_MESSAGE_RESPONSE_MAP
     *
     * @param channelId the channel id
     * @param messages  the messages
     */
    public static void setMessagesToMessageMap(Long channelId, ArrayList<Message> messages) {
        if (GET_MESSAGE_RESPONSE_MAP.get(channelId) != null && GET_MESSAGE_RESPONSE_MAP.get(channelId).getData() != null) {
            GET_MESSAGE_RESPONSE_MAP.get(channelId).getData().setMessages(messages);
            Paper.book().write(PAPER_GET_MESSAGE_RESPONSE_MAP, CommonData.GET_MESSAGE_RESPONSE_MAP);
        }
    }

    /**
     * Save PAPER_GET_LABEL_ID_RESPONSE_MAP
     *
     * @param channelId the channel id
     * @param messages  the messages
     */
    public static void setMessagesToLabelMap(Long channelId, ArrayList<Message> messages) {
        if (GET_LABEL_ID_RESPONSE_MAP.get(channelId) != null && GET_LABEL_ID_RESPONSE_MAP.get(channelId).getData() != null) {
            GET_LABEL_ID_RESPONSE_MAP.get(channelId).getData().setMessages(messages);
            Paper.book().write(PAPER_GET_LABEL_ID_RESPONSE_MAP, CommonData.GET_LABEL_ID_RESPONSE_MAP);
        }
    }

    /**
     * Gets PAPER_GET_MESSAGE_RESPONSE_MAP
     * `
     *
     * @param channelId the channel id
     * @return the messageResponse
     */
    public static FuguGetMessageResponse getMessageResponse(Long channelId) {
        if (GET_MESSAGE_RESPONSE_MAP.isEmpty()) {
            GET_MESSAGE_RESPONSE_MAP = Paper.book().read(PAPER_GET_MESSAGE_RESPONSE_MAP, new TreeMap<Long, FuguGetMessageResponse>());
        }
        return GET_MESSAGE_RESPONSE_MAP.get(channelId);
    }

    /**
     * Gets fugu message list.
     *
     * @param channelId the channel id
     * @return the fugu message list
     */
    public static LinkedHashMap<String, Integer> getFuguMessageSet(Long channelId) {
        FUGU_INDEX_SET = Paper.book().read(PAPER_FUGU_INDEX_SET, new TreeMap<Long, LinkedHashMap<String, Integer>>());
        return FUGU_INDEX_SET.get(channelId);
    }


    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param labelId              the label id
     * @param getByLabelIdResponse the get by label id response
     */
    public static void setLabelIdResponse(Long labelId, FuguGetMessageResponse getByLabelIdResponse) {
        GET_LABEL_ID_RESPONSE_MAP.put(labelId, getByLabelIdResponse);
        Paper.book().write(PAPER_GET_LABEL_ID_RESPONSE_MAP, CommonData.GET_LABEL_ID_RESPONSE_MAP);
    }

    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @param labelId the label id
     * @return the conversationList
     */
    public static FuguGetMessageResponse getLabelIdResponse(Long labelId) {
        if (GET_LABEL_ID_RESPONSE_MAP.isEmpty()) {
            GET_LABEL_ID_RESPONSE_MAP = Paper.book().read(PAPER_GET_LABEL_ID_RESPONSE_MAP, new TreeMap<Long, FuguGetMessageResponse>());
        }
        return GET_LABEL_ID_RESPONSE_MAP.get(labelId);
    }


    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @return the serverUrl
     */

    public static void setFuguServerUrl(String url) {
        Paper.book(SERVER_CACHE).write(PAPER_SERVER_URL, url);
    }

    public static void setSocketUrl(String url) {
        Paper.book(SERVER_CACHE).write(PAPER_SERVER_URL_SOCKET, url);
    }

    public static void setDomain(String domain) {
        Paper.book(SERVER_CACHE).write(PAPER_DOMAIN, domain);
    }

    public static String getFuguServerUrl() {
        return Paper.book(SERVER_CACHE).read(PAPER_SERVER_URL, LIVE_SERVER);
    }

    public static String getSocketUrl() {
        return Paper.book(SERVER_CACHE).read(PAPER_SERVER_URL_SOCKET, SOCKET_LIVE_SERVER);
    }

    public static String getDomain() {
        return Paper.book(SERVER_CACHE).read(PAPER_DOMAIN, DOMAIN_URL_LIVE);
    }

    public static String getOnboardingFlow() {
        return Paper.book().read(ONBOARDING_FLOW, "old");
    }

    public static void setOnboardingFlow(String onboardingFlow) {
        Paper.book().write(ONBOARDING_FLOW, onboardingFlow);
    }

    /**
     * Save PAPER_APP_SECRET_KEY
     *
     * @param appSecretKey the app secret key
     */

    /**
     * Save PAPER_APP_SECRET_KEY
     *
     * @param appSecretKey the app secret key
     */
    public static void setAppSecretKeyNew(String appSecretKey) {
        APP_SECRET_KEY_NEW = appSecretKey;
        Paper.book().write(PAPER_APP_SECRET_KEY_NEW, CommonData.APP_SECRET_KEY_NEW);
    }

    /**
     * Gets PAPER_APP_SECRET_KEY
     *
     * @return the appSecretKey
     */
    public static String getAppSecretKeyNew() {
        if (APP_SECRET_KEY_NEW.isEmpty()) {
            APP_SECRET_KEY_NEW = Paper.book().read(PAPER_APP_SECRET_KEY_NEW, "");
        }
        return APP_SECRET_KEY_NEW;
    }

    public static void setPushMuid(String muid) {
        Paper.book().write(PUSH_MUID, muid);
    }

    public static String getPushMuid() {
        return Paper.book().read(PUSH_MUID, "");
    }


    /**
     * Save PAPER_APP_SECRET_KEY
     *
     * @param appType the app type
     */
    public static void setAppType(int appType) {
        APP_TYPE = appType;
        Paper.book().write(PAPER_APP_TYPE, CommonData.APP_TYPE);
    }

    /**
     * Gets PAPER_APP_SECRET_KEY
     *
     * @return the appSecretKey
     */
    public static int getAppType() {
        APP_TYPE = Paper.book().read(PAPER_APP_TYPE, 1);
        return APP_TYPE;
    }

    /*
     * Sets cachedMessages in getMessagesResponse or getLabelIdResponse
     *
     * @param cachedMessages
     * */

    /**
     * Sets cached messages.
     *
     * @param isOpenChat     the is open chat
     * @param mapKey         the map key
     * @param cachedMessages the cached messages
     */
    public static void setCachedMessages(boolean isOpenChat, Long mapKey, ArrayList<Message> cachedMessages) {
        if (isOpenChat && GET_LABEL_ID_RESPONSE_MAP.get(mapKey) != null &&
                GET_LABEL_ID_RESPONSE_MAP.get(mapKey).getData() != null) {
            GET_LABEL_ID_RESPONSE_MAP.get(mapKey).getData().getMessages().clear();
            GET_LABEL_ID_RESPONSE_MAP.get(mapKey).getData().getMessages().addAll(cachedMessages);
        } else if (GET_MESSAGE_RESPONSE_MAP.get(mapKey) != null && GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData() != null) {
            GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData().getMessages().clear();
            GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData().getMessages().addAll(cachedMessages);
        }
    }


    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param conversationList the conversation list
     */
    public static void setConversationList(String mySecretKey, LinkedHashMap<Long, FuguConversation> conversationList) {
        try {
            Paper.book().write(PAPER_CONVERSATION_LIST_LATEST + mySecretKey, conversationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @return the conversationList
     */
    public static LinkedHashMap<Long, FuguConversation> getConversationList(String mySecretKey) {
        try {
            return Paper.book().read(PAPER_CONVERSATION_LIST_LATEST + mySecretKey, new LinkedHashMap<Long, FuguConversation>());
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

//    /**
//     * Gets PAPER_USER_DETAILS
//     *
//     * @return the userDetails
//     */
//    public static FuguPutUserDetailsResponse getUserDetails(String mySecretKey) {
//        USER_DETAILS_NEW = Paper.book().read(PAPER_USER_DETAILS_NEW, new TreeMap<String, FuguPutUserDetailsResponse>());
//        return USER_DETAILS_NEW.get(mySecretKey);
//    }

    /**
     * Save PAPER_COLOR_CONFIG
     *
     * @param fuguColorConfig the fugu color config
     */
    public static void setColorConfig(FuguColorConfig fuguColorConfig) {
        CommonData.COLOR_CONFIG = fuguColorConfig;
        Paper.book().write(PAPER_COLOR_CONFIG, fuguColorConfig);
    }

    /**
     * Gets PAPER_COLOR_CONFIG
     *
     * @return the fuguColorConfig
     */
    public static FuguColorConfig getColorConfig() {
        if (COLOR_CONFIG == null)
            COLOR_CONFIG = Paper.book().read(PAPER_COLOR_CONFIG, null);
        return COLOR_CONFIG;
    }

    /**
     * Save PAPER_USER_DETAILS
     *
     * @param userDetails the user details
     */
    public static void setUserDetails(String mySecretKey, FuguPutUserDetailsResponse userDetails) {
        try {
            USER_DETAILS_NEW = Paper.book().read(PAPER_USER_DETAILS_NEW, new TreeMap<String, FuguPutUserDetailsResponse>());
            CommonData.USER_DETAILS_NEW.put(mySecretKey, userDetails);
            Paper.book().write(PAPER_USER_DETAILS_NEW, CommonData.USER_DETAILS_NEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //======================================== Clear Data ===============================================

    /**
     * Delete paper.
     *
     * @throws Exception the exception
     */
    public static void clearData() throws Exception {
        USER_DETAILS_NEW = new TreeMap<>();
        GET_MESSAGE_RESPONSE_MAP = new TreeMap<>();
        GET_LABEL_ID_RESPONSE_MAP = new TreeMap<>();
        CACHED_SEARCh_RESULTS = new TreeMap<>();
        APP_SECRET_KEY = "";
        COLOR_CONFIG = new FuguColorConfig();

        APP_TYPE = 1;

        Paper.book().destroy();

//        Paper.book().delete(SERVER_URL);
//        Paper.book().delete(isNewChatKey);
//        Paper.book().delete(providerKey);
//        Paper.book().delete(pushKey);
//        Paper.book().delete(pushChannelKey);
//        Paper.book().delete(isAppOpenKey);
//        Paper.book().delete(SEARCH_RESULTS);
//        Paper.book().delete(MEMBERS_LIST);
//        Paper.book().delete(CHAT_TYPE);
//        Paper.book().delete(VIBRATE);
//        Paper.book().delete(STACK_NOTIFICATIONS);
//        Paper.book().delete(EN_USER_ID);
//        Paper.book().delete(USER_ID);
//        Paper.book().delete(PAPER_CONVERSATION_LIST_NEW);
//        Paper.book().delete(PAPER_USER_DETAILS_NEW);
    }

    /**
     * Device details json object.
     *
     * @param context the context
     * @return the json object
     */
    public static JsonObject deviceDetails(Context context) {
        Gson gson = new GsonBuilder().create();
        JsonObject deviceDetailsJson = null;
        try {
            deviceDetailsJson = gson.toJsonTree(new FuguDeviceDetails(
                    getAppVersion(context)).getDeviceDetails()).getAsJsonObject();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return deviceDetailsJson;
    }


    /**
     * Gets app version.
     *
     * @param context the context
     * @return the app version
     */
    private static int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Gets local ip address.
     *
     * @return the local ip address
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            FuguLog.e("IP Address", ex.toString());
        }
        return null;
    }

    /**
     * Sets is newchat.
     *
     * @param isNewchat the is newchat
     */
    public static void setIsNewchat(boolean isNewchat) {
        Paper.book().write(isNewChatKey, isNewchat);
    }

    /**
     * Gets is new chat.
     *
     * @return the is new chat
     */
    public static boolean getIsNewChat() {
        return Paper.book().read(isNewChatKey);
    }

    /**
     * Sets provider.
     *
     * @param provider the provider
     */
    public static void setProvider(String provider) {
        Paper.book().write(providerKey, provider);
    }

    /**
     * Gets provider.
     *
     * @return the provider
     */
    public static String getProvider() {
        return Paper.book().read(providerKey);
    }

    /**
     * Sets push boolean.
     *
     * @param push the push
     */
    public static void setPushBoolean(boolean push) {
        Paper.book().write(pushKey, push);
    }

    /**
     * Gets push boolean.
     *
     * @return the push boolean
     */
    public static boolean getPushBoolean() {
        if (Paper.book().read(pushKey) == null) {
            return false;
        }
        return Paper.book().read(pushKey);
    }

    /**
     * Sets push channel.
     *
     * @param pushChannel the push channel
     */
    public static void setPushChannel(Long pushChannel) {
        Paper.book().write(pushChannelKey, pushChannel);
    }

    /**
     * Gets push channel.
     *
     * @return the push channel
     */
    public static Long getPushChannel() {
        if (Paper.book().read(pushChannelKey) == null) {
            return -1l;
        }
        return Paper.book().read(pushChannelKey);
    }

    /**
     * Clear push channel.
     */
    public static void clearPushChannel() {
        Paper.book().delete(pushChannelKey);
    }

    /**
     * Sets transaction ids map.
     *
     * @param transactionIdsMap the transaction ids map
     */
    public static void setTransactionIdsMap(HashMap<String, Long> transactionIdsMap) {
        Paper.book().write("TransactionIdsMap", transactionIdsMap);
    }

    /**
     * Gets transaction ids map.
     *
     * @return the transaction ids map
     */
    public static HashMap<String, Long> getTransactionIdsMap() {
        if (Paper.book().read("TransactionIdsMap") != null) {
            return Paper.book().read("TransactionIdsMap");
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Gets is app open.
     *
     * @return the is app open
     */
    public static boolean getIsAppOpen() {
        if (Paper.book().read(isAppOpenKey) == null) {
            return true;
        }
        return Paper.book().read(isAppOpenKey);
    }

    /**
     * Sets is app open.
     *
     * @param isAppOpen the is app open
     */
    public static void setIsAppOpen(boolean isAppOpen) {
        Paper.book().write(isAppOpenKey, isAppOpen);
    }

    /**
     * Sets clear fugu data key.
     *
     * @param clearFuguData the clear fugu data
     */
    public static void setClearFuguDataKey(boolean clearFuguData) {
        Paper.book().write(clearFuguDataKey, clearFuguData);
    }

    /**
     * Gets clear fugu data key.
     *
     * @return the clear fugu data key
     */
    public static boolean getClearFuguDataKey() {
        return Paper.book().read(clearFuguDataKey, false);
    }

    /**
     * Sets search results.
     *
     * @param searchMap the search map
     */
    public static void setSearchResults(String appSecretKey, ArrayList<FuguCacheSearchResult> searchMap) {
        CACHED_SEARCh_RESULTS.put(appSecretKey, searchMap);
        Paper.book().write(SEARCH_RESULTS, CommonData.CACHED_SEARCh_RESULTS);
    }

    /**
     * Gets search results.
     *
     * @return the search results
     */
    public static ArrayList<FuguCacheSearchResult> getSearchResults(String appSecretKey) {
        try {
            if (CACHED_SEARCh_RESULTS.isEmpty()) {
                CACHED_SEARCh_RESULTS = Paper.book().read(SEARCH_RESULTS, new TreeMap<>());
            }
            if (CACHED_SEARCh_RESULTS.get(appSecretKey) != null) {
                return CACHED_SEARCh_RESULTS.get(appSecretKey);
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Sets group members.
     *
     * @param groupList the group list
     */
    public static void setGroupMembers(ArrayList<GroupMember> groupList) {
        Paper.book().write(MEMBERS_LIST, groupList);
    }


    /**
     * Gets group list.
     *
     * @return the group list
     */
    public static ArrayList<GroupMember> getGroupList() {
        try {
            return Paper.book().read(MEMBERS_LIST);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Sets chat type.
     *
     * @param chatType the type of chat
     */
    public static void setChatType(int chatType) {
        Paper.book().write(CHAT_TYPE, chatType);
    }

    /**
     * Gets chat type.
     *
     * @return the chat type
     */
    public static int getChatType() {
        return Paper.book().read(CHAT_TYPE);
    }

    /**
     * Sets vibration.
     *
     * @param isVibration the is vibration
     */
    public static void setVibration(Boolean isVibration) {
        Paper.book().write(VIBRATE, isVibration);
    }

    /**
     * Is vibration boolean.
     *
     * @return the boolean
     */
    public static Boolean isVibration() {
        return Paper.book().read(VIBRATE, true);
    }

//    /**
//     * Sets notification.
//     *
//     * @param channelId         the channel id
//     * @param notificationsList the notifications list
//     */
//    public static void setNotification(Long channelId, ArrayList<PushNotification> notificationsList) {
//        try {
//            STACk_NOTIFICATIONS_MAP.put(channelId, notificationsList);
//            Paper.book().write(STACK_NOTIFICATIONS, CommonData.STACk_NOTIFICATIONS_MAP);
//        } catch (Exception e) {
//            Paper.book().delete(STACK_NOTIFICATIONS);
//        }
//    }
//
//    /**
//     * Gets notifications.
//     *
//     * @param channelId the channel id
//     * @return the notifications
//     */
//    public static ArrayList<PushNotification> getNotifications(Long channelId) {
//        try {
//            if (STACk_NOTIFICATIONS_MAP.isEmpty()) {
//                STACk_NOTIFICATIONS_MAP = Paper.book().read(STACK_NOTIFICATIONS, new TreeMap<Long, ArrayList<PushNotification>>());
//            }
//            return STACk_NOTIFICATIONS_MAP.get(channelId);
//        } catch (Exception e) {
//            Paper.book().delete(STACK_NOTIFICATIONS);
//            return new ArrayList<>();
//        }
//    }
//
//    /**
//     * Remove notifications.
//     *
//     * @param channelId the channel id
//     */
//    public static void removeNotifications(Long channelId) {
//        try {
//            if (!STACk_NOTIFICATIONS_MAP.isEmpty()) {
//                TreeMap<Long, ArrayList<PushNotification>> NEW_STACk_NOTIFICATIONS_MAP = STACk_NOTIFICATIONS_MAP;
//                NEW_STACk_NOTIFICATIONS_MAP.remove(channelId);
//                Paper.book().write(STACK_NOTIFICATIONS, NEW_STACk_NOTIFICATIONS_MAP);
//            }
//        } catch (Exception e) {
//            Paper.book().delete(STACK_NOTIFICATIONS);
//        }
//    }

//    /**
//     * Sets en user id.
//     *
//     * @param enUserId the en user id
//     */
//    public static void setEnUserId(String enUserId) {
//        Paper.book().write(EN_USER_ID, enUserId);
//    }
//
//    /**
//     * Gets en user id.
//     *
//     * @return the en user id
//     */
//    public static Long getUserId() {
//        return Paper.book().read(USER_ID);
//    }
//
//    /**
//     * Sets en user id.
//     *
//     * @param enUserId the en user id
//     */
//    public static void setUserId(Long enUserId) {
//        Paper.book().write(USER_ID, enUserId);
//    }
//
//    /**
//     * Gets en user id.
//     *
//     * @return the en user id
//     */
//    public static String getEnUserId() {
//        return Paper.book().read(EN_USER_ID);
//    }

    /**
     * Sets clear fugu data key office.
     *
     * @param clearFuguData the clear fugu data
     */
    public static void setClearFuguDataKeyOffice(boolean clearFuguData) {
        Paper.book().write(clearFuguDataOfficeLiveKey, clearFuguData);
    }

    /**
     * Gets clear fugu data key office.
     *
     * @return the clear fugu data key office
     */
    public static boolean getClearFuguDataKeyOffice() {
        return Paper.book().read(clearFuguDataOfficeLiveKey, false);
    }

    public static void setFullName(String appSecretKey, String Name) {
        PAPER_NAMES.put(appSecretKey, Name);
        Paper.book().write("paper_names", PAPER_NAMES);
    }

    public static String getFullName(String appSecretKey) {
        return PAPER_NAMES.get(appSecretKey);
    }

    /**
     * Gets clear fugu data key office.
     *
     * @return the clear fugu data key office
     */
    public static String getTime() {
        return Paper.book().read(PAPER_TIME, "");
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public static void setTime(String email) {
        Paper.book().write(PAPER_TIME, email);
    }

    public static String getImageUri() {
        return Paper.book().read(PAPER_IMAGE_URI, "");
    }

    public static void setImageUri(String imageUri) {
        Paper.book().write(PAPER_IMAGE_URI, imageUri);
    }


    public static Uri getImageUriMain() {
        return Paper.book().read(PAPER_IMAGE_URI_MAIN);
    }

    public static void setImageUriMain(Uri imageUriMain) {
        Paper.book().write(PAPER_IMAGE_URI_MAIN, imageUriMain);
    }


    public static String getCustomText() {
        return Paper.book().read(PAPER_CUSTOM_TEXT, "");
    }

    public static void setCustomText(String imageUri) {
        Paper.book().write(PAPER_CUSTOM_TEXT, imageUri);
    }

    public static String getSharedText() {
        return Paper.book().read(PAPER_SHARED_TEXT, "");
    }

    public static void setSharedText(String imageUri) {
        Paper.book().write(PAPER_SHARED_TEXT, imageUri);
    }

    public static void deleteImageUri() {
        Paper.book().write(PAPER_IMAGE_URI, "");
    }

    public static void setFilesMap(String muid, String fileUrl, String localUrl) {
        urlMap.put(muid, fileUrl);
        filesMap.put(fileUrl, localUrl);
        Paper.book().write(PAPER_URL_MAP, CommonData.urlMap);
        Paper.book().write(PAPER_FILES_MAP, CommonData.filesMap);
    }

    public static void setFileLocalPath(String muid, String localUrl) {
        if (!TextUtils.isEmpty(localUrl)) {
            localMap.put(muid, localUrl);
        }
        Paper.book().write(PAPER_LOCAL_MAP, CommonData.localMap);
    }

    public static void setNotificationImagesMap(String link, String path) {
        try {
            notificationImagesMap = Paper.book().read(PAPER_NOTIFICATION_IMAGES_MAP);
            if (notificationImagesMap == null) {
                notificationImagesMap = new TreeMap<>();
            }
            notificationImagesMap.put(link, path);
            Paper.book().write(PAPER_NOTIFICATION_IMAGES_MAP, notificationImagesMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNotificationImage(String link) {
        try {
            notificationImagesMap = Paper.book().read(PAPER_NOTIFICATION_IMAGES_MAP);
            return notificationImagesMap.get(link);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFileLocalPath(String muid) {
        try {
            localMap = Paper.book().read(PAPER_LOCAL_MAP, new TreeMap<String, String>());
            if (localMap.get(muid) != null) {
                return localMap.get(muid);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFileLink(String muid) {
        try {
            urlMap = Paper.book().read(PAPER_URL_MAP, new TreeMap<String, String>());
            if (urlMap.get(muid) != null) {
                return urlMap.get(muid);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }


    public static void setCachedFilePath(String url, String muid, String localPath) {
        try {
            url = url.replaceAll("/", "");
            Paper.book().write(PAPER_CACHE + url + muid, localPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCachedFilePath(String url, String muid) {
        try {
            url = url.replaceAll("/", "");
            return Paper.book().read(PAPER_CACHE + url + muid, "");
        } catch (Exception e) {
            return "";
        }
    }

    public static void setDownloadId(String muid, int downloadId) {
        try {
            Paper.book().write("downloadId" + muid, downloadId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getDownloadId(String muid) {
        try {
            return Paper.book().read("downloadId" + muid);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getLocalFilePath(String fileUrl) {
        try {
            filesMap = Paper.book().read(PAPER_FILES_MAP, new TreeMap<String, String>());
            if (filesMap.get(fileUrl) != null) {
                return filesMap.get(fileUrl);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static void updateDownloadMap(String muid, Integer position) {
        downloadMap.put(muid, position);
        Paper.book().write(PAPER_DOWNLOAD_MAP, CommonData.downloadMap);
    }

    public static TreeMap<String, Integer> getDownloadMap() {
        return Paper.book().read(PAPER_DOWNLOAD_MAP);
    }

    public static void removeValueFromDownloadMap(String muid) {
        CommonData.downloadMap.remove(muid);
        Paper.book().write(PAPER_DOWNLOAD_MAP, downloadMap);
    }

    public static void setSupportedFileTypes(List<String> list) {
        Paper.book().write(PAPER_SUPPORTED_LIST, list);
    }

    public static List<String> getSupportedFileTypes() {
        return Paper.book().read(PAPER_SUPPORTED_LIST);
    }

    public static void setWorkspaceResponse(String appSecretKey, WorkspacesInfo workspacesInfo) {
        WORKSPACE_DETAILS = Paper.book().read(PAPER_WORKSPACE_INFO, new TreeMap<String, WorkspacesInfo>());
        CommonData.WORKSPACE_DETAILS.put(appSecretKey, workspacesInfo);
        Paper.book().write(PAPER_WORKSPACE_INFO, CommonData.WORKSPACE_DETAILS);
    }

    public static WorkspacesInfo getWorkspaceResponse(String appSecretKey) {
        WORKSPACE_DETAILS = Paper.book().read(PAPER_WORKSPACE_INFO, new TreeMap<String, WorkspacesInfo>());
        return WORKSPACE_DETAILS.get(appSecretKey);
    }


    public static void setClickedChannelId(Long channelId) {
        Paper.book().write(PAPER_CLICKED_CHANNEL, channelId);
    }

    public static Long getClickedChannelId() {
        return Paper.book().read(PAPER_CLICKED_CHANNEL, -1L);
    }

    public static void setAppVersionCount(HashMap<Integer, Integer> versionCount) {
        Paper.book().write(PAPER_APP_VERSION_COUNT, versionCount);
    }

    public static HashMap<Integer, Integer> getAppVersionCount() {
        return Paper.book().read(PAPER_APP_VERSION_COUNT);
    }

    public static void setWorkspaceContacts(ArrayList<ContactsList> contactsLists) {
        try {
            Paper.book().write(PAPER_GET_WORKSPACE_CONTACTS, contactsLists);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ContactsList> getWorkspaceContacts() {
        return Paper.book().read(PAPER_GET_WORKSPACE_CONTACTS);
    }

    public static void setUserContacts(ArrayList<ContactsList> contactsLists) {
        try {
            Paper.book().write(PAPER_GET_USER_CONTACTS, contactsLists);
        } catch (Exception e) {

        }
    }

    public static ArrayList<ContactsList> getUserContacts() {
        try {
            if (Paper.book().read(PAPER_GET_USER_CONTACTS) == null) {
                return new ArrayList<>();
            } else {
                return Paper.book().read(PAPER_GET_USER_CONTACTS);

            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void setPaperAllMembersChatMap(HashMap<Long, GetAllMembers> membersChatMap, String appSecretkey) {
        Paper.book().write(PAPER_ALL_MEMBERS_CHAT_MAP + appSecretkey, membersChatMap);
    }

    public static HashMap<Long, GetAllMembers> getPaperAllMembersChatMap(String appSecretkey) {
        return Paper.book().read(PAPER_ALL_MEMBERS_CHAT_MAP + appSecretkey);
    }

    public static void setPaperAllMembersMap(HashMap<Long, GetAllMembers> membersChatMap, String appSecretkey) {
        Paper.book().write(PAPER_ALL_MEMBERS_MAP + appSecretkey, membersChatMap);
    }

    public static HashMap<Long, GetAllMembers> getPaperAllMembersMap(String appSecretkey) {
        try {
            if (Paper.book().read(PAPER_ALL_MEMBERS_MAP + appSecretkey) == null) {
                return new HashMap<>();
            } else {
                return Paper.book().read(PAPER_ALL_MEMBERS_MAP + appSecretkey);
            }
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static void setNotificationCount(int count) {
        Paper.book().write(PAPER_UPDATE_NOTIFICATION_COUNT, count);
    }

    public static int getNotificationCount() {
        return Paper.book().read(PAPER_UPDATE_NOTIFICATION_COUNT, 0);
    }

    public static void setNotificationsCountList(ArrayList<UnreadCount> unreadCountArrayList) {
        Paper.book().write(PAPER_UPDATE_NOTIFICATION_COUNT_LIST, unreadCountArrayList);
    }

    public static ArrayList<UnreadCount> getNotificationCountList() {
        return Paper.book().read(PAPER_UPDATE_NOTIFICATION_COUNT_LIST, new ArrayList<UnreadCount>());
    }

    public static void setVideoCall(VideoCall videoCall) {
        Paper.book().write(PAPER_VIDEO_CALL, videoCall);
    }

    public static VideoCall getVideoCall() {
        try {
            return Paper.book().read(PAPER_VIDEO_CALL);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setOtherFilesUri(Uri otherFilesUri) {
        Paper.book().write(PAPER_OTHER_FILES_URI, otherFilesUri);
    }

    public static Uri getOtherFilesUri() {
        return Paper.book().read(PAPER_OTHER_FILES_URI);
    }

    public static void setOtherFilesUriString(String otherFilesuri) {
        Paper.book().write(PAPER_OTHER_FILES_URI_STRING, otherFilesuri);
    }

    public static String getOtherFilesUriString() {
        return Paper.book().read(PAPER_OTHER_FILES_URI_STRING, "");
    }


    public static String getVideoUri() {
        return Paper.book().read(PAPER_VIDEO_URI, "");
    }

    public static void setVideoUri(String imageUri) {
        Paper.book().write(PAPER_VIDEO_URI, imageUri);
    }


    public static Uri getVideoUriMain() {
        try {
            return Paper.book().read(PAPER_VIDEO_URI_MAIN);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setVideoUriMain(Uri imageUriMain) {
        Paper.book().write(PAPER_VIDEO_URI_MAIN, imageUriMain);
    }

    public static void setMultipleImageList(ArrayList<String> imageList) {
        Paper.book().write(PAPER_MULTIPLE_IMAGE_LIST, imageList);
    }

    public static void setConferenceUrl(String url) {
        Paper.book().write(PAPER_CONFERENCE_URL, url);
    }

    public static String getConferenceUrl() {
        try {
            return Paper.book().read(PAPER_CONFERENCE_URL, BuildConfig.FLAVOR.equals("dev") ? CONFERENCING_TEST : CONFERENCING_LIVE_NEW);
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<String> getMultipleImageList() {
        try {
            return Paper.book().read(PAPER_MULTIPLE_IMAGE_LIST);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setMultipleImage(String paperMultipleImage) {
        Paper.book().write(PAPER_MULTIPLE_IMAGE, paperMultipleImage);
    }

    public static String getMultipleImage() {
        return Paper.book().read(PAPER_MULTIPLE_IMAGE);
    }


}
