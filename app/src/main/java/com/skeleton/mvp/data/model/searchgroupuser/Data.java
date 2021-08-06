
package com.skeleton.mvp.data.model.searchgroupuser;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("users")
    @Expose
    private List<User> users = null;
    @SerializedName("channels")
    @Expose
    private List<Channel> channels = null;
    @SerializedName("open_groups")
    @Expose
    private List<OpenGroup> openGroups = null;

    @SerializedName("bot")
    @Expose
    private List<Bot> bot = null;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<OpenGroup> getOpenGroups() {
        return openGroups;
    }

    public void setOpenGroups(List<OpenGroup> openGroups) {
        this.openGroups = openGroups;
    }

    public List<Bot> getBot() {
        return bot;
    }

    public void setBot(List<Bot> bot) {
        this.bot = bot;
    }
}
