package com.skeleton.mvp.data.model.editprofile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ManagerData {

    @SerializedName("fugu_user_id")
    @Expose
    private Long managerUserId;
    @SerializedName("full_name")
    @Expose
    private String managerFullName;

    public void setManagerUserId(Long managerUserId){
        this.managerUserId = managerUserId;
    }

    public Long getManagerUserId(){
        return managerUserId;
    }

    public void setManagerFullName(String managerFullName){
        this.managerFullName = managerFullName;
    }
    public String getManagerFullName(){
        return managerFullName;
    }


}
