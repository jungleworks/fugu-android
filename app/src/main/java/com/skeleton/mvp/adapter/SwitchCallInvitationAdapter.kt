package com.skeleton.mvp.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatImageView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.skeleton.mvp.R
import com.skeleton.mvp.model.FuguSearchResult
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import com.skeleton.mvp.videoCall.FuguCallActivity

class SwitchCallInvitationAdapter(messageList: ArrayList<FuguSearchResult>, mContext: Context, userIds: ArrayList<Long>, multiMemberAddGroupMap: java.util.LinkedHashMap<Long, FuguSearchResult>) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    private var callActivity: FuguCallActivity
    private var membersList: ArrayList<FuguSearchResult>
    private var userIds: ArrayList<Long>
    private var multiMemberAddGroupMap = java.util.LinkedHashMap<Long, FuguSearchResult>()
    private var isMaxLimitReached: Boolean = false

    init {
        this.membersList = messageList
        this.callActivity = mContext as FuguCallActivity
        this.userIds = userIds
        this.multiMemberAddGroupMap = multiMemberAddGroupMap
    }

    fun updateList(membersList: ArrayList<FuguSearchResult>, userIds: ArrayList<Long>, multiMemberAddGroupMap: java.util.LinkedHashMap<Long, FuguSearchResult>) {
        this.membersList = membersList
        this.userIds = userIds
        this.multiMemberAddGroupMap = multiMemberAddGroupMap
        isMaxLimitReached = userIds.size >= 10
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        when (getItemViewType(viewType)) {
            0 -> {
                val itemView: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_selected_members_list, parent, false)
                return SelectedMembers(itemView)
            }
            else -> {
                val itemView: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.fugu_user_list_item, parent, false)
                return GroupMember(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 0
        } else {
            return 1
        }
    }

    override fun onBindViewHolder(mainHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
        val position: Int = mainHolder.adapterPosition
        when (position) {
            0 -> {
                val holder = mainHolder as SelectedMembers
                val videoCallinvitationMultiAdapter = SwitchCallinvitationMultiAdapter(multiMemberAddGroupMap, callActivity)
                holder.rvSelectedMembers.itemAnimator = ScaleUpAnimator()
                holder.rvSelectedMembers.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(callActivity, androidx.recyclerview.widget.RecyclerView.HORIZONTAL, false)
                holder.rvSelectedMembers.adapter = videoCallinvitationMultiAdapter
                try {
                    holder.rvSelectedMembers.scrollToPosition(videoCallinvitationMultiAdapter.itemCount - 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (multiMemberAddGroupMap.isEmpty()) {
                    holder.llMain.visibility = View.GONE
                    val params = holder.llMain.getLayoutParams()
                    params.height = 1
                    params.width = 1
                    holder.llMain.layoutParams = params
                } else {
                    holder.llMain.visibility = View.VISIBLE
                    val params = holder.llMain.getLayoutParams()
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT
                    holder.llMain.layoutParams = params
                }

                holder.ivVideoCall.setOnClickListener {
                    if (callActivity.isNetworkConnected) {
                        holder.ivVideoCall.visibility = View.GONE
                        holder.pbConnecting.visibility = View.VISIBLE
                        callActivity.apiInitiateVideoConference(userIds)
                    } else {
                        holder.ivVideoCall.visibility = View.VISIBLE
                        holder.pbConnecting.visibility = View.GONE
                        callActivity.showErrorMessage("Please check your Internet Connection!")
                    }
                }
            }
            else -> {
                val holder = mainHolder as GroupMember
                setUserView(membersList, holder)
            }

        }
    }

    private fun setUserView(membersList: ArrayList<FuguSearchResult>, holder: GroupMember) {
        val position = holder.adapterPosition
        val getAllMembers: FuguSearchResult = membersList.get(position)
        holder.tvName.text = getAllMembers.name
        if (userIds.contains(getAllMembers.user_id)) {
            holder.ivTickIcon.visibility = View.VISIBLE
            holder.llMain.alpha = 1f
        } else {
            holder.ivTickIcon.visibility = View.GONE
            if (isMaxLimitReached) {
                holder.llMain.alpha = 0.5f
            } else {
                holder.llMain.alpha = 1f
            }
        }

        if (TextUtils.isEmpty(getAllMembers.email) || getAllMembers.email.contains("@fuguchat")) {
            holder.tvEmail.text = ""
        } else {
            holder.tvEmail.text = getAllMembers.email
        }

        if (!TextUtils.isEmpty(getAllMembers.user_image)) {

            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(100))


            Glide.with(callActivity)
                    .asBitmap()
                    .apply(options)
                    .load(getAllMembers.user_image)
                    .into(holder.ivContactIcon)

            holder.tvContactIcon.visibility = View.GONE
        } else {
            holder.tvContactIcon.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(getAllMembers.name)) {
                holder.tvContactIcon.text = getFirstCharInUpperCase(getAllMembers.name)
            }
            when (getAllMembers.user_id.toLong() % 5) {
                1L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(callActivity, R.drawable.ring_purple))
                2L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(callActivity, R.drawable.ring_teal))
                3L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(callActivity, R.drawable.ring_red))
                4L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(callActivity, R.drawable.ring_indigo))
                else -> {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(callActivity, R.drawable.ring_red))
                }
            }
        }

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (holder.ivTickIcon.visibility == View.VISIBLE) {
                    holder.ivTickIcon.visibility = View.GONE
                    if (userIds.contains(getAllMembers.user_id)) {
                        userIds.remove(getAllMembers.user_id)
                    } else {
                        userIds.add(getAllMembers.user_id)
                    }
                    callActivity.setRecyclerViewAddedMembers(getAllMembers)
                    notifyItemChanged(holder.adapterPosition)
                } else {
                    if (!isMaxLimitReached) {
                        holder.ivTickIcon.visibility = View.VISIBLE
                        if (userIds.contains(getAllMembers.user_id)) {
                            userIds.remove(getAllMembers.user_id)
                        } else {
                            userIds.add(getAllMembers.user_id)
                        }
                        callActivity.setRecyclerViewAddedMembers(getAllMembers)
                        notifyItemChanged(holder.adapterPosition)
                    } else {
                        Toast.makeText(callActivity, "You cannot select more than 10 people", Toast.LENGTH_LONG).show()
                        callActivity.setRecyclerViewAddedMembers(null)
                        notifyItemChanged(holder.adapterPosition)
                    }
                }


            }
        })
    }

    override fun getItemCount(): Int {
        return membersList.size

    }

    class GroupMember(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {

        var tvName: TextView = itemView!!.findViewById(R.id.tvName)
        var tvEmail: TextView = itemView!!.findViewById(R.id.tvEmail)
        var tvContactIcon: TextView = itemView!!.findViewById(R.id.tvContactIcon)
        var ivContactIcon: ImageView = itemView!!.findViewById(R.id.ivContactImage)
        var ivTickIcon: ImageView = itemView!!.findViewById(R.id.iv_tick)
        var llMain: LinearLayout = itemView!!.findViewById(R.id.llMain)

    }

    class SelectedMembers(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {
        var rvSelectedMembers: androidx.recyclerview.widget.RecyclerView = itemView!!.findViewById(R.id.rvSelectedMembers)
        var llMain: LinearLayout = itemView!!.findViewById(R.id.llMain)
        var ivVideoCall: AppCompatImageView = itemView!!.findViewById(R.id.ivVideoCall)
        var pbConnecting: ProgressBar = itemView!!.findViewById(R.id.pbConnecting)
    }

}