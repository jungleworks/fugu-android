package com.skeleton.mvp.ui.base;

import com.google.gson.JsonSyntaxException;
import com.skeleton.mvp.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Developer: Click Labs
 */
public class BasePresenterImpl implements BasePresenter {

    private boolean mIsViewAttached;

    @Override
    public void onAttach() {
        mIsViewAttached = true;
    }

    @Override
    public void onDetach() {
        mIsViewAttached = false;
    }

    @Override
    public int parseThrowableMessage(final Throwable cause) {

        if (cause instanceof UnknownHostException) {
            return R.string.error_bad_connection_try_again;
        } else if (cause instanceof SocketTimeoutException) {
            return R.string.error_remote_server_failed;
        } else if (cause instanceof ConnectException) {
            return R.string.error_internet_not_connected;
        } else if (cause instanceof JsonSyntaxException) {
            return R.string.error_paring;
        }
        return R.string.error_unexpected_error;
    }

    /**
     * Checks if the view is attached
     *
     * @return true if view is attached else false
     */
    public boolean isViewAttached() {
        return mIsViewAttached;
    }
}
