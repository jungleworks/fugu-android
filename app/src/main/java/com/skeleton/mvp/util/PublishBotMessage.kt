package com.skeleton.mvp.util

import android.content.Context
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.adapter.MessageAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.customAction.Button
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.utils.FuguUtils.Companion.getTimeZoneOffset
import org.json.JSONObject

class PublishBotMessage {
    fun publishSocketClick(button: Button, muid: String, pos: Int, context: Context,
                           messageList: ArrayList<Message>, messagesMap: LinkedHashMap<String, Message>,
                           messageAdapter: MessageAdapter?, userId: Long, channelId: Long,
                           botButtonsCallBack: BotButtonActions.BotButtonsCallBack) {
        val view = (context as ChatActivity).currentFocus
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        val message = messagesMap[muid]!!
        message.customActions[pos].isActionTaken = true
        messageList[message.messageIndex] = message
        messagesMap[muid] = message
        messageAdapter?.updateMessageList(messageList)
        messageAdapter?.notifyItemChanged(message.messageIndex)
        val data = JSONObject()
        val buttonData = JSONObject()
        if (message.customActions[pos].taggedUserId != 0) {
            buttonData.put("tagged_user_id", message.customActions[pos].taggedUserId)
        }
        if (!TextUtils.isEmpty(message.customActions[pos].comment)) {
            buttonData.put("comment", message.customActions[pos].comment)
        }
        if (!TextUtils.isEmpty(message.customActions[pos].remark)) {
            buttonData.put("remark", message.customActions[pos].remark)
        }
        if (message.customActions[pos].leaveId != "0") {
            buttonData.put("leave_id", message.customActions[pos].leaveId)
        }
        if (!TextUtils.isEmpty(message.customActions[pos].confirmationType)) {
            buttonData.put("confirmation_type", message.customActions[pos].confirmationType)
        }
        if (!TextUtils.isEmpty(message.customActions[pos].title)) {
            buttonData.put("title", message.customActions[pos].title)
        }
        buttonData.put("type_id", button.typeId)
        data.put("button_data", buttonData)
        data.put("button_action", button.action)
        data.put("clicked_button", button)
        data.put("time_zone", getTimeZoneOffset())
        data.put(FuguAppConstant.IS_TYPING, 0)
        data.put(FuguAppConstant.MESSAGE, button.label)
        data.put(FuguAppConstant.MESSAGE_UNIQUE_ID, muid)
        data.put(FuguAppConstant.USER_ID, userId)
        data.put(FuguAppConstant.CHANNEL_ID, channelId)
        SocketConnection.sendMessage(data)
        botButtonsCallBack.onDoneClicked(messagesMap, messageList)
    }
}