package com.skeleton.mvp.pushNotification

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguConversation
import org.json.JSONObject

class ClearConversationNotification {
    fun clearConversation(context: Context, messageJson: JSONObject) {
        Thread {
            kotlin.run {
                try {
                    val conversationMap: LinkedHashMap<Long, FuguConversation> = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                    val conversation: FuguConversation?
                    conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                    if (conversation != null) {
                        conversationMap.remove(messageJson.getLong(CHANNEL_ID))
                        ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                        val mIntent = Intent(CHANNEL_INTENT)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    }
                    var pos = 0
                    ChatDatabase.setMessageList(ArrayList(), messageJson.getLong(CHANNEL_ID))
                    if (CommonData.getConversationList(messageJson.getString(APP_SECRET_KEY)) != null) {
                        val conversationsMap: LinkedHashMap<Long, FuguConversation> = CommonData.getConversationList(messageJson.getString(APP_SECRET_KEY))
                        val conversations: ArrayList<FuguConversation> = ArrayList(CommonData.getConversationList(messageJson.getString(APP_SECRET_KEY)).values)

                        for (i in conversations.indices) {
                            if (conversations[i].channelId!!.compareTo(messageJson.getLong(CHANNEL_ID)) == 0) {
                                conversations.removeAt(i)
                                conversationsMap.remove(messageJson.getLong(CHANNEL_ID))
                                CommonData.setConversationList(messageJson.getString(APP_SECRET_KEY), conversationsMap)
                                pos = i
                                break
                            }
                        }
                        val mIntent = Intent(CLEAR_INTENT)
                        mIntent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
                        mIntent.putExtra(POSITION, pos)
                        mIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}