package com.skeleton.mvp.data.model.addCalendarEvent;

public class ConferenceData {
    private CreateRequest createRequest;
    private EntryPoint[] entryPoints;
    private ConferenceSolution conferenceSolution;
    private String conferenceID;
    private String signature;

    public CreateRequest getCreateRequest() {
        return createRequest;
    }

    public void setCreateRequest(CreateRequest value) {
        this.createRequest = value;
    }

    public EntryPoint[] getEntryPoints() {
        return entryPoints;
    }

    public void setEntryPoints(EntryPoint[] value) {
        this.entryPoints = value;
    }

    public ConferenceSolution getConferenceSolution() {
        return conferenceSolution;
    }

    public void setConferenceSolution(ConferenceSolution value) {
        this.conferenceSolution = value;
    }

    public String getConferenceID() {
        return conferenceID;
    }

    public void setConferenceID(String value) {
        this.conferenceID = value;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String value) {
        this.signature = value;
    }
}
