
package com.skeleton.mvp.data.model.onetoone;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("channel_image")
    @Expose
    private String channelImage;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getChannelImage() {
        return channelImage;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

}
