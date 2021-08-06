
package com.skeleton.mvp.data.model.allgroups;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("joined_channels")
    @Expose
    private List<JoinedChannel> joinedChannels = new ArrayList<>();
    @SerializedName("open_channels")
    @Expose
    private List<OpenChannel> openChannels = new ArrayList<>();

    public List<JoinedChannel> getJoinedChannels() {
        return joinedChannels;
    }


    @SerializedName("o2o_channels")
    @Expose
    private List<O2oChannel> o2oChannels = new ArrayList<>();
    public void setJoinedChannels(List<JoinedChannel> joinedChannels) {
        this.joinedChannels = joinedChannels;
    }

    public List<OpenChannel> getOpenChannels() {
        return openChannels;
    }

    public void setOpenChannels(List<OpenChannel> openChannels) {
        this.openChannels = openChannels;
    }

    public List<O2oChannel> getO2oChannels() {
        return o2oChannels;
    }

    public void setO2oChannels(List<O2oChannel> o2oChannels) {
        this.o2oChannels = o2oChannels;
    }

}
