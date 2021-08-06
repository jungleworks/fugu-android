
package com.skeleton.mvp.model.threadMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThreadedMessageInfo {

    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("thread_message")
    @Expose
    private ThreadMessage threadMessage;
    @SerializedName("total_message_count")
    @Expose
    private Integer count;
    @SerializedName("thread_message_type")
    @Expose
    private Integer threadMessageType;

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public ThreadMessage getThreadMessage() {
        return threadMessage;
    }

    public void setThreadMessage(ThreadMessage threadMessage) {
        this.threadMessage = threadMessage;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getThreadMessageType() {
        return threadMessageType;
    }

    public void setThreadMessageType(Integer threadMessageType) {
        this.threadMessageType = threadMessageType;
    }

}
