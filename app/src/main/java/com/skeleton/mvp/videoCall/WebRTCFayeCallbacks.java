package com.skeleton.mvp.videoCall;

import org.json.JSONObject;

/**
 * Created by rajatdhamija on 20/09/18.
 */

public interface WebRTCFayeCallbacks {
    void onIceCandidateRecieved(JSONObject jsonObject);

    void onVideoOfferRecieved(JSONObject jsonObject);

    void onVideoAnswerRecieved(JSONObject jsonObject);

    void onVideoOfferScreenSharingRecieved(JSONObject jsonObject);

    void onReadyToConnectRecieved(JSONObject jsonObject);

    void onCallHungUp(JSONObject jsonObject,Boolean showFeedback);

    void onCallRejected(JSONObject jsonObject);

    void onUserBusyRecieved(JSONObject jsonObject);

    void onVideoConfInitiated(JSONObject jsonObject);

    void onErrorRecieved(String error);

    void onFayeConnected();
}
