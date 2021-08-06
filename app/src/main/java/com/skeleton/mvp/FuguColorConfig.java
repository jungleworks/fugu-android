package com.skeleton.mvp;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

/**
 * Created by bhavya on 01/08/17.
 */

public class FuguColorConfig {

    public int getFuguActionBarBg() {
        return Color.parseColor(fuguActionBarBg);
    }

    public int getFuguActionBarText() {
        return Color.parseColor(fuguActionBarText);
    }

    public int getFuguBgMessageYou() {
        return Color.parseColor(fuguBgMessageYou);
    }

    public int getFuguBgMessageFrom() {
        return Color.parseColor(fuguBgMessageFrom);
    }

    public int getFuguPrimaryTextMsgYou() {
        return Color.parseColor(fuguPrimaryTextMsgYou);
    }

    public int getFuguMessageRead() {
        return Color.parseColor(fuguMessageRead);
    }

    public int getFuguPrimaryTextMsgFrom() {
        return Color.parseColor(fuguPrimaryTextMsgFrom);
    }

    public int getFuguSecondaryTextMsgYou() {
        return Color.parseColor(fuguSecondaryTextMsgYou);
    }

    public int getFuguSecondaryTextMsgFrom() {
        return Color.parseColor(fuguSecondaryTextMsgFrom);
    }

    public int getFuguSecondaryTextMsgFromName() {
        return Color.parseColor(fuguSecondaryTextMsgFromName);
    }

    public int getFuguTextColorPrimary() {
        return Color.parseColor(fuguTextColorPrimary);
    }

    public int getFuguChannelDateText() {
        return Color.parseColor(fuguChannelDateText);
    }

    public int getFuguChatBg() {
        return Color.parseColor(fuguChatBg);
    }

    public int getFuguBorderColor() {
        return Color.parseColor(fuguBorderColor);
    }

    public int getFuguChatDateText() {
        return Color.parseColor(fuguChatDateText);
    }

    public int getFuguThemeColorPrimary() {
        return Color.parseColor(fuguThemeColorPrimary);
    }

    public int getFuguThemeColorSecondary() {
        return Color.parseColor(fuguThemeColorSecondary);
    }

    public int getFuguTypeMessageBg() {
        return Color.parseColor(fuguTypeMessageBg);
    }

    public int getFuguTypeMessageHint() {
        return Color.parseColor(fuguTypeMessageHint);
    }

    public int getFuguTypeMessageText() {
        return Color.parseColor(fuguTypeMessageText);
    }

    public int getFuguChannelBg() {
        return Color.parseColor(fuguChannelBg);
    }

    public int getFuguChannelItemBgPressed() {
        return Color.parseColor(fuguChannelItemBgPressed);
    }

    public int getFuguChannelItemBg() {
        return Color.parseColor(fuguChannelItemBg);
    }

    private String fuguActionBarBg = "#2496ff";
    private String fuguActionBarText = "#ffffff";
    private String fuguBgMessageYou = "#f9f9f9";
    private String fuguBgMessageFrom = "#ffffff";
    private String fuguPrimaryTextMsgYou = "#2c2333";
    private String fuguMessageRead = "#2496ff";
    private String fuguPrimaryTextMsgFrom = "#2c2333";
    private String fuguSecondaryTextMsgYou = "#5c5c5c";
    private String fuguSecondaryTextMsgFrom = "#a7a5b5";
    private String fuguSecondaryTextMsgFromName = "#2496ff";
    private String fuguTextColorPrimary = "#2c2333";
    private String fuguChannelDateText = "#88838c";
    private String fuguChatBg = "#f1f3f8";
    private String fuguBorderColor = "#dce0e6";
    private String fuguChatDateText = "#51445c";
    private String fuguThemeColorPrimary = "#2496ff";
    private String fuguThemeColorSecondary = "#6cc64d";
    private String fuguTypeMessageBg = "#ffffff";
    private String fuguTypeMessageHint = "#8e8e8e";
    private String fuguTypeMessageText = "#2c2333";
    private String fuguChannelBg = "#ffffff";
    private String fuguChannelItemBg = "#ffffff";
    private String fuguChannelItemBgPressed = "#668e8e8e";
    private String fuguMessageColor = "#9E9E9E";

    public static class Builder {
        private FuguColorConfig fuguColorConfig = new FuguColorConfig();

        public Builder fuguActionBarBg(String fuguActionBarBg) {
            fuguColorConfig.fuguActionBarBg = fuguActionBarBg;
            return this;
        }

        public Builder fuguActionBarText(String fuguActionBarText) {
            fuguColorConfig.fuguActionBarText = fuguActionBarText;
            return this;
        }

        public Builder fuguBgMessageYou(String fuguBgMessageYou) {
            fuguColorConfig.fuguBgMessageYou = fuguBgMessageYou;
            return this;
        }

        public Builder fuguBgMessageFrom(String fuguBgMessageFrom) {
            fuguColorConfig.fuguBgMessageFrom = fuguBgMessageFrom;
            return this;
        }

        public Builder fuguPrimaryTextMsgYou(String fuguPrimaryTextMsgYou) {
            fuguColorConfig.fuguPrimaryTextMsgYou = fuguPrimaryTextMsgYou;
            return this;
        }

        public Builder fuguMessageRead(String fuguMessageRead) {
            fuguColorConfig.fuguMessageRead = fuguMessageRead;
            return this;
        }

