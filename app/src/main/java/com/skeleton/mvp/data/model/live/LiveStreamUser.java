package com.skeleton.mvp.data.model.live;

import com.google.gson.annotations.SerializedName;

/********************************
 * Created by Amandeep Chauhan  *
 * Date :- 14/05/2020           *
 *******************************/

public class LiveStreamUser {
    @SerializedName("user_id")
    private int userId;
    @SerializedName("email")
    private String email;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("contact_number")
    private String contactNumber;
    @SerializedName("user_image")
    private String userImage;
    @SerializedName("user_thumbnail_image")
    private String userThumbnailImage;
    @SerializedName("role")
    private String role;
    @SerializedName("stream_token")
    private String streamToken;

    @SerializedName("socket_token")
    private String socketToken;

    @SerializedName("stream_id")
    private String streamId;
    @SerializedName("token_expiry")
    private long tokenExpiry;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserThumbnailImage() {
        return userThumbnailImage;
    }

    public void setUserThumbnailImage(String userThumbnailImage) {
        this.userThumbnailImage = userThumbnailImage;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStreamToken() {
        return streamToken;
    }

    public String getSocketToken() {
        return socketToken;
    }

    public void setSocketToken(String socketToken) {
        this.socketToken = socketToken;
    }

    public void setStreamToken(String streamToken) {
        this.streamToken = streamToken;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public long getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(long tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }
}
