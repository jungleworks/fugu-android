package com.skeleton.mvp.groupTasks.selectedTaskUsers

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.skeleton.mvp.R
import com.skeleton.mvp.data.model.groupTasks.UserData
import com.skeleton.mvp.groupTasks.GroupMemberAdapter
import com.skeleton.mvp.groupTasks.TaskActivity
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase

class SelectedTaskUsersAdapter(private val taskId: Long, var userData: ArrayList<UserData>) : RecyclerView.Adapter<GroupMemberAdapter.ViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberAdapter.ViewHolder {
        context = parent.context
        return GroupMemberAdapter.ViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return userData.size
    }

    override fun onBindViewHolder(holder: GroupMemberAdapter.ViewHolder, position: Int) {
        val userData: UserData = userData[holder.adapterPosition]
        holder.tvName.text = if (!TextUtils.isEmpty(userData.fullName)) userData.fullName else ""
        if (userData.isCompleted == 1L) {
            holder.ivTickIcon.visibility = View.VISIBLE
        } else {
            holder.ivTickIcon.visibility = View.GONE
        }

        if (TextUtils.isEmpty(userData.email) || userData.email.contains("@fuguchat")) {
            holder.tvEmail.text = ""
        } else {
            holder.tvEmail.text = userData.email
        }

        if (!TextUtils.isEmpty(userData.userThumbnailImage)) {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))


            Glide.with(context)
                    .asBitmap()
                    .apply(options)
                    .load(userData.userThumbnailImage)
                    .into(holder.ivContactIcon)

            holder.tvContactIcon.visibility = View.GONE
        } else {
            holder.tvContactIcon.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(userData.fullName)) {
                holder.tvContactIcon.text = getFirstCharInUpperCase(userData.fullName)
            }
            when (userData.userID % 5) {
                1L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_purple))
                2L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_teal))
                3L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_red))
                4L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_indigo))
                else -> {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_red))
                }
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, TaskActivity::class.java)
            intent.putExtra("isViewMode", true)
            intent.putExtra("isAdminViewingUser", true)
            intent.putExtra("eventId", taskId)
            intent.putExtra("user_id", userData.userID.toString())
            context.startActivity(intent)
        }
    }

    fun updateList(userData: ArrayList<UserData>) {
        this.userData = userData
        notifyDataSetChanged()
    }

}