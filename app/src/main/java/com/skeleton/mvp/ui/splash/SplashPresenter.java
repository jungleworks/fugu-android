package com.skeleton.mvp.ui.splash;

import com.skeleton.mvp.ui.base.BasePresenter;

/**
 * Developer: Click Labs
 */

public interface SplashPresenter extends BasePresenter {

    /**
     * Init the splash presenter
     */
    void init();

    /**
     * Registers for fcm token
     */
    void registerForFcmToken();

    /**
     * Root confirmation listener. The user can either
     * choose to proceed or quit
     */
    interface RootConfirmationListener {

        /**
         * On proceed despite root
         */
        void onProceed();

        /**
         * On exit
         */
        void onExit();

    }
}
