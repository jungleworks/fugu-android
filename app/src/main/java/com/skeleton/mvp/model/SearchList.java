package com.skeleton.mvp.model;

import android.text.TextUtils;

import android.text.TextUtils;

import com.skeleton.mvp.data.model.creategroup.MembersInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajatdhamija
 * 22/05/18.
 */

public class SearchList {
    private String name;
    private Long user_id;
    private String user_image;
    private String email;
    private String userUniqueKey;
    private boolean isGroup;
    private boolean isJoined;
    private List<MembersInfo> membersInfos = new ArrayList<>();
    private boolean isFromSearch;

    public SearchList(String name, Long user_id, String user_image, String email, boolean isGroup, boolean isJoined, List<MembersInfo> membersInfos, boolean isFromSearch) {
        this.name = name;
        this.user_id = user_id;
        this.user_image = user_image;
        if (TextUtils.isEmpty(email)) {
            this.email = "";
        }else {
            this.email = email;
        }
        this.userUniqueKey = userUniqueKey;
        this.isGroup = isGroup;
        this.isJoined = isJoined;
        this.membersInfos = membersInfos;
        this.isFromSearch = isFromSearch;
    }

    public boolean isFromSearch() {
        return isFromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        isFromSearch = fromSearch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }

    public List<MembersInfo> getMembersInfos() {
        return membersInfos;
    }

    public void setMembersInfos(List<MembersInfo> membersInfos) {
        this.membersInfos = membersInfos;
    }
}
