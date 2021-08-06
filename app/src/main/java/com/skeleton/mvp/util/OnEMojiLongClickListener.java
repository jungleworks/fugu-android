package com.skeleton.mvp.util;

import androidx.annotation.NonNull;

import com.vanniktech.emoji.emoji.Emoji;

/**
 * Created by rajatdhamija on 04/05/18.
 */

public interface OnEMojiLongClickListener {
    void onEmojiLongClick(@NonNull EmojiImageView view, @NonNull Emoji emoji);
}
