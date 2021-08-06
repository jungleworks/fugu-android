package com.skeleton.mvp.model;

/**
 * Created by rajatdhamija on 09/04/18.
 */

public class Emoji {
    private String unicode;
    private String name;

    public Emoji(String unicode, String name) {
        this.unicode = unicode;
        this.name = name;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
