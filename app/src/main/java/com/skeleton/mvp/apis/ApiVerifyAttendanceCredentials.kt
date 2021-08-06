package com.skeleton.mvp.apis

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.retrofit.*
import id.zelory.compressor.Compressor
import org.json.JSONObject
import java.io.File

class ApiVerifyAttendanceCredentials {
    fun apiVerifyAttendanceCredentials(finalFile: File?,
                                       workspaceInfoList: ArrayList<WorkspacesInfo>,
                                       currentPosition: Int, context: Context, channelId: Long,
                                       isMockLocation: Boolean, lat: Double, lon: Double, action: String,
                                       message: String, isHrmBot: Boolean, verifyAttendanceCallback: VerifyAttendanceCallback) {
        val fuguSecretkey: String = workspaceInfoList[currentPosition].fuguSecretKey
        val mediaStorageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                FuguAppConstant.ATTENDANCE)
        val multipartBuilder = MultipartParams.Builder()
        if (finalFile != null) {
            val compressedImage = Compressor(context)
                    .setMaxWidth(1200)
                    .setMaxHeight(1200)
                    .setQuality(70)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(finalFile)
            multipartBuilder.addFile("files", compressedImage)
        }
        if (lat.toInt() != 0 && lon.toInt() != 0) {
            val location = JSONObject()
            location.put("latitude", lat.toString())
            location.put("longitude", lon.toString())
            multipartBuilder.add("location", location)
        } else {
            if (action.equals(FuguAppConstant.AttendanceAuthenticationLevel.LOCATION.toString()) || action.equals(FuguAppConstant.AttendanceAuthenticationLevel.BOTH.toString())) {
                if (isMockLocation) {
                    verifyAttendanceCallback.onMockLocationDetected()
                }
                return
            }
        }
        multipartBuilder.add(FuguAppConstant.EN_USER_ID, workspaceInfoList[currentPosition].enUserId)
        multipartBuilder.add("is_hrm_bot", isHrmBot)
        if (isHrmBot) {
            multipartBuilder.add(FuguAppConstant.CHANNEL_ID, channelId)
            multipartBuilder.add("authentication_level", action)
        }
        if (message.toLowerCase().equals("in") || message.toLowerCase().equals("/in")) {
            multipartBuilder.add("action", "in")
        } else if (message.toLowerCase().equals("out") || message.toLowerCase().equals("/out")) {
            multipartBuilder.add("action", "out")
        }

        RestClient.getApiInterface().verifyAttendanceCredentials(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fuguSecretkey, 1, BuildConfig.VERSION_CODE, multipartBuilder.build().map)
                .enqueue(object : ResponseResolver<CommonResponse>() {
                    override fun success(t: CommonResponse?) {
                        verifyAttendanceCallback.onSuccess(t)
                    }

                    override fun failure(error: APIError?) {
                        verifyAttendanceCallback.onFailure(error)
                    }
                })
    }

    interface VerifyAttendanceCallback {
        fun onMockLocationDetected()
        fun onSuccess(t: CommonResponse?)
        fun onFailure(error: APIError?)
    }
}