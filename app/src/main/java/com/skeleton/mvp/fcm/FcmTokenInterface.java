package com.skeleton.mvp.fcm;

/**
 * Developer: Click Labs
 */
public interface FcmTokenInterface {
    /**
     * On token received.
     *
     * @param token the token
     */
    void onTokenReceived(String token);

    /**
     * On failure.
     */
    void onFailure();
}
