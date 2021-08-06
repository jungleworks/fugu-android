package com.skeleton.mvp.apis

import android.content.Context
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.retrofit.*

class ApiJoinGroup {
    fun apiJoinGroup(enUserId: String, channelId: Long, appSecretKey: String, context: Context,
                     joinGroupCallback: JoinGroupCallback) {
        val commonParams = CommonParams.Builder()
                .add(FuguAppConstant.EN_USER_ID, enUserId)
                .add(FuguAppConstant.CHANNEL_ID, channelId)
                .build()
        RestClient.getApiInterface().joinGroup(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), appSecretKey, 1, BuildConfig.VERSION_CODE,
                commonParams.map)
                .enqueue(object : ResponseResolver<CommonResponse>(context as ChatActivity,
                        true, false) {
                    override fun success(commonResponse: CommonResponse) {
                        joinGroupCallback.onSuccess(commonResponse)
                    }

                    override fun failure(error: APIError) {
                    }
                })
    }

    interface JoinGroupCallback {
        fun onSuccess(commonResponse: CommonResponse)
    }
}