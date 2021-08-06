
package com.skeleton.mvp.model.searchgroupuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Channel {

    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("channel_image")
    @Expose
    private String channelImage;
    @SerializedName("members")
    @Expose
    private String members;

    public Integer getChannelId() {
        return channelId;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelImage() {
        return channelImage;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String message) {
        this.members = message;
    }
}
