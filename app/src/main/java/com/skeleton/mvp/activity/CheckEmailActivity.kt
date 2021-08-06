package com.skeleton.mvp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.iid.FirebaseInstanceId
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.intro.IntroActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.FuguUtils
import com.skeleton.mvp.utils.FuguUtils.Companion.getTimeZoneOffset
import org.json.JSONObject
import java.net.SocketTimeoutException

/*******************************
Created by Amandeep Chauhan  *
Date :- 30/04/2020           *
 *******************************/

class CheckEmailActivity : BaseActivity(), View.OnClickListener {
    private var llChangeEmail: LinearLayout? = null
    private var tvCountdown: TextView? = null
    var fcCommonResponse: FcCommonResponse? = null
    lateinit var tvSubtitle: TextView
    private var btnSignin: AppCompatButton? = null
    private var btnResendEmail: AppCompatButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_email)
        initViews()
        addClickListeners()
    }

    private fun initViews() {
        PushReceiver.PushChannel.isEmailVerificationScreen = true
        btnSignin = findViewById(R.id.btnSignin)
        llChangeEmail = findViewById(R.id.llChangeEmail)
        btnResendEmail = findViewById(R.id.btnResendEmail)
        tvCountdown = findViewById(R.id.tvCountdown)
        tvSubtitle = findViewById<TextView>(R.id.tvSubtitile)
        var email = "your email."
        if (intent.hasExtra(AppConstants.EXTRA_EMAIL)) {
            email = intent.getStringExtra(AppConstants.EXTRA_EMAIL)!!
        }
        tvSubtitle.text = "We have sent a verification link to your email $email"
        if (intent.hasExtra(AppConstants.TOKEN) && intent.hasExtra(AppConstants.SIGNUP_TYPE)) {
            val token = intent.getStringExtra(AppConstants.TOKEN)!!
            if (intent.hasExtra(AppConstants.SIGNUP_TYPE)) {
                apiLoginViaAccessToken(AppConstants.BRANCH_IO, token, email, intent.getIntExtra(AppConstants.SIGNUP_TYPE, 1))
            }
        } else setCountdown()
    }

    private fun addClickListeners() {
        btnSignin!!.setOnClickListener(this)
        llChangeEmail!!.setOnClickListener(this)
        btnResendEmail!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.btnSignin || id == R.id.llChangeEmail) {
            finish()
        } else if (id == R.id.btnResendEmail) {
            resendEmail()
        }
    }

    private fun setCountdown() {
        btnResendEmail!!.visibility = View.GONE
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvCountdown!!.text = getRemainingTime(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                tvCountdown!!.visibility = View.GONE
                btnResendEmail!!.visibility = View.VISIBLE
            }
        }.start()
        tvCountdown!!.visibility = View.VISIBLE
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d("CheckEmail", "CheckEmail")
        PushReceiver.PushChannel.isEmailVerificationScreen = false
        if (intent != null && intent.hasExtra(AppConstants.EMAIL)) {

            val email = intent.getStringExtra(AppConstants.EMAIL)!!
            tvSubtitle.text = "We have sent a verification link to your email $email"
            if (intent.hasExtra(AppConstants.TOKEN) && intent.hasExtra(AppConstants.SIGNUP_TYPE)) {
                val token = intent.getStringExtra(AppConstants.TOKEN)!!
                if (intent.hasExtra(AppConstants.SIGNUP_TYPE)) {
//                    if (intent!!.getIntExtra(AppConstants.SIGNUP_TYPE, 1) == 1) {
//                    CommonData.setToken(token)
                    apiLoginViaAccessToken(AppConstants.BRANCH_IO, token, email, intent.getIntExtra(AppConstants.SIGNUP_TYPE, 1))
//                    } else {
//                        val setUserDetailsIntent = Intent(this@CheckEmailActivity, SetNewUserDetailsActivity::class.java)
//                        setUserDetailsIntent.putExtra(AppConstants.EXTRA_ACCESS_TOKEN, token)
//                        setUserDetailsIntent.putExtra(AppConstants.CONTACT_NUMBER_INTENT, false)
//                        setUserDetailsIntent.putExtra(AppConstants.EMAIL, email)
//                        setUserDetailsIntent.putExtra(AppConstants.SIGNUP_TYPE, intent!!.getIntExtra(AppConstants.SIGNUP_TYPE, 1))
//                        startActivity(setUserDetailsIntent)
//                        finishAffinity()
//                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
//                    }

//                    apiLoginViaAccessToken(AppConstants.BRANCH_IO, token, email, intent!!.getIntExtra(AppConstants.SIGNUP_TYPE, 1))
                } else
                    showErrorMessage("No SignUp Type Received.")
            }


        }
        super.onNewIntent(intent)
    }

    private fun getRemainingTime(value: Long): String {
        val seconds = Math.floor(value % 60.toDouble()).toInt()
        var duration = ""
        duration = if (seconds == 1) {
            "00:0$seconds sec"
        } else if (seconds < 10) {
            "00:0$seconds secs"
        } else {
            "00:$seconds secs"
        }
        return duration
    }

    private fun resendEmail() {
        tvCountdown!!.visibility = View.VISIBLE
        val loginParams = CommonParams.Builder()
        loginParams.add(AppConstants.DEVICE_DETAILS, CommonData.deviceDetails(this@CheckEmailActivity))
        loginParams.add(AppConstants.EMAIL, intent.getStringExtra(AppConstants.EXTRA_EMAIL))
        loginParams.add(AppConstants.DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain())
        showLoading()
        RestClient.getApiInterface(true).userLogin(BuildConfig.VERSION_CODE, AppConstants.ANDROID, loginParams.build().map)
                .enqueue(object : ResponseResolver<FcCommonResponse?>() {
                    override fun onSuccess(response: FcCommonResponse?) {
                        hideLoading()
                        Toast.makeText(this@CheckEmailActivity, "Email Sent Successfully.", Toast.LENGTH_SHORT).show()
                        setCountdown()
                    }

                    override fun onError(error: ApiError) {
                        hideLoading()
                        showErrorMessage(error.message)
                    }

                    override fun onFailure(throwable: Throwable) {
                        hideLoading()
                    }
                })
    }


    private fun apiLoginViaAccessToken(source: String, accessToken: String, email: String, signupType: Int) {
        val commonParams: CommonParams.Builder = getLoginViaAccessTokenParams(source)
        RestClient.getApiInterface(true).accessTokenLogin(accessToken, BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<FcCommonResponse>() {
                    override fun onSuccess(fcCommonResponse: FcCommonResponse?) {
                        hideLoading()
                        if (signupType == 1) {
                            CommonData.setCommonResponse(fcCommonResponse)
                            CommonData.setToken(accessToken)
                            val intent = Intent(this@CheckEmailActivity, MainActivity::class.java)
                            if (fcCommonResponse!!.getData()!!.workspacesInfo!!.size > 0) {
                                CommonData.setCurrentSignedInPosition(0)
                                for (i in 1 until fcCommonResponse.getData().workspacesInfo.size)
                                    fcCommonResponse.getData().workspacesInfo[i].currentLogin = false
                            }
                            startActivity(intent)
                            finishAffinity()
                            overridePendingTransition(R.anim.right_in, R.anim.left_out)
                        } else {
                            val setUserDetailsIntent = Intent(this@CheckEmailActivity, SetNewUserDetailsActivity::class.java)
                            setUserDetailsIntent.putExtra(AppConstants.EXTRA_ACCESS_TOKEN, accessToken)
                            setUserDetailsIntent.putExtra(AppConstants.CONTACT_NUMBER_INTENT, false)
                            setUserDetailsIntent.putExtra(AppConstants.EMAIL, email)
                            setUserDetailsIntent.putExtra("ShowYourSpaces", fcCommonResponse!!.data.openWorkspacesToJoin.size + fcCommonResponse.data.openWorkspacesToJoin.size > 0)
                            startActivity(setUserDetailsIntent)
                            finishAffinity()
                            overridePendingTransition(R.anim.right_in, R.anim.left_out)
                        }
                    }

                    override fun onError(error: ApiError?) {
                        hideLoading()
                        if (error?.statusCode == 401) {
                            CommonData.clearData()
                            FuguConfig.clearFuguData(this@CheckEmailActivity)
                            val sharedPreferences: SharedPreferences = this@CheckEmailActivity.getSharedPreferences("General", Context.MODE_PRIVATE)
                            val isFirstTimeOpened = sharedPreferences.getBoolean("isFirstTimeOpened", true)
                            if (!FuguUtils.isWhiteLabel() && isFirstTimeOpened)
                                startActivity(Intent(this@CheckEmailActivity, OnboardActivity::class.java))
                            else
                                startActivity(Intent(this@CheckEmailActivity, IntroActivity::class.java))
                            finishAffinity()
                        } else {
                            showErrorMessage(error?.message!!)
                        }
                    }

                    override fun onFailure(throwable: Throwable?) {
                        hideLoading()
                        if (throwable is SocketTimeoutException) {
                            try {
                                showErrorMessage("Please check your Internet connection and Try Again !") {
                                    val fcCommonResponse = CommonData.getCommonResponse()
                                    if (isNetworkConnected) {
                                        showLoading()
                                        if (fcCommonResponse.getData() != null && fcCommonResponse.getData().getUserInfo() != null) {
                                            apiLoginViaAccessToken(
                                                    "",
                                                    accessToken,
                                                    email,
                                                    signupType
                                            )
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }

                })
    }


    private fun getLoginViaAccessTokenParams(source: String): CommonParams.Builder {
        val commonParams = CommonParams.Builder()
        commonParams.add("time_zone", getTimeZoneOffset())
        if (!TextUtils.isEmpty(source)) {
            commonParams.add(AppConstants.SOURCE, source)
        }
        if (TextUtils.isEmpty(CommonData.getFcmToken())) {
            val token = FirebaseInstanceId.getInstance().token
            CommonData.updateFcmToken(token)
        }
        commonParams.add(AppConstants.TOKEN, CommonData.getFcmToken())
        commonParams.add(FuguAppConstant.DEVICE_ID, com.skeleton.mvp.ui.UniqueIMEIID.getUniqueIMEIId(this))
        val devicePayload = JSONObject()
        devicePayload.put(FuguAppConstant.DEVICE_ID, com.skeleton.mvp.utils.UniqueIMEIID.getUniqueIMEIId(this))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        commonParams.add(FuguAppConstant.DEVICE_DETAILS, com.skeleton.mvp.fugudatabase.CommonData.deviceDetails(this))
        val workspaceIds = java.util.ArrayList<Int>()
        commonParams.add("user_workspace_ids", workspaceIds)
        return commonParams
    }

    override fun onBackPressed() {
        PushReceiver.PushChannel.isEmailVerificationScreen = false
        super.onBackPressed()
    }
}