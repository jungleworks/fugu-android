package com.skeleton.mvp.data.model.creategroup;

/**
 * Created by rajatdhamija on 10/07/18.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MembersInfo implements Serializable {

    @SerializedName("full_name")
    @Expose
    private String fullName = "";
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("user_id")
    @Expose
    private Long userId = 0L;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
