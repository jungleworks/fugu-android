package com.skeleton.mvp.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.model.customAction.CustomAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhavya Rattan on 12/06/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class Message implements Serializable,Cloneable {

    @SerializedName("seen_by")
    @Expose
    String seenBy = "";

    @SerializedName("muid")
    @Expose
    String muid;
    @SerializedName("isSent")
    @Expose
    boolean isSent;
    @SerializedName("full_name")
    @Expose
    private String fromName;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("user_id")
    @Expose
    private Long userId;

    public void setSentAtUtc(String sentAtUtc) {
        this.sentAtUtc = sentAtUtc;
    }

    @SerializedName("date_time")
    @Expose
    private String sentAtUtc = "";
    @SerializedName("message")
    @Expose
    private String message = "";

    @SerializedName("alteredMessage")
    @Expose
    private String alteredMessage = "";

    @SerializedName("formattedMessage")
    @Expose
    private String formattedMessage = "";

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    @SerializedName("message_status")
    @Expose
    private Integer messageStatus = 3;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl = "";
    @SerializedName("image_url")
    @Expose
    private String image_url = "";

    @SerializedName("image_url_100x100")
    @Expose
    private String image_url_100x100 = "";

    @SerializedName("sharable_thumbnail_url")
    @Expose
    private String sharableThumbnailUrl = "";
    @SerializedName("sharable_image_url")
    @Expose
    private String sharableImage_url = "";

    @SerializedName("sharable_image_url_100x100")
    @Expose
    private String sharableImage_url_100x100 = "";

    @SerializedName("url")
    @Expose
    private String url = "";
    @SerializedName("message_type")
    @Expose
    private int messageType = FuguAppConstant.TEXT_MESSAGE;


    @SerializedName("thread_message_data")
    @Expose
    private ArrayList<ThreadMessageData> thread_message_data;

    @SerializedName("tagged_users")
    private ArrayList<Integer> taggedUsers;

    @SerializedName("chat_type")
    private int chatType = FuguAppConstant.ChatType.OTHER;

    @SerializedName("email")
    private String email;

    @SerializedName("local_url")
    private String localUrl;

    @SerializedName("isAudioPlaying")
    private boolean isAudioPlaying = false;

    @SerializedName("currentprogress")
    private int currentprogress;

    @SerializedName("downloadStatus")
    private int downloadStatus = 1;
    @SerializedName("uploadStatus")
    private int uploadStatus = 1;
    @SerializedName("sentFilePath")
    private String sentFilePath;

    @SerializedName("unsentFilePath")
    private String unsentFilePath;

    @SerializedName("fileImagePreview")
    private String fileImagePreview;

    @SerializedName("isExpired")
    private boolean isExpired = false;

    @SerializedName("emojis")
    private ArrayList<String> emojis;

    @SerializedName("user_reaction")
    @Expose
    private UserReaction userReaction;

    @SerializedName("user_image")
    @Expose
    private String userImage = "";
    @SerializedName("thread_message")
    @Expose
    private boolean threadMessage;

    @SerializedName("thread_message_click")
    @Expose
    private boolean threadMessageClick = true;

//    @SerializedName("reply_count")
//    @Expose
//    private int replyCount = 0;

    @SerializedName("thread_message_count")
    @Expose
    private int threadMessageCount = 0;

    @SerializedName("thread_muid")
    @Expose
    private String threadMuid;

    @SerializedName("is_thread_message")
    @Expose
    private Boolean isThreadMessage = false;
    @SerializedName("user_type")
    @Expose
    private int userType;

    @SerializedName("message_state")
    @Expose
    private int messageState = 1;

    @SerializedName("image_height")
    @Expose
    private int imageHeight = 700;

    @SerializedName("image_width")
    @Expose
    private int imageWidth = 700;

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("animate")
    @Expose
    private boolean animate;

    @SerializedName("video_call_duration")
    @Expose
    private Long videoCallDuration = -1L;

    @SerializedName("question")
    @Expose
    private String question = "";
    @SerializedName("multiple_select")
    @Expose
    private Boolean multipleSelect = false;
    @SerializedName("poll_options")
    @Expose
    private List<PollOption> pollOptions = null;
    @SerializedName("total_votes")
    @Expose
    private Integer total_votes = 0;

    @SerializedName("expire_time")
    @Expose
    private Long expireTime = -1L;

    @SerializedName("is_starred")
    @Expose
    private int isStarred = 0;

    @SerializedName("call_type")
    @Expose
    private String callType = "VIDEO";

    @SerializedName("is_edit_mode")
    @Expose
    private Boolean isEditMode = false;


    @SerializedName("custom_actions")
    @Expose
    private ArrayList<CustomAction> customActions = new ArrayList<>();

    private int seenByCount=0;

    public int getSeenByCount() {
        return seenByCount;
    }

    public void setSeenByCount(int seenByCount) {
        this.seenByCount = seenByCount;
    }

    private String role = "";

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private ArrayList<SeenUser> seenUsers = new ArrayList<>();

    public ArrayList<SeenUser> getSeenUsers() {
        return seenUsers;
    }

    public void setSeenUsers(ArrayList<SeenUser> seenUsers) {
        this.seenUsers = seenUsers;
    }

    private boolean isProgressUpdate = false;

    public void setProgressUpdate(boolean isProgressUpdate) {
        this.isProgressUpdate = isProgressUpdate;
    }

    public boolean getProgressUpdate() {
        return isProgressUpdate;
    }

    public ArrayList<CustomAction> getCustomActions() {
        return customActions;
    }

    public void setCustomActions(ArrayList<CustomAction> customActions) {
        this.customActions = customActions;
    }

    public ArrayList<Integer> getTaggedUsers() {
        return taggedUsers;
    }

    public void setTaggedUsers(final ArrayList<Integer> taggedUsers) {
        this.taggedUsers = taggedUsers;
    }

    public String getImage_url_100x100() {
        return image_url_100x100;
    }

    public void setImage_url_100x100(String image_url_100x100) {
        this.image_url_100x100 = image_url_100x100;
    }

    public String getSharableImage_url_100x100() {
        return sharableImage_url_100x100;
    }

    public void setSharableImage_url_100x100(String sharableImage_url_100x100) {
        this.sharableImage_url_100x100 = sharableImage_url_100x100;
    }

    public int getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(int isStarred) {
        this.isStarred = isStarred;
    }

    public List<PollOption> getPollOptions() {
        return pollOptions;
    }

    public void setPollOptions(List<PollOption> pollOptions) {
        this.pollOptions = pollOptions;
    }


    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    private int downloadId = 0;

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getTotal_votes() {
        return total_votes;
    }

    public void setTotal_votes(Integer total_votes) {
        this.total_votes = total_votes;
    }

    @SerializedName("file_name")
    @Expose
    String fileName = "";
    @SerializedName("file_size")
    @Expose
    String fileSize = "";
    @SerializedName("file_extension")
    @Expose
    String fileExtension = "";
    @SerializedName("file_path")
    @Expose
    String filePath = "";

    String localSentAtUtc = "";

    public String getLocalSentAtUtc() {
        return localSentAtUtc;
    }

    public void setLocalSentAtUtc(String localSentAtUtc) {
        this.localSentAtUtc = localSentAtUtc;
    }

    private int messageIndex = 0;
    private int timeIndex = 0;
    private int rowType;

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public String getfromName() {
        return fromName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSentAtUtc() {
        return sentAtUtc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getImage_url() {
        return image_url;
    }

    public int getMessageType() {
        return messageType;
    }

    public Long getId() {
        if (id == null) {
            id = -1l;
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message() {
    }

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    public Message(String fromName, Long userId, String message, String sentAtUtc, int rowType,
                   int messageStatus, int messageIndex, int messageType, boolean isSent, String muid) {
        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.rowType = rowType;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.messageType = messageType;
        this.isSent = isSent;
        this.muid = muid;
    }

//    public Message(long id, String fromName, Long userId, String message, String sentAtUtc, int rowType,
//                   int messageStatus, int messageIndex, String url, String thumbnailUrl, int messageType, boolean isSent, String muid) {
//        this.id = id;
//        this.fromName = fromName;
//        this.userId = userId;
//        this.message = message;
//        this.sentAtUtc = sentAtUtc;
//        this.rowType = rowType;
//        this.messageStatus = messageStatus;
//        this.messageIndex = messageIndex;
//        this.thumbnailUrl = thumbnailUrl;
//        this.messageType = messageType;
//        this.url = url;
//        this.isSent = isSent;
//        this.muid = muid;
//    }

    public Message(long id, String fromName, Long userId, String message, String sentAtUtc, int rowType,
                   int messageStatus, int messageIndex, String image_url, String thumbnailUrl, int messageType, boolean isSent, String muid, int chatType, String email, String url) {
        this.id = id;
        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.rowType = rowType;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.thumbnailUrl = thumbnailUrl;
        this.messageType = messageType;
        this.image_url = image_url;
        this.isSent = isSent;
        this.muid = muid;
        this.chatType = chatType;
        this.email = email;
        this.url = url;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public int getRowType() {
        return rowType;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public String getUuid() {
        if (muid != null) {
            return muid;
        } else {
            return "";
        }
    }

    public void setUuid(String muid) {
        this.muid = muid;
    }

    public int getChatType() {
        if (chatType == FuguAppConstant.ChatType.OTHER) {
            return 1;
        }
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public boolean isAudioPlaying() {
        return isAudioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying) {
        isAudioPlaying = audioPlaying;
    }

    public int getCurrentprogress() {
        return currentprogress;
    }

    public void setCurrentprogress(int currentprogress) {
        this.currentprogress = currentprogress;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getSentFilePath() {
        return sentFilePath;
    }

    public void setSentFilePath(String sentFilePath) {
        this.sentFilePath = sentFilePath;
    }

    public String getUnsentFilePath() {
        return unsentFilePath;
    }

    public void setUnsentFilePath(String unsentFilePath) {
        this.unsentFilePath = unsentFilePath;
    }

    public String getFileImagePreview() {
        return fileImagePreview;
    }

    public void setFileImagePreview(String fileImagePreview) {
        this.fileImagePreview = fileImagePreview;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public ArrayList<String> getEmojis() {
        if (emojis == null) {
            return new ArrayList<>();
        }
        return emojis;
    }

    public void setEmojis(ArrayList<String> emojis) {
        this.emojis = emojis;
    }

    public UserReaction getUserReaction() {
        return userReaction;
    }

    public void setUserReaction(UserReaction userReaction) {
        this.userReaction = userReaction;
    }

    public String getUserImage() {
        if (TextUtils.isEmpty(userImage)) {
            return "";
        }
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public boolean getThreadMessage() {
        return threadMessage;
    }

    public void setThreadMessage(boolean threadMessage) {
        this.threadMessage = threadMessage;
    }

//    public int getReplyCount() {
//        return replyCount;
//    }
//
//    public void setReplyCount(int replyCount) {
//        this.replyCount = replyCount;
//    }


    public int getThreadMessageCount() {
        return threadMessageCount;
    }

    public void setThreadMessageCount(int threadMessageCount) {
        this.threadMessageCount = threadMessageCount;
    }

    public boolean isThreadMessageClick() {
        return threadMessageClick;
    }

    public void setThreadMessageClick(boolean threadMessageClick) {
        this.threadMessageClick = threadMessageClick;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }

    public void setRowType(int rowType) {
        this.rowType = rowType;
    }

    public String getThreadMuid() {
        return threadMuid;
    }

    public void setThreadMuid(String threadMuid) {
        this.threadMuid = threadMuid;
    }

    public Boolean getIsThreadMessage() {
        return isThreadMessage;
    }

    public void setIsThreadMessage(Boolean isThreadMessage) {
        this.isThreadMessage = isThreadMessage;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getSharableThumbnailUrl() {
        return sharableThumbnailUrl;
    }

    public void setSharableThumbnailUrl(String sharableThumbnailUrl) {
        this.sharableThumbnailUrl = sharableThumbnailUrl;
    }

    public String getSharableImage_url() {
        return sharableImage_url;
    }

    public void setSharableImage_url(String sharableImage_url) {
        this.sharableImage_url = sharableImage_url;
    }

    public Long getVideoCallDuration() {
        return videoCallDuration;
    }

    public void setVideoCallDuration(Long videoCallDuration) {
        this.videoCallDuration = videoCallDuration;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getMultipleSelect() {
        return multipleSelect;
    }

    public void setMultipleSelect(Boolean multipleSelect) {
        this.multipleSelect = multipleSelect;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public Boolean getEditMode() {
        return isEditMode;
    }

    public void setEditMode(Boolean editMode) {
        isEditMode = editMode;
    }

    public String getAlteredMessage() {
        return alteredMessage;
    }

    public void setAlteredMessage(String alteredMessage) {
        this.alteredMessage = alteredMessage;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public void setFormattedMessage(String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    public String getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(String seenBy) {
        this.seenBy = seenBy;
    }

    public ArrayList<ThreadMessageData> getThread_message_data() {
        return thread_message_data;
    }

    public void setThread_message_data(ArrayList<ThreadMessageData> thread_message_data) {
        this.thread_message_data = thread_message_data;
    }

    public Message(String muid, boolean isSent, String fromName, Long id, Long userId, String sentAtUtc, String message, Integer messageStatus, String thumbnailUrl, String image_url, String sharableThumbnailUrl, String sharableImage_url, String url, int messageType, ArrayList<Integer> taggedUsers, int chatType, String email, String localUrl, boolean isAudioPlaying, int currentprogress, int downloadStatus, int uploadStatus, String sentFilePath, String unsentFilePath, String fileImagePreview, boolean isExpired, ArrayList<String> emojis, UserReaction userReaction, String userImage, boolean threadMessage, boolean threadMessageClick, int replyCount, String threadMuid, Boolean isThreadMessage, int userType, int messageState, int imageHeight, int imageWidth, int downloadId, String fileName, String fileSize, String fileExtension, String filePath, int messageIndex, int timeIndex, int rowType) {
        this.muid = muid;
        this.isSent = isSent;
        this.fromName = fromName;
        this.id = id;
        this.userId = userId;
        this.sentAtUtc = sentAtUtc;
        this.message = message;
        this.messageStatus = messageStatus;
        this.thumbnailUrl = thumbnailUrl;
        this.image_url = image_url;
        this.sharableThumbnailUrl = sharableThumbnailUrl;
        this.sharableImage_url = sharableImage_url;
        this.url = url;
        this.messageType = messageType;
        this.taggedUsers = taggedUsers;
        this.chatType = chatType;
        this.email = email;
        this.localUrl = localUrl;
        this.isAudioPlaying = isAudioPlaying;
        this.currentprogress = currentprogress;
        this.downloadStatus = downloadStatus;
        this.uploadStatus = uploadStatus;
        this.sentFilePath = sentFilePath;
        this.unsentFilePath = unsentFilePath;
        this.fileImagePreview = fileImagePreview;
        this.isExpired = isExpired;
        this.emojis = emojis;
        this.userReaction = userReaction;
        this.userImage = userImage;
        this.threadMessage = threadMessage;
        this.threadMessageClick = threadMessageClick;
        this.threadMuid = threadMuid;
        this.isThreadMessage = isThreadMessage;
        this.userType = userType;
        this.messageState = messageState;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.downloadId = downloadId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
        this.filePath = filePath;
        this.messageIndex = messageIndex;
        this.timeIndex = timeIndex;
        this.rowType = rowType;
    }

}
