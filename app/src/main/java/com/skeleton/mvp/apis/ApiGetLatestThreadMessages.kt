package com.skeleton.mvp.apis

import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.model.threadMessage.LatestThreadedMessagesResponse
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.retrofit.RestClient
import org.json.JSONArray

class ApiGetLatestThreadMessages {
    fun apiGetLatestThreadMessage(threadedMessagesArray: ArrayList<String>, enUserId: String,
                                  channelId: Long, appSecretKey: String, latestThreadMessagesCallBack: LatestThreadMessagesCallBack) {
        val mJSONArray = JSONArray(threadedMessagesArray)
        val commonParams = CommonParams.Builder()
                .add("muids", mJSONArray)
                .add(FuguAppConstant.EN_USER_ID, enUserId)
                .add(FuguAppConstant.CHANNEL_ID, channelId)
                .build()
        RestClient.getApiInterface().getLatestThreadedMessages(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), appSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<LatestThreadedMessagesResponse>() {
                    override fun success(latestThreadedMessagesResponse: LatestThreadedMessagesResponse?) {
                        latestThreadMessagesCallBack.onSuccess(latestThreadedMessagesResponse)
                    }

                    override fun failure(error: APIError?) {
                    }
                })
    }

    interface LatestThreadMessagesCallBack {
        fun onSuccess(latestThreadedMessagesResponse: LatestThreadedMessagesResponse?)
    }
}