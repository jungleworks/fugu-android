
package com.skeleton.mvp.model.innerMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.constant.FuguAppConstant;

public class ThreadMessage {


    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("message_id")
    @Expose
    private Integer messageId;
    @SerializedName("message")
    @Expose
    private String message="";
    @SerializedName("thread_message_id")
    @Expose
    private Integer threadMessageId;
    @SerializedName("thread_muid")
    @Expose
    private String threadMuid;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("message_type")
    @Expose
    private Integer messageType;
    @SerializedName("user_reaction")
    @Expose
    private com.skeleton.mvp.model.UserReaction userReaction;

    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl = "";

    @SerializedName("image_url")
    @Expose
    private String imageUrl = "";

    @SerializedName("url")
    @Expose
    private String url = "";
    @SerializedName("message_state")
    @Expose
    private int messageState;

    @SerializedName("user_type")
    @Expose
    private int userType = FuguAppConstant.UserType.CUSTOMER;

    @SerializedName("image_height")
    @Expose
    private int imageHeight = 700;

    @SerializedName("image_width")
    @Expose
    private int imageWidth = 700;

    @SerializedName("file_name")
    @Expose
    private String fileName = "";

    @SerializedName("file_size")
    @Expose
    private String fileSize = "";

    @SerializedName("is_starred")
    @Expose
    private int isStarred = 0;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getThreadMessageId() {
        return threadMessageId;
    }

    public void setThreadMessageId(Integer threadMessageId) {
        this.threadMessageId = threadMessageId;
    }

    public String getThreadMuid() {
        return threadMuid;
    }

    public void setThreadMuid(String threadMuid) {
        this.threadMuid = threadMuid;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public com.skeleton.mvp.model.UserReaction getUserReaction() {
        return userReaction;
    }

    public void setUserReaction(com.skeleton.mvp.model.UserReaction userReaction) {
        this.userReaction = userReaction;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
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

    public int getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(int isStarred) {
        this.isStarred = isStarred;
    }
}
