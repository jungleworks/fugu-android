
package com.skeleton.mvp.model.alreadyInvited;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("invited_users")
    @Expose
    private List<InvitedUser> invitedUsers = null;

    public List<InvitedUser> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<InvitedUser> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

}
