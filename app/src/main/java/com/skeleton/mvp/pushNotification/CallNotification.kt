package com.skeleton.mvp.pushNotification

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.service.VideoCallService
import com.skeleton.mvp.util.GeneralFunctions
import com.skeleton.mvp.utils.stopCallForegroundService
import org.json.JSONObject

class CallNotification {
    fun newVideoCall(context: Context, messageJson: JSONObject) {
        val videoCallIntent = Intent(VIDEO_CALL_INTENT)
        videoCallIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
        videoCallIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
        videoCallIntent.putExtra(VIDEO_CALL_TYPE, messageJson.getString(VIDEO_CALL_TYPE))
        if (messageJson.has(HUNGUP_TYPE)) {
            videoCallIntent.putExtra(HUNGUP_TYPE, messageJson.getString(HUNGUP_TYPE))
        }
        val workspacesInfos = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo as java.util.ArrayList<WorkspacesInfo>
        var userId = -1L
        for (i in workspacesInfos.indices) {
            if (workspacesInfos[i].fuguSecretKey == messageJson.getString(APP_SECRET_KEY)) {
                userId = java.lang.Long.valueOf(workspacesInfos[i].userId)
                break
            }
        }
        videoCallIntent.putExtra(USER_ID, userId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(videoCallIntent)
        val mngr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskList = mngr.getRunningTasks(10)
        if (GeneralFunctions().isMyServiceRunning(VideoCallService::class.java.simpleName,context) && messageJson.getString(HUNGUP_TYPE).equals("DEFAULT")){
            context.stopCallForegroundService(false)
        }
    }
}