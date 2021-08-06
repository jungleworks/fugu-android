package com.skeleton.mvp.community

/********************************
Created by Amandeep Chauhan     *
Date :- 29/06/2020              *
 ********************************/

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.`object`.ObjectResponse
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.model.getPublicInfo.Data
import com.skeleton.mvp.data.model.getPublicInfo.GetPublicInfoResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.databinding.ActivityJoinCommunityBinding
import com.skeleton.mvp.socket.SocketConnection.disconnectSocket
import com.skeleton.mvp.socket.SocketConnection.initSocketConnection
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.*
import com.skeleton.mvp.ui.UniqueIMEIID
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.intro.IntroActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.FuguUtils.Companion.getTimeZoneOffset
import com.skeleton.mvp.utils.showToast
import kotlinx.android.synthetic.main.activity_join_community.*
import java.util.*

class JoinCommunityActivity : BaseActivity(), CommunityListener {

    lateinit var workspace: String
    lateinit var publicInfo: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityJoinCommunityBinding>(this, R.layout.activity_join_community)
        val viewModel = ViewModelProvider(this).get(JoinCommunityViewModel::class.java)
        binding.joinCommunityViewModel = viewModel
        viewModel.communityListener = this
        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            Log.i("IntentData", intent.data?.toString())
            val splitArray = intent.data?.toString()!!.split("/")
            workspace = splitArray[3]
            showLoading()
            if (CommonData.getCommonResponse() != null && CommonData.getCommonResponse().data != null && CommonData.getCommonResponse().data.userInfo != null)
                RestClient.getApiInterface(true).getPublicInfo(CommonData.getCommonResponse().data.userInfo.accessToken, BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID, workspace, CommonData.getCommonResponse().data.userInfo.userId)
                        .enqueue(object : ResponseResolver<GetPublicInfoResponse>() {
                            override fun onSuccess(getPublicInfoResponse: GetPublicInfoResponse) {
                                handleOnSuccess(getPublicInfoResponse.data, true)
                            }

                            override fun onError(error: ApiError?) {
                                hideLoading()
                                showError(error?.message ?: "")
                            }

                            override fun onFailure(throwable: Throwable?) {
                                hideLoading()
                                showToast("Network Failure")
                            }

                        })
            else
                RestClient.getApiInterface(true).getPublicInfo(BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID, workspace)
                        .enqueue(object : ResponseResolver<GetPublicInfoResponse>() {
                            override fun onSuccess(getPublicInfoResponse: GetPublicInfoResponse) {
                                handleOnSuccess(getPublicInfoResponse.data, false)
                            }

                            override fun onError(error: ApiError?) {
                                hideLoading()
                                showError(error?.message ?: "Some Error Occurred")
                            }

                            override fun onFailure(throwable: Throwable?) {
                                hideLoading()
                                showToast("Network Failure")
                            }

                        })
        } else {
            finish()
        }

        etEmail.setOnEditorActionListener(TextView.OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                btnGetInvite.performClick()
            }
            false
        })
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val regexStr = "^[0-9]*$"
                if (s.isNotEmpty()) {
                    if (s.toString().matches(Regex(regexStr))) {
                        etCountryCode.visibility = View.VISIBLE
                        etEmail.setPadding(0, 18, 7, 18)
                    } else {
                        etCountryCode.visibility = View.GONE
                        etEmail.setPadding(40, 18, 7, 18)
                    }
                } else if (s.isEmpty()) {
                    etCountryCode.visibility = View.GONE
                    etEmail.setPadding(40, 18, 7, 18)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun handleOnSuccess(publicInfo: Data, isUserLoggedIn: Boolean) {
        hideLoading()
        if (publicInfo.isUserAlreadyExist) {
            val workspacesInfo = CommonData.getCommonResponse().data.workspacesInfo
            for (i in 0 until workspacesInfo.size) {
                if (workspacesInfo[i].workspace == workspace) {
                    CommonData.setCurrentSignedInPosition(i)
                    val commonResponseData = CommonData.getCommonResponse().getData()
                    disconnectSocket()
                    initSocketConnection(commonResponseData.getUserInfo().getAccessToken(),
                            commonResponseData.workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId, commonResponseData.userInfo.userId,
                            commonResponseData.userInfo.userChannel, "Switch/Join Open Community", false,
                            commonResponseData.userInfo.pushToken)
//                    val intent = Intent(this@JoinCommunityActivity, MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    startActivity(intent)
////                    finishAffinity()
                    startActivity(Intent(this@JoinCommunityActivity, MainActivity::class.java))
                    finishAffinity()
                    break
                }
            }
        } else if (publicInfo.publicInviteEnabled) {
            this.publicInfo = publicInfo
            tvJoinGreen.visibility = View.VISIBLE
            if (publicInfo.registeredUsers == 1)
                tvMembersCount.text = "${publicInfo.registeredUsers} member"
            else
                tvMembersCount.text = "${publicInfo.registeredUsers} members"
            tvMembersCount.visibility = View.VISIBLE
            tvCommunityName.text = publicInfo.workspaceName
            if (isUserLoggedIn) {
                llEmailPhone.visibility = View.GONE
                btnGetInvite.visibility = View.GONE
                llSignIn.visibility = View.GONE
                btnJoinCommunity.visibility = View.VISIBLE
            } else {
                llEmailPhone.visibility = View.VISIBLE
                btnGetInvite.visibility = View.VISIBLE
                llSignIn.visibility = View.VISIBLE
                btnJoinCommunity.visibility = View.GONE
            }
        } else {
            showError("The community is not open to public.")
        }
    }

    private fun apiGetInvite() {
        val commonParams = CommonParams.Builder()
        commonParams.add(WORKSPACE, workspace)
        commonParams.add("invitation_type", "PUBLIC_INVITATION")
        if (etCountryCode.visibility == View.VISIBLE) {
            commonParams.add(COUNTRY_CODE, etCountryCode.selectedCountryNameCode)
            commonParams.add(CONTACT_NUMBER, "+" + etCountryCode.selectedCountryCode + "-" + etEmail.text.toString().trim { it <= ' ' })
        } else
            commonParams.add(EMAIL, etEmail.text.toString().trim { it <= ' ' })
        showLoading()
        RestClient.getApiInterface(true).getPublicInvite(BuildConfig.VERSION_CODE, ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<com.skeleton.mvp.data.model.CommonResponse>() {
                    override fun onSuccess(commonResponse: com.skeleton.mvp.data.model.CommonResponse) {
                        hideLoading()
                        showError(commonResponse.message)
                    }

                    override fun onError(error: ApiError?) {
                        hideLoading()
                        showErrorMessage(error?.message)
                    }

                    override fun onFailure(throwable: Throwable?) {
                        hideLoading()
                    }

                })
    }

    private fun showError(errorMessage: String) {
        showErrorMessage(errorMessage, {
            this@JoinCommunityActivity.finish()
        }, {
            this@JoinCommunityActivity.finish()
        }, "Ok", "Cancel")
    }

    override fun onJoin() {
        val commonParams = CommonParams.Builder()
        commonParams.add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@JoinCommunityActivity))
        commonParams.add(DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        commonParams.add(DEVICE_DETAILS, com.skeleton.mvp.fugudatabase.CommonData.deviceDetails(this@JoinCommunityActivity))
        commonParams.add("email_token", publicInfo.emailToken)
        commonParams.add("invitation_type", "ALREADY_INVITED")
        showLoading()
        RestClient.getApiInterface(true).joinWorkspace(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<ObjectResponse>() {
                    override fun onSuccess(joinResponse: ObjectResponse?) {
                        hideLoading()
                        val info = ArrayList<WorkspacesInfo>()
                        info.addAll(CommonData.getCommonResponse().getData().workspacesInfo)
                        info.add(joinResponse!!.data)
                        info[info.size - 1].workspaceName = publicInfo.workspaceName
                        val fcCommonResponse = CommonData.getCommonResponse()
                        fcCommonResponse.getData().workspacesInfo = info
                        CommonData.setCommonResponse(fcCommonResponse)
                        val currentPosition = CommonData.getCommonResponse().getData().workspacesInfo.size - 1
                        CommonData.setCurrentSignedInPosition(currentPosition)
                        val commonResponseData = CommonData.getCommonResponse().getData()
                        disconnectSocket()
                        initSocketConnection(commonResponseData.getUserInfo().getAccessToken(),
                                commonResponseData.workspacesInfo[currentPosition].enUserId, commonResponseData.userInfo.userId,
                                commonResponseData.userInfo.userChannel, "Join Open Community", false,
                                commonResponseData.userInfo.pushToken)
                        val tokenLoginParams = CommonParams.Builder()
                        tokenLoginParams.add(TOKEN, CommonData.getFcmToken())
                        tokenLoginParams.add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@JoinCommunityActivity))
                        tokenLoginParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain())
                        val workspaceIds = ArrayList<Int?>()
                        for (i in CommonData.getCommonResponse().getData().workspacesInfo.indices) {
                            workspaceIds.add(CommonData.getCommonResponse().getData().workspacesInfo[i].workspaceId)
                        }
                        tokenLoginParams.add("user_workspace_ids", workspaceIds)
                        tokenLoginParams.add("time_zone", getTimeZoneOffset())
                        RestClient.getApiInterface(true).accessTokenLogin(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, tokenLoginParams.build().map)
                                .enqueue(object : ResponseResolver<FcCommonResponse>() {
                                    override fun onSuccess(fcCommonResponse: FcCommonResponse) {
                                        this@JoinCommunityActivity.hideLoading()
                                        CommonData.setCommonResponse(fcCommonResponse)
                                        for (i in 0 until fcCommonResponse.data.workspacesInfo.size - 1) {
                                            if (fcCommonResponse.data.workspacesInfo[i].workspaceId == joinResponse.data.workspaceId) {
                                                CommonData.setCurrentSignedInPosition(i)
                                                break
                                            }
                                        }
                                        this@JoinCommunityActivity.initDialog()
                                        this@JoinCommunityActivity.finishAffinity()
                                        this@JoinCommunityActivity.startActivity(Intent(this@JoinCommunityActivity, MainActivity::class.java))
                                        this@JoinCommunityActivity.overridePendingTransition(R.anim.left_in, R.anim.right_out)
                                    }

                                    override fun onError(error: ApiError) {
                                        this@JoinCommunityActivity.hideLoading()
                                        showErrorMessage(error.message)
                                    }

                                    override fun onFailure(throwable: Throwable) {
                                        this@JoinCommunityActivity.hideLoading()
                                    }
                                })

                    }

                    override fun onError(error: ApiError?) {
                        hideLoading()
                        showErrorMessage(error?.message)
                    }

                    override fun onFailure(throwable: Throwable?) {
                        hideLoading()
                        showToast("Network Failure")
                    }

                })
    }

    override fun onGetInvite() {
        if (etEmail.text.toString().isNotEmpty()) {
            apiGetInvite()
        } else {
            showErrorMessage(R.string.error_email_phoneno)
        }
    }

    override fun onSignIn() {
        startActivity(Intent(this@JoinCommunityActivity, IntroActivity::class.java))
    }

}
