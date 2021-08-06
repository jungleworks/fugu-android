package com.skeleton.mvp.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.model.EmojiReactions;

import java.util.ArrayList;

/**
 * Created by rajatdhamija
 * 12/04/18.
 */

public class EmojiReactionAdapter extends RecyclerView.Adapter<EmojiReactionAdapter.MyViewHolder> {
    private ArrayList<EmojiReactions> reactionsList = new ArrayList<>();

    public EmojiReactionAdapter(ArrayList<EmojiReactions> reactionsList) {
        this.reactionsList = reactionsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.emoji_reaction_row, parent, false);
        return new EmojiReactionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        EmojiReactions emojiReactions = reactionsList.get(position);
        holder.tvReaction.setText(emojiReactions.getName());
        holder.tvEmoji.setText(emojiReactions.getEmoji());
        if (position == reactionsList.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return reactionsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReaction, tvEmoji;
        private View divider;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvReaction = itemView.findViewById(R.id.tvReaction);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            divider = itemView.findViewById(R.id.tvDivider);
        }
    }
}
