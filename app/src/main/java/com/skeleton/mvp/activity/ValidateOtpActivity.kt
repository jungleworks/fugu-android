package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.CommonResponse
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.model.gplusverification.GPlusVerification
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.DEVICE_DETAILS
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.setpassword.SetPasswordActivity
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.util.ValidationUtil
import com.skeleton.mvp.utils.FuguUtils.Companion.getTimeZoneOffset

/********************************
 * Created by Amandeep Chauhan    *
 * Date :- 30/04/2020             *
 */
class ValidateOtpActivity : BaseActivity(), View.OnClickListener {
    private var etOne: EditText? = null
    private var etTwo: EditText? = null
    private var etThree: EditText? = null
    private var etFour: EditText? = null
    private var etFive: EditText? = null
    private var etSix: EditText? = null
    private var otp: String? = null
    private var tvCountdown: TextView? = null
    private var btnContinue: AppCompatButton? = null
    private var btnResendOtp: AppCompatButton? = null
    private val OTP_COUNT = 6
    private var otpContainer: LinearLayout? = null
    private var llChangeNumber: LinearLayout? = null
    var fcCommonResponse: FcCommonResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_otp)
        initViews()
        clickListeners()
        etOne!!.requestFocus()
    }

    /**
     * Initialize Views
     */
    private fun initViews() {
        etOne = findViewById(R.id.etOne)
        etTwo = findViewById(R.id.etTwo)
        etThree = findViewById(R.id.etThree)
        etFour = findViewById(R.id.etFour)
        etFive = findViewById(R.id.etFive)
        etSix = findViewById(R.id.etSix)
        tvCountdown = findViewById(R.id.tvCountdown)
        btnContinue = findViewById(R.id.btnContinue)
        btnResendOtp = findViewById(R.id.btnResendOtp)
        otpContainer = findViewById(R.id.llOTP)
        llChangeNumber = findViewById(R.id.llChangeNumber)
        val tvSubtitle = findViewById<TextView>(R.id.tvSubtitile)
        val text = if (intent.hasExtra(AppConstants.EXTRA_EMAIL) && intent.hasExtra(AppConstants.CONTACT_NUMBER)) {
            intent.getStringExtra(AppConstants.CONTACT_NUMBER)
        } else if (intent.hasExtra(AppConstants.EXTRA_EMAIL)) {
            intent.getStringExtra(AppConstants.EXTRA_EMAIL)
        } else {
            intent.getStringExtra(AppConstants.CONTACT_NUMBER)
        }
        tvSubtitle.text = "We’ve sent a six-digit confirmation code to $text"
        handleOtpContainerFields()
        setCountdown()
    }

    private fun setCountdown() {
        btnResendOtp!!.visibility = View.GONE
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvCountdown!!.text = getRemainingTime(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                tvCountdown!!.visibility = View.GONE
                btnResendOtp!!.visibility = View.VISIBLE
            }
        }.start()
        tvCountdown!!.visibility = View.VISIBLE
    }

    private fun getRemainingTime(value: Long): String {
        val seconds = Math.floor(value % 60.toDouble()).toInt()
        val duration: String
        duration = if (seconds == 1) {
            "00:0$seconds sec"
        } else if (seconds < 10) {
            "00:0$seconds secs"
        } else {
            "00:$seconds secs"
        }
        return duration
    }

    private fun clickListeners() {
        btnContinue!!.setOnClickListener(this)
        llChangeNumber!!.setOnClickListener(this)
        btnResendOtp!!.setOnClickListener(this)
        etSix!!.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                btnContinue!!.performClick()
            }
            false
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.btnContinue) {
            if (validate()) {
                otp = etOne!!.text.toString().trim { it <= ' ' } +
                        etTwo!!.text.toString().trim { it <= ' ' } +
                        etThree!!.text.toString().trim { it <= ' ' } +
                        etFour!!.text.toString().trim { it <= ' ' } +
                        etFive!!.text.toString().trim { it <= ' ' } +
                        etSix!!.text.toString().trim { it <= ' ' }
                if (isNetworkConnected) {
                    showLoading()
                    if (intent.hasExtra(AppConstants.NEW_SIGNUP)) {
                        apiCheckIfGoogleUserAlreadyRegistered()
                    } else if (intent.hasExtra("EditPhone")) {
                        apiVerifyEditOtp()
                    } else {
                        apiValidateOtp()
                    }
                } else {
                    showErrorMessage(R.string.error_internet_not_connected)
                }
            } else {
                showErrorMessage(R.string.invalid_otp)
            }
        } else if (id == R.id.llChangeNumber) {
            finish()
        } else if (id == R.id.btnResendOtp) {
            resendOtp()
        }
    }

    private fun resendOtp() {
        val googleLoginParams = CommonParams.Builder()
        googleLoginParams.add(AppConstants.DEVICE_DETAILS, CommonData.deviceDetails(this@ValidateOtpActivity))
        googleLoginParams.add(AppConstants.CONTACT_NUMBER, intent.getStringExtra(AppConstants.CONTACT_NUMBER))
        googleLoginParams.add(AppConstants.COUNTRY_CODE, intent.getStringExtra(AppConstants.COUNTRY_CODE))
        googleLoginParams.add(AppConstants.DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain())
        showLoading()
        RestClient.getApiInterface(true).userLogin(BuildConfig.VERSION_CODE, AppConstants.ANDROID, googleLoginParams.build().map)
                .enqueue(object : ResponseResolver<FcCommonResponse?>() {
                    override fun onSuccess(t: FcCommonResponse?) {
                        hideLoading()
                        Toast.makeText(this@ValidateOtpActivity, "OTP Sent Successfully.", Toast.LENGTH_SHORT).show()
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

    /**
     * Validate OTP fields
     *
     * @return boolean
     */
    private fun validate(): Boolean {
        return ValidationUtil.checkName(etOne!!.text.toString()) &&
                ValidationUtil.checkName(etTwo!!.text.toString()) &&
                ValidationUtil.checkName(etThree!!.text.toString()) &&
                ValidationUtil.checkName(etFour!!.text.toString()) &&
                ValidationUtil.checkName(etFive!!.text.toString()) &&
                ValidationUtil.checkName(etSix!!.text.toString())
    }

    private fun apiCheckIfGoogleUserAlreadyRegistered() {
        val commonParams = CommonParams.Builder()
                .add(AppConstants.EMAIL, intent.getStringExtra(AppConstants.EXTRA_EMAIL))
                .add(AppConstants.OTP, otp)
                .add(AppConstants.CONTACT_NUMBER, intent.getStringExtra(AppConstants.CONTACT_NUMBER))
                .add(AppConstants.COUNTRY_CODE, intent.getStringExtra(AppConstants.COUNTRY_CODE))
                .add(AppConstants.SIGNUP_SOURCE, "GOOGLE")
                .build()
        RestClient.getApiInterface(true).checkIfGoogleUserAlreadyRegistered(BuildConfig.VERSION_CODE, AppConstants.ANDROID, commonParams.map)
                .enqueue(object : ResponseResolver<GPlusVerification?>() {
                    override fun onSuccess(gPlusVerification: GPlusVerification?) {
                        hideLoading()
                        val intent = Intent()
                        intent.putExtra(AppConstants.ACCESS_TOKEN, gPlusVerification!!.data.accessToken)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
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

    /**
     * Verify OTP API Hit
     */
    private fun apiValidateOtp() {
        val commonParams = CommonParams.Builder()
        commonParams.add(AppConstants.OTP, otp)
        commonParams.add(DEVICE_DETAILS, CommonData.deviceDetails(this@ValidateOtpActivity))
        commonParams.add("time_zone", getTimeZoneOffset())
        if (intent.hasExtra(AppConstants.EXTRA_EMAIL)) {
            commonParams.add(AppConstants.EMAIL, intent.getStringExtra(AppConstants.EXTRA_EMAIL))
        } else {
            commonParams.add(AppConstants.CONTACT_NUMBER, intent.getStringExtra(AppConstants.CONTACT_NUMBER))
            commonParams.add(AppConstants.COUNTRY_CODE, intent.getStringExtra(AppConstants.COUNTRY_CODE))
        }
        if (com.skeleton.mvp.fugudatabase.CommonData.getOnboardingFlow() == "new") {
            RestClient.getApiInterface(true).validateOtp(BuildConfig.VERSION_CODE, AppConstants.ANDROID, commonParams.build().map).enqueue(object : ResponseResolver<FcCommonResponse?>() {
                override fun onSuccess(fcCommonResponse: FcCommonResponse?) {
                    hideLoading()
                    if (fcCommonResponse!!.getData().signupType == 1) {
                        CommonData.setCommonResponse(fcCommonResponse)
                        CommonData.setToken(fcCommonResponse.getData().getUserInfo().getAccessToken())
                        val intent = Intent(this@ValidateOtpActivity, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    } else {
                        val setUserDetailsIntent = Intent(this@ValidateOtpActivity, SetNewUserDetailsActivity::class.java)
                        setUserDetailsIntent.putExtra(AppConstants.EXTRA_ACCESS_TOKEN, fcCommonResponse.getData().getUserInfo().getAccessToken())
                        if (intent.hasExtra(AppConstants.CONTACT_NUMBER_INTENT)) {
                            val isContactNumber = intent.getBooleanExtra(AppConstants.CONTACT_NUMBER_INTENT, false)
                            setUserDetailsIntent.putExtra(AppConstants.CONTACT_NUMBER_INTENT, isContactNumber)
                        }
                        if (intent.hasExtra(AppConstants.EXTRA_EMAIL)) {
                            setUserDetailsIntent.putExtra(AppConstants.EMAIL, intent.getStringExtra(AppConstants.EXTRA_EMAIL))
                        } else {
                            setUserDetailsIntent.putExtra(AppConstants.CONTACT_NUMBER, intent.getStringExtra(AppConstants.CONTACT_NUMBER))
                            setUserDetailsIntent.putExtra(AppConstants.COUNTRY_CODE, intent.getStringExtra(AppConstants.COUNTRY_CODE))
                        }
                        setUserDetailsIntent.putExtra("ShowYourSpaces", fcCommonResponse.data.openWorkspacesToJoin.size > 0)
                        startActivity(setUserDetailsIntent)
                        finishAffinity()
                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    }
                }

                override fun onError(error: ApiError) {
                    hideLoading()
                    showErrorMessage(error.message)
                }

                override fun onFailure(throwable: Throwable) {
                    hideLoading()
                }
            })
        } else {
            RestClient.getApiInterface(true).verifyOtp(BuildConfig.VERSION_CODE, AppConstants.ANDROID, commonParams.build().map).enqueue(object : ResponseResolver<FcCommonResponse?>() {
                override fun onSuccess(fcCommonResponse: FcCommonResponse?) {
                    hideLoading()
                    CommonData.setCommonResponse(fcCommonResponse)
                    CommonData.setToken(fcCommonResponse!!.getData().getUserInfo().getAccessToken())
                    val yourSpacesIntent = Intent(this@ValidateOtpActivity, YourSpacesActivity::class.java)
                    startActivity(yourSpacesIntent)
                    finishAffinity()
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }

                override fun onError(error: ApiError) {
                    hideLoading()
                    if (error.statusCode == 403) {
                        val setPasswordIntent = Intent(this@ValidateOtpActivity, SetPasswordActivity::class.java)
                        setPasswordIntent.putExtra(AppConstants.EXTRA_OTP, otp)
                        if (intent.hasExtra(AppConstants.CONTACT_NUMBER_INTENT)) {
                            val isContactNumber = intent.getBooleanExtra(AppConstants.CONTACT_NUMBER_INTENT, false)
                            setPasswordIntent.putExtra(AppConstants.CONTACT_NUMBER_INTENT, isContactNumber)
                        }
                        if (intent.hasExtra(AppConstants.EXTRA_EMAIL)) {
                            setPasswordIntent.putExtra(AppConstants.EMAIL, intent.getStringExtra(AppConstants.EXTRA_EMAIL))
                        } else {
                            setPasswordIntent.putExtra(AppConstants.CONTACT_NUMBER, intent.getStringExtra(AppConstants.CONTACT_NUMBER))
                            setPasswordIntent.putExtra(AppConstants.COUNTRY_CODE, intent.getStringExtra(AppConstants.COUNTRY_CODE))
                        }
                        startActivity(setPasswordIntent)
                        finishAffinity()
                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    } else {
                        showErrorMessage(error.message)
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    hideLoading()
                }
            })
        }
    }

    /**
     * Verify editPhone OTP API Hit
     */
    private fun apiVerifyEditOtp() {
        fcCommonResponse = CommonData.getCommonResponse()
        val commonParams = CommonParams.Builder()
        commonParams.add(AppConstants.OTP, otp)
        RestClient.getApiInterface(true).verifyEdit_Phone_Otp(fcCommonResponse!!.data.userInfo.accessToken, FuguAppConstant.ANDROIDS, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<CommonResponse?>() {
                    override fun onSuccess(fcCommonResponse: CommonResponse?) {
                        hideLoading()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    override fun onError(error: ApiError) {
                        hideLoading()
                    }

                    override fun onFailure(throwable: Throwable) {
                        hideLoading()
                    }
                })
    }

    private fun handleOtpContainerFields() {
        for (i in 0 until OTP_COUNT) {
            val editText = otpContainer!!.getChildAt(i) as EditText
            val j = i
            if (j < OTP_COUNT) {
                editText.setOnKeyListener { _, keyCode, keyEvent ->
                    Log.e("onKey pressed", "==$keyCode")
                    Log.e("text", "==" + editText.text.toString())
                    if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN && editText.text.toString().isEmpty() && j != 0) {
                        otpContainer!!.getChildAt(j - 1).requestFocus()
                    }
                    false
                }
                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun afterTextChanged(editable: Editable) {
                        if (!editText.text.toString().isEmpty() && j != OTP_COUNT - 1) otpContainer!!.getChildAt(j + 1).requestFocus()
                    }
                })
            }
        }
    }
}