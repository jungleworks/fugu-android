package com.skeleton.mvp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PollOption implements Serializable {

    @SerializedName("puid")
    @Expose
    private String puid;
    @SerializedName("users")
    @Expose
    private List<User> users = null;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("poll_count")
    @Expose
    private Integer pollCount;
    @SerializedName("vote_map")
    @Expose
    private HashMap<Long, User> voteMap = new HashMap<>();
    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getPollCount() {
        return pollCount;
    }

    public void setPollCount(Integer pollCount) {
        this.pollCount = pollCount;
    }
    public HashMap<Long, User> getVoteMap() {
        return voteMap;
    }

    public void setVoteMap(HashMap<Long, User> voteMap) {
        this.voteMap = voteMap;
    }
}
