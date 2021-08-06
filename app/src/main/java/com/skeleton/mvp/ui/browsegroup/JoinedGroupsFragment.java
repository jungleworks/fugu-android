package com.skeleton.mvp.ui.browsegroup;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.AllGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created
 * rajatdhamija on 22/01/18.
 */

public class JoinedGroupsFragment extends Fragment implements BrowseGroupActivity.SetAdapter {
    private ArrayList<AllGroup> joinedGroupList = new ArrayList<>();
    private ArrayList<AllGroup> allGroupsList = new ArrayList<>();
    private RecyclerView rvGroups;
    private JoinAdapter allGroupsAdapter;
    private BrowseGroupActivity browseGroupActivity;
    private boolean isSearched = false;
    private TextView tvNoRecordsFound;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmnet_joined_groups, container, false);
        rvGroups = view.findViewById(R.id.rvGroups);
        tvNoRecordsFound = view.findViewById(R.id.tvNoRecordsFound);
        browseGroupActivity = (BrowseGroupActivity) getActivity();
        if (CommonData.getAllGroupResults() != null && CommonData.getJoinGroupResults() != null) {
            joinedGroupList = CommonData.getJoinGroupResults();
            allGroupsList = CommonData.getAllGroupResults();
            Collections.sort(joinedGroupList, new Comparator<AllGroup>() {
                public int compare(AllGroup one, AllGroup other) {
                    return one.getGroupName().compareTo(other.getGroupName());
                }
            });
        }
        allGroupsAdapter = new JoinAdapter(joinedGroupList, getActivity(), allGroupsList);
        rvGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvGroups.setAdapter(allGroupsAdapter);
        if (joinedGroupList.size() == 0) {
            rvGroups.setVisibility(View.GONE);
            tvNoRecordsFound.setVisibility(View.VISIBLE);
        } else {
            tvNoRecordsFound.setVisibility(View.GONE);
            rvGroups.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        joinedGroupList.clear();
        if (isVisibleToUser) {
            if (CommonData.getAllGroupResults() != null && CommonData.getJoinGroupResults() != null) {
                joinedGroupList = CommonData.getJoinGroupResults();
                allGroupsList = CommonData.getAllGroupResults();
                Collections.sort(joinedGroupList, new Comparator<AllGroup>() {
                    public int compare(AllGroup one, AllGroup other) {
                        return one.getGroupName().compareTo(other.getGroupName());
                    }
                });
            }
            if (rvGroups != null) {
                allGroupsAdapter = new JoinAdapter(joinedGroupList, getActivity(), allGroupsList);
                rvGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvGroups.setAdapter(allGroupsAdapter);
                if (joinedGroupList.size() == 0) {
                    rvGroups.setVisibility(View.GONE);
                    tvNoRecordsFound.setVisibility(View.VISIBLE);
                } else {
                    tvNoRecordsFound.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void setAdapter(ArrayList<AllGroup> filteredList, boolean Searched) {
        isSearched = Searched;
        joinedGroupList = filteredList;
        allGroupsList = CommonData.getAllGroupResults();
        if (allGroupsAdapter != null && rvGroups != null) {
            allGroupsAdapter = new JoinAdapter(joinedGroupList, getActivity(), allGroupsList);
            if (rvGroups != null) {
                rvGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvGroups.setAdapter(allGroupsAdapter);
                allGroupsAdapter.notifyDataSetChanged();
                if (joinedGroupList.size() == 0) {
                    rvGroups.setVisibility(View.GONE);
                    tvNoRecordsFound.setVisibility(View.VISIBLE);
                } else {
                    tvNoRecordsFound.setVisibility(View.GONE);
                }
            }
        }
    }
}
