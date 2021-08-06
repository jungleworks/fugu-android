package com.skeleton.mvp.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.skeleton.mvp.R;
import com.skeleton.mvp.model.GroupContacts;
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InviteOnBoardGroupsSelectedAdapter extends RecyclerView.Adapter<InviteOnBoardGroupsSelectedAdapter.MyViewHolder> {
    private ArrayList<GroupContacts> selectedGroupArrayList;
    private Context mContext;
    private LinkedHashMap<Long, GroupContacts> groupMap;

    public InviteOnBoardGroupsSelectedAdapter(LinkedHashMap<Long, GroupContacts> groupMap, ArrayList<GroupContacts> selectedGroupArrayList, Context mContext) {
        this.mContext = mContext;
        for (Long key : groupMap.keySet()) {
            selectedGroupArrayList.add(groupMap.get(key));
        }
        this.selectedGroupArrayList = selectedGroupArrayList;


    }

    public void updatelist(LinkedHashMap<Long, GroupContacts> groupMap) {
        this.groupMap = groupMap;
        selectedGroupArrayList = new ArrayList<>(groupMap.values());
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiadd_group_item, parent, false);
       return new InviteOnBoardGroupsSelectedAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final InviteOnBoardGroupsSelectedAdapter.MyViewHolder holder , int position) {
        final int pos = holder.getAdapterPosition();
        final GroupContacts groupContacts = selectedGroupArrayList.get(pos);
            holder.tvGroupChannelName.setText(groupContacts.getGroupsName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.group_purple)
                .error(R.drawable.group_purple)
                .fitCenter()
                .priority(Priority.HIGH)
                .transforms(new CenterCrop(), new RoundedCorners(10));

        Glide.with(mContext)
                .asBitmap()
                .apply(options)
                .load(groupContacts.getChannelImageUrl())
                .into(holder.ivGroupChannelIcon);

//                Glide.with(mContext).load(groupContacts.getChannelImageUrl()).asBitmap()
//                        .centerCrop()
//                        .placeholder(ContextCompat.getDrawable(mContext, R.drawable.group_purple))
//                        .error(ContextCompat.getDrawable(mContext, R.drawable.group_purple))
//                        .into(new BitmapImageViewTarget(holder.ivGroupChannelIcon) {
//                            @Override
//                            protected void setResource(Bitmap resource) {
//                                RoundedBitmapDrawable circularBitmapDrawable =
//                                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
//                                circularBitmapDrawable.setCircular(true);
//                                holder.ivGroupChannelIcon.setImageDrawable(circularBitmapDrawable);
//                            }
//                        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long channelId = Long.valueOf(selectedGroupArrayList.get(holder.getAdapterPosition()).getChannelId());
                selectedGroupArrayList.remove(holder.getAdapterPosition());
                groupMap.remove(channelId);
                updatelist(groupMap);
                notifyItemRemoved(holder.getAdapterPosition());

                ((InviteOnboardActivity) mContext).updateSelectedGroupAdapter(selectedGroupArrayList,groupMap);


            }
        });


    }

    @Override
    public int getItemCount() {
        return selectedGroupArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView ivGroupChannelIcon;
        private TextView tvGroupChannelName;
        private ImageView ivCross;


        public MyViewHolder(View itemView) {
            super(itemView);
            ivGroupChannelIcon = itemView.findViewById(R.id.ivContactImage);
            tvGroupChannelName = itemView.findViewById(R.id.tvName);
            ivCross = itemView.findViewById(R.id.iv_delete);
        }


    }




}
