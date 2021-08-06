package com.skeleton.mvp.util

import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.POLL_ID
import com.skeleton.mvp.constant.FuguAppConstant.USER_IMAGE
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.User
import org.json.JSONObject

class ParsePollVote {

    fun parsePollVote(messageJson: JSONObject, message: Message): Message {
        val user = User()
        user.userId = messageJson.getLong(FuguAppConstant.USER_ID)
        user.fullName = messageJson.getString(FuguAppConstant.FULL_NAME)
        user.userImage = messageJson.getString(USER_IMAGE)
        for (i in 0 until message.pollOptions.size) {
            if (message.pollOptions[i].puid == messageJson.getString(POLL_ID)) {
                if (message.multipleSelect) {
                    if (message.pollOptions[i].voteMap[messageJson.getLong(FuguAppConstant.USER_ID)] == null) {
                        message.pollOptions[i].users.add(user)
                        message.pollOptions[i].voteMap[messageJson.getLong(FuguAppConstant.USER_ID)] = user
                        message.total_votes += 1
                    } else {
                        for (j in 0 until message.pollOptions[i].users.size) {
                            if (message.pollOptions[i].users[j].userId.compareTo(messageJson.getLong(FuguAppConstant.USER_ID)) == 0) {
                                message.pollOptions[i].users.removeAt(j)
                                break
                            }
                        }
                        message.pollOptions[i].voteMap.remove(messageJson.getLong(FuguAppConstant.USER_ID))
                        message.total_votes -= 1
                    }
                } else {
                    if (message.pollOptions[i].voteMap[messageJson.getLong(FuguAppConstant.USER_ID)] == null) {
                        message.pollOptions[i].users.add(user)

                        message.pollOptions[i].voteMap[messageJson.getLong(FuguAppConstant.USER_ID)] = user
                        message.total_votes += 1
                    }
                }
            } else {
                if (!message.multipleSelect) {
                    if (message.pollOptions[i].voteMap[messageJson.getLong(FuguAppConstant.USER_ID)] != null) {
                        for (j in 0..message.pollOptions[i].users.size - 1) {
                            if (message.pollOptions[i].users[j].userId.compareTo(messageJson.getLong(FuguAppConstant.USER_ID)) == 0) {
                                message.pollOptions[i].users.removeAt(j)
                                break
                            }
                        }
                        message.pollOptions[i].voteMap.remove(messageJson.getLong(FuguAppConstant.USER_ID))
                        message.total_votes -= 1
                    }
                }
            }
        }
        return message
    }
}