package com.skeleton.mvp.data.model.scheduleMeets;

import java.util.ArrayList;

public class GetMeetingsResponse {
    private long statusCode;
    private String message;
    private ArrayList<MeetDetails> data = new ArrayList<>();

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

    public ArrayList<MeetDetails> getData() {
        return data;
    }

    public void setData(ArrayList<MeetDetails> value) {
        this.data = value;
    }
}