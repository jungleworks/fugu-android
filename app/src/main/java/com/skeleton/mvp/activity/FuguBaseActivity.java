package com.skeleton.mvp.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.ChatDatabase;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.pushNotification.PushReceiver;
import com.skeleton.mvp.retrofit.APIError;
import com.skeleton.mvp.retrofit.CommonResponse;
import com.skeleton.mvp.retrofit.ResponseResolver;
import com.skeleton.mvp.retrofit.RestClient;
import com.skeleton.mvp.utils.FuguLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Created by rajatdhamija  14/12/17.
 */

public class FuguBaseActivity extends AppCompatActivity implements FuguAppConstant {
    private String TAG = getClass().getSimpleName();
    public static final int READ_WRITE_STORAGE = 52;
    private android.app.ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uncaughtExceptionError();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.getWindow().setStatusBarColor(Color.BLACK);
        }
    }

    @Override
    protected void attachBaseContext(final Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    public boolean requestPermission(String permission) {
        boolean isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    READ_WRITE_STORAGE);
        }
        return isGranted;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        WorkspacesInfo workspacesInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition());
                        ArrayList<Integer> notificationsList = ChatDatabase.INSTANCE.getNotificationsMap().get(workspacesInfo.getBusinessId());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (notificationsList != null) {
                                    for (int notification : notificationsList) {
                                        try {
                                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            nm.cancel(notification);
                                        } catch (Exception e) {

                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            if (nm.getActiveNotifications().length == 1) {
                                                StatusBarNotification[] notifications = nm.getActiveNotifications();
                                                for (StatusBarNotification notifi : notifications) {
                                                    if (notifi.getId() == PushReceiver.PushChannel.INSTANCE.getSUMMARY_NOTIFICATION_ID()) {
                                                        nm.cancel(PushReceiver.PushChannel.INSTANCE.getSUMMARY_NOTIFICATION_ID());
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {

        }
    }

    public void isPermissionGranted(boolean isGranted, String permission) {

    }
    /**
     * Uncaught Exception encountered
     */
    private void uncaughtExceptionError() {
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                        //Do your own error handling here
                        FuguLog.e("unCaughtException paramThread", "---> " + paramThread.toString());
                        FuguLog.e("unCaughtException paramThrowable", "---> " + paramThrowable.toString());
                        StringWriter stackTrace = new StringWriter();
                        paramThrowable.printStackTrace(new PrintWriter(stackTrace));
                        FuguLog.e("unCaughtException stackTrace", "---> " + stackTrace);
                        System.err.println(stackTrace);
                        apiSendError(stackTrace.toString());
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                });
    }

    /**
     * APi to send error messages to server
     *
     * @param logs log to be sent
     */
    public void apiSendError(String logs) {
        if (isNetworkAvailable()) {
            PackageInfo pInfo = null;
            try {
                pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            JSONObject error = new JSONObject();
            try {
                error.put("log", logs);
                if (pInfo != null) {
                    error.put("version", pInfo.versionCode);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, Object> params = new HashMap<>();
            params.put(FuguAppConstant.DEVICE_TYPE, ANDROID_USER);
            params.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(FuguBaseActivity.this));
            params.put(FuguAppConstant.ERROR, error.toString());

            RestClient.getApiInterface().sendError(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, params)
                    .enqueue(new ResponseResolver<CommonResponse>(FuguBaseActivity.this, false, true) {
                        @Override
                        public void success(CommonResponse commonResponse) {
                            FuguLog.v("success", commonResponse.toString());
                        }

                        @Override
                        public void failure(APIError error) {
                            FuguLog.v("failure", error.toString());
                        }
                    });
        }
    }

    /**
     * Check Network Connection
     *
     * @return boolean
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            // close this context and return to preview context (if there is any)
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    protected void showImageEditorLoading(@NonNull String message) {
        mProgressDialog = new android.app.ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideImageEditorLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected void showSnackbar(@NonNull String message) {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
