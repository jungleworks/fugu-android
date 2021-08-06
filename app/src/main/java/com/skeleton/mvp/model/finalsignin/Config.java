package com.skeleton.mvp.model.finalsignin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rajatdhamija on 16/02/18.
 */

public class Config {
    @SerializedName("any_user_can_invite")
    @Expose
    private String anyUserCanUpdate;

    public String getAnyUserCanUpdate() {
        return anyUserCanUpdate;
    }

    public void setAnyUserCanUpdate(String anyUserCanUpdate) {
        this.anyUserCanUpdate = anyUserCanUpdate;
    }
}
