package com.skeleton.mvp.util

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.*
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.adapter.FuguBotAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.mentions.Mention
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.UniqueIMEIID
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class GeneralFunctions {
    fun spannableRetryText(textView: TextView, errorMsg: String, retry: String) {
        val spanText = SpannableStringBuilder()
        spanText.append(errorMsg)
        spanText.append(retry)
        val txtSpannable = SpannableString(spanText)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(UnderlineSpan(), spanText.length - 5, spanText.length, 0)
        txtSpannable.setSpan(boldSpan, spanText.length - 5, spanText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(txtSpannable, TextView.BufferType.SPANNABLE)
    }

    fun getTypingObject(userName: String, userId: Long, channelId: Long): JSONObject {
        val typingJson = JSONObject()
        typingJson.put(FuguAppConstant.FULL_NAME, userName)
        typingJson.put(FuguAppConstant.USER_ID, userId)
        typingJson.put(FuguAppConstant.CHANNEL_ID, channelId)
        return typingJson
    }

    fun getTaggedMessage(mentionsList: ArrayList<Mention>, editText: EmojiGifEditText): String {
        val initialMessage = editText.text.toString().trim { it <= ' ' }
        val removeAmp = initialMessage.replace("&".toRegex(), "&amp;")
        val removeLt = removeAmp.replace("<".toRegex(), "&lt;")
        val removeGt = removeLt.replace(">".toRegex(), "&gt;")
        val removeQuotes = removeGt.replace("\"".toRegex(), "&quot;")
        var finalMessage = removeQuotes.replace("'".toRegex(), "&#39;")
        try {
            for (i in 0 until mentionsList.size) {
                if (finalMessage.contains(mentionsList[i].mentionName)) {
                    val prefix = "<a style=\"color=#007bff;text-decoration:none\" contenteditable=\"false\" data-uid=\"" + mentionsList[i].userId + "\" href=\"" + "mention://" + mentionsList[i].userId + "\" class=\"tagged-agent\">"
                    val postfix = "</a>"
                    // Commenting it out to solve the bug of @Everybody getting untagged on editing the message
//                    val prefixEverybody = "<a style=\"color=#007bff;text-decoration:none\" contenteditable=\"false\" data-uid=\"" + mentionsList[i].userId + "\" href=\"" + "mention://" + mentionsList[i].userId + "\">"
//                    finalMessage = if (mentionsList[i].mentionName == "@Everyone") {
//                        finalMessage.replace(mentionsList[i].mentionName.toString(), prefixEverybody + mentionsList[i].mentionName + postfix, ignoreCase = true)
//                    } else {
//                        finalMessage.replace(mentionsList[i].mentionName.toString(), prefix + mentionsList[i].mentionName + postfix, ignoreCase = true)
//                    }
                    finalMessage = finalMessage.replace(mentionsList[i].mentionName.toString(), prefix + mentionsList[i].mentionName + postfix, ignoreCase = true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return finalMessage
    }

    fun getFromSdcardAndDelete(channelId: Long, workspaceInfoList: ArrayList<WorkspacesInfo>, currentPosition: Int) {
        var listFile: Array<File>? = null
        try {
            val filesNormal = File(Environment.getExternalStorageDirectory(), FuguAppConstant.APP_NAME_SHORT +
                    File.separator + workspaceInfoList[currentPosition].workspaceName?.replace(" ".toRegex(), "")?.replace("'s".toRegex(), "")
                    + File.separator + FuguAppConstant.IMAGE)

            val filesPrivate = File(Environment.getExternalStorageDirectory(), FuguAppConstant.APP_NAME_SHORT +
                    File.separator + workspaceInfoList[currentPosition].workspaceName?.replace(" ".toRegex(), "")?.replace("'s".toRegex(), "")
                    + File.separator + FuguAppConstant.PRIVATE_IMAGES)

            if (filesNormal.isDirectory) {
                listFile = filesNormal.listFiles()
                for (i in listFile!!.indices) {
                    val exifFile = ExifInterface(listFile[i].absolutePath)
                    if (!TextUtils.isEmpty(exifFile.getAttribute(ExifInterface.TAG_MAKE)) && exifFile.getAttribute(ExifInterface.TAG_MAKE)!!.contains(channelId.toString())) {
                        listFile.get(i).delete()
                    }
                }
            }
            if (filesPrivate.isDirectory) {
                listFile = filesPrivate.listFiles()
                for (i in listFile!!.indices) {
                    val exifFile = ExifInterface(listFile[i].absolutePath)
                    if (!TextUtils.isEmpty(exifFile.getAttribute(ExifInterface.TAG_MAKE)) && exifFile.getAttribute(ExifInterface.TAG_MAKE)!!.contains(channelId.toString())) {
                        listFile.get(i).delete()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isMyChannelId(channelId: Long, messageJson: JSONObject): Boolean {
        return try {
            (messageJson.getLong(FuguAppConstant.CHANNEL_ID).compareTo(channelId) == 0)
        } catch (e: Exception) {
            false
        }
    }

    fun foregrounded(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
    }

    fun isGPSTurnedOn(context: Context): Boolean {
        try {
            val provider = Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
            return provider.contains("gps")
        } catch (e: Exception) {
            return false
        }
    }

    fun setToolbarLabel(label: String, businessName: String, tvTitle: TextView, chatType: Int, leaveType: String) {
        if (!TextUtils.isEmpty(label)) {
            setToolBarText(label, tvTitle, chatType, leaveType)
        } else {
            setToolBarText(businessName, tvTitle, chatType, leaveType)
        }
    }

    fun setToolBarText(businessName: String?, tvTitle: TextView, chatType: Int, leaveType: String?) {
        //tvTitle.text = businessName
        val text = SpannableStringBuilder()
        //text.append(ellipsizeText(fuguGetMessageResponse.data.label))
        text.append(ellipsizeText(businessName!!))
        if (chatType == FuguAppConstant.ChatType.O2O && leaveType?.toLowerCase().equals("absent")) {
            text.append(smallText(" (on leave)"))
        } else if (chatType == FuguAppConstant.ChatType.O2O && leaveType?.toLowerCase().equals("work_from_home")) {
            text.append(smallText(" (on WFH)"))
        }
        tvTitle.text = text
    }

    fun sendPollOption(jsonObject: JSONObject, channelId: Long, userId: Long,
                       workspaceInfoList: ArrayList<WorkspacesInfo>, currentPosition: Int, context: Context) {
        jsonObject.put("message_poll", true)
        jsonObject.put(FuguAppConstant.CHANNEL_ID, channelId)
        jsonObject.put(FuguAppConstant.MESSAGE_TYPE, FuguAppConstant.POLL_MESSAGE)
        jsonObject.put(FuguAppConstant.IS_TYPING, 0)
        jsonObject.put(FuguAppConstant.USER_ID, userId)
        jsonObject.put(FuguAppConstant.FULL_NAME, workspaceInfoList[currentPosition].fullName)
        val devicePayload = JSONObject()
        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(context))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(context))
        jsonObject.put("device_payload", devicePayload)
        SocketConnection.sendPollVote(jsonObject)
    }

    fun isMyServiceRunning(serviceClass: String?, context: Context): Boolean {
        val manager: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
            if (service.service.className.contains(serviceClass!!)) {
                return true
            }
        }
        return false
    }

    fun removeNotification(channelId: Long) {
        Thread {
            kotlin.run {
                try {
                    ChatDatabase.removeNotifications(channelId)
                    ChatDatabase.setPushCount(channelId, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun resetChat(chatType: Int, selectedBotAction: FuguBotAdapter.BotAction?, channelId: Long,
                  mentionsList: ArrayList<Mention>, etMessage: EmojiGifEditText,
                  messageList: ArrayList<Message>, messagesMap: LinkedHashMap<String, Message>) {
        PushReceiver.PushChannel.pushChannelId = -2L
        Thread {
            kotlin.run {
                if (chatType == FuguAppConstant.ChatType.BOT && selectedBotAction != null) {
                    ChatDatabase.setUnsentTypedBotMessage(selectedBotAction, channelId)
                }
                ChatDatabase.setUnsentTypedMessage(GeneralFunctions().getTaggedMessage(mentionsList, etMessage), channelId)
                ChatDatabase.setMentions(mentionsList, channelId)
                ChatDatabase.setMessageList(messageList, channelId)
                ChatDatabase.setMessageMap(messagesMap, channelId)
            }
        }.start()
    }

    fun copyUrl(url: String, context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", url)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Link Copied to Clipboard", Toast.LENGTH_SHORT).show()
    }

    fun copyMessage(context: Context, messageList: ArrayList<Message>, position: Int) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val message = messageList[position].message.replace("<br/>", "fuguLineBreak").replace("<br>", "fuguLineBreak").replace("\n", "fuguLineBreak")
        val clip = ClipData.newPlainText("", Html.fromHtml(message).toString().replace("fuguLineBreak", "\n"))
        clipboard.setPrimaryClip(clip)
    }

    fun getMimeType(url: String, context: Context): String? {
        var type: String?
        val extensions = url.split(Pattern.quote(".").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val extension = extensions[extensions.size - 1]
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        if (type == null) {
            try {
                val contentURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", File(url))
                type = context.contentResolver.getType(contentURI)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return type
    }

    fun getRealPathFromURI(contentURI: Uri?, context: Context): String {
        var result: String
        val cursor = context.contentResolver.query(contentURI!!, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path!!
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            try {
                result = cursor.getString(idx)
            } catch (e: java.lang.Exception) {
                result = contentURI.path!!
            }
            cursor.close()
        }
        return result
    }

    fun getDirectory(extension: String, workspaceInfoList: ArrayList<WorkspacesInfo>, currentPosition: Int): String? {
        try {
            var filePath = (Environment.getExternalStorageDirectory()).toString() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + CommonData.getWorkspaceResponse(workspaceInfoList.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]!!.directory
            val folder = File(filePath)
            val filePathArray = filePath.split("/")
            if (filePathArray[filePathArray.size - 1].equals(FuguAppConstant.IMAGE)) {
                if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[currentPosition].mediaVisibility == 0) {
                    filePath = filePath.replace(FuguAppConstant.IMAGE, FuguAppConstant.PRIVATE_IMAGES)
                }
            }
            if (!folder.exists()) {
                folder.mkdirs()
            }
            if (filePathArray[filePathArray.size - 1].equals(FuguAppConstant.IMAGE)) {
                if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[currentPosition].mediaVisibility == 0) {
                    val f = File(filePath + "/.nomedia")
                    if (!f.exists()) {
                        f.createNewFile()
                    }
                }
            }
            return filePath
        } catch (e: Exception) {
            return null
        }
    }

    fun ellipsizeText(text: String): CharSequence {
        val s = SpannableString(text)
        s.setSpan(TrimmedTextView.EllipsizeRange.ELLIPSIS_AT_END, 0, s.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return s
    }

    fun smallText(text: String): CharSequence {
        val s = SpannableString(text)
        s.setSpan(RelativeSizeSpan(0.7f), 0, text.length, 0) // set size
        return s
    }

    fun checkIfExpired(notification_snooze_time: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        var timeInMilliseconds = 0L
        try {
            val mDate = sdf.parse(DateUtils.getInstance().convertToLocal(notification_snooze_time))
            timeInMilliseconds = mDate.time
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return (timeInMilliseconds.compareTo(calendar.timeInMillis)) < 0
    }

}