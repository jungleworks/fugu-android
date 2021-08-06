
package com.skeleton.mvp.model.seenBy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageSeenBy {

    @SerializedName("full_name")
    @Expose
    private String fullName = "";
    @SerializedName("role")
    @Expose
    private String role = "";
    @SerializedName("seen_at")
    @Expose
    private String seenAt = "";
    @SerializedName("user_thumbnail_image")
    private String userThumbnailImage = "";

    @SerializedName("user_id")
    @Expose
    private Long userId = -1L;

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(String seenAt) {
        this.seenAt = seenAt;
    }

    public String getUserThumbnailImage() {
        return userThumbnailImage;
    }

    public void setUserThumbnailImage(String userThumbnailImage) {
        this.userThumbnailImage = userThumbnailImage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
