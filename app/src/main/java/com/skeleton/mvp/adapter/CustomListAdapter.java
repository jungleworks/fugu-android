package com.skeleton.mvp.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.model.ContactsList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajatdhamija
 * 28/06/18.
 */

public class CustomListAdapter extends ArrayAdapter {
    private List<ContactsList> dataList;
    private Context mContext;
    private int itemLayout;
    private ListFilter listFilter = new ListFilter();
    private List<ContactsList> dataListAllItems;

    public CustomListAdapter(Context context, int resource, List<ContactsList> storeDataLst) {
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
        return dataList.get(position).getData();
    }
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }
        ContactsList contactsList = dataList.get(position);
        TextView strName = view.findViewById(R.id.name);
        TextView strName2 = view.findViewById(R.id.data);
        strName.setText(contactsList.getName());
        strName2.setText(contactsList.getData());
        if (TextUtils.isEmpty(contactsList.getName())) {
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

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<ContactsList> matchValues = new ArrayList<>();

                for (ContactsList dataItem : dataListAllItems) {
                    if (dataItem.getData().toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(new ContactsList(dataItem.getName(), dataItem.getData(),dataItem.isSelected()));
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
                dataList = (ArrayList<ContactsList>) results.values;
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
