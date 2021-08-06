package com.skeleton.mvp.pushNotification

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.CUSTOM_LABEL
import com.skeleton.mvp.ui.AppConstants.DOMAIN
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class GroupInformationNotification {

    fun groupInfoChanged(context: Context, messageJson: JSONObject) {
        Thread {
            kotlin.run {
                try {
                    val conversationMap: LinkedHashMap<Long, FuguConversation> = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                    val conversation: FuguConversation?
                    conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                    if (messageJson.has(IS_DELETED_GROUP)) {
                        val mIntent = Intent(CHANNEL_INTENT)
                        mIntent.putExtra(IS_DELETED_GROUP, true)
                        if (messageJson.has("only_admin_can_message")) {
                            mIntent.putExtra("only_admin_can_message", messageJson.getBoolean("only_admin_can_message"))
                        }
                        if (messageJson.has("message_type")) {
                            mIntent.putExtra(MESSAGE_TYPE, true)
                        }
                        mIntent.putExtra(CHANNEL_ID, conversation?.channelId)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                        val mIntent2 = Intent(PUBLIC_INTENT)
                        mIntent2.putExtra(IS_DELETED_GROUP, true)
                        if (messageJson.has("message_type")) {
                            mIntent2.putExtra(MESSAGE_TYPE, true)
                        }
                        if (messageJson.has("only_admin_can_message")) {
                            mIntent.putExtra("only_admin_can_message", messageJson.getBoolean("only_admin_can_message"))
                        }
                        mIntent2.putExtra(CHANNEL_ID, conversation?.channelId)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                    } else {
                        if (conversation != null) {
                            updateLocalConversation(messageJson, conversation, conversationMap)
                            fireChannelIntent(context)
                            firePublicIntent(messageJson, context, conversation)
                        }
                    }
                    if (messageJson.has(USER_UNIQUE_KEY) && messageJson.getString(USER_UNIQUE_KEY)
                            != CommonData.getCommonResponse().getData().getUserInfo().userId) {
                        if (messageJson.has(DOMAIN) && messageJson.getString(DOMAIN) == com.skeleton.mvp.fugudatabase.CommonData.getDomain()) {
                            NotificationCounter().addCountToLocal(Date().time / 1000L % Integer.MAX_VALUE,
                                    UUID.randomUUID().toString(),
                                    GROUP_INFO_NOTIFICATION, context, messageJson)
                        }
                    }
                    fireGroupInfoIntent(messageJson, context)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun updateLocalConversation(messageJson: JSONObject, conversation: FuguConversation,
                                        conversationMap: LinkedHashMap<Long, FuguConversation>) {
        if (messageJson.has(CUSTOM_LABEL)) {
            conversation.label = messageJson.getString(CUSTOM_LABEL)
            conversation.customLabel = ""
        }
        if (messageJson.has(CHANNEL_THUMBNAIL_URL)) {
            if (messageJson.getString(CHANNEL_THUMBNAIL_URL)
                    != "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png") {
                conversation.thumbnailUrl = messageJson.getString(CHANNEL_THUMBNAIL_URL)
            }
        }
        conversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
        conversation.message = messageJson.getString(MESSAGE)
        conversation.message_type = 5
        conversation.messageState = 1
        conversation.dateTime = messageJson.getString(DATE_TIME).replace("+00:00", ".000Z")
        conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
        ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
    }

    private fun fireGroupInfoIntent(messageJson: JSONObject, context: Context) {
        val groupInfoIntent = Intent(GROUP_INFO_INTENT)
        groupInfoIntent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
        groupInfoIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
        if (messageJson.has(CUSTOM_LABEL)) {
            groupInfoIntent.putExtra(TITLE, messageJson.getString(CUSTOM_LABEL))
        }
        groupInfoIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
        groupInfoIntent.putExtra(DATE_TIME, messageJson.getString(DATE_TIME))
        groupInfoIntent.putExtra(NOTI_MSG, messageJson.getString(MESSAGE))
        if (messageJson.has(CHANNEL_THUMBNAIL_URL)) {
            groupInfoIntent.putExtra(CHANNEL_THUMBNAIL_URL, messageJson.getString(CHANNEL_THUMBNAIL_URL))
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(groupInfoIntent)
    }

    private fun fireChannelIntent(context: Context) {
        val channelIntent = Intent(CHANNEL_INTENT)
        LocalBroadcastManager.getInstance(context).sendBroadcast(channelIntent)
    }

    private fun firePublicIntent(messageJson: JSONObject, context: Context, conversation: FuguConversation) {
        val publicIntent = Intent(PUBLIC_INTENT)
        publicIntent.putExtra(MESSAGE_UNIQUE_ID, conversation.muid)
        publicIntent.putExtra(CHANNEL_ID, conversation.channelId)
        publicIntent.putExtra(MESSAGE, conversation.message)
        publicIntent.putExtra(MESSAGE_TYPE, conversation.message_type)
        publicIntent.putExtra(DATE_TIME, conversation.dateTime)
        publicIntent.putExtra(USER_ID, messageJson.getLong(USER_ID))

        if (messageJson.has(USER_IDS_TO_MAKE_ADMIN)) {
            val usersToMakeAdmin = ArrayList<Int>()
            for (i in 0 until messageJson.getJSONArray(USER_IDS_TO_MAKE_ADMIN).length()) {
                usersToMakeAdmin.add(messageJson.getJSONArray(USER_IDS_TO_MAKE_ADMIN).optInt(i))
            }
            publicIntent.putExtra(USER_IDS_TO_MAKE_ADMIN, usersToMakeAdmin)
        }
        if (messageJson.has(USER_IDS_TO_REMOVE_ADMIN)) {
            val usersToRemoveAdmin = ArrayList<Int>()
            for (i in 0 until messageJson.getJSONArray(USER_IDS_TO_REMOVE_ADMIN).length()) {
                usersToRemoveAdmin.add(messageJson.getJSONArray(USER_IDS_TO_REMOVE_ADMIN).optInt(i))
            }
            publicIntent.putExtra(USER_IDS_TO_REMOVE_ADMIN, usersToRemoveAdmin)
        }
        if (messageJson.has(CHAT_TYPE)) {
            publicIntent.putExtra(CHAT_TYPE, messageJson.getInt(CHAT_TYPE))
        }
        if (messageJson.has("messgae_type")) {
            publicIntent.putExtra(MESSAGE_TYPE, true)
        }
        if(messageJson.has("only_admin_can_message")){
            publicIntent.putExtra("only_admin_can_message",messageJson.getBoolean("only_admin_can_message"))
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(publicIntent)
    }
}