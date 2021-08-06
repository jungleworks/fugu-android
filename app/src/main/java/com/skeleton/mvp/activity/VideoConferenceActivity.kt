package com.skeleton.mvp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import com.skeleton.mvp.R
import java.net.MalformedURLException
import java.net.URL

class VideoConferenceActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_conference)
//        val jitsiView = JitsiMeetView(this@VideoConferenceActivity)
//        val linearLayoutJitsi = findViewById<LinearLayout>(R.id.ll)
//        linearLayoutJitsi.addView(jitsiView)
//        try {
//            val options = JitsiMeetConferenceOptions.Builder()
//                    .setServerURL(URL(intent.getStringExtra("base_url")))
//                    .setRoom(intent.getStringExtra("room_name"))
//                    .setAudioMuted(false)
//                    .setVideoMuted(false)
//                    .setAudioOnly(false)
//                    .setWelcomePageEnabled(false)
//                    .build()
//            JitsiMeet.setDefaultConferenceOptions(options)
//            jitsiView.join(options)
//            jitsiView.listener=object :JitsiMeetViewListener{
//                override fun onConferenceTerminated(p0: MutableMap<String, Any>?) {
//                    finish()
//                }
//
//                override fun onConferenceJoined(p0: MutableMap<String, Any>?) {
//                }
//
//                override fun onConferenceWillJoin(p0: MutableMap<String, Any>?) {
//                }
//
//
//
//            }
//        } catch (e: MalformedURLException) {
//            e.printStackTrace()
//        }
    }
}
