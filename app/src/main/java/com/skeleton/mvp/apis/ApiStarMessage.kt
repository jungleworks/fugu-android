package com.skeleton.mvp.apis

import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.model.CommonResponse
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.ResponseResolver

class ApiStarMessage {
    fun starMessage(muid: String, isStarred: Int,
                     channelId: Long,
                    enUserId: String, workspaceInfoList: ArrayList<WorkspacesInfo>,
                    currentPosition: Int, starMessageCallBack: StarMessageCallBack) {
        val commonParams = com.skeleton.mvp.data.network.CommonParams.Builder()
        commonParams.add(FuguAppConstant.MESSAGE_UNIQUE_ID, muid)
        if (isStarred == 1) {
            commonParams.add("is_starred", 0)
        } else {
            commonParams.add("is_starred", 1)
        }
        commonParams.add(FuguAppConstant.CHANNEL_ID, channelId)
        commonParams.add(FuguAppConstant.EN_USER_ID, enUserId)
        com.skeleton.mvp.data.network.RestClient.getApiInterface(false).starMessage(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfoList[currentPosition].fuguSecretKey,
                1, BuildConfig.VERSION_CODE, commonParams.build().map).enqueue(object : ResponseResolver<CommonResponse>() {
            override fun success(t: com.skeleton.mvp.data.model.CommonResponse?) {
                starMessageCallBack.onSuccess(t)
            }

            override fun failure(error: APIError?) {
                starMessageCallBack.onFailure(error)
            }
        })
    }

    interface StarMessageCallBack {
        fun onSuccess(t: com.skeleton.mvp.data.model.CommonResponse?)
        fun onFailure(error: APIError?)
    }
}