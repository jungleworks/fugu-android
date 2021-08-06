package com.skeleton.mvp.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.GroupInfoActivity;
import com.skeleton.mvp.model.Media;

import java.util.ArrayList;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder> {
    private ArrayList<Media> mediaList = new ArrayList<>();
    private Context mContext;
    GroupInfoActivity groupInfoActivity;

    public MediaAdapter(ArrayList<Media> mediaList, Context mContext) {
        this.mediaList = mediaList;
        this.mContext = mContext;
        groupInfoActivity = (GroupInfoActivity) mContext;
    }

    @Override
    public MediaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MediaAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final Media media = mediaList.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .fitCenter()
                .priority(Priority.HIGH);

        Glide.with(groupInfoActivity)
                .asBitmap()
                .apply(options)
                .load(media.getThumbnailUrl())
                .into(holder.ivImage);

//        Glide.with(groupInfoActivity).load(media.getThumbnailUrl()).asBitmap()
//                .centerCrop()
//                .placeholder(ContextCompat.getDrawable(groupInfoActivity, R.drawable.placeholder))
//                .error(ContextCompat.getDrawable(groupInfoActivity, R.drawable.placeholder))
//                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;

        MyViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}
