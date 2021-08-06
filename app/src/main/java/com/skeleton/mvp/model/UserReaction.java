package com.skeleton.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajatdhamija on 10/04/18.
 */

public class UserReaction implements Serializable {

    @SerializedName("reaction")
    @Expose
    private List<Reaction> reaction = new ArrayList<>();
    @SerializedName("total_reaction")
    @Expose
    private Integer totalReaction;

    public List<Reaction> getReaction() {
        return reaction;
    }

    public void setReaction(List<Reaction> reaction) {
        this.reaction = reaction;
    }

    public Integer getTotalReaction() {
        return totalReaction;
    }

    public void setTotalReaction(Integer totalReaction) {
        this.totalReaction = totalReaction;
    }

}