package com.skeleton.mvp.data.model.setUserPassword;


/*********************************
 Created by Amandeep Chauhan     *
 Date :- 21/05/2020              *
 ********************************/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SetUserPasswordResponse {
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
