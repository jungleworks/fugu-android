package com.skeleton.mvp.adapter

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
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
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.FuguSearchsActivity
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguCacheSearchResult

import com.skeleton.mvp.model.FuguSearchResult
import com.skeleton.mvp.util.TrimmedTextView
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import org.json.JSONArray
import kotlin.collections.ArrayList


@Suppress("NAME_SHADOWING")
class FuguSearchResultsAdapter(searchResultList: ArrayList<FuguSearchResult>, mContext: Context, isParentSearch: Boolean, channelId: Long, userIds: ArrayList<Long>) : androidx.recyclerview.widget.RecyclerView.Adapter<FuguSearchResultsAdapter.MyViewHolder>() {
    var searchResultList = ArrayList<FuguSearchResult>()
    private var mContext: Context
    private var isParentSearch: Boolean
    private var channelId: Long?
    private var searchActivity: FuguSearchsActivity
    private var userIds = ArrayList<Long>()

    init {
        this.searchResultList = searchResultList
        this.mContext = mContext
        this.isParentSearch = isParentSearch
        this.channelId = channelId
        searchActivity = mContext as FuguSearchsActivity
        this.userIds = userIds
    }

    fun updateList(searchResultList: ArrayList<FuguSearchResult>, userIds: ArrayList<Long>) {
        this.searchResultList = searchResultList
        this.userIds = userIds
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuguSearchResultsAdapter.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.fugu_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    override fun onBindViewHolder(holder: FuguSearchResultsAdapter.MyViewHolder, position: Int) {
        val position: Int = holder.adapterPosition
        val fuguSearchResult: FuguSearchResult = searchResultList.get(position)
       // holder.tvName.setText(fuguSearchResult.name)
        val text = SpannableStringBuilder()
        text.append(ellipsizeText(fuguSearchResult.name))

        if (!TextUtils.isEmpty(fuguSearchResult.getLeaveType()) && fuguSearchResult.getLeaveType().toLowerCase() == "work_from_home") {
            text.append(smallText(" (on WFH)"))
        } else if (!TextUtils.isEmpty(fuguSearchResult.getLeaveType()) && fuguSearchResult.getLeaveType().toLowerCase() == "absent") {
            text.append(smallText(" (on leave)"))
        }
        holder.tvName.setText(text, TextView.BufferType.SPANNABLE)
        holder.tvName.requestLayout()
        holder.tvName.invalidate()
        holder.tvEmail.setText(fuguSearchResult.email)

        if (userIds.contains(fuguSearchResult.user_id)) {
            holder.ivTickIcon.visibility = View.VISIBLE
        } else {
            holder.ivTickIcon.visibility = View.GONE
        }
        holder.tvContactIcon.text = getFirstCharInUpperCase(fuguSearchResult.name)
        if (fuguSearchResult.isGroup) {
            Glide.with(mContext).load(fuguSearchResult.user_image).into(holder.ivContactIcon)
        } else {
            if (!TextUtils.isEmpty(fuguSearchResult.user_image)) {
                holder.tvContactIcon.visibility = View.GONE
//                Glide.with(searchActivity).load(fuguSearchResult.user_image).asBitmap().centerCrop().placeholder(ContextCompat.getDrawable(searchActivity, R.drawable.fugu_ic_channel_icon))
//                        .error(ContextCompat.getDrawable(searchActivity, R.drawable.fugu_ic_channel_icon))
//                        .into(object : BitmapImageViewTarget(holder.ivContactIcon) {
//                            override fun setResource(resource: Bitmap) {
//                                val circularBitmapDrawable: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(searchActivity.resources, resource)
//                                circularBitmapDrawable.isCircular = true
//                                holder.ivContactIcon.setImageDrawable(circularBitmapDrawable)
//                            }
//                        })

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
                        .load(fuguSearchResult.user_image)
                        .into(holder.ivContactIcon)

                holder.tvContactIcon.visibility = View.GONE
            } else {


                holder.tvContactIcon.visibility = View.VISIBLE
                when (fuguSearchResult.user_id!! % 5) {
                    1L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_purple))
                    2L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_teal))
                    3L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                    4L -> holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_indigo))
                    else -> {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                    }
                }
            }

        }
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val userIdsToBeAdded = ArrayList<Long>()
                var count: Int = 0
                var j = 0
                var flag = false
                var searchResults = ArrayList<FuguCacheSearchResult>()
                if (CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey) != null) {
                    searchResults = CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                    for (i: Int in searchResults.indices) {
                        if (fuguSearchResult.user_id.compareTo(searchResults[i].user_id) == 0) {
                            flag = true
                            j = i
                        }
                    }
                    if (flag) {
                        count = searchResults[j].clickCount
                        count += 1
                        searchResults.removeAt(j)
                        searchResults.add(object : FuguCacheSearchResult(fuguSearchResult.name, fuguSearchResult.user_id, fuguSearchResult.user_image, fuguSearchResult.email, count, fuguSearchResult.membersInfos, fuguSearchResult.isGroup, fuguSearchResult.chatType) {

                        })

                    } else {
                        searchResults.add(object : FuguCacheSearchResult(fuguSearchResult.name, fuguSearchResult.user_id, fuguSearchResult.user_image, fuguSearchResult.email, count, fuguSearchResult.membersInfos, fuguSearchResult.isGroup, fuguSearchResult.chatType) {

                        })
                    }
                } else {
                    searchResults.add(object : FuguCacheSearchResult(fuguSearchResult.name, fuguSearchResult.user_id, fuguSearchResult.user_image, fuguSearchResult.email, count, fuguSearchResult.membersInfos, fuguSearchResult.isGroup, fuguSearchResult.chatType) {

                    })
                }
                CommonData.setSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey, searchResults)
                userIdsToBeAdded.add(fuguSearchResult.user_id)
                if (userIds.contains(fuguSearchResult.user_id)) {
                    userIds.remove(fuguSearchResult.user_id)
                } else {
                    userIds.add(fuguSearchResult.user_id)
                }
                searchActivity.setViewOnAdddingMemberToGroup(searchResultList, fuguSearchResult, holder.adapterPosition)
                notifyItemChanged(holder.adapterPosition)
            }
        })


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


    class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var tvName: TextView
        var tvEmail: TextView
        var tvContactIcon: TextView
        var ivContactIcon: ImageView
        var ivTickIcon: ImageView

        init {
            this.tvName = itemView.findViewById(R.id.tvName)
            this.tvEmail = itemView.findViewById(R.id.tvEmail)
            this.tvContactIcon = itemView.findViewById(R.id.tvContactIcon)
            this.ivContactIcon = itemView.findViewById(R.id.ivContactImage)
            this.ivTickIcon = itemView.findViewById(R.id.iv_tick)
        }

    }


}


// data class User(val name: String, val email: String,)
