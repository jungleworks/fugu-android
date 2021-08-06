package com.skeleton.mvp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.iid.FirebaseInstanceId
import com.hbb20.CountryCodePicker
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.`object`.ObjectResponse
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.fragment.TermsConditionFragment
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.socket.SocketConnection.disconnectSocket
import com.skeleton.mvp.socket.SocketConnection.initSocketConnection
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.UniqueIMEIID
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.intro.IntroActivity
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity
import com.skeleton.mvp.utils.FuguUtils
import com.skeleton.mvp.utils.FuguUtils.Companion.getTimeZoneOffset
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.*

class SetNewUserDetailsActivity : BaseActivity(), View.OnClickListener {
    private var etWorkspaceName: EditText? = null
    private var etFullName: EditText? = null
    private var etPhoneNumber: EditText? = null
    private var btnContinue: AppCompatButton? = null
    private var termsConditionCheckbox: CheckBox? = null
    private var tvPhoneNumber: TextView? = null
    private var tvWorkspaceName: TextView? = null
    private var llPhoneNumber: LinearLayout? = null
    private var etCountryCode: CountryCodePicker? = null
    private var isPhoneSignup: Boolean = true
    var hasOpenWorkspaces = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_new_user_details)
        hasOpenWorkspaces = intent.getBooleanExtra("ShowYourSpaces", false)
        PushReceiver.PushChannel.isEmailVerificationScreen = false
        initViews()
        clickListeners()
        etFullName!!.requestFocus()
        if (hasOpenWorkspaces) {
            etWorkspaceName!!.visibility = View.GONE
            tvWorkspaceName!!.visibility = View.GONE
        }
        if (intent.hasExtra(AppConstants.EMAIL)) {
            isPhoneSignup = false
            tvPhoneNumber!!.visibility = View.VISIBLE
            llPhoneNumber!!.visibility = View.VISIBLE
            etPhoneNumber!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val regexStr = "^[0-9]*$"
                    if (s.isNotEmpty()) {
                        if (s.toString().matches(Regex.fromLiteral(regexStr))) {
                            etCountryCode!!.visibility = View.VISIBLE
                            etPhoneNumber!!.setPadding(0, 18, 7, 18)
                        } else {
                            etCountryCode!!.visibility = View.GONE
                            etPhoneNumber!!.setPadding(40, 18, 7, 18)
                        }
                    } else if (s.isEmpty()) {
                        etCountryCode!!.visibility = View.GONE
                        etPhoneNumber!!.setPadding(40, 18, 7, 18)
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
    }

    /**
     * Initialize Views
     */
    private fun initViews() {
        etCountryCode = findViewById(R.id.etCountryCode)
        llPhoneNumber = findViewById(R.id.llPhoneNumber)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvWorkspaceName = findViewById(R.id.tvWorkSpaceName)
        etWorkspaceName = findViewById(R.id.etWorkspaceName)
        btnContinue = findViewById(R.id.btnContinue)
        etFullName = findViewById(R.id.etFullName)
        termsConditionCheckbox = findViewById(R.id.termsCondition)
        val termsConditionTextView = findViewById<TextView>(R.id.textView2)
        val ss = SpannableString("I accept Terms of Service and Privacy Policy")
        ss.setSpan(ClickableText(1), 9, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ClickableText(2), 30, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        termsConditionTextView.text = ss
        termsConditionTextView.movementMethod = LinkMovementMethod.getInstance()
        termsConditionTextView.highlightColor = Color.TRANSPARENT
    }

    /**
     * Click Listeners
     */
    private fun clickListeners() {
        btnContinue!!.setOnClickListener(this)
        etWorkspaceName!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                btnContinue!!.performClick()
            }
            false
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btnContinue) {
            if (llPhoneNumber!!.visibility == View.VISIBLE && etPhoneNumber!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                showErrorMessage("Phone Number Field can\'t be left blank")
            } else if (etFullName!!.text.toString().trim().isEmpty()) {
                showErrorMessage("Enter your name.")
            } else if (!hasOpenWorkspaces && etWorkspaceName!!.text.toString().trim().isEmpty()) {
                showErrorMessage("Enter a workspace name.")
            } else {
                if (termsConditionCheckbox!!.isChecked) {
                    if (isNetworkConnected) {
                        showLoading()
                        apiSetNewUserDetails()
                    } else {
                        showErrorMessage(R.string.error_internet_not_connected)
                    }
                } else {
                    showErrorMessage("Please accept the Terms of Service and Privacy Policy.")
                }
            }
        }
    }

    /**
     * Api set user data
     */
    private fun apiSetNewUserDetails() {
        if (TextUtils.isEmpty(CommonData.getFcmToken())) {
            val token = FirebaseInstanceId.getInstance().token
            CommonData.updateFcmToken(token)
            //            FuguNotificationConfig.updateFcmRegistrationToken(token);
        }
        val commonParams = CommonParams.Builder()
        if (llPhoneNumber!!.visibility == View.VISIBLE) {
            commonParams.add("country_code", etCountryCode!!.selectedCountryNameCode)
            commonParams.add("contact_number", "+" + etCountryCode!!.selectedCountryCode + "-" + etPhoneNumber!!.text.toString().trim { it <= ' ' })
        }
        commonParams.add(AppConstants.FULL_NAME, etFullName!!.text.toString())
        if (!hasOpenWorkspaces)
            commonParams.add(AppConstants.WORKSPACE_NAME, etWorkspaceName!!.text.toString().trim { it <= ' ' })
        commonParams.add(AppConstants.TOKEN, CommonData.getFcmToken())
        commonParams.add(AppConstants.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@SetNewUserDetailsActivity))
        commonParams.add(AppConstants.DEVICE_DETAILS, CommonData.deviceDetails(this@SetNewUserDetailsActivity))
        commonParams.add(AppConstants.DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain())
        RestClient.getApiInterface(true).setNewUserDetails(intent.getStringExtra(AppConstants.EXTRA_ACCESS_TOKEN), BuildConfig.VERSION_CODE, AppConstants.ANDROID, commonParams.build().map).enqueue(object : ResponseResolver<ObjectResponse?>() {
            override fun onSuccess(createWorkspaceResponse: ObjectResponse?) {
                hideLoading()
                apiLoginViaAccessToken(AppConstants.BRANCH_IO, intent.getStringExtra(AppConstants.EXTRA_ACCESS_TOKEN)!!, createWorkspaceResponse)
//                apiLoginViaAccessToken(AppConstants.BRANCH_IO, intent.getStringExtra(AppConstants.EXTRA_ACCESS_TOKEN), intent.getStringExtra(AppConstants.EMAIL), intent!!.getIntExtra(AppConstants.SIGNUP_TYPE, 1), createWorkspaceResponse)
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


    //    private fun apiLoginViaAccessToken(source: String, accessToken: String, email: String, signupType: Int, createWorkspaceResponse: ObjectResponse?) {
    private fun apiLoginViaAccessToken(source: String, accessToken: String, createWorkspaceResponse: ObjectResponse?) {
        val commonParams: CommonParams.Builder = getLoginViaAccessTokenParams(source)
        RestClient.getApiInterface(true).accessTokenLogin(accessToken, BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<FcCommonResponse>() {
                    override fun onSuccess(fcCommonResponse: FcCommonResponse?) {
                        hideLoading()
                        CommonData.setCommonResponse(fcCommonResponse)
                        if (!hasOpenWorkspaces) {
                            val commonResponse = CommonData.getCommonResponse()
                            disconnectSocket()
                            initSocketConnection(fcCommonResponse!!.getData().getUserInfo().getAccessToken(),
                                    createWorkspaceResponse!!.data.enUserId, CommonData.getCommonResponse().data.userInfo.userId,
                                    CommonData.getCommonResponse().data.userInfo.userChannel, "Create WorkSpace", false,
                                    CommonData.getCommonResponse().data.userInfo.pushToken)
                            if (isPhoneSignup) {
                                val info = ArrayList<WorkspacesInfo>()
                                if (CommonData.getCommonResponse().getData().workspacesInfo.size > 0) {
                                    info.clear()
                                    info.addAll(CommonData.getCommonResponse().getData().workspacesInfo)
                                    info.add(createWorkspaceResponse.data)
                                    commonResponse.getData().workspacesInfo = info
                                    CommonData.setCommonResponse(commonResponse)
                                    CommonData.setCurrentSignedInPosition(0)
                                } else {
                                    info.add(createWorkspaceResponse.data)
                                    CommonData.setCurrentSignedInPosition(0)
                                    commonResponse.getData().workspacesInfo = info
                                    CommonData.setCommonResponse(commonResponse)
                                }
                            }
                            val sharedPreferences: SharedPreferences = this@SetNewUserDetailsActivity.getSharedPreferences("General", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putBoolean("isFirstTimeOpened", true).commit()
                            startActivity(Intent(this@SetNewUserDetailsActivity, MainActivity::class.java))
                        } else {
                            val intent = Intent(this@SetNewUserDetailsActivity, YourSpacesActivity::class.java)
                            intent.putExtra("isCreateWorkspaceAllowed", true)
                            startActivity(intent)
                        }
                        finishAffinity()
                    }

                    override fun onError(error: ApiError?) {
                        hideLoading()
                        if (error?.statusCode == 401) {
                            CommonData.clearData()
                            FuguConfig.clearFuguData(this@SetNewUserDetailsActivity)
                            val sharedPreferences: SharedPreferences = this@SetNewUserDetailsActivity.getSharedPreferences("General", Context.MODE_PRIVATE)
                            val isFirstTimeOpened = sharedPreferences.getBoolean("isFirstTimeOpened", true)
                            if (!FuguUtils.isWhiteLabel() && isFirstTimeOpened)
                                startActivity(Intent(this@SetNewUserDetailsActivity, OnboardActivity::class.java))
                            else
                                startActivity(Intent(this@SetNewUserDetailsActivity, IntroActivity::class.java))
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
//                                                    email,
//                                                    signupType,
                                                    createWorkspaceResponse
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

    private inner class ClickableText(var position: Int) : ClickableSpan() {
        override fun onClick(widget: View) {
            when (position) {
                1 -> openTermsConditionFrag("https://jungleworks.co/terms-of-service/")
                2 -> openTermsConditionFrag("https://jungleworks.co/privacy-policy/")
                else -> {
                }
            }
        }

        fun openTermsConditionFrag(url: String?) {
            val manager = this@SetNewUserDetailsActivity.supportFragmentManager
            val ft = manager.beginTransaction()
            val newFragment = TermsConditionFragment.newInstance(0, url)
            newFragment.show(ft, "TermsConditionFragment")
        }

    }
}