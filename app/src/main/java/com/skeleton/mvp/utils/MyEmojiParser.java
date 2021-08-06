package com.skeleton.mvp.utils;

/**
 * Created by rajatdhamija on 28/04/18.
 */

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.EmojiTrie;
import com.vdurmont.emoji.Fitzpatrick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vdurmont.emoji.EmojiParser.parseFromUnicode;

/**
 * Provides methods to parse strings with emojis.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class MyEmojiParser {
    private static final Pattern ALIAS_CANDIDATE_PATTERN =
            Pattern.compile("(?<=:)\\+?(\\w|\\||\\-)+(?=:)");

    protected static List<AliasCandidate> getAliasCandidates(String input) {
        List<AliasCandidate> candidates = new ArrayList<AliasCandidate>();
        Matcher matcher = ALIAS_CANDIDATE_PATTERN.matcher(input);
        matcher = matcher.useTransparentBounds(true);
        while (matcher.find()) {
            String match = matcher.group();
            if (!match.contains("|")) {
                candidates.add(new AliasCandidate(match, match, null));
            } else {
                String[] splitted = match.split("\\|");
                if (splitted.length == 2 || splitted.length > 2) {
                    candidates.add(new AliasCandidate(match, splitted[0], splitted[1]));
                } else {
                    candidates.add(new AliasCandidate(match, match, null));
                }
            }
        }
        return candidates;
    }

    /**
     * Removes all emojis from a String
     *
     * @param str the string to process
     * @return the string without any emoji
     */
    public static String removeAllEmojis(String str) {
        EmojiParser.EmojiTransformer emojiTransformer = new EmojiParser.EmojiTransformer() {
            public String transform(EmojiParser.UnicodeCandidate unicodeCandidate) {
                return "";
            }
        };
        return parseFromUnicode(str, emojiTransformer);
    }
    public static List<String> extractEmojis(String input) {
        List<UnicodeCandidate> emojis = getUnicodeCandidates(input);
        List<String> result = new ArrayList<String>();
        for (UnicodeCandidate emoji : emojis) {
            result.add(emoji.getEmoji().getUnicode());
        }
        return result;
    }


    /**
     * Generates a list UnicodeCandidates found in input string. A
     * UnicodeCandidate is created for every unicode emoticon found in input
     * string, additionally if Fitzpatrick modifier follows the emoji, it is
     * included in UnicodeCandidate. Finally, it contains start and end index of
     * unicode emoji itself (WITHOUT Fitzpatrick modifier whether it is there or
     * not!).
     *
     * @param input String to find all unicode emojis in
     * @return List of UnicodeCandidates for each unicode emote in text
     */
    public static List<UnicodeCandidate> getUnicodeCandidates(String input) {
        char[] inputCharArray = input.toCharArray();
        List<UnicodeCandidate> candidates = new ArrayList<UnicodeCandidate>();
        UnicodeCandidate next;
        for (int i = 0; (next = getNextUnicodeCandidate(inputCharArray, i)) != null; i = next.getFitzpatrickEndIndex()) {
            candidates.add(next);
        }

        return candidates;
    }

    /**
     * Finds the next UnicodeCandidate after a given starting index
     *
     * @param chars char array to find UnicodeCandidate in
     * @param start starting index for search
     * @return the next UnicodeCandidate or null if no UnicodeCandidate is found after start index
     */
    private static UnicodeCandidate getNextUnicodeCandidate(char[] chars, int start) {
        for (int i = start; i < chars.length; i++) {
            int emojiEnd = getEmojiEndPos(chars, i);

            if (emojiEnd != -1) {
                Emoji emoji = EmojiManager.getByUnicode(new String(chars, i, emojiEnd - i));
                String fitzpatrickString = (emojiEnd + 2 <= chars.length) ?
                        new String(chars, emojiEnd, 2) :
                        null;
                return new UnicodeCandidate(
                        emoji,
                        fitzpatrickString,
                        i
                );
            }
        }

        return null;
    }


    /**
     * Returns end index of a unicode emoji if it is found in text starting at
     * index startPos, -1 if not found.
     * This returns the longest matching emoji, for example, in
     * "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66"
     * it will find alias:family_man_woman_boy, NOT alias:man
     *
     * @param text     the current text where we are looking for an emoji
     * @param startPos the position in the text where we should start looking for
     *                 an emoji end
     * @return the end index of the unicode emoji starting at startPos. -1 if not
     * found
     */
    private static int getEmojiEndPos(char[] text, int startPos) {
        int best = -1;
        for (int j = startPos + 1; j <= text.length; j++) {
            EmojiTrie.Matches status = EmojiManager.isEmoji(Arrays.copyOfRange(
                    text,
                    startPos,
                    j
            ));

            if (status.exactMatch()) {
                best = j;
            } else if (status.impossibleMatch()) {
                return best;
            }
        }

        return best;
    }


    public static class UnicodeCandidate {
        private final Emoji emoji;
        private final Fitzpatrick fitzpatrick;
        private final int startIndex;

        private UnicodeCandidate(Emoji emoji, String fitzpatrick, int startIndex) {
            this.emoji = emoji;
            this.fitzpatrick = Fitzpatrick.fitzpatrickFromUnicode(fitzpatrick);
            this.startIndex = startIndex;
        }

        public Emoji getEmoji() {
            return emoji;
        }

        public boolean hasFitzpatrick() {
            return getFitzpatrick() != null;
        }

        public Fitzpatrick getFitzpatrick() {
            return fitzpatrick;
        }

        public String getFitzpatrickType() {
            return hasFitzpatrick() ? fitzpatrick.name().toLowerCase() : "";
        }

        public String getFitzpatrickUnicode() {
            return hasFitzpatrick() ? fitzpatrick.unicode : "";
        }

        public int getEmojiStartIndex() {
            return startIndex;
        }

        public int getEmojiEndIndex() {
            return startIndex + emoji.getUnicode().length();
        }

        public int getFitzpatrickEndIndex() {
            return getEmojiEndIndex() + (fitzpatrick != null ? 2 : 0);
        }
    }


    protected static class AliasCandidate {
        final String fullString;
        final String alias;
        final Fitzpatrick fitzpatrick;

        private AliasCandidate(
                String fullString,
                String alias,
                String fitzpatrickString
        ) {
            this.fullString = fullString;
            this.alias = alias;
            if (fitzpatrickString == null) {
                this.fitzpatrick = null;
            } else {
                this.fitzpatrick = Fitzpatrick.fitzpatrickFromType(fitzpatrickString);
            }
        }
    }

    public interface EmojiTransformer {
        String transform(UnicodeCandidate unicodeCandidate);
    }
}
