package com.skeleton.mvp.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.ViewGroup;

import com.skeleton.mvp.R;
import com.skeleton.mvp.viewHolders.SendEmailViewHolder;
import com.skeleton.mvp.baseRecycler.BaseRecyclerViewAdapter;
import com.skeleton.mvp.baseRecycler.OnRecyclerViewObjectClickListener;
import com.skeleton.mvp.model.GetAllMembers;

/**
 * Created by rajatdhamija
 * 10/08/18.
 */

public class SendEmailMembersAdapter extends BaseRecyclerViewAdapter<GetAllMembers,
        OnRecyclerViewObjectClickListener<GetAllMembers>,OnRecyclerViewObjectLongClickListener<GetAllMembers>,
        SendEmailViewHolder> {

    public SendEmailMembersAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public SendEmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SendEmailViewHolder(inflate(R.layout.item_user, parent));
    }
}
