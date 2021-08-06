package com.skeleton.mvp.util.googlemap;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.TextUtils;

import com.skeleton.mvp.util.EmojiRange;
import com.skeleton.mvp.util.EmojiSpan;
import com.vanniktech.emoji.EmojiProvider;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.emoji.EmojiCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.skeleton.mvp.util.Utils.checkNotNull;
/**
 * Created by rajatdhamija on 04/05/18.
 */

public class MyEmojiManager {
    private static final MyEmojiManager INSTANCE = new MyEmojiManager();
    private static final int GUESSED_UNICODE_AMOUNT = 3000;
    private static final int GUESSED_TOTAL_PATTERN_LENGTH = GUESSED_UNICODE_AMOUNT * 4;

    private static final Comparator<String> STRING_LENGTH_COMPARATOR = new Comparator<String>() {
        @Override public int compare(final String first, final String second) {
            final int firstLength = first.length();
            final int secondLength = second.length();

            return firstLength < secondLength ? 1 : firstLength == secondLength ? 0 : -1;
        }
    };

    private final Map<String, Emoji> emojiMap = new LinkedHashMap<>(GUESSED_UNICODE_AMOUNT);
    private EmojiCategory[] categories;
    private Pattern emojiPattern;
    private Pattern emojiRepetitivePattern;

    private MyEmojiManager() {
        // No instances apart from singleton.
    }

    public static MyEmojiManager getInstance() {
        return INSTANCE;
    }

    /**
     * Installs the given EmojiProvider.
     *
     * NOTE: That only one can be present at any time.
     *
     * @param provider the provider that should be installed.
     */
    public static void install(@NonNull final EmojiProvider provider) {
        INSTANCE.categories = checkNotNull(provider.getCategories(), "categories == null");
        INSTANCE.emojiMap.clear();

        final List<String> unicodesForPattern = new ArrayList<>(GUESSED_UNICODE_AMOUNT);

        final int categoriesSize = INSTANCE.categories.length;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < categoriesSize; i++) {
            final Emoji[] emojis = checkNotNull(INSTANCE.categories[i].getEmojis(), "emojies == null");

            final int emojisSize = emojis.length;
            //noinspection ForLoopReplaceableByForEach
            for (int j = 0; j < emojisSize; j++) {
                final Emoji emoji = emojis[j];
                final String unicode = emoji.getUnicode();
                final List<Emoji> variants = emoji.getVariants();

                INSTANCE.emojiMap.put(unicode, emoji);
                unicodesForPattern.add(unicode);

                //noinspection ForLoopReplaceableByForEach
                for (int k = 0; k < variants.size(); k++) {
                    final Emoji variant = variants.get(k);
                    final String variantUnicode = variant.getUnicode();

                    INSTANCE.emojiMap.put(variantUnicode, variant);
                    unicodesForPattern.add(variantUnicode);
                }
            }
        }

        if (unicodesForPattern.isEmpty()) {
            throw new IllegalArgumentException("Your EmojiProvider must at least have one category with at least one emoji.");
        }

        // We need to sort the unicodes by length so the longest one gets matched first.
        Collections.sort(unicodesForPattern, STRING_LENGTH_COMPARATOR);

        final StringBuilder patternBuilder = new StringBuilder(GUESSED_TOTAL_PATTERN_LENGTH);

        final int unicodesForPatternSize = unicodesForPattern.size();
        for (int i = 0; i < unicodesForPatternSize; i++) {
            patternBuilder.append(Pattern.quote(unicodesForPattern.get(i))).append('|');
        }

        final String regex = patternBuilder.deleteCharAt(patternBuilder.length() - 1).toString();
        INSTANCE.emojiPattern = Pattern.compile(regex);
        INSTANCE.emojiRepetitivePattern = Pattern.compile('(' + regex + ")+");
    }

    static void destroy() {
        INSTANCE.emojiMap.clear();
        INSTANCE.categories = null;
        INSTANCE.emojiPattern = null;
        INSTANCE.emojiRepetitivePattern = null;
    }

    public static void replaceWithImages(final Context context, final Spannable text, final float emojiSize) {
        final MyEmojiManager emojiManager = MyEmojiManager.getInstance();
        final EmojiSpan[] existingSpans = text.getSpans(0, text.length(), EmojiSpan.class);
        final List<Integer> existingSpanPositions = new ArrayList<>(existingSpans.length);

        final int size = existingSpans.length;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < size; i++) {
            existingSpanPositions.add(text.getSpanStart(existingSpans[i]));
        }

        final List<EmojiRange> findAllEmojis = emojiManager.findAllEmojis(text);

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < findAllEmojis.size(); i++) {
            final EmojiRange location = findAllEmojis.get(i);

            if (!existingSpanPositions.contains(location.start)) {
                text.setSpan(new EmojiSpan(context, location.emoji.getResource(), emojiSize),
                        location.start, location.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public EmojiCategory[] getCategories() {
        verifyInstalled();
        return categories; // NOPMD
    }

    Pattern getEmojiRepetitivePattern() {
        return emojiRepetitivePattern;
    }

    @NonNull List<EmojiRange> findAllEmojis(@Nullable final CharSequence text) {
        verifyInstalled();

        final List<EmojiRange> result = new ArrayList<>();

        if (!TextUtils.isEmpty(text)) {
            final Matcher matcher = emojiPattern.matcher(text);

            while (matcher.find()) {
                final Emoji found = findEmoji(text.subSequence(matcher.start(), matcher.end()));

                if (found != null) {
                    result.add(new EmojiRange(matcher.start(), matcher.end(), found));
                }
            }
        }

        return result;
    }

    @Nullable
    public Emoji findEmoji(@NonNull final CharSequence candidate) {
        verifyInstalled();

        // We need to call toString on the candidate, since the emojiMap may not find the requested entry otherwise, because
        // the type is different.
        return emojiMap.get(candidate.toString());
    }

    public void verifyInstalled() {
        if (categories == null) {
            throw new IllegalStateException("Please install an EmojiProvider through the EmojiManager.install() method first.");
        }
    }
}
