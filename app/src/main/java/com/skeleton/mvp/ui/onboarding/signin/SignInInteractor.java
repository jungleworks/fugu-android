package com.skeleton.mvp.ui.onboarding.signin;

import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.network.ApiError;

/**
 * Developer: Click Labs
 */

public interface SignInInteractor {

    /**
     * Do login
     *
     * @param email          the provided email
     * @param signInListener the sign in listener
     */
    void login(String email, SignInListener signInListener);

    /**
     * SignIn listener
     */
    interface SignInListener {

        /**
         * On SignIn success
         *
         * @param commonResponse the parsed common response object
         */
        void onSignInSuccess(CommonResponse commonResponse);

        /**
         * On SignIn failed
         *
         * @param apiError  the parsed api error if any
         * @param throwable the generated throwable if any
         */
        void onSignInFailed(final ApiError apiError, final Throwable throwable);
    }

}
