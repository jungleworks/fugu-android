package com.skeleton.mvp.model;

import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

import java.util.ArrayList;

/**
 * Created by rajatdhamija
 * 18/09/18.
 */

public class VideoCall {
    private PeerConnection peerConnection;
    private Long userid;
    private String fullName;
    private Long channelId;
    private String userImage;
    private boolean isSelfCalling;
    private String username;
    private String credential;
    private ArrayList<String> stunServers;
    private ArrayList<String> turnServers;
    private String turnApiKey;
    private String muid;
    private MediaStream mediaStream;

    public VideoCall(PeerConnection peerConnection, Long userid, String fullName, Long channelId, String userImage, boolean isSelfCalling, String username, String credential, ArrayList<String> stunServers, ArrayList<String> turnServers, String turnApiKey, String muid, MediaStream mediaStream) {
        this.peerConnection = peerConnection;
        this.userid = userid;
        this.fullName = fullName;
        this.channelId = channelId;
        this.userImage = userImage;
        this.isSelfCalling = isSelfCalling;
        this.username = username;
        this.credential = credential;
        this.stunServers = stunServers;
        this.turnServers = turnServers;
        this.turnApiKey = turnApiKey;
        this.muid = muid;
        this.mediaStream = mediaStream;
    }

    public PeerConnection getPeerConnection() {
        return peerConnection;
    }

    public void setPeerConnection(PeerConnection peerConnection) {
        this.peerConnection = peerConnection;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public boolean isSelfCalling() {
        return isSelfCalling;
    }

    public void setSelfCalling(boolean selfCalling) {
        isSelfCalling = selfCalling;
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

    public ArrayList<String> getStunServers() {
        return stunServers;
    }

    public void setStunServers(ArrayList<String> stunServers) {
        this.stunServers = stunServers;
    }

    public ArrayList<String> getTurnServers() {
        return turnServers;
    }

    public void setTurnServers(ArrayList<String> turnServers) {
        this.turnServers = turnServers;
    }

    public String getTurnApiKey() {
        return turnApiKey;
    }

    public void setTurnApiKey(String turnApiKey) {
        this.turnApiKey = turnApiKey;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public MediaStream getMediaStream() {
        return mediaStream;
    }

    public void setMediaStream(MediaStream mediaStream) {
        this.mediaStream = mediaStream;
    }
}
