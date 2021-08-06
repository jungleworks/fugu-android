package com.skeleton.mvp.groupTasks

/********************************
Created by Amandeep Chauhan     *
Date :- 05/08/2020              *
 ********************************/

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.skeleton.mvp.R
import com.skeleton.mvp.model.GroupMember
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import java.util.*

@Suppress("NAME_SHADOWING")
class GroupMemberAdapter(private var activity: Activity, private var membersList: ArrayList<GroupMember>, private var selectedMembers: LinkedHashMap<Long, GroupMember>, private var maxSelectionAllowed: Int = 0) : RecyclerView.Adapter<GroupMemberAdapter.ViewHolder>() {

    private var isMaxLimitReached: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member: GroupMember = membersList[holder.adapterPosition]
        holder.tvName.text = if (!TextUtils.isEmpty(member.name)) member.name else ""
        if (selectedMembers.contains(member.userId)) {
            holder.ivTickIcon.visibility = View.VISIBLE
            holder.llRootListItem.alpha = 1f
        } else {
            holder.ivTickIcon.visibility = View.GONE
            if (isMaxLimitReached) {
                holder.llRootListItem.alpha = 0.5f
            } else {
                holder.llRootListItem.alpha = 1f
            }
        }

        if (TextUtils.isEmpty(member.email) || member.email.contains("@fuguchat")) {
            holder.tvEmail.text = ""
        } else {
            holder.tvEmail.text = member.email
        }

        if (!TextUtils.isEmpty(member.image)) {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))


            Glide.with(activity)
                    .asBitmap()
                    .apply(options)
                    .load(member.image)
                    .into(holder.ivContactIcon)

            holder.tvContactIcon.visibility = View.GONE
        } else {
            holder.tvContactIcon.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(member.name)) {
                holder.tvContactIcon.text = getFirstCharInUpperCase(member.name)
            }
            when (member.userId.toLong() % 5) {
                1L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ring_purple))
                2L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ring_teal))
                3L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ring_red))
                4L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ring_indigo))
                else -> {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ring_red))
                }
            }
        }

        holder.itemView.setOnClickListener {
            if (holder.ivTickIcon.visibility == View.VISIBLE) {
                holder.ivTickIcon.visibility = View.GONE
                if (selectedMembers.contains(member.userId)) {
                    selectedMembers.remove(member.userId)
                } else {
                    selectedMembers[member.userId] = member
                }
                notifyItemChanged(holder.adapterPosition)
                ((activity as AppCompatActivity).supportFragmentManager.findFragmentByTag("SelectGroupMemberDialogFragment") as SelectGroupMemberDialogFragment).updateUserIds(selectedMembers)
            } else {
                if (!isMaxLimitReached) {
                    holder.ivTickIcon.visibility = View.VISIBLE
                    if (selectedMembers.contains(member.userId)) {
                        selectedMembers.remove(member.userId)
                    } else {
                        selectedMembers[member.userId] = member
                    }
                    isMaxLimitReached = maxSelectionAllowed != 0 && maxSelectionAllowed > selectedMembers.size
                    notifyItemChanged(holder.adapterPosition)
                    ((activity as AppCompatActivity).supportFragmentManager.findFragmentByTag("SelectGroupMemberDialogFragment") as SelectGroupMemberDialogFragment).updateUserIds(selectedMembers)
                } else {
                    Toast.makeText(activity, "You cannot select more than $maxSelectionAllowed people", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateList(membersList: ArrayList<GroupMember>) {
        this.membersList = membersList
    }

    class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.select_group_member_list_item, parent, false)) {

        internal val tvName: TextView = itemView.findViewById(R.id.tvName)
        internal val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        internal val tvContactIcon: TextView = itemView.findViewById(R.id.tvContactIcon)
        internal val ivContactIcon: ImageView = itemView.findViewById(R.id.ivContactImage)
        internal val ivTickIcon: ImageView = itemView.findViewById(R.id.iv_tick)
        internal val llRootListItem: LinearLayout = itemView.findViewById(R.id.llRootListItem)
        internal val cbSelected: CheckBox = itemView.findViewById(R.id.cbSelected)
    }


    override fun getItemCount(): Int {
        return membersList.size
    }

}