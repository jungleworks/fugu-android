package com.skeleton.mvp.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MembersSearchActivity;
import com.skeleton.mvp.data.db.ChatDatabase;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.model.searchgroupuser.Channel;
import com.skeleton.mvp.data.model.searchgroupuser.OpenGroup;
import com.skeleton.mvp.data.model.searchgroupuser.SearchUserResponse;
import com.skeleton.mvp.data.model.searchgroupuser.User;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.FuguCacheSearchResult;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.model.FuguSearchResult;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.creategroup.CreateGroupSearchResultAdapter;
import com.skeleton.mvp.ui.intro.IntroActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.constant.FuguAppConstant.TEXT_MESSAGE;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_ALREADY_MEMBER;
import static com.skeleton.mvp.ui.AppConstants.SEARCH_TEXT;

public class CreateGroupSearchActivity extends BaseActivity {
    private EditText etSearchMember;
    private RecyclerView rvSearchResults;
    private CreateGroupSearchResultAdapter searchResultAdapter;
    private FcCommonResponse fcCommonResponse;
    private ArrayList<FuguSearchResult> searchResultList = new ArrayList<>();
    private TextView tvNoResultsFound, tvMostSearched;
    private ImageView ivBack;
    private LinearLayout llCreategroup;
    private ArrayList<FuguCacheSearchResult> cacheSearchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etSearchMember = findViewById(R.id.etSearchMember);
        rvSearchResults = findViewById(R.id.rvSearchresults);
        tvNoResultsFound = findViewById(R.id.tvNoResultsFound);
        tvMostSearched = findViewById(R.id.tvMostSearched);
        llCreategroup = findViewById(R.id.llCreateGroup);
        if (getIntent().hasExtra("fab_click") && getCreateGroupRolesList().contains(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getRole())) {
            llCreategroup.setVisibility(View.VISIBLE);
        } else {
            llCreategroup.setVisibility(View.GONE);
        }
        llCreategroup.setOnClickListener(v -> {
            Intent intent2 = (new Intent(CreateGroupSearchActivity.this, MembersSearchActivity.class));
            intent2.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER);
            startActivity(intent2);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
        ivBack = findViewById(R.id.ivBack);
        fcCommonResponse = CommonData.getCommonResponse();
        tvMostSearched.setVisibility(View.GONE);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        searchResultAdapter = new CreateGroupSearchResultAdapter(searchResultList, CreateGroupSearchActivity.this, true);
        rvSearchResults.setAdapter(searchResultAdapter);
        if (com.skeleton.mvp.fugudatabase.CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()) != null) {
            searchResultList.clear();
            ArrayList<FuguCacheSearchResult> searchResult = com.skeleton.mvp.fugudatabase.CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
            if (searchResult.size() > 5) {
                cacheSearchResults = searchResult;
                Collections.sort(cacheSearchResults, (one, other) -> other.getClickCount().compareTo(one.getClickCount()));
                for (int i = 0; i < cacheSearchResults.size(); i++) {
                    searchResultList.add(new FuguSearchResult(cacheSearchResults.get(i).getName(),
                            cacheSearchResults.get(i).getUser_id(),
                            cacheSearchResults.get(i).getUser_image(),
                            cacheSearchResults.get(i).getEmail(),
                            cacheSearchResults.get(i).isGroup(), true, cacheSearchResults.get(i).getChatType(), false, cacheSearchResults.get(i).getMembersInfos(), ""));
                }
                searchResultAdapter.notifyDataSetChanged();
                if (searchResultList.size() > 0) {
                    tvMostSearched.setVisibility(View.VISIBLE);
                }
            } else {
                setConversationList();
            }
        } else {
            setConversationList();
        }
        etSearchMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 3) {
                    if (getIntent().hasExtra("fab_click") && getCreateGroupRolesList().contains(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getRole())) {
                        llCreategroup.setVisibility(View.VISIBLE);
                    } else {
                        llCreategroup.setVisibility(View.GONE);
                    }
                    if (com.skeleton.mvp.fugudatabase.CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()) != null) {
                        searchResultList.clear();
                        ArrayList<FuguCacheSearchResult> searchResult = com.skeleton.mvp.fugudatabase.CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                        if (searchResult.size() > 5) {
                            cacheSearchResults = searchResult;
                            Collections.sort(cacheSearchResults, (one, other) -> other.getClickCount().compareTo(one.getClickCount()));
                            for (int j = 0; j < cacheSearchResults.size(); j++) {
                                searchResultList.add(new FuguSearchResult(cacheSearchResults.get(j).getName(),
                                        cacheSearchResults.get(j).getUser_id(),
                                        cacheSearchResults.get(j).getUser_image(),
                                        cacheSearchResults.get(j).getEmail(),
                                        cacheSearchResults.get(j).isGroup(), true, cacheSearchResults.get(i).getChatType(), false, cacheSearchResults.get(i).getMembersInfos(), ""));

                            }
                            searchResultAdapter.notifyDataSetChanged();
                        } else {
                            setConversationList();
                        }
                        tvNoResultsFound.setVisibility(View.GONE);
                        if (searchResultList.size() > 0) {
                            tvMostSearched.setVisibility(View.VISIBLE);
                        }
                        rvSearchResults.setVisibility(View.VISIBLE);
                    } else {
                        searchResultList.clear();
                        if (com.skeleton.mvp.fugudatabase.CommonData.getConversationList(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()) != null) {
                            List<FuguConversation> conversations = new ArrayList<>(com.skeleton.mvp.fugudatabase.CommonData.getConversationList(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).values());
                            if (conversations.size() > 10) {
                                int count = 0;
                                for (int k = 10; k < conversations.size() && count < 10; k++) {
                                    if (conversations.get(k).getMessage_type() != TEXT_MESSAGE) {
                                        searchResultList.add(new FuguSearchResult(conversations.get(k).getLabel(),
                                                conversations.get(k).getChannelId(),
                                                conversations.get(k).getThumbnailUrl(),
                                                "Attachment", true,
                                                true, 4, false, conversations.get(k).getMembersInfo(), ""));
                                        count++;
                                    } else {
                                        searchResultList.add(new FuguSearchResult(conversations.get(k).getLabel(),
                                                conversations.get(k).getChannelId(),
                                                conversations.get(k).getThumbnailUrl(),
                                                conversations.get(k).getMessage(), true,
                                                true, 4, false, conversations.get(k).getMembersInfo(), ""));
                                        count++;

                                    }
                                }
                                if (searchResultList.size() == 0) {
                                    tvNoResultsFound.setVisibility(View.VISIBLE);
                                    tvMostSearched.setVisibility(View.GONE);
                                    rvSearchResults.setVisibility(View.GONE);
                                } else {
                                    tvNoResultsFound.setVisibility(View.GONE);
                                    rvSearchResults.setVisibility(View.VISIBLE);
                                }
                                searchResultAdapter.updateList(searchResultList);
                                searchResultAdapter.notifyDataSetChanged();
                            }else {
                                int count = 0;
                                for (int k = 0; k < conversations.size() && count < 10; k++) {
                                    if (conversations.get(k).getMessage_type() != TEXT_MESSAGE) {
                                        searchResultList.add(new FuguSearchResult(conversations.get(k).getLabel(),
                                                conversations.get(k).getChannelId(),
                                                conversations.get(k).getThumbnailUrl(),
                                                "Attachment", true,
                                                true, 4, false, conversations.get(k).getMembersInfo(), ""));
                                        count++;
                                    } else {
                                        searchResultList.add(new FuguSearchResult(conversations.get(k).getLabel(),
                                                conversations.get(k).getChannelId(),
                                                conversations.get(k).getThumbnailUrl(),
                                                conversations.get(k).getMessage(), true,
                                                true, 4, false, conversations.get(k).getMembersInfo(), ""));
                                        count++;

                                    }
                                }
                                if (searchResultList.size() == 0) {
                                    tvNoResultsFound.setVisibility(View.VISIBLE);
                                    tvMostSearched.setVisibility(View.GONE);
                                    rvSearchResults.setVisibility(View.GONE);
                                } else {
                                    tvNoResultsFound.setVisibility(View.GONE);
                                    rvSearchResults.setVisibility(View.VISIBLE);
                                }
                                searchResultAdapter.updateList(searchResultList);
                                searchResultAdapter.notifyDataSetChanged();
                            }
                        }
                        if (searchResultAdapter != null) {
                            searchResultAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if (charSequence.length() > 2) {
                    tvMostSearched.setVisibility(View.GONE);
                    rvSearchResults.setVisibility(View.VISIBLE);
                    searchResultList.clear();
                    if (isNetworkConnected()) {
                        llCreategroup.setVisibility(View.GONE);
                        apiSearchUsers();
                    } else {
                        showErrorMessage(R.string.error_internet_not_connected);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ivBack.setOnClickListener(view -> onBackPressed());
    }

    private void setConversationList() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                LinkedHashMap<Long, FuguConversation> conversationMap = new LinkedHashMap<>();
                conversationMap = ChatDatabase.INSTANCE.getConversationMap(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                ArrayList<FuguConversation> conversations = new ArrayList<>(conversationMap.values());
                Collections.sort(conversations, (one, other) -> other.getDateTime().compareTo(one.getDateTime()));
                if (conversations.size() > 10) {
                    int count = 0;
                    for (int k = 10; k < conversations.size() && count < 10; k++) {
                        if (conversations.get(k).getMessage_type() != TEXT_MESSAGE) {
                            searchResultList.add(new FuguSearchResult(conversations.get(k).getLabel(),
                                    conversations.get(k).getChannelId(),
                                    conversations.get(k).getThumbnailUrl(),
                                    "Attachment", true,
                                    true, conversations.get(k).getChat_type(), false,
                                    conversations.get(k).getMembersInfo(), ""));
                            count++;
                        } else {
                            searchResultList.add(new FuguSearchResult(conversations.get(k).getLabel(),
                                    conversations.get(k).getChannelId(),
                                    conversations.get(k).getThumbnailUrl(),
                                    conversations.get(k).getMessage(), true,
                                    true, conversations.get(k).getChat_type(), false,
                                    conversations.get(k).getMembersInfo(), ""));
                            count++;

                        }
                    }
                    runOnUiThread(() -> {
                        if (searchResultList.size() == 0) {
                            tvNoResultsFound.setVisibility(View.VISIBLE);
                            tvMostSearched.setVisibility(View.GONE);
                            rvSearchResults.setVisibility(View.GONE);
                        } else {
                            tvNoResultsFound.setVisibility(View.GONE);
                            rvSearchResults.setVisibility(View.VISIBLE);
                        }
                        searchResultAdapter.notifyDataSetChanged();
                    });
                }else {
                    int count = 0;
                    for (int k = 0; k < conversations.size() && count < 10; k++) {
                        if (conversations.get(k).getMessage_type() != TEXT_MESSAGE) {
                            searchResultList.add(new FuguSearchResult(conversations.get(k).getLabel(),
                                    conversations.get(k).getChannelId(),
                                    conversations.get(k).getThumbnailUrl(),
                                    "Attachment", true,
                                    true, conversations.get(k).getChat_type(), false,
                                    conversations.get(k).getMembersInfo(), ""));
                            count++;
                        } else {
                            searchResultList.add(new FuguSearchResult(conversations.get(k).getLabel(),
                                    conversations.get(k).getChannelId(),
                                    conversations.get(k).getThumbnailUrl(),
                                    conversations.get(k).getMessage(), true,
                                    true, conversations.get(k).getChat_type(), false,
                                    conversations.get(k).getMembersInfo(), ""));
                            count++;

                        }
                    }
                    runOnUiThread(() -> {
                        if (searchResultList.size() == 0) {
                            tvNoResultsFound.setVisibility(View.VISIBLE);
                            tvMostSearched.setVisibility(View.GONE);
                            rvSearchResults.setVisibility(View.GONE);
                        } else {
                            tvNoResultsFound.setVisibility(View.GONE);
                            rvSearchResults.setVisibility(View.VISIBLE);
                        }
                        searchResultAdapter.notifyDataSetChanged();
                    });
                }
            }
        }.start();
    }

    private void apiSearchUsers() {
        CommonParams commonParams = new CommonParams.Builder()
                .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
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
                                        user.get(i).getEmail(), false, true, 2, false,
                                        user.get(i).getMembersInfo(), ""));
                            }
                        }
                        for (int i = 0; i < channels.size(); i++) {
                            if (!TextUtils.isEmpty(channels.get(i).getLabel())) {
                                searchResultList.add(new FuguSearchResult(channels.get(i).getLabel(),
                                        Long.valueOf(channels.get(i).getChannelId()),
                                        channels.get(i).getChannelImage(),
                                        channels.get(i).getMembers(), true, true, 4, false,
                                        channels.get(i).getMembersInfo(), ""));
                            }
                        }
                        for (int i = 0; i < openGroups.size(); i++) {
                            if (!TextUtils.isEmpty(openGroups.get(i).getLabel())) {
                                searchResultList.add(new FuguSearchResult(openGroups.get(i).getLabel(),
                                        Long.valueOf(openGroups.get(i).getChannelId()),
                                        openGroups.get(i).getChannelImage(),
                                        "Public Group", true, false, 4, false,
                                        openGroups.get(i).getMembersInfo(), ""));
                            }
                        }
                        if (searchResultList.size() == 0) {
                            tvNoResultsFound.setVisibility(View.VISIBLE);
                            tvMostSearched.setVisibility(View.GONE);
                            rvSearchResults.setVisibility(View.GONE);
                        } else {
                            tvNoResultsFound.setVisibility(View.GONE);
                            rvSearchResults.setVisibility(View.VISIBLE);
                        }
                        searchResultAdapter = new CreateGroupSearchResultAdapter(searchResultList, CreateGroupSearchActivity.this, true);
                        rvSearchResults.setAdapter(searchResultAdapter);
                    }

                    @Override
                    public void onError(ApiError error) {
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(CreateGroupSearchActivity.this);
                            finishAffinity();
                            startActivity(new Intent(CreateGroupSearchActivity.this, IntroActivity.class));
                        } else {
                            showErrorMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

    private ArrayList<String> getCreateGroupRolesList() {
        WorkspacesInfo workspacesInfo = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition());
        String roles = workspacesInfo.getConfig().getEnableCreateGroup();
        roles = roles.replace("[", "");
        roles = roles.replace("]", "");
        roles = roles.replaceAll("\"", "");
        String[] rolesArray = roles.split(",");
        return new ArrayList<>(Arrays.asList(rolesArray));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        View view = CreateGroupSearchActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
