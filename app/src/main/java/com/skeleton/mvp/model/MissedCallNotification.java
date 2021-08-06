package com.skeleton.mvp.model;

/**
 * Created by rajatdhamija on 24/04/18.
 */

public class MissedCallNotification {
    private String fullName;
    private Long userId;
    private boolean isThread;
    private String dateTime;
    private boolean isSilent;

    public MissedCallNotification(String fullName, boolean isThread, String dateTime, boolean isSilent, Long userId) {
        this.fullName = fullName;
        this.isThread = isThread;
        this.dateTime = dateTime;
        this.isSilent = isSilent;
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String message) {
        this.fullName = message;
    }

    public boolean isThread() {
        return isThread;
    }

    public void setThread(boolean thread) {
        isThread = thread;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isSilent() {
        return isSilent;
    }

    public void setSilent(boolean silent) {
        isSilent = silent;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
