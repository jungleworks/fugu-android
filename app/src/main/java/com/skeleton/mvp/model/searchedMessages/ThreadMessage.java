
package com.skeleton.mvp.model.searchedMessages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThreadMessage {

    @SerializedName("searchable_message")
    @Expose
    private String searchableMessage;
    @SerializedName("message_id")
    @Expose
    private Integer messageId;
    @SerializedName("thread_muid")
    @Expose
    private String threadMuid;
    @SerializedName("thread_user_id")
    @Expose
    private Long threadUserId;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("chat_type")
    @Expose
    private Integer chatType=2;
    @SerializedName("channel_image")
    @Expose
    private String channelImage = "";

    @SerializedName("message_type")
    @Expose
    private int messageType;

    @SerializedName("file_name")
    @Expose
    private String fileName = "";

    @SerializedName("file_size")
    @Expose
    private String fileSize = "";

    @SerializedName("thumbnail_url")
    @Expose
    private String imageurl = "";


    public String getSearchableMessage() {
        return searchableMessage;
    }

    public void setSearchableMessage(String searchableMessage) {
        this.searchableMessage = searchableMessage;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getThreadMuid() {
        return threadMuid;
    }

    public void setThreadMuid(String threadMuid) {
        this.threadMuid = threadMuid;
    }

    public Long getThreadUserId() {
        return threadUserId;
    }

    public void setThreadUserId(Long threadUserId) {
        this.threadUserId = threadUserId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getChatType() {
        return chatType;
    }

    public void setChatType(Integer chatType) {
        this.chatType = chatType;
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

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
