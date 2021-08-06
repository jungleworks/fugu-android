package com.skeleton.mvp.activity;

import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;

/**
 * Webrtc_Step2
 * Created by vivek-3102 on 11/03/17.
 */

class CustomPeerConnectionObserver implements PeerConnection.Observer {
    private VideoCallActivity videoCallActivity;
    private String logTag = "CustomPeerConnectionObserver";

    CustomPeerConnectionObserver(String logTag, VideoCallActivity videoCallActivity) {
        this.logTag = this.logTag + " " + logTag;
        this.videoCallActivity = videoCallActivity;
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(logTag, "onSignalingChange() called with: signalingState = [" + signalingState + "]");
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.d(logTag, "onIceConnectionChange() called with: iceConnectionState = [" + iceConnectionState + "]");
        if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
            videoCallActivity.finish();
        }
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.d(logTag, "onIceConnectionReceivingChange() called with: b = [" + b + "]");
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.d(logTag, "onIceGatheringChange() called with: iceGatheringState = [" + iceGatheringState + "]");
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.d(logTag, "onIceCandidate() called with: iceCandidate = [" + iceCandidate + "]");
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.d(logTag, "onIceCandidatesRemoved() called with: iceCandidates = [" + iceCandidates + "]");
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        Log.d(logTag, "onAddStream() called with: mediaStream = [" + mediaStream + "]");
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.d(logTag, "onRemoveStream() called with: mediaStream = [" + mediaStream + "]");
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.d(logTag, "onDataChannel() called with: dataChannel = [" + dataChannel + "]");
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.d(logTag, "onRenegotiationNeeded() called");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

    }
}
