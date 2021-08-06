package com.skeleton.mvp.data.network;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Developer: Click Labs
 *
 * Error Utils
 */
final class ErrorUtils {

    static final int DEFAULT_STATUS_CODE = 900;

    /**
     * Prevent instantiation
     */
    private ErrorUtils() {
    }

    /**
     * Parses error from the api response
     *
     * @param response the api response
     * @return parsed instance of ApiError
     */
    static ApiError parseError(final Response<?> response) {
        Converter<ResponseBody, ApiError> converter = RestClient.getRetrofitBuilder().responseBodyConverter(ApiError.class, new Annotation[0]);
        ApiError error;
        try {
            if (response.errorBody() != null) {
                error = converter.convert(response.errorBody());
            } else {
                error = new ApiError(response.code(), response.message());
            }

        } catch (Exception e) {
            int statusCode = DEFAULT_STATUS_CODE;
            // keeping empty string as we cannot reference direct strings here
            String message = "";
            if (response.code() != 0) {
                statusCode = response.code();
            }
            if (response.message() != null && !response.message().isEmpty()) {
                message = response.message();
            }

            return new ApiError(statusCode, message);
        }
        return error;
    }
}
