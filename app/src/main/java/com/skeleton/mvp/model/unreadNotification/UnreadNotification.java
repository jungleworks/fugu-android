
package com.skeleton.mvp.model.unreadNotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnreadNotification {

    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("notification_type")
    @Expose
    private Integer notificationType;
    @SerializedName("thread_muid")
    @Expose
    private String threadMuid = "";
    @SerializedName("is_tagged")
    @Expose
    private Integer isTagged;

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public Long getChannelId() {
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

    public Integer getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public String getThreadMuid() {
        return threadMuid;
    }

    public void setThreadMuid(String threadMuid) {
        this.threadMuid = threadMuid;
    }

    public Integer getIsTagged() {
        return isTagged;
    }

    public void setIsTagged(Integer isTagged) {
        this.isTagged = isTagged;
    }

}
