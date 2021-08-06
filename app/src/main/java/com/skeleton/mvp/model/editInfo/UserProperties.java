
package com.skeleton.mvp.model.editInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProperties {

    @SerializedName("enable_vibration")
    @Expose
    private Boolean enableVibration;
    @SerializedName("push_notification_sound")
    @Expose
    private String pushNotificationSound;

    public Boolean getEnableVibration() {
        return enableVibration;
    }

    public void setEnableVibration(Boolean enableVibration) {
        this.enableVibration = enableVibration;
    }

    public String getPushNotificationSound() {
        return pushNotificationSound;
    }

    public void setPushNotificationSound(String pushNotificationSound) {
        this.pushNotificationSound = pushNotificationSound;
    }

}
