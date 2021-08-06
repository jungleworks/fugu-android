package com.skeleton.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhavya Rattan on 09/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguCreateConversationResponse {

    public Integer getStatusCode() {
        return statusCode;
    }

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

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

        @SerializedName("channel_id")
        @Expose
        private Long channelId;

        public String getlabel() {
            return label;
        }

        @SerializedName("label")
        @Expose
        private String label;

        public Long getChannelId() {
            return channelId;
        }
    }

}