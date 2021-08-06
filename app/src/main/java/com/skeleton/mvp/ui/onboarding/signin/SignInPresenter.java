package com.skeleton.mvp.ui.onboarding.signin;

import com.skeleton.mvp.ui.base.BasePresenter;

/**
 * Developer: Click Labs
 */

public interface SignInPresenter extends BasePresenter {

    /**
     * On SignIn clicked
     *
     * @param email    the provided email
     */
    void onSignInClicked(final String email);
}
