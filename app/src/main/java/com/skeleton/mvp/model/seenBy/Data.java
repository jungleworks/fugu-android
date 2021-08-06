
package com.skeleton.mvp.model.seenBy;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("page_size")
    @Expose
    private Integer pageSize = 0;
    @SerializedName("message_seen_by")
    @Expose
    private List<MessageSeenBy> messageSeenBy = new ArrayList<>();
    @SerializedName("customMessage")
    @Expose
    private String message = "";
    @SerializedName("count")
    @Expose
    private int seenByCount = 0;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<MessageSeenBy> getMessageSeenBy() {
        return messageSeenBy;
    }

    public void setMessageSeenBy(List<MessageSeenBy> messageSeenBy) {
        this.messageSeenBy = messageSeenBy;
    }

    public int getSeenByCount() {
        return seenByCount;
    }

    public void setSeenByCount(int seenByCount) {
        this.seenByCount = seenByCount;
    }
}