        public Builder fuguPrimaryTextMsgFrom(String fuguPrimaryTextMsgFrom) {
            fuguColorConfig.fuguPrimaryTextMsgFrom = fuguPrimaryTextMsgFrom;
            return this;
        }

        public Builder fuguSecondaryTextMsgYou(String fuguSecondaryTextMsgYou) {
            fuguColorConfig.fuguSecondaryTextMsgYou = fuguSecondaryTextMsgYou;
            return this;
        }

        public Builder fuguSecondaryTextMsgFrom(String fuguSecondaryTextMsgFrom) {
            fuguColorConfig.fuguSecondaryTextMsgFrom = fuguSecondaryTextMsgFrom;
            return this;
        }

        public Builder fuguSecondaryTextMsgFromName(String fuguSecondaryTextMsgFromName) {
            fuguColorConfig.fuguSecondaryTextMsgFromName = fuguSecondaryTextMsgFromName;
            return this;
        }

        public Builder fuguTextColorPrimary(String fuguTextColorPrimary) {
            fuguColorConfig.fuguTextColorPrimary = fuguTextColorPrimary;
            return this;
        }

        public Builder fuguChannelDateText(String fuguChannelDateText) {
            fuguColorConfig.fuguChannelDateText = fuguChannelDateText;
            return this;
        }

        public Builder fuguChatBg(String fuguChatBg) {
            fuguColorConfig.fuguChatBg = fuguChatBg;
            return this;
        }

        public Builder fuguBorderColor(String fuguBorderColor) {
            fuguColorConfig.fuguBorderColor = fuguBorderColor;
            return this;
        }

        public Builder fuguChatDateText(String fuguChatDateText) {
            fuguColorConfig.fuguChatDateText = fuguChatDateText;
            return this;
        }

        public Builder fuguThemeColorPrimary(String fuguThemeColorPrimary) {
            fuguColorConfig.fuguThemeColorPrimary = fuguThemeColorPrimary;
            return this;
        }

        public Builder fuguThemeColorSecondary(String fuguThemeColorSecondary) {
            fuguColorConfig.fuguThemeColorSecondary = fuguThemeColorSecondary;
            return this;
        }

        public Builder fuguTypeMessageBg(String fuguTypeMessageBg) {
            fuguColorConfig.fuguTypeMessageBg = fuguTypeMessageBg;
            return this;
        }

        public Builder fuguTypeMessageHint(String fuguTypeMessageHint) {
            fuguColorConfig.fuguTypeMessageHint = fuguTypeMessageHint;
            return this;
        }

        public Builder fuguTypeMessageText(String fuguTypeMessageText) {
            fuguColorConfig.fuguTypeMessageText = fuguTypeMessageText;
            return this;
        }

        public Builder fuguChannelBg(String fuguChannelBg) {
            fuguColorConfig.fuguChannelBg = fuguChannelBg;
            return this;
        }

        public Builder fuguChannelItemBgPressed(String fuguChannelItemBgPressed) {
            fuguColorConfig.fuguChannelItemBgPressed = fuguChannelItemBgPressed;
            return this;
        }

        public Builder fuguChannelItemBg(String fuguChannelItemBg) {
            fuguColorConfig.fuguChannelItemBg = fuguChannelItemBg;
            return this;
        }

        public Builder fuguMessageColor(String fuguMessageColor) {
            fuguColorConfig.fuguMessageColor = fuguMessageColor;
            return this;
        }

        public FuguColorConfig build() {
            return fuguColorConfig;
        }

    }

    public static StateListDrawable makeSelector(int color, int colorPressed) {
        StateListDrawable res = new StateListDrawable();
        // res.setExitFadeDuration(400);
        //res.setAlpha(230);
        res.addState(new int[]{android.R.attr.state_pressed}, roundedBackground(0, colorPressed, false));
        res.addState(new int[]{}, roundedBackground(0, color, false));
        return res;
    }

    public static StateListDrawable makeRoundedSelector(int color) {
        StateListDrawable res = new StateListDrawable();
        // res.setExitFadeDuration(400);
        //res.setAlpha(230);
        res.addState(new int[]{android.R.attr.state_pressed}, roundedBackground(150, color, true));
        res.addState(new int[]{}, roundedBackground(150, color, false));
        return res;
    }

    public int getFuguMessageColor() {
        return Color.parseColor(fuguMessageColor);
    }

    private static ShapeDrawable roundedBackground(float radius, int color, boolean isPressed) {
        ShapeDrawable footerBackground = new ShapeDrawable();

        // The corners are ordered top-left, top-right, bottom-right,
        // bottom-left. For each corner, the array contains 2 values, [X_radius,
        // Y_radius]

        float[] radii = new float[8];
        radii[0] = radius;
        radii[1] = radius;

        radii[2] = radius;
        radii[3] = radius;

        radii[4] = radius;
        radii[5] = radius;

        radii[6] = radius;
        radii[7] = radius;


        footerBackground.setShape(new RoundRectShape(radii, null, null));

        footerBackground.getPaint().setColor(color);
        if (isPressed)
            footerBackground.setAlpha(250);

        return footerBackground;
    }

    public void setFuguMessageColor(String fuguMessageColor) {
        this.fuguMessageColor = fuguMessageColor;
    }
}
