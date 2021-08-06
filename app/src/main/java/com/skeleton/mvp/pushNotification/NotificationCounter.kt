package com.skeleton.mvp.pushNotification

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.UnreadCount
import org.json.JSONObject
import java.util.ArrayList

class NotificationCounter {
    fun addCountToLocal(channelId: Long, messageUniqueId: String, notificationType: Int, context: Context, messageJson: JSONObject) {
        object : Thread() {
            override fun run() {
                super.run()
                try {
                    if (messageJson.has(UPDATE_NOTIFICATION_COUNT) && messageJson.getBoolean(UPDATE_NOTIFICATION_COUNT)) {
                        val unreadCountArrayList: ArrayList<UnreadCount> = CommonData.getNotificationCountList()
                        val unreadCount = UnreadCount(channelId, messageUniqueId, notificationType, false, false)
                        unreadCountArrayList.add(unreadCount)
                        CommonData.setNotificationsCountList(unreadCountArrayList)
                        val mIntent2 = Intent(NOTIFICATION_COUNTER_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                    } else if (!messageJson.has(UPDATE_NOTIFICATION_COUNT)) {
                        val unreadCountArrayList: ArrayList<UnreadCount> = CommonData.getNotificationCountList()
                        val unreadCount = UnreadCount(channelId, messageUniqueId, notificationType, false, false)
                        unreadCountArrayList.add(unreadCount)
                        CommonData.setNotificationsCountList(unreadCountArrayList)
                        val mIntent2 = Intent(NOTIFICATION_COUNTER_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }.start()
    }

}