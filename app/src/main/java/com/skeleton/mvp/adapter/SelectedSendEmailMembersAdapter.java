package com.skeleton.mvp.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.ViewGroup;

import com.skeleton.mvp.R;
import com.skeleton.mvp.viewHolders.SelectedSendEmailViewHolder;
import com.skeleton.mvp.baseRecycler.BaseRecyclerViewAdapter;
import com.skeleton.mvp.baseRecycler.OnRecyclerViewObjectClickListener;
import com.skeleton.mvp.model.GetAllMembers;

/**
 * Created by rajatdhamija
 * 10/08/18.
 */

public class SelectedSendEmailMembersAdapter extends BaseRecyclerViewAdapter<GetAllMembers,
        OnRecyclerViewObjectClickListener<GetAllMembers>, OnRecyclerViewObjectLongClickListener<GetAllMembers>,
        SelectedSendEmailViewHolder> {

    public SelectedSendEmailMembersAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public SelectedSendEmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectedSendEmailViewHolder(inflate(R.layout.item_selected_user, parent));
    }
}
