package com.skeleton.mvp.utils

/********************************
Created by Amandeep Chauhan     *
Date :- 05/06/2020              *
 ********************************/

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.service.OngoingCallService
import com.skeleton.mvp.service.VideoCallService
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.videoCall.WebRTCCallConstants
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FuguUtils {
    companion object {
        fun randomVideoConferenceLink(sizeOfRandomString: Int = 10): ArrayList<String> {
            val linkArray = ArrayList<String>()
            val allowedCharacters = "qwertyuiopasdfghjklzxcvbnm"
            val random = Random()
            val sb = StringBuilder(sizeOfRandomString)
            for (i in 0 until sizeOfRandomString) {
                sb.append(allowedCharacters[random.nextInt(allowedCharacters.length)])
            }
            linkArray.add(CommonData.getConferenceUrl())
            linkArray.add(sb.toString())
            return linkArray
        }

        /**
         *  Check if the environment is set to test ot not
         */
        fun isTestEnvironment(): Boolean {
            return CommonData.getFuguServerUrl() == FuguAppConstant.TEST_SERVER
        }

        /**
         *  Check if the current project is a whitelabel or fugu
         */
        fun isWhiteLabel(): Boolean {
            return BuildConfig.APPLICATION_ID != "com.officechat"
        }

        /**
         *  Get current user's timezone offset in minutes
         */
        fun getTimeZoneOffset(): Int {
            val calendar = Calendar.getInstance(Locale.getDefault())
            return (calendar[Calendar.ZONE_OFFSET] + calendar[Calendar.DST_OFFSET]) / (60 * 1000)
        }

        /**
         *  Get domain
         */
        fun getDomain(): String {
            return CommonData.getDomain()
        }

        fun getFirstCharInUpperCase(str: String?): String {
            return if (TextUtils.isEmpty(str)) "" else str!!.substring(0, 1).toUpperCase(Locale.getDefault())
        }

        /**
         *  Get background resource id based on some id
         */
        fun getBgResIdFromSomeId(someId: Long): Int {
            return when (abs(someId % 10)) {
                0L -> R.drawable.background_teal
                1L -> R.drawable.background_green
                2L -> R.drawable.background_indigo
                3L -> R.drawable.background_violet
                4L -> R.drawable.background_red
                5L -> R.drawable.background_mehndi
                6L -> R.drawable.background_golden
                7L -> R.drawable.background_sand
                8L -> R.drawable.background_pink_dark
                9L -> R.drawable.background_pink_light
                else -> {
                    R.drawable.background_teal
                }
            }
        }
    }
}

/**
 *  Make Views Visible .
 *
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}


/**
 *  Make Views Invisible .
 *
 */
fun View.invisible() {
    this.visibility = View.INVISIBLE
}


/**
 *  Make Views Gone .
 *
 */
fun View.gone() {
    this.visibility = View.GONE
}

/**
 *  Display toast directly from any context.
 *
 *  @param message A message string to be displayed by the toast. *
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 *  Display toast directly from any context for long duration.
 *
 *  @param message A message string to be displayed by the toast. *
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 *  Add domain to CommonParams to com.skeleton.mvp.retrofit
 */
fun CommonParams.Builder.addDomain(): CommonParams.Builder {
    add(AppConstants.DOMAIN, CommonData.getDomain())
    return this@addDomain
}

/**
 *  Add domain to CommonParams to com.skeleton.mvp.data.network
 */
fun com.skeleton.mvp.data.network.CommonParams.Builder.addDomain(): com.skeleton.mvp.data.network.CommonParams.Builder {
    add(AppConstants.DOMAIN, CommonData.getDomain())
    return this@addDomain
}

fun Context.getDeviceDetails(): JSONObject {
    val devicePayload = JSONObject()
    try {
        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this))
    } catch (e: java.lang.Exception) {
        Log.e("getDeviceDetails() ----> ", e.message)
        devicePayload.put(FuguAppConstant.DEVICE_ID, "UNKNOWN")
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, JSONObject())
    }
    return devicePayload
}

