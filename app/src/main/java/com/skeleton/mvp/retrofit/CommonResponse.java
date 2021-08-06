package com.skeleton.mvp.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 */

public class CommonResponse {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Object data;

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return statusCode + " " + message + "\n" + data;
    }
}
