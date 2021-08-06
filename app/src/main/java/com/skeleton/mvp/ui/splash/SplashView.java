package com.skeleton.mvp.ui.splash;


import com.skeleton.mvp.ui.base.BaseView;

/**
 * Developer: Click Labs
 */
public interface SplashView extends BaseView {

    /**
     * Show network not available error
     */
    void showNetworkError();

    /**
     * Checks for play-service availability
     *
     * @return true if play services available else false
     */
    boolean isPlayServiceAvailable();

    /**
     * Exit splash
     */
    void exit();

    /**
     * Show device rooted alert and allows the user to either proceed or exit
     *
     * @param rootConfirmationListener the root confirmation listener
     */
    void showDeviceRootedAlert(final SplashPresenter.RootConfirmationListener rootConfirmationListener);
}
