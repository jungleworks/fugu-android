package com.skeleton.mvp.ui.base;

import com.skeleton.mvp.data.network.ApiError;

/**
 * Developer: Click Labs
 */
public interface BaseView {

    /**
     * Checks if network connection is available
     *
     * @return if connectivity is available
     */
    boolean isNetworkConnected();

    /**
     * Show loader
     */
    void showLoading();

    /**
     * Show loader with message
     *
     * @param message message which display with loading dialog
     */
    void showLoading(String message);

    /**
     * Hide loader
     */
    void hideLoading();

    /**
     * Show error message
     *
     * @param stringId the message id
     */
    void showErrorMessage(int stringId);

    void showErrorMessage(int stringId,OnErrorHandleCallback onErrorHandleCallback);


    /**
     * Show error message.
     *
     * @param apiError the api error
     */
    void showErrorMessage(ApiError apiError);

    /**
     * Show error message.
     *
     * @param apiError               the api error
     * @param mOnErrorHandleCallback the m on error handle callback
     */
    void showErrorMessage(ApiError apiError, OnErrorHandleCallback mOnErrorHandleCallback, OnPositiveButtonCallback onPositiveButtonCallback, String PositiveButton, String NegativeButton);

    /**
     * Show error message
     *
     * @param message the message to show
     */
    void showErrorMessage(String message);
    void showErrorMessage(String message,OnErrorHandleCallback onErrorHandleCallback);

    /**
     * Show error message.
     *
     * @param message                the message
     * @param mOnErrorHandleCallback the m on error handle callback
     */
    void showErrorMessage(String message, OnErrorHandleCallback mOnErrorHandleCallback, OnPositiveButtonCallback onPositiveButtonCallback, String PositiveButton, String NegativeButton);

    /**
     * The interface On error handle callback.
     */
    interface OnErrorHandleCallback {
        /**
         * On error callback.
         */
        void onErrorCallback();
    }

    interface OnPositiveButtonCallback {
        void onPositiveButtonClick();
    }
}
