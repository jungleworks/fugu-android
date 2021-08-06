package com.skeleton.mvp.model;

import java.util.ArrayList;
import java.util.List;

public class GroupContacts {
    private String workspaceName;
    private String groupsName;
    private boolean isSelected;
    private String channelId;
    private String channelImageUrl;
    private String channelThumbnailUrl;
    private String appSecretkey;
    private List<String> emails;
    private List<String>phoneNo;
    private int viewType;

    public GroupContacts(String workspaceName, String groupsName, boolean isSelected, String channelId, String channelImageUrl, String channelThumbnailUrl, String appSecretkey, List<String> emails, List<String> phoneNo,int viewType){
        this.workspaceName=workspaceName;
        this.groupsName=groupsName;
        this.isSelected=isSelected;
        this.channelId=channelId;
        this.channelImageUrl=channelImageUrl;
        this.channelThumbnailUrl=channelThumbnailUrl;
        this.appSecretkey=appSecretkey;
        this.emails=emails;
        this.phoneNo=phoneNo;
        this.viewType=viewType;


    }


    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public void setGroupsName(String groupsName) {
        this.groupsName = groupsName;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setChannelImageUrl(String channelImageUrl) {
        this.channelImageUrl = channelImageUrl;
    }

    public void setChannelThumbnailUrl(String channelThumbnailUrl) {
        this.channelThumbnailUrl = channelThumbnailUrl;
    }

    public void setAppSecretkey(String appSecretkey) {
        this.appSecretkey = appSecretkey;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    public void setPhoneNo(ArrayList<String> phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public String getGroupsName() {
        return groupsName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelImageUrl() {
        return channelImageUrl;
    }

    public String getChannelThumbnailUrl() {
        return channelThumbnailUrl;
    }

    public String getAppSecretkey() {
        return appSecretkey;
    }

    public List<String> getEmails() {
        return emails;
    }

    public List<String> getPhoneNo() {
        return phoneNo;
    }

    public int getViewType() {
        return viewType;
    }
}
