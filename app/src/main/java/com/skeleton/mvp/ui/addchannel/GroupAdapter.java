package com.skeleton.mvp.ui.addchannel;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.model.Group;
import com.skeleton.mvp.fragment.NotificationBottomSheetFragment;
import com.skeleton.mvp.ui.groupspecific.OldGroupSpecificActivity;

import java.util.ArrayList;


/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

    private ArrayList<Group> groupList = new ArrayList<>();
    private Context mContext;
    OldGroupSpecificActivity oldGroupSpecificActivity;

    public GroupAdapter(ArrayList<Group> groupList, Context mContext) {
        this.groupList = groupList;
        this.mContext = mContext;
        oldGroupSpecificActivity = (OldGroupSpecificActivity) mContext;
    }

    @Override
    public GroupAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final Group group = groupList.get(position);
        if (group.getMuted().equals(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MENTIONS.toString())) {
            holder.tvIsMuted.setText("Mentions Only");
        } else if (group.getMuted().equals(FuguAppConstant.NOTIFICATION_LEVEL.DIRECT_MENTIONS.toString())) {
            holder.tvIsMuted.setText("Direct Mentions Only");
        } else {
            holder.tvIsMuted.setText("All Messages");
        }

        holder.tvGroupName.setText(group.getName());
        if (group.getMuted().equals(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MESSAGES.toString()) || group.getMuted().toLowerCase().equals("unmuted")) {
            holder.ivMuted.setImageResource(R.drawable.notification_unmuted);
        } else {
            holder.ivMuted.setImageResource(R.drawable.notifications_muted);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NotificationBottomSheetFragment newFragment = NotificationBottomSheetFragment.newInstance(0, group.getMuted(), oldGroupSpecificActivity, holder.getAdapterPosition(), group.getChannelId());
                newFragment.show(oldGroupSpecificActivity.getSupportFragmentManager(), "NotificationBottomSheetFragment");


//                CommonParams commonParams;
//                if (StringUtil.toCamelCase(group.getMuted()).equals("Muted")) {
//                    commonParams = new CommonParams.Builder()
//                            .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
//                            .add("unmute_channel_id", group.getChannelId())
//                            .build();
//                    RestClient.getApiInterface().editInfo(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).
//                            enqueue(new ResponseResolver<EditInfoResponse>(oldGroupSpecificActivity, true, false) {
//                                @Override
//                                public void success(EditInfoResponse editInfoResponse) {
//                                    group.setMuted("UNMUTED");
//                                    notifyItemChanged(holder.getAdapterPosition());
//                                }
//
//                                @Override
//                                public void failure(APIError error) {
//
//                                }
//                            });
//                } else {
//                    commonParams = new CommonParams.Builder()
//                            .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
//                            .add("mute_channel_id", group.getChannelId())
//                            .build();
//                    RestClient.getApiInterface().editInfo(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).
//                            enqueue(new ResponseResolver<EditInfoResponse>(oldGroupSpecificActivity, true, false) {
//                                @Override
//                                public void success(EditInfoResponse editInfoResponse) {
//                                    group.setMuted("MUTED");
//                                    notifyItemChanged(holder.getAdapterPosition());
//                                }
//
//                                @Override
//                                public void failure(APIError error) {
//
//                                }
//                            });
//                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void updateList(ArrayList<Group> groupList) {
        this.groupList = groupList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvIsMuted;
        private ImageView ivMuted;

        MyViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvIsMuted = itemView.findViewById(R.id.tvIsMuted);
            ivMuted = itemView.findViewById(R.id.ivMuted);
        }
    }
}
