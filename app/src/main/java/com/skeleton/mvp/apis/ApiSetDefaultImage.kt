package com.skeleton.mvp.apis

import android.content.Context
import android.graphics.Bitmap
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.retrofit.*
import id.zelory.compressor.Compressor
import java.io.File

class ApiSetDefaultImage {
    fun apiSetDefaultImage(finalFile: File, workspaceInfoList: ArrayList<WorkspacesInfo>, currentPosition: Int,
                           context: Context, cameraMuid: String, channelId: Long, defaultImageCallback: DefaultImageCallback) {
        val fuguSecretKey: String = workspaceInfoList[currentPosition].fuguSecretKey
        val multipartBuilder = MultipartParams.Builder()
        val compressedImage = Compressor(context)
                .setMaxWidth(1200)
                .setMaxHeight(1200)
                .setQuality(70)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFile(finalFile)
        multipartBuilder.addFile("files", compressedImage)
        multipartBuilder.add(FuguAppConstant.MESSAGE_UNIQUE_ID, cameraMuid)
        multipartBuilder.add(FuguAppConstant.CHANNEL_ID, channelId)
        multipartBuilder.add(FuguAppConstant.EN_USER_ID, workspaceInfoList[currentPosition].enUserId)
        RestClient.getApiInterface().uploadDefaultImage(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fuguSecretKey, 1, BuildConfig.VERSION_CODE, multipartBuilder.build().map)
                .enqueue(object : ResponseResolver<CommonResponse>() {
                    override fun success(t: CommonResponse?) {
                        defaultImageCallback.onSuccess(t)
                    }

                    override fun failure(error: APIError?) {
                        defaultImageCallback.onFailure(error)
                    }

                })
    }

    interface DefaultImageCallback {
        fun onSuccess(t: CommonResponse?)
        fun onFailure(error: APIError?)
    }
}