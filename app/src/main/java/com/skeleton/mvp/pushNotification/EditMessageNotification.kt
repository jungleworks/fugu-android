package com.skeleton.mvp.pushNotification

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.UnreadCount
import com.skeleton.mvp.ui.AppConstants
import org.json.JSONException
import org.json.JSONObject

class EditMessageNotification {
    var isSpecialPush = false
    fun editNotification(data: Map<String, String>, context: Context,
                         messageJson: JSONObject, priority: Int,
                         smallIcon: Int) {
        val editMessageIntent = Intent(EDIT_MESSAGE_INTENT)
        editMessageIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
        editMessageIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
        if (messageJson.has(THREAD_MUID)) {
            editMessageIntent.putExtra(THREAD_MUID, messageJson.getString(THREAD_MUID))
        }
        editMessageIntent.putExtra(MESSAGE, messageJson.getString(MESSAGE).trim())
        editMessageIntent.putExtra(IS_THREAD_MESSAGE, messageJson.getBoolean(IS_THREAD_MESSAGE))
        LocalBroadcastManager.getInstance(context).sendBroadcast(editMessageIntent)
        if (messageJson.has(TAGGED_USERS)) {
            for (i in 0 until messageJson.getJSONArray(TAGGED_USERS).length()) {
                if (messageJson.getJSONArray(TAGGED_USERS).optInt(i)
                        == CommonData.getWorkspaceResponse(messageJson.getString(AppConstants.APP_SECRET_KEY)).userId.toInt()
                        || messageJson.getJSONArray(TAGGED_USERS).optInt(i) == -1) {
                    isSpecialPush = true
                    object : Thread() {
                        override fun run() {
                            super.run()
                            val unreadCountArrayList = CommonData.getNotificationCountList()
                            try {
                                var isThread = false
                                if (messageJson.has(IS_THREAD_MESSAGE) && messageJson.getBoolean(IS_THREAD_MESSAGE)) {
                                    isThread = true
                                }
                                if (messageJson.has(UPDATE_NOTIFICATION_COUNT) && messageJson.getBoolean(UPDATE_NOTIFICATION_COUNT)) {
                                    val unreadCount = UnreadCount(messageJson.getLong(CHANNEL_ID),
                                            messageJson.getString(MESSAGE_UNIQUE_ID), TEXT_MESSAGE,
                                            true, isThread)
                                    unreadCountArrayList.add(unreadCount)
                                    CommonData.setNotificationsCountList(unreadCountArrayList)
                                    val mIntent2 = Intent(NOTIFICATION_COUNTER_INTENT)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                                } else if (!messageJson.has(UPDATE_NOTIFICATION_COUNT)) {
                                    val unreadCount = UnreadCount(messageJson.getLong(CHANNEL_ID),
                                            messageJson.getString(MESSAGE_UNIQUE_ID),
                                            TEXT_MESSAGE, true, isThread)
                                    unreadCountArrayList.add(unreadCount)
                                    CommonData.setNotificationsCountList(unreadCountArrayList)
                                    val mIntent2 = Intent(NOTIFICATION_COUNTER_INTENT)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }.start()

                    break
                }
            }
        } else {
            isSpecialPush = false
        }
        var isSilent = false
        try {
            if (messageJson.has(SHOW_PUSH) && messageJson.getInt(SHOW_PUSH) == 0) {
                isSilent = true
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (!isSilent) {
//            showPush(messageJson, context, messageJson.getString(NOTI_MSG), data, priority)
            MultipleMessageNotification().publishNotification(messageJson, context,
                    messageJson.getString(NOTI_MSG), data,
                    priority, smallIcon, isSpecialPush)
            //Show Notification
        }
    }
}