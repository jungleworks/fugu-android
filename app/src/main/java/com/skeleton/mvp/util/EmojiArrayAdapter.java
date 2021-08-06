package com.skeleton.mvp.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.skeleton.mvp.R;
import com.vanniktech.emoji.VariantEmoji;
import com.vanniktech.emoji.emoji.Emoji;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.skeleton.mvp.util.Utils.checkNotNull;


/**
 * Created by rajatdhamija on 04/05/18.
 */

public final class EmojiArrayAdapter extends ArrayAdapter<Emoji> {
    @Nullable
    private final VariantEmoji variantManager;

    @Nullable
    private final com.skeleton.mvp.util.OnEmojiClickListener listener;
    @Nullable
    private final OnEMojiLongClickListener longListener;

    public EmojiArrayAdapter(@NonNull final Context context, @NonNull final Emoji[] emojis, @Nullable final VariantEmoji variantManager,
                      @Nullable final com.skeleton.mvp.util.OnEmojiClickListener listener, @Nullable final OnEMojiLongClickListener longListener) {
        super(context, 0, new ArrayList<>(Arrays.asList(emojis)));

        this.variantManager = variantManager;
        this.listener = listener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        com.skeleton.mvp.util.EmojiImageView image = (com.skeleton.mvp.util.EmojiImageView) convertView;

        final Context context = getContext();

        if (image == null) {
            image = (com.skeleton.mvp.util.EmojiImageView) LayoutInflater.from(context).inflate(R.layout.emoji_item, parent, false);

            image.setOnEmojiClickListener(listener);
            image.setOnEmojiLongClickListener(longListener);
        }

        final Emoji emoji = checkNotNull(getItem(position), "emoji == null");
        final Emoji variantToUse = variantManager == null ? emoji : variantManager.getVariant(emoji);
        image.setEmoji(variantToUse);

        return image;
    }

    void updateEmojis(final Collection<Emoji> emojis) {
        clear();
        addAll(emojis);
        notifyDataSetChanged();
    }
}

