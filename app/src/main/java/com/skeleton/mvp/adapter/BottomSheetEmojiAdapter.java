package com.skeleton.mvp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.activity.FuguInnerChatActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.model.Emoji;

import java.util.ArrayList;

/**
 * Created by rajatdhamija on 09/04/18.
 */

public class BottomSheetEmojiAdapter extends RecyclerView.Adapter<BottomSheetEmojiAdapter.MyViewHolder> {
    private ArrayList<Emoji> emojisList;
    private Activity context;
    private ChatActivity fuguChatActivity;
    private FuguInnerChatActivity fuguInnerChatActivity;
    private String muid;

    public BottomSheetEmojiAdapter(ArrayList<Emoji> emojisList, Activity mContext, String muid) {
        this.emojisList = emojisList;
        this.muid = muid;
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
        holder.itemView.setOnClickListener(v -> {
            if (fuguChatActivity != null) {
                fuguChatActivity.initClickedEmojiMuid(muid);
                fuguChatActivity.getClickedEmoji(emoji.getUnicode(), false, "");
            } else {
                fuguInnerChatActivity.initClickedEmojiMuid(muid);
                fuguInnerChatActivity.getClickedEmoji(emoji.getUnicode(), false, muid);
            }
            CommonData.setEmojiMap(emoji.getUnicode());
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

    public interface ThreadOpened {
        void openedThread();
    }
}
