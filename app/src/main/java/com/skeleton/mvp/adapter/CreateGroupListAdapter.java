package com.skeleton.mvp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.skeleton.mvp.R;
import com.skeleton.mvp.model.GetAllMembers;
import com.skeleton.mvp.ui.creategroup.CreateGroupActivity;
import com.skeleton.mvp.utils.FuguUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rajatdhamija
 * 14/06/18.
 */

public class CreateGroupListAdapter extends RecyclerView.Adapter<CreateGroupListAdapter.MyViewHolder> {

    private HashMap<Long, GetAllMembers> createGroupMap = new HashMap<>();
    private ArrayList<GetAllMembers> searchResultList = new ArrayList<>();
    private Context mContext;
    private CreateGroupActivity createGroupActivity;

    public CreateGroupListAdapter(HashMap<Long, GetAllMembers> createGroupMap, Context mContext) {
        this.createGroupMap = createGroupMap;
        this.mContext = mContext;
        searchResultList = new ArrayList<>(createGroupMap.values());
    }

    public void updateMap(HashMap<Long, GetAllMembers> createGroupMap) {
        this.createGroupMap.clear();
        this.searchResultList.clear();
        this.createGroupMap = createGroupMap;
        this.searchResultList = new ArrayList<>(this.createGroupMap.values());
        createGroupActivity = (CreateGroupActivity) mContext;
    }

    @NonNull
    @Override

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multiadd_group_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final GetAllMembers searchResult = searchResultList.get(position);
        if (!TextUtils.isEmpty(searchResult.getUserThumbnailImage())) {

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.fugu_ic_channel_icon)
                    .error(R.drawable.fugu_ic_channel_icon)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(new CenterCrop(), new RoundedCorners(100));

            Glide.with(mContext)
                    .asBitmap()
                    .apply(options)
                    .load(searchResult.getUserThumbnailImage())
                    .into(holder.ivContactIcon);

//            Glide.with(mContext).load(searchResult.getUserThumbnailImage()).asBitmap()
//                    .centerCrop()
//                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.fugu_ic_channel_icon))
//                    .error(ContextCompat.getDrawable(mContext, R.drawable.fugu_ic_channel_icon))
//                    .into(new BitmapImageViewTarget(holder.ivContactIcon) {
//                        @Override
//                        protected void setResource(Bitmap resource) {
//                            RoundedBitmapDrawable circularBitmapDrawable =
//                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
//                            circularBitmapDrawable.setCircular(true);
//                            holder.ivContactIcon.setImageDrawable(circularBitmapDrawable);
//                        }
//                    });
            holder.tvContactIcon.setVisibility(View.GONE);
        } else {
            holder.tvContactIcon.setVisibility(View.VISIBLE);
            holder.tvContactIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(searchResult.getFullName()));
            if (searchResult.getUserId() % 5 == 1) {
                holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_grey));
            } else if (searchResult.getUserId() % 5 == 2) {
                holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_teal));
            } else if (searchResult.getUserId() % 5 == 3) {
                holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red));
            } else if (searchResult.getUserId() % 5 == 4) {
                holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_indigo));
            } else {
                holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red));
            }
        }
        holder.tvName.setText(searchResult.getFullName().split(" ")[0]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResultList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                createGroupMap.remove(searchResult.getUserId());
                createGroupActivity.updateMap(createGroupMap);
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchResultList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvContactIcon;
        private ImageView ivContactIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivContactIcon = itemView.findViewById(R.id.ivContactImage);
            tvContactIcon = itemView.findViewById(R.id.tvContactIcon);
        }
    }
}
