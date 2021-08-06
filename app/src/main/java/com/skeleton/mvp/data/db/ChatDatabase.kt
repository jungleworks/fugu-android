package com.skeleton.mvp.data.db

import com.skeleton.mvp.adapter.FuguBotAdapter
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.MissedCallNotification
import com.skeleton.mvp.model.PushNotification
import com.skeleton.mvp.model.mentions.Mention
import com.skeleton.mvp.util.Log
import io.paperdb.Paper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

/**
 * Created
 * rajatdhamija on 30/05/18.
 */

object ChatDatabase {

    private val PAPER_CONVERSATION_LIST = "new_paper_conversation_list"
    private val PAPER_CONVERSATION_MAP = "new_paper_conversation_map"
    private val PAPER_MESSAGE_LIST = "new_paper_message_list"
    private val PAPER_THREAD_MAP = "new_paper_thread_map"
    private val PAPER_MESSAGE_MAP = "new_pager_message_map"
    private val PAPER_UNSENT_MESSAGES_MAP = "new_paper_unsent_messages"
    private var STACK_NOTIFICATIONS_MAP = TreeMap<Long, ArrayList<PushNotification>>()
    private var PUSH_NOTIFICATIONS_MAP = TreeMap<Long, Int>()
    private var STACK_CALL_NOTIFICATIONS_MAP = TreeMap<Long, ArrayList<MissedCallNotification>>()
    private var UNSENT_MESSAGES_MAP = java.util.LinkedHashMap<Long, java.util.LinkedHashMap<String, Message>>()
    private val STACK_NOTIFICATIONS = "new_stack_notifications"
    private val PUSH_NOTIFICATIONS_COUNT = "push_notifications_count"
    private val STACK_CALL_NOTIFICATIONS = "stack_call_notifications"
    private val PAPER_UNSENT_TYPED_MESSAGE = "new_paper_unsent_typed_message"
    private val PAPER_UNSENT_TYPED_BOT_MESSAGE = "new_paper_unsent_typed_bot_message"
    private val PAPER_MENTIONS = "new_paper_mentions"
    private val PAPER_NOTIFICATIONS_MAP = "paper_notification_map"
    private val PAPER_CALL_NOTIFICATIONS_MAP = "paper_call_notification_map"

    fun setMessageList(messageList: ArrayList<Message>, channelId: Long?) {
        try {
            Paper.book().write(PAPER_MESSAGE_LIST + channelId!!, messageList)
        } catch (e: Exception) {

        }
    }

    fun getMessageList(channelId: Long?): ArrayList<Message> {
        return try {
            Paper.book().read(PAPER_MESSAGE_LIST + channelId!!, ArrayList())
        } catch (e: Exception) {
            ArrayList()
        }
    }

    fun setConversationMap(conversationMap: LinkedHashMap<Long, FuguConversation>, appSecretKey: String) {
        try {
            Paper.book().write(PAPER_CONVERSATION_MAP + appSecretKey, conversationMap)
        } catch (e: Exception) {

        }
    }

    fun getConversationMap(appSecretKey: String): LinkedHashMap<Long, FuguConversation> {
        return try {
            Paper.book().read(PAPER_CONVERSATION_MAP + appSecretKey, LinkedHashMap())
        } catch (e: Exception) {
            LinkedHashMap()
        }
    }

    fun setThreadMap(threadMap: LinkedHashMap<String, Int>, channelId: Long?) {
        try {
            Paper.book().write(PAPER_THREAD_MAP + channelId!!, threadMap)
        } catch (e: Exception) {

        }
    }

    fun getThreadMap(channelId: Long?): LinkedHashMap<String, Int> {
        try {
            return Paper.book().read(PAPER_THREAD_MAP + channelId!!, LinkedHashMap())
        } catch (e: Exception) {
            return LinkedHashMap()
        }
    }

    fun setMessageMap(messageMap: LinkedHashMap<String, Message>, channelId: Long?) {
        try {
            Paper.book().write(PAPER_MESSAGE_MAP + channelId!!, messageMap)
        } catch (e: Exception) {

        }
    }

    fun getMessageMap(channelId: Long?): LinkedHashMap<String, Message> {
        return try {
            Paper.book().read(PAPER_MESSAGE_MAP + channelId!!, LinkedHashMap())
        } catch (e: Exception) {
            LinkedHashMap()
        }
    }


