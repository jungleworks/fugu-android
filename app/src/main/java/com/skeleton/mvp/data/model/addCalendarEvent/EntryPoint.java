package com.skeleton.mvp.data.model.addCalendarEvent;

public class EntryPoint {
    private String entryPointType;
    private String uri;
    private String label;
    private String regionCode;
    private String pin;

    public String getEntryPointType() {
        return entryPointType;
    }

    public void setEntryPointType(String value) {
        this.entryPointType = value;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String value) {
        this.uri = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        this.label = value;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String value) {
        this.regionCode = value;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String value) {
        this.pin = value;
    }
}
