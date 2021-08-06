package com.skeleton.mvp.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skeleton.mvp.R;

import java.util.ArrayList;

/**
 * Created by rajatdhamija
 * 29/06/18.
 */

public class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.MyViewHolder> {
    private ArrayList<String> selected = new ArrayList<>();

    public SelectedAdapter(ArrayList<String> selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public SelectedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public void update(ArrayList<String> list) {
        selected = list;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedAdapter.MyViewHolder holder, int position) {
        holder.name.setText(selected.get(position));
    }

    @Override
    public int getItemCount() {
        return selected.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
        }
    }
}
