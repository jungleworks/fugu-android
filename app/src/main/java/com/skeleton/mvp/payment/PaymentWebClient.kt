package com.skeleton.mvp.payment

import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity
import com.skeleton.mvp.util.Log

class PaymentWebClient internal constructor(var baseActivity: BaseActivity) : WebViewClient() {
    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
        Log.i("PaymentUrl", url)
        when {
            url.contains("success") -> {
                view.stopLoading()
                baseActivity.showErrorMessage("Payment Successful. Would you like to invite users now?", {
                    val intent = Intent(baseActivity, InviteOnboardActivity::class.java)
                    baseActivity.startActivity(intent)
                    baseActivity.finish()
                }, { baseActivity.finish() }, "Invite", "Close")
                Log.i("PaymentSuccess", url)
            }
            url.contains("failed") -> {
                view.stopLoading()
                baseActivity.showErrorMessage("Payment Failed.", { baseActivity.finish() }, { baseActivity.finish() }, "Ok", "Close")
                Log.i("PaymentFailed", url)
            }
            else -> {
                super.onPageStarted(view, url, favicon)
            }
        }
    }

    override fun onPageFinished(view: WebView, url: String) {}

}