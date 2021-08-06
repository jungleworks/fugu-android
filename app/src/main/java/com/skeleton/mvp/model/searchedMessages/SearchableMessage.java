
package com.skeleton.mvp.model.searchedMessages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchableMessage {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("last_message_id")
    @Expose
    private Integer lastMessageId;
    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("searchable_message")
    @Expose
    private String searchableMessage;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("user_type")
    @Expose
    private Integer userType;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("message_type")
    @Expose
    private Integer messageType;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("chat_type")
    @Expose
    private Integer chatType;
    @SerializedName("message_index")
    @Expose
    private Integer messageIndex;
    @SerializedName("thread_message")
    @Expose
    private Boolean threadMessage;
    @SerializedName("channel_image")
    @Expose
    private String channelImage = "";
    @SerializedName("notification")
    @Expose
    private String notification = "";

    @SerializedName("file_name")
    @Expose
    private String fileName = "";

    @SerializedName("file_size")
    @Expose
    private String fileSize = "";

    @SerializedName("thumbnail_url")
    @Expose
    private String imageurl = "";

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Integer lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getSearchableMessage() {
        return searchableMessage;
    }

    public void setSearchableMessage(String searchableMessage) {
        this.searchableMessage = searchableMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
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

    public Integer getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(Integer messageIndex) {
        this.messageIndex = messageIndex;
    }

    public Boolean getThreadMessage() {
        return threadMessage;
    }

    public void setThreadMessage(Boolean threadMessage) {
        this.threadMessage = threadMessage;
    }

    public String getChannelImage() {
        if (channelImage == null) {
            return "";
        }
        return channelImage;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
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

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
