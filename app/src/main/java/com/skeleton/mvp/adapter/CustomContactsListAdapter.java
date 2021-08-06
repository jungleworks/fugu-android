package com.skeleton.mvp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.GetAllMembers;
import com.skeleton.mvp.model.userSearch.User;
import com.skeleton.mvp.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.ui.AppConstants.SEARCH_TEXT;

public class CustomContactsListAdapter extends ArrayAdapter {

    private List<GetAllMembers> dataList;
    private Context mContext;
    private int itemLayout;
    private ListFilter listFilter = new ListFilter();
    private List<GetAllMembers> dataListAllItems;


    public CustomContactsListAdapter(Context context, int resource, List<GetAllMembers> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    public void updateList(List<GetAllMembers> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        try {
            return dataList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public GetAllMembers getItem(int position) {
        return dataList.get(position);
    }


    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }
        GetAllMembers contactsList = dataList.get(position);
        TextView strName = view.findViewById(R.id.name);
        TextView strName2 = view.findViewById(R.id.data);
        strName.setText(contactsList.getFullName());
        strName2.setText(contactsList.getEmail());
        if (TextUtils.isEmpty(contactsList.getFullName())) {
            strName.setVisibility(View.GONE);
            strName2.setTextSize(14);
        } else {
            strName.setVisibility(View.VISIBLE);
            strName2.setTextSize(12);
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<>(dataList);
                }
            }

//            if (prefix == null || prefix.length() == 0) {
//                synchronized (lock) {
//                    results.values = dataListAllItems;
//                    results.count = dataListAllItems.size();
//                }
//            } else {
            final String searchStrLowerCase = prefix.toString().toLowerCase();
            if (!searchStrLowerCase.isEmpty() && searchStrLowerCase.length() > 1) {
                ((ProfileActivity) mContext).apiUserSearch(searchStrLowerCase, matchValues -> {
                    results.values = matchValues;
                    results.count = matchValues.size();
                    publishResults(prefix, results);
                });
            }
//                apiUserSearch(searchStrLowerCase, matchValues -> {
//                    results.values = matchValues;
//                    results.count = matchValues.size();
//                });

//                for (GetAllMembers dataItem : dataListAllItems) {
//                    if (dataItem.getFullname().toLowerCase().contains(searchStrLowerCase)) {
//                        matchValues.add(new GetAllMembers(dataItem.getUserId(), dataItem.getFullname(),dataItem.getEmail(),dataItem.getUserImage(),
//                                dataItem.getUserThumbnailImage(),dataItem.getRole(),dataItem.getSerachCount(),dataItem.getPhoneNo()));
//                    }
//                }


//            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<GetAllMembers>) results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }

    private void apiUserSearch(String searchStrLowerCase, UserSearch userSearchInterface) {
        WorkspacesInfo workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition());
        com.skeleton.mvp.retrofit.CommonParams.Builder commonParams = new com.skeleton.mvp.retrofit.CommonParams.Builder();
        commonParams.add(EN_USER_ID, workspaceInfo.getEnUserId());
        commonParams.add(SEARCH_TEXT, searchStrLowerCase);

        RestClient.getApiInterface(true).userSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.build().getMap())
                .enqueue(new ResponseResolver<com.skeleton.mvp.model.userSearch.UserSearch>() {
                    @Override
                    public void onSuccess(com.skeleton.mvp.model.userSearch.UserSearch userSearch) {
                        ArrayList<GetAllMembers> matchValues = new ArrayList<>();

                        for (User user : userSearch.getData().getUsers()) {
                            GetAllMembers getAllMembers = new GetAllMembers(user.getUserId(), user.getFullName(),
                                    user.getEmail(),
                                    user.getUserImage(),
                                    user.getUserThumbnailImage(),
                                    user.getRole(),
                                    0, user.getContactNumber(), "");
                            matchValues.add(getAllMembers);
                        }
                        userSearchInterface.onSuccess(matchValues);
                    }

                    @Override
                    public void onError(ApiError error) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });

    }

    public interface UserSearch {
        void onSuccess(ArrayList<GetAllMembers> matchValues);
    }
}
