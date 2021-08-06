package com.skeleton.mvp.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant.ANDROID_DEVICE_ID
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.util.Foreground
import com.skeleton.mvp.utils.UniqueIMEIID
import io.socket.client.Socket
import org.json.JSONObject

object NotificationSockets : NotificationService.NotificationHandler {

    private var mContext: Context? = null
    private var socketDisconnectTimer: CountDownTimer? = null
    private var countDownTime = 90000L
    private val fuguNotificationConfig = PushReceiver().getInstance()
    var notificationSockets: NotificationSockets? = null
    var initAPi = false
    private var notificationsArray = ArrayList<String>()
    fun init(context: Context, initAPI: Boolean) {
        if (this.mContext == null) {
            try {
                countDownTime = CommonData.getCommonResponse().data.fuguConfig.socketTimeout * 1000L
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (countDownTime.compareTo(0) == 0) {
                countDownTime = 90000L
            }
            this.mContext = context
            this.initAPi = initAPI
            Handler(Looper.getMainLooper()).postDelayed({
                Handler(Looper.getMainLooper()).postDelayed({
                    socketDisconnectTimer = object : CountDownTimer(countDownTime, 1000) {
                        override fun onFinish() {
                            Log.e("countDownTime", countDownTime.toString())
                            if (Foreground.get().isBackground) {
                                mContext = null
                                SocketConnection.disconnectSocket()
                            } else {
                                Log.e("countDownTime", countDownTime.toString())
                                socketDisconnectTimer?.start()
                            }
                        }

                        override fun onTick(millisUntilFinished: Long) {
                        }

                    }.start()
                }, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fuguNotificationConfig.setSmallIcon(R.drawable.notification_white)
                } else {
                    fuguNotificationConfig.setSmallIcon(R.drawable.ic_fugu)
                }
                try {
                    SocketConnection.initSocketConnection(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                            CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId,
                            CommonData.getCommonResponse().getData().getUserInfo().userId,
                            CommonData.getCommonResponse().getData().getUserInfo().userChannel, "Notification Receiver", true,
                            CommonData.getCommonResponse().getData().getUserInfo().pushToken)

                    SocketConnection.setNotificationSocketListeners(object : SocketConnection.SocketClientCallback {

                        override fun onCalling(messageJson: String) {}

                        override fun onPresent(messageJson: String) {}

                        override fun onMessageSent(messageJson: String) {}

                        override fun onThreadMessageSent(messageJson: String) {}

                        override fun onMessageReceived(messageJson: String) {
                            Log.e("Message-->", messageJson)
                            try {
                                val messageJSON = JSONObject(messageJson)
                                if (messageJSON.has("last_notification_id")) {
                                    CommonData.setLocalNotificationId(messageJSON.getLong("last_notification_id"))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            try {
                                val messageJSON = JSONObject(messageJson)
                                val userUniqueKey = messageJSON.getString("user_unique_key")

                                if (!userUniqueKey.equals(CommonData.getCommonResponse().data.workspacesInfo[0].userUniqueKey)) {
                                    if (!messageJSON.has(ANDROID_DEVICE_ID)) {
                                        notificationsArray.add(messageJson)
                                    } else if (messageJSON.has(ANDROID_DEVICE_ID) && messageJSON.getString(ANDROID_DEVICE_ID).equals(UniqueIMEIID.getUniqueIMEIId(mContext))) {
                                        notificationsArray.add(messageJson)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            socketDisconnectTimer?.cancel()
                            Handler(Looper.getMainLooper()).postDelayed({
                                socketDisconnectTimer = object : CountDownTimer(countDownTime, 1000) {
                                    override fun onFinish() {
                                        if (Foreground.get().isBackground) {
                                            mContext = null
                                            SocketConnection.disconnectSocket()
                                        } else {
                                            socketDisconnectTimer?.start()
                                        }
                                    }

                                    override fun onTick(millisUntilFinished: Long) {
                                    }

                                }.start()
                            }, 0)
                            publishFirstNotification()
                        }

                        override fun onTypingStarted(messageJson: String) {}

                        override fun onTypingStopped(messageJson: String) {}

                        override fun onThreadMessageReceived(messageJson: String) {
                            Log.e("Thread Message-->", messageJson)
                            try {
                                val messageJSON = JSONObject(messageJson)
                                if (messageJSON.has("last_notification_id")) {
                                    CommonData.setLocalNotificationId(messageJSON.getLong("last_notification_id"))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            try {
                                val messageJSON = JSONObject(messageJson)
                                val userUniqueKey = messageJSON.getString("user_unique_key")
                                if (!userUniqueKey.equals(CommonData.getCommonResponse().data.workspacesInfo[0].userUniqueKey)) {
                                    if (!messageJSON.has(ANDROID_DEVICE_ID)) {
                                        notificationsArray.add(messageJson)
                                    } else if (messageJSON.has(ANDROID_DEVICE_ID) && messageJSON.getString(ANDROID_DEVICE_ID).equals(UniqueIMEIID.getUniqueIMEIId(mContext))) {
                                        notificationsArray.add(messageJson)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            socketDisconnectTimer?.cancel()
                            Handler(Looper.getMainLooper()).postDelayed({
                                socketDisconnectTimer = object : CountDownTimer(countDownTime, 1000) {
                                    override fun onFinish() {
                                        if (Foreground.get().isBackground) {
                                            mContext = null
                                            SocketConnection.disconnectSocket()
                                        } else {
                                            socketDisconnectTimer?.start()
                                        }
                                    }

                                    override fun onTick(millisUntilFinished: Long) {
                                    }

                                }.start()
                            }, 0)
                            publishFirstNotification()
                        }

                        override fun onReadAll(messageJson: String) {}

                        override fun onPinChat(messageJson: String) {}

                        override fun onUnpinChat(messageJson: String) {}

                        override fun onPollVoteReceived(messageJson: String) {}

                        override fun onReactionReceived(messageJson: String) {}

                        override fun onVideoCallReceived(messageJson: String) {}

                        override fun onAudioCallReceived(messageJson: String) {}

                        override fun onChannelSubscribed() {}

                        override fun onConnect() {
                            if (initAPi) {
                                if (!NotificationService.NotificationServiceState.isServiceRunning) {
                                    val startIntent = Intent(context, NotificationService::class.java)
                                    startIntent.action = "com.officechat.notification.start"
                                    ContextCompat.startForegroundService(context, startIntent)
                                } else {
                                    Log.d(NotificationService::class.simpleName, ": Already Running")
                                }
                            }
                        }

                        override fun onDisconnect() {}

                        override fun onConnectError(socket: Socket, message: String) {}

                        override fun onErrorReceived(messageJson: String) {}

                        override fun onMeetScheduled(messageJson: String) {}

                        override fun onUpdateNotificationCount(messageJson: String) {}

                        override fun onTaskAssigned(messageJson: String) {}
                    })
                } catch (e: Exception) {
                }
            }, 500)
        }
    }

    fun setInitApi(initAPi: Boolean) {
        this.initAPi = initAPi
    }

    override fun onNotificationsPublished() {
        Log.e("NotificationsPublished", "onNotificationsPublished")
        publishFirstNotification()
    }

    private fun publishFirstNotification() {
        try {
            val pushMap = LinkedHashMap<String, String>()
            if (notificationsArray.size > 0) {
                val notification = notificationsArray[0]
                notificationsArray.removeAt(0)
                pushMap["message"] = notification
                try {
                    if (JSONObject(pushMap["message"]).getInt("notification_type") != 16) {
                        fuguNotificationConfig.pushRedirection(mContext!!, pushMap, true)
                    }
                } catch (E: Exception) {
                    fuguNotificationConfig.pushRedirection(mContext!!, pushMap, true)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    publishFirstNotification()
                }, 200)
            }
        } catch (e: Exception) {

        }
    }
}