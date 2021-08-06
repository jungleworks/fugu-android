package com.skeleton.mvp.util;

import androidx.annotation.NonNull;

import com.vanniktech.emoji.emoji.Emoji;

/**
 * Created by rajatdhamija on 04/05/18.
 */

public class EmojiRange {
    public final int start;
    public final int end;
    public final Emoji emoji;

    public EmojiRange(final int start, final int end, @NonNull final Emoji emoji) {
        this.start = start;
        this.end = end;
        this.emoji = emoji;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final com.vanniktech.emoji.EmojiRange that = (com.vanniktech.emoji.EmojiRange) o;

        return start == that.start && end == that.end && emoji.equals(that.emoji);
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        result = 31 * result + emoji.hashCode();
        return result;

    }
}
