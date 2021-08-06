package com.skeleton.mvp.ui.yourspaces;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.ui.browsegroup.BrowseGroupActivity;

import java.util.ArrayList;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class JoinedAdapter extends RecyclerView.Adapter<JoinedAdapter.MyViewHolder> {

    private ArrayList<WorkspacesInfo> joinedList = new ArrayList<>();
    private Context mContext;
    private Editable editableOld;
    private BrowseGroupActivity allGroupsFragment;
    YourSpacesActivity yourSpacesActivity;

    public JoinedAdapter(ArrayList<WorkspacesInfo> joinedList, Context mContext) {
        this.joinedList = joinedList;
        this.mContext = mContext;
        yourSpacesActivity = (YourSpacesActivity) mContext;

    }

    @Override
    public JoinedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_joined_spaces, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final JoinedAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        WorkspacesInfo workspacesInfo = joinedList.get(position);
        holder.tvName.setText(workspacesInfo.getWorkspaceName());
        if (position == 0) {
            holder.llRoot.setBackgroundResource(R.drawable.rectangle_border);
        } else {
            holder.llRoot.setBackgroundResource(R.drawable.rectangle_border_open);
        }
        holder.btnLaunch.setOnClickListener(v -> {
            CommonData.setCurrentSignedInPosition(position);
            for (int i = 0; i < CommonData.getCommonResponse().getData().getWorkspacesInfo().size(); i++) {
                CommonData.getCommonResponse().getData().getWorkspacesInfo().get(i).setCurrentLogin(false);
            }
            CommonData.getCommonResponse().getData().getWorkspacesInfo().get(position).setCurrentLogin(true);
//                com.skeleton.mvp.fugudatabase.CommonData.setAppSecretKey(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(position).getFuguSecretKey());
            yourSpacesActivity.initDialog();
            yourSpacesActivity.finishAffinity();
            yourSpacesActivity.startActivity(new Intent(yourSpacesActivity, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
    }

    @Override
    public int getItemCount() {
        return joinedList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private LinearLayout llRoot;
        private AppCompatButton btnLaunch;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            llRoot = itemView.findViewById(R.id.llRoot);
            btnLaunch = itemView.findViewById(R.id.btnLaunch);
        }
    }
}
