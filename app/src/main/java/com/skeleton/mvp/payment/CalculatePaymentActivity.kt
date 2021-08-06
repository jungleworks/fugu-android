package com.skeleton.mvp.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.payment.CalculatePriceResponse
import com.skeleton.mvp.data.model.payment.InitiatePaymentResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.databinding.ActivityCalcuatePaymentBinding
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.base.BaseActivity

class CalculatePaymentActivity : BaseActivity(), PaymentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityCalcuatePaymentBinding>(this, R.layout.activity_calcuate_payment)
        val viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        binding.paymentViewModel = viewModel
        viewModel.paymentListener = this
    }

    override fun calculate(commonParams: CommonParams) {
        val commonData = CommonData.getCommonResponse().getData()
        showLoading()
        RestClient.getApiInterface(false).calculatePrice(commonData.getUserInfo().getAccessToken(), commonData.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<CalculatePriceResponse>() {
                    override fun onSuccess(calculatePriceResponse: CalculatePriceResponse?) {
                        hideLoading()
                        val data = calculatePriceResponse!!.data
                        showErrorMessage("Total payable amount for ${data.noOfUsers} users will be ${data.currency} ${data.totalPrice}", {
                            val paymentParamsBuilder = CommonParams.Builder()
                                    .add("amount", data.totalPrice)
                                    .add("user_count", data.noOfUsers)
                                    .add("currency", data.currency)
                                    .add("en_user_id", commonData.workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
                                    .add("price_type", 1)
                                    .add("workspace_id", commonData.workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceId)
                                    .add(AppConstants.DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain())
                            showLoading()
                            RestClient.getApiInterface(false).initiatePayment(commonData.getUserInfo().getAccessToken(), commonData.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, paymentParamsBuilder.build().map)
                                    .enqueue(object : ResponseResolver<InitiatePaymentResponse>() {
                                        override fun onSuccess(initiatePaymentResponse: InitiatePaymentResponse?) {
                                            if (isNetworkConnected) {
                                                val intent = Intent(this@CalculatePaymentActivity, PaymentActivity::class.java)
                                                intent.putExtra("url", initiatePaymentResponse?.data!!.redirectURL)
                                                startActivity(intent)
                                                finish()
                                            } else
                                                showErrorMessage("No Internet Connection")
                                        }

                                        override fun onError(error: ApiError?) {
                                            hideLoading()
                                            showErrorMessage(error?.message)
                                        }

                                        override fun onFailure(throwable: Throwable?) {
                                        }
                                    })
                        }, {}, "Pay ${data.currency} ${data.totalPrice}", "Cancel")
                    }

                    override fun onError(error: ApiError?) {
                        hideLoading()
                        showErrorMessage(error?.message)
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }
                })
    }

    override fun onBack() {
        onBackPressed()
    }


    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

