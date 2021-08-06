package com.skeleton.mvp.model.customAction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DefaultTextField implements Serializable {

    @SerializedName("hint")
    @Expose
    private String hint;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("output")
    @Expose
    private String output;
    @SerializedName("is_required")
    @Expose
    private Boolean isRequired = false;
    @SerializedName("minimum_length")
    @Expose
    private Integer minimumLength = 0;
    @SerializedName("id")
    @Expose
    private int button_id = 0;

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(Integer minimumLength) {
        this.minimumLength = minimumLength;
    }

    public int getButton_id() {
        return button_id;
    }

    public void setButton_id(int button_id) {
        this.button_id = button_id;
    }
}
