package com.skeleton.mvp.pushNotification

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.CommonData
import org.json.JSONObject

class DeactivateUserNotification {
    fun deactivateUserNotification(context: Context, messageJson: JSONObject) {
        if (foregrounded()) {
            val deactivateUserIntent = Intent(DEACTIVATE_USER_INTENT)
            deactivateUserIntent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
            LocalBroadcastManager.getInstance(context).sendBroadcast(deactivateUserIntent)
        } else {
            if (messageJson.has(APP_SECRET_KEY)) {
                val fcCommonResponse = CommonData.getCommonResponse()
                if (CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey == messageJson.getString(APP_SECRET_KEY)) {
                    if (fcCommonResponse.getData().workspacesInfo.size > 0) {
                        fcCommonResponse.data.workspacesInfo.remove(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()])
                        CommonData.setCommonResponse(fcCommonResponse)
                    } else {
                        FuguConfig.clearFuguData(null)
                        CommonData.clearData()
                    }
                } else {
                    val fcCommonResponse = CommonData.getCommonResponse()
                    if (fcCommonResponse.getData().workspacesInfo.size > 0) {
                        fcCommonResponse.data.workspacesInfo.remove(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()])
                        CommonData.setCommonResponse(fcCommonResponse)
                    } else {
                        FuguConfig.clearFuguData(null)
                        CommonData.clearData()
                    }
                }
            }
        }
    }

    private fun foregrounded(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
    }
}