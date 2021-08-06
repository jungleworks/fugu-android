package com.easyfilepicker.adapter;

import android.view.View;

/**
 * Created by gurmail on 15/01/19.
 *
 * @author gurmail
        */
public interface RecyclerItemClickListener {

    void onItemClick(View viewClicked, View parentView);

    void onItemSelected(View viewClicked, View parentView, boolean b);
}