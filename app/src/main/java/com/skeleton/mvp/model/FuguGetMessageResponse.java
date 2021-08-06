package com.skeleton.mvp.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.constant.FuguAppConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhavya Rattan on 09/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguGetMessageResponse {

    public FuguGetMessageResponse() {
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data = new Data();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        public Data() {
        }

        public void setMessages(ArrayList<Message> messages) {
            this.messages = messages;
        }

        @SerializedName("messages")
        @Expose
        private ArrayList<Message> messages = new ArrayList<>();
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("custom_label")
        @Expose
        private String customLabel;

        public void setLabel(String label) {
            this.label = label;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }


        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        @SerializedName("full_name")
        @Expose
        private String fullName;
        @SerializedName("muid")
        @Expose
        private String muid;
        @SerializedName("page_size")
        @Expose
        private int pageSize;
        @SerializedName("channel_id")
        @Expose
        private Long channelId;

        @SerializedName("email")
        @Expose
        private String email;

        public void setStatus(Integer status) {
            this.status = status;
        }

        @SerializedName("status")
        @Expose
        private Integer status;

        @SerializedName("user_image")
        @Expose

        private String userImage = "";

        @SerializedName("business_name")
        @Expose
        private String businessName;

        @SerializedName("user_id")
        @Expose
        private Long userId = -1L;

        @SerializedName("is_starred")
        @Expose
        private int isStarred = 0;

        @SerializedName("fugu_bot_tags")
        @Expose
        private List<FuguBotTag> fuguBotTags = new ArrayList<>();

        @SerializedName("user_type")
        @Expose
        private int userType = FuguAppConstant.UserType.BOT;

        @SerializedName("leave_type")
        @Expose
        private String leaveType;

        @SerializedName("only_admin_can_message")
        @Expose
        private boolean onlyAdminsCanMessage = false;

        @SerializedName("user_channel_role")
        @Expose
        private String userChannelRole="USER";

        public String getLeaveType() {
            return leaveType;
        }

        public void setLeaveType(String leaveType) {
            this.leaveType = leaveType;
        }
//        @SerializedName("status")
//        @Expose
//        private int status = 1;

        public ArrayList<Message> getMessages() {
            return messages;
        }

        public String getLabel() {
            return label;
        }

        public String getFullName() {
            return fullName;
        }

        public int getPageSize() {
            return pageSize;
        }


        public Long getChannelId() {
            return channelId;
        }

        public void setChannelId(Long channelId) {
            this.channelId = channelId;
        }

        public Integer getStatus() {
            return status;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

        public String getMuid() {
            return muid;
        }

        public void setMuid(String muid) {
            this.muid = muid;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setCustomLabel(String customLabel) {
            this.customLabel = customLabel;
        }

        public int getIsStarred() {
            return isStarred;
        }

        public void setIsStarred(int isStarred) {
            this.isStarred = isStarred;
        }

        public Long getUserId() {
            if (userId == null) {
                return -1L;
            }
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getCustomLabel() {
            return customLabel;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }


        public List<FuguBotTag> getFuguBotTags() {
            if (fuguBotTags == null) {
                return new ArrayList<>();
            }
            return fuguBotTags;
        }

        public void setFuguBotTags(List<FuguBotTag> fuguBotTags) {
            this.fuguBotTags = fuguBotTags;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public boolean isOnlyAdminsCanMessage() {
            return onlyAdminsCanMessage;
        }

        public void setOnlyAdminsCanMessage(boolean onlyAdminsCanMessage) {
            this.onlyAdminsCanMessage = onlyAdminsCanMessage;
        }

        public String getUserChannelRole() {
            if(!TextUtils.isEmpty(userChannelRole)) {
                return userChannelRole;
            }else {
                return "USER";
            }
        }

        public void setUserChannelRole(String userChannelRole) {
            this.userChannelRole = userChannelRole;
        }
    }


}