package com.skeleton.mvp.data.db;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.skeleton.mvp.data.model.AllGroup;
import com.skeleton.mvp.data.model.CacheSearchResult;
import com.skeleton.mvp.data.model.Contacts;
import com.skeleton.mvp.data.model.OfficeChatDeviceDetails;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcsignup.FcSignupResponse;
import com.skeleton.mvp.data.model.getdomains.GetDomainsResponse;
import com.skeleton.mvp.model.homeNotifications.Notification;
import com.skeleton.mvp.videoCall.Connection;
import com.skeleton.mvp.videoCall.Signal;
import com.skeleton.mvp.videoCall.VideoCallModel;
import com.skeleton.mvp.videoCall.WebRTCCallClient;
import com.skeleton.mvp.videoCall.WebRTCSignallingClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import io.paperdb.Paper;


/**
 * Developer: Click Labs
 */
public class CommonData {

    private static final String PAPER_DEVICE_TOKEN = "paper_device_token";
    private static final String PAPER_GET_DOMAINS_RESPONSE = "paper_get_domains";
    private static final String PAPER_EMAIL = "paper_email";
    private static final String PAPER_SIGNIN = "paper_signin_response";
    private static final String PAPER_COMMON = "paper_common";
    private static final String PAPER_CONTACTS = "paper_contacts";
    private static final String PAPER_FIRST_LOGIN = "paper_first_login";
    private static final String SEARCH_RESULTS = "search_results";
    private static final String ALL_GROUP_RESULTS = "group_results";
    private static final String JOIN_GROUP_RESULTS = "join_group_results";
    private static final String CURRENT_SIGNED_IN_POSITION = "current_signed_in_position";
    private static final String PAPER_TOKEN = "access_token";
    private static final String SIGNUP_RESPONSE = "signup_response";
    private static final String CURRENT_VERSION = "current_version";
    private static final String RECENT_EMOJIS = "recent_emojis";
    private static ArrayList<String> emojiMap = new ArrayList<>();
    private static final String CONTACT_EMAILS = "contact_emails";
    private static final String CONTACT_PHONE = "contact_phone";
    private static final String WORKPSCES_MAP = "workspaces_map";
    private static final String HOME_NOTIFICATIONS_MAP = "home_notifications_map";
    private static final String SIGNAL_MODEL = "signal_model";
    private static final String CONNECTION_MODEL = "connection_model";
    private static final String VIDEO_CALL_MODEL = "video_call_model_new";
    private static final String CALL_STATUS = "cal_status";
    private static final String MEDIA_PLAYER = "media_player";
    private static final String WEB_RTC_CALL_CLIENT = "web_rtc_call_client";
    private static final String WEB_RTC_SIGNALLING_CLIENT = "web_rtc_signalling_client";
    private static final String LOCAL_NOTIFICATION_ID = "local_notification_id";

    /**
     * Prevent instantiation
     */
    private CommonData() {
    }

    /**
     * Update fcm token.
     *
     * @param token the fcm token
     */
    public static void updateFcmToken(String token) {
        if (token == null) {
            token = "";
        }
        Paper.book().write(PAPER_DEVICE_TOKEN, token);
    }

    /**
     * Gets fcm token.
     *
     * @return the fcm token
     */
    public static String getFcmToken() {
        try {
            return Paper.book().read(PAPER_DEVICE_TOKEN);
        } catch (Exception e) {
            return "";
        }
    }

    public static void setLocalNotificationId(long id) {
        Paper.book().write(LOCAL_NOTIFICATION_ID, id);
    }

