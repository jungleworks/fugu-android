package com.percolate.mentions;

import android.widget.EditText;

/**
 * An interface that all models need to inherit. It contains methods for setting the offset,
 * length and mention name. It is used to keep track all the mentions internally in the library.
 */
public interface Mentionable {

    /**
     * Get mentions' start location.
     */
    int getMentionOffset();

    /**
     * Set mentions' start location.
     *
     * @param offset int     The starting locating of the mention in the {@link EditText}.
     */
    void setMentionOffset(final int offset);

    /**
     * Get length of mention.
     */
    int getMentionLength();

    /**
     * Set mentions' length.
     *
     * @param length int     The length of the mention in the {@link EditText}.
     */
    void setMentionLength(final int length);

    /**
     * Get mentions' display name.
     */
    CharSequence getMentionName();

    /**
     * Set mentions' display name.
     */
    void setMentionName(final CharSequence mentionName);

    void setUserId(final long userId);

    long getUserId();

    void setEmail(final String email);

    String getEmailId();

}
