package com.skeleton.mvp.adapter

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.FuguSearchsActivity
import com.skeleton.mvp.model.FuguSearchResult
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase


@Suppress("NAME_SHADOWING")
class FuguMultiMemberAddGroupAdapter(groupMembersMap: LinkedHashMap<Long, FuguSearchResult>, mContext: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<FuguMultiMemberAddGroupAdapter.MyViewHolder>(), Animation.AnimationListener {
    private var groupMemberList = ArrayList<FuguSearchResult>()
    private var groupMembersMap = LinkedHashMap<Long, FuguSearchResult>()
    private var mContext: Context
    private var searchActivity: FuguSearchsActivity
    private var animation: Animation? = null

    init {
        this.groupMembersMap = groupMembersMap
        this.mContext = mContext
        searchActivity = mContext as FuguSearchsActivity
        groupMemberList = ArrayList(groupMembersMap.values)
        animation = AnimationUtils.loadAnimation(mContext, R.anim.emoji_anim)
        animation?.setAnimationListener(this)
    }

    fun updateList(groupMembersMap: LinkedHashMap<Long, FuguSearchResult>) {
        this.groupMembersMap = groupMembersMap
        groupMemberList = ArrayList(groupMembersMap.values)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuguMultiMemberAddGroupAdapter.MyViewHolder {
        var itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.multiadd_group_item, parent, false)
        return FuguMultiMemberAddGroupAdapter.MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return groupMemberList.size

    }

    override fun onBindViewHolder(holder: FuguMultiMemberAddGroupAdapter.MyViewHolder, position: Int) {
        val position: Int = holder.adapterPosition
        val multiMembersGroup: FuguSearchResult = groupMemberList[position]
        holder.tvName.text = multiMembersGroup.name.toString().split(" ")[0]
        holder.tvContactIcon.text = getFirstCharInUpperCase(multiMembersGroup.name)
        if (!TextUtils.isEmpty(multiMembersGroup.user_image)) {
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


            Glide.with(searchActivity)
                    .asBitmap()
                    .apply(options)
                    .load(multiMembersGroup.user_image)
                    .into(holder.ivContactIcon)
            holder.tvContactIcon.visibility = View.GONE
        } else {


            holder.tvContactIcon.visibility = View.VISIBLE
            when (multiMembersGroup.user_id!! % 5) {
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
                val userId: Long = groupMemberList[holder.adapterPosition].user_id
                if (v?.id == R.id.ivContactImage) {
                    groupMemberList.removeAt(holder.adapterPosition)
                    groupMembersMap.remove(userId)
                    updateList(groupMembersMap)
                    notifyItemRemoved(holder.adapterPosition)
                    searchActivity.updateSearchAdapter(userId)
                }
            }

        })


    }

    class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvName: TextView
        var tvContactIcon: TextView
        var ivContactIcon: ImageView

        init {
            this.tvName = itemView.findViewById(R.id.tvName)
            this.ivContactIcon = itemView.findViewById(R.id.ivContactImage)
            this.tvContactIcon = itemView.findViewById(R.id.tvContactIcon)


        }

    }

    override fun onAnimationRepeat(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationStart(animation: Animation?) {
    }


}