    fun setUnsentMessageMapByChannel(channelId: Long?, unsentMessageMap: java.util.LinkedHashMap<String, Message>) {
        try {
            if (unsentMessageMap.size != 0) {
                UNSENT_MESSAGES_MAP.put(channelId!!, unsentMessageMap)
            } else {
                try {
                    UNSENT_MESSAGES_MAP.remove(channelId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            Paper.book().write(PAPER_UNSENT_MESSAGES_MAP, UNSENT_MESSAGES_MAP)
        } catch (e: Exception) {

        }
    }

    fun removeUnsentMessageMapChannel(channelId: Long?) {
        UNSENT_MESSAGES_MAP.remove(channelId)
        Paper.book().write<java.util.LinkedHashMap<Long, java.util.LinkedHashMap<String, Message>>>(PAPER_UNSENT_MESSAGES_MAP, UNSENT_MESSAGES_MAP)
    }

    fun getUnsentMessageMapByChannel(channelId: Long?): java.util.LinkedHashMap<String, Message> {
        try {
            if (UNSENT_MESSAGES_MAP.isEmpty()) {
                UNSENT_MESSAGES_MAP = Paper.book().read(PAPER_UNSENT_MESSAGES_MAP, java.util.LinkedHashMap())
            }
            if (UNSENT_MESSAGES_MAP.get(channelId) != null) {
                return UNSENT_MESSAGES_MAP.get(channelId)!!
            } else {
                return LinkedHashMap()
            }
        } catch (e: java.lang.Exception) {
            return LinkedHashMap()
        }
    }

    @Throws(Exception::class)
    fun getUnsentMessageMap(): java.util.LinkedHashMap<Long, java.util.LinkedHashMap<String, Message>> {
        return try {
            if (UNSENT_MESSAGES_MAP.isEmpty()) {
                UNSENT_MESSAGES_MAP = Paper.book().read(PAPER_UNSENT_MESSAGES_MAP, java.util.LinkedHashMap())
            }
            UNSENT_MESSAGES_MAP
        } catch (e: Exception) {
            java.util.LinkedHashMap()
        }
    }

    fun setNotification(channelId: Long?, notificationsList: ArrayList<PushNotification>) {
        try {
            STACK_NOTIFICATIONS_MAP[channelId!!] = notificationsList
            Paper.book().write(STACK_NOTIFICATIONS, ChatDatabase.STACK_NOTIFICATIONS_MAP)
        } catch (e: Exception) {
            Paper.book().delete(STACK_NOTIFICATIONS)
        }

    }

    fun getNotifications(channelId: Long?): ArrayList<PushNotification> {
        try {
            if (STACK_NOTIFICATIONS_MAP.isEmpty()) {
                STACK_NOTIFICATIONS_MAP = Paper.book().read(STACK_NOTIFICATIONS, TreeMap<Long, ArrayList<PushNotification>>())
            }
            return STACK_NOTIFICATIONS_MAP[channelId]!!
        } catch (e: Exception) {
            Paper.book().delete(STACK_NOTIFICATIONS)
            return ArrayList()
        }

    }


    fun setPushCount(channelId: Long?, count: Int) {
        try {
            Log.e("Channel: +$channelId +:", count.toString())
            PUSH_NOTIFICATIONS_MAP[channelId!!] = count
            Paper.book().write(PUSH_NOTIFICATIONS_COUNT, ChatDatabase.PUSH_NOTIFICATIONS_MAP)
        } catch (e: Exception) {
            Paper.book().delete(PUSH_NOTIFICATIONS_COUNT)
        }
    }

    fun getPushCount(channelId: Long?): Int {
        try {
            if (PUSH_NOTIFICATIONS_MAP.isEmpty()) {
                PUSH_NOTIFICATIONS_MAP = Paper.book().read(PUSH_NOTIFICATIONS_COUNT, TreeMap<Long, Int>())
            }
            return PUSH_NOTIFICATIONS_MAP[channelId]!!
        } catch (e: Exception) {
            Paper.book().delete(PUSH_NOTIFICATIONS_COUNT)
            return 0
        }
    }

    fun getCallNotifications(channelId: Long?): ArrayList<MissedCallNotification> {
        try {
            if (STACK_CALL_NOTIFICATIONS_MAP.isEmpty()) {
                STACK_CALL_NOTIFICATIONS_MAP = Paper.book().read(STACK_CALL_NOTIFICATIONS, TreeMap<Long, ArrayList<MissedCallNotification>>())
            }
            return STACK_CALL_NOTIFICATIONS_MAP[channelId]!!
        } catch (e: Exception) {
            Paper.book().delete(STACK_CALL_NOTIFICATIONS)
            return ArrayList()
        }

    }

    fun setCallNotification(channelId: Long?, notificationsList: ArrayList<MissedCallNotification>) {
        try {
            STACK_CALL_NOTIFICATIONS_MAP[channelId!!] = notificationsList
            Paper.book().write(STACK_CALL_NOTIFICATIONS, ChatDatabase.STACK_CALL_NOTIFICATIONS_MAP)
        } catch (e: Exception) {
            Paper.book().delete(STACK_CALL_NOTIFICATIONS)
        }
    }

    fun removeCallNotifications(channelId: Long?) {
        try {
            if (!STACK_CALL_NOTIFICATIONS_MAP.isEmpty()) {
                val NEW_CALL_STACk_NOTIFICATIONS_MAP = STACK_CALL_NOTIFICATIONS_MAP
                NEW_CALL_STACk_NOTIFICATIONS_MAP.remove(channelId)
                Paper.book().write(STACK_CALL_NOTIFICATIONS, NEW_CALL_STACk_NOTIFICATIONS_MAP)
            }
        } catch (e: Exception) {
            Paper.book().delete(STACK_CALL_NOTIFICATIONS)
        }

    }

    fun removeNotifications(channelId: Long?) {
        try {
            if (!STACK_NOTIFICATIONS_MAP.isEmpty()) {
                val NEW_STACk_NOTIFICATIONS_MAP = STACK_NOTIFICATIONS_MAP
                NEW_STACk_NOTIFICATIONS_MAP.remove(channelId)
                Paper.book().write<TreeMap<Long, ArrayList<PushNotification>>>(STACK_NOTIFICATIONS, NEW_STACk_NOTIFICATIONS_MAP)
            }
        } catch (e: Exception) {
            Paper.book().delete(STACK_NOTIFICATIONS)
        }

    }

    fun setUnsentTypedMessage(message: String, channelId: Long?) {
        try {
            Paper.book().write(PAPER_UNSENT_TYPED_MESSAGE + channelId, message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUnsentTypedMessage(channelId: Long?): String {
        try {
            return Paper.book().read(PAPER_UNSENT_TYPED_MESSAGE + channelId)
        } catch (e: Exception) {
            return ""
        }
    }

    fun setUnsentTypedBotMessage(message: FuguBotAdapter.BotAction?, channelId: Long?) {
        try {
            Paper.book().write(PAPER_UNSENT_TYPED_BOT_MESSAGE + channelId, message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUnsentTypedBotMessage(channelId: Long?): FuguBotAdapter.BotAction? {
        return try {
            Paper.book().read(PAPER_UNSENT_TYPED_BOT_MESSAGE + channelId)
        } catch (e: Exception) {
            null
        }
    }


    fun setMentions(message: ArrayList<Mention>, channelId: Long?) {
        try {
            Paper.book().write(PAPER_MENTIONS + channelId, message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMentions(channelId: Long): ArrayList<Mention>? {
        return Paper.book().read(PAPER_MENTIONS + channelId)
    }

    fun setNotificationsMap(notificationsMap: HashMap<Int, ArrayList<Int>>) {
        try {
            Paper.book().write(PAPER_NOTIFICATIONS_MAP, notificationsMap)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getNotificationsMap(): HashMap<Int, ArrayList<Int>> {
        try {
            return (Paper.book().read(PAPER_NOTIFICATIONS_MAP) as HashMap<Int, ArrayList<Int>>)
        } catch (e: java.lang.Exception) {
            return HashMap()
        }
    }

    fun setCallNotificationsMap(notificationsMap: HashMap<Int, ArrayList<Int>>) {
        try {
            Paper.book().write(PAPER_CALL_NOTIFICATIONS_MAP, notificationsMap)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getCallNotificationsMap(): HashMap<Int, ArrayList<Int>> {
        return try {
            (Paper.book().read(PAPER_CALL_NOTIFICATIONS_MAP) as HashMap<Int, ArrayList<Int>>)
        } catch (e: java.lang.Exception) {
            HashMap()
        }
    }
}
