package com.skeleton.mvp.data.model.groupTasks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TaskDetail {
    @SerializedName("task_id")
    @Expose
    private long taskID;
    @SerializedName("is_completed")
    @Expose
    private int isCompleted = 0;
    @SerializedName("assigner_user_id")
    @Expose
    private long assignerID;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("start_datetime")
    @Expose
    private String startDateTime;
    @SerializedName("end_datetime")
    @Expose
    private String endDateTime;
    @SerializedName("reminder")
    @Expose
    private int reminder = 10;
    @SerializedName("submission_datetime")
    @Expose
    private String submissionDatetime = null;
    @SerializedName("content")
    @Expose
    private String content = null;
    @SerializedName("task_work")
    @Expose
    private TaskWork taskWork = null;
    @SerializedName("is_selected_all")
    @Expose
    private long isSelectedAll;
    @SerializedName("is_deleted")
    @Expose
    private long isDeleted;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("user_data")
    @Expose
    private ArrayList<UserData> userData;
    @SerializedName("channel_id")
    @Expose
    private long channelID;

    public long getTaskID() {
        return taskID;
    }

    public void setTaskID(long value) {
        this.taskID = value;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public long getAssignerID() {
        return assignerID;
    }

    public void setAssignerID(long value) {
        this.assignerID = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String value) {
        this.startDateTime = value;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String value) {
        this.endDateTime = value;
    }

    public long getIsSelectedAll() {
        return isSelectedAll;
    }

    public void setIsSelectedAll(long value) {
        this.isSelectedAll = value;
    }

    public long getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(long value) {
        this.isDeleted = value;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String value) {
        this.createdAt = value;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String value) {
        this.updatedAt = value;
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int value) {
        this.reminder = value;
    }

    public String getSubmissionDatetime() {
        return submissionDatetime;
    }

    public void setSubmissionDatetime(String value) {
        this.submissionDatetime = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String value) {
        this.content = value;
    }

    public TaskWork getTaskWork() {
        return taskWork;
    }

    public void setTaskWork(TaskWork value) {
        this.taskWork = value;
    }

    public ArrayList<UserData> getUserData() {
        return userData;
    }

    public void setUserData(ArrayList<UserData> value) {
        this.userData = value;
    }

    public long getChannelID() {
        return channelID;
    }

    public void setChannelID(long value) {
        this.channelID = value;
    }
}

