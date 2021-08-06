package com.skeleton.mvp.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.model.sendinvitesgoogle.SendInvitesGoogle;

import java.util.ArrayList;
import java.util.List;

public class GoogleInvitesAdapter extends RecyclerView.Adapter<GoogleInvitesAdapter.MyViewHolder> {

    private List<SendInvitesGoogle> googleEmailIdList;
    private List<String> selectedEmails=new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView emailId;
        public AppCompatCheckBox checkBox;
        private LinearLayout llMain;
        private View divider;

        public MyViewHolder(View view) {
            super(view);
            emailId = (TextView) view.findViewById(R.id.tv_emailid_invites);
            checkBox = view.findViewById(R.id.invites_checkbox);
            llMain=view.findViewById(R.id.llMain);
            divider=view.findViewById(R.id.divider);
        }
    }

    public GoogleInvitesAdapter(List<SendInvitesGoogle> googleEmailIdList){
        this.googleEmailIdList = googleEmailIdList;
    }

    public void updateList(List<SendInvitesGoogle> googleEmailIdList){
        this.googleEmailIdList = googleEmailIdList;
    }

    @NonNull
    @Override
    public GoogleInvitesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.send_invites_row_layout, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GoogleInvitesAdapter.MyViewHolder myViewHolder, int i) {
        SendInvitesGoogle sendInvitesGoogle = googleEmailIdList.get(i);
        myViewHolder.emailId.setText(sendInvitesGoogle.getEmailId());

        if(selectedEmails.contains(sendInvitesGoogle.getEmailId())){
            myViewHolder.checkBox.setChecked(true);
        }else {
            myViewHolder.checkBox.setChecked(false);
        }
        myViewHolder.checkBox.setOnCheckedChangeListener(null);
        myViewHolder.checkBox.setClickable(false);
        myViewHolder.checkBox.setFocusable(false);
        myViewHolder.checkBox.setFocusableInTouchMode(false);

        if (myViewHolder.getAdapterPosition()==googleEmailIdList.size()-1){
            myViewHolder.divider.setVisibility(View.GONE);
        }else {
            myViewHolder.divider.setVisibility(View.VISIBLE);
        }

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedEmails.contains(sendInvitesGoogle.getEmailId())){
                    List<String> tempList=new ArrayList<>();
                    tempList.addAll(selectedEmails);
                    for (int i1 = 0; i1 <selectedEmails.size(); i1++){
                        if (selectedEmails.get(i1).equals(sendInvitesGoogle.getEmailId())){
                            tempList.remove(i1);
                        }
                    }
                    selectedEmails=new ArrayList<>();
                    selectedEmails.addAll(tempList);
                    myViewHolder.checkBox.setChecked(false);
                }else {
                    selectedEmails.add(sendInvitesGoogle.getEmailId());
                    myViewHolder.checkBox.setChecked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return googleEmailIdList.size();
    }

    public List<String> getSelectedEmails(){
        return selectedEmails;
    }

    public void onSelectAll(List<String> selectedEmails){
        this.selectedEmails = selectedEmails;
        notifyDataSetChanged();
    }

    public void onUnselectAll(){
        selectedEmails = new ArrayList<>();
        notifyDataSetChanged();
    }
}
