package com.skeleton.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.data.model.fcCommon.AppUpdateConfig;

import java.util.List;

/**
 * Created by Bhavya Rattan on 09/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguGetConversationsResponse {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

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

        @SerializedName("conversation_list")
        @Expose
        private List<FuguConversation> conversationList = null;

        @SerializedName("page_size")
        @Expose
        private int pageSize;

        @SerializedName("unread_notification_count")
        @Expose
        private int unreadNotificationCount = 0;
        @SerializedName("app_update_config")
        @Expose
        private AppUpdateConfig appUpdateConfig;

        public int getPageSize() {
            return pageSize;
        }

        public List<FuguConversation> getConversationList() {
            return conversationList;
        }

        public AppUpdateConfig getAppUpdateConfig() {
            return appUpdateConfig;
        }

        public int getUnreadNotificationCount() {
            return unreadNotificationCount;
        }
    }

}
