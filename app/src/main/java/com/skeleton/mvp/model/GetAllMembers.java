package com.skeleton.mvp.model;

public class GetAllMembers {
    private String fullName;
    private String email;
    private String userImage;
    private String userThumbnailImage;
    private String role;
    private int searchCount;
    private String phoneNo;
    private Long userId;
    private boolean isSelected = false;
    private String leaveType;


    public GetAllMembers(Long userId, String fullName, String email, String userImage, String userThumbnailImage, String role, int searchCount, String contactNumber, String leaveType) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.userImage = userImage;
        this.userThumbnailImage = userThumbnailImage;
        this.role = role;
        this.searchCount = searchCount;
        this.phoneNo = contactNumber;
        this.leaveType = leaveType;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void setUserThumbnailImage(String userThumbnailImage) {
        this.userThumbnailImage = userThumbnailImage;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Long getUserId() {
        return userId;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public String getEmail() {
        return email;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUserThumbnailImage() {
        return userThumbnailImage;
    }

    public String getRole() {
        return role;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }
}
