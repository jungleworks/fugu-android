
package com.skeleton.mvp.model.homeNotifications;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("notification_id")
    @Expose
    private Integer notificationId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("action_by_user_id")
    @Expose
    private Integer actionByUserId;
    @SerializedName("action_by_user_image")
    @Expose
    private String actionByUserImage;
    @SerializedName("action_by_user_name")
    @Expose
    private String actionByUserName;
    @SerializedName("channel_image")
    @Expose
    private String channelImage;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("chat_type")
    @Expose
    private Integer chatType;
    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("thread_muid")
    @Expose
    private String threadMuid;
    @SerializedName("notification_title")
    @Expose
    private String notificationTitle;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("business_image")
    @Expose
    private Object businessImage;
    @SerializedName("is_tagged")
    @Expose
    private Integer isTagged;
    @SerializedName("notification_type")
    @Expose
    private Integer notificationType;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("read_at")
    @Expose
    private String readAt = "";
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public Integer getActionByUserId() {
        return actionByUserId;
    }

    public void setActionByUserId(Integer actionByUserId) {
        this.actionByUserId = actionByUserId;
    }

    public String getActionByUserImage() {
        return actionByUserImage;
    }

    public void setActionByUserImage(String actionByUserImage) {
        this.actionByUserImage = actionByUserImage;
    }

    public String getActionByUserName() {
        return actionByUserName;
    }

    public void setActionByUserName(String actionByUserName) {
        this.actionByUserName = actionByUserName;
    }

    public String getChannelImage() {
        return channelImage;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Integer getChatType() {
        return chatType;
    }

    public void setChatType(Integer chatType) {
        this.chatType = chatType;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getThreadMuid() {
        return threadMuid;
    }

    public void setThreadMuid(String threadMuid) {
        this.threadMuid = threadMuid;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public Object getBusinessImage() {
        return businessImage;
    }

    public void setBusinessImage(Object businessImage) {
        this.businessImage = businessImage;
    }

    public Integer getIsTagged() {
        return isTagged;
    }

    public void setIsTagged(Integer isTagged) {
        this.isTagged = isTagged;
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReadAt() {
        return readAt;
    }

    public void setReadAt(String readAt) {
        this.readAt = readAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
