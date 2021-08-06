package com.percolate.mentions;

import android.text.Editable;

/**
 * Used for testing inserting, deletion and highlighting of mentions.
 */
class Mention implements Mentionable {

    private Editable mentionName;

    private int offset;

    private int length;

    private long userId;

    @Override
    public int getMentionOffset() {
        return offset;
    }

    @Override
    public int getMentionLength() {
        return length;
    }

    @Override
    public Editable getMentionName() {
        return mentionName;
    }

    @Override
    public void setMentionName(CharSequence mentionName) {

    }

    @Override
    public void setMentionOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void setMentionLength(int length) {
        this.length = length;
    }

//    @Override
//    public void setMentionName(Editable mentionName) {
//        this.mentionName = mentionName;
//    }

    @Override
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public void setEmail(String email) {

    }

    @Override
    public String getEmailId() {
        return null;
    }
}
