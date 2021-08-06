
package com.skeleton.mvp.model.channelResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.data.model.creategroup.MembersInfo;

import java.util.ArrayList;
import java.util.List;

public class Data {

    @SerializedName("label")
    @Expose
    private String label = "";
    @SerializedName("custom_label")
    @Expose
    private String customLabel = "";
    @SerializedName("unread_count")
    @Expose
    private Integer unreadCount;
    @SerializedName("channel_thumbnail_url")
    @Expose
    private String channelThumbnailUrl = "";

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getUnreadCount() {
        return unreadCount = 1;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    @SerializedName("members_info")
    @Expose
    private List<MembersInfo> membersInfo = new ArrayList<>();

    public List<MembersInfo> getMembersInfo() {
        return membersInfo;
    }

    public void setMembersInfo(List<MembersInfo> membersInfo) {
        this.membersInfo = membersInfo;
    }

    public String getCustomLabel() {
        return customLabel;
    }

    public void setCustomLabel(String customLabel) {
        this.customLabel = customLabel;
    }

    public String getChannelThumbnailUrl() {
        return channelThumbnailUrl;
    }

    public void setChannelThumbnailUrl(String channelThumbnailUrl) {
        this.channelThumbnailUrl = channelThumbnailUrl;
    }
}
