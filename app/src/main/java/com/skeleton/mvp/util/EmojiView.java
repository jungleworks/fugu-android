package com.skeleton.mvp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.content.res.AppCompatResources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.skeleton.mvp.util.googlemap.MyEmojiManager;
import com.vanniktech.emoji.RecentEmoji;
import com.vanniktech.emoji.VariantEmoji;
import com.vanniktech.emoji.emoji.EmojiCategory;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.RepeatListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by rajatdhamija on 04/05/18.
 */

@SuppressLint("ViewConstructor") final class EmojiView extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static final long INITIAL_INTERVAL = TimeUnit.SECONDS.toMillis(1) / 2;
    private static final int NORMAL_INTERVAL = 50;

    @ColorInt
    private final int themeAccentColor;
    @ColorInt private final int themeIconColor;

    private final ImageButton[] emojiTabs;
    private final EmojiPagerAdapter emojiPagerAdapter;

    @Nullable
    OnEmojiBackspaceClickListener onEmojiBackspaceClickListener;

    private int emojiTabLastSelectedIndex = -1;

    EmojiView(final Context context, final com.skeleton.mvp.util.OnEmojiClickListener onEmojiClickListener,
              final OnEMojiLongClickListener onEmojiLongClickListener, @NonNull final RecentEmoji recentEmoji,
              @NonNull final VariantEmoji variantManager) {
        super(context);

        View.inflate(context, com.vanniktech.emoji.R.layout.emoji_view, this);

        setOrientation(VERTICAL);
        setBackgroundColor(ContextCompat.getColor(context, com.vanniktech.emoji.R.color.emoji_background));

        themeIconColor = ContextCompat.getColor(context, com.vanniktech.emoji.R.color.emoji_icons);
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(com.vanniktech.emoji.R.attr.colorAccent, value, true);
        themeAccentColor = value.data;

        final ViewPager emojisPager = (ViewPager) findViewById(com.vanniktech.emoji.R.id.emojis_pager);
        final LinearLayout emojisTab = (LinearLayout) findViewById(com.vanniktech.emoji.R.id.emojis_tab);
        emojisPager.addOnPageChangeListener(this);

        final EmojiCategory[] categories = MyEmojiManager.getInstance().getCategories();

        emojiTabs = new ImageButton[categories.length + 2];
        emojiTabs[0] = inflateButton(context, com.vanniktech.emoji.R.drawable.emoji_recent, emojisTab);
        for (int i = 0; i < categories.length; i++) {
            emojiTabs[i + 1] = inflateButton(context, categories[i].getIcon(), emojisTab);
        }
        emojiTabs[emojiTabs.length - 1] = inflateButton(context, com.vanniktech.emoji.R.drawable.emoji_backspace, emojisTab);

        handleOnClicks(emojisPager);

        emojiPagerAdapter = new EmojiPagerAdapter(onEmojiClickListener, onEmojiLongClickListener, recentEmoji, variantManager);
        emojisPager.setAdapter(emojiPagerAdapter);

        final int startIndex = emojiPagerAdapter.numberOfRecentEmojis() > 0 ? 0 : 1;
        emojisPager.setCurrentItem(startIndex);
        onPageSelected(startIndex);
    }

    private void handleOnClicks(final ViewPager emojisPager) {
        for (int i = 0; i < emojiTabs.length - 1; i++) {
            emojiTabs[i].setOnClickListener(new EmojiView.EmojiTabsClickListener(emojisPager, i));
        }

        emojiTabs[emojiTabs.length - 1].setOnTouchListener(new RepeatListener(INITIAL_INTERVAL, NORMAL_INTERVAL, new OnClickListener() {
            @Override public void onClick(final View view) {
                if (onEmojiBackspaceClickListener != null) {
                    onEmojiBackspaceClickListener.onEmojiBackspaceClick(view);
                }
            }
        }));
    }

    public void setOnEmojiBackspaceClickListener(@Nullable final OnEmojiBackspaceClickListener onEmojiBackspaceClickListener) {
        this.onEmojiBackspaceClickListener = onEmojiBackspaceClickListener;
    }

    private ImageButton inflateButton(final Context context, @DrawableRes final int icon, final ViewGroup parent) {
        final ImageButton button = (ImageButton) LayoutInflater.from(context).inflate(com.vanniktech.emoji.R.layout.emoji_category, parent, false);

        button.setImageDrawable(AppCompatResources.getDrawable(context, icon));
        button.setColorFilter(themeIconColor, PorterDuff.Mode.SRC_IN);

        parent.addView(button);

        return button;
    }

    @Override public void onPageSelected(final int i) {
        if (emojiTabLastSelectedIndex != i) {
            if (i == 0) {
                emojiPagerAdapter.invalidateRecentEmojis();
            }

            if (emojiTabLastSelectedIndex >= 0 && emojiTabLastSelectedIndex < emojiTabs.length) {
                emojiTabs[emojiTabLastSelectedIndex].setSelected(false);
                emojiTabs[emojiTabLastSelectedIndex].setColorFilter(themeIconColor, PorterDuff.Mode.SRC_IN);
            }

            emojiTabs[i].setSelected(true);
            emojiTabs[i].setColorFilter(themeAccentColor, PorterDuff.Mode.SRC_IN);

            emojiTabLastSelectedIndex = i;
        }
    }

    @Override public void onPageScrolled(final int i, final float v, final int i2) {
        // No-op.
    }

    @Override public void onPageScrollStateChanged(final int i) {
        // No-op.
    }

    static class EmojiTabsClickListener implements OnClickListener {
        private final ViewPager emojisPager;
        private final int position;

        EmojiTabsClickListener(final ViewPager emojisPager, final int position) {
            this.emojisPager = emojisPager;
            this.position = position;
        }

        @Override public void onClick(final View v) {
            emojisPager.setCurrentItem(position);
        }
    }
}

