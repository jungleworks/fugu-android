package com.skeleton.mvp.pushNotification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.DATA
import com.skeleton.mvp.service.FuguPushIntentService
import com.skeleton.mvp.ui.AppConstants.DOMAIN
import com.skeleton.mvp.util.Utils
import org.json.JSONObject
import java.util.*

class NewSpaceNotification {
    fun addedToNewSpace(data: Map<String, String>,
                        context: Context,
                        messageJson: JSONObject,
                        priority: Int, smallIcon: Int) {

        val mBundle = Bundle()
        for (key in data.keys) {
            mBundle.putString(key, data[key])
        }

        val domain: String = Utils.getDomain()
        if (messageJson.has(DOMAIN) && messageJson.getString(DOMAIN) == domain) {
            NotificationCounter().addCountToLocal(Date().time / 1000L % Integer.MAX_VALUE,
                    UUID.randomUUID().toString(), FuguAppConstant.NEW_WORKSPACE_NOTIFICATION,
                    context, messageJson)
        }

        val notificationIntent = Intent(context, FuguPushIntentService::class.java)
        notificationIntent.putExtra("data", mBundle)
        SingleMessageNotification().publishNotification(context,
                notificationIntent,
                messageJson,
                priority, smallIcon)
    }
}