package com.skeleton.mvp.model;

import java.io.Serializable;

public class GroupMember implements Serializable {
    private String Name;
    private String image;
    private Long userId;
    private String UserUniqueKey;
    private String email;
    private String role;
    private int viewtype;

    public GroupMember(String name, Long userId, String UserUniqueKey, String image, String email, String role, int viewType) {
        Name = name.trim();
        this.image = image;
        this.userId = userId;
        this.UserUniqueKey = UserUniqueKey;
        this.email = email;
        this.role = role;
        this.viewtype = viewType;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserUniqueKey() {
        return UserUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        UserUniqueKey = userUniqueKey;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }
}
