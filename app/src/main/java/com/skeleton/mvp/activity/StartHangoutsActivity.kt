package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.service.ConferenceCallService
import com.skeleton.mvp.service.OngoingCallService
import com.skeleton.mvp.utils.emitAnswerEvent
import com.skeleton.mvp.utils.joinHangoutsCall

class StartHangoutsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(FuguAppConstant.INVITE_LINK)) {
            val inviteLink = intent.getStringExtra(FuguAppConstant.INVITE_LINK)!!
            stopService(Intent(this@StartHangoutsActivity, OngoingCallService::class.java))
            stopService(Intent(this@StartHangoutsActivity, ConferenceCallService::class.java))
            emitAnswerEvent(intent, this@StartHangoutsActivity)
            joinHangoutsCall(inviteLink)
            finish()
        }
        finish()
    }
}