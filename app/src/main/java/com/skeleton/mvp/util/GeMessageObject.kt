package com.skeleton.mvp.util

import android.os.Environment
import android.text.TextUtils
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.*
import com.skeleton.mvp.model.customAction.Button
import com.skeleton.mvp.model.customAction.CustomAction
import com.skeleton.mvp.model.customAction.DefaultTextField
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class GeMessageObject {
    private val emptyString: String = ""
    private var TEXT_MESSGAE_SELF: Int = 0
    private var TEXT_MESSGAE_OTHER: Int = 1
    private var IMAGE_MESSGAE_SELF: Int = 2
    private var IMAGE_MESSGAE_OTHER: Int = 3
    private var FILE_MESSGAE_SELF: Int = 4
    private var FILE_MESSGAE_OTHER: Int = 5
    private var HEADER_ITEM = 6
    private var PUBLIC_NOTE = 7
    private var UNREAD_ITEM = 8
    private var MESSAGE_DELETED_SELF = 9
    private var MESSAGE_DELETED_OTHER = 10
    private var VIDEO_MESSGAE_SELF: Int = 11
    private var VIDEO_MESSGAE_OTHER: Int = 12
    private var VIDEO_CALL_SELF: Int = 13
    private var VIDEO_CALL_OTHER: Int = 14
    private var POLL_SELF: Int = 15
    private var POLL_OTHER: Int = 16
    private var CUSTOM_ACTION: Int = 17

    fun getServerMessageObject(serverMessageObject: Message?, messageIndex: Int, userId: Long, updateChatData: UpdateChatData): Message {
        val messageObject = Message()
        messageObject.muid = serverMessageObject?.muid ?: emptyString
        messageObject.isSent = serverMessageObject?.isSent ?: true
        messageObject.fromName = serverMessageObject?.fromName ?: emptyString
        messageObject.id = serverMessageObject?.id ?: -1L
        messageObject.userId = serverMessageObject?.userId ?: -1L
        messageObject.sentAtUtc = serverMessageObject?.sentAtUtc ?: emptyString
        messageObject.message = serverMessageObject?.message ?: emptyString
        val formattedStrings = FormatStringUtil.FormatString.getFormattedString(serverMessageObject?.message
                ?: emptyString)
        messageObject.alteredMessage = formattedStrings[0]
        messageObject.formattedMessage = formattedStrings[1]
        messageObject.messageStatus = serverMessageObject?.messageStatus
                ?: FuguAppConstant.MESSAGE_SENT
        messageObject.thumbnailUrl = serverMessageObject?.thumbnailUrl ?: emptyString
        messageObject.image_url = serverMessageObject?.image_url ?: emptyString
        messageObject.image_url_100x100 = serverMessageObject?.image_url_100x100 ?: emptyString
        messageObject.sharableImage_url = serverMessageObject?.image_url ?: emptyString
        messageObject.sharableThumbnailUrl = serverMessageObject?.thumbnailUrl ?: emptyString
        messageObject.sharableImage_url_100x100 = serverMessageObject?.sharableImage_url_100x100
                ?: emptyString
        messageObject.url = serverMessageObject?.url ?: ""
        messageObject.messageType = serverMessageObject?.messageType ?: FuguAppConstant.TEXT_MESSAGE
        messageObject.chatType = serverMessageObject?.chatType ?: 2
        messageObject.email = serverMessageObject?.email ?: emptyString

        val userReactions = serverMessageObject?.userReaction ?: UserReaction()

        Collections.sort(userReactions.reaction, object : Comparator<Reaction> {
            override fun compare(one: Reaction, other: Reaction): Int {
                return other.totalCount!!.compareTo(one.totalCount)
            }
        })
        messageObject.threadMessageCount = serverMessageObject!!.threadMessageCount
        messageObject.userReaction = userReactions
        messageObject.userImage = serverMessageObject.userImage ?: emptyString
        messageObject.threadMessage = serverMessageObject.threadMessage
        messageObject.userType = serverMessageObject.userType
        messageObject.messageState = serverMessageObject.messageState
        messageObject.imageHeight = serverMessageObject.imageHeight
        messageObject.imageWidth = serverMessageObject.imageWidth
        messageObject.fileName = serverMessageObject.fileName ?: emptyString
        messageObject.fileSize = serverMessageObject.fileSize ?: emptyString
        messageObject.videoCallDuration = serverMessageObject.videoCallDuration ?: -1L
        messageObject.callType = serverMessageObject.callType

        messageObject.thread_message_data = serverMessageObject.thread_message_data

        messageObject.customActions = serverMessageObject.customActions
        try {
            messageObject.isStarred = serverMessageObject.isStarred
        } catch (e: java.lang.Exception) {

        }
        messageObject.messageIndex = messageIndex
        if (serverMessageObject.pollOptions != null) {
            serverMessageObject.pollOptions.sortWith(Comparator { one, other -> other.users.size.compareTo(one.users.size) })
        }
        messageObject.multipleSelect = serverMessageObject.multipleSelect
        try {
            if (serverMessageObject.multipleSelect!!) {
                var posToBeSwapped = -1
                for (i in 1..serverMessageObject.pollOptions.size - 1) {
                    for (j in 0..serverMessageObject.pollOptions[i].users.size - 1) {
                        if (serverMessageObject.pollOptions[i].users[j].userId.compareTo(userId) == 0) {
                            posToBeSwapped = i
                            break
                        }
                    }
                    if (posToBeSwapped != -1) {
                        break
                    }
                }
                if (posToBeSwapped != -1) {
                    val objectOne = serverMessageObject.pollOptions[posToBeSwapped]
                    val oldObject = serverMessageObject.pollOptions[1]
                    serverMessageObject.pollOptions[1] = objectOne
                    serverMessageObject.pollOptions[posToBeSwapped] = oldObject
                }
            } else {
                var isUserAtFirstPosition = false
                if (serverMessageObject.pollOptions != null) {
                    for (i in 0..serverMessageObject.pollOptions[0]?.users?.size!! - 1) {
                        if (serverMessageObject.pollOptions[0].users[i].userId.compareTo(userId) == 0) {
                            isUserAtFirstPosition = true
                            break
                        }
                    }
                    var posToBeSwapped = -1
                    if (!isUserAtFirstPosition) {
                        for (i in 0..serverMessageObject.pollOptions.size - 1) {
                            for (j in 0..serverMessageObject.pollOptions[i].users.size - 1) {
                                if (serverMessageObject.pollOptions[i].users[j].userId.compareTo(userId) == 0) {
                                    posToBeSwapped = i
                                    break
                                }
                            }
                            if (posToBeSwapped != -1) {
                                break
                            }
                        }
                        if (posToBeSwapped != -1) {
                            val objectOne = serverMessageObject.pollOptions[posToBeSwapped]
                            val oldObject = serverMessageObject.pollOptions[1]
                            serverMessageObject.pollOptions[1] = objectOne
                            serverMessageObject.pollOptions[posToBeSwapped] = oldObject
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        messageObject.pollOptions = serverMessageObject.pollOptions
        messageObject.question = serverMessageObject.question
        messageObject.total_votes = serverMessageObject.total_votes
        messageObject.expireTime = serverMessageObject.expireTime
        if (messageObject.pollOptions != null) {
            for (poll in 0..(messageObject.pollOptions.size - 1)) {
                messageObject.pollOptions[poll].voteMap = HashMap()
                for (user in (0..messageObject.pollOptions[poll].users.size - 1)) {
                    messageObject.pollOptions[poll].voteMap[messageObject.pollOptions[poll].users[user].userId] = messageObject.pollOptions[poll].users[user]
                }
            }
        }
        messageObject.isAnimate = false
        if (userId.compareTo(messageObject.userId) == 0) {
            when (messageObject.messageType) {
                FuguAppConstant.TEXT_MESSAGE -> messageObject.rowType = TEXT_MESSGAE_SELF
                FuguAppConstant.IMAGE_MESSAGE -> messageObject.rowType = IMAGE_MESSGAE_SELF
                FuguAppConstant.FILE_MESSAGE -> messageObject.rowType = FILE_MESSGAE_SELF
                FuguAppConstant.PUBLIC_NOTE -> messageObject.rowType = PUBLIC_NOTE
                FuguAppConstant.VIDEO_MESSAGE -> messageObject.rowType = VIDEO_MESSGAE_SELF
                FuguAppConstant.VIDEO_CALL -> messageObject.rowType = VIDEO_CALL_SELF
                FuguAppConstant.CUSTOM_ACTION_MESSAGE -> messageObject.rowType = CUSTOM_ACTION
                FuguAppConstant.POLL_MESSAGE -> messageObject.rowType = POLL_SELF
            }
        } else {
            when (messageObject.messageType) {
                FuguAppConstant.TEXT_MESSAGE -> messageObject.rowType = TEXT_MESSGAE_OTHER
                FuguAppConstant.IMAGE_MESSAGE -> messageObject.rowType = IMAGE_MESSGAE_OTHER
                FuguAppConstant.FILE_MESSAGE -> messageObject.rowType = FILE_MESSGAE_OTHER
                FuguAppConstant.PUBLIC_NOTE -> messageObject.rowType = PUBLIC_NOTE
                FuguAppConstant.VIDEO_MESSAGE -> messageObject.rowType = VIDEO_MESSGAE_OTHER
                FuguAppConstant.VIDEO_CALL -> messageObject.rowType = VIDEO_CALL_OTHER
                FuguAppConstant.CUSTOM_ACTION_MESSAGE -> messageObject.rowType = CUSTOM_ACTION
                FuguAppConstant.POLL_MESSAGE -> messageObject.rowType = POLL_OTHER
            }
        }
        if (serverMessageObject.messageState == 0) {
            if (userId.compareTo(messageObject.userId) == 0) {
                messageObject.rowType = MESSAGE_DELETED_SELF
            } else {
                messageObject.rowType = MESSAGE_DELETED_OTHER
            }
        }


//        if (serverMessageObject?.threadMessage!!) {
//            threadedMessagesArray.add(serverMessageObject.muid)
//            if (threadMessagesMap[serverMessageObject.muid] != null) {
//                messageObject.replyCount = threadMessagesMap[serverMessageObject.muid]!!
//            }
//            updateChatData.updateThreadMessageArray(threadedMessagesArray, threadMessagesMap)
//        }
        if (!TextUtils.isEmpty(serverMessageObject.url)) {
            val extensions = serverMessageObject.url?.split("\\.".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
            val extension = extensions!![extensions.size - 1].toLowerCase()
            messageObject.fileExtension = extension
        }

        if (serverMessageObject.messageType == FuguAppConstant.IMAGE_MESSAGE && (serverMessageObject.rowType == MESSAGE_DELETED_SELF || messageObject.rowType == MESSAGE_DELETED_OTHER)) {
            try {
                var extension = serverMessageObject.image_url!!.split(".")[serverMessageObject.image_url.split(".").size - 1]
                if (extension.toLowerCase().equals("png")) {
                    extension = "jpg"
                }
                val fileName = serverMessageObject.fileName + "_" + serverMessageObject.muid + "." + extension
                val filePath = File(getDirectory(extension) + "/" + fileName)
                if (filePath.exists()) {
                    filePath.delete()
                    updateChatData.deleteMessageFromLocal(serverMessageObject.id.toString())
//                    messageAdapter?.deleteImageFromImageList(serverMessageObject.id.toString())
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        try {
            if (serverMessageObject.messageType == FuguAppConstant.FILE_MESSAGE || serverMessageObject.messageType == FuguAppConstant.VIDEO_MESSAGE) {
                val link = serverMessageObject.url.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                messageObject.fileExtension = link[link.size - 1]
                messageObject.fileName = serverMessageObject.fileName
                messageObject.fileSize = serverMessageObject.fileSize
                if (!TextUtils.isEmpty(serverMessageObject.unsentFilePath)) {
                    messageObject.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus
                    messageObject.uploadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus
                    messageObject.image_url = serverMessageObject.url ?: emptyString
                    messageObject.image_url_100x100 = serverMessageObject.url ?: emptyString
                    messageObject.sharableImage_url = serverMessageObject.thumbnailUrl
                            ?: emptyString
                    messageObject.sharableThumbnailUrl = serverMessageObject.url ?: emptyString
                    messageObject.sharableImage_url_100x100 = serverMessageObject.thumbnailUrl
                            ?: emptyString

                } else {
                    if (!TextUtils.isEmpty(CommonData.getCachedFilePath(serverMessageObject.url, serverMessageObject.uuid))) {
                        messageObject.filePath = CommonData.getCachedFilePath(serverMessageObject.url, serverMessageObject.uuid)
                        messageObject.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                        messageObject.uploadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                        messageObject.image_url = serverMessageObject.url ?: emptyString
                        messageObject.image_url_100x100 = serverMessageObject.url ?: emptyString
                        messageObject.sharableImage_url = serverMessageObject.url ?: emptyString
                        messageObject.sharableThumbnailUrl = serverMessageObject.thumbnailUrl
                                ?: emptyString
                        messageObject.sharableImage_url_100x100 = serverMessageObject.thumbnailUrl
                                ?: emptyString
                    } else {
                        messageObject.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
                        messageObject.uploadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
                    }
                }
            } else if (serverMessageObject.messageType == FuguAppConstant.IMAGE_MESSAGE) {
                messageObject.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return messageObject
    }

    private fun getDirectory(extension: String): String? {
        try {
            var filePath = (Environment.getExternalStorageDirectory()).toString() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                    File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo!!.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]!!.directory
            val folder = File(filePath)
            val filePathArray = filePath.split("/")
            if (filePathArray[filePathArray.size - 1].equals(FuguAppConstant.IMAGE)) {
                if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].mediaVisibility == 0) {
                    Log.i("true", "true")
                    filePath = filePath.replace(FuguAppConstant.IMAGE, FuguAppConstant.PRIVATE_IMAGES)
                }
            }
            Log.i("Path", filePath)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            if (filePathArray[filePathArray.size - 1].equals(FuguAppConstant.IMAGE)) {
                if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].mediaVisibility == 0) {
                    val f = File(filePath + "/.nomedia")
                    if (!f.exists()) {
                        f.createNewFile()
                    }
                    Log.i("FilePath", filePath)
                }
            }
            return filePath
        } catch (e: Exception) {
            return null
        }
    }


    fun createMessageObject(messageJson: JSONObject, messageList: ArrayList<Message>, userId: Long): Message {
        val messageObject = Message()
        if (messageJson.has("message_id")) {
            messageObject.id = messageJson.getLong("message_id")
        }
        messageObject.muid = messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID)
        messageObject.isSent = true
        if (messageJson.has(FuguAppConstant.FULL_NAME)) {
            messageObject.fromName = messageJson.getString(FuguAppConstant.FULL_NAME)
        }
        messageObject.userId = messageJson.getLong(FuguAppConstant.USER_ID)
        messageObject.sentAtUtc = messageJson.getString(FuguAppConstant.DATE_TIME)


        val formattedStrings = FormatStringUtil.FormatString.getFormattedString(messageJson.getString(FuguAppConstant.MESSAGE))
        messageObject.alteredMessage = formattedStrings[0]
        messageObject.formattedMessage = formattedStrings[1]
        messageObject.messageStatus = FuguAppConstant.MESSAGE_SENT
        if (messageJson.has(FuguAppConstant.MESSAGE_TYPE)) {
            messageObject.messageType = messageJson.getInt(FuguAppConstant.MESSAGE_TYPE)
        }
        if (messageJson.has(FuguAppConstant.EMAIL)) {
            messageObject.email = messageJson.getString(FuguAppConstant.EMAIL)
        }
        messageObject.message = messageJson.getString(FuguAppConstant.MESSAGE)
        if (messageObject.messageType == FuguAppConstant.IMAGE_MESSAGE || messageObject.messageType == FuguAppConstant.VIDEO_MESSAGE || messageObject.messageType == FuguAppConstant.FILE_MESSAGE) {
            if (messageJson.has("hasCaption") && messageJson.getBoolean("hasCaption")) {
                messageObject.message = messageJson.getString(FuguAppConstant.MESSAGE)
            } else {
                messageObject.message = ""
            }
        }
        messageObject.downloadStatus = 0
        messageObject.uploadStatus = 0
        messageObject.isExpired = false
        messageObject.userReaction = UserReaction()
        messageObject.threadMessage = false
        messageObject.thread_message_data = ArrayList()
        if (messageJson.has(FuguAppConstant.USER_TYPE)) {
            messageObject.userType = messageJson.getInt(FuguAppConstant.USER_TYPE)
        }

        messageObject.messageState = 1
        if (messageObject.messageType == FuguAppConstant.IMAGE_MESSAGE) {
            messageObject.thumbnailUrl = messageJson.getString(FuguAppConstant.THUMBNAIL_URL)
            messageObject.image_url = messageJson.getString(FuguAppConstant.IMAGE_URL)
            messageObject.sharableThumbnailUrl = messageJson.getString(FuguAppConstant.THUMBNAIL_URL)
            messageObject.sharableImage_url = messageJson.getString(FuguAppConstant.IMAGE_URL)
            if (messageJson.has(FuguAppConstant.IMAGE_HEIGHT)) {
                messageObject.imageHeight = messageJson.getInt(FuguAppConstant.IMAGE_HEIGHT)
            }
            if (messageJson.has(FuguAppConstant.IMAGE_WIDTH)) {
                messageObject.imageWidth = messageJson.getInt(FuguAppConstant.IMAGE_WIDTH)
            }
            if (messageJson.has("file_name")) {
                messageObject.fileName = messageJson.getString("file_name")
            }
            if (messageJson.has("file_size")) {
                messageObject.fileSize = messageJson.getString("file_size")
            }
        }
        if (messageObject.messageType == FuguAppConstant.FILE_MESSAGE) {
            messageObject.url = messageJson.getString("url")
            messageObject.fileName = messageJson.getString(FuguAppConstant.FILE_NAME)
            messageObject.fileSize = messageJson.getString(FuguAppConstant.FILE_SIZE)
            val extensions = messageJson.getString(FuguAppConstant.FILE_NAME).split(Pattern.quote(".").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            messageObject.fileExtension = extensions[extensions.size - 1]
            messageObject.filePath = ""
        }
        if (messageObject.messageType == FuguAppConstant.VIDEO_MESSAGE) {
            messageObject.url = messageJson.getString("url")
            messageObject.thumbnailUrl = messageJson.getString(FuguAppConstant.THUMBNAIL_URL)
            messageObject.fileName = messageJson.getString(FuguAppConstant.FILE_NAME)
            messageObject.fileSize = messageJson.getString(FuguAppConstant.FILE_SIZE)
            val extensions = messageJson.getString(FuguAppConstant.FILE_NAME).split(Pattern.quote(".").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            messageObject.fileExtension = extensions[extensions.size - 1]
            messageObject.filePath = ""
        }
        val customActionsList = ArrayList<CustomAction>()
        if (messageObject.messageType == FuguAppConstant.CUSTOM_ACTION_MESSAGE) {
            if (messageJson.has(FuguAppConstant.CUSTOM_ACTIONS)) {
                val customActions = messageJson.getJSONArray(FuguAppConstant.CUSTOM_ACTIONS)
                for (j in 0 until customActions.length()) {
                    val customActionJson = customActions.getJSONObject(j)
                    val customAction = CustomAction()
                    customAction.title = customActionJson.getString(FuguAppConstant.TITLE)
                    if (customActionJson.has(FuguAppConstant.CONFIRMATION_TYPE)) {
                        customAction.confirmationType = customActionJson.getString(FuguAppConstant.CONFIRMATION_TYPE)
                    }
                    if (customActionJson.has(FuguAppConstant.LEAVE_ID)) {
                        customAction.leaveId = customActionJson.getString(FuguAppConstant.LEAVE_ID)
                    }
                    if (customActionJson.has(FuguAppConstant.TAGGED_USER_ID)) {
                        customAction.taggedUserId = customActionJson.getInt(FuguAppConstant.TAGGED_USER_ID)
                    }
                    if (customActionJson.has(FuguAppConstant.DEFAULT_TEXT_FIELD)) {
                        val defaultFieldjson = customActionJson.getJSONObject(FuguAppConstant.DEFAULT_TEXT_FIELD)
                        val defaultTextField = DefaultTextField()
                        defaultTextField.hint = defaultFieldjson.getString(FuguAppConstant.HINT)
                        defaultTextField.minimumLength = defaultFieldjson.getInt(FuguAppConstant.MINIMUM_LENGTH)
                        defaultTextField.isRequired = defaultFieldjson.getBoolean(FuguAppConstant.IS_REQUIRED)
                        defaultTextField.output = defaultFieldjson.getString(FuguAppConstant.OUTPUT)
                        defaultTextField.action = defaultFieldjson.getString(FuguAppConstant.ACTION)
                        defaultTextField.button_id = defaultFieldjson.getInt("id")
                        customAction.defaultTextField = defaultTextField
                    }
                    val buttonsList = ArrayList<Button>()
                    if (customActionJson.has(FuguAppConstant.BUTTONS)) {
                        val buttons = customActionJson.getJSONArray(FuguAppConstant.BUTTONS)
                        for (i in 0 until buttons.length()) {
                            val jsonObject = buttons.getJSONObject(i)
                            val button = Button()
                            button.label = jsonObject.getString(FuguAppConstant.LABEL)
                            button.action = jsonObject.getString(FuguAppConstant.ACTION)
                            if (jsonObject.has(FuguAppConstant.DATA))
                                button.data = jsonObject.getString(FuguAppConstant.DATA)
                            button.style = jsonObject.getString(FuguAppConstant.STYLE)

                            if (jsonObject.has("invite_link")) {
                                button.inviteLink = jsonObject.getString("invite_link")
                            }

                            if (jsonObject.has("id")) {
                                button.id = jsonObject.getInt("id")
                            }
                            if (jsonObject.has(FuguAppConstant.OUTPUT)) {
                                button.output = jsonObject.getString(FuguAppConstant.OUTPUT)
                            } else {
                                button.output = emptyString
                            }
                            if (jsonObject.has("type_id")) {
                                button.typeId = jsonObject.getString("type_id")
                            }

                            button.actionType = jsonObject.getString(FuguAppConstant.ACTION_TYPE)
                            buttonsList.add(button)
                        }
                        customAction.buttons = buttonsList
                    }
                    customActionsList.add(customAction)
                }
            }
            messageObject.customActions = customActionsList
        }

        messageObject.messageIndex = messageList.size
        if (userId.compareTo(messageObject.userId) == 0) {
            when (messageObject.messageType) {
                FuguAppConstant.TEXT_MESSAGE -> messageObject.rowType = FuguAppConstant.TEXT_MESSGAE_SELF
                FuguAppConstant.IMAGE_MESSAGE -> messageObject.rowType = FuguAppConstant.IMAGE_MESSGAE_SELF
                FuguAppConstant.FILE_MESSAGE -> messageObject.rowType = FuguAppConstant.FILE_MESSGAE_SELF
                FuguAppConstant.PUBLIC_NOTE -> messageObject.rowType = PUBLIC_NOTE
                FuguAppConstant.VIDEO_MESSAGE -> messageObject.rowType = FuguAppConstant.VIDEO_MESSGAE_SELF
                FuguAppConstant.VIDEO_CALL -> messageObject.rowType = FuguAppConstant.VIDEO_CALL_SELF
                FuguAppConstant.CUSTOM_ACTION_MESSAGE -> messageObject.rowType = FuguAppConstant.CUSTOM_ACTION
                FuguAppConstant.POLL_MESSAGE -> messageObject.rowType = FuguAppConstant.POLL_SELF
            }
        } else {
            when (messageObject.messageType) {
                FuguAppConstant.TEXT_MESSAGE -> messageObject.rowType = FuguAppConstant.TEXT_MESSGAE_OTHER
                FuguAppConstant.IMAGE_MESSAGE -> messageObject.rowType = FuguAppConstant.IMAGE_MESSGAE_OTHER
                FuguAppConstant.FILE_MESSAGE -> messageObject.rowType = FuguAppConstant.FILE_MESSGAE_OTHER
                FuguAppConstant.PUBLIC_NOTE -> messageObject.rowType = PUBLIC_NOTE
                FuguAppConstant.VIDEO_MESSAGE -> messageObject.rowType = FuguAppConstant.VIDEO_MESSGAE_OTHER
                FuguAppConstant.VIDEO_CALL -> messageObject.rowType = FuguAppConstant.VIDEO_CALL_OTHER
                FuguAppConstant.CUSTOM_ACTION_MESSAGE -> messageObject.rowType = FuguAppConstant.CUSTOM_ACTION
                FuguAppConstant.POLL_MESSAGE -> messageObject.rowType = FuguAppConstant.POLL_OTHER
            }
        }
        if (messageJson.has(FuguAppConstant.IS_MULTIPLE_SELECT)) {
            messageObject.multipleSelect = messageJson.getBoolean(FuguAppConstant.IS_MULTIPLE_SELECT)
        }
        if (messageJson.has(FuguAppConstant.QUESTION)) {
            messageObject.question = messageJson.getString(FuguAppConstant.QUESTION)
        }
        messageObject.total_votes = 0
        if (messageJson.has(FuguAppConstant.EXPIRY_TIME)) {
            messageObject.expireTime = messageJson.getLong(FuguAppConstant.EXPIRY_TIME)
        }
        val pollOptions = ArrayList<PollOption>()
        if (messageJson.has(FuguAppConstant.POLL_OPTIONS)) {
            val pollJson = messageJson.getJSONArray(FuguAppConstant.POLL_OPTIONS)
            for (i in 0..pollJson.length() - 1) {
                val jsonObject = pollJson.getJSONObject(i)
                val pollOption = PollOption()
                pollOption.label = jsonObject.getString("label")
                pollOption.users = ArrayList<User>()
                pollOption.pollCount = 0
                pollOption.puid = jsonObject.getString("puid")
                pollOptions.add(pollOption)
            }
            messageObject.pollOptions = pollOptions
            if (messageObject.pollOptions != null) {
                for (poll in 0..(messageObject.pollOptions.size - 1)) {
                    messageObject.pollOptions[poll].voteMap = HashMap()
                    for (user in (0..messageObject.pollOptions[poll].users.size - 1)) {

                        messageObject.pollOptions[poll].voteMap[messageObject.pollOptions[poll].users[user].userId] = messageObject.pollOptions[poll].users[user]
                    }
                }
            }
        }
        return messageObject
    }

    interface UpdateChatData {
        //        fun updateThreadMessageArray(updatedThreadedMessagesArray: ArrayList<String>, updatedThreadMessagesMap: LinkedHashMap<String, Int>)
        fun deleteMessageFromLocal(id: String)
    }
}