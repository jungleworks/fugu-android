package com.skeleton.mvp.meet

/********************************
Created by Amandeep Chauhan     *
Date :- 29/04/2020              *
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
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.util.CommonUtil
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase

class NewMeetMembersAdapter(var members: HashMap<Long, GetAllMembers>, val mContext: Context) : RecyclerView.Adapter<NewMeetMembersAdapter.MemberViewHolder>() {
    val memberList: ArrayList<GetAllMembers> = ArrayList()
    var maxItems: Int = 0

    init {
        memberList.addAll(members.values)
//        Toast.makeText(mContext, "${memberList.size} Members ${members.size} Hashed", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.meet_selected_member_item, parent, false)
        return MemberViewHolder(view)
    }

    override fun getItemCount(): Int {
        maxItems = getScreenWidth() / CommonUtil.dpToPx(mContext, 50)
        return if (memberList.size > maxItems)
            maxItems
        else
            memberList.size
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {

        val position: Int = holder.adapterPosition
        val multiMembersGroup: GetAllMembers = memberList[position]
        if (position < maxItems - 1 || maxItems == memberList.size) {
            holder.tvName.text = multiMembersGroup.fullName.toString().split(" ")[0]
            holder.tvContactIcon.text = getFirstCharInUpperCase(multiMembersGroup.fullName)
            if (!TextUtils.isEmpty(multiMembersGroup.userImage)) {
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
                        .load(multiMembersGroup.userImage)
                        .into(holder.ivContactIcon)

                holder.tvContactIcon.visibility = View.GONE
            } else {
                holder.tvContactIcon.visibility = View.VISIBLE
                when (multiMembersGroup.userId!! % 5) {
                    1L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_purple))
                    2L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_teal))
                    3L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                    4L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_indigo))
                    else -> {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                    }
                }
            }
        } else {
            holder.tvName.text = ""
            val moreText: Int = (memberList.size - (maxItems - 1))
//            Toast.makeText(mContext, "${moreText} More", Toast.LENGTH_SHORT).show()
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