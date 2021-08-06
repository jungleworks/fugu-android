package com.skeleton.mvp.model.media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelInfo {

    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("owner_id")
    @Expose
    private Integer ownerId;
    @SerializedName("owner_name")
    @Expose
    private String ownerName;
    @SerializedName("chat_type")
    @Expose
    private Integer chatType;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getChatType() {
        return chatType;
    }

    public void setChatType(Integer chatType) {
        this.chatType = chatType;
    }

}
