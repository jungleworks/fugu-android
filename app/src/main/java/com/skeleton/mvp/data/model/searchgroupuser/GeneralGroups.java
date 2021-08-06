package com.skeleton.mvp.data.model.searchgroupuser;

/**
 * Created by rajatdhamija on 24/05/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralGroups {

    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("chat_type")
    @Expose
    private Integer chatType;
    @SerializedName("channel_image")
    @Expose
    private String channelImage;
    @SerializedName("label")
    @Expose
    private String label;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getChatType() {
        return chatType;
    }

    public void setChatType(Integer chatType) {
        this.chatType = chatType;
    }

    public String getChannelImage() {
        return channelImage;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
