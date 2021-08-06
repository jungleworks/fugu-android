
package com.skeleton.mvp.model.starredmessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StarredMessage {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("chat_type")
    @Expose
    private Integer chatType;
    @SerializedName("thread_muid")
    @Expose
    private String threadMuid;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("message_type")
    @Expose
    private Integer messageType;
    @SerializedName("is_starred")
    @Expose
    private Integer isStarred;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("image_url")
    @Expose
    private String imageUrl = "";
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl = "";
    @SerializedName("image_width")
    @Expose
    private Integer imageWidth;
    @SerializedName("image_height")
    @Expose
    private Integer imageHeight;
    @SerializedName("url")
    @Expose
    private String url = "";
    @SerializedName("file_name")
    @Expose
    private String fileName = "";
    @SerializedName("file_size")
    @Expose
    private String fileSize = "";
    @SerializedName("thread_message")
    @Expose
    private Boolean threadMessage;
    @SerializedName("message_index")
    @Expose
    private Integer messageIndex = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getChatType() {
        return chatType;
    }

    public void setChatType(Integer chatType) {
        this.chatType = chatType;
    }

    public String getThreadMuid() {
        return threadMuid;
    }

    public void setThreadMuid(String threadMuid) {
        this.threadMuid = threadMuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(Integer isStarred) {
        this.isStarred = isStarred;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Boolean getThreadMessage() {
        return threadMessage;
    }

    public void setThreadMessage(Boolean threadMessage) {
        this.threadMessage = threadMessage;
    }

    public Integer getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(Integer messageIndex) {
        this.messageIndex = messageIndex;
    }

}
