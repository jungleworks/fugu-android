package com.skeleton.mvp.model;

import java.io.Serializable;

/**
 * Created by rajatdhamija on 17/05/18.
 */

public class DeleteMessage implements Serializable {
    private String message;
    private int replyCount;
    private boolean isThreadMessage;
    private int rowType;
    private String muid;
    private String sentAtUtc;
    private String fromName;
    private String email;
    private Long userId;
    private int userType;

    public DeleteMessage(String message, int replyCount, boolean isThreadMessage, int rowType, String muid, String sentAtUtc, String fromName, String email, Long userId, int userType) {
        this.message = message;
        this.replyCount = replyCount;
        this.isThreadMessage = isThreadMessage;
        this.rowType = rowType;
        this.muid = muid;
        this.sentAtUtc = sentAtUtc;
        this.fromName = fromName;
        this.email = email;
        this.userId = userId;
        this.userType = userType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public boolean isThreadMessage() {
        return isThreadMessage;
    }

    public void setThreadMessage(boolean threadMessage) {
        isThreadMessage = threadMessage;
    }

    public int getRowType() {
        return rowType;
    }

    public void setRowType(int rowType) {
        this.rowType = rowType;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getSentAtUtc() {
        return sentAtUtc;
    }

    public void setSentAtUtc(String sentAtUtc) {
        this.sentAtUtc = sentAtUtc;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
