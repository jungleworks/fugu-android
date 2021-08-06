package com.skeleton.mvp.util

import android.content.Context
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.adapter.MessageAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.socket.SocketConnection
import org.json.JSONObject

class BotButtonActions {

    fun onDoneClick(muid: String, comment: String, pos: Int, context: Context,
                    messagesMap: LinkedHashMap<String, Message>,
                    messageList: ArrayList<Message>, messageAdapter: MessageAdapter?,
                    userId: Long, channelId: Long, botButtonsCallBack: BotButtonsCallBack) {
        val view = (context as ChatActivity).currentFocus
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        val message = messagesMap[muid]!!
        message.customActions[pos].isShowTextField = false
        message.customActions[pos].title = message.customActions[pos].title
        if (message.customActions[pos].defaultTextField != null) {
            if (message.customActions[pos].defaultTextField.output.equals("comment")) {
                if (TextUtils.isEmpty(message.customActions[pos].comment)) {
                    message.customActions[pos].comment = comment
                } else {
                    message.customActions[pos].comment = message.customActions[pos].comment + "\n" + comment
                }
            } else if (message.customActions[pos].defaultTextField.output.equals("remark")) {
                if (TextUtils.isEmpty(message.customActions[pos].remark)) {
                    message.customActions[pos].remark = comment
                } else {
                    message.customActions[pos].remark = message.customActions[pos].remark + "\n" + comment
                }
            }
        } else {
            if (message.customActions[pos].lastClickedButton.output.equals("comment")) {
                if (TextUtils.isEmpty(message.customActions[pos].comment)) {
                    message.customActions[pos].comment = comment
                } else {
                    message.customActions[pos].comment = message.customActions[pos].comment + "\n" + comment
                }
            } else if (message.customActions[pos].lastClickedButton.output.equals("remark")) {
                if (TextUtils.isEmpty(message.customActions[pos].remark)) {
                    message.customActions[pos].remark = comment
                } else {
                    message.customActions[pos].remark = message.customActions[pos].remark + "\n" + comment
                }
            }
        }
        messageList[message.messageIndex] = message
        messagesMap[muid] = message
        messageAdapter?.updateMessageList(messageList)
        messageAdapter?.notifyItemChanged(message.messageIndex)
        val data = JSONObject()
        val buttonData = JSONObject()
        buttonData.put("tagged_user_id", message.customActions[pos].taggedUserId)
        if (!TextUtils.isEmpty(message.customActions[pos].comment)) {
            buttonData.put("comment", message.customActions[pos].comment)
        }
        if (!TextUtils.isEmpty(message.customActions[pos].remark)) {
            buttonData.put("remark", message.customActions[pos].remark)
        }
        buttonData.put("leave_id", message.customActions[pos].leaveId)
        buttonData.put("confirmation_type", message.customActions[pos].confirmationType)
        buttonData.put("title", message.customActions[pos].title)
        data.put("button_data", buttonData)
        if (message.customActions[pos].defaultTextField != null) {
            data.put("button_action", message.customActions[pos].defaultTextField.action)
            data.put(FuguAppConstant.MESSAGE, message.customActions[pos].defaultTextField.action)
        } else {
            data.put("button_action", message.customActions[pos].lastClickedButton.action)
            data.put(FuguAppConstant.MESSAGE, message.customActions[pos].lastClickedButton.label)
        }
        data.put("comment", comment)
        data.put(FuguAppConstant.IS_TYPING, 0)
        data.put(FuguAppConstant.MESSAGE_UNIQUE_ID, muid)
        data.put(FuguAppConstant.USER_ID, userId)
        data.put(FuguAppConstant.CHANNEL_ID, channelId)
        SocketConnection.sendMessage(data)
        botButtonsCallBack.onDoneClicked(messagesMap, messageList)
    }

    interface BotButtonsCallBack {
        fun onDoneClicked(updatedMessagesMap: LinkedHashMap<String, Message>,
                          updatedMessageList: ArrayList<Message>)
    }

}