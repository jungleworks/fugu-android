package com.skeleton.mvp.ui.dialog.listdialog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skeleton.mvp.R;

import java.util.ArrayList;

/**
 * Developer: Click Labs
 */
public class ListAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private ArrayList<String> array = new ArrayList<>();

    /**
     * Instantiates a new Dialog list adapter.
     *
     * @param activity the activity
     */
    public ListAdapter(final Activity activity) {
        mInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Sets data.
     *
     * @param jobsArrayList the jobs array list
     */
    public void setData(final ArrayList<String> jobsArrayList) {
        this.array = jobsArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(final int position) {
        return position;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View item = convertView;
        final ViewHolder viewHolder;
        if (convertView == null) {
            item = mInflater.inflate(R.layout.item_view_dialog_list, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) item.findViewById(R.id.tvValue);
            viewHolder.pos = position;
            item.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) item.getTag();
        }
        viewHolder.name.setText(array.get(position));
        return item;
    }

    /**
     * The type View holder.
     */
    static class ViewHolder {
        private TextView name;
        private int pos;

        /**
         * getter of name TextView
         *
         * @return name ,  TextView instance
         */
        public TextView getName() {
            return name;
        }

        /**
         * setter of name TextView
         *
         * @param name ,  TextView instance
         */
        public void setName(final TextView name) {
            this.name = name;
        }

        /**
         * getter of Position
         *
         * @return the position
         */
        public int getPos() {
            return pos;
        }

        /**
         * setter of position
         *
         * @param pos , position in adapter
         */
        public void setPos(final int pos) {
            this.pos = pos;
        }
    }
}

