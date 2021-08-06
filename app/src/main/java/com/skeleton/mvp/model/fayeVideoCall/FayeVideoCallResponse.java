
package com.skeleton.mvp.model.fayeVideoCall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FayeVideoCallResponse {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("data")
    @Expose
    private Message data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Message getMessage() {
        return data;
    }

    public void setMessage(Message data) {
        this.data = data;
    }

}
