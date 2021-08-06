package com.skeleton.mvp.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skeleton.mvp.util.googlemap.EmojiGridView;
import com.vanniktech.emoji.RecentEmoji;
import com.vanniktech.emoji.emoji.Emoji;

import java.util.Collection;

/**
 * Created by rajatdhamija on 04/05/18.
 */

final class RecentEmojiGridView extends EmojiGridView {
    private RecentEmoji recentEmojis;

    public RecentEmojiGridView(@NonNull final Context context) {
        super(context);
    }

    public RecentEmojiGridView init(@Nullable final com.skeleton.mvp.util.OnEmojiClickListener onEmojiClickListener,
                                                         @Nullable final OnEMojiLongClickListener onEmojiLongClickListener,
                                                         @NonNull final RecentEmoji recentEmoji) {
        recentEmojis = recentEmoji;

        final Collection<Emoji> emojis = recentEmojis.getRecentEmojis();
        emojiArrayAdapter = new EmojiArrayAdapter(getContext(), emojis.toArray(new Emoji[emojis.size()]), null,
                onEmojiClickListener, onEmojiLongClickListener);

        setAdapter(emojiArrayAdapter);

        return this;
    }

    public void invalidateEmojis() {
        emojiArrayAdapter.updateEmojis(recentEmojis.getRecentEmojis());
    }
}

