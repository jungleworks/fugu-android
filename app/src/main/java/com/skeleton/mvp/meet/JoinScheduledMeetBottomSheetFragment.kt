package com.skeleton.mvp.meet

/********************************
Created by Amandeep Chauhan     *
Date :- 28/04/2020              *
 *********************************/

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.activity.ScheduleMeetActivity
import com.skeleton.mvp.activity.VideoConfActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.model.scheduleMeets.Attendee
import com.skeleton.mvp.data.model.scheduleMeets.MeetDetails
import com.skeleton.mvp.fragment.BottomSheetFragment
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.utils.*
import java.util.*

class JoinScheduledMeetBottomSheetFragment(private val meetDetails: MeetDetails, var mContext: Context) : BottomSheetFragment() {

    val dateUtils: DateUtils = DateUtils.getInstance()
    val workspaceInfo: WorkspacesInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()]

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_join_scheduled_meet, container)
        val ivClose = view.findViewById<AppCompatImageView>(R.id.iv_close)
        val ivEdit = view.findViewById<AppCompatImageView>(R.id.iv_edit)
        val ivAudio = view.findViewById<AppCompatImageView>(R.id.iv_audio)
        val tvMeetTitle = view.findViewById<AppCompatTextView>(R.id.tvMeetTitle)
        val tvMeetTimings = view.findViewById<AppCompatTextView>(R.id.tvMeetTimings)
        val tvOngoing = view.findViewById<AppCompatTextView>(R.id.tvOngoing)
        val tvAudio = view.findViewById<AppCompatTextView>(R.id.tv_audio)
        val ivVideo = view.findViewById<AppCompatImageView>(R.id.iv_video)
        val tvVideo = view.findViewById<AppCompatTextView>(R.id.tv_video)
        val ivShare = view.findViewById<AppCompatImageView>(R.id.ivShare)
        val tvShareLink = view.findViewById<AppCompatTextView>(R.id.tvShareLink)
        val rvMeetMembers = view.findViewById<RecyclerView>(R.id.rvMeetMembers)
        val btnJoinScheduledMeet = view.findViewById<Button>(R.id.btnJoinScheduledMeet)
        val tvParticipants = view.findViewById<AppCompatTextView>(R.id.tv_participants)
        var isAudioEnabled = true
        var isVideoEnabled = true

        val attendees = meetDetails.attendees.filter { attendee -> attendee.userId != workspaceInfo.userId.toLong() || (!attendee.isSelf && meetDetails.meetType != FuguAppConstant.MeetType.JITSI.toString()) }
        val totalAttendeeCount = meetDetails.attendees.size
        if (attendees.isNotEmpty()) {
            val sb = StringBuilder()
            sb.append("You")
            var count = 1
            for (attendee in attendees) {
                if (count < 3) {
                    if (attendee.userId != workspaceInfo.userId.toLong() || (!attendee.isSelf && meetDetails.meetType != FuguAppConstant.MeetType.JITSI.toString())) {
                        if (count == totalAttendeeCount - 1)
                            sb.append(" and " + attendee.fullName.split(" ")[0])
                        else
                            sb.append(", " + attendee.fullName.split(" ")[0])
                        count++
                    }
                } else
                    break
            }
            val others = totalAttendeeCount - count
            if (others == 1)
                sb.append(" and $others other")
            else if (others > 1)
                sb.append(" and $others others")
            tvParticipants.text = "$sb will join the meeting."
            tvParticipants.visible()
        } else
            tvParticipants.gone()
        if (workspaceInfo.userId.toLong() == meetDetails.userID) {
            ivEdit.visible()
            ivEdit.setOnClickListener {
                val editMeetIntent = Intent(mContext, ScheduleMeetActivity::class.java)
                editMeetIntent.putExtra("isEditMode", true)
                editMeetIntent.putExtra("meet_id", meetDetails.meetId)
                editMeetIntent.putExtra("meetDetails", meetDetails)
                (mContext as MainActivity).startActivityForResult(editMeetIntent, FuguAppConstant.RequestCodes.SCHEDULE_MEET_REQUEST)
                dismiss()
            }
        } else
            ivEdit.gone()
        ivClose.setOnClickListener {
            run {
                this@JoinScheduledMeetBottomSheetFragment.dismiss()
            }
        }
        tvMeetTitle.text = meetDetails.title
        val startTime = dateUtils.getLocalDateObject(meetDetails.startDatetime)
        val endTime = dateUtils.getLocalDateObject(meetDetails.endDatetime)
        val currentTime = Calendar.getInstance()
        val timings = "${getFormattedTime(startTime)} - ${getFormattedTime(endTime)}"
        tvMeetTimings.text = timings
        val timingsWithDate = getFormattedDate(startTime) + " (" + timings + ")"
        val isSameDay = io.github.memfis19.cadar.internal.utils.DateUtils.isSameDay(startTime, Calendar.getInstance().time)
        if (isSameDay && startTime.time < currentTime.timeInMillis && currentTime.timeInMillis < endTime.time) {
            tvOngoing.visible()
            btnJoinScheduledMeet.visible()
            ivShare.visible()
            tvShareLink.visible()
            ivShare.setOnClickListener {
                shareMeetLink(meetDetails.title, timingsWithDate, meetDetails.meetType, meetDetails.roomId)
            }
            tvShareLink.setOnClickListener {
                shareMeetLink(meetDetails.title, timingsWithDate, meetDetails.meetType, meetDetails.roomId)
            }
            when (meetDetails.meetType) {
                FuguAppConstant.MeetType.GOOGLE.toString() -> {
                    ivAudio.visibility = View.GONE
                    ivVideo.visibility = View.GONE
                    tvAudio.visibility = View.GONE
                    tvVideo.visibility = View.GONE
                    btnJoinScheduledMeet.setOnClickListener {
                        mContext.joinHangoutsCall("https://meet.google.com/" + meetDetails.roomId)
                    }
                }
                FuguAppConstant.MeetType.JITSI.toString() -> {
                    ivAudio.visibility = View.VISIBLE
                    ivVideo.visibility = View.VISIBLE
                    tvAudio.visibility = View.VISIBLE
                    tvVideo.visibility = View.VISIBLE
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
                    btnJoinScheduledMeet.setOnClickListener {
                        val baseUrl = CommonData.getConferenceUrl()
                        val videoIntent = Intent(mContext as MainActivity, VideoConfActivity::class.java)
                        videoIntent.putExtra("base_url", baseUrl)
                        videoIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        videoIntent.putExtra(FuguAppConstant.INVITE_LINK, "$baseUrl/${meetDetails.roomId}")
                        videoIntent.putExtra("room_name", meetDetails.roomId)
                        videoIntent.putExtra("is_audio_muted", !isAudioEnabled)
                        videoIntent.putExtra("is_video_muted", !isVideoEnabled)
                        startActivity(videoIntent)
                        this@JoinScheduledMeetBottomSheetFragment.dismiss()
                    }
                }
                else -> {
                    ivAudio.visibility = View.GONE
                    ivVideo.visibility = View.GONE
                    tvAudio.visibility = View.GONE
                    tvVideo.visibility = View.GONE
                    btnJoinScheduledMeet.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(meetDetails.link)
                        startActivity(intent)
                        this@JoinScheduledMeetBottomSheetFragment.dismiss()
                    }
                }
            }
        } else {
            if (!isSameDay)
                tvMeetTimings.text = timingsWithDate
            btnJoinScheduledMeet.alpha = 0.5f
            when (meetDetails.meetType) {
                FuguAppConstant.MeetType.JITSI.toString() -> {
                    ivAudio.alpha = 0.5f
                    ivVideo.alpha = 0.5f
                    tvAudio.alpha = 0.5f
                    tvVideo.alpha = 0.5f
                }
                else -> {
                    ivAudio.visibility = View.GONE
                    ivVideo.visibility = View.GONE
                    tvAudio.visibility = View.GONE
                    tvVideo.visibility = View.GONE

                }
            }
            btnJoinScheduledMeet.setOnClickListener {
                mContext.showToast("Meeting yet to begin!")
            }
        }

        rvMeetMembers.layoutManager = LinearLayoutManager(mContext as MainActivity, LinearLayoutManager.HORIZONTAL, false)
        rvMeetMembers.adapter = ScheduledMeetMembersAdapter(meetDetails.attendees as ArrayList<Attendee>, mContext)

        return view
    }

    private fun shareMeetLink(meetTitle: String, meetTimings: String, meetType: String, roomId: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        val baseUrl = if (meetType == FuguAppConstant.MeetType.GOOGLE.toString()) "https://meet.google.com/" else CommonData.getConferenceUrl() + "/"
        val shareText = "${workspaceInfo.fullName} is inviting you to a scheduled meeting(${meetTitle}).\nTimings :- ${meetTimings}\nJoin the meeting: $baseUrl$roomId"
        share.putExtra(Intent.EXTRA_SUBJECT, "Invitation to Meeting")
        share.putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(Intent.createChooser(share, "Share meeting link!"))
    }
}

