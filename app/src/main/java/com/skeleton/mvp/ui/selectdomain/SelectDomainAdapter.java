package com.skeleton.mvp.ui.selectdomain;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.Domain;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.ui.setupprofile.SetUpProfileActivity;
import com.skeleton.mvp.activity.CreateWorkspaceActivity;
import com.skeleton.mvp.utils.FuguUtils;

import java.util.ArrayList;

import static com.skeleton.mvp.ui.AppConstants.EXTRA_ALREADY_MEMBER;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class SelectDomainAdapter extends RecyclerView.Adapter<SelectDomainAdapter.MyViewHolder> {

    private ArrayList<Domain> domainsList = new ArrayList<>();
    private Context mContext;
    private SelectDomainActivity selectDomainActivity;
    private String extra;

    public SelectDomainAdapter(ArrayList<Domain> domainsList, Context mContext, String extra) {
        this.domainsList = domainsList;
        this.mContext = mContext;
        selectDomainActivity = (SelectDomainActivity) mContext;
        this.extra = extra;
    }

    @Override
    public SelectDomainAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_domain_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectDomainAdapter.MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final Domain domain = domainsList.get(position);
        holder.tvBusinessName.setText(domain.getBusinessName());
        holder.tvDomainName.setText("https://" + domain.getDomain() + ".fuguchat.com".toLowerCase());
        final FcCommonResponse[] fcCommonResponses = new FcCommonResponse[1];
        if (extra.equals(EXTRA_ALREADY_MEMBER) && position == 0) {
            holder.tvDomainName.setVisibility(View.GONE);
            holder.tvIcon.setText("+");
            holder.tvIcon.setTextSize(40);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(selectDomainActivity, CreateWorkspaceActivity.class);
                    intent.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER);
                    selectDomainActivity.startActivity(intent);
                }
            });
        } else {
            holder.tvDomainName.setVisibility(View.VISIBLE);
            holder.tvIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(domain.getDomain()));
            holder.tvIcon.setTextSize(20);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fcCommonResponses[0] = CommonData.getCommonResponse();
                    for (int i = 0; i < fcCommonResponses[0].getData().getWorkspacesInfo().size(); i++) {
                        if (extra.equals(EXTRA_ALREADY_MEMBER)) {
                            if (i != position - 1) {
                                fcCommonResponses[0].getData().getWorkspacesInfo().get(i).setCurrentLogin(false);
                            } else {
                                fcCommonResponses[0].getData().getWorkspacesInfo().get(i).setCurrentLogin(true);
                            }
                        } else {
                            if (i != position) {
                                fcCommonResponses[0].getData().getWorkspacesInfo().get(i).setCurrentLogin(false);
                            } else {
                                fcCommonResponses[0].getData().getWorkspacesInfo().get(i).setCurrentLogin(true);
                            }
                        }
                    }
                    CommonData.setCommonResponse(fcCommonResponses[0]);
                    for (int j = 0; j < fcCommonResponses[0].getData().getWorkspacesInfo().size(); j++) {
                        com.skeleton.mvp.fugudatabase.CommonData.setFullName(fcCommonResponses[0].getData().getWorkspacesInfo().get(j).getFuguSecretKey(),
                                fcCommonResponses[0].getData().getWorkspacesInfo().get(j).getFullName());
                    }
                    if (extra.equals(EXTRA_ALREADY_MEMBER)) {
                        CommonData.setCurrentSignedInPosition(position - 1);
                    } else {
                        CommonData.setCurrentSignedInPosition(position);
                    }
                    if (TextUtils.isEmpty(fcCommonResponses[0].getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName())) {
                        selectDomainActivity.hideLoading();
                        selectDomainActivity.finishAffinity();
                        selectDomainActivity.startActivity(new Intent(selectDomainActivity, SetUpProfileActivity.class));
                        selectDomainActivity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    } else {
                        selectDomainActivity.hideLoading();
                        selectDomainActivity.finishAffinity();
                        selectDomainActivity.startActivity(new Intent(selectDomainActivity, MainActivity.class));
                        selectDomainActivity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return domainsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBusinessName, tvDomainName;
        private TextView tvIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvBusinessName = itemView.findViewById(R.id.tvBusinessName);
            tvDomainName = itemView.findViewById(R.id.tvDomainName);
            tvIcon = itemView.findViewById(R.id.tvContactIcon);
        }
    }
}
