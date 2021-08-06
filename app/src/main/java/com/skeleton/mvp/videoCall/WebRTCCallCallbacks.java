package com.skeleton.mvp.videoCall;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;

/**
 * Created by rajatdhamija
 * 20/09/18.
 */

public interface WebRTCCallCallbacks {
    void onIceCandidate(IceCandidate iceCandidate);

    void onAddStream(MediaStream mediaStream);
}
