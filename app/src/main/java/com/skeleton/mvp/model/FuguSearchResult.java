package com.skeleton.mvp.model;

import com.skeleton.mvp.data.model.creategroup.MembersInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajatdhamija on 08/01/18.
 */
public class FuguSearchResult implements Serializable {
    private String name;
    private Long user_id;
    private String user_image;
    private String email;
    private String userUniqueKey;
    private boolean isGroup;
    private boolean isJoined;
    private int chatType;
    private boolean ischecked;
    private List<MembersInfo> membersInfos;
    private String leaveType;



    /**
     * Instantiates a new Search result.
     *
     * @param name       the name
     * @param user_id    the user id
     * @param user_image the user image
     * @param email      the email
     */
    public FuguSearchResult(String name, Long user_id, String user_image, String email, boolean isGroup, boolean isJoined, int chatType, boolean ischecked, List<MembersInfo> membersInfos, String leaveType) {
        this.name = name;
        this.user_id = user_id;
        this.user_image = user_image;
        this.email = email;
        this.isGroup = isGroup;
        this.isJoined = isJoined;
        this.chatType = chatType;
        this.ischecked = ischecked;
        this.membersInfos = membersInfos;
        this.leaveType = leaveType;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }
    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name != null ? name : "";
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public Long getUser_id() {
        return user_id;
    }

    /**
     * Sets user id.
     *
     * @param user_id the user id
     */
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    /**
     * Gets user image.
     *
     * @return the user image
     */
    public String getUser_image() {
        return user_image;
    }

    /**
     * Sets user image.
     *
     * @param user_image the user image
     */
    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
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

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public void setIschecked(boolean checked) {
        ischecked = checked;
    }

    public boolean getIschecked() {
        return ischecked;
    }

    public List<MembersInfo> getMembersInfos() {
        if (membersInfos==null){
            return new ArrayList<>();
        }
        return membersInfos;
    }

    public void setMembersInfos(ArrayList<MembersInfo> membersInfos) {
        this.membersInfos = membersInfos;
    }
}
