package com.skeleton.mvp.model;

import java.io.Serializable;

public class Image implements Serializable {
    private String imageUrl;
    private String thumbnailUrl;
    private String transitionName;
    private String dateTime;
    private String channelName;

    public Image(String imageUrl, String thumbnailUrl, String transitionName, String dateTime, String channelName) {
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.transitionName = transitionName;
        this.dateTime = dateTime;
        this.channelName = channelName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTransitionName() {
        return transitionName;
    }

    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
