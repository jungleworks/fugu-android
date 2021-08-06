
package com.skeleton.mvp.model.editInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.model.media.ChannelImageUrl;

public class Data {

    @SerializedName("channel_image")
    @Expose
    private ChannelImageUrl channelImageUrl;

    public ChannelImageUrl getChannelImageUrl() {
        return channelImageUrl;
    }

    public void setChannelImageUrl(ChannelImageUrl channelImageUrl) {
        this.channelImageUrl = channelImageUrl;
    }
    //
//    @SerializedName("user_id")
//    @Expose
//    private Integer userId;
//    @SerializedName("business_id")
//    @Expose
//    private Integer businessId;
//    @SerializedName("user_properties")
//    @Expose
//    private UserProperties userProperties;
//
//    public Integer getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Integer userId) {
//        this.userId = userId;
//    }
//
//    public Integer getBusinessId() {
//        return businessId;
//    }
//
//    public void setBusinessId(Integer businessId) {
//        this.businessId = businessId;
//    }
//
//    public UserProperties getUserProperties() {
//        return userProperties;
//    }
//
//    public void setUserProperties(UserProperties userProperties) {
//        this.userProperties = userProperties;
//    }

}
