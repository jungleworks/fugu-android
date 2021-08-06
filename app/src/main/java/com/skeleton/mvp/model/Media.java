package com.skeleton.mvp.model;

import com.skeleton.mvp.model.media.Message;

import java.io.Serializable;

/**
 * Created by rajatdhamija on 11/01/18.
 */

public class Media implements Serializable {
    private String imageUrl;
    private String thumbnailUrl;
    private int messageType;
    private String fileurl;
    private String fileName;
    private String localPath;
    private Message message;
    private String muid;
    private int isThreadMessage;
    private String createdAt;
    private Long messageId;
    private Long threadMessageId;




    public Media(String imageUrl, String thumbnailUrl, int messageType, String fileurl, String fileName, String localPath,
                 Message message, String muid, int isThreadMessage, String createdAt, Long messageId, Long threadMesageId) {
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.messageType = messageType;
        this.fileurl = fileurl;
        this.fileName = fileName;
        this.localPath=localPath;
        this.message = message;
        this.muid=muid;
        this.createdAt = createdAt;
        this.isThreadMessage = isThreadMessage;
        this.messageId = messageId;
        this.threadMessageId = threadMesageId;
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

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public int getIsThreadMessage() {
        return isThreadMessage;
    }

    public void setIsThreadMessage(int isThreadMessage) {
        this.isThreadMessage = isThreadMessage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getThreadMessageId() {
        return threadMessageId;
    }

    public void setThreadMessageId(Long threadMessageId) {
        this.threadMessageId = threadMessageId;
    }
}
