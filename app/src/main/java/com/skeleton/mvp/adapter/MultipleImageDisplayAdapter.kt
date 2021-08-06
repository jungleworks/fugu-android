package com.skeleton.mvp.adapter

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.easyfilepicker.filter.entity.ImageFile
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MultipleImageDisplayActivity
import com.skeleton.mvp.photoEditor.EmojiBSFragment
import com.skeleton.mvp.photoEditor.PropertiesBSFragment
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.ViewType
import java.io.File

class MultipleImageDisplayAdapter(imageList: ArrayList<ImageFile>) : androidx.recyclerview.widget.RecyclerView.Adapter<MultipleImageDisplayAdapter.MyViewHolder>() {
    private var imageList: ArrayList<ImageFile>

    init {
        this.imageList = imageList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleImageDisplayAdapter.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.multiple_image_display_item, parent, false)
        return MultipleImageDisplayAdapter.MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: MultipleImageDisplayAdapter.MyViewHolder, p1: Int) {
//        holder.setIsRecyclable(false)
        Glide.with(holder.itemView.context)
                .load(imageList[holder.adapterPosition].path)
                .into(holder.ivImage)
    }

    class MyViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {
        var ivImage: AppCompatImageView = itemView!!.findViewById(R.id.ivImage)

    }

    private fun getUriFromPath(path: String, context: Context): Uri {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", File(path))
    }
}