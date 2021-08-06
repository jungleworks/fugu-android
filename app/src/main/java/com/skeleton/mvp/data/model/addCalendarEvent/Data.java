package com.skeleton.mvp.data.model.addCalendarEvent;

public class Data {
    private String kind;
    private String etag;
    private String id;
    private String status;
    private String htmlLink;
    private String created;
    private String updated;
    private String summary;
    private String description;
    private Creator creator;
    private Creator organizer;
    private Timings start;
    private Timings end;
    private String iCalUID;
    private long sequence;
    private Attendee[] attendees;
    private String hangoutLink;
    private ConferenceData conferenceData;
    private Reminders reminders;

    public String getKind() {
        return kind;
    }

    public void setKind(String value) {
        this.kind = value;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String value) {
        this.etag = value;
    }

    public String getID() {
        return id;
    }

    public void setID(String value) {
        this.id = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getHTMLLink() {
        return htmlLink;
    }

    public void setHTMLLink(String value) {
        this.htmlLink = value;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String value) {
        this.created = value;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String value) {
        this.updated = value;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String value) {
        this.summary = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator value) {
        this.creator = value;
    }

    public Creator getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Creator value) {
        this.organizer = value;
    }

    public Timings getStart() {
        return start;
    }

    public void setStart(Timings value) {
        this.start = value;
    }

    public Timings getEnd() {
        return end;
    }

    public void setEnd(Timings value) {
        this.end = value;
    }

    public String getICalUID() {
        return iCalUID;
    }

    public void setICalUID(String value) {
        this.iCalUID = value;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long value) {
        this.sequence = value;
    }

    public Attendee[] getAttendees() {
        return attendees;
    }

    public void setAttendees(Attendee[] value) {
        this.attendees = value;
    }

    public String getHangoutLink() {
        return hangoutLink;
    }

    public void setHangoutLink(String value) {
        this.hangoutLink = value;
    }

    public ConferenceData getConferenceData() {
        return conferenceData;
    }

    public void setConferenceData(ConferenceData value) {
        this.conferenceData = value;
    }

    public Reminders getReminders() {
        return reminders;
    }

    public void setReminders(Reminders value) {
        this.reminders = value;
    }
}
