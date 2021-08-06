package com.skeleton.mvp.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.skeleton.mvp.constant.FuguAppConstant.FONT_BOLD
import com.skeleton.mvp.constant.FuguAppConstant.FONT_REGULAR
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.interfaces.RecyclerViewAddedMembers
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.util.TrimmedTextView
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase

class AllMemberAdapter(allMemberArrayList: ArrayList<GetAllMembers>, mContext: Context, userIds: ArrayList<Long>) : androidx.recyclerview.widget.RecyclerView.Adapter<AllMemberAdapter.MyViewHolder>() {

    private var allMemberArrayList: ArrayList<GetAllMembers>
    private var userIds: ArrayList<Long>
    private var mContext: Context
    private var workspacesInfoList = ArrayList<WorkspacesInfo>()
    private var currentSignedInPosition = 0

    init {
        this.allMemberArrayList = allMemberArrayList
        this.userIds = userIds
        this.mContext = mContext
        allMemberArrayList.sortWith(Comparator { one, other -> other.searchCount.compareTo(one.searchCount) })
        workspacesInfoList = CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
        currentSignedInPosition = CommonData.getCurrentSignedInPosition()
    }

    fun updateList(allMemberArrayList: ArrayList<GetAllMembers>, userIds: ArrayList<Long>) {
        this.allMemberArrayList = allMemberArrayList
        this.userIds = userIds
        allMemberArrayList.sortWith(Comparator { one, other -> other.searchCount.compareTo(one.searchCount) })
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.fugu_list_item, parent, false)
        return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return allMemberArrayList.size
    }

    @Suppress("NAME_SHADOWING")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val position: Int = holder.adapterPosition
        val getAllMembers: GetAllMembers = allMemberArrayList.get(position)
        val text = SpannableStringBuilder()
        text.append(ellipsizeText(getAllMembers.fullName ?: ""))

        if (!TextUtils.isEmpty(getAllMembers.leaveType) && getAllMembers.leaveType.toLowerCase().equals("work_from_home")) {
            text.append(smallText(" (on WFH)"))
        } else if (!TextUtils.isEmpty(getAllMembers.leaveType) && getAllMembers.leaveType.toLowerCase().equals("absent")) {
            text.append(smallText(" (on leave)"))
        }
        holder.tvName.text = text
        holder.tvName.requestLayout()
        holder.tvName.invalidate()
        if (userIds.contains(getAllMembers.userId)) {
            holder.ivTickIcon.visibility = View.VISIBLE
        } else {
            holder.ivTickIcon.visibility = View.GONE
        }

        val subText = if (workspacesInfoList[currentSignedInPosition].config.hideEmail == "1") {
            if (!TextUtils.isEmpty(getAllMembers.phoneNo) && workspacesInfoList[currentSignedInPosition].config.hideContactNumber == "0") {
                getAllMembers.phoneNo
            } else {
                ""
            }
        } else if (workspacesInfoList[currentSignedInPosition].config.hideContactNumber == "1") {
            ""
        } else {
            if (TextUtils.isEmpty(getAllMembers.email) || getAllMembers.email.contains("@fuguchat")) {
                getAllMembers.phoneNo
            } else {
                getAllMembers.email
            }
        }
        holder.tvEmail.text = subText
        if (TextUtils.isEmpty(subText)) {
            holder.tvEmail.visibility = View.GONE
        } else {
            holder.tvEmail.visibility = View.VISIBLE

        }

        if (!TextUtils.isEmpty(getAllMembers.userThumbnailImage)) {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))


            Glide.with(mContext)
                    .asBitmap()
                    .apply(options)
                    .load(getAllMembers.userThumbnailImage)
                    .into(holder.ivContactIcon)

//            Glide.with(membersSearchActivity).load(getAllMembers.userThumbnailImage).asBitmap().centerCrop().placeholder(ContextCompat.getDrawable(membersSearchActivity, R.drawable.fugu_ic_channel_icon))
//                    .error(ContextCompat.getDrawable(membersSearchActivity, R.drawable.fugu_ic_channel_icon))
//                    .into(object : BitmapImageViewTarget(holder.ivContactIcon) {
//                        override fun setResource(resource: Bitmap) {
//                            val circularBitmapDrawable: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(membersSearchActivity.resources, resource)
//                            circularBitmapDrawable.isCircular = true
//                            holder.ivContactIcon.setImageDrawable(circularBitmapDrawable)
//                        }
//                    })
            holder.tvContactIcon.visibility = View.GONE
        } else {
            holder.tvContactIcon.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(getAllMembers.fullName)) {
                holder.tvContactIcon.text = getFirstCharInUpperCase(getAllMembers.fullName)
            }
            when (getAllMembers.userId.toLong() % 5) {
                1L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_purple))
                2L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_teal))
                3L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                4L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_indigo))
                else -> {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                }
            }
        }

        holder.itemView.setOnClickListener { v ->
            val imm: InputMethodManager = (mContext as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v?.windowToken, 0)

            if (holder.ivTickIcon.visibility == View.VISIBLE) {
                holder.ivTickIcon.visibility = View.GONE
            } else {
                holder.ivTickIcon.visibility = View.VISIBLE
            }

            if (userIds.contains(getAllMembers.userId)) {
                userIds.remove(getAllMembers.userId)
            } else {
                userIds.add(getAllMembers.userId)
            }
            if (mContext is RecyclerViewAddedMembers) {
                (mContext as RecyclerViewAddedMembers).recyclerViewAddedMembersCallback(getAllMembers)
            }
            notifyItemChanged(holder.adapterPosition)
        }


    }

    private fun ellipsizeText(text: String): CharSequence {
        val s = SpannableString(text)
        s.setSpan(TrimmedTextView.EllipsizeRange.ELLIPSIS_AT_END, 0, s.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return s
    }

    private fun smallText(text: String): CharSequence {
        val s = SpannableString(text)
        s.setSpan(RelativeSizeSpan(0.7f), 0, text.length, 0) // set size
        return s
    }

    class MyViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {

        var tvName: TextView = itemView!!.findViewById(R.id.tvName)
        var tvEmail: TextView = itemView!!.findViewById(R.id.tvEmail)
        var tvContactIcon: TextView = itemView!!.findViewById(R.id.tvContactIcon)
        var ivContactIcon: ImageView = itemView!!.findViewById(R.id.ivContactImage)
        var ivTickIcon: ImageView = itemView!!.findViewById(R.id.iv_tick)
        var boldFont: Typeface? = null
        var normalFont: Typeface? = null

        init {
            boldFont = Typeface.createFromAsset(itemView!!.context.assets, FONT_BOLD)
            normalFont = Typeface.createFromAsset(itemView.context.assets, FONT_REGULAR)
            tvName.typeface = boldFont
            tvEmail.typeface = normalFont

        }


    }


}