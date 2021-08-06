package com.skeleton.mvp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.adapter.SearchListAdapter;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.ChatDatabase;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.searchgroupuser.Channel;
import com.skeleton.mvp.data.model.searchgroupuser.SearchUserResponse;
import com.skeleton.mvp.data.model.searchgroupuser.User;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.model.Message;
import com.skeleton.mvp.model.SearchList;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.intro.IntroActivity;
import com.skeleton.mvp.util.SearchAnimationToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.constant.FuguAppConstant.TEXT_MESSAGE;
import static com.skeleton.mvp.ui.AppConstants.SEARCH_TEXT;

public class ForwardActivity extends BaseActivity implements SearchAnimationToolbar.OnSearchQueryChangedListener {
    private SearchAnimationToolbar toolbar;
    private RecyclerView rvList;
    private SearchListAdapter listAdapter;
    private ArrayList<SearchList> searchList = new ArrayList<>();
    private FcCommonResponse fcCommonResponse;
    private TextView tvNoResultsFound;
    private Message message;
    private int chatType = FuguAppConstant.ChatType.PUBLIC_GROUP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);

        initViews();
        setupRecycler();
        setConversationList();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvList = findViewById(R.id.rvList);
        tvNoResultsFound = findViewById(R.id.tvNoResultsFound);
        toolbar.setSupportActionBar(this);
        toolbar.setOnSearchQueryChangedListener(this);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fcCommonResponse = CommonData.getCommonResponse();
    }

    private void setupRecycler() {
        rvList.setLayoutManager(new LinearLayoutManager(this));
        message = (Message) getIntent().getSerializableExtra("MESSAGE");
        listAdapter = new SearchListAdapter(searchList, this, message);
        rvList.setAdapter(listAdapter);
    }

    @Override
    public void onSearchCollapsed() {

    }

    @Override
    public void onSearchQueryChanged(String query) {
        if (query.length() > 2) {
            apiSearchUsers(query);
        } else {
            setConversationList();
        }
    }

    @Override
    public void onSearchExpanded() {

    }

    @Override
    public void onSearchSubmitted(String query) {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_search) {
            toolbar.onSearchIconClick();
            return true;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        boolean handledByToolbar = toolbar.onBackPressed();

        if (!handledByToolbar) {
            super.onBackPressed();
        }
    }

    private void setConversationList() {
        searchList.clear();
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (ChatDatabase.INSTANCE.getConversationMap(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey()) != null) {
                    LinkedHashMap<Long, FuguConversation> conversationMap = new LinkedHashMap<>();
                    conversationMap = ChatDatabase.INSTANCE.getConversationMap(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                    ArrayList<FuguConversation> conversations = new ArrayList<>(conversationMap.values());
                    Collections.sort(conversations, new Comparator<FuguConversation>() {
                        public int compare(FuguConversation one, FuguConversation other) {
                            return other.getDateTime().compareTo(one.getDateTime());
                        }
                    });
                    int count = 0;
                    for (int k = 0; k < conversations.size() && count < 15; k++) {
                        boolean isGroup = true;
                        if(conversations.get(k).getChat_type() == 2){
                            isGroup = false;
                        }
                        if (conversations.get(k).getMessage_type() != TEXT_MESSAGE) {
                            searchList.add(new SearchList(conversations.get(k).getLabel(),
                                    conversations.get(k).getChannelId(),
                                    conversations.get(k).getThumbnailUrl(),
                                    "Attachment", isGroup,
                                    true, conversations.get(k).getMembersInfo(),false));
                            count++;
                        } else {
                            searchList.add(new SearchList(conversations.get(k).getLabel(),
                                    conversations.get(k).getChannelId(),
                                    conversations.get(k).getThumbnailUrl(),
                                    conversations.get(k).getMessage(), isGroup,
                                    true, conversations.get(k).getMembersInfo(),false));
                            count++;

                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }.start();
    }

    private void apiSearchUsers(String query) {
        CommonParams commonParams = new CommonParams.Builder()
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add(SEARCH_TEXT, query)
                .build();
        RestClient.getApiInterface(false).groupChatSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap())
                .enqueue(new ResponseResolver<SearchUserResponse>() {
                    @Override
                    public void onSuccess(SearchUserResponse searchUserResponse) {
                        searchList.clear();
                        List<User> user = searchUserResponse.getData().getUsers();
                        List<Channel> channels = searchUserResponse.getData().getChannels();
                        boolean isGroup = true;

                        for (int i = 0; i < user.size(); i++) {

                            if (Long.valueOf(user.get(i).getUserId()).compareTo(Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getUserId())) != 0) {
                                searchList.add(new SearchList(user.get(i).getFullName(),
                                        Long.valueOf(user.get(i).getUserId()),
                                        user.get(i).getUserImage(),
                                        user.get(i).getEmail(), false, true, user.get(i).getMembersInfo(),true));
                            }
                        }
                        for (int i = 0; i < channels.size(); i++) {
                            if (!TextUtils.isEmpty(channels.get(i).getLabel())) {
                                searchList.add(new SearchList(channels.get(i).getLabel(),
                                        Long.valueOf(channels.get(i).getChannelId()),
                                        channels.get(i).getChannelImage(),
                                        channels.get(i).getMembers(), true, true, channels.get(i).getMembersInfo(),true));
                            }
                        }
                        if (searchList.size() == 0) {
                            tvNoResultsFound.setVisibility(View.VISIBLE);
                            rvList.setVisibility(View.GONE);
                        } else {
                            tvNoResultsFound.setVisibility(View.GONE);
                            rvList.setVisibility(View.VISIBLE);
                        }
                        listAdapter = new SearchListAdapter(searchList, ForwardActivity.this, message);
                        rvList.setAdapter(listAdapter);
                    }

                    @Override
                    public void onError(ApiError error) {
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(ForwardActivity.this);
                            finishAffinity();
                            startActivity(new Intent(ForwardActivity.this, IntroActivity.class));
                        } else {
                            showErrorMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }
}
