package com.skeleton.mvp.ui.onboarding.signin;

import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Developer: Click Labs
 */

public class SignInInteractorImpl implements SignInInteractor {

    @Override
    public void login(final String email, final SignInListener signInListener) {

        Map<String, String> map = new HashMap<>();

        // todo add params and hit api

        // dummy implementation
        RestClient.getApiInterface(true).signIn(map).enqueue(new ResponseResolver<CommonResponse>() {

            @Override
            public void onSuccess(final CommonResponse commonResponse) {
                signInListener.onSignInSuccess(commonResponse);
            }

            @Override
            public void onError(final ApiError error) {
                signInListener.onSignInFailed(error, null);
            }

            @Override
            public void onFailure(final Throwable throwable) {
                signInListener.onSignInFailed(null, throwable);
            }


        });
    }
}
