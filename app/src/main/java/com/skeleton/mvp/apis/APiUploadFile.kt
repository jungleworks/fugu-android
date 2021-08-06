package com.skeleton.mvp.apis

import android.app.AlertDialog
import android.text.TextUtils
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguFileDetails
import com.skeleton.mvp.model.FuguUploadImageResponse
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.MultipartParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.retrofit.RestClient
import com.skeleton.mvp.util.GeneralFunctions
import com.skeleton.mvp.utils.ProgressRequestBody
import okhttp3.MultipartBody
import java.io.File

class APiUploadFile {
    fun uploadFileServerCall(taggesUsers: java.util.ArrayList<Int>, textMessage: String,
                             uuid: String, url: String, messageIndex: Int, messageType: Int,
                             fileDetails: FuguFileDetails?, dimens: ArrayList<Int>,
                             fileBody: ProgressRequestBody, filePart: MultipartBody.Part,
                             workspaceInfoList: ArrayList<WorkspacesInfo>, currentPosition: Int,
                             uploadFileCallback: UploadFileCallback) {
        val fuguSecretkey: String = workspaceInfoList[currentPosition].fuguSecretKey
        try {
            val multipartBuilder = MultipartParams.Builder()
            val multipartParams = multipartBuilder
                    .add(FuguAppConstant.APP_SECRET_KEY, fuguSecretkey)
                    .add(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_CODE)
                    .add("file_name", fileDetails?.fileName)
                    .add(FuguAppConstant.MESSAGE_TYPE, messageType)
                    .add(FuguAppConstant.DEVICE_TYPE, 1).build()
            RestClient.getApiInterface().uploadFile(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fuguSecretkey, 1, BuildConfig.VERSION_CODE, filePart, multipartParams.map)
                    .enqueue(object : ResponseResolver<FuguUploadImageResponse>() {
                        override fun success(fuguUploadImageResponse: FuguUploadImageResponse?) {
                            uploadFileCallback.onSuccess(fuguUploadImageResponse)
                        }

                        override fun failure(error: APIError?) {
                            uploadFileCallback.onFailure(error)
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface UploadFileCallback {
        fun onSuccess(fuguUploadImageResponse: FuguUploadImageResponse?)
        fun onFailure(error: APIError?)
    }

}