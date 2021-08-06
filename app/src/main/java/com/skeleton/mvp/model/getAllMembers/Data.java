package com.skeleton.mvp.model.getAllMembers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.model.alreadyInvited.InvitedUser;

public class Data {

    @SerializedName("all_members")
    @Expose
    private List<AllMember> allMemberResponse = new ArrayList<>();

    public List<AllMember> getAllMemberResponse() {
        return allMemberResponse;
    }

    public void setAllMemberRespons(List<AllMember> allMemberResponse) {
        this.allMemberResponse = allMemberResponse;
    }


    @SerializedName("pending_members")
    @Expose
    private List<PendingMember> pendingMemberList = new ArrayList<>();

    @SerializedName("accepted_members")
    @Expose
    private List<PendingMember> acceptedMemberList = new ArrayList<>();

    @SerializedName("invited_users")
    @Expose
    private List<InvitedUser> invitedUsers = new ArrayList<>();

    public List<InvitedUser> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<InvitedUser> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    @SerializedName("user_count")
    @Expose
    private int userCount = 0;

    @SerializedName("get_all_member_page_size")
    @Expose
    private int getAllMemberPageSize = 0;


    public void setAllMemberResponse(List<AllMember> allMemberResponse) {
        this.allMemberResponse = allMemberResponse;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getGetAllMemberPageSize() {
        return getAllMemberPageSize;
    }

    public void setGetAllMemberPageSize(int getAllMemberPageSize) {
        this.getAllMemberPageSize = getAllMemberPageSize;
    }

    public List<PendingMember> getPendingMemberList() {
        return pendingMemberList;
    }

    public void setPendingMemberList(List<PendingMember> pendingMemberList) {
        this.pendingMemberList = pendingMemberList;
    }

    public List<PendingMember> getAcceptedMemberList() {
        return acceptedMemberList;
    }

    public void setAcceptedMemberList(List<PendingMember> acceptedMemberList) {
        this.acceptedMemberList = acceptedMemberList;
    }
}