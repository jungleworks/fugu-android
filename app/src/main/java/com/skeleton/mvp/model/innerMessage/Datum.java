
package com.skeleton.mvp.model.innerMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

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
    private ThreadMessage threadMessage;
    @SerializedName("muid")
    @Expose
    private String threadMuid;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("message_type")
    @Expose
    private Integer threadMessageType;

    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("url")
    @Expose
    private String url;

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

    public ThreadMessage getThreadMessage() {
        return threadMessage;
    }

    public void setThreadMessage(ThreadMessage threadMessage) {
        this.threadMessage = threadMessage;
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

    public Integer getThreadMessageType() {
        return threadMessageType;
    }

    public void setThreadMessageType(Integer threadMessageType) {
        this.threadMessageType = threadMessageType;
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
}
