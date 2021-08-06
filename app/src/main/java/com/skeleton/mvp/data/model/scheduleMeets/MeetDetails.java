package com.skeleton.mvp.data.model.scheduleMeets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MeetDetails implements Serializable {
    @SerializedName("meet_id")
    @Expose
    private long meetId;
    @SerializedName("user_id")
    @Expose
    private long userID;
    @SerializedName("workspace_id")
    @Expose
    private long workspaceID;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("start_datetime")
    @Expose
    private String startDatetime;
    @SerializedName("meet_type")
    @Expose
    private String meetType;
    @SerializedName("end_datetime")
    @Expose
    private String endDatetime;
    @SerializedName("reminder_time")
    @Expose
    private int reminderTime;
    @SerializedName("room_id")
    @Expose
    private String roomId;
    @SerializedName("link")
    @Expose
    private String link = "";
    @SerializedName("frequency")
    @Expose
    private int frequency;
    @SerializedName("attendees")
    @Expose
    private ArrayList<Attendee> attendees;
    @SerializedName("status")
    @Expose
    private int status;

    public long getMeetId() {
        return meetId;
    }

    public void setMeetId(long meetId) {
        this.meetId = meetId;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(long workspaceID) {
        this.workspaceID = workspaceID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeetType() {
        return meetType;
    }

    public void setMeetType(String meetType) {
        this.meetType = meetType;
    }

    public String getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    public String getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(String endDatetime) {
        this.endDatetime = endDatetime;
    }

    public int getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(int reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public ArrayList<Attendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<Attendee> attendees) {
        this.attendees = attendees;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}