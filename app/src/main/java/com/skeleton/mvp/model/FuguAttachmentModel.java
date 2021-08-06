package com.skeleton.mvp.model;

/**
 * Created by bhavya on 16/08/17.
 */

public class FuguAttachmentModel {
    private int imageIcon;
    private String text;
    private int action;
    private int color;
    private boolean listener;

    public FuguAttachmentModel(int imageIcon, String text, int action,int color,boolean listener) {
        this.imageIcon = imageIcon;
        this.text = text;
        this.action = action;
        this.color=color;
        this.listener=listener;
    }

    public int getImageIcon() {
        return imageIcon;
    }

    public String getText() {
        return text;
    }

    public int getAction() {
        return action;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isListener() {
        return listener;
    }
}
