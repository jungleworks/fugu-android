package com.skeleton.mvp.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.skeleton.mvp.R;
import com.skeleton.mvp.model.GroupContacts;
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity;

import java.util.ArrayList;

public class InviteOnBoardGroupsAdapter extends RecyclerView.Adapter<InviteOnBoardGroupsAdapter.GroupHolder> {
    private ArrayList<GroupContacts> groupContactsArrayList;
    private Context mContext;
    private ArrayList<GroupContacts> selectedGroupArrayList;
    private ArrayList<Long> channelIDArrayList = new ArrayList<>();

    public InviteOnBoardGroupsAdapter(ArrayList<GroupContacts> groupContactsArrayList, Context mContext) {
        this.groupContactsArrayList = groupContactsArrayList;
        this.mContext = mContext;

    }

    public void updateList(ArrayList<Long> channelIDArrayList) {
        this.channelIDArrayList = channelIDArrayList;

    }


    @NonNull
    @Override
    public InviteOnBoardGroupsAdapter.GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite_onboard, parent, false);
        return new InviteOnBoardGroupsAdapter.GroupHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final GroupHolder groupHolder, int position) {
        final GroupContacts groupContacts = groupContactsArrayList.get(position);
        if (!TextUtils.isEmpty(groupContacts.getChannelId())) {
            if (channelIDArrayList.contains(Long.valueOf(groupContacts.getChannelId()))) {
                groupHolder.iv_checked.setVisibility(View.VISIBLE);
            } else {
                groupHolder.iv_checked.setVisibility(View.GONE);
            }
        }

        groupHolder.tvGroupChannelName.setText(groupContacts.getGroupsName());


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.group_purple)
                .error(R.drawable.group_purple)
                .fitCenter()
                .priority(Priority.HIGH);

        Glide.with(mContext)
                .asBitmap()
                .apply(options)
                .load(groupContacts.getChannelImageUrl())
                .into(groupHolder.ivGroupChannelIcon);


//        Glide.with(mContext).load(groupContacts.getChannelImageUrl()).asBitmap()
//                .centerCrop()
//                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.group_purple))
//                .error(ContextCompat.getDrawable(mContext, R.drawable.group_purple))
//                .into(new BitmapImageViewTarget(groupHolder.ivGroupChannelIcon) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        RoundedBitmapDrawable circularBitmapDrawable =
//                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
//                        circularBitmapDrawable.setCircular(true);
//                        groupHolder.ivGroupChannelIcon.setImageDrawable(circularBitmapDrawable);
//                    }
//                });
        selectedGroupArrayList = new ArrayList<>();

        groupHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupHolder.iv_checked.getVisibility() == View.GONE) {
                    groupHolder.iv_checked.setVisibility(View.VISIBLE);
                    channelIDArrayList.add(Long.valueOf(groupContacts.getChannelId()));
                } else {
                    groupHolder.iv_checked.setVisibility(View.GONE);
                    channelIDArrayList.remove(Long.valueOf(groupContacts.getChannelId()));
                }
                selectedGroupArrayList.add(groupContacts);
                ((InviteOnboardActivity) mContext).setRecyclerViewOnAddingGroups(groupContacts);


            }
        });
    }


    @Override
    public int getItemCount() {
        return groupContactsArrayList.size();

    }

    public class GroupHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView ivGroupChannelIcon;
        private TextView tvGroupChannelName;
        private AppCompatImageView iv_checked;

        public GroupHolder(View itemView) {
            super(itemView);
            ivGroupChannelIcon = itemView.findViewById(R.id.ivGroupChannelIcon);
            tvGroupChannelName = itemView.findViewById(R.id.tvGroupChannelName);
            iv_checked = itemView.findViewById(R.id.iv_checked);

        }
    }

}
