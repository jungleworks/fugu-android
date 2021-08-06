
package com.skeleton.mvp.data.model.signup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The type Data.
 */
public class Data {

    @SerializedName("email")
    @Expose
    private String email;

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

}
