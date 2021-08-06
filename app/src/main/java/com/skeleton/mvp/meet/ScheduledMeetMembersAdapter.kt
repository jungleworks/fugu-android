package com.skeleton.mvp.meet

/********************************
Created by Amandeep Chauhan     *
Date :- 13/08/2020              *
 ********************************/

import android.content.Context
import android.graphics.Point
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.data.model.scheduleMeets.Attendee
import com.skeleton.mvp.util.CommonUtil
import com.skeleton.mvp.utils.FuguUtils
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase

class ScheduledMeetMembersAdapter(var attendees: ArrayList<Attendee>, val mContext: Context) : RecyclerView.Adapter<ScheduledMeetMembersAdapter.MemberViewHolder>() {
    var maxItems: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.meet_selected_member_item, parent, false)
        return MemberViewHolder(view)
    }

    override fun getItemCount(): Int {
        maxItems = getScreenWidth() / CommonUtil.dpToPx(mContext, 50)
        return if (this.attendees.size > maxItems)
            maxItems
        else
            this.attendees.size
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val pos: Int = holder.adapterPosition
        val attendee: Attendee = this.attendees[pos]
        if (pos < maxItems - 1 || maxItems == this.attendees.size) {
//            holder.tvName.text = attendee.fullName.toString().split(" ")[0]
            holder.tvContactIcon.text = getFirstCharInUpperCase(attendee.fullName)
            if (!TextUtils.isEmpty(attendee.userThumbnailImage)) {
                holder.tvContactIcon.visibility = View.GONE

                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(CenterCrop(), RoundedCorners(10))

                Glide.with(mContext as MainActivity)
                        .asBitmap()
                        .apply(options)
                        .load(attendee.userThumbnailImage)
                        .into(holder.ivContactIcon)

                holder.tvContactIcon.visibility = View.GONE
            } else {
                holder.tvContactIcon.visibility = View.VISIBLE
                val backgroundId = if (attendee.userId != -1L) attendee.userId else attendee.email.hashCode().toLong()
                holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, FuguUtils.getBgResIdFromSomeId(backgroundId)))
            }
        } else {
//            holder.tvName.text = ""
            val moreText: Int = (this.attendees.size - (maxItems - 1))
            holder.tvContactIcon.text = "+${moreText}"
            holder.tvContactIcon.visibility = View.VISIBLE
            holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.meet_selected_members_more_bg))
        }
    }

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvContactIcon: TextView = itemView.findViewById(R.id.tvContactIcon)
        val ivContactIcon: ImageView = itemView.findViewById(R.id.ivContactImage)
    }

    private fun getScreenWidth(): Int {
        val wm: WindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }
}