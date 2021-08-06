package com.skeleton.mvp.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.model.Member
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase

class VideoCallInvitationAdapter(
        messageList: ArrayList<Member>,
        mContext: Context,
        userIds: ArrayList<Long>,
        multiMemberAddGroupMap: java.util.LinkedHashMap<Long, Member>,
        private val isHangoutsMeet: Boolean = false
) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {


    private var chatActivity: ChatActivity
    private var membersList: ArrayList<Member>
    private var userIds: ArrayList<Long>
    private var multiMemberAddGroupMap = java.util.LinkedHashMap<Long, Member>()
    private var isMaxLimitReached: Boolean = false

    init {
        this.membersList = messageList
        this.chatActivity = mContext as ChatActivity
        this.userIds = userIds
        this.multiMemberAddGroupMap = multiMemberAddGroupMap
    }

    fun updateList(membersList: ArrayList<Member>, userIds: ArrayList<Long>, multiMemberAddGroupMap: java.util.LinkedHashMap<Long, Member>) {
        this.membersList = membersList
        this.userIds = userIds
        this.multiMemberAddGroupMap = multiMemberAddGroupMap
        isMaxLimitReached =  userIds.size >= CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].config.max_conference_participants
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        return when (getItemViewType(viewType)) {
            0 -> {
                val itemView: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_selected_members_list, parent, false)
                SelectedMembers(itemView)
            }
            else -> {
                val itemView: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.fugu_user_list_item, parent, false)
                GroupMember(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0
        } else {
            1
        }
    }

    fun getItems(): LinkedHashMap<Long, Member> {
        return multiMemberAddGroupMap
    }

    override fun onBindViewHolder(mainHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
        when (mainHolder.adapterPosition) {
            0 -> {
                val holder = mainHolder as SelectedMembers
                val videoCallinvitationMultiAdapter = VideoCallinvitationMultiAdapter(multiMemberAddGroupMap, chatActivity)
                holder.rvSelectedMembers.itemAnimator = ScaleUpAnimator()
                holder.rvSelectedMembers.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(chatActivity, androidx.recyclerview.widget.RecyclerView.HORIZONTAL, false)
                holder.rvSelectedMembers.adapter = videoCallinvitationMultiAdapter
                try {
                    holder.rvSelectedMembers.scrollToPosition(videoCallinvitationMultiAdapter.itemCount - 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (multiMemberAddGroupMap.isEmpty()) {
                    holder.llMain.visibility = View.GONE
                    val params = holder.llMain.layoutParams
                    params.height = 1
                    params.width = 1
                    holder.llMain.layoutParams = params
                } else {
                    holder.llMain.visibility = View.VISIBLE
                    val params = holder.llMain.layoutParams
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT
                    holder.llMain.layoutParams = params
                }
                holder.ivVideoCall.setOnClickListener {
                    if (chatActivity.isNetworkConnected) {
                        holder.ivVideoCall.visibility = View.GONE
                        holder.pbConnecting.visibility = View.VISIBLE
                        chatActivity.apiInitiateVideoConference(userIds, isHangoutsMeet)
                    } else {
                        holder.ivVideoCall.visibility = View.VISIBLE
                        holder.pbConnecting.visibility = View.GONE
                        chatActivity.showErrorMessage("Please check your Internet Connection!")
                    }
                }
            }
            else -> {
                val holder = mainHolder as GroupMember
                setUserView(membersList, holder)
            }

        }
    }

    private fun setUserView(membersList: ArrayList<Member>, holder: GroupMember) {
        val position = holder.adapterPosition
        val getAllMembers: Member = membersList.get(position)
        holder.tvName.text = getAllMembers.name
        if (userIds.contains(getAllMembers.userId)) {
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

        if (!TextUtils.isEmpty(getAllMembers.image)) {

            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))


            Glide.with(chatActivity)
                    .asBitmap()
                    .apply(options)
                    .load(getAllMembers.image)
                    .into(holder.ivContactIcon)

            holder.tvContactIcon.visibility = View.GONE
        } else {
            holder.tvContactIcon.visibility = View.VISIBLE
            holder.tvContactIcon.text = getFirstCharInUpperCase(getAllMembers.name)
            when (getAllMembers.userId.toLong() % 5) {
                1L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(chatActivity, R.drawable.ring_purple))
                2L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(chatActivity, R.drawable.ring_teal))
                3L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(chatActivity, R.drawable.ring_red))
                4L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(chatActivity, R.drawable.ring_indigo))
                else -> {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(chatActivity, R.drawable.ring_red))
                }
            }
        }

        holder.itemView.setOnClickListener {
            if (holder.ivTickIcon.visibility == View.VISIBLE) {
                holder.ivTickIcon.visibility = View.GONE
                if (userIds.contains(getAllMembers.userId)) {
                    userIds.remove(getAllMembers.userId)
                } else {
                    userIds.add(getAllMembers.userId)
                }
                chatActivity.setRecyclerViewAddedMembers(getAllMembers)
                notifyItemChanged(holder.adapterPosition)
            } else {
                if (!isMaxLimitReached) {
                    holder.ivTickIcon.visibility = View.VISIBLE
                    if (userIds.contains(getAllMembers.userId)) {
                        userIds.remove(getAllMembers.userId)
                    } else {
                        userIds.add(getAllMembers.userId)
                    }
                    chatActivity.setRecyclerViewAddedMembers(getAllMembers)
                    notifyItemChanged(holder.adapterPosition)
                } else {
                    Toast.makeText(chatActivity, "You cannot select more than " + CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].config.max_conference_participants + " people", Toast.LENGTH_LONG).show()
                    chatActivity.setRecyclerViewAddedMembers(null)
                    notifyItemChanged(holder.adapterPosition)
                }
            }
        }
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