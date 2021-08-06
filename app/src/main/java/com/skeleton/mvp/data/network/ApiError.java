package com.skeleton.mvp.data.network;

import com.google.gson.annotations.SerializedName;

/**
 * Developer: Click Labs
 * <p>
 * Base error model
 */
public class ApiError {

    @SerializedName("statusCode")
    private final int statusCode;

    @SerializedName("message")
    private final String message;

    /**
     * Constructor
     *
     * @param statusCode status code of api error response
     * @param message    message of api error response
     */
    public ApiError(final int statusCode, final String message) {
        this.message = message;
        this.statusCode = statusCode;
    }


    /**
     * Returns the status code of the response
     *
     * @return status code of api error response
     */
    public int getStatusCode() {

        if (statusCode == 0) {
            return ErrorUtils.DEFAULT_STATUS_CODE;
        }
        return statusCode;
    }


    /**
     * Returns the message of the response
     *
     * @return the error message
     */
    public String getMessage() {

        if (message == null) {
            return "";
        } else {
            return message;
        }
    }
}
