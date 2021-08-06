package com.skeleton.mvp.ui.browsegroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MembersSearchActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.AllGroup;
import com.skeleton.mvp.data.model.allgroups.AllGroupsResponse;
import com.skeleton.mvp.data.model.allgroups.JoinedChannel;
import com.skeleton.mvp.data.model.allgroups.OpenChannel;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.skeleton.mvp.ui.AppConstants.REQUEST_JOIN_GROUP;

public class BrowseGroupActivity extends BaseActivity {
    private static final int REQUEST_CREATE_GROUP = 1223;
    private ArrayList<AllGroup> allGroupsList = new ArrayList<>();
    private ArrayList<AllGroup> filteredAllGroupsList = new ArrayList<>();
    private ArrayList<AllGroup> filteredJoinedGroupsList = new ArrayList<>();
    private ArrayList<AllGroup> joinedGroupList = new ArrayList<>();
    private ViewPager viewPager;
    private ImageView ivBack;
    private EditText etSearch;
    private AppCompatImageView ivSearch;
    private AllGroupsFragment allGroupsFragment;
    private JoinedGroupsFragment joinedGroupsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_group);
        viewPager = findViewById(R.id.viewpager);
        ivBack = findViewById(R.id.ivBack);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        etSearch = findViewById(R.id.etSearch);
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setVisibility(View.VISIBLE);
                ScaleAnimation animate = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
                animate.setDuration(300);
                animate.setFillAfter(true);
                etSearch.startAnimation(animate);
                etSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        FloatingActionButton fabCreateGroup = findViewById(R.id.fabCreateGroup);

        if (!getCreateGroupRolesList().contains(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getRole())) {
            fabCreateGroup.setVisibility(View.GONE);
        }

        fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BrowseGroupActivity.this, MembersSearchActivity.class), REQUEST_CREATE_GROUP);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
        showLoading();
//        apiGetAllGroups();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    filteredAllGroupsList = getAllFilteredList(editable.toString());
                    filteredJoinedGroupsList = getJoinedFilteredList(editable.toString());
                    CommonData.setJoinGroupResult(filteredJoinedGroupsList);
                    CommonData.setAllGroupResult(filteredAllGroupsList);
                    joinedGroupsFragment.setUserVisibleHint(true);
                    allGroupsFragment.setUserVisibleHint(true);

                } else {
                    CommonData.setJoinGroupResult(joinedGroupList);
                    CommonData.setAllGroupResult(allGroupsList);
                    joinedGroupsFragment.setUserVisibleHint(true);
                    allGroupsFragment.setUserVisibleHint(true);
                    joinedGroupsFragment.setAdapter(joinedGroupList, false);
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        allGroupsFragment = new AllGroupsFragment();
        joinedGroupsFragment = new JoinedGroupsFragment();
        adapter.addFragment(allGroupsFragment, "All");
        adapter.addFragment(joinedGroupsFragment, "Joined");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void apiGetAllGroups() {
        CommonParams commonParams = new CommonParams.Builder()
                .add("en_user_id", CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                .build();
        RestClient.getApiInterface(false).getGroups(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap())
                .enqueue(new ResponseResolver<AllGroupsResponse>() {
                    @Override
                    public void onSuccess(AllGroupsResponse allGroupsResponse) {
                        allGroupsList.clear();
                        joinedGroupList.clear();
                        for (int i = 0; i < allGroupsResponse.getData().getJoinedChannels().size(); i++) {
                            JoinedChannel joinedChannel = allGroupsResponse.getData().getJoinedChannels().get(i);
                            allGroupsList.add(new AllGroup(Long.valueOf(joinedChannel.getChannelId()), joinedChannel.getLabel(), true, joinedChannel.getChatType()));
                            joinedGroupList.add(new AllGroup(Long.valueOf(joinedChannel.getChannelId()), joinedChannel.getLabel(), true, joinedChannel.getChatType()));
                        }
                        for (int i = 0; i < allGroupsResponse.getData().getOpenChannels().size(); i++) {
                            OpenChannel openChannel = allGroupsResponse.getData().getOpenChannels().get(i);
                            allGroupsList.add(new AllGroup(Long.valueOf(openChannel.getChannelId()), openChannel.getLabel(), false, openChannel.getChatType()));
                        }
                        Collections.sort(allGroupsList, (one, other) -> one != null && other != null ? one.getGroupName().compareTo(other.getGroupName()) : 0);
                        CommonData.setAllGroupResult(allGroupsList);
                        CommonData.setJoinGroupResult(joinedGroupList);
                        setupViewPager(viewPager);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CREATE_GROUP || requestCode == REQUEST_JOIN_GROUP) && resultCode == RESULT_OK) {
            allGroupsList.clear();
            joinedGroupList.clear();
            showLoading();
            apiGetAllGroups();
            setResult(RESULT_OK);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private ArrayList<AllGroup> getAllFilteredList(String text) {
        ArrayList<AllGroup> newfilteredGroupList = new ArrayList<>();
        newfilteredGroupList.clear();
        for (int i = 0; i < allGroupsList.size(); i++) {
            if (allGroupsList.get(i).getGroupName().toLowerCase().contains(text.toLowerCase())) {
                newfilteredGroupList.add(allGroupsList.get(i));
            }
        }
        return newfilteredGroupList;
    }

    private ArrayList<AllGroup> getJoinedFilteredList(String text) {
        ArrayList<AllGroup> newfilteredGroupList = new ArrayList<>();
        newfilteredGroupList.clear();
        for (int i = 0; i < joinedGroupList.size(); i++) {
            if (joinedGroupList.get(i).getGroupName().toLowerCase().contains(text.toLowerCase())) {
                newfilteredGroupList.add(joinedGroupList.get(i));
            }
        }
        return newfilteredGroupList;
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

    public interface SetAdapter {
        void setAdapter(ArrayList<AllGroup> filteredList, boolean isSearched);
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiGetAllGroups();
    }
}
