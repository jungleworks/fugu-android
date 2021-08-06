
package com.skeleton.mvp.data.model.searchgroupuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.data.model.creategroup.MembersInfo;

import java.util.ArrayList;
import java.util.List;

public class OpenGroup {

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
    @SerializedName("members_info")
    @Expose
    private List<MembersInfo> membersInfo = new ArrayList<>();
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

    public List<MembersInfo> getMembersInfo() {
        return membersInfo;
    }

    public void setMembersInfo(List<MembersInfo> membersInfo) {
        this.membersInfo = membersInfo;
    }
}
