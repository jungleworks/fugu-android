package com.skeleton.mvp.ui.splash;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.skeleton.mvp.R;
import com.skeleton.mvp.ui.base.BaseActivity;


/**
 * Developer: Click Labs
 */

public class SplashActivity extends BaseActivity implements SplashView {

    private static final int REQ_CODE_PLAY_SERVICES_RESOLUTION = 1000;
    private SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashPresenter = new SplashPresenterImpl(this);
        mSplashPresenter.onAttach();
        mSplashPresenter.init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSplashPresenter.onDetach();
    }

    @Override
    public void showNetworkError() {

        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(R.string.error_internet_not_connected)
                .setCancelable(false)
                .setPositiveButton(R.string.text_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mSplashPresenter.registerForFcmToken();
                    }
                })
                .show();
    }


    @Override
    public boolean isPlayServiceAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog mErrorDialog = apiAvailability.getErrorDialog(this, resultCode, REQ_CODE_PLAY_SERVICES_RESOLUTION);
                mErrorDialog.setCanceledOnTouchOutside(false);
                mErrorDialog.setCancelable(false);
                mErrorDialog.show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void showDeviceRootedAlert(final SplashPresenter.RootConfirmationListener rootConfirmationListener) {

        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(R.string.error_device_rooted)
                .setCancelable(false)
                .setPositiveButton(R.string.text_proceed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        rootConfirmationListener.onProceed();
                    }
                })
                .setNegativeButton(R.string.text_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        rootConfirmationListener.onExit();
                    }
                })
                .show();

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // handles play service resolution
        if (requestCode == REQ_CODE_PLAY_SERVICES_RESOLUTION) {
            mSplashPresenter.registerForFcmToken();
        }
    }
}
