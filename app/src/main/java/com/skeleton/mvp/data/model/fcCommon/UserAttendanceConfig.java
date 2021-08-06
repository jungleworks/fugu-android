package com.skeleton.mvp.data.model.fcCommon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.constant.FuguAppConstant;

public class UserAttendanceConfig {


    @SerializedName("punch_in_permission")
    @Expose
    private String attendanceIn = FuguAppConstant.AttendanceAuthenticationLevel.NONE.toString();

    @SerializedName("punch_out_permission")
    @Expose
    private String attendanceOut = FuguAppConstant.AttendanceAuthenticationLevel.NONE.toString();

    public UserAttendanceConfig() {
    }

    public UserAttendanceConfig(String attendanceIn, String attendanceOut) {
        this.attendanceIn = attendanceIn;
        this.attendanceOut = attendanceOut;
    }

    public String getAttendanceIn() {
        return attendanceIn;
    }

    public void setAttendanceIn(String attendanceIn) {
        this.attendanceIn = attendanceIn;
    }

    public String getAttendanceOut() {
        return attendanceOut;
    }

    public void setAttendanceOut(String attendanceOut) {
        this.attendanceOut = attendanceOut;
    }
}
