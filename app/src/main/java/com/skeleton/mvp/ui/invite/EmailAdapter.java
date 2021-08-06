package com.skeleton.mvp.ui.invite;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.skeleton.mvp.R;
import com.skeleton.mvp.data.model.Emails;

import java.util.ArrayList;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.MyViewHolder> {

    private ArrayList<Emails> emailsList = new ArrayList<>();
    private Context mContext;
    private InviteMembersActivity inviteMembersActivity;
    private Editable editableOld;

    public EmailAdapter(ArrayList<Emails> emailsList, Context mContext) {
        this.emailsList = emailsList;
        this.mContext = mContext;
        inviteMembersActivity = (InviteMembersActivity) mContext;
    }

    @Override
    public EmailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invite, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EmailAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        Emails emails = emailsList.get(position);
        if (!TextUtils.isEmpty(emails.getEmail())) {
            holder.etEmail.setText(emails.getEmail());
            holder.etEmail.clearFocus();
        }
//        holder.etEmail.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (ValidationUtil.checkEmail(editable.toString())) {
//                    inviteMembersActivity.activateButton(editable.toString());
//                } else {
//                    inviteMembersActivity.deactivateButton();
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return emailsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private EditText etEmail;

        MyViewHolder(View itemView) {
            super(itemView);
            etEmail = itemView.findViewById(R.id.etEmail);
        }
    }

//    public interface SendButton {
//        void activateButton(String email);
//
//        void deactivateButton();
//    }
}
