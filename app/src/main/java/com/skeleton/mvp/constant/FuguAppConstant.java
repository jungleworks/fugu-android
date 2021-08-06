package com.skeleton.mvp.constant;

import android.os.Environment;

import com.skeleton.mvp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Bhavya Rattan on 10/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public interface FuguAppConstant {

    String BETA_LIVE_SERVER = "https://api.fuguchat.com";

    String LIVE_SERVER = "https://openapi.fuguchat.com/api/"; // Change 'openapi.fuguchat.com' to the api pointing of your fugu-server
    String SOCKET_LIVE_SERVER = "https://openapi.fuguchat.com"; // Change 'openapi.fuguchat.com' to the api pointing of your fugu-server

    String TEST_SERVER = "https://openapi.fuguchat.com/api/"; // Change 'openapi.fuguchat.com' to the api pointing of your fugu-server
    String SOCKET_TEST_SERVER = "https://openapi.fuguchat.com"; // Change 'openapi.fuguchat.com' to the api pointing of your fugu-server

    String BETA_SERVER = "https://openapi.fuguchat.com/api/"; // Change 'openapi.fuguchat.com' to the api pointing of your fugu-server
    String SOCKET_BETA_SERVER = "https://openapi.fuguchat.com"; // Change 'openapi.fuguchat.com' to the api pointing of your fugu-server

    String CONFERENCING_TEST = "https://meet.jit.si"; // Deploy your own Jitsi instance and change this url to yours
    String CONFERENCING_LIVE_NEW = "https://meet.jit.si"; // Deploy your own Jitsi instance and change this url to yours

    String DOMAIN_URL_LIVE = "fuguchat.com"; // Change your domain name here (Can be found in domain_credentials.domain table)
    String DOMAIN_URL_TEST = "fuguchat.com"; // Change your domain name here (Can be found in domain_credentials.domain table)

    String APP_NAME_SHORT = "Fugu"; // Replace with your AppName

    String DOCUMENT = APP_NAME_SHORT + " Documents";
    String IMAGE = APP_NAME_SHORT + " Images";
    String PRIVATE_IMAGES = APP_NAME_SHORT + " PrivateImages";
    String GIF = APP_NAME_SHORT + " Gifs";
    String AUDIO = APP_NAME_SHORT + " Audios";
    String VIDEO = APP_NAME_SHORT + " Videos";
    String OTHERS = APP_NAME_SHORT + " Others";
    String USER_IMAGES = APP_NAME_SHORT + " UserImages";
    String ATTENDANCE = APP_NAME_SHORT + " Attendance";

    // In case font are changed, don't forget to the change values in strings.xml
    String FONT_REGULAR = "fonts/TitilliumWeb-Regular.ttf";
    String FONT_ITALIC = "fonts/italic.otf";
    String FONT_SEMIBOLD = "fonts/TitilliumWeb-SemiBold.ttf";
    String FONT_BOLD = "fonts/TitilliumWeb-SemiBold.ttf";

    int SESSION_EXPIRE = 401;
    int INVALID_VIDEO_CALL_CREDENTIALS = 413;
    String NETWORK_STATE_INTENT = "network_state_changed";
    String VIDEO_CALL_HUNGUP = "VIDEO_CALL_HUNGUP";
    String BACKGROUND_SENDING_COMPLETE = "background_sending_complete";
    String NOTIFICATION_INTENT = "notification_received";
    String CLEAR_INTENT = "clear_chat";
    String DELETE_INTENT = "delete_chat";
    String GROUP_INFO_INTENT = "group_info_intent";
    String USER_REMOVED_INTENT = "user_removed_intent";
    String USER_ADDED_INTENT = "user_added_intent";
    String CHANNEL_INTENT = "channel_intent";
    String REJECT_CALL_INTENT = "reject_call_intent";
    String NOTIFICATION_COUNTER_INTENT = "user_added_intent";
    String VIDEO_CALL_INTENT = "video_call_intent";
    String EDIT_MESSAGE_INTENT = "edit_message_intent";
    String PUBLIC_INTENT = "public_chat";
    String DEACTIVATE_USER_INTENT = "deactivate_user_intent";
    String THREAD_DELETE_INTENT = "thread_delete_chat";
    String NOTIFICATION_TAPPED = "notification_tapped";
    String MESSAGE_NOTIFICATION_INTENT = "message_notification_intent";
    String VIDEO_CONFERENCE_HUNGUP_INTENT = "video_conference_hungup_intent";
    String BOT_USER_ID = "bot_user_id";
    String TAGGED_ALL = "tagged_all";
    String DEVICE_TOKEN = "device_token";
    String DEVICE_PAYLOAD = "device_payload";
    String LEAVE = "leave";
    String WORK_FROM_HOME = "work from home";
    String LEAVE_START_DATE = "leave_start_date";
    String LEAVE_END_DATE = "leave_end_date";
    String BUSINESS_ID = "business_id";
    String GROUP_SIZE_CHANGED = "group_size_changed";
    String IS_THREAD_MESSAGE = "is_thread_message";
    String IS_THREAD_REACTION = "is_thread_reaction";
    String EXTRA_atCREATE_GROUP = "create_group";
    String EXTRA_CREATE_GROUP_MAP = "create_group_map";
    String IS_DELETED_GROUP = "is_deleted_group";
    String DATA = "data";
    String THREAD_MUID = "thread_muid";
    String ASTERIK = "*";
    String UNDERSCORE = "_";
    String ASTERIK_UNDERSCORE = "*_";
    String UNDERSCORE_ASTERIK = "_*";

    int IMAGE_MESSAGE = 10;
    int VIDEO_MESSAGE = 12;
    int VIDEO_CALL = 13;
    int CUSTOM_ACTION_MESSAGE = 14;
    int POLL_MESSAGE = 15;
    int CONFERENCE_CALL = 16;
    int FILE_MESSAGE = 11;
    int TEXT_MESSAGE = 1;
    int READ_MESSAGE = 6;
    int PUBLIC_NOTE = 5;
    int REQ_CODE_INVITE_GUEST = 1020;

    int ANDROID_USER = 1;

    int CHANNEL_SUBSCRIBED = 1;
    int CHANNEL_UNSUBSCRIBED = 0;

    int STATUS_CHANNEL_CLOSED = 0;
    int STATUS_CHANNEL_OPEN = 1;

    int TYPING_SHOW_MESSAGE = 0;
    int TYPING_STARTED = 1;
    int TYPING_STOPPED = 2;

    int MESSAGE_SENT = 1;
    int MESSAGE_DELIVERED = 2;
    int MESSAGE_READ = 3;
    int MESSAGE_UNSENT = 4;
    int MESSAGE_IMAGE_RETRY = 115;
    int MESSAGE_FILE_RETRY = 6;

    int PERMISSION_CONSTANT_CAMERA = 9;
    int PERMISSION_CONSTANT_GALLERY = 8;
    int PERMISSION_READ_IMAGE_FILE = 4;
    int PERMISSION_SAVE_BITMAP = 5;
    int PERMISSION_READ_FILE = 6;
    String EXTRA_CREATE_GROUP = "create_group";

    int OPEN_CAMERA_ADD_IMAGE = 514;
    int OPEN_GALLERY_ADD_IMAGE = 515;
    int SELECT_FILE = 516;
    int SELECT_AUDIO = 518;
    int SELECT_DOCUMENT = 517;
    int SELECT_VIDEO = 519;
    int SELECT_NONE = 600;
    int START_POLL = 520;

    //Notification Type
    int NOTIFICATION_DEFAULT = -1;
    int NOTIFICATION_READ_ALL = 6;

    // action
    String FUGU_CUSTOM_ACTION_SELECTED = "FUGU_CUSTOM_ACTION_SELECTED";
    String FUGU_CUSTOM_ACTION_PAYLOAD = "FUGU_CUSTOM_ACTION_PAYLOAD";

    String IMAGE_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator + "fugu" +
            File.separator + "picture";
    String CONVERSATION = "conversation";
    String NOTIFICATION_TYPE = "notification_type";
    String PUSH_TYPE = "push_type";
    String USER_ID = "user_id";
    String POLL_ID = "puid";
    String EN_USER_ID = "en_user_id";
    String PEER_CHAT_PARAMS = "peer_chat_params";
    String APP_SECRET_KEY = "app_secret_key";
    String DEVICE_TYPE = "device_type";
    String DEVICE_DETAILS = "device_details";
    String RESELLER_TOKEN = "reseller_token";
    String REFERENCE_ID = "reference_id";
    String DEVICE_ID = "device_id";
    String ANDROID_DEVICE_ID = "android_device_id";
    String APP_TYPE = "app_type";
    String APP_VERSION = "app_version";
    String ANDROID = "ANDROID";
    String ANDROIDS = "ANDROID";
    String USER_UNIQUE_KEY = "user_unique_key";
    String FULL_NAME = "full_name";
    String MESSAGE_STATUS = "message_status";
    String MESSAGE_INDEX = "message_index";
    String USER_TYPE = "user_type";
    String USER_REACTION_EMOJI = "user_reaction_emoji";
    String EMAIL = "email";
    String PHONE_NUMBER = "phone_number";
    String IS_TYPING = "is_typing";
    String CHANNEL_ID = "channel_id";
    String INVITE_LINK = "invite_link";
    String DOCUMENT_TYPE = "document_type";
    String MEMBERS_INFO = "members_info";
    String CHANNEL_NAME = "channel_name";
    String VIDEO_CALL_MODEL = "video_Call_model";
    String INIT_FULL_SCREEN_SERVICE = "init_full_screen_service";
    String INCOMING_VIDEO_CONFERENCE = "incoming_video_conference";
    String CHANNEL_THUMBNAIL_URL = "channel_thumbnail_url";
    String GET_DATA_TYPE = "get_data_type";
    String DEFAULT = "DEFAULT";
    String MEMBERS = "MEMBERS";
    String UNREAD_COUNT = "unread_count";
    String ON_SUBSCRIBE = "on_subscribe";
    String IMAGE_URL = "image_url";
    String IMAGE_URL_100X100 = "image_url_100x100";
    String THUMBNAIL_URL = "thumbnail_url";
    String ADDRESS = "address";
    String LAT_LONG = "lat_long";
    String IP_ADDRESS = "ip_address";
    String ATTRIBUTES = "attributes";
    String CUSTOM_ATTRIBUTES = "custom_attributes";
    String ISP2P = "isP2P";
    String CHAT_TYPE = "chat_type";
    String ADDED_MEMBER_INFO = "added_member_info";
    String ADDED_MEMBER_ID = "added_user_id";
    String ADDED_MEMBER_NAME = "added_user_name";
    String ADDED_MEMBER_IMAGE = "added_user_image";
    String USER_IDS_TO_MAKE_ADMIN = "user_ids_to_make_admin";
    String USER_IDS_TO_REMOVE_ADMIN = "user_ids_to_remove_admin";
    String ERROR = "error";
    String INTRO_MESSAGE = "intro_message";
    String MESSAGE_UNIQUE_ID = "muid";
    String MESSAGE_ID = "message_id";
    String DELETED_MESSAGE = "deleted_message";
    String TAGGED_USERS = "tagged_users";
    String PUSH_SOURCE = "push_source";
    String NOTI_MSG = "noti_msg";
    String REMOVED_USER_ID = "removed_user_id";
    String FUGU = "FUGU";
    String MESSAGE = "message";
    String CALL_TYPE = "call_type";
    String ROOM_NAME = "room_name";
    String BASE_URL = "base_url";
    String FORMATTED_MESSAGE = "formatted_message";
    String TITLE = "title";
    String MESSAGE_TYPE = "message_type";
    String LAST_SENT_BY_FULL_NAME = "last_sent_by_full_name";
    String LAST_SENT_BY_ID = "last_sent_by_id";
    String USER_THUMBNAIL_IMAGE = "user_thumbnail_image";
    String NEW_MESSAGE = "new_message";
    String DATE_TIME = "date_time";
    String SHOW_PUSH = "showpush";
    String POSITION = "position";
    String PROGRESS = "progress";
    String STATUS_UPLOAD = "statusUpload";
    String MUTED = "MUTED";
    String PAGE_START = "page_start";
    String USER_PAGE_START = "user_page_start";
    String PAGE_END = "page_end";
    String USER_IMAGE = "user_image";
    String IMAGE_HEIGHT = "image_height";
    String IMAGE_WIDTH = "image_width";
    String FILE_NAME = "file_name";
    String FILE_SIZE = "file_size";
    String FILE_EXTENSION = "file_extension";
    String UPDATE_NOTIFICATION_COUNT = "update_notification_count";
    int MAX_HEIGHT = 250;
    int MIN_HEIGHT = 150;
    int MAX_WIDTH = 248;
    int MAX_WIDTH_OUTER_SPIKED = 260;
    int MAX_WIDTH_OUTER = 248;
    String VIDEO_CALL_TYPE = "video_call_type";
    String HUNGUP_TYPE = "hungup_type";
    String SDP_M_LINE_INDEX = "sdpMLineIndex";
    String SDP_MID = "sdpMid";
    String CANDIDATE = "candidate";
    String IS_SILENT = "is_silent";
    String SDP = "sdp";
    String LOCAL_SET_REMOTE_DESC = "localSetRemoteDesc";
    String REMOTE_SET_REMOTE_DESC = "remoteSetRemoteDesc";
    String REMOTE_SET_LOCAL_DESC = "remoteSetLocalDesc";
    String OFFER_TO_RECEIVE_AUDIO = "offerToReceiveAudio";
    String OFFER_TO_RECEIVE_VIDEO = "offerToReceiveVideo";
    String LOCAL_CREATE_OFFER = "localCreateOffer";
    String REMOTE_CREATE_OFFER = "remoteCreateOffer";
    String USER_NAME = "username";
    String CREDENTIAL = "credential";
    String TURN_API_KEY = "turnApiKey";
    String STUN_SERVERS = "stunServers";
    String TURN_SERVERS = "turnServers";
    String CALL_STATUS = "call_status";
    String CALL_TIMER = "call_timer";
    String ONGOING_VIDEO_CALL = "Ongoing Video Call...";
    String RINGING = "Ringing...";
    String QUESTION = "question";
    String POLL_OPTIONS = "poll_options";
    String IS_MULTIPLE_SELECT = "multiple_select";
    String EXPIRY_TIME = "expire_time";
    String BUTTONS = "buttons";
    String CUSTOM_ACTIONS = "custom_actions";
    String LABEL = "label";
    String ACTION = "action";
    String STYLE = "style";
    String ACTION_TYPE = "action_type";
    String OUTPUT = "output";
    String IS_REQUIRED = "is_required";
    String HINT = "hint";
    String MINIMUM_LENGTH = "minimum_length";
    String DEFAULT_TEXT_FIELD = "default_text_field";
    String TAGGED_USER_ID = "tagged_user_id";
    String LEAVE_ID = "leave_id";
    String CONFIRMATION_TYPE = "confirmation_type";
    ArrayList<String> supportedFormats = new ArrayList(Arrays.asList("pdf", "doc",
            "docx", "ppt", "pptx", "xls", "xlsx", "ipa", "apk", "csv", "txt", "3gp", "mp3", "midi",
            "mpeg", "x-aiff", "x-wav", "webm", "ogg", "m4a", "wav", "mp4", "mov", "flv",
            "mkv", "mts", "mpg"));

    int DOWNLOAD_FAILED = 0;
    int DOWNLOAD_IN_PROGRESS = 1;
    int DOWNLOAD_PAUSED = 2;
    int DOWNLOAD_COMPLETED = 3;

    int UPLOAD_FAILED = 0;
    int UPLOAD_IN_PROGRESS = 1;
    int UPLOAD_PAUSED = 2;
    int UPLOAD_COMPLETED = 3;

    int FUGU_TYPE_HEADER = 0;
    int FUGU_ITEM_TYPE_OTHER = 1;
    int FUGU_ITEM_TYPE_SELF = 2;
    int FUGU_ITEM_TYPE_UNREAD = 3;
    int FUGU_ITEM_PUBLIC_NOTE = 4;
    int FUGU_ITEM_TYPE_OTHER_DELETED = 5;
    int FUGU_ITEM_TYPE_SELF_DELETED = 6;
    int FILE_NOT_EXIST = 4;
    String PROGRESS_INTENT = "progress_intent";
    String PROFILE_INTENT = "profile_intent";
    String INVALID_CREDENTIALS_INTENT = "profile_intent";


    int TEXT_MESSGAE_SELF = 0;
    int TEXT_MESSGAE_OTHER = 1;
    int IMAGE_MESSGAE_SELF = 2;
    int IMAGE_MESSGAE_OTHER = 3;
    int FILE_MESSGAE_SELF = 4;
    int FILE_MESSGAE_OTHER = 5;
    int HEADER_ITEM = 6;
    int UNREAD_ITEM = 8;
    int MESSAGE_DELETED_SELF = 9;
    int MESSAGE_DELETED_OTHER = 10;
    int VIDEO_MESSGAE_SELF = 11;
    int VIDEO_MESSGAE_OTHER = 12;
    int VIDEO_CALL_SELF = 13;
    int VIDEO_CALL_OTHER = 14;
    int POLL_SELF = 15;
    int POLL_OTHER = 16;
    int CUSTOM_ACTION = 17;


    int MESSAGE_NOTIFICATION = 1;
    int CLEAR_NOTIFICATION = 2;
    int DELETE_NOTIFICATION = 3;
    int NEW_WORKSPACE_NOTIFICATION = 5;
    int READ_ALL_NOTIFICATION = 6;
    int REMOVE_MEMBER_NOTIFICATION = 7;
    int GROUP_INFO_NOTIFICATION = 8;
    int ADD_MEMBER_NOTIFICATION = 9;
    int UPDATE_COUNTER_NOTIFICATION = 10;
    int TEST_NOTIFICATION = 11;
    int VIDEO_CALL_NOTIFICATION = 12;
    int AUDIO_CALL_NOTIFICATION = 13;
    int EDIT_MESSAGE_NOTIFICATION = 14;
    int DEACTIVATE_USER_NOTIFICATION = 15;
    int VIDEO_CONFERENCE_NOTIFICATION = 16;
    int MISSED_CALL_NOTIFICATION = 17;
    int NOTIFICATION_SYNC_SEVICE = 18;
    int VIDEO_CONFERENCE_HUNGUP_NOTIFICATION = 19;
    int INCOMING_CALL_NOTIFICATION = 20;
    int HANGOUTS_CALL_NOTIFICATION = 21;
    int TASK_ASSIGNED_NOTIFICATION = 22;
    int MEET_SCHEDULED_NOTIFICATION = 23;

    String MAX_NUMBER = "MaxNumber";

    int REQUEST_CODE_PICK_IMAGE = 0x100;
    String RESULT_PICK_IMAGE = "ResultPickImage";
    int REQUEST_CODE_TAKE_IMAGE = 0x101;

    int REQUEST_CODE_BROWSER_IMAGE = 0x102;
    String RESULT_BROWSER_IMAGE = "ResultBrowserImage";

    int REQUEST_CODE_PICK_VIDEO = 0x200;
    String RESULT_PICK_VIDEO = "ResultPickVideo";
    int REQUEST_CODE_TAKE_VIDEO = 0x201;

    int REQUEST_CODE_PICK_AUDIO = 0x300;
    String RESULT_PICK_AUDIO = "ResultPickAudio";
    int REQUEST_CODE_TAKE_AUDIO = 0x301;

    int REQUEST_CODE_PICK_FILE = 0x400;
    String RESULT_PICK_FILE = "ResultPickFILE";
    int REQUEST_CODE_PICK_ANY_FILE = 0X500;

    int REQUEST_MULTIPLE_IMAGES = 0x600;


    String SUFFIX = "Suffix";

    enum MeetType {
        JITSI {
            @Override
            public String toString() {
                return "JITSI";
            }
        },
        GOOGLE {
            @Override
            public String toString() {
                return "GOOGLE";
            }
        }
    }

    ArrayList<String> arrayOfColors = new ArrayList<>(Arrays.asList("#26A69A",
            "#4CAF50",
            "#5C6BC0",
            "#7E57C2",
            "#EF5350",
            "#9E9D24",
            "#FBC02D",
            "#A1887F",
            "#E040FB",
            "#EC407A"));

    ArrayList<String> SUPPORTED_FILE_FORMATS = new ArrayList<>(Arrays.asList("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "ipa", "apk", "csv", "txt"));
    ArrayList<String> SUPPORTED_AUDIO_FORMATS = new ArrayList<>(Arrays.asList("3gp", "mp3", "midi", "mpeg", "x-aiff", "mpeg", "x-wav", "m4a", "wav"));
    String EMOJI_CHECKER_REGEX = "";

    HashMap<String, Integer> IMAGE_MAP = new HashMap<String, Integer>() {{
        put("mp3", R.drawable.music);
        put("wav", R.drawable.music);
//        put("txt", R.drawable.txt);
//        put("pdf", R.drawable.pdf);
//        put("csv", R.drawable.csv);
//        put("doc", R.drawable.doc);
//        put("docx", R.drawable.doc);
//        put("ppt", R.drawable.ppt);
//        put("pptx", R.drawable.ppt);
//        put("xls", R.drawable.excel);
//        put("xlsx", R.drawable.excel);
        put("apk", R.drawable.ic_android);
        put("ipa", R.drawable.ic_apple);
        put("video", R.drawable.ic_video); // Common icon for mimetype
        put("audio", R.drawable.ic_music); // Common icon for mimetype
        put("image", R.drawable.ic_photo); // Common icon for mimetype
    }};

    HashMap<String, FileType> FILE_TYPE_MAP = new HashMap<String, FileType>() {{
        put("pdf", FileType.PDF_FILE);
        put("ppt", FileType.PPT_FILE);
        put("pptx", FileType.PPTX_FILE);
        put("xls", FileType.XLS_FILE);
        put("xlsx", FileType.XLSX_FILE);
        put("txt", FileType.TXT_FILE);
        put("csv", FileType.CSV_FILE);
        put("doc", FileType.DOC_FILE);
        put("docx", FileType.DOCX_FILE);
        put("apk", FileType.APK_FILE);
        put("ipa", FileType.IPA_FILE);
        put("mp4", FileType.MP4_FILE);
        put("flv", FileType.FLV_FILE);
        put("mkv", FileType.MKV_FILE);
        put("mov", FileType.MOV_FILE);
        put("3gp", FileType.TGP_FILE);
        put("mp3", FileType.MP3_FILE);
        put("midi", FileType.MIDI_FILE);
        put("mpeg", FileType.MPEG_FILE);
        put("x-aiff", FileType.XAIFF_FILE);
        put("x-wav", FileType.XWAV_FILE);
        put("webm", FileType.WEBM_FILE);
        put("ogg", FileType.OGG_FILE);
        put("m4a", FileType.M4A_FILE);
        put("wav", FileType.WAV_FILE);
        put("3gpp", FileType.TGPP_FILE);
        put("mts", FileType.MTS_FILE);
        put("mpg", FileType.MPG_FILE);
        put("jpg", FileType.JPG_FILE);
        put("jpeg", FileType.JPEG_FILE);
        put("gif", FileType.GIF_FILE);
        put("png", FileType.PNG_FILE);
        put("default", FileType.DEFAULT_FILE);
    }};

    /**
     * The type of file being Saved
     */
    enum FileType {
        LOG_FILE(OTHERS, ".log"),
        DOC_FILE(DOCUMENT, ".doc"),
        DOCX_FILE(DOCUMENT, ".docx"),
        TXT_FILE(DOCUMENT, ".txt"),
        PPT_FILE(DOCUMENT, ".ppt"),
        PPTX_FILE(DOCUMENT, ".pptx"),
        IPA_FILE(DOCUMENT, ".ipa"),
        XLS_FILE(DOCUMENT, ".xls"),
        XLSX_FILE(DOCUMENT, ".xlsx"),
        APK_FILE(DOCUMENT, ".apk"),
        CSV_FILE(DOCUMENT, ".csv"),
        IMAGE_FILE(IMAGE, ".jpg"),
        GENERAL_FILE(DOCUMENT, ".txt"),
        PDF_FILE(DOCUMENT, ".pdf"),
        MP3_FILE(AUDIO, ".mp3"),
        PRIVATE_FILE(OTHERS, ".sys"),
        OTHERSS(OTHERS, ".other"),
        TGP_FILE(VIDEO, ".3gp"),
        MIDI_FILE(VIDEO, ".midi"),
        MPEG_FILE(VIDEO, ".mpeg"),
        XAIFF_FILE(VIDEO, ".x-aiff"),
        XWAV_FILE(VIDEO, ".x-wav"),
        WEBM_FILE(VIDEO, ".webm"),
        OGG_FILE(VIDEO, ".ogg"),
        M4A_FILE(VIDEO, ".m4a"),
        WAV_FILE(VIDEO, ".wav"),
        MP4_FILE(VIDEO, ".mp4"),
        FLV_FILE(VIDEO, ".flv"),
        MKV_FILE(VIDEO, ".mkv"),
        MOV_FILE(VIDEO, ".mov"),
        MPG_FILE(VIDEO, ".mpg"),
        JPG_FILE(IMAGE, ".jpg"),
        JPEG_FILE(IMAGE, ".jpeg"),
        MTS_FILE(VIDEO, ".mts"),
        TGPP_FILE(VIDEO, ".3gpp"),
        PNG_FILE(IMAGE, ".png"),
        GIF_FILE(GIF, ".gif"),
        DEFAULT_FILE(DOCUMENT, ".default");

        public final String extension;
        public final String directory;

        FileType(String relativePath, String extension) {
            this.extension = extension;
            this.directory = relativePath;
        }
    }

    interface UserType {
        int BOT = 0;
        int CUSTOMER = 1;
        int AGENT = 2;
        int FUGU_BOT = 3;
        int ATTENDANCE_BOT = 4;
        int FUGU_SUPPORT = 5;
        int GUEST = 6;
        int SCRUM_BOT = 7;
        int HRM_BOT = 8;
        int CONFERENCE_BOT = 9;
        int SELF_BOT = 10;
    }

    interface ChatType {
        int P2P = 1;
        int OTHER = 0;
        int O2O = 2;
        int PRIVATE_GROUP = 3;
        int PUBLIC_GROUP = 4;
        int GENERAL_GROUP = 5;
        int DEFAULT_GROUP = 6;
        int BOT = 7;
        int CUSTOM_GROUP = 8;
    }

    interface RequestCodes {
        int SHOW_NOTIFICATIONS_REQUEST = 101;
        int START_MEET_REQUEST = 102;
        int SCHEDULE_MEET_REQUEST = 103;
        int SELECT_MEMBERS_FOR_MEET_REQUEST = 104;
    }

    enum AutoDownloadLevel {
        WIFI {
            @Override
            public String toString() {
                return "WIFI";
            }
        },
        MOBILE_NETWORK {
            @Override
            public String toString() {
                return "MOBILE_NETWORK";
            }
        },
        BOTH {
            @Override
            public String toString() {
                return "BOTH";
            }
        },
        NONE {
            @Override
            public String toString() {
                return "NONE";
            }
        }
    }


    enum AttendanceAuthenticationLevel {
        CAMERA {
            @Override
            public String toString() {
                return "CAMERA";
            }
        },
        LOCATION {
            @Override
            public String toString() {
                return "LOCATION";
            }
        },
        BOTH {
            @Override
            public String toString() {
                return "BOTH";
            }
        },
        NONE {
            @Override
            public String toString() {
                return "NONE";
            }
        }
    }

    enum DownloadStatus {
        DOWNLOAD_FAILED(0),
        DOWNLOAD_IN_PROGRESS(1),
        DOWNLOAD_PAUSED(2),
        DOWNLOAD_COMPLETED(3);

        public final int downloadStatus;

        DownloadStatus(int downloadStatus) {
            this.downloadStatus = downloadStatus;
        }
    }

    enum UploadStatus {
        UPLOAD_FAILED(0),
        UPLOAD_IN_PROGRESS(1),
        UPLOAD_PAUSED(2),
        UPLOAD_COMPLETED(3);

        public final int uploadStatus;

        UploadStatus(int downloadStatus) {
            this.uploadStatus = downloadStatus;
        }
    }

    enum Role {
        USER {
            @Override
            public String toString() {
                return "USER";
            }
        },
        ADMIN {
            @Override
            public String toString() {
                return "ADMIN";
            }
        }
    }


    enum Style {
        DEFAULT {
            @Override
            public String toString() {
                return "DEFAULT";
            }
        },
        DANGER {
            @Override
            public String toString() {
                return "DANGER";
            }
        },
        SUCCESS {
            @Override
            public String toString() {
                return "SUCCESS";
            }
        }
    }

    enum Feedback {
        HELP {
            @Override
            public String toString() {
                return "HELP";
            }
        },
        AUDIO_CALL {
            @Override
            public String toString() {
                return "AUDIO_CALL";
            }
        },
        VIDEO_CALL {
            @Override
            public String toString() {
                return "VIDEO_CALL";
            }
        },
        VIDEO_CONFERENCE {
            @Override
            public String toString() {
                return "VIDEO_CONFERENCE";
            }
        }
    }


    enum VideoCallType {
//        START_CALL("START_CALL"),
//        CALL_REJECTED("CALL_REJECTED"),
//        READY_TO_CONNECT("READY_TO_CONNECT"),
//        CALL_HUNG_UP("CALL_HUNG_UP"),
//        USER_BUSY("USER_BUSY"),
//        VIDEO_OFFER("VIDEO_OFFER"),
//        NEW_ICE_CANDIDATE("NEW_ICE_CANDIDATE"),
//        VIDEO_ANSWER("VIDEO_ANSWER");

        START_CALL {
            @Override
            public String toString() {
                return "START_CALL";
            }
        },
        READY_TO_CONNECT {
            @Override
            public String toString() {
                return "READY_TO_CONNECT";
            }
        },
        CALL_HUNG_UP {
            @Override
            public String toString() {
                return "CALL_HUNG_UP";
            }
        },
        CALL_REJECTED {
            @Override
            public String toString() {
                return "CALL_REJECTED";
            }
        },
        VIDEO_OFFER {
            @Override
            public String toString() {
                return "VIDEO_OFFER";
            }
        },
        NEW_ICE_CANDIDATE {
            @Override
            public String toString() {
                return "NEW_ICE_CANDIDATE";
            }
        },
        USER_BUSY {
            @Override
            public String toString() {
                return "USER_BUSY";
            }
        },
        VIDEO_ANSWER {
            @Override
            public String toString() {
                return "VIDEO_ANSWER";
            }
        },
        SWITCH_TO_CONFERENCE {
            @Override
            public String toString() {
                return "SWITCH_TO_CONFERENCE";
            }
        }
//        public final String videoCallType;
//
//        VideoCallType(String videoCallType) {
//            this.videoCallType = videoCallType;
//        }
    }

    enum SocketEvent {
        CONNECT {
            @Override
            public String toString() {
                return "connect";
            }
        },
        CONNECT_ERROR {
            @Override
            public String toString() {
                return "connect_error";
            }
        },
        CONNECTING {
            @Override
            public String toString() {
                return "connecting";
            }
        },
        RECONNECT {
            @Override
            public String toString() {
                return "reconnect";
            }
        },
        CONNECT_TIMEOUT {
            @Override
            public String toString() {
                return "connect_timeout";
            }
        },
        RECONNECT_ATTEMPT {
            @Override
            public String toString() {
                return "reconnect_attempt";
            }
        },
        RECONNECTING {
            @Override
            public String toString() {
                return "reconnecting";
            }
        },
        RECONNECT_ERROR {
            @Override
            public String toString() {
                return "reconnect_error";
            }
        },
        DISCONNECT {
            @Override
            public String toString() {
                return "disconnect";
            }
        },
        VIDEO_CONFERENCE {
            @Override
            public String toString() {
                return "video_conference";
            }
        },
        HANGOUTS_CALL {
            @Override
            public String toString() {
                return "hangouts_call";
            }
        },
        MESSAGE {
            @Override
            public String toString() {
                return "message";
            }
        },
        THREAD_MESSAGE {
            @Override
            public String toString() {
                return "thread_message";
            }
        },
        CLEAR_CHAT {
            @Override
            public String toString() {
                return "clear_chat";
            }
        },
        DELETE_MESSAGE {
            @Override
            public String toString() {
                return "delete_message";
            }
        },
        NEW_WORKSPACE {
            @Override
            public String toString() {
                return "new_workspace";
            }
        },
        READ_ALL {
            @Override
            public String toString() {
                return "read_all";
            }
        },
        REMOVE_MEMBER {
            @Override
            public String toString() {
                return "remove_member";
            }
        },
        CHANGE_GROUP_INFO {
            @Override
            public String toString() {
                return "change_group_info";
            }
        },
        ADD_MEMBER {
            @Override
            public String toString() {
                return "add_member";
            }
        },
        READ_UNREAD_NOTIFICATION {
            @Override
            public String toString() {
                return "read_unread_notification";
            }
        },
        VIDEO_CALL {
            @Override
            public String toString() {
                return "video_call";
            }
        },
        AUDIO_CALL {
            @Override
            public String toString() {
                return "audio_call";
            }
        },
        EDIT_MESSAGE {
            @Override
            public String toString() {
                return "edit_message";
            }
        },
        SESSION_EXPIRED {
            @Override
            public String toString() {
                return "session_expired";
            }
        },
        TYPING {
            @Override
            public String toString() {
                return "typing";
            }
        },
        STOP_TYPING {
            @Override
            public String toString() {
                return "stop_typing";
            }
        },
        SUBSCRIBE_USER {
            @Override
            public String toString() {
                return "subscribe_user";
            }
        },
        SUBSCRIBE_PUSH {
            @Override
            public String toString() {
                return "subscribe_push";
            }
        },
        SUBSCRIBE_CHANNEL {
            @Override
            public String toString() {
                return "subscribe_channel";
            }
        },
        UNSUBSCRIBE_CHANNEL {
            @Override
            public String toString() {
                return "unsubscribe_channel";
            }
        },
        PIN_CHAT {
            @Override
            public String toString() {
                return "pin_chat";
            }
        },
        UNPIN_CHAT {
            @Override
            public String toString() {
                return "unpin_chat";
            }
        },
        POLL {
            @Override
            public String toString() {
                return "poll";
            }
        },
        REACTION {
            @Override
            public String toString() {
                return "reaction";
            }
        },
        PRESENCE {
            @Override
            public String toString() {
                return "presence";
            }
        },
        SUBSCRIBE_PRESENCE {
            @Override
            public String toString() {
                return "subscribe_presence";
            }
        },
        UNSUBSCRIBE_PRESENCE {
            @Override
            public String toString() {
                return "unsubscribe_presence";
            }
        },
        CALLING {
            @Override
            public String toString() {
                return "calling";
            }
        },
        VIEWER_COUNT {
            @Override
            public String toString() {
                return "viewer_count";
            }
        },
        ASSIGN_TASK {
            @Override
            public String toString() {
                return "assign_task";
            }
        },
        SCHEDULE_MEETING {
            @Override
            public String toString() {
                return "schedule_meeting";
            }
        },
        UPDATE_NOTIFICATION_COUNT {
            @Override
            public String toString() {
                return "update_notification_count";
            }
        }

    }

    enum JitsiCallType {
        START_CONFERENCE {
            @Override
            public String toString() {
                return "START_CONFERENCE";
            }
        },
        START_CONFERENCE_IOS {
            @Override
            public String toString() {
                return "START_CONFERENCE";
            }
        },
        READY_TO_CONNECT_CONFERENCE {
            @Override
            public String toString() {
                return "READY_TO_CONNECT_CONFERENCE";
            }
        },
        READY_TO_CONNECT_CONFERENCE_IOS {
            @Override
            public String toString() {
                return "READY_TO_CONNECT_CONFERENCE_IOS";
            }
        },
        HUNGUP_CONFERENCE {
            @Override
            public String toString() {
                return "HUNGUP_CONFERENCE";
            }
        },
        REJECT_CONFERENCE {
            @Override
            public String toString() {
                return "REJECT_CONFERENCE";
            }
        },
        USER_BUSY_CONFERENCE {
            @Override
            public String toString() {
                return "USER_BUSY_CONFERENCE";
            }
        },
        OFFER_CONFERENCE {
            @Override
            public String toString() {
                return "OFFER_CONFERENCE";
            }
        },
        ANSWER_CONFERENCE {
            @Override
            public String toString() {
                return "ANSWER_CONFERENCE";
            }
        }
    }

    enum FuguEvents {
        CREATE_GROUP {
            @Override
            public String toString() {
                return "CREATE_GROUP";
            }
        },
        BROWSE_GROUP {
            @Override
            public String toString() {
                return "BROWSE_GROUP";
            }
        },
        INVITE_MEMBER {
            @Override
            public String toString() {
                return "INVITE_MEMBER";
            }
        },
        CREATE_WORKSPACE {
            @Override
            public String toString() {
                return "CREATE_WORKSPACE";
            }
        },
        OPEN_CAMERA {
            @Override
            public String toString() {
                return "OPEN_CAMERA";
            }
        },
        VIDEO_CONFERENCE {
            @Override
            public String toString() {
                return "VIDEO_CONFERENCE";
            }
        }

    }

    enum NOTIFICATION_LEVEL {
        ALL_MESSAGES {
            @Override
            public String toString() {
                return "UNMUTED";
            }
        },
        DIRECT_MENTIONS {
            @Override
            public String toString() {
                return "DIRECT_MENTIONS";
            }
        },
        ALL_MENTIONS {
            @Override
            public String toString() {
                return "MUTED";
            }
        }
    }

    enum SEND_MESSGAES {
        ALL_PARTICIPANTS {
            @Override
            public String toString() {
                return "ALL_PARTICIPANTS";
            }
        },
        ONLY_ADMINS {
            @Override
            public String toString() {
                return "ONLY_ADMINS";
            }
        }
    }

}
