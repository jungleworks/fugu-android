
package com.skeleton.mvp.data.model.invitation;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The type Data.
 */
public class Data {

    @SerializedName("success")
    @Expose
    private List<String> success = null;
    @SerializedName("alreadyRegistered")
    @Expose
    private List<Object> alreadyRegistered = null;

    /**
     * Gets success.
     *
     * @return the success
     */
    public List<String> getSuccess() {
        return success;
    }

    /**
     * Sets success.
     *
     * @param success the success
     */
    public void setSuccess(final List<String> success) {
        this.success = success;
    }

    /**
     * Gets already registered.
     *
     * @return the already registered
     */
    public List<Object> getAlreadyRegistered() {
        return alreadyRegistered;
    }

    /**
     * Sets already registered.
     *
     * @param alreadyRegistered the already registered
     */
    public void setAlreadyRegistered(final List<Object> alreadyRegistered) {
        this.alreadyRegistered = alreadyRegistered;
    }

}
