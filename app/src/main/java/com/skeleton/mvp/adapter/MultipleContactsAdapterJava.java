package com.skeleton.mvp.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skeleton.mvp.R;
import com.skeleton.mvp.model.ContactsList;
import com.skeleton.mvp.util.Log;

import java.util.ArrayList;

/**
 * Created
 * rajatdhamija on 29/06/18.
 */

public class MultipleContactsAdapterJava extends RecyclerView.Adapter<MultipleContactsAdapterJava.MyViewHolder> {

    private Context mContext;
    private ArrayList<ContactsList> contactsLists = new ArrayList<>();

    public MultipleContactsAdapterJava(ArrayList<ContactsList> contactsLists, Context mContext) {
        this.mContext = mContext;
        this.contactsLists = contactsLists;
    }

    public void updateList(ArrayList<ContactsList> contactsLists) {
        this.contactsLists = contactsLists;
    }

    @NonNull
    @Override
    public MultipleContactsAdapterJava.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MultipleContactsAdapterJava.MyViewHolder holder, int position) {
        Log.e("tag", position + "");
    }

    @Override
    public int getItemCount() {
        return contactsLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView tvName, tvData;

        public MyViewHolder(View itemView) {
            super(itemView);
//            tvName = itemView.findViewById(R.id.tvName);
//            tvData = itemView.findViewById(R.id.tvData);
        }
    }
}
