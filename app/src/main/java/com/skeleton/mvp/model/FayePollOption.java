package com.skeleton.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FayePollOption {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("puid")
    @Expose
    private String puid;
    @SerializedName("poll_count")
    @Expose
    private Integer pollCount;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public Integer getPollCount() {
        return pollCount;
    }

    public void setPollCount(Integer pollCount) {
        this.pollCount = pollCount;
    }

}