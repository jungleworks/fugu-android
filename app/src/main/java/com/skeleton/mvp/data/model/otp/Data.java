
package com.skeleton.mvp.data.model.otp;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The type Data.
 */
public class Data {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("otp")
    @Expose
    private String otp;

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets otp.
     *
     * @return the otp
     */
    public String getOtp() {
        return otp;
    }

    /**
     * Sets otp.
     *
     * @param otp the otp
     */
    public void setOtp(final String otp) {
        this.otp = otp;
    }

}