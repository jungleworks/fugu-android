package com.skeleton.mvp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.AlreadyInvitedMembersActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.InvitedMembers;
import com.skeleton.mvp.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE_ID;

public class InvitedMembersAdapter extends RecyclerView.Adapter<InvitedMembersAdapter.MyViewHolder> {

    private ArrayList<InvitedMembers> invitedMembersArrayList;
    private Activity activity;

    public InvitedMembersAdapter(ArrayList<InvitedMembers> invitedMembersArrayList, Activity activity) {
        this.invitedMembersArrayList = invitedMembersArrayList;
        this.activity = activity;

    }

    public void updateList(ArrayList<InvitedMembers> invitedMembersArrayList) {
        this.invitedMembersArrayList = invitedMembersArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_email, parent, false);
        return new InvitedMembersAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final InvitedMembers invitedMembers = invitedMembersArrayList.get(position);
        holder.tvEmail.setText(invitedMembers.getData());
        if (invitedMembers.isEmail()) {
            holder.ivEmail.setImageResource(R.drawable.ic_email_blue_24dp);
        } else {
            holder.ivEmail.setImageResource(R.drawable.ic_call_blue_24dp);
        }
        if (invitedMembers.getIsExpired().equals("EXPIRED")) {
            holder.tvTime.setText("Accepted: " + DateUtils.getDate(DateUtils.getInstance().convertToLocal(invitedMembers.getDateTime())));
            holder.ivArrow.setVisibility(View.GONE);
        } else if (invitedMembers.getIsExpired().equals("RE_INVITED")) {
            holder.tvTime.setText("Resent: " + DateUtils.getDate(DateUtils.getInstance().convertToLocal(invitedMembers.getDateTime())));
            holder.ivArrow.setVisibility(View.VISIBLE);
        } else if (invitedMembers.getIsExpired().equals("NOT_EXPIRED")) {
            holder.tvTime.setText("Invited: " + DateUtils.getDate(DateUtils.getInstance().convertToLocal(invitedMembers.getDateTime())));
            holder.ivArrow.setVisibility(View.VISIBLE);
        } else {
            holder.ivArrow.setVisibility(View.GONE);
        }

        if (invitedMembers.isExpanded()) {
            holder.llInviteManipulation.setVisibility(View.VISIBLE);
            holder.ivArrow.setRotation(180);
        } else {
            holder.llInviteManipulation.setVisibility(View.GONE);
            holder.ivArrow.setRotation(0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (invitedMembers.getIsExpired().equals("NOT_EXPIRED") || invitedMembers.getIsExpired().equals("RE_INVITED")) {
                    if (holder.llInviteManipulation.getVisibility() == View.VISIBLE) {
                        holder.llInviteManipulation.setVisibility(View.GONE);
                        holder.ivArrow.setRotation(0);
                        invitedMembers.setExpanded(false);
                    } else {
                        holder.llInviteManipulation.setVisibility(View.VISIBLE);
                        holder.ivArrow.setRotation(180);
                        invitedMembers.setExpanded(true);
                    }
                } else {

                }
            }
        });
        holder.tvRevoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AlreadyInvitedMembersActivity) activity).isNetworkConnected()) {
                    ((AlreadyInvitedMembersActivity) activity).showLoading();
                    apiRevokeInvitation(invitedMembersArrayList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                } else {
                    ((AlreadyInvitedMembersActivity) activity).showErrorMessage(R.string.fugu_not_connected_to_internet);
                }
            }
        });
        holder.tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AlreadyInvitedMembersActivity) activity).isNetworkConnected()) {
                    ((AlreadyInvitedMembersActivity) activity).showLoading();
                    apiResendInvitation(invitedMembersArrayList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                } else {
                    ((AlreadyInvitedMembersActivity) activity).showErrorMessage(R.string.fugu_not_connected_to_internet);
                }
            }
        });
    }

    private void apiResendInvitation(final InvitedMembers invitedMembers, final int pos) {
        final CommonParams.Builder commonParams = new CommonParams.Builder();

        commonParams.add(WORKSPACE_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        if (invitedMembers.isEmail()) {
            commonParams.add(EMAIL, invitedMembers.getData());
        } else {
            JSONObject contactObject = new JSONObject();
            try {
                contactObject.put("contact_number", invitedMembers.getData());
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(activity);
                contactObject.put("country_code", phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(invitedMembers.getData().split("-")[0])));
                commonParams.add("contact_info", contactObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RestClient.getApiInterface(true).resendInvitation(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap()).enqueue(new ResponseResolver<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse commonResponse) {
                String localDate = DateUtils.getFormattedDate(new Date());
                invitedMembers.setIsExpired("RE_INVITED");
                invitedMembers.setExpanded(false);
                invitedMembers.setDateTime(DateUtils.getInstance().convertToUTC(localDate));
                invitedMembersArrayList.set(pos, invitedMembers);
                notifyItemChanged(pos);
                ((AlreadyInvitedMembersActivity) activity).hideLoading();
                Toast.makeText(activity, commonResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ApiError error) {
                ((AlreadyInvitedMembersActivity) activity).hideLoading();
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                ((AlreadyInvitedMembersActivity) activity).hideLoading();

            }
        });
    }

    private void apiRevokeInvitation(final InvitedMembers invitedMembers, final int pos) {
        final CommonParams.Builder commonParams = new CommonParams.Builder();

        commonParams.add(WORKSPACE_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        if (invitedMembers.isEmail()) {
            commonParams.add(EMAIL, invitedMembers.getData());
        } else {
            JSONObject contactObject = new JSONObject();
            try {
                contactObject.put("contact_number", invitedMembers.getData());
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(activity);
                contactObject.put("country_code", phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(invitedMembers.getData().split("-")[0])));
                commonParams.add("contact_info", contactObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        RestClient.getApiInterface(true).revokeInvitation(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap()).enqueue(new ResponseResolver<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse commonResponse) {
                invitedMembersArrayList.remove(invitedMembers);
                notifyItemRemoved(pos);
                ((AlreadyInvitedMembersActivity) activity).hideLoading();
                Toast.makeText(activity, commonResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ApiError error) {
                ((AlreadyInvitedMembersActivity) activity).hideLoading();
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                ((AlreadyInvitedMembersActivity) activity).hideLoading();

            }
        });
    }

    @Override
    public int getItemCount() {
        return invitedMembersArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView ivEmail;
        private AppCompatTextView tvEmail;
        private AppCompatTextView tvTime;
        private LinearLayout llInviteManipulation;
        private ImageView ivArrow;
        private TextView tvRevoke, tvResend;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivEmail = itemView.findViewById(R.id.ivEmail);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvTime = itemView.findViewById(R.id.tvTime);
            llInviteManipulation = itemView.findViewById(R.id.llInviteManipulation);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            tvResend = itemView.findViewById(R.id.tvResend);
            tvRevoke = itemView.findViewById(R.id.tvRevoke);
        }
    }
}
