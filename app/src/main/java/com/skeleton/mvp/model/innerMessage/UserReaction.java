
package com.skeleton.mvp.model.innerMessage;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserReaction {

    @SerializedName("reaction")
    @Expose
    private List<Object> reaction = null;
    @SerializedName("total_reaction")
    @Expose
    private Integer totalReaction;

    public List<Object> getReaction() {
        return reaction;
    }

    public void setReaction(List<Object> reaction) {
        this.reaction = reaction;
    }

    public Integer getTotalReaction() {
        return totalReaction;
    }

    public void setTotalReaction(Integer totalReaction) {
        this.totalReaction = totalReaction;
    }

}
