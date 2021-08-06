package com.skeleton.mvp.data.model.groupTasks;

import java.util.ArrayList;

public class GetTaskResponse {
    private long statusCode;
    private String message;
    private ArrayList<TaskDetail> data = new ArrayList<>();

    public long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(long value) {
        this.statusCode = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public ArrayList<TaskDetail> getData() {
        return data;
    }

    public void setData(ArrayList<TaskDetail> value) {
        this.data = value;
    }
}