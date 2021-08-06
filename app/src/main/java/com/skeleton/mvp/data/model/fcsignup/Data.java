
package com.skeleton.mvp.data.model.fcsignup;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("disallow_workspace_email")
    @Expose
    private List<String> disallowWorkspaceEmail = null;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getDisallowWorkspaceEmail() {
        return disallowWorkspaceEmail;
    }

    public void setDisallowWorkspaceEmail(List<String> disallowWorkspaceEmail) {
        this.disallowWorkspaceEmail = disallowWorkspaceEmail;
    }

}
