package com.skeleton.mvp.model;

/**
 * Created by rajatdhamija on 24/04/18.
 */

public class PushNotification {
    private String message;
    private boolean isThread;
    private String dateTime;
    private boolean isSilent;
    private String senderName;
    private String senderImage;

    public PushNotification(String message, boolean isThread, String dateTime, boolean isSilent, String senderName,String senderImage) {
        this.message = message;
        this.isThread = isThread;
        this.dateTime = dateTime;
        this.isSilent = isSilent;
        this.senderName = senderName;
        this.senderImage=senderImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }
}
