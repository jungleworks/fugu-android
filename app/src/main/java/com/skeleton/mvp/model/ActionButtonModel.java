package com.skeleton.mvp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 12/15/17.
 */

public class ActionButtonModel {

    @SerializedName("button_text")
    private String buttonText;

    @SerializedName("button_action")
    private Object buttonAction;

    public Object getButtonAction() {
        return buttonAction;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(final String buttonText) {
        this.buttonText = buttonText;
    }


}
