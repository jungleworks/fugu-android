package com.skeleton.mvp.ui.groupspecific;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.Group;
import com.skeleton.mvp.model.editInfo.EditInfoResponse;
import com.skeleton.mvp.model.getChannelInfo.GetChannelInfoResponse;
import com.skeleton.mvp.retrofit.APIError;
import com.skeleton.mvp.retrofit.CommonParams;
import com.skeleton.mvp.retrofit.ResponseResolver;
import com.skeleton.mvp.retrofit.RestClient;
import com.skeleton.mvp.ui.addchannel.GroupAdapter;
import com.skeleton.mvp.ui.base.BaseActivity;

import java.util.ArrayList;

import static com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;


public class OldGroupSpecificActivity extends BaseActivity {
    private RecyclerView rvGroupList;
    private ArrayList<Group> groupList = new ArrayList<>();
    private ArrayList<Group> filteredGroupList = new ArrayList<>();
    private GroupAdapter groupAdapter;
    private EditText etSearch;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_group_specific);
        rvGroupList = findViewById(R.id.rvGroupList);
        etSearch = findViewById(R.id.etSearch);
        ivBack = findViewById(R.id.ivBack);
        apiGetChannelsInfo();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    filteredGroupList = getFilteredList(editable.toString());
                    groupAdapter = new GroupAdapter(filteredGroupList, OldGroupSpecificActivity.this);
                    rvGroupList.setLayoutManager(new LinearLayoutManager(OldGroupSpecificActivity.this));
                    rvGroupList.setAdapter(groupAdapter);
                } else {
                    groupAdapter = new GroupAdapter(groupList, OldGroupSpecificActivity.this);
                    rvGroupList.setLayoutManager(new LinearLayoutManager(OldGroupSpecificActivity.this));
                    rvGroupList.setAdapter(groupAdapter);
                }
            }
        });
    }

    /**
     *
     */
    private void apiGetChannelsInfo() {
        CommonParams commonParams = new CommonParams.Builder()
                .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                .build();
        RestClient.getApiInterface().getUserChannelInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<GetChannelInfoResponse>() {
            @Override
            public void success(GetChannelInfoResponse getChannelInfoResponse) {
                for (int i = 0; i < getChannelInfoResponse.getData().size(); i++) {
                    if (!TextUtils.isEmpty(getChannelInfoResponse.getData().get(i).getLabel())) {
                        groupList.add(new Group(getChannelInfoResponse.getData().get(i).getLabel(),
                                getChannelInfoResponse.getData().get(i).getNotification(),
                                getChannelInfoResponse.getData().get(i).getChannelId()));
                    }
                    groupAdapter = new GroupAdapter(groupList, OldGroupSpecificActivity.this);
                    rvGroupList.setLayoutManager(new LinearLayoutManager(OldGroupSpecificActivity.this));
                    rvGroupList.setAdapter(groupAdapter);
                }
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }

    private ArrayList<Group> getFilteredList(String text) {
        ArrayList<Group> newfilteredGroupList = new ArrayList<>();
        newfilteredGroupList.clear();
        for (int i = 0; i < groupList.size(); i++) {
            if (groupList.get(i).getName().toLowerCase().contains(text.toLowerCase())) {
                newfilteredGroupList.add(groupList.get(i));
            }
        }
        return newfilteredGroupList;
    }

    public void updateNotificationSettings(String notificationLevelUpdated, int pos, Long channelId) {

        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(FuguAppConstant.EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId());
        commonParams.add(CHANNEL_ID, channelId);
        commonParams.add("notification", notificationLevelUpdated);
        showLoading();
        RestClient.getApiInterface().editInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.build().getMap())
                .enqueue(new ResponseResolver<EditInfoResponse>() {
                    @Override
                    public void success(EditInfoResponse editInfoResponse) {
                        hideLoading();
                        String text = "";
                        groupList.get(pos).setMuted(notificationLevelUpdated);
                        groupAdapter.updateList(groupList);
                        groupAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(APIError error) {
                        hideLoading();
                    }
                });
    }
}