    public static long getLocalNotificationId() {
        return Paper.book().read(LOCAL_NOTIFICATION_ID, 0L);
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
            deviceDetailsJson = gson.toJsonTree(new OfficeChatDeviceDetails(
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
    public static int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void setGetDomainsResponse(GetDomainsResponse getDomainsResponse) {
        Paper.book().write(PAPER_GET_DOMAINS_RESPONSE, getDomainsResponse);
    }

    public static GetDomainsResponse getDomainsResponse() {
        return Paper.book().read(PAPER_GET_DOMAINS_RESPONSE);
    }

    public static void setEmail(String email) {
        Paper.book().write(PAPER_EMAIL, email);
    }

    public static String getEmail() {
        return Paper.book().read(PAPER_EMAIL);
    }

//    public static void setSignInResponse(FinalSignInResponse finalSigninResponse) {
//        Paper.book().write(PAPER_SIGNIN, finalSigninResponse);
//    }
//
//    public static FinalSignInResponse getFinalSignInResponse() {
//        return Paper.book().read(PAPER_SIGNIN);
//    }

    public static void setContactsList(ArrayList<Contacts> contactsList) {
        Paper.book().write(PAPER_CONTACTS, contactsList);
    }

    public static void setFirstLogin(boolean isFirstLogin) {
        Paper.book().write(PAPER_FIRST_LOGIN, isFirstLogin);
    }

    public static boolean isFirstLogin() {
        return Paper.book().read(PAPER_FIRST_LOGIN, true);
    }

    public static ArrayList<Contacts> getContactsList() {
        return Paper.book().read(PAPER_CONTACTS);
    }

    public static void clearData() {
        Paper.book().delete(PAPER_GET_DOMAINS_RESPONSE);
        Paper.book().delete(PAPER_EMAIL);
        Paper.book().delete(PAPER_SIGNIN);
        Paper.book().delete(PAPER_CONTACTS);
        Paper.book().delete(SEARCH_RESULTS);
    }

    public static void setSearchResults(ArrayList<CacheSearchResult> searchMap) {
        Paper.book().write(SEARCH_RESULTS, searchMap);
    }

    public static ArrayList<CacheSearchResult> getSearchResults() {
        return Paper.book().read(SEARCH_RESULTS);
    }

    public static void setAllGroupResult(ArrayList<AllGroup> allGroupList) {
        Paper.book().write(ALL_GROUP_RESULTS, allGroupList);
    }

    public static ArrayList<AllGroup> getAllGroupResults() {
        return Paper.book().read(ALL_GROUP_RESULTS);
    }

    public static void setJoinGroupResult(ArrayList<AllGroup> allGroupList) {
        Paper.book().write(JOIN_GROUP_RESULTS, allGroupList);
    }

    public static ArrayList<AllGroup> getJoinGroupResults() {
        return Paper.book().read(JOIN_GROUP_RESULTS);
    }

    public static void setCurrentSignedInPosition(int position) {
        Paper.book().write(CURRENT_SIGNED_IN_POSITION, position);
    }

    public static int getCurrentSignedInPosition() {
        if (Paper.book().read(CURRENT_SIGNED_IN_POSITION) == null) {
            return 0;
        }
        return Paper.book().read(CURRENT_SIGNED_IN_POSITION);
    }

    public static void setCommonResponse(FcCommonResponse fcCommonResponse) {
        Paper.book().write(PAPER_SIGNIN, fcCommonResponse);
    }

    public static FcCommonResponse getCommonResponse() {
        return Paper.book().read(PAPER_SIGNIN);
    }

    public static void setToken(String token) {
        Paper.book().write(PAPER_TOKEN, token);
    }

    public static String getToken() {
        return Paper.book().read(PAPER_TOKEN);
    }

    public static void setSignUpResponse(FcSignupResponse token) {
        Paper.book().write(SIGNUP_RESPONSE, token);
    }

    public static FcSignupResponse getSignupResponse() {
        return Paper.book().read(SIGNUP_RESPONSE);
    }

    public static void setCurrentVersion(int version) {
        try {
            Paper.book().write(CURRENT_VERSION, version);
        } catch (Exception e) {

        }
    }

    public static int getCurrentVersion() {
        return Paper.book().read(CURRENT_VERSION);
    }

    public static ArrayList<String> getEmojiMap() {
        try {
            emojiMap = Paper.book().read(RECENT_EMOJIS, new ArrayList<String>());
            if (emojiMap.size() == 0) {
                emojiMap.add("1f620");//angry
                emojiMap.add("1f4a9");//poop
                emojiMap.add("1f389");//party
                emojiMap.add("1f642");//smiling face
                emojiMap.add("1f60e");//sunglass
                emojiMap.add("2764");//heart
                emojiMap.add("1f604");//smiling face and smiling eyes
                emojiMap.add("1f602");//ansu vali emoji
                emojiMap.add("1f44d");//like
            }
            return emojiMap;
        } catch (Exception e) {
            emojiMap.add("1f620");//angry
            emojiMap.add("1f4a9");//poop
            emojiMap.add("1f389");//party
            emojiMap.add("1f642");//smiling face
            emojiMap.add("1f60e");//sunglass
            emojiMap.add("2764");//heart
            emojiMap.add("1f604");//smiling face and smiling eyes
            emojiMap.add("1f602");//ansu vali emoji
            emojiMap.add("1f44d");//like
            return emojiMap;
        }
    }

    public static void setEmojiMap(String emoji) {
        emojiMap = Paper.book().read(RECENT_EMOJIS, new ArrayList<String>());
        if (emojiMap.size() == 0) {
            emojiMap.add("1f620");//angry
            emojiMap.add("1f4a9");//poop
            emojiMap.add("1f389");//party
            emojiMap.add("1f642");//smiling face
            emojiMap.add("1f60e");//sunglass
            emojiMap.add("2764");//heart
            emojiMap.add("1f604");//smiling face and smiling eyes
            emojiMap.add("1f602");//ansu vali emoji
            emojiMap.add("1f44d");//like
        }
        boolean isPresent = false;
        for (int i = 0; i < emojiMap.size(); i++) {
            if (emojiMap.get(i).equals(emoji)) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            emojiMap.add(emoji);
        }
        if (emojiMap.size() == 36) {
            emojiMap.remove(0);
        }
        Paper.book().write(RECENT_EMOJIS, emojiMap);
    }

    public static void setContactEmails(ArrayList<String> emailsList) {
        Paper.book().write(CONTACT_EMAILS, emailsList);
    }

    public static ArrayList<String> getContactEmails() {
        return Paper.book().read(CONTACT_EMAILS);
    }

    public static void setContactPhone(ArrayList<String> emailsList) {
        Paper.book().write(CONTACT_PHONE, emailsList);
    }

    public static ArrayList<String> getContactPhone() {
        return Paper.book().read(CONTACT_PHONE);
    }

    public static void setWorkspacesMap(LinkedHashMap<String, String> workspaceMap) {
        Paper.book().write(WORKPSCES_MAP, workspaceMap);
    }

    public static LinkedHashMap<String, String> getWorkpscesMap() {
        return Paper.book().read(WORKPSCES_MAP, new LinkedHashMap<String, String>());
    }

    public static void setHomeNotifications(ArrayList<Notification> homeNotifications) {
        Paper.book().write(HOME_NOTIFICATIONS_MAP, homeNotifications);
    }

    public static ArrayList<Notification> getHomeNotifications() {
        try {
            return Paper.book().read(HOME_NOTIFICATIONS_MAP, new ArrayList<Notification>());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void setSignalModel(Signal signal) {
        Paper.book().write(SIGNAL_MODEL, signal);
    }

    public static void setConnectionModel(Connection connection) {
        Paper.book().write(CONNECTION_MODEL, connection);
    }

    public static void setVideoCallModel(VideoCallModel videoCallModel) {
        try {
            if (videoCallModel == null) {
//                Paper.book().write(VIDEO_CALL_MODEL, "");
            } else {
                Paper.book().write(VIDEO_CALL_MODEL, videoCallModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Signal getSignalModel() {
        return Paper.book().read(SIGNAL_MODEL);
    }

    public static Connection getConnectionModel() {
        return Paper.book().read(CONNECTION_MODEL);
    }

    public static VideoCallModel getVideoCallModel() {
        return Paper.book().read(VIDEO_CALL_MODEL);
    }

    public static String getCallStatus() {
        if (!TextUtils.isEmpty(Paper.book().read(CALL_STATUS))) {
            return Paper.book().read(CALL_STATUS);
        }else {
            return "NONE";
        }
    }

    public static void setCallStatus(String callStatus) {
        Paper.book().write(CALL_STATUS, callStatus);
    }
}
