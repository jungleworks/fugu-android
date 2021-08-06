package com.skeleton.mvp.ui.browsegroup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.AllGroup;
import com.skeleton.mvp.data.model.setPassword.CommonResponseFugu;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.utils.StringUtil;

import java.util.ArrayList;

import static com.skeleton.mvp.ui.AppConstants.REQUEST_JOIN_GROUP;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class AllGroupsAdapter extends RecyclerView.Adapter<AllGroupsAdapter.MyViewHolder> {

    private ArrayList<AllGroup> allGroupsList = new ArrayList<>();
    private ArrayList<AllGroup> joinGroupList = new ArrayList<>();
    private Context mContext;
    private Editable editableOld;
    private BrowseGroupActivity allGroupsFragment;

    public AllGroupsAdapter(ArrayList<AllGroup> allGroupsList, Context mContext, ArrayList<AllGroup> joinGroupList) {
        this.allGroupsList = allGroupsList;
        this.joinGroupList = joinGroupList;
        this.mContext = mContext;
        allGroupsFragment = (BrowseGroupActivity) mContext;
    }

    @Override
    public AllGroupsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_all_groups, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AllGroupsAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final AllGroup allGroup = allGroupsList.get(position);
        holder.tvGroupName.setText(allGroup.getGroupName());

        if (allGroup.getChatType() == 5 || allGroup.getChatType() == 6) {
            holder.tvJoin.setVisibility(View.GONE);
        } else {
            holder.tvJoin.setVisibility(View.VISIBLE);
            if (!allGroup.isJoined()) {
                holder.tvJoin.setText("JOIN");
                holder.tvJoin.setTextColor(allGroupsFragment.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.tvJoin.setText("LEAVE");
                holder.tvJoin.setTextColor(allGroupsFragment.getResources().getColor(R.color.gray_dark));
            }
        }
        if (allGroup.getChatType() == 3) {
            holder.ivPrivate.setVisibility(View.VISIBLE);
        } else {
            holder.ivPrivate.setVisibility(View.GONE);
        }
        if (position == allGroupsList.size() - 1) {
            holder.llRoot.setPadding(0, 0, 0, 200);
        } else {
            holder.llRoot.setPadding(0, 0, 0, 00);
        }
        holder.tvJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CommonParams commonParams = new CommonParams.Builder()
                        .add("en_user_id", CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                        .add("channel_id", allGroup.getChannelId())
                        .build();
                if (!allGroup.isJoined()) {
                    allGroupsFragment.showLoading();
                    apiJoinGroup(commonParams, allGroup, position);
                } else {
                    new AlertDialog.Builder(mContext)
                            .setMessage("Are you sure, you want to leave this group?")
                            .setPositiveButton("No", null)
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    allGroupsFragment.showLoading();
                                    apiLeaveGroup(commonParams, allGroup, position, holder);
                                }
                            })
                            .show();

                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(allGroupsFragment, ChatActivity.class);
                FuguConversation conversation = new FuguConversation();
                conversation.setBusinessName(allGroup.getGroupName());
                conversation.setOpenChat(true);
                conversation.setChannelId(allGroup.getChannelId());
                conversation.setUserName(StringUtil.toCamelCase(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName()));
                conversation.setUserId(Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId()));
                conversation.setEnUserId(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName());
                conversation.setChat_type(allGroup.getChatType());
                conversation.setJoined(allGroup.isJoined());
                conversation.setLabel(allGroup.getGroupName());
                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                allGroupsFragment.startActivityForResult(chatIntent, REQUEST_JOIN_GROUP);
            }
        });
    }

    private void apiLeaveGroup(CommonParams commonParams, final AllGroup allGroup, final int position, final AllGroupsAdapter.MyViewHolder holder) {
        RestClient.getApiInterface(false).leaveGroup(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap())
                .enqueue(new ResponseResolver<CommonResponseFugu>() {
                    @Override
                    public void onSuccess(CommonResponseFugu commonResponseFugu) {
                        allGroupsFragment.hideLoading();
                        allGroup.setJoined(false);
                        notifyDataSetChanged();
                        CommonData.setAllGroupResult(allGroupsList);
                        for (int i = 0; i < joinGroupList.size(); i++) {
                            if (allGroup.getChannelId().compareTo(joinGroupList.get(i).getChannelId()) == 0) {
                                joinGroupList.remove(i);
                                CommonData.setJoinGroupResult(joinGroupList);
                            }
                        }
                        if (allGroup.getChatType() == 3) {
                            allGroupsList.remove(position);
                            CommonData.setAllGroupResult(allGroupsList);
                        }
                        if (allGroup.getChatType() != 3) {
                            Snackbar snackbar = Snackbar
                                    .make(holder.itemView, allGroup.getGroupName() + " Left", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CommonParams commonParams = new CommonParams.Builder()
                                                    .add("en_user_id", CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                                                    .add("channel_id", allGroup.getChannelId())
                                                    .build();
                                            allGroupsFragment.showLoading();
                                            apiJoinGroup(commonParams, allGroup, position);
                                        }
                                    });

                            snackbar.show();
                        }
                    }

                    @Override
                    public void onError(ApiError error) {
                        allGroupsFragment.hideLoading();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        allGroupsFragment.hideLoading();
                    }
                });
    }

    private void apiJoinGroup(CommonParams commonParams, final AllGroup allGroup, final int position) {
        RestClient.getApiInterface(false).joinGroup(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap())
                .enqueue(new ResponseResolver<CommonResponseFugu>() {
                    @Override
                    public void onSuccess(CommonResponseFugu commonResponseFugu) {
                        allGroupsFragment.hideLoading();
                        allGroup.setJoined(true);
                        notifyDataSetChanged();
                        CommonData.setAllGroupResult(allGroupsList);
                        joinGroupList.add(allGroup);
                        CommonData.setJoinGroupResult(joinGroupList);
                    }

                    @Override
                    public void onError(ApiError error) {
                        allGroupsFragment.hideLoading();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        allGroupsFragment.hideLoading();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return allGroupsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvJoin;
        private LinearLayout llRoot;
        private ImageView ivPrivate;

        MyViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvJoin = itemView.findViewById(R.id.tvJoin);
            llRoot = itemView.findViewById(R.id.llRoot);
            ivPrivate = itemView.findViewById(R.id.ivPrivate);
        }
    }
}
