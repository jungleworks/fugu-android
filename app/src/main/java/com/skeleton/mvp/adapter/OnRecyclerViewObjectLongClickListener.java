package com.skeleton.mvp.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.baseRecycler.BaseRecyclerViewLongListener;

/**
 * Created by rajatdhamija
 * 14/08/18.
 */

public interface OnRecyclerViewObjectLongClickListener<T> extends BaseRecyclerViewLongListener {
    void onItemLongClicked(T item, RecyclerView.ViewHolder holder);
}
