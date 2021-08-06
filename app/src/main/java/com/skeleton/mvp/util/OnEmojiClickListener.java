package com.skeleton.mvp.util;

import androidx.annotation.NonNull;

import com.vanniktech.emoji.emoji.Emoji;

/**
 * Created by rajatdhamija on 04/05/18.
 */

public interface OnEmojiClickListener {
    void onEmojiClick(@NonNull EmojiImageView emoji, @NonNull Emoji imageView);
}
