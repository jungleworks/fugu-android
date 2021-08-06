package com.skeleton.mvp.pushNotification

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguConversation
import org.json.JSONObject

@Suppress("NAME_SHADOWING")
class DeleteMessageNotification {
    fun deleteMessage(context: Context, messageJson: JSONObject) {
        Thread {
            kotlin.run {
                try {
                    val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                    val conversation: FuguConversation?
                    conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                    if (messageJson.has(MESSAGE_UNIQUE_ID) && messageJson.getString(MESSAGE_UNIQUE_ID).equals(conversation?.muid)) {
                        if (conversation != null) {
                            if (conversation.last_sent_by_id.toLong().compareTo(com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                                            .data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toLong()) == 0) {
                                conversation.message = context.resources.getString(R.string.you_deleted_this_message)
                            } else {
                                conversation.message = context.resources.getString(R.string.this_message_was_deleted)
                            }
                            conversation.message_type = 5
                            conversation.messageState = 0
                            conversation.dateTime = messageJson.getString(DATE_TIME).replace("+00:00", ".000Z")
                            conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                            ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                            val mIntent = Intent(CHANNEL_INTENT)
                            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                        } else {

                        }
                    }
                    if (messageJson.has(MESSAGE_UNIQUE_ID)) {
                        val pos = 0
                        if (CommonData.getConversationList(messageJson.getString(APP_SECRET_KEY)) != null) {
                            val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                            val conversation: FuguConversation?
                            conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                            if (conversation?.last_sent_by_id!!.compareTo(java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)) == 0) {
                                conversation.message = context.resources.getString(R.string.you_deleted_this_message)
                            } else {
                                conversation.message = context.resources.getString(R.string.this_message_was_deleted)
                            }
                            conversation.messageState = 0
                            conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                            ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                        }
                        val messageList = ChatDatabase.getMessageList(messageJson.getLong(CHANNEL_ID))
                        var text: String
                        var rowtype: Int
                        for (i in messageList.indices) {
                            if (messageList[i].uuid == messageJson.getString(MESSAGE_UNIQUE_ID)) {
                                text = if (messageList[i].rowType == TEXT_MESSGAE_SELF ||
                                        messageList[i].rowType == IMAGE_MESSGAE_SELF ||
                                        messageList[i].rowType == FILE_MESSGAE_SELF ||
                                        messageList[i].rowType == VIDEO_MESSGAE_SELF ||
                                        messageList[i].rowType == MESSAGE_DELETED_SELF) {
                                    context.resources.getString(R.string.you_deleted_this_message)
                                } else {
                                    context.resources.getString(R.string.this_message_was_deleted)
                                }
                                rowtype = if (messageList[i].rowType == TEXT_MESSGAE_SELF ||
                                        messageList[i].rowType == IMAGE_MESSGAE_SELF ||
                                        messageList[i].rowType == FILE_MESSGAE_SELF ||
                                        messageList[i].rowType == VIDEO_MESSGAE_SELF ||
                                        messageList[i].rowType == MESSAGE_DELETED_SELF) {
                                    MESSAGE_DELETED_SELF
                                } else {
                                    MESSAGE_DELETED_OTHER
                                }
                                messageList[i].message = text
                                messageList[i].rowType = rowtype
                            }
                        }
                        ChatDatabase.setMessageList(messageList, messageJson.getLong(CHANNEL_ID))
                        val mIntent = Intent(DELETE_INTENT)
                        mIntent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
                        mIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                        mIntent.putExtra(MESSAGE_UNIQUE_ID, messageJson.getString(MESSAGE_UNIQUE_ID))
                        mIntent.putExtra(POSITION, pos)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    } else {
                        val mIntent = Intent(THREAD_DELETE_INTENT)
                        mIntent.putExtra(APP_SECRET_KEY, messageJson.getString(APP_SECRET_KEY))
                        mIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                        mIntent.putExtra(THREAD_MUID, messageJson.getString(THREAD_MUID))
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }.start()
    }
}