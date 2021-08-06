package com.skeleton.mvp.baseRecycler;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by rajatdhamija
 * 10/08/18.
 */

public interface OnRecyclerViewObjectClickListener<T> extends BaseRecyclerViewListener {
    void onItemClicked(T item, RecyclerView.ViewHolder holder);
}
