
package com.skeleton.mvp.model.media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.data.model.creategroup.MembersInfo;

import java.util.ArrayList;
import java.util.List;

public class Data {

    @SerializedName("chat_members")
    @Expose
    private List<ChatMember> chatMembers = null;
    @SerializedName("chat_media")
    @Expose
    private List<ChatMedium> chatMedia = null;
    @SerializedName("page_size")
    @Expose
    private Integer pageSize;
    @SerializedName("channel_image")
    @Expose
    private ChannelImageUrl channelImageUrl;
    @SerializedName("members_info")
    @Expose
    private List<MembersInfo> membersInfo = new ArrayList<>();


    @SerializedName("channel_info")
    @Expose
    private ChannelInfo channelInfo;

    @SerializedName("user_count")
    @Expose
    private Long userCount = 0L;

    @SerializedName("user_page_size")
    @Expose
    private int userPageSize=0;


    public List<ChatMember> getChatMembers() {
        return chatMembers;
    }

    public void setChatMembers(List<ChatMember> chatMembers) {
        this.chatMembers = chatMembers;
    }

    public List<ChatMedium> getChatMedia() {
        return chatMedia;
    }

    public void setChatMedia(List<ChatMedium> chatMedia) {
        this.chatMedia = chatMedia;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public ChannelImageUrl getChannelImageUrl() {
        return channelImageUrl;
    }

    public void setChannelImageUrl(ChannelImageUrl channelImageUrl) {
        this.channelImageUrl = channelImageUrl;
    }

    public List<MembersInfo> getMembersInfo() {
        return membersInfo;
    }

    public void setMembersInfo(List<MembersInfo> membersInfo) {
        this.membersInfo = membersInfo;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public int getUserPageSize() {
        return userPageSize;
    }

    public void setUserPageSize(int userPageSize) {
        this.userPageSize = userPageSize;
    }
}


