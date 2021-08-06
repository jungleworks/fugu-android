package com.skeleton.mvp.payment

import com.skeleton.mvp.data.network.CommonParams

interface PaymentListener {
    fun calculate(commonParams: CommonParams)
    fun onBack()
}