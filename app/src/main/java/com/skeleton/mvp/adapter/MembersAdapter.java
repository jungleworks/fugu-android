package com.skeleton.mvp.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import com.skeleton.mvp.activity.GroupInformationActivity;
import com.skeleton.mvp.activity.GroupMembersActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.fragment.GroupBottomSheetFragment;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.GroupMember;
import com.skeleton.mvp.model.media.ChatMember;
import com.skeleton.mvp.ui.profile.ProfileActivity;
import com.skeleton.mvp.utils.FuguUtils;

import java.util.ArrayList;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class MembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String PROFILE_INTENT = "profile_intent";
    private ArrayList<GroupMember> membersList = new ArrayList<>();
    private Context mContext;
    private Long channelId;
    private GroupInformationActivity groupInfoActivity = null;
    private GroupMembersActivity membersSearchActivity = null;
    private int chatType;
    private boolean isJoined;
    private boolean click = true;
    private boolean isExpanded = false;
    private ChatMember chatMember;
    private Long userCount = 0L;

    public MembersAdapter(ArrayList<GroupMember> membersList, Context mContext, Long channelId, boolean isJoined, ChatMember chatMember, Boolean isExpanded, Long userCount) {
        this.membersList = membersList;
        this.mContext = mContext;
        this.channelId = channelId;
        chatType = CommonData.getChatType();
        this.isJoined = isJoined;
        this.chatMember = chatMember;
        this.isExpanded = isExpanded;
        this.userCount = userCount;
        if (mContext instanceof GroupInformationActivity) {
            groupInfoActivity = (GroupInformationActivity) mContext;
        } else {
            membersSearchActivity = (GroupMembersActivity) mContext;
        }
    }

    public ArrayList<GroupMember> getItems() {
        return membersList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case 0: {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_group_member, parent, false);
                return new MyViewHolder(itemView);
            }
            case 1: {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_group_expand, parent, false);
                return new ExpandViewHolder(itemView);
            }
            default:
                return new MyViewHolder(itemView);
        }
    }


    public void updateList(ArrayList<GroupMember> membersList) {
        this.membersList = membersList;
    }

    public void updateChatType(int chatType) {
        this.chatType = chatType;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int pos) {

        final int position = holder.getAdapterPosition();
        final GroupMember member = membersList.get(position);

        switch (getItemViewType(position)) {
            case 0: {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                setMemberData(myViewHolder, member);
                break;
            }
            case 1: {
                ExpandViewHolder myViewHolder = (ExpandViewHolder) holder;
                myViewHolder.tvMore.setText(userCount - 10 + " More");
                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        groupInfoActivity.expandList();
                    }
                });
                break;
            }
            default: {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                setMemberData(myViewHolder, member);
            }
        }
    }

    private void setMemberData(MyViewHolder myViewHolder, GroupMember member) {
        if (chatMember != null) {
            if (member.getUserId().compareTo(Long.valueOf(chatMember.getUserId())) == 0) {
                myViewHolder.tvName.setText("You");
            } else {
                myViewHolder.tvName.setText(member.getName());
            }
        } else {
            myViewHolder.tvName.setText(member.getName());
        }

        myViewHolder.tvContactIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(member.getName()));
        if (member.getRole().equals(FuguAppConstant.Role.ADMIN.toString())) {
            myViewHolder.tvAdmin.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.tvAdmin.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(member.getImage())) {
            myViewHolder.tvContactIcon.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(new CenterCrop(), new RoundedCorners(10));
            if (groupInfoActivity != null) {
                Glide.with(groupInfoActivity)
                        .asBitmap()
                        .apply(options)
                        .load(member.getImage())
                        .into(myViewHolder.ivContactIcon);
            } else {
                Glide.with(membersSearchActivity)
                        .asBitmap()
                        .apply(options)
                        .load(member.getImage())
                        .into(myViewHolder.ivContactIcon);
            }
        } else {
            myViewHolder.tvContactIcon.setVisibility(View.VISIBLE);
            if (member.getUserId() % 5 == 1) {
                myViewHolder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_grey));
            } else if (member.getUserId() % 5 == 2) {
                myViewHolder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_teal));
            } else if (member.getUserId() % 5 == 3) {
                myViewHolder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red));
            } else if (member.getUserId() % 5 == 4) {
                myViewHolder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_indigo));
            } else {
                myViewHolder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red));
            }
        }
        myViewHolder.itemView.setOnLongClickListener(view -> {
            try {
                if (groupInfoActivity != null) {
                    if (member.getUserId().compareTo(Long.valueOf(chatMember.getUserId())) != 0) {
                        GroupBottomSheetFragment newFragment = GroupBottomSheetFragment.Companion.newInstance(0, member.getUserId(), member.getRole(),
                                member.getName(), myViewHolder.getAdapterPosition(), chatMember.getRole(), chatType);
                        newFragment.show(groupInfoActivity.getSupportFragmentManager(), "BottomSheetFragment");
                        return true;
                    }
                } else {
                    if (member.getUserId().compareTo(Long.valueOf(chatMember.getUserId())) != 0) {
                        GroupBottomSheetFragment newFragment = GroupBottomSheetFragment.Companion.newInstance(0, member.getUserId(), member.getRole(),
                                member.getName(), myViewHolder.getAdapterPosition(), chatMember.getRole(), chatType);
                        newFragment.show(membersSearchActivity.getSupportFragmentManager(), "BottomSheetFragment");
                        return true;
                    }
                }
            } catch (Exception e) {

            }

            return false;
        });

        if (!isJoined) {
            myViewHolder.itemView.setOnLongClickListener(null);
        }
        myViewHolder.itemView.setOnClickListener(view -> {
            Intent mIntent = new Intent(new Intent(mContext, ProfileActivity.class));
            mIntent.putExtra("open_profile", member.getUserId() + "");
            if (member.getUserId().compareTo(Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getUserId())) == 0) {
                mIntent.putExtra("no_chat", "no_chat");
            }
            mContext.startActivity(mIntent);
        });

        if (myViewHolder.getAdapterPosition() == membersList.size() - 1) {
            myViewHolder.view.setVisibility(View.GONE);
        } else {
            myViewHolder.view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (membersList.size() > 10) {
            if (isExpanded) {
                return membersList.size();
            } else {
                return 11;
            }
        } else {
            return membersList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvContactIcon, tvAdmin;
        private ImageView ivContactIcon, ivContactIcon2;
        private View view;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAdmin = itemView.findViewById(R.id.tvAdmin);
            tvContactIcon = itemView.findViewById(R.id.tvContactIcon);
            ivContactIcon = itemView.findViewById(R.id.ivContactImage);
            ivContactIcon2 = itemView.findViewById(R.id.ivContactImageBlur);
            view = itemView.findViewById(R.id.vLine);
        }

    }

    class ExpandViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMore;

        ExpandViewHolder(View itemView) {
            super(itemView);
            tvMore = itemView.findViewById(R.id.tvMore);
        }
    }

    public interface DecreaseCount {
        void decreaseCount();
    }

    public interface ExpandList {
        void expandList();
    }

    @Override
    public int getItemViewType(int position) {
        return membersList.get(position).getViewtype();
    }
}
