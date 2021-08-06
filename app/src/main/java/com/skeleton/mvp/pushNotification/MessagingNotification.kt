package com.skeleton.mvp.pushNotification

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.model.creategroup.MembersInfo
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.model.UnreadCount
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.util.FormatStringUtil
import org.json.JSONException
import org.json.JSONObject

class MessagingNotification {
    var isThread = false
    var isSpecialPush = false
    fun showNotification(data: Map<String, String>,
                         context: Context, messageJson: JSONObject, priority: Int,
                         smallIcon: Int) {

        val mIntent = Intent(NOTIFICATION_INTENT)
        val dataBundle = Bundle()
        for (key in data.keys) {
            dataBundle.putString(key, data[key])
        }
        mIntent.putExtras(dataBundle)
        if (messageJson.has(IS_THREAD_MESSAGE) && !messageJson.getBoolean(IS_THREAD_MESSAGE)) {
            isThread = true
            Thread {
                kotlin.run {
                    try {
                        val unreadCountArrayList = CommonData.getNotificationCountList()
                        var isChannelPresent = false
                        for (i in unreadCountArrayList.indices) {
                            if (unreadCountArrayList[i].channelId!!.compareTo(messageJson.getLong(CHANNEL_ID)) == 0
                                    && !unreadCountArrayList[i].isThreadMessage && !unreadCountArrayList[i].isTagged) {
                                isChannelPresent = true
                                break
                            }
                        }
                        if (!isChannelPresent && !messageJson.has(TAGGED_USERS)) {
                            if (messageJson.has(UPDATE_NOTIFICATION_COUNT)
                                    && messageJson.getBoolean(UPDATE_NOTIFICATION_COUNT)) {
                                val unreadCount = UnreadCount(messageJson.getLong(CHANNEL_ID),
                                        messageJson.getString(MESSAGE_UNIQUE_ID), TEXT_MESSAGE,
                                        false, false)
                                unreadCountArrayList.add(unreadCount)
                                CommonData.setNotificationsCountList(unreadCountArrayList)
                                val mIntent2 = Intent(NOTIFICATION_COUNTER_INTENT)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                            } else if (!messageJson.has(UPDATE_NOTIFICATION_COUNT)) {
                                val unreadCount = UnreadCount(messageJson.getLong(CHANNEL_ID),
                                        messageJson.getString(MESSAGE_UNIQUE_ID), TEXT_MESSAGE,
                                        false, false)
                                unreadCountArrayList.add(unreadCount)
                                CommonData.setNotificationsCountList(unreadCountArrayList)
                                val mIntent2 = Intent(NOTIFICATION_COUNTER_INTENT)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                            }
                        }

                        val conversationMap = ChatDatabase.getConversationMap(messageJson.getString(APP_SECRET_KEY))
                        val currentConversation: FuguConversation?
                        currentConversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                        if (currentConversation != null) {
                            var new_message: String
                            if (messageJson.has(FuguAppConstant.NEW_MESSAGE)) {
                                new_message = messageJson.getString(FuguAppConstant.NEW_MESSAGE)
                            } else {
                                new_message = ""
                            }

                            currentConversation.message = new_message
                            currentConversation.message = FormatStringUtil.FormatString.getFormattedString(new_message)[1]
                            currentConversation.dateTime = messageJson.getString(DATE_TIME).replace("+00:00", ".000Z")
                            currentConversation.message_type = messageJson.getInt(MESSAGE_TYPE)
                            currentConversation.chat_type = messageJson.getInt(CHAT_TYPE)
                            if (messageJson.getInt(MESSAGE_TYPE) == PUBLIC_NOTE) {
                                currentConversation.unreadCount = currentConversation.unreadCount
                            } else {
                                if (PushReceiver.PushChannel.pushChannelId?.compareTo(messageJson.getLong(CHANNEL_ID)) != 0) {
                                    currentConversation.unreadCount = currentConversation.unreadCount.plus(1)
                                }
                            }
                            currentConversation.last_sent_by_id = messageJson.getLong(LAST_SENT_BY_ID)
                            currentConversation.last_sent_by_full_name = messageJson.getString(LAST_SENT_BY_FULL_NAME)
                            currentConversation.messageState = 1
                            currentConversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                            val membersInfoList = currentConversation.membersInfo
                            var isPersonAlreadyPresent = false
                            if (membersInfoList != null && membersInfoList.size > 0) {
                                for (i in 0 until membersInfoList.size) {
                                    if (membersInfoList[i].userId.compareTo(messageJson.getLong(LAST_SENT_BY_ID)) == 0) {
                                        isPersonAlreadyPresent = true
                                        val memberInfo = membersInfoList[i]
                                        membersInfoList[i] = membersInfoList[0]
                                        membersInfoList[0] = memberInfo
                                    }
                                }
                                if (!isPersonAlreadyPresent) {
                                    when (membersInfoList.size) {
                                        2 -> {
                                            membersInfoList[1] = membersInfoList[0]
                                            val membersInfo = MembersInfo()
                                            membersInfo.userId = messageJson.getLong(LAST_SENT_BY_ID)
                                            membersInfo.fullName = messageJson.getString(LAST_SENT_BY_FULL_NAME)
                                            membersInfo.userImage = messageJson.getString(USER_THUMBNAIL_IMAGE)
                                            membersInfoList[0] = membersInfo
                                        }
                                        3 -> {
                                            membersInfoList[2] = membersInfoList[1]
                                            membersInfoList[1] = membersInfoList[0]
                                            val membersInfo = MembersInfo()
                                            membersInfo.userId = messageJson.getLong(LAST_SENT_BY_ID)
                                            membersInfo.fullName = messageJson.getString(LAST_SENT_BY_FULL_NAME)
                                            membersInfo.userImage = messageJson.getString(USER_THUMBNAIL_IMAGE)
                                            membersInfoList[0] = membersInfo
                                        }
                                        else -> {
                                        }
                                    }
                                }
                            }

                            conversationMap[messageJson.getLong(CHANNEL_ID)] = currentConversation
                            ChatDatabase.setConversationMap(conversationMap, messageJson.getString(APP_SECRET_KEY))
                            val mIntent = Intent(CHANNEL_INTENT)
                            try {
                                mIntent.putExtra(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        } else if (messageJson.has(IS_THREAD_MESSAGE) && messageJson.getBoolean(IS_THREAD_MESSAGE)) {
            Thread {
                kotlin.run {
                    try {
                        val unreadCountArrayList = CommonData.getNotificationCountList()
                        var isChannelPresent = false
                        for (i in unreadCountArrayList.indices) {

                            if (unreadCountArrayList[i].channelId!!.compareTo(messageJson.getLong(CHANNEL_ID)) == 0
                                    && unreadCountArrayList[i].muid == messageJson.getString(MESSAGE_UNIQUE_ID)
                                    && !unreadCountArrayList[i].isTagged) {
                                isChannelPresent = true
                                break
                            }
                        }

                        if (!isChannelPresent && !messageJson.has(TAGGED_USERS)) {
                            if (messageJson.has(UPDATE_NOTIFICATION_COUNT) && messageJson.getBoolean(UPDATE_NOTIFICATION_COUNT)) {
                                val unreadCount = UnreadCount(messageJson.getLong(CHANNEL_ID),
                                        messageJson.getString(MESSAGE_UNIQUE_ID), TEXT_MESSAGE,
                                        false, true)
                                unreadCountArrayList.add(unreadCount)
                                CommonData.setNotificationsCountList(unreadCountArrayList)
                                val mIntent2 = Intent(NOTIFICATION_COUNTER_INTENT)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                            } else if (!messageJson.has(UPDATE_NOTIFICATION_COUNT)) {
                                val unreadCount = UnreadCount(messageJson.getLong(CHANNEL_ID),
                                        messageJson.getString(MESSAGE_UNIQUE_ID), TEXT_MESSAGE,
                                        false, true)
                                unreadCountArrayList.add(unreadCount)
                                CommonData.setNotificationsCountList(unreadCountArrayList)
                                val mIntent2 = Intent(NOTIFICATION_COUNTER_INTENT)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent2)
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        } else {
            if (messageJson.getInt(MESSAGE_TYPE) == PUBLIC_NOTE) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
            }
        }

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

        val mngr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskList = mngr.getRunningTasks(10)
        if (taskList[0].topActivity!!.className == "com.skeleton.mvp.activity.MainActivity") {
            PushReceiver.PushChannel.pushChannelId = -2L
        }

        if (PushReceiver.PushChannel.pushChannelId != null) {
            val mngr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val taskList = mngr.getRunningTasks(10)

            if (messageJson.has(IS_THREAD_MESSAGE) && messageJson.getBoolean(IS_THREAD_MESSAGE)) {
                if (taskList[0].topActivity!!.className == "com.skeleton.mvp.activity.FuguInnerChatActivity") {
                    if (PushReceiver.PushChannel.pushMuid == messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                    } else {
                        //Show Notification
                        MultipleMessageNotification().publishNotification(messageJson, context,
                                messageJson.getString(NOTI_MSG), data,
                                priority, smallIcon, isSpecialPush)
//                        showPush(messageJson, context, message, data, priority)
                    }
                } else {
                    //Show Notification
                    MultipleMessageNotification().publishNotification(messageJson, context,
                            messageJson.getString(NOTI_MSG), data,
                            priority, smallIcon, isSpecialPush)
//                    showPush(messageJson, context, message, data, priority)
                }
            } else if (PushReceiver.PushChannel.pushChannelId!!.compareTo(messageJson.getLong(CHANNEL_ID)) != 0) {
                //Show Notification
                MultipleMessageNotification().publishNotification(messageJson, context,
                        messageJson.getString(NOTI_MSG), data,
                        priority, smallIcon, isSpecialPush)
//                showPush(messageJson, context, message, data, priority)
            }
        }

    }
}