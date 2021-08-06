
package com.skeleton.mvp.model.pushNotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Data {

    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("noti_msg")
    @Expose
    private String notiMsg;
    @SerializedName("chat_type")
    @Expose
    private Integer chatType;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("save_push")
    @Expose
    private Boolean savePush;
    @SerializedName("user_type")
    @Expose
    private Integer userType;
    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("hasCaption")
    @Expose
    private Boolean hasCaption;
    @SerializedName("message_id")
    @Expose
    private Integer messageId;
    @SerializedName("business_id")
    @Expose
    private Integer businessId;
    @SerializedName("new_message")
    @Expose
    private String newMessage;
    @SerializedName("message_type")
    @Expose
    private Integer messageType;
    @SerializedName("push_message")
    @Expose
    private String pushMessage;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("channel_image")
    @Expose
    private String channelImage;
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("last_sent_by_id")
    @Expose
    private Integer lastSentById;
    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("is_thread_message")
    @Expose
    private Boolean isThreadMessage;
    @SerializedName("notification_type")
    @Expose
    private Integer notificationType;
    @SerializedName("user_thumbnail_image")
    @Expose
    private String userThumbnailImage;
    @SerializedName("last_sent_by_full_name")
    @Expose
    private String lastSentByFullName;
    @SerializedName("last_sent_by_user_type")
    @Expose
    private Integer lastSentByUserType;

    @SerializedName("push_type")
    @Expose
    private int push_type;

    @SerializedName("showpush")
    @Expose
    private int showpush = 1;

    public int getShowpush() {
        return showpush;
    }

    public void setShowpush(int showpush) {
        this.showpush = showpush;
    }


    @SerializedName("tagged_users")
    @Expose
    private List<Integer> taggedUsers = new ArrayList<>();
    @SerializedName("title")
    @Expose
    private String title;


    public List<Integer> getTaggedUsers() {
        return taggedUsers;
    }

    public void setTaggedUsers(List<Integer> taggedUsers) {
        this.taggedUsers = taggedUsers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getThreadMessage() {
        return isThreadMessage;
    }

    public void setThreadMessage(Boolean threadMessage) {
        isThreadMessage = threadMessage;
    }

    public int getPush_type() {
        return push_type;
    }

    public void setPush_type(int push_type) {
        this.push_type = push_type;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNotiMsg() {
        return notiMsg;
    }

    public void setNotiMsg(String notiMsg) {
        this.notiMsg = notiMsg;
    }

    public Integer getChatType() {
        return chatType;
    }

    public void setChatType(Integer chatType) {
        this.chatType = chatType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getSavePush() {
        return savePush;
    }

    public void setSavePush(Boolean savePush) {
        this.savePush = savePush;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Boolean getHasCaption() {
        return hasCaption;
    }

    public void setHasCaption(Boolean hasCaption) {
        this.hasCaption = hasCaption;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getChannelImage() {
        return channelImage;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public Integer getLastSentById() {
        return lastSentById;
    }

    public void setLastSentById(Integer lastSentById) {
        this.lastSentById = lastSentById;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public Boolean getIsThreadMessage() {
        return isThreadMessage;
    }

    public void setIsThreadMessage(Boolean isThreadMessage) {
        this.isThreadMessage = isThreadMessage;
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public String getUserThumbnailImage() {
        return userThumbnailImage;
    }

    public void setUserThumbnailImage(String userThumbnailImage) {
        this.userThumbnailImage = userThumbnailImage;
    }

    public String getLastSentByFullName() {
        return lastSentByFullName;
    }

    public void setLastSentByFullName(String lastSentByFullName) {
        this.lastSentByFullName = lastSentByFullName;
    }

    public Integer getLastSentByUserType() {
        return lastSentByUserType;
    }

    public void setLastSentByUserType(Integer lastSentByUserType) {
        this.lastSentByUserType = lastSentByUserType;
    }

}
