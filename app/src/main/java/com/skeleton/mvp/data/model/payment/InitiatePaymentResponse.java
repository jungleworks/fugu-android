package com.skeleton.mvp.data.model.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InitiatePaymentResponse {
    @SerializedName("statusCode")
    @Expose
    private long statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(long value) {
        this.statusCode = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data value) {
        this.data = value;
    }

    public class Data {
        @SerializedName("redirect_url")
        @Expose
        private String redirectURL;

        public String getRedirectURL() {
            return redirectURL;
        }

        public void setRedirectURL(String value) {
            this.redirectURL = value;
        }
    }

}