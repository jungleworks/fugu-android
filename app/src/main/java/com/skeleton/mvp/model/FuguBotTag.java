package com.skeleton.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FuguBotTag {

    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("input_parameter")
    @Expose
    private String inputParameter = "";
    @SerializedName("description")
    @Expose
    private String description = "";

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getInputParameter() {
        return inputParameter;
    }

    public void setInputParameter(String inputParameter) {
        this.inputParameter = inputParameter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}