
package com.skeleton.mvp.data.model.fcCommon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppUpdateConfig {

    @SerializedName("app_update_message")
    @Expose
    private String appUpdateMessage;
    @SerializedName("app_link")
    @Expose
    private String appLink;
    @SerializedName("app_update_text")
    @Expose
    private String appUpdateText;

    public String getAppUpdateMessage() {
        return appUpdateMessage;
    }

    public void setAppUpdateMessage(String appUpdateMessage) {
        this.appUpdateMessage = appUpdateMessage;
    }

    public String getAppLink() {
        return appLink;
    }

    public void setAppLink(String appLink) {
        this.appLink = appLink;
    }

    public String getAppUpdateText() {
        return appUpdateText;
    }

    public void setAppUpdateText(String appUpdateText) {
        this.appUpdateText = appUpdateText;
    }

}
