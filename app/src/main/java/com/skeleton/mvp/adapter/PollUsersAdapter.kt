package com.skeleton.mvp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.PollDetailsActivity
import com.skeleton.mvp.model.User
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.appcompat.widget.AppCompatImageView
import android.text.TextUtils
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.gson.JsonObject
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import kotlinx.android.synthetic.main.activity_invite_onboard.view.*
import org.json.JSONObject


internal class PollUsersAdapter(context: Context, users: ArrayList<User>) : androidx.recyclerview.widget.RecyclerView.Adapter<PollUsersAdapter.MyViewHolder>() {
    private var context: Context
    private var users: ArrayList<User>

    init {
        this.context = context
        this.users = users
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollUsersAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_poll_user, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PollUsersAdapter.MyViewHolder, pos: Int) {
        val position = holder.adapterPosition
        val user = users[position]
        holder.tvUserName.text = user.fullName
        if (!TextUtils.isEmpty(user.userImage)) {
            holder.ivName.visibility = View.GONE

            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.man)
                    .error(R.drawable.man)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))


            Glide.with(context)
                    .asBitmap()
                    .apply(options)
                    .load(user.userImage)
                    .into(holder.ivUserImage)

        } else {
            holder.ivName.visibility = View.VISIBLE
            holder.ivName.text = getFirstCharInUpperCase(user.fullName)
            if (user.getUserId()!! % 5 == 1L) {
                holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_greyy))
            } else if (user.getUserId()!! % 5 == 2L) {
                holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_teal))
            } else if (user.getUserId()!! % 5 == 3L) {
                holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_red))
            } else if (user.getUserId()!! % 5 == 4L) {
                holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_indigo))
            } else {
                holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_red))
            }
        }
        if (position == users.size - 1) {
            holder.divider.visibility = View.GONE
        } else {
            holder.divider.visibility = View.VISIBLE
        }

    }


    override fun getItemCount(): Int {
        return users.size
    }

    inner class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var ivUserImage: AppCompatImageView = itemView.findViewById(R.id.ivUserImage)
        var tvUserName: AppCompatTextView = itemView.findViewById(R.id.tvUserName)
        var ivName: AppCompatTextView = itemView.findViewById(R.id.ivName)
        var divider: View = itemView.findViewById(R.id.divider)
    }
}
