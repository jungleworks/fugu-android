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

/**
 * Created by rajatdhamija
 * 22/01/18.
 */

public class AllGroupsFragment extends Fragment implements BrowseGroupActivity.SetAdapter {
    private ArrayList<AllGroup> allGroupList = new ArrayList<>();
    private ArrayList<AllGroup> joinGroupResult = new ArrayList<>();
    private RecyclerView rvGroups;
    private AllGroupsAdapter allGroupsAdapter;
    private boolean isSearched = false;
    private TextView tvNoRecordsFound;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_groups, container, false);
        rvGroups = view.findViewById(R.id.rvGroups);
        tvNoRecordsFound = view.findViewById(R.id.tvNoRecordsFound);
        if (CommonData.getAllGroupResults() != null && CommonData.getJoinGroupResults() != null) {
            allGroupList = CommonData.getAllGroupResults();
            joinGroupResult = CommonData.getJoinGroupResults();
        }
        allGroupsAdapter = new AllGroupsAdapter(allGroupList, getActivity(), joinGroupResult);
        if (rvGroups != null) {
            rvGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvGroups.setAdapter(allGroupsAdapter);
            if (allGroupList.size() == 0) {
                rvGroups.setVisibility(View.GONE);
                tvNoRecordsFound.setVisibility(View.VISIBLE);
            } else {
                tvNoRecordsFound.setVisibility(View.GONE);
            }
        }
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && allGroupsAdapter != null) {
            if (CommonData.getAllGroupResults() != null && CommonData.getJoinGroupResults() != null && rvGroups != null) {
                allGroupList = CommonData.getAllGroupResults();
                joinGroupResult = CommonData.getJoinGroupResults();
            }
            allGroupsAdapter = new AllGroupsAdapter(allGroupList, getActivity(), joinGroupResult);
            if (rvGroups != null) {
                rvGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvGroups.setAdapter(allGroupsAdapter);
                allGroupsAdapter.notifyDataSetChanged();
                if (allGroupList.size() == 0) {
                    rvGroups.setVisibility(View.GONE);
                    tvNoRecordsFound.setVisibility(View.VISIBLE);
                } else {
                    tvNoRecordsFound.setVisibility(View.GONE);
                    rvGroups.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void setAdapter(ArrayList<AllGroup> filteredList, boolean Searched) {
        isSearched = Searched;
        allGroupList = filteredList;
        joinGroupResult = CommonData.getJoinGroupResults();
        if (allGroupsAdapter != null && rvGroups != null) {
            allGroupsAdapter = new AllGroupsAdapter(allGroupList, getActivity(), joinGroupResult);
            if (rvGroups != null) {
                rvGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvGroups.setAdapter(allGroupsAdapter);
                allGroupsAdapter.notifyDataSetChanged();
                if (allGroupList.size() == 0) {
                    rvGroups.setVisibility(View.GONE);
                    tvNoRecordsFound.setVisibility(View.VISIBLE);
                } else {
                    tvNoRecordsFound.setVisibility(View.GONE);
                    rvGroups.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
