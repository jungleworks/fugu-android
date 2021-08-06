
package com.skeleton.mvp.model.starredmessage;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("starred_muids")
    @Expose
    private List<String> starredMuids = new ArrayList<>();

    @SerializedName("starred_messages")
    @Expose
    private List<StarredMessage> starredMessages = new ArrayList<>();
    @SerializedName("page_size")
    @Expose
    private Integer pageSize = 0;

    public List<StarredMessage> getStarredMessages() {
        return starredMessages;
    }

    public void setStarredMessages(List<StarredMessage> starredMessages) {
        this.starredMessages = starredMessages;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getStarredMuids() {
        return starredMuids;
    }

    public void setStarredMuids(List<String> starredMuids) {
        this.starredMuids = starredMuids;
    }
}
