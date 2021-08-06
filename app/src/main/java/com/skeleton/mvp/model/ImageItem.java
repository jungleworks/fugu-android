package com.skeleton.mvp.model;

import java.io.Serializable;

public class ImageItem implements Serializable {

    private String absolutepath;
    private String muid;
    private String transitionName = "";
    private String datetime;
    private String messageIndex;
    private Message message;

    public ImageItem(String absolutepath, String muid, String transitionName, String datetime, Message message) {
        this.absolutepath = absolutepath;
        this.muid = muid;
        this.transitionName = transitionName;
        this.datetime = datetime;
        this.message = message;

        if (transitionName == null) {
            this.transitionName = "";
        }
    }

    public String getAbsolutepath() {
        return absolutepath;
    }

    public void setAbsolutepath(String absolutepath) {
        this.absolutepath = absolutepath;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getTransitionName() {
        return transitionName;
    }

    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(String messageIndex) {
        this.messageIndex = messageIndex;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
