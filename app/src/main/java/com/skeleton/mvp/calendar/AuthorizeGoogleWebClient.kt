package com.skeleton.mvp.calendar

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.Log

class AuthorizeGoogleWebClient(private val baseActivity: BaseActivity) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        baseActivity.showLoading()
    }

    override fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
        Log.i("AuthorizationUrl", url)
        return if (url != null && url.contains("code=")) {
            val splitArray = url.split("?")
            val code = getCode(splitArray[1].split("&"))
            baseActivity.onCalendarAuthorizationGranted(code, object : BaseActivity.CalendarLinkingCallback {
                override fun onAuthorizationSuccess() {
                }
            })
            webView?.stopLoading()
            baseActivity.finish()
            true
        } else if (url != null && url.contains("error=")) {
            val splitArray = url.split("?")
            val errorString = getError(splitArray[1].split("&"))
            baseActivity.onCalendarAuthorizationCanceled(errorString)
            webView?.stopLoading()
            baseActivity.finish()
            true
        } else
            super.shouldOverrideUrlLoading(webView, url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        Log.i("AuthorizationUrlLoaded", url)
        baseActivity.hideLoading()
        super.onPageFinished(view, url)
    }

    private fun getCode(splitArray: List<String>): String {
        for (data in splitArray) {
            if (data.contains("code=")) {
                return data.replace("code=", "")
            }
        }
        return ""
    }

    private fun getError(splitArray: List<String>): String {
        for (data in splitArray) {
            if (data.contains("error=")) {
                return data.replace("error=", "")
            }
        }
        return ""
    }
}