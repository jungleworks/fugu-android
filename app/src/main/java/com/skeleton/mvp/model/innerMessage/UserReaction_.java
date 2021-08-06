
package com.skeleton.mvp.model.innerMessage;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserReaction_ {

    @SerializedName("reaction")
    @Expose
    private List<Reaction> reaction = null;
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
