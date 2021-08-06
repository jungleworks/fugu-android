package com.skeleton.mvp.ui.onboarding.signin;


import androidx.annotation.NonNull;

import com.skeleton.mvp.R;
import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.ui.base.BasePresenterImpl;
import com.skeleton.mvp.util.ValidationUtil;

/**
 * Developer: Click Labs
 */

public class SignInPresenterImpl extends BasePresenterImpl implements SignInPresenter {

    private SignInView mSignInView;
    private SignInInteractor mSignInInteractor;

    /**
     * Constructor
     *
     * @param signInView the associated SignIn view
     */
    SignInPresenterImpl(@NonNull final SignInView signInView) {
        mSignInView = signInView;
        mSignInInteractor = new SignInInteractorImpl();

    }

    @Override
    public void onSignInClicked(final String email) {

        // checking for validation
        if (!ValidationUtil.checkEmail(email)) {
            mSignInView.showErrorMessage(R.string.error_invalid_email);
            return;
        }


        mSignInView.showLoading();
        mSignInInteractor.login(email, new SignInInteractor.SignInListener() {
            @Override
            public void onSignInSuccess(final CommonResponse commonResponse) {
                //todo handle success
            }

            @Override
            public void onSignInFailed(final ApiError apiError, final Throwable throwable) {

                if (isViewAttached()) {
                    mSignInView.hideLoading();
                    if (apiError != null) {
                        mSignInView.showErrorMessage(apiError.getMessage());
                    } else {
                        // resolve error through throwable
                        mSignInView.showErrorMessage(parseThrowableMessage(throwable));

                    }
                }
            }
        });
    }


}
