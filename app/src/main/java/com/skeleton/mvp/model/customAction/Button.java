package com.skeleton.mvp.model.customAction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Button implements Serializable {
    @SerializedName("label")
    @Expose
    private String label = "";

    @SerializedName("action")
    @Expose
    private String action = "";

    @SerializedName("action_type")
    @Expose
    private String actionType = "";

    @SerializedName("data")
    @Expose
    private String data = "";

    @SerializedName("style")
    @Expose
    private String style = "";

    @SerializedName("output")
    @Expose
    private String output = "";

    @SerializedName("id")
    @Expose
    private int id = 0;

    @SerializedName("type_id")
    @Expose
    private String typeId = "0";

    @SerializedName("invite_link")
    @Expose
    private String inviteLink = "";

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }
}