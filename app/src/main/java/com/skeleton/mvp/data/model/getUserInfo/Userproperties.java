package com.skeleton.mvp.data.model.getUserInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rajatdhamija on 25/04/18.
 */

public class Userproperties {

    @SerializedName("hide_email")
    @Expose
    private Boolean hideEmail;
    @SerializedName("hide_contact_number")
    @Expose
    private Boolean hideContactNumber;

    public Boolean getHideEmail() {
        return hideEmail;
    }

    public void setHideEmail(Boolean hideEmail) {
        this.hideEmail = hideEmail;
    }

    public Boolean getHideContactNumber() {
        return hideContactNumber;
    }

    public void setHideContactNumber(Boolean hideContactNumber) {
        this.hideContactNumber = hideContactNumber;
    }

}
