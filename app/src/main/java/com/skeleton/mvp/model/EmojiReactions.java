package com.skeleton.mvp.model;

/**
 * Created by rajatdhamija on 12/04/18.
 */

public class EmojiReactions {
    private String name;
    private String emoji;

    public EmojiReactions(String emoji, String name) {
        this.name = name;
        this.emoji = emoji;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
