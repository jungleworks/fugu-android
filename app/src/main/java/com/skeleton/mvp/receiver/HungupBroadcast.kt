package com.skeleton.mvp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.APP_SECRET_KEY
import com.skeleton.mvp.constant.FuguAppConstant.DEVICE_PAYLOAD
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.service.ConferenceCallService
import com.skeleton.mvp.service.OngoingCallService
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.ui.UniqueIMEIID
import com.skeleton.mvp.videoCall.WebRTCCallConstants
import org.json.JSONObject

class HungUpBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.getStringExtra("action").equals("rejectCall")) {
            context?.stopService(Intent(context, OngoingCallService::class.java))
            context?.stopService(Intent(context, ConferenceCallService::class.java))

            val startCallJson = JSONObject()
            startCallJson.put(FuguAppConstant.IS_SILENT, true)
            startCallJson.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.JitsiCallType.REJECT_CONFERENCE.toString())
            val workspaceInfoList =
                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
            var userId = workspaceInfoList[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId
            for (workspaceInfo in workspaceInfoList) {
                if (workspaceInfo.fuguSecretKey == intent?.getStringExtra(APP_SECRET_KEY)) {
                    userId = workspaceInfo.userId
                    break
                }
            }
            startCallJson.put(FuguAppConstant.USER_ID, userId)
            startCallJson.put(FuguAppConstant.CHANNEL_ID, intent?.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L))
            startCallJson.put(FuguAppConstant.MESSAGE_TYPE, WebRTCCallConstants.VIDEO_CALL)
            startCallJson.put(FuguAppConstant.CALL_TYPE, "VIDEO")
            startCallJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, intent?.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID))
            startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, intent?.getStringExtra(DEVICE_PAYLOAD))
            startCallJson.put(WebRTCCallConstants.DEVICE_ID,UniqueIMEIID.getUniqueIMEIId(context))
            startCallJson.put(FuguAppConstant.INVITE_LINK, intent?.getStringExtra(FuguAppConstant.INVITE_LINK))
            if (intent?.hasExtra(DEVICE_PAYLOAD)!!) {
                SocketConnection.sendMessage(startCallJson)
            }

            OngoingCallService.NotificationServiceState.isConferenceServiceRunning = false
            OngoingCallService.NotificationServiceState.isConferenceConnected = false
        }
    }
}