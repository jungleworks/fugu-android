package com.skeleton.mvp.adapter

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.easyfilepicker.filter.entity.ImageFile
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MultipleImageDisplayActivity

class SmallImageDisplayAdapter(imageList: ArrayList<ImageFile>, context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<SmallImageDisplayAdapter.MyViewHolder>() {

    private var imageList: ArrayList<ImageFile>
    private var context: Context
    private var currentSelectedItemPosition = 0

    init {
        this.imageList = imageList
        this.context = context
    }

    fun updateCurrentSelectedItemPosition(position: Int) {
        currentSelectedItemPosition = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallImageDisplayAdapter.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.small_image_display_item, parent, false)
        return SmallImageDisplayAdapter.MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: SmallImageDisplayAdapter.MyViewHolder, p1: Int) {
        Glide.with(holder.itemView.context)
                .load(imageList[holder.adapterPosition].path)
                .into(holder.ivImage)
        if (holder.adapterPosition == currentSelectedItemPosition) {
            holder.ivImage.setBackgroundResource(R.drawable.image_border)
        } else {
            holder.ivImage.setBackgroundResource(R.drawable.image_border_transparent)
        }


        holder.itemView.setOnClickListener {
            currentSelectedItemPosition = holder.adapterPosition
            (context as MultipleImageDisplayActivity).onItemClicked(holder.adapterPosition)
        }

    }

    class MyViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {
        var ivImage: AppCompatImageView = itemView!!.findViewById(R.id.ivImage)

    }

    interface OnItemClicked {
        fun onItemClicked(position: Int)
    }

}