package io.github.memfis19.cadar.internal.ui.list.adapter.holder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by memfis on 3/29/17.
 */

public class ListHolder extends RecyclerView.ViewHolder {

    public int type;

    public ListHolder(View itemView) {
        super(itemView);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
