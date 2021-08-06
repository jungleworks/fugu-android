package com.skeleton.mvp.apis

import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.model.getAllMembers.AllMemberResponse

class ApiGetAllMembers {
    fun apiGetMembers(getMembersCallback: GetMembersCallback) {
        val commonParams: com.skeleton.mvp.data.network.CommonParams =
                com.skeleton.mvp.data.network.CommonParams.Builder().add("workspace_id",
                        com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceId)
                        .add("user_type", "ALL_MEMBERS")
                        .add("user_status", "ENABLED")
                        .add("page_start", 0)
                        .build()
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).getAllMembers(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.accessToken, BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID, commonParams.map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<AllMemberResponse>() {
                    override fun onSuccess(allMemberResponse: AllMemberResponse) {
                        getMembersCallback.onSuccess(allMemberResponse)
                    }

                    override fun onError(error: ApiError?) {
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }
                })
    }

    interface GetMembersCallback {
        fun onSuccess(allMemberResponse: AllMemberResponse)
    }
}
