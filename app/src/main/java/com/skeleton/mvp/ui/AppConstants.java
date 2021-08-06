package com.skeleton.mvp.ui;

import com.skeleton.mvp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rajatdhamija
 * 29/12/17.
 */

public class AppConstants {
    public static final int REQ_CODE_NEW_SIGNUP = 12123;
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_EMAIL_ID = "emailId";
    public static final String EXTRA_OTP = "otp";
    public static final String EXTRA_ACCESS_TOKEN = "access_token";
    public static final String EXTRA_ALREADY_MEMBER = "already_member";
    public static final String SELECTED_MEMBERS = "SELECTED_MEMBERS";
    public static final String EXTRA_CREATE_GROUP_FROM_PROFILE = "EXTRA_CREATE_GROUP_FROM_PROFILE";
    public static final String EXTRA_CREATE_GROUP_FROM_SEARCH = "EXTRA_CREATE_GROUP_FROM_SEARCH";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_DOMAIN = "domain";
    public static final String EMAIL = "email";
    public static final String FULL_NAME = "full_name";
    public static final String EMAILS = "emails";
    public static final String DOMAIN = "domain";
    public static final String WORKSPACE = "workspace";
    public static final String WORKSPACE_NAME = "workspace_name";
    public static final String WORKSPACE_URL = "workspace_url";
    public static final String BUSINESS_NAME = "business_name";
    public static final String APP_VERSION = "app_version";
    public static final String DEVICE_TYPE = "device_type";
    public static final String SIGNUP_TYPE = "signup_type";
    public static final String TOKEN = "token";
    public static final String DEVICE_ID = "device_id";
    public static final String LAST_NOTIFICATION_ID = "last_notification_id";
    public static final String DEVICE_DETAILS = "device_details";
    public static final String ANDROID = "ANDROID";
    public static final String OTP = "otp";
    public static final String PASSWORD = "password";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String APP_SECRET_KEY = "app_secret_key";
    public static final String USER_ID = "user_id";
    public static final String FUGU_USER_ID = "fugu_user_id";
    public static final String CHAT_WITH_USER_ID = "chat_with_user_id";
    public static final String USER_IDS_TO_ADD = "user_ids_to_add";
    public static final String CUSTOM_LABEL = "custom_label";
    public static final String LABEL = "label";
    public static final String INTRO_MESSAGE = "intro_message";
    public static final String SEARCH_TEXT = "search_text";
    public static final int REQUEST_JOIN_GROUP = 1322;
    public static final String WORKSPACE_ID = "workspace_id";
    public static final String OPEN_HOME = "open_home";
    public static final String CONVERSATION = "conversation";
    public static final String BUNDLE = "bundle";
    public static final String CONTACT_NUMBER = "contact_number";
    public static final String SIGNUP_SOURCE = "signup_source";
    public static final String CONTACT_NUMBER_INTENT = "contactNumber";
    public static final String BRANCH_IO = "branch_io";
    public static final String SHARED_IMAGE_URI = "imageUri";
    public static final String SHARED_TEXT = "sharedText";
    public static final String SOURCE = "source";
    public static final String IMAGE_SLASH = "image/";
    public static final String VIDEO_SLASH = "video/";
    public static final String TEXT_SLASH = "text/";
    public static final String HARD_UPDATE = "HARD_UPDATE";
    public static final String SOFT_UPDATE = "SOFT_UPDATE";
    public static final String COUNTRY_CODE = "country_code";
    public static final String NEW_SIGNUP = "NEW_SIGNUP";
    public static ArrayList<String> colorsArray = new ArrayList<>();
    public static HashMap<Integer, Integer> dummyImagesArray = new HashMap<>();
    public static HashMap<Integer, Integer> dummyImagesArray2 = new HashMap<>();

    static {
        colorsArray.add("#2496ff");
        colorsArray.add("#009688");
        colorsArray.add("#F44336");
        colorsArray.add("#9C27B0");
        colorsArray.add("#673AB7");
        colorsArray.add("#3F51B5");

        dummyImagesArray.put(1, R.drawable.ring_grey);
        dummyImagesArray.put(2, R.drawable.ring_indigo);
        dummyImagesArray.put(3, R.drawable.ring_purple);
        dummyImagesArray.put(4, R.drawable.ring_red);
        dummyImagesArray.put(0, R.drawable.ring_teal);

        dummyImagesArray2.put(1, R.drawable.rectangle_grey);
        dummyImagesArray2.put(2, R.drawable.rectangle_indigo);
        dummyImagesArray2.put(3, R.drawable.rectangle_purple);
        dummyImagesArray2.put(4, R.drawable.rectangle_red);
        dummyImagesArray2.put(0, R.drawable.rectangle_teal);
    }
}
