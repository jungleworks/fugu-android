
package com.skeleton.mvp.model.innerMessage;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reaction {

    @SerializedName("users")
    @Expose
    private List<String> users = null;
    @SerializedName("full_names")
    @Expose
    private List<String> fullNames = null;
    @SerializedName("reaction")
    @Expose
    private String reaction;
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getFullNames() {
        return fullNames;
    }

    public void setFullNames(List<String> fullNames) {
        this.fullNames = fullNames;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

}
