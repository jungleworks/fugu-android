package com.skeleton.mvp.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant.FONT_REGULAR
import com.skeleton.mvp.constant.FuguAppConstant.FONT_SEMIBOLD
import com.skeleton.mvp.interfaces.UpdateAllMemberCallback
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import java.util.*

class MultiMemberAddGroupAdapter(groupMembersMap: HashMap<Long, GetAllMembers>, mContext: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<MultiMemberAddGroupAdapter.MyViewHolder>() {
    private var groupMembersMap = HashMap<Long, GetAllMembers>()
    private var mContext: Context
    private var groupMemberList = ArrayList<GetAllMembers>()

    init {
        this.groupMembersMap = groupMembersMap
        this.mContext = mContext
        groupMemberList = ArrayList(groupMembersMap.values)
    }

    fun updateList(groupMembersMap: HashMap<Long, GetAllMembers>) {
        this.groupMembersMap = groupMembersMap
        groupMemberList = ArrayList(groupMembersMap.values)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.multiadd_group_item, parent, false)
        return MultiMemberAddGroupAdapter.MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return groupMemberList.size
    }

    override fun onBindViewHolder(holder: MultiMemberAddGroupAdapter.MyViewHolder, position: Int) {

        val position: Int = holder.adapterPosition
        val multiMembersGroup: GetAllMembers = groupMemberList[position]

        holder.tvName.text = multiMembersGroup.fullName.toString().split(" ")[0]
        holder.tvContactIcon.text = getFirstCharInUpperCase(multiMembersGroup.fullName)
        if (!TextUtils.isEmpty(multiMembersGroup.userThumbnailImage)) {
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


//            Glide.with(membersSearchActivity)
            Glide.with(mContext)
                    .asBitmap()
                    .apply(options)
                    .load(multiMembersGroup.userThumbnailImage)
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

        holder.ivContactIcon.setOnClickListener { v ->
            val userId: Long = groupMemberList[holder.adapterPosition].userId
            if (v?.id == R.id.ivContactImage) {
                groupMemberList.removeAt(holder.adapterPosition)
                groupMembersMap.remove(userId)
                updateList(groupMembersMap)
                notifyItemRemoved(holder.adapterPosition)
                if (mContext is UpdateAllMemberCallback) {
                    (mContext as UpdateAllMemberCallback).updateAllMemberAdapterCallback(userId)
                }
//                    membersSearchActivity.updateAllMemberAdapter(userId)
            }
        }

    }

    class MyViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {

        var tvName: TextView = itemView!!.findViewById(R.id.tvName)
        var tvContactIcon: TextView = itemView!!.findViewById(R.id.tvContactIcon)
        var ivContactIcon: ImageView = itemView!!.findViewById(R.id.ivContactImage)
        var boldFont: Typeface? = Typeface.createFromAsset(itemView!!.context.assets, FONT_SEMIBOLD)
        var normalFont: Typeface? = Typeface.createFromAsset(itemView!!.context.assets, FONT_REGULAR)
        init {
            tvName.typeface = normalFont
        }
    }


}