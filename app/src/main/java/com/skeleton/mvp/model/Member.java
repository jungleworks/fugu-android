package com.skeleton.mvp.model;

/**
 * Created by rajatdhamija on 11/01/18.
 */

public class Member {
    private String Name;
    private String image;
    private Long userId;
    private String UserUniqueKey;
    private String email;
    private Integer userType;
    private String status;
    private String leaveType;



    public Member(String name, Long userId, String UserUniqueKey, String image, String email, Integer userType, String status, String leaveType) {
        Name = name.trim();
        this.image = image;
        this.userId = userId;
        this.UserUniqueKey = UserUniqueKey;
        this.email = email;
        this.userType = userType;
        this.status = status;
        this.leaveType = leaveType;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
