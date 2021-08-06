
package com.skeleton.mvp.data.model.getdomains;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The type Get domains response.
 */
public class GetDomainsResponse {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    /**
     * Gets status code.
     *
     * @return the status code
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * Sets status code.
     *
     * @param statusCode the status code
     */
    public void setStatusCode(final Integer statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public List<Datum> getData() {
        return data;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(final List<Datum> data) {
        this.data = data;
    }

}
