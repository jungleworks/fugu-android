
package com.skeleton.mvp.model.turncredsresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("turn_api_key")
    @Expose
    private String turnApiKey;
    @SerializedName("ice_servers")
    @Expose
    private IceServers iceServers;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("credential")
    @Expose
    private String credential;

    public String getTurnApiKey() {
        return turnApiKey;
    }

    public void setTurnApiKey(String turnApiKey) {
        this.turnApiKey = turnApiKey;
    }

    public IceServers getIceServers() {
        return iceServers;
    }

    public void setIceServers(IceServers iceServers) {
        this.iceServers = iceServers;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

}
