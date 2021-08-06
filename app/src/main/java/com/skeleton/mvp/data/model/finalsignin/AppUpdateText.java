
package com.skeleton.mvp.data.model.finalsignin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppUpdateText {

    @SerializedName("DEFAULT")
    @Expose
    private String dEFAULT;
    @SerializedName("SOFT_UPDATE")
    @Expose
    private String sOFTUPDATE;
    @SerializedName("HARD_UPDATE")
    @Expose
    private String hARDUPDATE;

    public String getDEFAULT() {
        return dEFAULT;
    }

    public void setDEFAULT(String dEFAULT) {
        this.dEFAULT = dEFAULT;
    }

    public String getSOFTUPDATE() {
        return sOFTUPDATE;
    }

    public void setSOFTUPDATE(String sOFTUPDATE) {
        this.sOFTUPDATE = sOFTUPDATE;
    }

    public String getHARDUPDATE() {
        return hARDUPDATE;
    }

    public void setHARDUPDATE(String hARDUPDATE) {
        this.hARDUPDATE = hARDUPDATE;
    }

}