fun Context.openFile(file: File) {
    try {
        val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        val fileOpenIntent = Intent(Intent.ACTION_VIEW)
        val filePath = file.absolutePath
        val extension = filePath.substring(filePath.lastIndexOf(".") + 1)
        var mimeType = FuguMimeUtils.guessMimeTypeFromExtension(extension.toLowerCase())
        if (mimeType == null)
            mimeType = "*/*"
        fileOpenIntent.setDataAndType(uri, mimeType)
        fileOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP + Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_ACTIVITY_NO_HISTORY
        this.startActivity(fileOpenIntent)
    } catch (e: Exception) {
        AlertDialog.Builder(this)
                .setMessage("You may not have a proper app for viewing this content.")
                .setPositiveButton("Ok", null)
                .setCancelable(false)
                .show()
    }
}

fun getFileExtension(fileName: String): String? {
    val i = fileName.lastIndexOf('.')
    return if (i > 0) {
        fileName.substring(i + 1)
    } else null
}

/**
 *  Gets an Intent object which connects to Google Meet passed in meetLink parameter
 *
 *  @param meetLink Link for Google Meet to join
 */
fun getHangoutsIntent(meetLink: String, isOldMeetApp: Boolean): Intent {
    return try {
        val intent = Intent(Intent.ACTION_VIEW)
        if (isOldMeetApp)
            intent.component = ComponentName.unflattenFromString("com.google.android.apps.meetings/com.google.android.apps.meetings.splash.SplashActivity")
        else
            intent.component = ComponentName.unflattenFromString("com.google.android.apps.meetings/com.google.android.libraries.communications.conference.ui.intents.ConferenceUrlHandlerActivity")
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse(meetLink)
        intent
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(meetLink)
        intent
    }
}

/**
 *  Join google meet using the link
 *
 *  @param meetLink Link for Google Meet to join
 */
fun Context.joinHangoutsCall(meetLink: String) {
    try {
        startActivity(getHangoutsIntent(meetLink, false))
    } catch (e: Exception) {
        try {
            startActivity(getHangoutsIntent(meetLink, true))
        } catch (e: Exception) {
            Log.e("joinHangoutsCall", e.message)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(meetLink)
            startActivity(i)
        }
    }
}

fun Context.stopCallForegroundService(isHungUpToBeSent: Boolean) {
    try {
        val startIntent = Intent(this, VideoCallService::class.java)
        startIntent.action = "com.officechat.start"
        startIntent.putExtra("isHungUpToBeSent", isHungUpToBeSent)
        stopService(startIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Format a Date object to HH:MM am/pm
 *
 * @param date An object of Date
 * @return The formatted string with 12 Hours time including AM/PM
 */
fun getFormattedTime(date: Date): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    return sdf.format(date).toUpperCase(Locale.ENGLISH)
}

/**
 * Format a Date object to dd MMM yyyy
 *
 * @param date An object of Date
 * @return The formatted string like 25 Nov 1997
 */
fun getFormattedDate(date: Date): String? {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    return sdf.format(date)
}

fun emitAnswerEvent(dataIntent: Intent, context: Context? = null) {
    val startCallJson = JSONObject()
    startCallJson.put(FuguAppConstant.IS_SILENT, true)
    startCallJson.put(WebRTCCallConstants.VIDEO_CALL_TYPE, WebRTCCallConstants.Companion.JitsiCallType.ANSWER_CONFERENCE.toString())
    val workspaceInfoList =
            com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
    var userId = workspaceInfoList[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId
    for (workspaceInfo in workspaceInfoList) {
        if (workspaceInfo.fuguSecretKey == dataIntent.getStringExtra(FuguAppConstant.APP_SECRET_KEY)) {
            userId = workspaceInfo.userId
            break
        }
    }
    startCallJson.put(FuguAppConstant.USER_ID, userId)
    startCallJson.put(FuguAppConstant.CHANNEL_ID, dataIntent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L))
    startCallJson.put(FuguAppConstant.MESSAGE_TYPE, WebRTCCallConstants.VIDEO_CALL)
    startCallJson.put(FuguAppConstant.CALL_TYPE, dataIntent.getStringExtra(FuguAppConstant.CALL_TYPE)
            ?: "VIDEO")
    startCallJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, OngoingCallService.NotificationServiceState.muid)
    startCallJson.put(WebRTCCallConstants.DEVICE_PAYLOAD, context?.getDeviceDetails()
            ?: JSONObject())
    startCallJson.put(FuguAppConstant.INVITE_LINK, dataIntent.getStringExtra(FuguAppConstant.INVITE_LINK))
    SocketConnection.sendMessage(startCallJson)
}