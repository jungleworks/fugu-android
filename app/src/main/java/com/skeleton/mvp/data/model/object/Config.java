
package com.skeleton.mvp.data.model.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("any_user_can_invite")
    @Expose
    private String anyUserCanInvite;

    public String getAnyUserCanInvite() {
        return anyUserCanInvite;
    }

    public void setAnyUserCanInvite(String anyUserCanInvite) {
        this.anyUserCanInvite = anyUserCanInvite;
    }

}
