package com.skeleton.mvp.activity

import android.os.Bundle
import android.view.WindowManager
import com.skeleton.mvp.R
import com.skeleton.mvp.ui.base.BaseActivity

class CallingFeedBackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling_feed_back)
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }
}
