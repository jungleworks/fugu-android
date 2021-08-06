package com.skeleton.mvp.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.skeleton.mvp.R
import com.skeleton.mvp.util.Log


class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val mywebview = findViewById<View>(R.id.webView) as WebView
        Log.d("Url", intent.getStringExtra("billing_url"))
        mywebview.clearCache(true)
        mywebview.settings.javaScriptEnabled = true
        mywebview.settings.loadsImagesAutomatically = true
        mywebview.webChromeClient = WebChromeClient()
        mywebview.settings.domStorageEnabled = true
        mywebview.addJavascriptInterface(MyJavaScriptInterface(),
                "android")

        mywebview.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                if (request?.url.toString().contains("success=true")) {
                    setResult(Activity.RESULT_OK)
                    finish()
                    return true
                } else if (request?.url.toString().equals("success=false")) {
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

        }
        mywebview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        mywebview.loadUrl(intent.getStringExtra("billing_url")!!)
//        mywebview.loadUrl("https://google.com");
    }

    internal inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun onUrlChange(url: String) {
            Log.d("hydrated", "onUrlChange$url")
        }
    }
}
