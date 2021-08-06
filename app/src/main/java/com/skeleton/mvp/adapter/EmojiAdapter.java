package com.skeleton.mvp.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.activity.FuguInnerChatActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.model.Emoji;

import java.util.ArrayList;

/**
 * Created by rajatdhamija on 09/04/18.
 */

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.MyViewHolder> {
    private ArrayList<Emoji> emojisList = new ArrayList<>();
    private Activity context;
    private ChatActivity fuguChatActivity;
    private FuguInnerChatActivity fuguInnerChatActivity;

    public EmojiAdapter(ArrayList<Emoji> emojisList, Activity mContext) {
        this.emojisList = emojisList;
        context = mContext;
        try {
            fuguInnerChatActivity = null;
            fuguChatActivity = (ChatActivity) context;
        } catch (Exception e) {
            fuguChatActivity = null;
            fuguInnerChatActivity = (FuguInnerChatActivity) context;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.emoji_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final Emoji emoji = emojisList.get(position);
        holder.tvEmoji.setText(getEmojiByUnicode(Integer.parseInt(emoji.getUnicode(), 16)));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fuguChatActivity != null) {
                    fuguChatActivity.getClickedEmoji(emoji.getUnicode(), false, "");
                } else {
                    fuguInnerChatActivity.getClickedEmoji(emoji.getUnicode(), false, "");
                }
                CommonData.setEmojiMap(emoji.getUnicode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return emojisList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEmoji;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
        }
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public interface GetClickedEmoji {
        void getClickedEmoji(String unicode, boolean isEmoji, String muid);
    }
    public interface ThreadOpened{
        void openedThread();
    }
}
