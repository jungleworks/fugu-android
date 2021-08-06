package com.skeleton.mvp.payment

import android.view.View
import androidx.lifecycle.ViewModel
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.network.CommonParams

class PaymentViewModel : ViewModel() {
    var userCount: String? = null
    var paymentListener: PaymentListener? = null

    fun calculate(v: View) {
        if (userCount.isNullOrEmpty()) {
            return
        }
        val workspaceInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
        val commonParams = CommonParams.Builder()
                .add("user_count", userCount!!.toInt())
                .add("en_user_id", workspaceInfo.enUserId)
                .add("price_type", 1)
                .add("workspace_id", workspaceInfo.workspaceId)
                .build()
        paymentListener?.calculate(commonParams)
    }

    fun onBackPressed(v: View) {
        paymentListener?.onBack()
    }
}