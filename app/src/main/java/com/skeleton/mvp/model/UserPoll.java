package com.skeleton.mvp.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPoll {

    @SerializedName("puid")
    @Expose
    private String puid;
    @SerializedName("user_ids")
    @Expose
    private List<Integer> userIds = null;
    @SerializedName("full_name")
    @Expose
    private List<String> fullName = null;
    @SerializedName("user_image")
    @Expose
    private List<String> userImage = null;
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("poll_option")
    @Expose
    private String poll_option = "";

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public List<String> getFullName() {
        return fullName;
    }

    public void setFullName(List<String> fullName) {
        this.fullName = fullName;
    }

    public List<String> getUserImage() {
        return userImage;
    }

    public void setUserImage(List<String> userImage) {
        this.userImage = userImage;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getPoll_option() {
        return poll_option;
    }

    public void setPoll_option(String name) {
        this.poll_option = poll_option;
    }
}
