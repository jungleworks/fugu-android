package com.skeleton.mvp.model;

import com.skeleton.mvp.util.FormatStringUtil;

/**
 * Created
 * rajatdhamija on 03/07/18.
 */

public class SearchMessage {
    private String channelName;
    private String message;
    private String date;
    private String fullName;
    private boolean isThreadMessage;
    private int chatType;
    private Long userId;
    private int messageId;
    private String threadMuid;
    private String muid;
    private Long channelId;
    private String notifications;
    private int messageType;
    private String fileName;
    private String fileSize;
    private String fileType;
    private String imageUrl;

    public SearchMessage(String channelName, String message, String date,
                         String fullName, boolean isThreadMessage, int chatType, Long userId,
                         int messageId, String threadMuid, String muid,
                         Long channelId, String notifications, int messageType, String fileName,
                         String fileSize, String fileType, String imageUrl) {
        this.channelName = channelName;
        this.message = FormatStringUtil.FormatString.INSTANCE.getFormattedString(message).get(1);
        this.date = date;
        this.fullName = fullName;
        this.isThreadMessage = isThreadMessage;
        this.chatType = chatType;
        this.userId = userId;
        this.messageId = messageId;
        this.threadMuid = threadMuid;
        this.muid = muid;
        this.channelId = channelId;
        this.notifications = notifications;
        this.messageType = messageType;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.imageUrl = imageUrl;

    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isThreadMessage() {
        return isThreadMessage;
    }

    public void setThreadMessage(boolean threadMessage) {
        isThreadMessage = threadMessage;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getThreadMuid() {
        return threadMuid;
    }

    public void setThreadMuid(String threadMuid) {
        this.threadMuid = threadMuid;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
