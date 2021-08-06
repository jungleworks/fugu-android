package com.skeleton.mvp.baseRecycler;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajatdhamija
 * 10/08/18.
 */

public abstract class BaseRecyclerViewAdapter<T, L extends BaseRecyclerViewListener, LO extends BaseRecyclerViewLongListener,
        VH extends BaseViewHolder<T, L, LO>> extends RecyclerView.Adapter<VH> {
    private List<T> items;
    private L listener;
    private LO longListener;
    private LayoutInflater layoutInflater;

    public BaseRecyclerViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T item = items.get(position);
        holder.onBind(item, listener, longListener, holder);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<T> items) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot set `null` item to the Recycler adapter");
        }
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return items;
    }

    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item to the Recycler adapter");
        }
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addAll(List<T> items) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot add `null` items to the Recycler adapter");
        }
        this.items.addAll(items);
        notifyItemRangeInserted(this.items.size() - items.size(), items.size());
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(T item) {
        int position = items.indexOf(item);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    public void setListener(L listener) {
        this.listener = listener;
    }

    public void setLongListener(LO longListener) {
        this.longListener = longListener;
    }

    @NonNull
    protected View inflate(@LayoutRes final int layout, @Nullable final ViewGroup parent, final boolean attachToRoot) {
        return layoutInflater.inflate(layout, parent, attachToRoot);
    }

    @NonNull
    protected View inflate(@LayoutRes final int layout, final @Nullable ViewGroup parent) {
        return inflate(layout, parent, false);
    }
}
