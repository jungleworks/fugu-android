package com.skeleton.mvp.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.adapter.InvitedMembersAdapter;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.InvitedMembers;
import com.skeleton.mvp.model.getAllMembers.AllMemberResponse;
import com.skeleton.mvp.model.getAllMembers.PendingMember;
import com.skeleton.mvp.model.searchPendingAndAccepted.SearchPendingAndAccepted;
import com.skeleton.mvp.model.searchPendingAndAccepted.User;
import com.skeleton.mvp.retrofit.APIError;
import com.skeleton.mvp.retrofit.ResponseResolver;
import com.skeleton.mvp.utils.EndlessScrolling;

import java.util.ArrayList;

import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.USER_TYPE;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.SEARCH_TEXT;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE_ID;

/**
 * Created by rajatdhamija
 * 19/06/18.
 */

public class AcceptedFragment extends Fragment {
    private ArrayList<InvitedMembers> alreadyInvitedList = new ArrayList<>();
    private InvitedMembersAdapter invitedMembersAdapter;
    private RecyclerView recyclerView;
    private FcCommonResponse fcCommonResponse;
    private LinearLayout llInvites;
    private TextView tvMessage;
    private int pageStart = 0;
    private int pageSize = 0;
    private int userCount = 0;
    private EndlessScrolling endlessScrolling = null;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted, container, false);
        recyclerView = view.findViewById(R.id.rvPending);
        llInvites = view.findViewById(R.id.llNoInvites);
        tvMessage = view.findViewById(R.id.tvMessage);
        invitedMembersAdapter = new InvitedMembersAdapter(alreadyInvitedList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(invitedMembersAdapter);
        setEndlessScrolling();
        apiGetInvitedMember();
        return view;
    }

    private void setEndlessScrolling() {
        if (endlessScrolling == null) {
            endlessScrolling = new EndlessScrolling((LinearLayoutManager) recyclerView.getLayoutManager()) {
                @Override
                public void onLoadMore(int currentPages) {
//                    if (pageStart == 0) {
//                        pageStart = pageSize + 1;
//                    } else {
                    pageStart = pageStart + pageSize;
//                    }
                    apiGetInvitedMember();
                }

                @Override
                public void onHide() {

                }

                @Override
                public void onShow() {

                }
            };
            recyclerView.addOnScrollListener(endlessScrolling);
        }
    }

    private void apiGetInvitedMember() {
        fcCommonResponse = CommonData.getCommonResponse();
        com.skeleton.mvp.data.network.CommonParams commonParams = new com.skeleton.mvp.data.network.CommonParams.Builder()
                .add(WORKSPACE_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId())
                .add("user_type", "ACCEPTED")
                .add("page_start", pageStart)
                .build();
        RestClient.getApiInterface(true).getAllMembers(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap())
                .enqueue(new com.skeleton.mvp.data.network.ResponseResolver<AllMemberResponse>() {
                    @Override
                    public void onSuccess(AllMemberResponse allMemberResponse) {
                        if (pageStart == 0) {
                            pageSize = allMemberResponse.getData().getGetAllMemberPageSize();
                            userCount = allMemberResponse.getData().getUserCount();
                        }
                        for (int i = 0; i < allMemberResponse.getData().getAcceptedMemberList().size(); i++) {
                            PendingMember invitedMembers = allMemberResponse.getData().getAcceptedMemberList().get(i);
                            if (invitedMembers.getStatus().equalsIgnoreCase("EXPIRED")) {
                                if (!TextUtils.isEmpty(allMemberResponse.getData().getAcceptedMemberList().get(i).getEmail())) {
                                    alreadyInvitedList.add(new InvitedMembers(invitedMembers.getEmail(), true, invitedMembers.getDateTime(), invitedMembers.getStatus(), false));
                                } else {
                                    alreadyInvitedList.add(new InvitedMembers(invitedMembers.getContactNumber(), false, invitedMembers.getDateTime(), invitedMembers.getStatus(), false));
                                }
                            }
                        }
                        invitedMembersAdapter.updateList(alreadyInvitedList);
                        invitedMembersAdapter.notifyDataSetChanged();
                        if (alreadyInvitedList.size() == 0) {
                            llInvites.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            tvMessage.setText(R.string.no_accepted_invites);
                        } else {
                            llInvites.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(ApiError error) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });

    }

    public void setFilteredList(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (userCount < pageSize) {
                ArrayList<InvitedMembers> newfilteredGroupList = new ArrayList<>();
                newfilteredGroupList.clear();
                for (int i = 0; i < alreadyInvitedList.size(); i++) {
                    if (alreadyInvitedList.get(i).getData().toLowerCase().contains(text.toLowerCase())) {
                        newfilteredGroupList.add(alreadyInvitedList.get(i));
                    }
                }
                invitedMembersAdapter.updateList(newfilteredGroupList);
                invitedMembersAdapter.notifyDataSetChanged();
                if (newfilteredGroupList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    llInvites.setVisibility(View.VISIBLE);
                    tvMessage.setText(R.string.no_results_found);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    llInvites.setVisibility(View.GONE);
                }
            } else {
                if (text.length() > 2) {
                    apiPendingAndAcceptedUserSearch(text);
                }
            }
        } else {
            invitedMembersAdapter.updateList(alreadyInvitedList);
            invitedMembersAdapter.notifyDataSetChanged();
            if (alreadyInvitedList.size() == 0) {
                llInvites.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                tvMessage.setText(R.string.no_accepted_invites);
            } else {
                llInvites.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            setEndlessScrolling();
        }
    }


    public void apiPendingAndAcceptedUserSearch(String text) {
        com.skeleton.mvp.retrofit.CommonParams commonParams = new com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add(SEARCH_TEXT, text)
                .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add(USER_TYPE, "ACCEPTED")
                .build();

        RestClient.getApiInterface(true).pendingAndAcceptedUserSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(),
                1, BuildConfig.VERSION_CODE, commonParams.getMap())
                .enqueue(new ResponseResolver<SearchPendingAndAccepted>() {
                    @Override
                    public void success(SearchPendingAndAccepted searchPendingAndAccepted) {
                        if (endlessScrolling != null) {
                            recyclerView.removeOnScrollListener(endlessScrolling);
                            endlessScrolling = null;
                        }
                        ArrayList<InvitedMembers> searchedList = new ArrayList<>();
                        for (int i = 0; i < searchPendingAndAccepted.getData().getUsers().size(); i++) {
                            User user = searchPendingAndAccepted.getData().getUsers().get(i);
                            if (!TextUtils.isEmpty(user.getEmail())) {
                                searchedList.add(new InvitedMembers(user.getEmail(),
                                        true, user.getDateTime(), user.getStatus(),
                                        false));
                            } else {
                                searchedList.add(new InvitedMembers(user.getContactNumber(),
                                        false, user.getDateTime(), user.getStatus(),
                                        false));
                            }
                        }
                        invitedMembersAdapter.updateList(searchedList);
                        invitedMembersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(APIError error) {

                    }
                });
    }
}
