package com.skeleton.mvp.meet

/********************************
Created by Amandeep Chauhan     *
Date :- 29/04/2020              *
*********************************/

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.activity.VideoConfActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.fragment.BottomSheetFragment
import com.skeleton.mvp.fugudatabase.CommonData

class JoinMeetBottomSheetFragment(var mContext: Context) : BottomSheetFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.JoinMeetBottomSheet)
        val view = inflater.inflate(R.layout.bottom_sheet_join_meet, container)
        val ivClose = view.findViewById<ImageView>(R.id.iv_close)
        val ivAudio = view.findViewById<ImageView>(R.id.iv_audio)
        val tvAudio = view.findViewById<TextView>(R.id.tv_audio)
        val ivVideo = view.findViewById<ImageView>(R.id.iv_video)
        val tvVideo = view.findViewById<TextView>(R.id.tv_video)
        val btnJoinMeet = view.findViewById<Button>(R.id.btn_join_meet)
        val etMeetLink = view.findViewById<EditText>(R.id.et_meet_link)
        var isAudioEnabled = true
        var isVideoEnabled = true
        ivClose.setOnClickListener { _ ->
            run {
                this@JoinMeetBottomSheetFragment.dismiss()
            }
        }
        ivAudio.setOnClickListener { _ ->
            run {
                if (isAudioEnabled) {
                    tvAudio.text = "Mic Off"
                    ivAudio.setImageResource(R.drawable.meet_audio_off)
                    isAudioEnabled = false
                } else {
                    tvAudio.text = "Mic On"
                    ivAudio.setImageResource(R.drawable.meet_audio_on)
                    isAudioEnabled = true
                }
            }
        }
        ivVideo.setOnClickListener { _ ->
            run {
                if (isVideoEnabled) {
                    tvVideo.text = "Video Off"
                    ivVideo.setImageResource(R.drawable.meet_video_off)
                    isVideoEnabled = false
                } else {
                    tvVideo.text = "Video On"
                    ivVideo.setImageResource(R.drawable.meet_video_on)
                    isVideoEnabled = true
                }
            }
        }
        btnJoinMeet.setOnClickListener { _ ->
            run {
                val meetId = etMeetLink.text.toString().trim().replace("${CommonData.getConferenceUrl()}/", "").replace(" ", "")
                if (meetId.length < 2) {
                    (context as MainActivity).showErrorMessage("Meeting ID should have minimum 2 characters.")
                } else {
                    val baseUrl = CommonData.getConferenceUrl()
                    val videoIntent = Intent(mContext as MainActivity, VideoConfActivity::class.java)
                    videoIntent.putExtra("base_url", baseUrl)
                    videoIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    videoIntent.putExtra(FuguAppConstant.INVITE_LINK, "$baseUrl/$meetId")
                    videoIntent.putExtra("room_name", meetId)
                    videoIntent.putExtra("is_audio_muted", !isAudioEnabled)
                    videoIntent.putExtra("is_video_muted", !isVideoEnabled)
                    startActivity(videoIntent)
                    this@JoinMeetBottomSheetFragment.dismiss()
                }
            }
        }
        return view
    }
}