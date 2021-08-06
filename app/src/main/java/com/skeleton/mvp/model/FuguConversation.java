package com.skeleton.mvp.model;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.model.creategroup.MembersInfo;
import com.skeleton.mvp.datastructure.ChannelStatus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by Bhavya Rattan on 10/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguConversation {

    @SerializedName("channel_id")
    @Expose
    private Long channelId = -1l;
    @SerializedName("label_id")
    @Expose
    private Long labelId = -1l;
    @SerializedName("user_id")
    @Expose
    private Long userId = -1l;
    @SerializedName("en_user_id")
    @Expose
    private String enUserId = "";
    @SerializedName("last_sent_by_full_name")
    @Expose
    private String last_sent_by_full_name = "";

    @SerializedName("call_type")
    @Expose
    private String callType = "VIDEO";

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @SerializedName("message")
    @Expose
    private String message = "";
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("status")
    @Expose
    private int status = 1;
    @SerializedName("channel_status")
    @Expose
    private int channelStatus = ChannelStatus.OPEN.getOrdinal();
    @SerializedName("channel_image")
    @Expose
    private String channelImage = "";

    @SerializedName("channel_image_url")
    @Expose
    private String url = "";

    @SerializedName("channel_thumbnail_url")
    @Expose
    private String thumbnailUrl = "";

    @SerializedName("isOpenChat")
    @Expose
    private boolean isOpenChat;
    @SerializedName("tags")
    @Expose
    private JsonArray tags = null;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("message_type")
    @Expose
    private int message_type;
    @SerializedName("chat_type")
    @Expose
    private int chat_type;
    @SerializedName("last_sent_by_id")
    @Expose
    private Long last_sent_by_id = -1L;
    @SerializedName("isGroup")
    @Expose
    private Boolean isGroup;
    @SerializedName("isJoined")
    @Expose
    private Boolean isJoined;

    @SerializedName("message_state")
    @Expose
    private int messageState = 1;

    private int isTimeSet = 0;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @SerializedName("userName")
    @Expose
    private String userName = "Anonymous";

    @SerializedName("last_message_status")
    @Expose
    private int last_message_status = 2;

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @SerializedName("defaultMessage")
    @Expose
    private String defaultMessage = "";

    @SerializedName("default_message")
    @Expose
    private String default_message = "";

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    @SerializedName("businessName")
    @Expose
    private String businessName = "";

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    @SerializedName("unread_count")
    @Expose
    private int unreadCount = 0;

    @SerializedName("muid")
    @Expose
    private String muid;

    public boolean isStartChannelsActivity() {
        return startChannelsActivity;
    }

    public void setStartChannelsActivity(boolean startChannelsActivity) {
        this.startChannelsActivity = startChannelsActivity;
    }

    @SerializedName("notification")
    @Expose
    public String notification = "";

    @SerializedName("startChannelsActivity")
    @Expose
    private boolean startChannelsActivity = false;
    @SerializedName("members_info")
    @Expose
    private ArrayList<MembersInfo> membersInfo = new ArrayList<>();

    @SerializedName("custom_label")
    @Expose
    private String customLabel = "";
    @SerializedName("is_pinned")
    @Expose
    private Integer isPinned = 0;

    @SerializedName("other_user_type")
    @Expose
    private Integer otherUserType = FuguAppConstant.UserType.CUSTOMER;

    public FuguConversation(@Nullable Long channelId,
                            @Nullable Long userId,
                            @Nullable String last_sent_by_full_name,
                            @Nullable String dateTime,
                            @Nullable String message,
                            @Nullable String label,
                            @Nullable String thumbnailUrl,
                            int message_type,
                            int chat_type,
                            @Nullable Long last_sent_by_id,
                            int messageState,
                            int unreadCount,
                            @Nullable String muid,
                            @Nullable String notifications,
                            @NotNull ArrayList<MembersInfo> membersInfo,
                            @Nullable String customLabel,
                            int last_message_status,
                            @Nullable String callType,
                            @Nullable Integer otherUserType) {
        this.channelId = channelId;
        this.userId = userId;
        this.last_sent_by_full_name = last_sent_by_full_name;
        this.dateTime = dateTime;
        if (message == null) {
            this.message = "";
        } else {
            this.message = message;
        }
        this.label = label;
        this.thumbnailUrl = thumbnailUrl;
        this.message_type = message_type;
        this.chat_type = chat_type;
        this.last_sent_by_id = last_sent_by_id;
        this.messageState = messageState;
        this.unreadCount = unreadCount;
        this.muid = muid;
        this.notification = notifications;
        this.membersInfo = membersInfo;
        this.customLabel = customLabel;
        this.last_message_status = last_message_status;
        this.callType = callType;
        this.otherUserType = otherUserType;
    }

    private Integer channelIndex;

    public Integer getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Integer isPinned) {
        this.isPinned = isPinned;
    }

    public String getChannelImage() {
        return channelImage;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getStatus() {
        return status;
    }

    public String getLabel() {
        return label != null ? label : "";
    }

    public boolean isOpenChat() {
        return isOpenChat;
    }

    public void setOpenChat(boolean openChat) {
        isOpenChat = openChat;
    }

    public FuguConversation(Long channelId, String message, String dateTime, String last_sent_by_full_name) {
        this.channelId = channelId;
        this.message = message;
        this.dateTime = dateTime;
        this.last_sent_by_full_name = last_sent_by_full_name;
    }

    public FuguConversation() {
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setLabel(String label) {
        this.label = label != null ? label : "";
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

    public JsonArray getTags() {
        return tags;
    }

    public void setTags(JsonArray tags) {
        this.tags = tags;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(int channelStatus) {
        this.channelStatus = channelStatus;
    }

    public int getIsTimeSet() {
        return isTimeSet;
    }

    public void setIsTimeSet(int isTimeSet) {
        this.isTimeSet = isTimeSet;
    }

    public String getLast_sent_by_full_name() {
        return last_sent_by_full_name;
    }

    public void setLast_sent_by_full_name(String last_sent_by_full_name) {
        this.last_sent_by_full_name = last_sent_by_full_name;
    }

    public Long getLast_sent_by_id() {
        return last_sent_by_id;
    }

    public void setLast_sent_by_id(Long last_sent_by_id) {
        this.last_sent_by_id = last_sent_by_id;
    }

    public int getLast_message_status() {
        return last_message_status;
    }

    public String getDefault_message() {
        return default_message;
    }

    public void setDefault_message(String default_message) {
        this.default_message = default_message;
    }

    public void setLast_message_status(int last_message_status) {
        this.last_message_status = last_message_status;

    }

    public String getEnUserId() {
        return enUserId;
    }

    public void setEnUserId(String enUserId) {
        this.enUserId = enUserId;
    }

    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }

    public int getChat_type() {
        return chat_type;
    }

    public void setChat_type(int chat_type) {
        this.chat_type = chat_type;
    }


    public Integer getChannelIndex() {
        return channelIndex;
    }

    public void setChannelIndex(Integer channelIndex) {
        this.channelIndex = channelIndex;
    }

    public boolean isGroup() {
        if (isGroup == null) {
            isGroup = false;
        }
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public Boolean getJoined() {
        if (isJoined == null) {
            isJoined = true;
        }
        return isJoined;
    }

    public void setJoined(Boolean joined) {
        isJoined = joined;
    }

    public String getNotifications() {
        return notification;
    }

    public void setNotifications(String notification) {
        this.notification = notification;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public ArrayList<MembersInfo> getMembersInfo() {
        return membersInfo;
    }

    public void setMembersInfo(ArrayList<MembersInfo> membersInfo) {
        this.membersInfo = membersInfo;
    }

    public String getCustomLabel() {
        return customLabel;
    }

    public void setCustomLabel(String customLabel) {
        this.customLabel = customLabel;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public Integer getOtherUserType() {
        return otherUserType;
    }

    public void setOtherUserType(Integer otherUserType) {
        this.otherUserType = otherUserType;
    }

}
