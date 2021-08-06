package com.skeleton.mvp.adapter

import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.skeleton.mvp.R
import com.skeleton.mvp.model.SeenUser
import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase

class SeenUserAdapter(seenUsersList: ArrayList<SeenUser>) : androidx.recyclerview.widget.RecyclerView.Adapter<SeenUserAdapter.MyViewHolder>() {

    private var seenUsersList = ArrayList<SeenUser>()

    init {
        this.seenUsersList = seenUsersList;
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_seen, parent, false))
    }

    override fun getItemCount(): Int {
        return seenUsersList.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val position = p0.adapterPosition
        setUserSeenBy(seenUsersList[position], p0)

    }

    private fun setUserSeenBy(message: SeenUser, holder: SeenUserAdapter.MyViewHolder) {
        holder.tvSeenAt.text = DateUtils.getTime(DateUtils().convertToLocal(message.seentAt))
        holder.tvSeenBy.text = message.fullName

        if (!TextUtils.isEmpty(message.userImage)) {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))
            Glide.with(holder.itemView.context)
                    .asBitmap()
                    .apply(options)
                    .load(message.userImage)
                    .into(holder.ivUserImage)
            holder.tvUserIcon.visibility = View.GONE
        } else {
            holder.tvUserIcon.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(message.fullName)) {
                holder.tvUserIcon.setText(getFirstCharInUpperCase(message.fullName))
            }
            when (message.userId % 5) {
                1L -> holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ring_purple))
                2L -> holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ring_teal))
                3L -> holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ring_red))
                4L -> holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ring_indigo))
                else -> {
                    holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ring_red))
                }
            }
        }
    }

    fun updateList(seenUsers: java.util.ArrayList<SeenUser>) {
        this.seenUsersList = seenUsers;
    }

    class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvSeenBy: AppCompatTextView = itemView.findViewById(R.id.tvSeenBy)
        var tvSeenAt: AppCompatTextView = itemView.findViewById(R.id.tvSeenAt)
        val ivUserImage: ImageView = itemView.findViewById(R.id.ivUserImage)
        val tvUserIcon: AppCompatTextView = itemView.findViewById(R.id.tvUserIcon)
    }
}