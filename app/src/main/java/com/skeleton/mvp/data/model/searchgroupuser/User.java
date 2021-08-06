
package com.skeleton.mvp.data.model.searchgroupuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.data.model.creategroup.MembersInfo;

import java.util.ArrayList;
import java.util.List;

public class User {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("members_info")
    @Expose
    private List<MembersInfo> membersInfo = new ArrayList<>();
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public List<MembersInfo> getMembersInfo() {
        return membersInfo;
    }

    public void setMembersInfo(List<MembersInfo> membersInfo) {
        this.membersInfo = membersInfo;
    }
}
