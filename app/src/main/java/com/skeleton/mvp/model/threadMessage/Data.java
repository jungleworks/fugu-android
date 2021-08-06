
package com.skeleton.mvp.model.threadMessage;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("threaded_message_info")
    @Expose
    private List<ThreadedMessageInfo> threadedMessageInfo = null;

    public List<ThreadedMessageInfo> getThreadedMessageInfo() {
        return threadedMessageInfo;
    }

    public void setThreadedMessageInfo(List<ThreadedMessageInfo> threadedMessageInfo) {
        this.threadedMessageInfo = threadedMessageInfo;
    }

}
