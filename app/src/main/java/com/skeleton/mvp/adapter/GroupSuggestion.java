package com.skeleton.mvp.adapter;

public class GroupSuggestion {
    private Long channelId;
    private String channelName;

    public GroupSuggestion(Long channelId, String channelName) {
        this.channelId = channelId;
        this.channelName = channelName;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
