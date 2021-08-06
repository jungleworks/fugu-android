package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.hbb20.CountryCodePicker
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant.ANDROID
import com.skeleton.mvp.data.model.CommonResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.ui.AppConstants.*
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.otp.OTPActivity

class PhoneNumberActivity : BaseActivity() {
    var etCountryCode: CountryCodePicker? = null
    var etPhoneNumber: EditText? = null
    var btnContinue: AppCompatButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)
        etCountryCode = findViewById(R.id.etCountryCode)
        etPhoneNumber = findViewById(R.id.etPhone)
        btnContinue = findViewById(R.id.btnContinue)

        etPhoneNumber?.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                btnContinue?.performClick()
            }
            false
        }

        btnContinue?.setOnClickListener {

            if (isNetworkConnected) {
                if (!TextUtils.isEmpty(etPhoneNumber?.text?.trim().toString())) {
                    showLoading()
                    apiRegisterPhoneNumber()
                } else {
                    showErrorMessage(R.string.error_invalid_phone_number)
                }
            } else {
                showErrorMessage(R.string.error_internet_not_connected)
            }
        }
    }

    private fun apiRegisterPhoneNumber() {
        val commonParams = CommonParams.Builder()
                .add(CONTACT_NUMBER, "+" + etCountryCode?.selectedCountryCode + "-" + etPhoneNumber?.text?.trim().toString())
                .add(COUNTRY_CODE, etCountryCode?.selectedCountryNameCode)

        RestClient.getApiInterface(true).registerPhoneNumber(BuildConfig.VERSION_CODE, ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<CommonResponse>() {
                    override fun onSuccess(t: CommonResponse?) {
                        hideLoading()
                        val intentotp = Intent(this@PhoneNumberActivity, OTPActivity::class.java)
                        intentotp.putExtra(EXTRA_EMAIL, intent.getStringExtra(EXTRA_EMAIL))
                        intentotp.putExtra(CONTACT_NUMBER, "+" + etCountryCode?.selectedCountryCode + "-" + etPhoneNumber?.text?.trim().toString())
                        intentotp.putExtra(COUNTRY_CODE, etCountryCode?.selectedCountryNameCode)
                        intentotp.putExtra(NEW_SIGNUP, NEW_SIGNUP)
                        startActivityForResult(intentotp, REQ_CODE_NEW_SIGNUP)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_NEW_SIGNUP) {
            val intent = Intent()
            intent.putExtra(ACCESS_TOKEN, data?.getStringExtra(ACCESS_TOKEN))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
