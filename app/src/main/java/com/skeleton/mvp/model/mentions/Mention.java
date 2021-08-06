package com.skeleton.mvp.model.mentions;

import android.widget.EditText;

import com.percolate.mentions.Mentionable;

/**
 * A mention inserted into the {@link EditText}. All mentions inserted into the
 * {@link EditText} must implement the {@link Mentionable} interface.
 */
public class Mention implements Mentionable {

    private CharSequence mentionName;

    private int offset;

    private int length;

    private long userId;

private String email;

    @Override
    public int getMentionOffset() {
        return offset;
    }

    @Override
    public int getMentionLength() {
        return length;
    }

    @Override
    public CharSequence getMentionName() {
        return mentionName;
    }

    @Override
    public void setMentionOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void setMentionLength(int length) {
        this.length = length;
    }

    @Override
    public void setMentionName(CharSequence mentionName) {
        this.mentionName = mentionName;
    }

    @Override
    public void setUserId(final long userId) {
        this.userId=userId;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public void setEmail(String email) {
        this.email=email;
    }

    @Override
    public String getEmailId() {
        return email;
    }


}
