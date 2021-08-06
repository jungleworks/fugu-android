package com.skeleton.mvp.model;

/**
 * Created
 * rajatdhamija on 02/08/18.
 */

public class UnreadCount {
    private Long channelId = -1L;
    private String muid = "";
    private int notificationType = 0;
    private boolean isTagged = false;
    private boolean isThreadMessage = false;

    public UnreadCount(Long channelId, String muid, int notificationType, boolean isTagged, boolean isThreadMessage) {
        this.channelId = channelId;
        this.muid = muid;
        this.notificationType = notificationType;
        this.isTagged = isTagged;
        this.isThreadMessage = isThreadMessage;
    }

    public Long getChannelId() {
        if (channelId == null) {
            return -1L;
        }
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isTagged() {
        return isTagged;
    }

    public void setTagged(boolean tagged) {
        isTagged = tagged;
    }

    public boolean isThreadMessage() {
        return isThreadMessage;
    }

    public void setThreadMessage(boolean threadMessage) {
        isThreadMessage = threadMessage;
    }
}
