
package com.skeleton.mvp.model.innerMessage;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("only_admin_can_message")
    @Expose
    private boolean onlyAdminsCanMessage = false;

    @SerializedName("user_channel_role")
    @Expose
    private String userChannelRole="USER";

    @SerializedName("message")
    @Expose
    private Message message;
    @SerializedName("status")
    @Expose
    private int status=1;
    @SerializedName("thread_message")
    @Expose
    private List<ThreadMessage> threadMessage = null;
    @SerializedName("user_following_status")
    @Expose
    private int userFollowingStatus = 2;

    @SerializedName("label")
    @Expose
    private String label="";

    @SerializedName("other_user_type")
    @Expose
    private int other_user_type=0;

    public int getOther_user_type() {
        return other_user_type;
    }

    public void setOther_user_type(int other_user_type) {
        this.other_user_type = other_user_type;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<ThreadMessage> getThreadMessage() {
        return threadMessage;
    }

    public void setThreadMessage(List<ThreadMessage> threadMessage) {
        this.threadMessage = threadMessage;
    }

    public int getUserFollowingStatus() {
        return userFollowingStatus;
    }

    public void setUserFollowingStatus(int userFollowingStatus) {
        this.userFollowingStatus = userFollowingStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isOnlyAdminsCanMessage() {
        return onlyAdminsCanMessage;
    }

    public void setOnlyAdminsCanMessage(boolean onlyAdminsCanMessage) {
        this.onlyAdminsCanMessage = onlyAdminsCanMessage;
    }

    public String getUserChannelRole() {
        return userChannelRole;
    }

    public void setUserChannelRole(String userChannelRole) {
        this.userChannelRole = userChannelRole;
    }
}
