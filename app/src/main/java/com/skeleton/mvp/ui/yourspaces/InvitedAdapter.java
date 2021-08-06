package com.skeleton.mvp.ui.yourspaces;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.model.fcCommon.Data;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.Invited;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.model.object.ObjectResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.socket.SocketConnection;
import com.skeleton.mvp.ui.UniqueIMEIID;
import com.skeleton.mvp.ui.browsegroup.BrowseGroupActivity;
import com.skeleton.mvp.utils.FuguUtils;

import org.json.JSONArray;

import java.util.ArrayList;

import static com.skeleton.mvp.constant.FuguAppConstant.ANDROID_USER;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_DETAILS;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_ID;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_TYPE;
import static com.skeleton.mvp.ui.AppConstants.DOMAIN;
import static com.skeleton.mvp.ui.AppConstants.TOKEN;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class InvitedAdapter extends RecyclerView.Adapter<InvitedAdapter.MyViewHolder> {

    private ArrayList<Invited> invitedList = new ArrayList<>();
    private Context mContext;
    private Editable editableOld;
    private BrowseGroupActivity allGroupsFragment;
    private YourSpacesActivity yourSpacesActivity;

    public InvitedAdapter(ArrayList<Invited> invitedList, Context mContext) {
        this.invitedList = invitedList;
        this.mContext = mContext;
        yourSpacesActivity = (YourSpacesActivity) mContext;
    }

    @Override
    public InvitedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invited_spaces, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InvitedAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final Invited invitationToWorkspace = invitedList.get(position);
        holder.tvName.setText(invitationToWorkspace.getWorkspaceName());
        if (position == 0) {
            holder.llRoot.setBackgroundResource(R.drawable.rectangle_border);
        } else {
            holder.llRoot.setBackgroundResource(R.drawable.rectangle_border_open);
        }
        holder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonParams.Builder commonparams = new CommonParams.Builder();
                commonparams.add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(yourSpacesActivity));
                commonparams.add(DEVICE_TYPE, ANDROID_USER);
                commonparams.add(DEVICE_DETAILS, CommonData.deviceDetails(yourSpacesActivity));
                if (TextUtils.isEmpty(com.skeleton.mvp.data.db.CommonData.getFcmToken())) {
                    com.skeleton.mvp.data.db.CommonData.updateFcmToken(FirebaseInstanceId.getInstance().getToken());
//                    FuguNotificationConfig.updateFcmRegistrationToken(com.skeleton.mvp.data.db.CommonData.getFcmToken());
                }
                commonparams.add(TOKEN, com.skeleton.mvp.data.db.CommonData.getFcmToken());
                try {
                    if (!TextUtils.isEmpty(invitationToWorkspace.getInvitationToken())) {
                        commonparams.add("email_token", invitationToWorkspace.getInvitationToken());
                        commonparams.add("invitation_type", "ALREADY_INVITED");
                        FcCommonResponse fcCommonResponse = com.skeleton.mvp.data.db.CommonData.getCommonResponse();
                        invitedList.remove(position);

                    } else {
                        commonparams.add("workspace_id", invitationToWorkspace.getWorkspaceId());
                        commonparams.add("invitation_type", "OPEN_INVITATION");
                        invitedList.remove(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                yourSpacesActivity.showLoading();
                RestClient.getApiInterface(true).joinWorkspace(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                        BuildConfig.VERSION_CODE, ANDROID, commonparams.build().getMap()).enqueue(new ResponseResolver<ObjectResponse>() {
                    @Override
                    public void onSuccess(ObjectResponse joinResponse) {
                        ArrayList<WorkspacesInfo> info = new ArrayList<>();
                        info.addAll(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo());
                        info.add(joinResponse.getData());
                        info.get(info.size() - 1).setWorkspaceName(invitationToWorkspace.getWorkspaceName());
                        FcCommonResponse fcCommonResponse = com.skeleton.mvp.data.db.CommonData.getCommonResponse();
                        fcCommonResponse.getData().setWorkspacesInfo(info);
                        com.skeleton.mvp.data.db.CommonData.setCommonResponse(fcCommonResponse);
                        int currentPosition = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().size() - 1;
                        com.skeleton.mvp.data.db.CommonData.setCurrentSignedInPosition(currentPosition);
//                        com.skeleton.mvp.fugudatabase.CommonData.setAppSecretKey(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
//                        com.skeleton.mvp.fugudatabase.CommonData.setAppSecretKeyOld(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                        Data commonResponseData = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData();
                        SocketConnection.INSTANCE.disconnectSocket();
                        SocketConnection.INSTANCE.initSocketConnection(commonResponseData.getUserInfo().getAccessToken(),
                                commonResponseData.getWorkspacesInfo().get(currentPosition).getEnUserId(), commonResponseData.userInfo.getUserId(),
                                commonResponseData.userInfo.getUserChannel(), "Join WorkSpace", false,
                                commonResponseData.userInfo.getPushToken());
                        CommonParams.Builder commonParams = new CommonParams.Builder();
                        commonParams.add(TOKEN, com.skeleton.mvp.data.db.CommonData.getFcmToken());
                        commonParams.add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(mContext));
                        commonParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
                        ArrayList<Integer> workspaceIds = new ArrayList<>();
                        for (int i = 0; i < com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().size(); i++) {
                            workspaceIds.add(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(i).getWorkspaceId());
                        }
                        JSONArray contactNumbersArray = new JSONArray(workspaceIds);
                        commonParams.add("user_workspace_ids", workspaceIds);
                        commonParams.add("time_zone", FuguUtils.Companion.getTimeZoneOffset());
                        RestClient.getApiInterface(true).accessTokenLogin(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                                .enqueue(new ResponseResolver<FcCommonResponse>() {
                                    @Override
                                    public void onSuccess(FcCommonResponse fcCommonResponse) {
                                        yourSpacesActivity.hideLoading();
                                        com.skeleton.mvp.data.db.CommonData.setCommonResponse(fcCommonResponse);

                                        for (int i = 0; i < fcCommonResponse.data.getWorkspacesInfo().size() - 1; i++) {
                                            if (fcCommonResponse.data.getWorkspacesInfo().get(i).getWorkspaceId().equals(joinResponse.getData().getWorkspaceId())) {
                                                com.skeleton.mvp.data.db.CommonData.setCurrentSignedInPosition(i);
                                                break;
                                            }
                                        }

                                        yourSpacesActivity.initDialog();
                                        yourSpacesActivity.finishAffinity();
                                        yourSpacesActivity.startActivity(new Intent(yourSpacesActivity, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        yourSpacesActivity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                                    }

                                    @Override
                                    public void onError(ApiError error) {
                                        yourSpacesActivity.hideLoading();
                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {
                                        yourSpacesActivity.hideLoading();
                                    }
                                });

                    }

                    @Override
                    public void onError(ApiError error) {
                        yourSpacesActivity.hideLoading();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        yourSpacesActivity.hideLoading();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return invitedList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private LinearLayout llRoot;
        private AppCompatButton btnJoin;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            llRoot = itemView.findViewById(R.id.llRoot);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }
    }
}
