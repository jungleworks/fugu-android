
package com.skeleton.mvp.data.model.getUserInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.data.model.editprofile.ManagerData;

public class Data {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("business_id")
    @Expose
    private Integer businessId;
    @SerializedName("contact_number")
    @Expose
    private String contactNumber;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("manager")
    @Expose
    private String manager;

    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("manager_data")
    @Expose
    private ManagerData managerData;


    @SerializedName("is_message_allowed")
    @Expose
    private boolean ismessageAllowed = false;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ManagerData getManagerData() {
        return managerData;
    }

    public void setManagerData(ManagerData managerData) {
        this.managerData = managerData;
    }

    public boolean isIsmessageAllowed() {
        return ismessageAllowed;
    }

    public void setIsmessageAllowed(boolean ismessageAllowed) {
        this.ismessageAllowed = ismessageAllowed;
    }
}
