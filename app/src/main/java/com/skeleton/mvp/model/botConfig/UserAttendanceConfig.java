
package com.skeleton.mvp.model.botConfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAttendanceConfig {

    @SerializedName("punch_in_permission")
    @Expose
    private String punchInPermission;
    @SerializedName("punch_out_permission")
    @Expose
    private String punchOutPermission;

    public String getPunchInPermission() {
        return punchInPermission;
    }

    public void setPunchInPermission(String punchInPermission) {
        this.punchInPermission = punchInPermission;
    }

    public String getPunchOutPermission() {
        return punchOutPermission;
    }

    public void setPunchOutPermission(String punchOutPermission) {
        this.punchOutPermission = punchOutPermission;
    }

}
