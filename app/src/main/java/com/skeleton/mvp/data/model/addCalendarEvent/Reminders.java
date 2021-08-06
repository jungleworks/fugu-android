package com.skeleton.mvp.data.model.addCalendarEvent;

public class Reminders {
    private boolean useDefault;
    private OverrideElement[] overrides;

    public boolean getUseDefault() {
        return useDefault;
    }

    public void setUseDefault(boolean value) {
        this.useDefault = value;
    }

    public OverrideElement[] getOverrides() {
        return overrides;
    }

    public void setOverrides(OverrideElement[] value) {
        this.overrides = value;
    }
}
