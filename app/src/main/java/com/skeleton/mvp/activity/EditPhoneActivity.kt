package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.hbb20.CountryCodePicker
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse
import com.skeleton.mvp.data.model.setPassword.CommonResponseFugu
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.EXTRA_EMAIL
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.otp.OTPActivity
import io.paperdb.Paper

/**
 * Roshan Kumar
 * 20/06/2018
 */
class EditPhoneActivity : BaseActivity(), View.OnClickListener {

    lateinit var btnRequestOtp: Button
    lateinit var fcCommonResponse: FcCommonResponse
    lateinit var etPhoneNumber: EditText
    lateinit var etCountryCode: CountryCodePicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_phone)
        initview()
    }

    fun initview() {
        btnRequestOtp = findViewById(R.id.btnRequestOtp)
        etCountryCode = findViewById(R.id.etCountryCode)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        btnRequestOtp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRequestOtp -> apiGetOtp()
        }

    }


    private fun apiGetOtp() {
        showLoading()
        fcCommonResponse = CommonData.getCommonResponse()
        val commonParams = CommonParams.Builder()
                .add(AppConstants.CONTACT_NUMBER, etCountryCode.fullNumberWithPlus.trim() + "-" + etPhoneNumber.text.toString().trim())
                .add(AppConstants.COUNTRY_CODE, etCountryCode.selectedCountryNameCode.trim())
                .build()
        RestClient.getApiInterface(true).getOTP(fcCommonResponse.data.userInfo.accessToken, FuguAppConstant.ANDROIDS, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<CommonResponseFugu>() {
                    override fun onSuccess(t: CommonResponseFugu?) {
                        hideLoading()
                        var intent: Intent = Intent(this@EditPhoneActivity, OTPActivity::class.java)
                        intent.putExtra("EditPhone", "EditPhone")
                        intent.putExtra(AppConstants.CONTACT_NUMBER, etCountryCode.selectedCountryCodeWithPlus + "-" + etPhoneNumber.getText().toString().trim({ it <= ' ' }))
                        startActivityForResult(intent, 2002)

                    }

                    override fun onError(error: ApiError?) {
                        hideLoading()
                        if (error!!.statusCode == FuguAppConstant.SESSION_EXPIRE) {
                            CommonData.clearData()
                            object : Thread() {
                                override fun run() {
                                    super.run()
                                    CommonData.clearData()
                                }
                            }.start()
                            FuguConfig.clearFuguData(this@EditPhoneActivity)
                            finishAffinity()
                        } else {
                            showErrorMessage(error.message)
                        }

                    }

                    override fun onFailure(throwable: Throwable?) {

                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2002 && resultCode == Activity.RESULT_OK) {
            var intent = Intent()
            intent.putExtra("phone", etCountryCode.fullNumberWithPlus.trim() + "-" + etPhoneNumber.text.toString().trim())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
