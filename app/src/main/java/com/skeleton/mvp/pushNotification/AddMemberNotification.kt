package com.skeleton.mvp.pushNotification

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.DOMAIN
import org.json.JSONObject
import java.util.*

class AddMemberNotification {
    fun memberAddedToGroup(context: Context, messageJson: JSONObject) {
        Thread {
            kotlin.run {
                try {
                    val mIntent = Intent(USER_ADDED_INTENT)
                    mIntent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
                    mIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                    mIntent.putExtra(MEMBERS_INFO, messageJson.getJSONArray(MEMBERS_INFO).toString())
                    mIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
                    mIntent.putExtra(NOTI_MSG, messageJson.getString(MESSAGE))
                    mIntent.putExtra(CHAT_TYPE, messageJson.getInt(CHAT_TYPE))
                    try {
                        if (messageJson.has(ADDED_MEMBER_INFO)
                                && messageJson.getJSONObject(ADDED_MEMBER_INFO).getString(USER_UNIQUE_KEY)
                                == com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().userId) {
                            if (messageJson.has(DOMAIN) && messageJson.getString(DOMAIN) == CommonData.getDomain()) {
                                NotificationCounter().addCountToLocal(Date().time / 1000L % Integer.MAX_VALUE, UUID.randomUUID().toString(), ADD_MEMBER_NOTIFICATION, context, messageJson)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (messageJson.has(AppConstants.CUSTOM_LABEL)) {
                        mIntent.putExtra(AppConstants.CUSTOM_LABEL, messageJson.getString(AppConstants.CUSTOM_LABEL))
                    }
                    if (messageJson.has(AppConstants.LABEL)) {
                        mIntent.putExtra(AppConstants.LABEL, messageJson.getString(AppConstants.LABEL))
                    }

                    if (messageJson.has(ADDED_MEMBER_INFO)) {
                        mIntent.putExtra(ADDED_MEMBER_ID, messageJson.getJSONObject(ADDED_MEMBER_INFO).getLong(USER_ID))
                        mIntent.putExtra(ADDED_MEMBER_NAME, messageJson.getJSONObject(ADDED_MEMBER_INFO).getString(FULL_NAME))
                        mIntent.putExtra(ADDED_MEMBER_IMAGE, messageJson.getJSONObject(ADDED_MEMBER_INFO).getString(USER_IMAGE))
                    }
                    mIntent.putExtra(DATE_TIME, messageJson.getString(DATE_TIME))
                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                    val conversation: FuguConversation?
                    conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                    if (conversation != null) {
                        conversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                        conversation.message = messageJson.getString(MESSAGE)
                        conversation.message_type = 5
                        conversation.messageState = 1
                        conversation.dateTime = messageJson.getString(DATE_TIME).replace("+00:00", ".000Z")
                        conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                        ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                        val mIntent = Intent(CHANNEL_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}