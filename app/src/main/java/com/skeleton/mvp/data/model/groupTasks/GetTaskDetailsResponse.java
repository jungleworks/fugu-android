package com.skeleton.mvp.data.model.groupTasks;


public class GetTaskDetailsResponse {
    private long statusCode;
    private String message;
    private TaskDetail data;

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

    public TaskDetail getData() {
        return data;
    }

    public void setData(TaskDetail value) {
        this.data = value;
    }
}