package com.skeleton.mvp.meet

/********************************
Created by Amandeep Chauhan     *
Date :- 28/04/2020              *
 *********************************/

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.apis.ApiInitiateVideoConference
import com.skeleton.mvp.fragment.BottomSheetFragment
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.utils.gone
import com.skeleton.mvp.utils.visible

class StartNewMeetBottomSheetFragment(var startMeetWithMembers: HashMap<Long, GetAllMembers>, var mContext: Context, var enUserId: String, var fuguSecretKey: String, private val isHangoutsMeet: Boolean = false) : BottomSheetFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_start_new_meet, container)
        val ivClose = view.findViewById<ImageView>(R.id.iv_close)
        val ivAudio = view.findViewById<ImageView>(R.id.iv_audio)
        val tvAudio = view.findViewById<TextView>(R.id.tv_audio)
        val ivVideo = view.findViewById<ImageView>(R.id.iv_video)
        val tvVideo = view.findViewById<TextView>(R.id.tv_video)
        val rvMeetMembers = view.findViewById<RecyclerView>(R.id.rvMeetMembers)
        val btnStartMeet = view.findViewById<Button>(R.id.btn_start_meet)
        val userIdsToBeCalled = startMeetWithMembers.keys
        val userIds = ArrayList<Long>()
        userIds.addAll(userIdsToBeCalled)
        var isAudioEnabled = true
        var isVideoEnabled = true
        val tvParticipants = view.findViewById<TextView>(R.id.tv_participants)
        if (startMeetWithMembers.size > 0) {
            val sb = StringBuilder()
            sb.append("You")
            var count = 0
            for (attendee in startMeetWithMembers.keys) {
                if (count < 3) {
                    if (count == startMeetWithMembers.size - 1)
                        sb.append(" and " + startMeetWithMembers[attendee]!!.fullName.split(" ")[0])
                    else
                        sb.append(", " + startMeetWithMembers[attendee]!!.fullName.split(" ")[0])
                    count++
                } else
                    break
            }
            val others = startMeetWithMembers.size - count
            if (others == 1)
                sb.append(" and $others other")
            else if (others > 1)
                sb.append(" and $others others")
            tvParticipants.text = "$sb will join the meeting."
            tvParticipants.visible()
        } else
            tvParticipants.gone()
//        val suffix = if (userIds.size == 1)
//            " Participant"
//        else
//            " Participants"
//        tvParticipants.text = userIds.size.toString() + suffix
        ivClose.setOnClickListener {
            run {
                this@StartNewMeetBottomSheetFragment.dismiss()
            }
        }

        rvMeetMembers.layoutManager = LinearLayoutManager(mContext as MainActivity, LinearLayoutManager.HORIZONTAL, false)
        rvMeetMembers.adapter = NewMeetMembersAdapter(startMeetWithMembers, mContext)

        if (isHangoutsMeet) {
            ivAudio.visibility = View.GONE
            ivVideo.visibility = View.GONE
            tvAudio.visibility = View.GONE
            tvVideo.visibility = View.GONE
            ivVideo.visibility = View.GONE
            btnStartMeet.setOnClickListener {
                ApiInitiateVideoConference().apiInitiateHangoutsConference(
                        userIds,
                        mContext as MainActivity,
                        enUserId,
                        fuguSecretKey
                )
            }
        } else {
            ivAudio.setOnClickListener {
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
            ivVideo.setOnClickListener {
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
            btnStartMeet.setOnClickListener {
                ApiInitiateVideoConference().apiInitiateVideoConference(
                        userIds,
                        mContext as MainActivity,
                        enUserId,
                        fuguSecretKey,
                        !isAudioEnabled,
                        !isVideoEnabled
                )
            }
        }
        return view
    }
}

