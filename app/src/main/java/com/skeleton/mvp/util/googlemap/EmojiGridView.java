package com.skeleton.mvp.util.googlemap;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.GridView;

import com.skeleton.mvp.util.EmojiArrayAdapter;
import com.skeleton.mvp.util.OnEMojiLongClickListener;
import com.vanniktech.emoji.VariantEmoji;
import com.vanniktech.emoji.emoji.EmojiCategory;

/**
 * Created by rajatdhamija on 04/05/18.
 */

public class EmojiGridView extends GridView {
    protected EmojiArrayAdapter emojiArrayAdapter;

    public EmojiGridView(Context context) {
        super(context);
        Resources resources = this.getResources();
        int width = resources.getDimensionPixelSize(com.vanniktech.emoji.R.dimen.emoji_grid_view_column_width);
        int spacing = resources.getDimensionPixelSize(com.vanniktech.emoji.R.dimen.emoji_grid_view_spacing);
        this.setColumnWidth(width);
        this.setHorizontalSpacing(spacing);
        this.setVerticalSpacing(spacing);
        this.setPadding(spacing, spacing, spacing, spacing);
        this.setNumColumns(-1);
        this.setClipToPadding(false);
        this.setVerticalScrollBarEnabled(false);
    }

    public EmojiGridView init(@Nullable com.skeleton.mvp.util.OnEmojiClickListener onEmojiClickListener, @Nullable OnEMojiLongClickListener onEmojiLongClickListener, @NonNull EmojiCategory category, @NonNull VariantEmoji variantManager) {
        this.emojiArrayAdapter = new EmojiArrayAdapter(this.getContext(), category.getEmojis(), variantManager, onEmojiClickListener, onEmojiLongClickListener);
        this.setAdapter(this.emojiArrayAdapter);
        return this;
    }
}

