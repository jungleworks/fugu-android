package com.skeleton.mvp.util;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.skeleton.mvp.util.googlemap.EmojiGridView;
import com.skeleton.mvp.util.googlemap.MyEmojiManager;
import com.vanniktech.emoji.RecentEmoji;
import com.vanniktech.emoji.VariantEmoji;

/**
 * Created by rajatdhamija on 04/05/18.
 */

final class EmojiPagerAdapter extends PagerAdapter {
    private static final int RECENT_POSITION = 0;

    private final com.skeleton.mvp.util.OnEmojiClickListener listener;
    private final OnEMojiLongClickListener longListener;
    private final RecentEmoji recentEmoji;
    private final VariantEmoji variantManager;

    private RecentEmojiGridView recentEmojiGridView;

    EmojiPagerAdapter(final com.skeleton.mvp.util.OnEmojiClickListener listener,
                      final OnEMojiLongClickListener longListener,
                      final RecentEmoji recentEmoji, final VariantEmoji variantManager) {
        this.listener = listener;
        this.longListener = longListener;
        this.recentEmoji = recentEmoji;
        this.variantManager = variantManager;
        this.recentEmojiGridView = null;
    }

    @Override public int getCount() {
        return MyEmojiManager.getInstance().getCategories().length + 1;
    }

    @Override public Object instantiateItem(final ViewGroup pager, final int position) {
        final View newView;

        if (position == RECENT_POSITION) {
            newView = new RecentEmojiGridView(pager.getContext()).init(listener, longListener, recentEmoji);
            recentEmojiGridView = (RecentEmojiGridView) newView;
        } else {
            newView = new EmojiGridView(pager.getContext()).init(listener, longListener,
                    MyEmojiManager.getInstance().getCategories()[position - 1], variantManager);
        }

        pager.addView(newView);
        return newView;
    }

    @Override public void destroyItem(final ViewGroup pager, final int position, final Object view) {
        pager.removeView((View) view);

        if (position == RECENT_POSITION) {
            recentEmojiGridView = null;
        }
    }

    @Override public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    int numberOfRecentEmojis() {
        return recentEmoji.getRecentEmojis().size();
    }

    void invalidateRecentEmojis() {
        if (recentEmojiGridView != null) {
            recentEmojiGridView.invalidateEmojis();
        }
    }
}

