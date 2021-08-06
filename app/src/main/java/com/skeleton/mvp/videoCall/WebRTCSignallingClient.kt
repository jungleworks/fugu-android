package com.skeleton.mvp.videoCall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.model.fayeVideoCall.FayeVideoCallResponse
import com.skeleton.mvp.service.VideoCallService
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.UniqueIMEIID
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.CREDENTIAL
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.DEVICE_PAYLOAD
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.FULL_NAME
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.IS_SILENT
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.IS_TYPING
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.MESSAGE_TYPE
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.MESSAGE_UNIQUE_ID
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.REASON
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.STUN
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.TURN
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.TURN_API_KEY
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.TURN_CREDENTIALS
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.USER_ID
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.USER_NAME
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.VIDEO_CALL
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.VIDEO_CALL_HUNGUP_FROM_NOTIFICATION
import com.skeleton.mvp.videoCall.WebRTCCallConstants.Companion.VIDEO_CALL_TYPE
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.PeerConnection

/**
 * Created by rajatdhamija
 * 20/09/18.
 */

class WebRTCSignallingClient(private val videoCallService: VideoCallService, private val channelId: Long?, private val activitylaunchState: String?) {
    //    private var mClient: FayeClient? = null
    private var signal: Signal? = null
    private var initalCalls = 1
    private var maxCalls = 15
    private var isErrorEncountered = false
    private var isOfferrecieved = false
    private var userBusyRecieved = false
    private var mInitiateStartCalltimer: CountDownTimer? = null
    fun setUpFayeConnection(sendStartCall: Boolean) {
        SocketConnection.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().userInfo.accessToken,
                CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userChannel, "WebRTC", false,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.pushToken)
        if (sendStartCall) {
            LocalBroadcastManager.getInstance(videoCallService).registerReceiver(mHungUp,
                    IntentFilter(VIDEO_CALL_HUNGUP_FROM_NOTIFICATION))

            if (activitylaunchState?.equals(WebRTCCallConstants.AcitivityLaunchState.SELF.toString())!!) {
                initOtherCalls()
            } else if (activitylaunchState.equals(WebRTCCallConstants.AcitivityLaunchState.OTHER.toString())) {
                replyWithReadyToConnect()
            }
        }
        SocketConnection.setCallSocketListeners(object : SocketConnection.SocketClientCallback {

            override fun onCalling(messageJson: String) {

            }

            override fun onPresent(messageJson: String) {

            }

            override fun onErrorReceived(messageJson: String) {
                try {
                    val fayeVideoCallResponse = Gson().fromJson(messageJson, FayeVideoCallResponse::class.java)
                    if (fayeVideoCallResponse.statusCode == INVALID_VIDEO_CALL_CREDENTIALS) {
                        isErrorEncountered = true
                        val iceServers = ArrayList<PeerConnection.IceServer>()
                        signal?.turnApiKey = fayeVideoCallResponse.message.turnApiKey
                        signal?.turnUserName = fayeVideoCallResponse.message.username
                        signal?.turnCredential = fayeVideoCallResponse.message.credentials
                        signal?.stunServers = fayeVideoCallResponse.message.iceServers.stun as ArrayList<String>
                        signal?.turnServers = fayeVideoCallResponse.message.iceServers.turn as ArrayList<String>

                        object : Thread() {
                            override fun run() {
                                super.run()
                                val fcCommonResponseFugu = CommonData.getCommonResponse()
                                val turnCreds = fcCommonResponseFugu.getData().turnCredentials
                                turnCreds.credentials = signal?.turnCredential
                                turnCreds.username = signal?.turnUserName
                                turnCreds.turnApiKey = signal?.turnApiKey
                                turnCreds.iceServers.stun = signal?.stunServers
                                turnCreds.iceServers.turn = signal?.turnServers
                                fcCommonResponseFugu.data.turnCredentials = turnCreds
                                CommonData.setCommonResponse(fcCommonResponseFugu)
                            }
                        }.start()
                        for (i in signal?.stunServers?.indices!!) {
                            val stunIceServer = PeerConnection.IceServer.builder(signal?.stunServers?.get(i))
                                    .createIceServer()
                            iceServers.add(stunIceServer)
                        }
                        for (i in signal?.turnServers?.indices!!) {
                            val turnIceServer = PeerConnection.IceServer.builder(signal?.turnServers?.get(i))
                                    .setUsername(fayeVideoCallResponse.message.username)
                                    .setPassword(fayeVideoCallResponse.message.credentials)
                                    .createIceServer()
                            iceServers.add(turnIceServer)
                        }
                        if (activitylaunchState?.equals(WebRTCCallConstants.AcitivityLaunchState.SELF.toString())!!) {
                            initiateVideoCall(false)
                            initalCalls = 1
                            isErrorEncountered = false
                            initOtherCalls()
                            //test
                            val connection = videoCallService.getConnectionModel()
                            connection?.turnCredential = signal?.turnCredential
                            connection?.turnUserName = signal?.turnUserName
                            connection?.stunServers = signal?.turnServers
                            connection?.turnServers = signal?.stunServers
                            //
                        } else if (activitylaunchState == WebRTCCallConstants.AcitivityLaunchState.OTHER.toString()) {
                            replyWithReadyToConnect()
                        } else {

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onVideoCallReceived(messageJson: String) {
                callRecieved(messageJson)
            }

            override fun onAudioCallReceived(messageJson: String) {
                callRecieved(messageJson)
            }

            override fun onMessageSent(messageJson: String) {
                val messageJson = JSONObject(messageJson)
                if (messageJson.has(VIDEO_CALL_TYPE) && messageJson.getString(VIDEO_CALL_TYPE).equals(VideoCallType.SWITCH_TO_CONFERENCE.toString())) {
                    videoCallService.initiateConferenceCall(messageJson)
                } else if (messageJson.has(HUNGUP_TYPE) && messageJson.getString(HUNGUP_TYPE).equals("DEFAULT")) {
                    videoCallService.onHungupSent()
                }
            }

            override fun onThreadMessageSent(messageJson: String) {}

            override fun onMessageReceived(messageJson: String) {}

            override fun onTypingStarted(messageJson: String) {}

            override fun onTypingStopped(messageJson: String) {}

            override fun onThreadMessageReceived(messageJson: String) {}

            override fun onReadAll(messageJson: String) {}

            override fun onPinChat(messageJson: String) {}

            override fun onUnpinChat(messageJson: String) {}

            override fun onPollVoteReceived(messageJson: String) {}

            override fun onReactionReceived(messageJson: String) {}

            override fun onConnect() {}

            override fun onDisconnect() {

            }

            override fun onConnectError(socket: Socket, message: String) {}

            override fun onChannelSubscribed() {

            }

            override fun onTaskAssigned(messageJson: String) {
            }

            override fun onMeetScheduled(messageJson: String) {
            }

            override fun onUpdateNotificationCount(messageJson: String) {
            }
        })
    }

    private fun callRecieved(messageJson: String) {
        try {
            val json = JSONObject(messageJson)
            val myUserId = json.getLong(USER_ID)
            if (json.getLong(CHANNEL_ID).compareTo(channelId!!) == 0 && myUserId.compareTo(signal?.signalUniqueUserId!!) != 0 && json.getString(MESSAGE_UNIQUE_ID) == signal?.signalUniqueId!!) {
                Log.e("Type---->", json.getString(VIDEO_CALL_TYPE))
                when (json.getString(VIDEO_CALL_TYPE)) {
                    WebRTCCallConstants.Companion.VideoCallType.READY_TO_CONNECT.toString() -> {
                        if (!videoCallService.isCallConnected!! && activitylaunchState?.equals(WebRTCCallConstants.AcitivityLaunchState.SELF.toString())!!
                                && !videoCallService.isCallInitiated!!) {
                            videoCallService.isCallInitiated = true
                            videoCallService.onReadyToConnectRecieved(json)
                        } else {
                            sendOfferToRemoteUser(videoCallService.webRTCCallClient?.videoOffer!!)
                        }
                    }
                    WebRTCCallConstants.Companion.VideoCallType.NEW_ICE_CANDIDATE.toString() -> {
                        videoCallService.onIceCandidateRecieved(json)
                    }
                    WebRTCCallConstants.Companion.VideoCallType.VIDEO_OFFER.toString() -> {
                        if (!videoCallService.isCallConnected!!) {
                            if (!isOfferrecieved) {
                                isOfferrecieved = true
                                videoCallService.onVideoOfferRecieved(json)
                            }
                        } else if (videoCallService.isCallConnected!! && json.has("is_screen_share")) {
                            if (videoCallService.peerConnection != null) {
                                videoCallService.onVideoOfferScreenSharingRecieved(json)
                            }
                        }
                    }
                    WebRTCCallConstants.Companion.VideoCallType.VIDEO_ANSWER.toString() -> {
                        if (!videoCallService.isCallConnected!!) {
                            mInitiateStartCalltimer?.cancel()
                            videoCallService.isReadyForConnection = true
                            videoCallService.isCallConnected = true
                            videoCallService.webRTCCallClient?.saveAnswer(json)
                            videoCallService.onVideoAnswerRecieved(json)
                        }
                    }
                    WebRTCCallConstants.Companion.VideoCallType.USER_BUSY.toString() -> {
                        if (!videoCallService.isCallConnected!!) {
                            videoCallService.onUserBusyRecieved(json)
                            userBusyRecieved = true
                        }
                    }
                    WebRTCCallConstants.Companion.VideoCallType.CALL_HUNG_UP.toString() -> {
                        if (json.has(HUNGUP_TYPE) && json.getString(HUNGUP_TYPE).equals("DEFAULT")) {
                            videoCallService.onCallHungUp(json, false)
                        }
                    }
                    WebRTCCallConstants.Companion.VideoCallType.CALL_REJECTED.toString() -> {
                        if (!videoCallService.isCallConnected!!) {
                            mInitiateStartCalltimer?.cancel()
                            videoCallService.isReadyForConnection = true
                            videoCallService.onCallRejected(json)
                        }
                    }
                    WebRTCCallConstants.Companion.VideoCallType.SWITCH_TO_CONFERENCE.toString() -> {
                        videoCallService.videoConfReceived(json)
                    }
                }
            } else if (json.getLong(CHANNEL_ID).compareTo(channelId) == 0 && json.has(MESSAGE_TYPE) && json.getInt(MESSAGE_TYPE) == 13 && myUserId.compareTo(signal?.signalUniqueUserId!!) == 0 && json.getString(MESSAGE_UNIQUE_ID) == signal?.signalUniqueId) {
                if (json.getString(VIDEO_CALL_TYPE) == WebRTCCallConstants.Companion.VideoCallType.CALL_REJECTED.toString()) {
                    videoCallService.onCallRejected(json)
                } else if (json.getString(VIDEO_CALL_TYPE) == WebRTCCallConstants.Companion.VideoCallType.USER_BUSY.toString()) {
                    if (!videoCallService.isCallConnected!!) {
                        videoCallService.onUserBusyRecieved(json)
                        userBusyRecieved = true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initOtherCalls() {
        Handler(Looper.getMainLooper()).post {
            mInitiateStartCalltimer = object : CountDownTimer(300000, 2000) {
                override fun onFinish() {
                }

                override fun onTick(millisUntilFinished: Long) {
                    if (initalCalls <= maxCalls && !videoCallService.isReadyForConnection!! && !isErrorEncountered && !userBusyRecieved) {
                        if (initalCalls == 1) {
                            initiateVideoCall(false)
                        } else {
                            initiateVideoCall(true)
                        }
                        initalCalls += 1
                    }
                }

            }.start()
        }
    }

    fun sendVideoConfJson(jsonObject: JSONObject) {
        val videoConfJson = addCommonuserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(videoConfJson)
    }

    private fun replyWithReadyToConnect() {
        val readyToConnectJson = JSONObject()
        readyToConnectJson.put(VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.VideoCallType.READY_TO_CONNECT.toString())
        readyToConnectJson.put(IS_SILENT, true)
        readyToConnectJson.put(USER_ID, signal?.signalUniqueUserId)
        readyToConnectJson.put(FULL_NAME, signal?.fullNameOfCalledPerson)
        readyToConnectJson.put(MESSAGE_TYPE, VIDEO_CALL)
        readyToConnectJson.put(IS_TYPING, TYPING_SHOW_MESSAGE)
        readyToConnectJson.put(MESSAGE_UNIQUE_ID, signal?.signalUniqueId)
        addTurnCredentialsAndDeviceDetails(readyToConnectJson)
    }

    fun setSignalRequirementModel(signal: Signal?) {
        this.signal = signal
    }

    fun initiateVideoCall(isSignalSilent: Boolean) {
        try {
            val startCallJson = JSONObject()
            startCallJson.put(VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.VideoCallType.START_CALL.toString())
            startCallJson.put(IS_SILENT, isSignalSilent)
            startCallJson.put(USER_ID, signal?.signalUniqueUserId)
            startCallJson.put(FULL_NAME, signal?.fullNameOfCalledPerson)
            startCallJson.put(MESSAGE_TYPE, VIDEO_CALL)
            startCallJson.put(IS_TYPING, TYPING_SHOW_MESSAGE)
            startCallJson.put(MESSAGE_UNIQUE_ID, signal?.signalUniqueId)
            addTurnCredentialsAndDeviceDetails(startCallJson)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendOfferToRemoteUser(jsonObject: JSONObject): JSONObject? {
        val offerJson = addCommonuserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(offerJson)
        return offerJson
    }

    fun sendAnswerToRemoteUser(jsonObject: JSONObject): JSONObject? {
        val offerJson = addCommonuserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(offerJson)
        return offerJson
    }

    fun sendIceCandidates(jsonObject: JSONObject) {
        val iceCandidateJson = addCommonuserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(iceCandidateJson)
    }

    fun hangUpCallDefault(isCallHungUp: Boolean) {
        val jsonObject = JSONObject()
        jsonObject.put(VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.VideoCallType.CALL_HUNG_UP.toString())
        jsonObject.put(HUNGUP_TYPE, "DEFAULT")
        if (isCallHungUp) {
            jsonObject.put(REASON, WebRTCCallConstants.CallHangupType.CALL_HANGUP)
        } else {
            jsonObject.put(REASON, WebRTCCallConstants.CallHangupType.CALL_PICKUP)
        }
        val hangupJson = addCommonuserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(hangupJson)
//        mClient?.unsubscribeAll()
    }

    fun hangUpCallSwitched() {
        val jsonObject = JSONObject()
        jsonObject.put(VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.VideoCallType.CALL_HUNG_UP.toString())
        jsonObject.put(HUNGUP_TYPE, "SWITCHED")
        jsonObject.put(REASON, WebRTCCallConstants.CallHangupType.CONF_INVITE)
        val hangupJson = addCommonuserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(hangupJson)
//        mClient?.unsubscribeAll()
    }


    fun rejectCall() {
        val jsonObject = JSONObject()
        jsonObject.put(VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.VideoCallType.CALL_REJECTED.toString())
        jsonObject.put(REASON, WebRTCCallConstants.CallHangupType.CALL_HANGUP.toString())
        val rejectedJson = addCommonuserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(rejectedJson)
//        mClient?.unsubscribeAll()
    }

    fun addCommonuserDetails(jsonObject: JSONObject): JSONObject {
        jsonObject.put(IS_SILENT, true)
        jsonObject.put(USER_ID, signal?.signalUniqueUserId)
        jsonObject.put(MESSAGE_TYPE, VIDEO_CALL)
        jsonObject.put(IS_TYPING, 0)
        jsonObject.put(IS_SILENT, true)
        jsonObject.put(MESSAGE_UNIQUE_ID, signal?.signalUniqueId)
        return jsonObject
    }

    fun addTurnCredentialsAndDeviceDetails(jsonObject: JSONObject) {
        val stunServers = JSONArray()
        val turnServers = JSONArray()
        val videoCallCredentials = JSONObject()

        videoCallCredentials.put(TURN_API_KEY, signal?.turnApiKey)
        videoCallCredentials.put(USER_NAME, signal?.turnUserName)
        videoCallCredentials.put(CREDENTIAL, signal?.turnCredential)
        for (i in signal?.stunServers!!.indices) {
            stunServers.put(signal?.stunServers!!.get(i))
        }
        for (i in signal?.turnServers!!.indices) {
            turnServers.put(signal?.turnServers!!.get(i))
        }

        videoCallCredentials.put(STUN, stunServers)
        videoCallCredentials.put(TURN, turnServers)
        jsonObject.put(CHANNEL_ID, channelId)
        jsonObject.put(TURN_CREDENTIALS, videoCallCredentials)
        jsonObject.put(DEVICE_PAYLOAD, signal?.deviceDetails)
        jsonObject.put("call_type", signal?.callType)
        publishSignalToFaye(jsonObject)
    }

    fun publishSignalToFaye(signalJson: JSONObject) {
        SocketConnection.sendMessage(signalJson)

//        if (mClient != null) {
//            mClient?.publish(channelId, "/$channelId", signalJson)
//        }
    }

    fun onBroadcastRecieved(intent: Intent) {
        if (intent.hasExtra(FuguAppConstant.CHANNEL_ID) && intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID) != signal?.signalUniqueId) {
            if (intent.getStringExtra(FuguAppConstant.VIDEO_CALL_TYPE) == FuguAppConstant.VideoCallType.START_CALL.toString()) {
                try {
                    val json = JSONObject()
                    json.put(FuguAppConstant.VIDEO_CALL_TYPE, FuguAppConstant.VideoCallType.USER_BUSY.toString())
                    json.put(FuguAppConstant.IS_SILENT, true)
                    json.put(FuguAppConstant.CHANNEL_ID, intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L))
                    json.put(FuguAppConstant.USER_ID, intent.getLongExtra(FuguAppConstant.USER_ID, -1L))
                    json.put(FuguAppConstant.FULL_NAME, signal?.fullNameOfCalledPerson)
                    json.put(FuguAppConstant.MESSAGE_TYPE, FuguAppConstant.VIDEO_CALL)
                    json.put(FuguAppConstant.IS_TYPING, TYPING_SHOW_MESSAGE)
                    json.put(FuguAppConstant.MESSAGE_UNIQUE_ID, intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID))
                    Log.e("MUID", intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID))
                    val devicePayload = JSONObject()
                    devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(videoCallService))
                    devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
                    devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
                    devicePayload.put(FuguAppConstant.DEVICE_DETAILS, com.skeleton.mvp.data.db.CommonData.deviceDetails(videoCallService))
                    json.put("device_payload", devicePayload)
                    publishSignalToFaye(json)
//                    mClient?.publish(intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L), "/" + intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L), json)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        } else if (intent.getStringExtra(FuguAppConstant.VIDEO_CALL_TYPE) == FuguAppConstant.VideoCallType.CALL_HUNG_UP.toString()) {
            if (intent.hasExtra(HUNGUP_TYPE)) {
                if (intent.getStringExtra(HUNGUP_TYPE).equals("DEFAULT")) {
                    videoCallService.onCallHungUp(null, false)
                    if (videoCallService.callDisconnectTime != null) {
                        videoCallService.callDisconnectTime?.cancel()
                    }
                } else if (intent.getStringExtra(HUNGUP_TYPE).equals("SWITCHED")) {

                }
            }
//            mClient?.unsubscribeAll()
        }
    }

    fun isFayeConnected(): Boolean {
        return isOfferrecieved
    }

    fun cancelCounter() {
        mInitiateStartCalltimer?.cancel()
    }

    private var mHungUp: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            hangUpCallDefault(true)
        }
    }

    fun reInitSockets() {
        SocketConnection.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().userInfo.accessToken,
                CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userChannel, "WebRTC", false,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.pushToken)
    }

    fun callFailed() {
        val jsonObject = JSONObject()
        jsonObject.put(VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.VideoCallType.CALL_HUNG_UP.toString())
        jsonObject.put(HUNGUP_TYPE, "DISCONNECTED")
        jsonObject.put(REASON, WebRTCCallConstants.CallHangupType.CALL_DISCONNECTED)
        val hangupJson = addCommonuserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(hangupJson)
    }

    fun acceptConf() {
        val jsonObject = JSONObject()
        jsonObject.put(VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.VideoCallType.CALL_HUNG_UP.toString())
        jsonObject.put(HUNGUP_TYPE, "SWITCHED")
        jsonObject.put(REASON, WebRTCCallConstants.CallHangupType.CONF_SWITCH)
        val hangupJson = addCommonuserDetails(jsonObject)
        addTurnCredentialsAndDeviceDetails(hangupJson)
    }

}
