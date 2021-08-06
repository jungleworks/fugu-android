package com.skeleton.mvp.baseRecycler;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by rajatdhamija
 * 10/08/18.
 */

public abstract class BaseViewHolder<T, L extends BaseRecyclerViewListener, LO extends BaseRecyclerViewLongListener> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(T item, @Nullable L listener, @Nullable LO longListener, @Nullable BaseViewHolder<T, L, LO> holder);
}
