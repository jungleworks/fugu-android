package com.skeleton.mvp.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.skeleton.mvp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajatdhamija
 * 28/06/18.
 */

public class SearchSuggestionsAdapter extends ArrayAdapter {
    private List<GroupSuggestion> dataList;
    private Context mContext;
    private int itemLayout;
    private ListFilter listFilter = new ListFilter();
    private List<GroupSuggestion> dataListAllItems;

    public SearchSuggestionsAdapter(Context context, int resource, List<GroupSuggestion> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position).getChannelName();
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }
        GroupSuggestion groupSuggestion = dataList.get(position);
        TextView strName = view.findViewById(R.id.name);
        TextView strName2 = view.findViewById(R.id.data);
        strName.setText(groupSuggestion.getChannelName());
        strName2.setVisibility(View.GONE);
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
                    dataListAllItems = new ArrayList<GroupSuggestion>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<GroupSuggestion> matchValues = new ArrayList<>();

                for (GroupSuggestion dataItem : dataListAllItems) {
                    if (dataItem.getChannelName().toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(new GroupSuggestion(dataItem.getChannelId(), dataItem.getChannelName()));
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<GroupSuggestion>) results.values;
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
}
