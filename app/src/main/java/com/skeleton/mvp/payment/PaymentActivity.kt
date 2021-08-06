package com.skeleton.mvp.payment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import com.skeleton.mvp.R
import com.skeleton.mvp.ui.base.BaseActivity

class PaymentActivity : BaseActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        var url = ""
        if (intent.hasExtra("url"))
            url = intent.getStringExtra("url")!!
        else
            finish()
        val wvPayment = findViewById<WebView>(R.id.wvPayment)
        wvPayment.webViewClient = PaymentWebClient(this@PaymentActivity)
        wvPayment.settings.javaScriptEnabled = true
        wvPayment.settings.setSupportMultipleWindows(true)
        if(isNetworkConnected)
        wvPayment.loadUrl(url)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("You're about to cancel the payment process. Are you sure you want to cancel?")
                .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                    super.onBackPressed()
                }
                .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
    }
}