
package com.skeleton.mvp.data.model.creategroup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;
    @SerializedName("members_info")
    @Expose
    private List<MembersInfo> membersInfo = new ArrayList<>();

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public List<MembersInfo> getMembersInfo() {
        return membersInfo;
    }

    public void setMembersInfo(List<MembersInfo> membersInfo) {
        this.membersInfo = membersInfo;
    }
}
