package com.skeleton.mvp.calendar

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.webkit.WebChromeClient
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.databinding.ActivityAuthorizeGoogleBinding
import com.skeleton.mvp.retrofit.*
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.utils.addDomain
import kotlinx.android.synthetic.main.activity_authorize_google.*

class AuthorizeGoogleActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityAuthorizeGoogleBinding>(this, R.layout.activity_authorize_google)
        val viewModel = ViewModelProvider(this).get(AuthorizeGoogleViewModel::class.java)
        binding.authorizeGoogleViewModel = viewModel
        showLoading()
        val commonResponseData = CommonData.getCommonResponse().data
        val commonParams = CommonParams.Builder()
                .add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this))
                .add(FuguAppConstant.EN_USER_ID, commonResponseData.workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
                .addDomain()
                .build()
        RestClient.getApiInterface().getCalenderAuthorizeUrl(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<CommonResponse>() {
                    @SuppressLint("SetJavaScriptEnabled")
                    override fun success(response: CommonResponse) {
                        hideLoading()
                        val url = response.data.toString()
                        wvAuthorizeGoogle.webViewClient = AuthorizeGoogleWebClient(this@AuthorizeGoogleActivity)
                        val webSetting = wvAuthorizeGoogle.settings
                        webSetting.javaScriptEnabled = true
                        wvAuthorizeGoogle.webChromeClient = WebChromeClient()
//                        try {
//                            wvAuthorizeGoogle.settings.userAgentString = "Mozilla/5.0 (Linux; Android " + android.os.Build.VERSION.RELEASE + "; " + android.os.Build.MODEL + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.116 Mobile Safari/537.36 EdgA/45.05.4.5036"
//                        } catch (e: Exception) {
                        wvAuthorizeGoogle.settings.userAgentString = "Mozilla/5.0 (Linux; Android Device; Custom UserAgent) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.116 Mobile Safari/537.36 EdgA/45.05.4.5036"
//                        }
                        wvAuthorizeGoogle.loadUrl(url)
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                        showErrorMessage(error?.message ?: "Some Error Occurred.")
                    }

                })
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("You're about to cancel the account linking process. Are you sure you want to proceed?")
                .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                    super.onBackPressed()
                }
                .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
    }
}
