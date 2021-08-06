package com.skeleton.mvp.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.adapter.GoogleInvitesAdapter;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.invitation.InvitationResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.inviteContacts.GetUserContactsResponse;
import com.skeleton.mvp.model.sendinvitesgoogle.SendInvitesGoogle;
import com.skeleton.mvp.payment.CalculatePaymentActivity;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity;
import com.skeleton.mvp.ui.intro.IntroActivity;
import com.skeleton.mvp.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.EMAILS;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_ALREADY_MEMBER;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE_ID;

public class SendInvitesActivity extends BaseActivity {

    private List<SendInvitesGoogle> googleContactsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GoogleInvitesAdapter googleInvitesAdapter;
    private AppCompatButton btn_send_invites;
    private TextView tvSelectAll, tvSkip;
    private TextView tvNoContactMsg;
    private ImageView ivBack;
    private EditText etSearch;
    private CardView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invites);

        recyclerView = findViewById(R.id.recycler_view);
        btn_send_invites = findViewById(R.id.btn_send_invites);
        tvSelectAll = findViewById(R.id.tv_select_all);
        tvSkip = findViewById(R.id.tvSkip);
        tvNoContactMsg = findViewById(R.id.tv_no_contact_msg);
        ivBack = findViewById(R.id.ivBack);
        etSearch = findViewById(R.id.etSearch);
        search = findViewById(R.id.search);
        googleInvitesAdapter = new GoogleInvitesAdapter(googleContactsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(googleInvitesAdapter);
        apiGetUserContacts("ALL");

        if (getIntent().hasExtra("isFromOnboardActivity")) {
            tvSkip.setVisibility(View.GONE);
            ivBack.setVisibility(View.VISIBLE);
        } else {
            tvSkip.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.GONE);
        }

        ivBack.setOnClickListener(v -> onBackPressed());

        tvSkip.setOnClickListener(v -> startActivity(new Intent(SendInvitesActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));

        btn_send_invites.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                if (googleInvitesAdapter.getSelectedEmails().size() == 0) {
                    showErrorMessage("You need to select atleast one contact to send invite !");
                } else {
                    showLoading();
                    apiInviteMember((ArrayList<String>) googleInvitesAdapter.getSelectedEmails());
                }

            } else {
                showErrorMessage("Not Connected To Internet");
            }
        });
        tvSelectAll.setClickable(true);
        tvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvSelectAll.getText().toString().equals(getResources().getString(R.string.select_all_string))) {
                    List<String> tempList = new ArrayList<>();
                    for (int i = 0; i < googleContactsList.size(); i++) {
                        tempList.add(googleContactsList.get(i).getEmailId());
                    }
                    googleInvitesAdapter.onSelectAll(tempList);
                    tvSelectAll.setText(getString(R.string.unselected_all));
                    tvSelectAll.setPaintFlags(tvSelectAll.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else if (tvSelectAll.getText().toString().equals(getResources().getString(R.string.unselected_all))) {
                    googleInvitesAdapter.onUnselectAll();
                    tvSelectAll.setText(R.string.select_all_string);
                    tvSelectAll.setPaintFlags(tvSelectAll.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }

            }
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<SendInvitesGoogle> filteredList = new ArrayList<>();
                if (s.length() > 0) {
                    for (int i = 0; i < googleContactsList.size(); i++) {
                        if (googleContactsList.get(i).getEmailId().toLowerCase().contains(s.toString().toLowerCase())) {
                            filteredList.add(googleContactsList.get(i));
                        }
                    }

                    googleInvitesAdapter.updateList(filteredList);
                    googleInvitesAdapter.notifyDataSetChanged();
                } else {
                    googleInvitesAdapter.updateList(googleContactsList);
                    googleInvitesAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void apiGetUserContacts(String contact_type) {
        showLoading();
        CommonParams commonParams = new CommonParams.Builder()
                .add("workspace_id", CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId())
                .add("contact_type", contact_type)
                .build();
        RestClient.getApiInterface(true).getUserContacts((CommonData.getCommonResponse().getData().getUserInfo().getAccessToken()), BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap())
                .enqueue(new ResponseResolver<GetUserContactsResponse>() {
                    @Override
                    public void onSuccess(final GetUserContactsResponse getUserContactsResponse) {
                        googleContactsList = new ArrayList<>();
                        for (int j = 0; j < getUserContactsResponse.getData().getGoogleContacts().size(); j++) {
                            googleContactsList.add(new SendInvitesGoogle(getUserContactsResponse.getData()
                                    .getGoogleContacts().get(j), false));
                        }
                        googleInvitesAdapter.updateList(googleContactsList);
                        googleInvitesAdapter.notifyDataSetChanged();
                        if (googleContactsList.size() == 0) {
                            if (!getIntent().hasExtra("isFromOnboardActivity")) {
                                Intent intent = new Intent(SendInvitesActivity.this, InviteOnboardActivity.class);
                                intent.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER);
                                startActivity(intent);
                                finish();
                            }
                            search.setVisibility(View.GONE);
                            tvNoContactMsg.setVisibility(View.VISIBLE);
                            tvSelectAll.setVisibility(View.GONE);
                            btn_send_invites.setVisibility(View.GONE);
                        } else {
                            search.setVisibility(View.VISIBLE);
                            tvNoContactMsg.setVisibility(View.GONE);
                            tvSelectAll.setVisibility(View.VISIBLE);
                            btn_send_invites.setVisibility(View.VISIBLE);
                        }
                        hideLoading();
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

    private void apiInviteMember(ArrayList<String> emails) {
        JSONArray emailJsonArray = new JSONArray(emails);

        FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
        CommonParams.Builder commonParams = new CommonParams.Builder();
        if (emails.size() > 0) {
            commonParams.add(EMAILS, emailJsonArray);
        }

        commonParams.add(WORKSPACE_ID, fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        Log.e("map", commonParams.build().getMap().toString());
        RestClient.getApiInterface(true).inviteUsers(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<InvitationResponse>() {
                    @Override
                    public void onSuccess(InvitationResponse invitationResponse) {
                        hideLoading();

                        showErrorMessage("Invitation sent successfully", () -> {
                            finishAffinity();
                            Intent intent = new Intent(SendInvitesActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(SendInvitesActivity.this);
                            finishAffinity();
                            startActivity(new Intent(SendInvitesActivity.this, IntroActivity.class));
                        } else if(error.getStatusCode() == 402) {
                            startActivity(new Intent(SendInvitesActivity.this, CalculatePaymentActivity.class));
                            finish();
                        } else {
                            showErrorMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                    }
                });
    }

}
