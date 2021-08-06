
package com.skeleton.mvp.model.botConfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("user_attendance_config")
    @Expose
    private UserAttendanceConfig userAttendanceConfig;
    @SerializedName("attendance_role")
    @Expose
    private String attendanceRole;
    @SerializedName("attendance_user_name")
    @Expose
    private String attendanceUserName;

    public UserAttendanceConfig getUserAttendanceConfig() {
        return userAttendanceConfig;
    }

    public void setUserAttendanceConfig(UserAttendanceConfig userAttendanceConfig) {
        this.userAttendanceConfig = userAttendanceConfig;
    }

    public String getAttendanceRole() {
        return attendanceRole;
    }

    public void setAttendanceRole(String attendanceRole) {
        this.attendanceRole = attendanceRole;
    }

    public String getAttendanceUserName() {
        return attendanceUserName;
    }

    public void setAttendanceUserName(String attendanceUserName) {
        this.attendanceUserName = attendanceUserName;
    }

}
