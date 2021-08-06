
package com.skeleton.mvp.model.media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatMedium {

    @SerializedName("message")
    @Expose
    private Message message;
    @SerializedName("message_type")
    @Expose
    private Integer messageType;
    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("is_thread_message")
    @Expose
    private int isThreadMessage;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("message_id")
    @Expose
    private Long messageId = -1L;
    @SerializedName("thread_message_id")
    @Expose
    private Long threadMessageId = -1L;



    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }
    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
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

    public int getIsThreadMessage() {
        return isThreadMessage;
    }

    public void setIsThreadMessage(int isThreadMessage) {
        this.isThreadMessage = isThreadMessage;
    }
}
