package com.skeleton.mvp.data.model;

/**
 * Created by rajatdhamija
 * 23/01/18.
 */

public class AllGroup {
    private Long channelId;
    private String groupName;
    private int members;
    private boolean isJoined;
    private int chatType;

    public AllGroup(Long channelId, String groupName, boolean isJoined, int chatType) {
        this.channelId = channelId;
        this.groupName = groupName;
        this.members = members;
        this.isJoined = isJoined;
        this.chatType = chatType;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getGroupName() {
        if (groupName != null) return groupName;
        else return "";
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }
}
