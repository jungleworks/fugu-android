package com.skeleton.mvp.meet

/********************************
Created by Amandeep Chauhan     *
Date :- 10/08/2020              *
 ********************************/

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.model.scheduleMeets.MeetDetails
import com.skeleton.mvp.utils.*
import java.util.*

class ScheduledMeetsAdapter(var meetingsList: ArrayList<MeetDetails>, val mContext: Context) : RecyclerView.Adapter<ScheduledMeetsAdapter.MeetViewHolder>() {

    val dateUtils: DateUtils = DateUtils.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.scheduled_meet_row_item, parent, false)
        return MeetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return meetingsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MeetViewHolder, position: Int) {
        val meetDetails = meetingsList[holder.adapterPosition]
        if(meetDetails.meetType == FuguAppConstant.MeetType.GOOGLE.toString()) {
            holder.ivMeetIcon.setImageResource(R.drawable.ic_google_meet_small)
        } else {
            holder.ivMeetIcon.setImageResource(R.drawable.ic_fugu)
        }
        holder.tvMeetTitle.text = meetDetails.title
        val startTime = dateUtils.getLocalDateObject(meetDetails.startDatetime)
        val endTime = dateUtils.getLocalDateObject(meetDetails.endDatetime)
        val timings = "${getFormattedTime(startTime)} - ${getFormattedTime(endTime)}"
        holder.tvMeetTimings.text = timings
        val currentTime = Calendar.getInstance()
        val tomorrow = Calendar.getInstance()
        val meetStartTime = Calendar.getInstance()
        meetStartTime.time = startTime
        val isSameDay = io.github.memfis19.cadar.internal.utils.DateUtils.isSameDay(startTime, Calendar.getInstance().time)
        if (isSameDay && startTime.time < currentTime.timeInMillis && currentTime.timeInMillis < endTime.time) {
            holder.tvOngoing.visible()
            holder.tvJoinMeet.visible()
            holder.tvTomorrow.gone()
            holder.tvJoinMeet.setOnClickListener {
                holder.itemView.performClick()
            }
        } else if (tomorrow.get(Calendar.DAY_OF_YEAR) + 1 == meetStartTime.get(Calendar.DAY_OF_YEAR)
                && tomorrow.get(Calendar.YEAR) == meetStartTime.get(Calendar.YEAR)) {
            holder.tvTomorrow.visible()
            holder.tvOngoing.gone()
            holder.tvJoinMeet.gone()
        } else {
            holder.tvOngoing.gone()
            holder.tvJoinMeet.gone()
            holder.tvTomorrow.gone()
            if (!isSameDay)
                holder.tvMeetTimings.text = getFormattedDate(startTime) + " (" + timings + ")"
        }
        holder.itemView.setOnClickListener {
            JoinScheduledMeetBottomSheetFragment(meetDetails, mContext).show((mContext as MainActivity).supportFragmentManager, "JoinScheduledMeetBottomSheetFragment")
        }
    }

    class MeetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMeetTitle: AppCompatTextView = itemView.findViewById(R.id.tvMeetTitle)
        val tvMeetTimings: AppCompatTextView = itemView.findViewById(R.id.tvMeetTimings)
        val tvOngoing: AppCompatTextView = itemView.findViewById(R.id.tvOngoing)
        val tvTomorrow: AppCompatTextView = itemView.findViewById(R.id.tvTomorrow)
        val tvJoinMeet: AppCompatTextView = itemView.findViewById(R.id.tvJoinMeet)
        val ivMeetIcon: AppCompatImageView = itemView.findViewById(R.id.ivMeetIcon)
    }

    fun updateList(meetingsList: ArrayList<MeetDetails>) {
        this.meetingsList = meetingsList
        notifyDataSetChanged()
    }

}