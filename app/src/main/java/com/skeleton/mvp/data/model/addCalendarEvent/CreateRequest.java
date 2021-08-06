package com.skeleton.mvp.data.model.addCalendarEvent;

public class CreateRequest {
    private String requestID;
    private Key conferenceSolutionKey;
    private Status status;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String value) {
        this.requestID = value;
    }

    public Key getConferenceSolutionKey() {
        return conferenceSolutionKey;
    }

    public void setConferenceSolutionKey(Key value) {
        this.conferenceSolutionKey = value;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        this.status = value;
    }
}
