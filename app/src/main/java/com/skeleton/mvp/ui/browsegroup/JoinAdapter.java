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

public class JoinAdapter extends RecyclerView.Adapter<JoinAdapter.MyViewHolder> {

    private ArrayList<AllGroup> allGroupsList = new ArrayList<>();
    private ArrayList<AllGroup> joinGroupList = new ArrayList<>();
    private Context mContext;
    private Editable editableOld;
    private BrowseGroupActivity browseGroupActivity;

    public JoinAdapter(ArrayList<AllGroup> joinGroupList, Context mContext, ArrayList<AllGroup> allGroupsList) {
        this.allGroupsList = allGroupsList;
        this.joinGroupList = joinGroupList;
        this.mContext = mContext;
        browseGroupActivity = (BrowseGroupActivity) mContext;
    }

    @Override
    public JoinAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_all_groups, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final JoinAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final AllGroup allGroup = joinGroupList.get(position);
        holder.tvGroupName.setText(allGroup.getGroupName());
        if (allGroup.getChatType() == 5||allGroup.getChatType() == 6) {
            holder.tvJoin.setVisibility(View.GONE);
        } else {
            holder.tvJoin.setVisibility(View.VISIBLE);
            if (!allGroup.isJoined()) {
                holder.tvJoin.setText("JOIN");
                holder.tvJoin.setTextColor(browseGroupActivity.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.tvJoin.setText("LEAVE");
                holder.tvJoin.setTextColor(browseGroupActivity.getResources().getColor(R.color.gray_dark));
            }
        }
        if (allGroup.getChatType() == 3) {
            holder.ivPrivate.setVisibility(View.VISIBLE);
        } else {
            holder.ivPrivate.setVisibility(View.GONE);
        }
        holder.tvJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseGroupActivity.showLoading();
                final CommonParams commonParams = new CommonParams.Builder()
                        .add("en_user_id", CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                        .add("channel_id", allGroup.getChannelId())
                        .build();
                new AlertDialog.Builder(mContext)
                        .setMessage("Are you sure, you want to leave this group?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                browseGroupActivity.hideLoading();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RestClient.getApiInterface(false).leaveGroup(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap())
                                        .enqueue(new ResponseResolver<CommonResponseFugu>() {
                                            @Override
                                            public void onSuccess(CommonResponseFugu commonResponseFugu) {
                                                browseGroupActivity.hideLoading();
                                                final int deletedPosition = position;
                                                for (int i = 0; i < allGroupsList.size(); i++) {
                                                    if (allGroup.getChannelId().compareTo(allGroupsList.get(i).getChannelId()) == 0) {
                                                        if (allGroup.getChatType() == 4) {
                                                            allGroupsList.get(i).setJoined(false);
                                                        } else {
                                                            allGroupsList.remove(i);
                                                        }
                                                        CommonData.setAllGroupResult(allGroupsList);
                                                    }
                                                }
                                                joinGroupList.remove(position);
                                                notifyDataSetChanged();
                                                CommonData.setJoinGroupResult(joinGroupList);
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
                                                                    browseGroupActivity.showLoading();
                                                                    RestClient.getApiInterface(false).joinGroup(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap())
                                                                            .enqueue(new ResponseResolver<CommonResponseFugu>() {
                                                                                @Override
                                                                                public void onSuccess(CommonResponseFugu commonResponseFugu) {
                                                                                    browseGroupActivity.hideLoading();
                                                                                    joinGroupList.add(deletedPosition, allGroup);
                                                                                    notifyDataSetChanged();
                                                                                    CommonData.setJoinGroupResult(joinGroupList);
                                                                                    for (int i = 0; i < allGroupsList.size(); i++) {
                                                                                        if (allGroup.getChannelId().compareTo(allGroupsList.get(i).getChannelId()) == 0) {
                                                                                            allGroupsList.get(i).setJoined(true);
                                                                                            CommonData.setAllGroupResult(allGroupsList);
                                                                                        }
                                                                                    }

                                                                                }

                                                                                @Override
                                                                                public void onError(ApiError error) {
                                                                                    browseGroupActivity.hideLoading();
                                                                                }

                                                                                @Override
                                                                                public void onFailure(Throwable throwable) {
                                                                                    browseGroupActivity.hideLoading();
                                                                                }
                                                                            });
                                                                }
                                                            });

                                                    snackbar.show();
                                                }
                                            }

                                            @Override
                                            public void onError(ApiError error) {
                                                browseGroupActivity.hideLoading();
                                            }

                                            @Override
                                            public void onFailure(Throwable throwable) {
                                                browseGroupActivity.hideLoading();
                                            }
                                        });
                            }
                        })
                        .show();

            }
        });
        if (position == joinGroupList.size() - 1) {
            holder.llRoot.setPadding(0, 0, 0, 200);
        } else {
            holder.llRoot.setPadding(0, 0, 0, 0);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(browseGroupActivity, ChatActivity.class);
                FuguConversation conversation = new FuguConversation();
                conversation.setBusinessName(allGroup.getGroupName());
                conversation.setOpenChat(true);
                conversation.setChannelId(allGroup.getChannelId());
                conversation.setUserName(StringUtil.toCamelCase(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName()));
                conversation.setUserId(Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId()));
                conversation.setEnUserId(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId());
                conversation.setChat_type(allGroup.getChatType());
                conversation.setJoined(allGroup.isJoined());
                conversation.setLabel(allGroup.getGroupName());
                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                browseGroupActivity.startActivityForResult(chatIntent, REQUEST_JOIN_GROUP);
            }
        });
    }

    @Override
    public int getItemCount() {
        return joinGroupList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvJoin;
        private ImageView ivPrivate;
        private LinearLayout llRoot;

        MyViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvJoin = itemView.findViewById(R.id.tvJoin);
            ivPrivate = itemView.findViewById(R.id.ivPrivate);
            llRoot = itemView.findViewById(R.id.llRoot);
        }
    }
}
