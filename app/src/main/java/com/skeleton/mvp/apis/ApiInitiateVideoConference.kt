package com.skeleton.mvp.apis

import android.content.Context
import android.content.Intent
import android.text.format.Time
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.activity.VideoConfActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.addCalendarEvent.AddEventResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.retrofit.*
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.FuguUtils.Companion.randomVideoConferenceLink
import com.skeleton.mvp.utils.addDomain
import com.skeleton.mvp.utils.joinHangoutsCall
import org.json.JSONArray
import java.util.*

class ApiInitiateVideoConference {
    fun apiInitiateVideoConference(userIds: ArrayList<Long>, context: Context, enUserId: String, appSecretKey: String, isAudioMuted: Boolean = false, isVideoMuted: Boolean = false) {
        Log.d("InitiateCall", "Initiate Conference Call")
        val linkArray = randomVideoConferenceLink(10)
        val link = linkArray[0] + "/" + linkArray[1]
        val commonParams = CommonParams.Builder()
                .add("invite_link", link)
                .add(FuguAppConstant.EN_USER_ID, enUserId)
                .add("invite_user_ids", JSONArray(userIds))
                .build()
        inviteToConference(commonParams, appSecretKey, object : InviteToConferenceCallback {
            override fun onSuccess(t: CommonResponse?) {
                var prev: Fragment? = null
                if (context is ChatActivity)
                    prev = context.supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment")
                else if (context is MainActivity)
                    prev = context.supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment")
                if (prev != null) {
                    val df = prev as BottomSheetDialogFragment
                    df.dismiss()
                }
                val intent = Intent(
                        if (context is ChatActivity)
                            context
                        else
                            context as MainActivity, VideoConfActivity::class.java)
                intent.putExtra("base_url", linkArray[0])
                intent.putExtra("room_name", linkArray[1])
                intent.putExtra("invite_link", linkArray[0] + "/" + linkArray[1])
                intent.putExtra("is_audio_muted", isAudioMuted)
                intent.putExtra("is_video_muted", isVideoMuted)
                context.startActivity(intent)
            }

            override fun onError(error: ApiError?) {
                onError(error, context)
            }

            override fun onFailure(throwable: Throwable?) {
                onFailure(context)
            }
        })
    }

    fun apiInitiateHangoutsConference(userIds: ArrayList<Long>, context: Context, enUserId: String, appSecretKey: String) {
        Log.d("AddCalendarEvent", "AddCalendarEvent and Invite to Conference")
        val commonResponseData = CommonData.getCommonResponse().data
        val workspacesInfo = commonResponseData.workspacesInfo
        val currentPosition = CommonData.getCurrentSignedInPosition()
        val inviteArray = JSONArray(userIds)
        val commonParams = CommonParams.Builder()
                .add(FuguAppConstant.EN_USER_ID, workspacesInfo[currentPosition].enUserId)
                .add("is_scheduled", 0)
                .add("timezone", TimeZone.getTimeZone(Time.getCurrentTimezone()).getOffset(Date().time) / 1000 / 60)
                .add("attendees", inviteArray)
                .addDomain()
                .build()
        if (context is BaseActivity) context.showLoading()
        RestClient.getApiInterface().addCalendarEvent(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<AddEventResponse>() {
                    override fun success(addEventResponse: AddEventResponse?) {
                        if (context is BaseActivity) context.hideLoading()
                        val link = addEventResponse?.data?.hangoutLink
                        if (link == null) {
                            if (context is BaseActivity) context.showErrorMessage("Meet is disabled in your google account settings. Please change your connected google account.")
                            return
                        }
                        val inviteToConferenceParams = CommonParams.Builder()
                                .add("invite_link", link)
                                .add(FuguAppConstant.EN_USER_ID, enUserId)
                                .add("invite_user_ids", inviteArray)
                                .add("is_google_meet_conference", true)
                                .build()
                        inviteToConference(inviteToConferenceParams, appSecretKey, object : InviteToConferenceCallback {
                            override fun onSuccess(t: CommonResponse?) {
                                if (context is BaseActivity) context.hideLoading()
                                val prev = (
                                        if (context is ChatActivity)
                                            context
                                        else
                                            context as MainActivity).supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment")
                                if (prev != null) {
                                    val df = prev as BottomSheetDialogFragment
                                    df.dismiss()
                                }
                                context.joinHangoutsCall(link)
                            }

                            override fun onError(error: ApiError?) {
                                onError(error, context)
                            }

                            override fun onFailure(throwable: Throwable?) {
                                onFailure(throwable)
                            }
                        })
                    }

                    override fun failure(error: APIError?) {
                        if (context is BaseActivity) {
                            context.hideLoading()
                            context.showErrorMessage(error?.message
                                    ?: "Error")
                        }
                    }

                })


    }

    fun inviteToConference(commonParams: CommonParams, appSecretKey: String, inviteToConferenceCallback: InviteToConferenceCallback) {
        com.skeleton.mvp.data.network.RestClient.getApiInterface(false)
                .initiateVideoCall(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), appSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<CommonResponse>() {
                    override fun onSuccess(t: CommonResponse?) {
                        inviteToConferenceCallback.onSuccess(t)
                    }

                    override fun onError(error: ApiError?) {
                        inviteToConferenceCallback.onError(error)
                    }

                    override fun onFailure(throwable: Throwable?) {
                        inviteToConferenceCallback.onFailure(throwable)
                    }
                })
    }

    fun onError(error: ApiError?, context: Context) {
        if (context is BaseActivity) context.hideLoading()
        val prev = (
                if (context is ChatActivity)
                    context
                else
                    context as MainActivity).supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment")
        if (prev != null) {
            val df = prev as BottomSheetDialogFragment
            df.dismiss()
        }
        (if (context is ChatActivity)
            context
        else
            context as MainActivity).showErrorMessage(error?.message.toString())
    }

    fun onFailure(context: Context) {
        if (context is BaseActivity) context.hideLoading()
        val prev = (
                if (context is ChatActivity)
                    context
                else
                    context as MainActivity).supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment")
        if (prev != null) {
            val df = prev as BottomSheetDialogFragment
            df.dismiss()
        }
        (if (context is ChatActivity)
            context
        else
            context as MainActivity).showErrorMessage("Something went wrong. Please Try Again!")
    }

    interface InviteToConferenceCallback {
        fun onSuccess(t: CommonResponse?)
        fun onError(error: ApiError?)
        fun onFailure(throwable: Throwable?)
    }
}