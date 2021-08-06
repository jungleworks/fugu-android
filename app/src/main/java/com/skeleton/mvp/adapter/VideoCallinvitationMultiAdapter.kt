package com.skeleton.mvp.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.model.Member
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import kotlinx.android.synthetic.main.video_call_invitation_fragment.*
import java.util.*

class VideoCallinvitationMultiAdapter(groupMembersMap: HashMap<Long, Member>, mContext: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<VideoCallinvitationMultiAdapter.MyViewHolder>() {
    private var groupMembersMap = HashMap<Long, Member>()
    private var mContext: Context
    private var groupMemberList = ArrayList<Member>()
    private var chatActivity: ChatActivity


    init {
        this.groupMembersMap = groupMembersMap
        this.mContext = mContext
        groupMemberList = ArrayList(groupMembersMap.values)
        chatActivity = mContext as ChatActivity
    }

    fun updateList(groupMembersMap: HashMap<Long, Member>) {
        this.groupMembersMap = groupMembersMap
        groupMemberList = ArrayList(groupMembersMap.values)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.multiadd_group_item, parent, false)
        return VideoCallinvitationMultiAdapter.MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return groupMemberList.size

    }

    override fun onBindViewHolder(holder: VideoCallinvitationMultiAdapter.MyViewHolder, position: Int) {

        val position: Int = holder.adapterPosition
        val multiMembersGroup: Member = groupMemberList[position]


        holder.tvName.text = multiMembersGroup.name.toString().split(" ")[0]
        holder.tvContactIcon.text = getFirstCharInUpperCase(multiMembersGroup.name)
        if (!TextUtils.isEmpty(multiMembersGroup.image)) {
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


            Glide.with(chatActivity)
                    .asBitmap()
                    .apply(options)
                    .load(multiMembersGroup.image)
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

        holder.ivContactIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                try {
                    val userId: Long = groupMemberList[holder.adapterPosition].userId
                    if (v?.id == R.id.ivContactImage) {
                        groupMemberList.removeAt(holder.adapterPosition)
                        groupMembersMap.remove(userId)
                        updateList(groupMembersMap)
                        notifyItemRemoved(holder.adapterPosition)
                        chatActivity.updateAllMemberAdapter(userId)
//                        (chatActivity.supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment") as BottomSheetDialogFragment).cbSelectAll.isChecked = false
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

        })


    }

    class MyViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {
        var tvName: TextView = itemView!!.findViewById(R.id.tvName)
        var tvContactIcon: TextView = itemView!!.findViewById(R.id.tvContactIcon)
        var ivContactIcon: ImageView = itemView!!.findViewById(R.id.ivContactImage)
    }


}