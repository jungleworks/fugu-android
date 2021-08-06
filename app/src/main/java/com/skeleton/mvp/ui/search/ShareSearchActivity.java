package com.skeleton.mvp.ui.search;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.searchgroupuser.Channel;
import com.skeleton.mvp.data.model.searchgroupuser.OpenGroup;
import com.skeleton.mvp.data.model.searchgroupuser.SearchUserResponse;
import com.skeleton.mvp.data.model.searchgroupuser.User;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.FuguCacheSearchResult;
import com.skeleton.mvp.model.FuguSearchResult;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.creategroup.ShareSearchResultAdapter;
import com.skeleton.mvp.ui.intro.IntroActivity;

import java.util.ArrayList;
import java.util.List;

import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.ui.AppConstants.SEARCH_TEXT;

public class ShareSearchActivity extends BaseActivity {
    private EditText etSearchMember;
    private RecyclerView rvSearchResults;
    private ShareSearchResultAdapter searchResultAdapter;
    private FcCommonResponse fcCommonResponse;
    private ArrayList<FuguSearchResult> searchResultList = new ArrayList<>();
    private TextView tvNoResultsFound;
    private ImageView ivBack;
    private ArrayList<FuguCacheSearchResult> cacheSearchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etSearchMember = findViewById(R.id.etSearchMember);
        rvSearchResults = findViewById(R.id.rvSearchresults);
        tvNoResultsFound = findViewById(R.id.tvNoResultsFound);
        ivBack = findViewById(R.id.ivBack);
        etSearchMember.requestFocus();
        fcCommonResponse = CommonData.getCommonResponse();
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        if (com.skeleton.mvp.fugudatabase.CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()) != null) {
            searchResultList.clear();
            searchResultAdapter = new ShareSearchResultAdapter(searchResultList, ShareSearchActivity.this, true);
            rvSearchResults.setAdapter(searchResultAdapter);
        }
        etSearchMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 2) {
                    rvSearchResults.setVisibility(View.VISIBLE);
                    searchResultList.clear();
                    if (isNetworkConnected()) {
                        apiSearchUsers();
                    } else {
                        showErrorMessage(R.string.error_internet_not_connected);
                    }
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void apiSearchUsers() {
        CommonParams commonParams = new CommonParams.Builder()
                .add("en_user_id", fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add(SEARCH_TEXT, etSearchMember.getText().toString())
                .build();
        RestClient.getApiInterface(false).groupChatSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap())
                .enqueue(new ResponseResolver<SearchUserResponse>() {
                    @Override
                    public void onSuccess(SearchUserResponse searchUserResponse) {
                        searchResultList.clear();
                        List<User> user = searchUserResponse.getData().getUsers();
                        List<Channel> channels = searchUserResponse.getData().getChannels();
                        List<OpenGroup> openGroups = searchUserResponse.getData().getOpenGroups();
                        for (int i = 0; i < user.size(); i++) {
                            if (Long.valueOf(user.get(i).getUserId()).compareTo(Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId())) != 0) {
                                searchResultList.add(new FuguSearchResult(user.get(i).getFullName(),
                                        Long.valueOf(user.get(i).getUserId()),
                                        user.get(i).getUserImage(),
                                        user.get(i).getEmail(), false, true,2,false,
                                        user.get(i).getMembersInfo(),""));
                            }
                        }
                        for (int i = 0; i < channels.size(); i++) {
                            if (!TextUtils.isEmpty(channels.get(i).getLabel())) {
                                searchResultList.add(new FuguSearchResult(channels.get(i).getLabel(),
                                        Long.valueOf(channels.get(i).getChannelId()),
                                        channels.get(i).getChannelImage(),
                                        channels.get(i).getMembers(), true, true,4,false,
                                        channels.get(i).getMembersInfo(),""));
                            }
                        }
                        if (searchResultList.size() == 0) {
                            tvNoResultsFound.setVisibility(View.VISIBLE);
                            rvSearchResults.setVisibility(View.GONE);
                        } else {
                            tvNoResultsFound.setVisibility(View.GONE);
                            rvSearchResults.setVisibility(View.VISIBLE);
                        }
                        searchResultAdapter = new ShareSearchResultAdapter(searchResultList, ShareSearchActivity.this, true);
                        rvSearchResults.setAdapter(searchResultAdapter);
                    }

                    @Override
                    public void onError(ApiError error) {
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(ShareSearchActivity.this);
                            finishAffinity();
                            startActivity(new Intent(ShareSearchActivity.this, IntroActivity.class));
                        } else {
                            showErrorMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
