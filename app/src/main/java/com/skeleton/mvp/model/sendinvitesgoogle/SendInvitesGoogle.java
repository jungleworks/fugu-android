package com.skeleton.mvp.model.sendinvitesgoogle;

public class SendInvitesGoogle {

    private String emailId;
    boolean isChecked;

    public SendInvitesGoogle(String emailId, boolean isChecked){
        this.emailId = emailId;
        this.isChecked = isChecked;
    }

    public void setIsChecked(boolean isChecked){
        this.isChecked = isChecked;
    }
    public String getEmailId(){
        return emailId;
    }

    public Boolean getIsChecked(){
        return isChecked;
    }
}
