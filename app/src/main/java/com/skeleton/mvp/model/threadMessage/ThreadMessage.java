
package com.skeleton.mvp.model.threadMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThreadMessage {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("muid")
    @Expose
    private String muid;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

}
