package com.skeleton.mvp.model.customAction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomAction implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("buttons")
    @Expose
    private ArrayList<Button> buttons = new ArrayList<>();

    @SerializedName("is_action_taken")
    @Expose
    private boolean isActionTaken = false;

    @SerializedName("show_text_field")
    @Expose
    private boolean showTextField = false;

    @SerializedName("lastClickedButton")
    @Expose
    private Button lastClickedButton;

    @SerializedName("comment")
    @Expose
    private String comment = "";

    @SerializedName("remark")
    @Expose
    private String remark = "";

    @SerializedName("default_text_field")
    @Expose
    private DefaultTextField defaultTextField = null;

    @SerializedName("leave_id")
    @Expose
    private String leaveId = "0";
    @SerializedName("tagged_user_id")
    @Expose
    private Integer taggedUserId=0;

    @SerializedName("confirmation_type")
    @Expose
    private String confirmationType;




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public void setButtons(ArrayList<Button> buttons) {
        this.buttons = buttons;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Button getLastClickedButton() {
        return lastClickedButton;
    }

    public void setLastClickedButton(Button lastClickedButton) {
        this.lastClickedButton = lastClickedButton;
    }

    public boolean isShowTextField() {
        return showTextField;
    }

    public void setShowTextField(boolean showTextField) {
        this.showTextField = showTextField;
    }

    public boolean isActionTaken() {
        return isActionTaken;
    }

    public void setActionTaken(boolean actionTaken) {
        isActionTaken = actionTaken;
    }

    public DefaultTextField getDefaultTextField() {
        return defaultTextField;
    }

    public void setDefaultTextField(DefaultTextField defaultTextField) {
        this.defaultTextField = defaultTextField;
    }

    public String getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }

    public Integer getTaggedUserId() {
        return taggedUserId;
    }

    public void setTaggedUserId(Integer taggedUserId) {
        this.taggedUserId = taggedUserId;
    }

    public String getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(String confirmationType) {
        this.confirmationType = confirmationType;
    }

}
