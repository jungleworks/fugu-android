package com.skeleton.mvp.model.inviteContacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelImage {

    @SerializedName("channel_image_url")
    @Expose
    private String channelImageUrl;
    @SerializedName("channel_thumbnail_url")
    @Expose
    private String channelThumbnailUrl;

    public String getChannelImageUrl() {
        return channelImageUrl;
    }

    public void setChannelImageUrl(String channelImageUrl) {
        this.channelImageUrl = channelImageUrl;
    }

    public String getChannelThumbnailUrl() {
        return channelThumbnailUrl;
    }

    public void setChannelThumbnailUrl(String channelThumbnailUrl) {
        this.channelThumbnailUrl = channelThumbnailUrl;
    }

}
