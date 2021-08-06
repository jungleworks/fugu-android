package com.skeleton.mvp.socket

import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.util.Log
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject
import java.net.URISyntaxException


object SocketConnection {
    private var mSocket: Socket? = null
    private var mOptions: IO.Options? = null

    private var isSocketConnected = false
    private var enUserId: String = ""
    private var userUniqueKey: String = ""
    private var userChannel: String = ""
    private var pushChannel: String = ""
    private var accessToken: String = ""
    private var avoidLastSeen: Boolean = false
    private var socketClientCallback: SocketClientCallback? = null
    private var socketNotificationClientCallback: SocketClientCallback? = null
    private var socketCallClientCallback: SocketClientCallback? = null

    fun initSocketConnection(
            accessToken: String,
            en_user_id: String,
            user_unique_key: String,
            user_channel: String,
            callingFunction: String,
            avoidLastSeen: Boolean,
            pushChannel: String
    ) {
        this.enUserId = en_user_id
        this.userUniqueKey = user_unique_key
        this.userChannel = user_channel
        this.accessToken = accessToken
        this.avoidLastSeen = avoidLastSeen
        this.pushChannel = pushChannel

        try {
            mSocket = getSocketConnection()
            isSocketConnected = mSocket != null && mSocket!!.connected()
            if (!isSocketConnected) {
                Log.d("SocketConnection------->", "Initiate Connection => $callingFunction")
                mSocket?.connect()
                setSocketEvents()
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun getSocketConnection(): Socket? {
        if (mSocket == null) {
            mOptions = IO.Options()
            if (!avoidLastSeen) {
                mOptions?.query =
                        "en_user_id=$enUserId&user_unique_key=$userUniqueKey&device_type=1&accessToken=$accessToken"
            } else {
                mOptions?.query =
                        "en_user_id=$enUserId&user_unique_key=$userUniqueKey&device_type=1&accessToken=$accessToken&avoid_last_seen=true"
            }
            mOptions?.transports = arrayOf(WebSocket.NAME)
            mOptions?.reconnection = true

            mSocket = IO.socket(CommonData.getSocketUrl(), mOptions)
        }
        return mSocket
    }

    fun isSocketConnected(): Boolean {
        try {
            return mSocket?.connected()!!
        } catch (e: Exception) {
            return false
        }
    }

    fun disconnectSocket() {
        mSocket?.disconnect()
        mSocket = null
        Log.d(
                "SocketConnection------->",
                FuguAppConstant.SocketEvent.DISCONNECT.toString() + " to " + mSocket?.id()
        )
    }

    fun setSocketListeners(socketClientCallback: SocketClientCallback) {
        this.socketClientCallback = socketClientCallback
    }

    fun setNotificationSocketListeners(socketClientCallback: SocketClientCallback) {
        this.socketNotificationClientCallback = socketClientCallback
    }

    fun setCallSocketListeners(socketClientCallback: SocketClientCallback) {
        this.socketCallClientCallback = socketClientCallback
    }

    private fun setSocketEvents() {

        mSocket?.off(FuguAppConstant.SocketEvent.CONNECT.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.CONNECT.toString()) { args ->
            Log.d(
                    "SocketConnection------->",
                    FuguAppConstant.SocketEvent.CONNECT.toString() + " to " + mSocket?.id()
            )
            socketClientCallback?.onConnect()
            socketNotificationClientCallback?.onConnect()
            mSocket?.emit(
                    FuguAppConstant.SocketEvent.SUBSCRIBE_USER.toString(),
                    userChannel,
                    Ack { args ->
                        Log.d(
                                "SocketConnection------->",
                                FuguAppConstant.SocketEvent.SUBSCRIBE_USER.toString() + " $userChannel"
                        )
                    })

            mSocket?.emit(
                    FuguAppConstant.SocketEvent.SUBSCRIBE_PUSH.toString(),
                    pushChannel,
                    Ack { args ->
                        Log.d(
                                "SocketConnection------->",
                                FuguAppConstant.SocketEvent.SUBSCRIBE_PUSH.toString() + " $pushChannel"
                        )
                    })

        }

//        mSocket?.off(FuguAppConstant.SocketEvent.SUBSCRIBE_PUSH.toString())
//        mSocket?.on(FuguAppConstant.SocketEvent.SUBSCRIBE_PUSH.toString()){
//            args ->
//
//        }
//

        mSocket?.off(FuguAppConstant.SocketEvent.CALLING.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.CALLING.toString()) { args ->
            try {
                android.util.Log.e("Video_CONF-->", args[0].toString())
                socketClientCallback?.onCalling(args[0].toString())
                socketCallClientCallback?.onCalling(args[0].toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mSocket?.off(FuguAppConstant.SocketEvent.CONNECTING.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.CONNECTING.toString()) { args ->
            Log.d("SocketConnection------->", FuguAppConstant.SocketEvent.CONNECTING.toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.RECONNECT.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.RECONNECT.toString()) { args ->
            Log.d("SocketConnection------->", FuguAppConstant.SocketEvent.RECONNECT.toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.CONNECT_ERROR.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.CONNECT_ERROR.toString()) { args ->
            Log.d("SocketConnection------->", FuguAppConstant.SocketEvent.CONNECT_ERROR.toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.CONNECT_TIMEOUT.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.CONNECT_TIMEOUT.toString()) { args ->
            Log.d(
                    "SocketConnection------->",
                    FuguAppConstant.SocketEvent.CONNECT_TIMEOUT.toString()
            )
        }
        mSocket?.off(FuguAppConstant.SocketEvent.RECONNECT_ATTEMPT.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.RECONNECT_ATTEMPT.toString()) { args ->
            Log.d(
                    "SocketConnection------->",
                    FuguAppConstant.SocketEvent.RECONNECT_ATTEMPT.toString()
            )
        }
        mSocket?.off(FuguAppConstant.SocketEvent.RECONNECTING.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.RECONNECTING.toString()) { args ->
            Log.d("SocketConnection------->", FuguAppConstant.SocketEvent.RECONNECTING.toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.RECONNECT_ERROR.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.RECONNECT_ERROR.toString()) { args ->
            Log.d(
                    "SocketConnection------->",
                    FuguAppConstant.SocketEvent.RECONNECT_ERROR.toString()
            )
        }
        mSocket?.off(FuguAppConstant.SocketEvent.DISCONNECT.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.DISCONNECT.toString()) { args ->
            Log.e(
                    "SocketConnection------->",
                    args[0].toString() + "---" + FuguAppConstant.SocketEvent.DISCONNECT.toString()
            )
            socketClientCallback?.onDisconnect()
        }
        mSocket?.off(FuguAppConstant.SocketEvent.MESSAGE.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.MESSAGE.toString()) { args ->
            socketClientCallback?.onMessageReceived(args.get(0).toString())
            socketCallClientCallback?.onMessageReceived(args.get(0).toString())
            socketNotificationClientCallback?.onMessageReceived(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.TYPING.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.TYPING.toString()) { args ->
            socketClientCallback?.onTypingStarted(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.STOP_TYPING.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.STOP_TYPING.toString()) { args ->
            socketClientCallback?.onTypingStopped(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.READ_ALL.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.READ_ALL.toString()) { args ->
            socketClientCallback?.onReadAll(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.POLL.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.POLL.toString()) { args ->
            Log.d("SocketConnection------->", args.get(0).toString())
            socketClientCallback?.onPollVoteReceived(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.REACTION.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.REACTION.toString()) { args ->
            socketClientCallback?.onReactionReceived(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.PIN_CHAT.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.PIN_CHAT.toString()) { args ->
            socketClientCallback?.onPinChat(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.UNPIN_CHAT.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.UNPIN_CHAT.toString()) { args ->
            socketClientCallback?.onUnpinChat(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.THREAD_MESSAGE.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.THREAD_MESSAGE.toString()) { args ->
            socketClientCallback?.onThreadMessageReceived(args.get(0).toString())
            socketNotificationClientCallback?.onThreadMessageReceived(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.VIDEO_CALL.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.VIDEO_CALL.toString()) { args ->
            Log.d("SocketConnection------->", args.get(0).toString())
            socketClientCallback?.onVideoCallReceived(args.get(0).toString())
            socketCallClientCallback?.onVideoCallReceived(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.AUDIO_CALL.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.AUDIO_CALL.toString()) { args ->
            Log.d("SocketConnection------->", args.get(0).toString())
            socketClientCallback?.onAudioCallReceived(args.get(0).toString())
            socketCallClientCallback?.onAudioCallReceived(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.PRESENCE.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.PRESENCE.toString()) { args ->
            Log.d("SocketConnection------->", args.get(0).toString())
            socketClientCallback?.onPresent(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.ASSIGN_TASK.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.ASSIGN_TASK.toString()) { args ->
            Log.d("SocketConnection------->", args.get(0).toString())
            socketClientCallback?.onTaskAssigned(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.SCHEDULE_MEETING.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.SCHEDULE_MEETING.toString()) { args ->
            Log.d("SocketConnection------->", args.get(0).toString())
            socketClientCallback?.onMeetScheduled(args.get(0).toString())
        }
        mSocket?.off(FuguAppConstant.SocketEvent.UPDATE_NOTIFICATION_COUNT.toString())
        mSocket?.on(FuguAppConstant.SocketEvent.UPDATE_NOTIFICATION_COUNT.toString()) { args ->
            Log.d("SocketConnection------->", args.get(0).toString())
            socketClientCallback?.onUpdateNotificationCount(args.get(0).toString())
        }
    }

    fun sendConferenceEvent(jsonObject: JSONObject) {
        mSocket?.emit(FuguAppConstant.SocketEvent.VIDEO_CONFERENCE.toString(),
                jsonObject,
                Ack { acknowledgedMessage ->
                    if (acknowledgedMessage.get(0) != null) {
                        Log.d("SocketConnection------->", acknowledgedMessage.get(0).toString())
                    } else if (acknowledgedMessage.get(1) != null) {
                        Log.d("SocketConnection------->", acknowledgedMessage.get(1).toString())
                    }
                })
    }

    fun sendHangoutsEvent(jsonObject: JSONObject) {
        mSocket?.emit(FuguAppConstant.SocketEvent.HANGOUTS_CALL.toString(),
                jsonObject,
                Ack { acknowledgedMessage ->
                    if (acknowledgedMessage.get(0) != null) {
                        Log.d("SocketConnection------->", acknowledgedMessage.get(0).toString())
                    } else if (acknowledgedMessage.get(1) != null) {
                        Log.d("SocketConnection------->", acknowledgedMessage.get(1).toString())
                    }
                })
    }

    fun sendMessage(jsonObject: JSONObject) {
        mSocket?.emit(
                FuguAppConstant.SocketEvent.MESSAGE.toString(),
                jsonObject,
                Ack { acknowledgedMessage ->
                    if (acknowledgedMessage.get(0) != null) {
                        Log.d("SocketConnection------->", acknowledgedMessage.get(0).toString())
                        socketClientCallback?.onErrorReceived(acknowledgedMessage.get(0).toString())
                        socketCallClientCallback?.onErrorReceived(acknowledgedMessage.get(0).toString())
                    } else if (acknowledgedMessage.get(1) != null) {
                        socketClientCallback?.onMessageSent(acknowledgedMessage.get(1).toString())
                        Log.d("SocketConnection------->", acknowledgedMessage.get(1).toString())
                    }
                })
    }

    fun sendThreadMessage(jsonObject: JSONObject) {
        mSocket?.emit(
                FuguAppConstant.SocketEvent.THREAD_MESSAGE.toString(),
                jsonObject,
                Ack { acknowledgedMessage ->
                    try {
                        socketClientCallback?.onThreadMessageSent(acknowledgedMessage.get(1).toString())
                        Log.d("SocketConnection------->", acknowledgedMessage.get(1).toString())
                    } catch (e: Exception) {
                    }
                })
    }

    fun subscribeChannel(channelId: Long) {
        mSocket?.emit(
                FuguAppConstant.SocketEvent.SUBSCRIBE_CHANNEL.toString(),
                channelId,
                Ack { args ->
                    socketClientCallback?.onChannelSubscribed()
                    Log.d(
                            "SocketConnection------->",
                            FuguAppConstant.SocketEvent.SUBSCRIBE_CHANNEL.toString() + " $channelId"
                    )
                })
    }

    fun unsubscribeChannel(channelId: Long) {
        mSocket?.emit(
                FuguAppConstant.SocketEvent.UNSUBSCRIBE_CHANNEL.toString(),
                channelId,
                Ack { args ->
                    Log.d(
                            "SocketConnection------->",
                            FuguAppConstant.SocketEvent.UNSUBSCRIBE_CHANNEL.toString() + " $channelId"
                    )
                })
    }

    fun startTyping(jsonObject: JSONObject) {
        mSocket?.emit(FuguAppConstant.SocketEvent.TYPING.toString(), jsonObject)
    }

    fun stopTyping(jsonObject: JSONObject) {
        mSocket?.emit(FuguAppConstant.SocketEvent.STOP_TYPING.toString(), jsonObject)
    }

    fun readAll(jsonObject: JSONObject) {
        mSocket?.emit(FuguAppConstant.SocketEvent.READ_ALL.toString(), jsonObject)
    }

    fun sendPollVote(jsonObject: JSONObject) {
        mSocket?.emit(FuguAppConstant.SocketEvent.POLL.toString(), jsonObject)
    }

    fun reactOnMessage(jsonObject: JSONObject) {
        mSocket?.emit(FuguAppConstant.SocketEvent.REACTION.toString(), jsonObject)
    }

    fun readNotification(jsonObject: JSONObject) {
        mSocket?.emit(FuguAppConstant.SocketEvent.READ_UNREAD_NOTIFICATION.toString(), jsonObject)
    }

    fun subscribe(userId: Long) {
        mSocket?.emit(
                FuguAppConstant.SocketEvent.SUBSCRIBE_PRESENCE.toString(),
                "p_$userId",
                Ack { args ->
                    Log.d(
                            "SocketConnection------->",
                            FuguAppConstant.SocketEvent.SUBSCRIBE_PRESENCE.toString() + " p_$userId"
                    )
                })
    }

    fun unsubscribe(userId: Long) {
        mSocket?.emit(
                FuguAppConstant.SocketEvent.UNSUBSCRIBE_PRESENCE.toString(),
                "p_$userId",
                Ack { args ->
                    Log.d(
                            "SocketConnection------->",
                            FuguAppConstant.SocketEvent.UNSUBSCRIBE_PRESENCE.toString() + " p_$userId"
                    )
                })
    }

    interface SocketClientCallback {
        fun onMessageSent(messageJson: String)
        fun onThreadMessageSent(messageJson: String)
        fun onMessageReceived(messageJson: String)
        fun onTypingStarted(messageJson: String)
        fun onTypingStopped(messageJson: String)
        fun onThreadMessageReceived(messageJson: String)
        fun onReadAll(messageJson: String)
        fun onPinChat(messageJson: String)
        fun onUnpinChat(messageJson: String)
        fun onPollVoteReceived(messageJson: String)
        fun onReactionReceived(messageJson: String)
        fun onVideoCallReceived(messageJson: String)
        fun onAudioCallReceived(messageJson: String)
        fun onChannelSubscribed()
        fun onConnect()
        fun onDisconnect()
        fun onConnectError(socket: Socket, message: String)
        fun onErrorReceived(messageJson: String)
        fun onPresent(messageJson: String)
        fun onCalling(messageJson: String)
        fun onTaskAssigned(messageJson: String)
        fun onMeetScheduled(messageJson: String)
        fun onUpdateNotificationCount(messageJson: String)
    }
}
