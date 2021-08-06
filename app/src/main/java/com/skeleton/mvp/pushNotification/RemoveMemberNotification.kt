package com.skeleton.mvp.pushNotification

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.creategroup.MembersInfo
import com.skeleton.mvp.model.FuguConversation
import org.json.JSONArray
import org.json.JSONObject
import java.util.LinkedHashMap

class RemoveMemberNotification {
    fun removeMember(context: Context, messageJson: JSONObject) {
        fireUserRemovedIntent(context, messageJson)
        Thread {
            kotlin.run {
                try {
                    val conversationMap: LinkedHashMap<Long, FuguConversation> = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                    val conversation: FuguConversation?
                    conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                    if (conversation != null) {
                        val removedUserId: Long = messageJson.getLong(REMOVED_USER_ID)
                        if (removedUserId.compareTo(CommonData.getCommonResponse()
                                        .data.workspacesInfo[CommonData.getCurrentSignedInPosition()].userId.toLong()) == 0) {
                            Thread {
                                kotlin.run {
                                    conversationMap.remove(messageJson.getLong(CHANNEL_ID))
                                    ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                                    val mIntent = Intent(CHANNEL_INTENT)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                                }
                            }.start()
                        } else {
                            val jsonArray: JSONArray?
                            val membersInfos = java.util.ArrayList<MembersInfo>()
                            jsonArray = JSONArray(messageJson.getJSONArray(MEMBERS_INFO).toString())
                            for (i in 0 until jsonArray.length()) {
                                membersInfos.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(),
                                        MembersInfo::class.java))
                            }
                            conversation.membersInfo = membersInfos
                            conversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                            conversation.message = messageJson.getString(MESSAGE)
                            conversation.message_type = 5
                            conversation.messageState = 1
                            conversation.dateTime = messageJson.getString(DATE_TIME)
                            conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                            ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                            val mIntent = Intent(CHANNEL_INTENT)
                            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }


    private fun fireUserRemovedIntent(context: Context, messageJson: JSONObject) {
        try {
            val mIntent = Intent(USER_REMOVED_INTENT)
            mIntent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
            mIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
            mIntent.putExtra(MEMBERS_INFO, messageJson.getJSONArray(MEMBERS_INFO).toString())
            mIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
            mIntent.putExtra(DATE_TIME, messageJson.getString(DATE_TIME))
            mIntent.putExtra(NOTI_MSG, messageJson.getString(MESSAGE))
            mIntent.putExtra(REMOVED_USER_ID, messageJson.getLong(REMOVED_USER_ID))
            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}