package com.skeleton.mvp.util

import android.text.TextUtils
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.Reaction
import com.skeleton.mvp.model.UserReaction

class EmojiReceived {
    fun emojiReceived(clickedEmojiMuid: String, messageList: ArrayList<Message>, emoji: String,
                      isToBePublished: Boolean, userId: String, fullName: String, updateAndPublishEmoji: UpdateAndPublishEmoji) {
        var selectedPos = 0
        var alreadyReacted = false
        var emojiExists = false
        var sentEmpty = false
        var users: MutableList<String> = java.util.ArrayList()
        var fullNames: MutableList<String> = java.util.ArrayList()

        var emojiList: MutableList<Reaction> = java.util.ArrayList()
        for (fuguItemLoop in messageList.indices) {
            val uniqueId = messageList[fuguItemLoop].muid
            if (!TextUtils.isEmpty(uniqueId) && uniqueId == clickedEmojiMuid) {
                selectedPos = fuguItemLoop
                if (messageList[fuguItemLoop].userReaction != null) {
                    emojiList = messageList[fuguItemLoop].userReaction.reaction
                    break
                }
            }
        }
        if (!TextUtils.isEmpty(emoji)) {
            //check if user has already reacted if yes set "alreadyReacted = true"
            for (reaction in emojiList.indices) {
                if (emojiList[reaction].users.contains(userId)) {
                    alreadyReacted = true
                    break
                }
            }
            //check if emoji already exists if yes set "emojiExists="true"
            for (user in emojiList.indices) {
                if (emojiList[user].reaction == emoji) {
                    emojiExists = true
                }
            }
            if (!alreadyReacted && !emojiExists) {
                val reaction = Reaction()
                users.add(userId)
                fullNames.add(fullName)
                reaction.reaction = emoji
                reaction.users = users
                reaction.fullNames = fullNames
                emojiList.add(reaction)
            } else if (!alreadyReacted && emojiExists) {
                for (i in emojiList.indices) {
                    if (emojiList[i].reaction == emoji) {
                        if (emojiList[i].users != null) {
                            users = emojiList[i].users
                            fullNames = emojiList[i].fullNames
                        }
                        users.add(userId)
                        fullNames.add(fullName)
                        emojiList[i].users = users
                        emojiList[i].fullNames = fullNames
                        emojiList[i].reaction = emoji
                    }
                }
            } else if (alreadyReacted && !emojiExists) {
                for (i in emojiList.indices) {
                    if (emojiList[i].users != null) {
                        users = emojiList[i].users
                        fullNames = emojiList[i].fullNames
                    }
                    if (users.contains(userId)) {
                        users.remove(userId)
                        fullNames.remove(fullName)
                        emojiList[i].users = users
                        emojiList[i].fullNames = fullNames
                        if (emojiList[i].users.size == 0) {
                            emojiList.removeAt(i)
                        }
                        break
                    }
                }
                val reaction = Reaction()
                users = java.util.ArrayList()
                fullNames = java.util.ArrayList()
                users.add(userId)
                fullNames.add(fullName)
                reaction.reaction = emoji
                reaction.users = users
                reaction.fullNames = fullNames
                emojiList.add(reaction)
            } else if (alreadyReacted && emojiExists) {
                var emojiPos = -1
                for (i in emojiList.indices) {
                    if (emojiList[i].reaction == emoji) {
                        if (emojiList[i].users.contains(userId)) {
                            emojiPos = i
                        }
                    }
                }
                if (emojiPos > -1) {
                    sentEmpty = true
                    for (i in emojiList.indices) {
                        if (emojiList[i].users != null) {
                            users = emojiList[i].users
                            fullNames = emojiList[i].fullNames
                        }
                        if (users.contains(userId)) {
                            users.remove(userId)
                            fullNames.remove(fullName)
                            emojiList[i].users = users
                            emojiList[i].fullNames = fullNames
                            if (emojiList[i].users.size == 0) {
                                emojiList.removeAt(i)
                            }
                            break
                        }
                    }
                } else {
                    for (i in emojiList.indices) {
                        if (emojiList[i].users != null) {
                            users = emojiList[i].users
                            fullNames = emojiList[i].fullNames
                        }
                        if (users.contains(userId)) {
                            users.remove(userId)
                            fullNames.remove(fullName)
                            emojiList[i].users = users
                            emojiList[i].fullNames = fullNames
                            if (emojiList[i].users.size == 0) {
                                emojiList.removeAt(i)
                            }
                            break
                        }
                    }
                    var hasEmoji = false
                    var pos = -1
                    for (i in emojiList.indices) {
                        if (emojiList[i].reaction == emoji) {
                            hasEmoji = true
                            pos = i
                        }
                    }
                    if (hasEmoji) {
                        users = emojiList[pos].users
                        fullNames = emojiList[pos].fullNames
                        users.add(userId)
                        fullNames.add(fullName)
                        emojiList[pos].users = users
                        emojiList[pos].fullNames = fullNames
                    } else {
                        val reaction = Reaction()
                        users.add(userId)
                        fullNames.add(fullName)
                        reaction.reaction = emoji
                        reaction.users = users
                        reaction.fullNames = fullNames
                        emojiList.add(reaction)
                    }
                }
            }
        } else {
            if (!TextUtils.isEmpty(userId)) {
                for (i in emojiList.size - 1 downTo 0) {
                    if (emojiList[i].users.contains(userId)) {
                        val users = emojiList[i].users
                        users.remove(userId)
                        users.remove(fullName)
                        if (emojiList[i].users.size == 0) {
                            emojiList.removeAt(i)
                        }
                    }
                }
            }
        }
        if (messageList[selectedPos].userReaction == null) {
            val userReaction = UserReaction()
            userReaction.reaction = emojiList
            messageList[selectedPos].userReaction = userReaction
        } else {
            messageList[selectedPos].userReaction.reaction = emojiList
        }
        val finalSelectedPos = selectedPos
        updateAndPublishEmoji.updateAndPublishEmoji(emoji, finalSelectedPos, messageList, isToBePublished, sentEmpty)
    }

    public interface UpdateAndPublishEmoji {
        fun updateAndPublishEmoji(emoji: String, selectedPos: Int, messageList: ArrayList<Message>, isToBePublished: Boolean, senEmpty: Boolean)
    }
}