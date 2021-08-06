package com.skeleton.mvp.ui.yourspaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.CreateWorkspaceActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.model.fcCommon.Data;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.InvitationToWorkspace;
import com.skeleton.mvp.data.model.fcCommon.Invited;
import com.skeleton.mvp.data.model.fcCommon.OpenWorkspacesToJoin;
import com.skeleton.mvp.data.model.fcCommon.UserInfo;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.model.openandinvited.OpenAndInvited;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

import static com.skeleton.mvp.constant.FuguAppConstant.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_ALREADY_MEMBER;

public class YourSpacesActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rvJoined, rvInvited;
    private ArrayList<WorkspacesInfo> joinedList = new ArrayList<>();
    private ArrayList<Invited> invitedList = new ArrayList<>();
    private JoinedAdapter joinedAdapter;
    private InvitedAdapter invitedAdapter;
    private int invitedCount = 0;
    private TextView tvJoinText, tvJoinSubText, tvJoinedMore, tvInvitedMore, tvCreate, tvYourSpacesText;
    private ImageView ivBack;
    private View separator;
    private AppCompatButton btnCreateNew;
    private LinearLayout llCreateNew;
    ArrayList<InvitationToWorkspace> invitationToWorkspace = new ArrayList<>();
    ArrayList<OpenWorkspacesToJoin> openWorkspacesToJoin = new ArrayList<>();
    private LinearLayout llMain;
    private LinearLayoutCompat llNoWorkspacesToShow;
    private ScrollView svYourWorkspaces;
    private Boolean isCreateWorkspaceAllowed = false;
    UserInfo.WhitelabelProperties whitelabelProperties = CommonData.getCommonResponse().data.userInfo.getWhitelabelProperties();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_spaces);
        rvJoined = findViewById(R.id.rvJoined);
        rvInvited = findViewById(R.id.rvInvited);
        tvJoinText = findViewById(R.id.tvJoinAnotherText);
        tvJoinSubText = findViewById(R.id.tvJoinSubText);
        tvJoinedMore = findViewById(R.id.tvJoinedMore);
        tvInvitedMore = findViewById(R.id.tvInvitedMore);
        tvYourSpacesText = findViewById(R.id.tvYourSpacesText);
        btnCreateNew = findViewById(R.id.btnCreateNew);
        llCreateNew = findViewById(R.id.llCreateNew);
        llMain = findViewById(R.id.llMain);
        llNoWorkspacesToShow = findViewById(R.id.llNoWorkspacesToShow);
        svYourWorkspaces = findViewById(R.id.svYourWorkspaces);

        ivBack = findViewById(R.id.ivBack);
        tvCreate = findViewById(R.id.tvCreate);
        separator = findViewById(R.id.seperator);
        tvJoinedMore.setOnClickListener(this);
        tvInvitedMore.setOnClickListener(this);
        tvCreate.setOnClickListener(this);

        readCreateWorkspacePermission();

        if (getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
            ivBack.setVisibility(View.VISIBLE);
            ivBack.setOnClickListener(this);
            tvYourSpacesText.setVisibility(View.GONE);
            rvJoined.setVisibility(View.GONE);
            llCreateNew.setVisibility(View.GONE);
            btnCreateNew.setVisibility(View.VISIBLE);
            btnCreateNew.setOnClickListener(this);
        } else {
            ivBack.setVisibility(View.GONE);
            ivBack.setOnClickListener(null);
            tvYourSpacesText.setVisibility(View.VISIBLE);
            rvJoined.setVisibility(View.VISIBLE);
            llCreateNew.setVisibility(View.VISIBLE);
            btnCreateNew.setVisibility(View.GONE);
            btnCreateNew.setOnClickListener(null);
        }

        if (getIntent().hasExtra("API_HIT")) {
            llMain.setVisibility(View.GONE);
            showLoading();
            apiGetOpenAndInvited(new YourWorkspacesSuccess() {
                @Override
                public void success(ArrayList<InvitationToWorkspace> invitationToWorkspaces, ArrayList<OpenWorkspacesToJoin> openWorkspacesToJoins) {
                    invitationToWorkspace = invitationToWorkspaces;
                    openWorkspacesToJoin = openWorkspacesToJoins;
                    setViews(invitationToWorkspaces, openWorkspacesToJoins);
                    hideLoading();
                    llMain.setVisibility(View.VISIBLE);
                }
            });
        } else {
            invitationToWorkspace = (ArrayList<InvitationToWorkspace>) CommonData.getCommonResponse().getData().getInvitationToWorkspaces();
            openWorkspacesToJoin = (ArrayList<OpenWorkspacesToJoin>) CommonData.getCommonResponse().getData().getOpenWorkspacesToJoin();
            setViews(invitationToWorkspace, openWorkspacesToJoin);
            if (joinedList.size() == 0 && invitationToWorkspace.size() == 0 && openWorkspacesToJoin.size() == 0) {
                if (isCreateWorkspaceAllowed)
                    startActivity(new Intent(YourSpacesActivity.this, CreateWorkspaceActivity.class));
                else
                    Toast.makeText(YourSpacesActivity.this, "You don't have any pending invitations.", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isCreateWorkspaceAllowed) {
            llCreateNew.setVisibility(View.GONE);
            btnCreateNew.setVisibility(View.GONE);
        }
    }

    private void readCreateWorkspacePermission() {
        if (whitelabelProperties == null || whitelabelProperties.isCreateWorkspaceEnabled()) {
            int joinedSpacesCount = CommonData.getCommonResponse().getData().getWorkspacesInfo().size();
            if (joinedSpacesCount != 0) {
                WorkspacesInfo workspaceInfo = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition());
                String roles = workspaceInfo.getConfig().getCreateWorkspacePermisson();
                roles = roles.replace("[", "");
                roles = roles.replace("]", "");
                roles = roles.replaceAll("\"", "");
                String[] rolesArray = roles.split(",");
                ArrayList<String> rolesList = new ArrayList<>(Arrays.asList(rolesArray));
                String presentRole = workspaceInfo.getRole();
                if (rolesList.contains(presentRole)) {
                    isCreateWorkspaceAllowed = true;
                }
            } else if (getIntent().getBooleanExtra("isCreateWorkspaceAllowed", false)) {
                isCreateWorkspaceAllowed = true;
            }
        }
    }

    private void setViews(ArrayList<InvitationToWorkspace> invitationToWorkspaces, ArrayList<OpenWorkspacesToJoin> openWorkspacesToJoins) {
        int totalInvitedSpacesCount = invitationToWorkspaces.size() + openWorkspacesToJoins.size();
        isCreateWorkspaceAllowed = whitelabelProperties != null && whitelabelProperties.isCreateWorkspaceEnabled();
        if (getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
            if (totalInvitedSpacesCount == 0) {
                finish();
                if (isCreateWorkspaceAllowed) {
                    Intent intent = new Intent(YourSpacesActivity.this, CreateWorkspaceActivity.class);
                    if (getIntent().hasExtra("isGoogleSignIn")) {
                        intent.putExtra("isGoogleSignIn", getIntent().getBooleanExtra("isGoogleSignIn", false));
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                } else
                    Toast.makeText(YourSpacesActivity.this, "You don't have any pending invitations.", Toast.LENGTH_SHORT).show();
            }
        }
        int joinedSpacesCount = CommonData.getCommonResponse().getData().getWorkspacesInfo().size();
        if (joinedSpacesCount > 0) {
            for (int i = 0; i < joinedSpacesCount && i < 3; i++) {
                joinedList.add(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(i));
            }
        }
        for (int i = 0; i < invitationToWorkspaces.size() && invitedCount < 3; i++) {
            invitedCount++;
            invitedList.add(new Invited(invitationToWorkspaces.get(i).getWorkspaceId(),
                    invitationToWorkspaces.get(i).getEmail(),
                    invitationToWorkspaces.get(i).getFuguSecretKey(),
                    "",
                    invitationToWorkspaces.get(i).getWorkspace(),
                    invitationToWorkspaces.get(i).getWorkspaceName(),
                    invitationToWorkspaces.get(i).getInvitationToken()));
        }
        for (int i = 0; i < openWorkspacesToJoins.size() && invitedCount < 3; i++) {
            invitedCount++;
            invitedList.add(new Invited(openWorkspacesToJoins.get(i).getWorkspaceId(),
                    openWorkspacesToJoins.get(i).getEmailDomain(),
                    openWorkspacesToJoins.get(i).getFuguSecretKey(),
                    openWorkspacesToJoins.get(i).getStatus(),
                    openWorkspacesToJoins.get(i).getWorkspace(),
                    openWorkspacesToJoins.get(i).getWorkspaceName(),
                    ""));
        }

        if (CommonData.getCommonResponse().getData().getWorkspacesInfo().size() > 3) {
            if (!getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
                tvJoinedMore.setVisibility(View.VISIBLE);
            }
            if (CommonData.getCommonResponse().getData().getWorkspacesInfo().size() == 4) {
                tvJoinedMore.setText("show 1 more space");
            } else {
                tvJoinedMore.setText("show " + (CommonData.getCommonResponse().getData().getWorkspacesInfo().size() - 3) + " more spaces");
            }
        } else {
            tvJoinedMore.setVisibility(View.GONE);
        }

        if (totalInvitedSpacesCount > 3) {
            tvInvitedMore.setVisibility(View.VISIBLE);
            if (totalInvitedSpacesCount == 4) {
                tvInvitedMore.setText("show 1 more space");
            } else {
                tvInvitedMore.setText("show " + (totalInvitedSpacesCount - 3) + " more spaces");
            }
        } else {
            tvInvitedMore.setVisibility(View.GONE);
        }

        if (!isCreateWorkspaceAllowed && joinedList.isEmpty() && invitedList.isEmpty()) {
            llNoWorkspacesToShow.setVisibility(View.VISIBLE);
            svYourWorkspaces.setVisibility(View.GONE);
        } else {
            llNoWorkspacesToShow.setVisibility(View.GONE);
            svYourWorkspaces.setVisibility(View.VISIBLE);
            if (joinedList.isEmpty()) {
                tvYourSpacesText.setVisibility(View.GONE);
                rvJoined.setVisibility(View.GONE);
            } else {
                joinedAdapter = new JoinedAdapter(joinedList, this);
                rvJoined.setLayoutManager(new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                rvJoined.setAdapter(joinedAdapter);
            }
            if (invitedList.isEmpty()) {
                tvJoinText.setVisibility(View.GONE);
                tvJoinSubText.setVisibility(View.GONE);
                separator.setVisibility(View.GONE);
            } else {
                tvJoinText.setVisibility(View.VISIBLE);
                tvJoinSubText.setVisibility(View.VISIBLE);
                if (getIntent().hasExtra(EXTRA_ALREADY_MEMBER) || joinedSpacesCount == 0) {
                    separator.setVisibility(View.GONE);
                } else {
                    separator.setVisibility(View.VISIBLE);
                }
                invitedAdapter = new InvitedAdapter(invitedList, this);
                rvInvited.setLayoutManager(new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                rvInvited.setAdapter(invitedAdapter);
            }
            if (isCreateWorkspaceAllowed && joinedList.isEmpty() && invitedList.isEmpty()) {
                tvYourSpacesText.setVisibility(View.VISIBLE);
                tvYourSpacesText.setText("You're not part of any workspace.");
                if (getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
                    btnCreateNew.setVisibility(View.VISIBLE);
                } else {
                    llCreateNew.setVisibility(View.VISIBLE);
                }
            } else {
                llCreateNew.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvJoinedMore:
                joinedList.clear();
                joinedList.addAll(CommonData.getCommonResponse().getData().getWorkspacesInfo());
                joinedAdapter.notifyDataSetChanged();
                tvJoinedMore.setVisibility(View.GONE);
                break;
            case R.id.tvInvitedMore:
                invitedList.clear();
                for (int i = 0; i < invitationToWorkspace.size(); i++) {
                    invitedCount++;
                    invitedList.add(new Invited(invitationToWorkspace.get(i).getWorkspaceId(),
                            invitationToWorkspace.get(i).getEmail(),
                            invitationToWorkspace.get(i).getFuguSecretKey(),
                            "",
                            invitationToWorkspace.get(i).getWorkspace(),
                            invitationToWorkspace.get(i).getWorkspaceName(),
                            invitationToWorkspace.get(i).getInvitationToken()));
                }
                for (int i = 0; i < openWorkspacesToJoin.size(); i++) {
                    invitedCount++;
                    invitedList.add(new Invited(openWorkspacesToJoin.get(i).getWorkspaceId(),
                            openWorkspacesToJoin.get(i).getEmailDomain(),
                            openWorkspacesToJoin.get(i).getFuguSecretKey(),
                            openWorkspacesToJoin.get(i).getStatus(),
                            openWorkspacesToJoin.get(i).getWorkspace(),
                            openWorkspacesToJoin.get(i).getWorkspaceName(),
                            ""));
                }
                invitedAdapter.notifyDataSetChanged();
                tvInvitedMore.setVisibility(View.GONE);
                break;
            case R.id.tvCreate:
            case R.id.btnCreateNew:
                Intent intent = new Intent(YourSpacesActivity.this, CreateWorkspaceActivity.class);
                if (getIntent().hasExtra("isGoogleSignIn")) {
                    intent.putExtra("isGoogleSignIn", getIntent().getBooleanExtra("isGoogleSignIn", false));
                }
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    public class CustomLinearLayoutManager extends LinearLayoutManager {
        public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);

        }

        // it will always pass false to RecyclerView when calling "canScrollVertically()" method.
        @Override
        public boolean canScrollVertically() {
            return true;
        }
    }

    private void apiGetOpenAndInvited(final YourWorkspacesSuccess yourSpacesSuccess) {
        RestClient.getApiInterface(true).getOpenAndInvited(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID)
                .enqueue(new ResponseResolver<OpenAndInvited>() {
                    @Override
                    public void onSuccess(final OpenAndInvited openAndInvited) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
                                fcCommonResponse.data.setInvitationToWorkspaces(openAndInvited.getData().getInvitationToWorkspaces());
                                fcCommonResponse.data.setOpenWorkspacesToJoin(openAndInvited.getData().getOpenWorkspacesToJoin());
                                UserInfo.WhitelabelProperties whitelabelProperties = openAndInvited.getData().getWhitelabelProperties();
                                if (whitelabelProperties != null) {
                                    YourSpacesActivity.this.whitelabelProperties = whitelabelProperties;
                                    UserInfo userInfo = CommonData.getCommonResponse().getData().userInfo;
                                    userInfo.setWhitelabelProperties(openAndInvited.getData().getWhitelabelProperties());
                                    fcCommonResponse.data.setUserInfo(userInfo);
                                }
                                CommonData.setCommonResponse(fcCommonResponse);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                yourSpacesSuccess.success((ArrayList<InvitationToWorkspace>) openAndInvited.getData().getInvitationToWorkspaces(), (ArrayList<OpenWorkspacesToJoin>) openAndInvited.getData().getOpenWorkspacesToJoin());
                                            }
                                        }, 1000);
                                    }
                                });

                            }
                        }.start();


                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                    }
                });
    }

    interface YourWorkspacesSuccess {
        void success(ArrayList<InvitationToWorkspace> invitationToWorkspaces, ArrayList<OpenWorkspacesToJoin> openWorkspacesToJoins);
    }
}
