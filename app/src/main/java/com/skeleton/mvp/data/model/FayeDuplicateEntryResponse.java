package com.skeleton.mvp.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FayeDuplicateEntryResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("customMessage")
    @Expose
    private String customMessage;

    @SerializedName("type")
    @Expose
    private String type;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
