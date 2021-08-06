package com.skeleton.mvp.util

import android.content.Context
import android.text.TextUtils
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguFileDetails
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.UniqueIMEIID
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MessageFileJson {
    fun createFileJson(taggesUsers: ArrayList<Int>, textMessage: String, messageType: Int,
                       url: String?, thumbnailUrl: String?, image_url_100x100: String?,
                       fileDetails: FuguFileDetails?, uuid: String,
                       messageIndex: Int, userId: Long, userName: String,
                       channelId: Long, threadMuid: String, dimens: ArrayList<Int>, messageList: ArrayList<Message>,
                       context: Context): JSONObject? {
        val messageJson = JSONObject()
        val localDate = DateUtils.getFormattedDate(Date())
        try {


            when (messageType) {
                FuguAppConstant.IMAGE_MESSAGE -> messageJson.put(FuguAppConstant.DOCUMENT_TYPE, "image")
                FuguAppConstant.FILE_MESSAGE -> {
                    val link = url?.split("\\.".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                    val extension = link!![link.size - 1]
                    if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(extension)) {
                        messageJson.put(FuguAppConstant.DOCUMENT_TYPE, "audio")
                    } else {
                        messageJson.put(FuguAppConstant.DOCUMENT_TYPE, "file")
                    }
                }
                FuguAppConstant.VIDEO_MESSAGE -> messageJson.put(FuguAppConstant.DOCUMENT_TYPE, "video")
            }

            messageJson.put(FuguAppConstant.CHANNEL_ID, channelId)
            messageJson.put(FuguAppConstant.FULL_NAME, userName)
            messageJson.put(FuguAppConstant.MESSAGE, textMessage)
            messageJson.put(FuguAppConstant.FORMATTED_MESSAGE, FormatStringUtil.FormatString.getFormattedString(textMessage).get(1))
            messageJson.put(FuguAppConstant.MESSAGE_TYPE, messageType)
            messageJson.put(FuguAppConstant.DATE_TIME, DateUtils.getInstance().convertToUTC(localDate))
            messageJson.put(FuguAppConstant.MESSAGE_INDEX, messageList.size - 1)
            messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, uuid)
            if (messageType == FuguAppConstant.IMAGE_MESSAGE && !url!!.trim().isEmpty() && !thumbnailUrl!!.trim().isEmpty()) {
                messageJson.put(FuguAppConstant.IMAGE_URL, url)
                messageJson.put(FuguAppConstant.THUMBNAIL_URL, thumbnailUrl)
                if (fileDetails != null) {
                    if (!TextUtils.isEmpty(fileDetails.getFileName())) {
                        messageJson.put(FuguAppConstant.FILE_NAME, fileDetails.getFileName())
                    }
                    if (!TextUtils.isEmpty(fileDetails.getFileSize())) {
                        messageJson.put(FuguAppConstant.FILE_SIZE, fileDetails.getFileSize())
                    }
                    if (!TextUtils.isEmpty(image_url_100x100)) {
                        messageJson.put(FuguAppConstant.IMAGE_URL_100X100, image_url_100x100)
                    }
                }
            }
            if (!TextUtils.isEmpty(threadMuid)) {
                messageJson.put(FuguAppConstant.THREAD_MUID, threadMuid)
                messageJson.put(FuguAppConstant.IS_THREAD_MESSAGE, true)
            } else {
                messageJson.put(FuguAppConstant.IS_THREAD_MESSAGE, false)
            }

            if (messageType == FuguAppConstant.FILE_MESSAGE && url != null && !url.trim().isEmpty()) {
                messageJson.put("url", url)
                messageJson.put(FuguAppConstant.FILE_NAME, fileDetails?.getFileName())
                messageJson.put(FuguAppConstant.FILE_SIZE, fileDetails?.getFileSize())
            }
            if (messageType == FuguAppConstant.VIDEO_MESSAGE && url != null && !url.trim().isEmpty()
                    && thumbnailUrl != null && !thumbnailUrl.trim().isEmpty()) {
                messageJson.put("url", url)
                messageJson.put(FuguAppConstant.THUMBNAIL_URL, thumbnailUrl)
                if (image_url_100x100 != null && !image_url_100x100.trim().isEmpty()) {
                    messageJson.put(FuguAppConstant.IMAGE_URL_100X100, image_url_100x100)
                }
                messageJson.put(FuguAppConstant.FILE_NAME, fileDetails?.getFileName())
                messageJson.put(FuguAppConstant.FILE_SIZE, fileDetails?.getFileSize())
            }
            if (dimens.size == 2) {
                messageJson.put(FuguAppConstant.IMAGE_HEIGHT, dimens.get(0))
                messageJson.put(FuguAppConstant.IMAGE_WIDTH, dimens.get(1))
            }
            messageJson.put(FuguAppConstant.DEVICE_TOKEN, com.skeleton.mvp.data.db.CommonData.getFcmToken())
            val devicePayload = JSONObject()
            devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(context))
            devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
            devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
            devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(context))
            messageJson.put(FuguAppConstant.DEVICE_PAYLOAD, devicePayload)
            if (taggesUsers != null && taggesUsers.size != 0) {
                val jsonArrayTaggedUsers = JSONArray()
                for (id in taggesUsers) {
                    jsonArrayTaggedUsers.put(id)
                }
                messageJson.put(FuguAppConstant.TAGGED_USERS, jsonArrayTaggedUsers)
                if (jsonArrayTaggedUsers.toString().contains("-1")) {
                    messageJson.put(FuguAppConstant.TAGGED_ALL, true)
                }
            }
            messageJson.put(FuguAppConstant.MESSAGE_STATUS, FuguAppConstant.MESSAGE_UNSENT)
            messageJson.put(FuguAppConstant.USER_ID, userId.toString())
            messageJson.put(FuguAppConstant.USER_TYPE, FuguAppConstant.ANDROID_USER)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null
        }
        return messageJson
    }
}