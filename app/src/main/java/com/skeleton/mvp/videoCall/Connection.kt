package com.skeleton.mvp.videoCall

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory

/**
 * Created by rajatdhamija
 * 21/09/18.
 */
data class Connection(var stunServers: ArrayList<String>? = ArrayList(),
                      var turnServers: ArrayList<String>? = ArrayList(),
                      var sdpConstraints: MediaConstraints? = MediaConstraints(),
                      var turnUserName: String? = "",
                      var turnCredential: String? = "",
                      var peerConnectionFactory: PeerConnectionFactory?)