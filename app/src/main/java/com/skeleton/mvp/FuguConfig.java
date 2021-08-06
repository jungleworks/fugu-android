package com.skeleton.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.skeleton.mvp.activity.FuguBaseActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.utils.FuguLog;

import javax.annotation.Nullable;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


/**
 * Created by Bhavya Rattan on 08/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguConfig extends FuguBaseActivity {

    private String TAG = FuguAppConstant.APP_NAME_SHORT + "Config";
    private static FuguConfig fuguConfig;
    protected Context context;
    private Activity activity;
    private boolean isDataCleared = true;

    private int targetSDKVersion = 0;

    public FuguConfig() {

    }

    public void setColorConfig(FuguColorConfig fuguColorConfig) {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath(FONT_REGULAR)
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        CommonData.setColorConfig(fuguColorConfig);
    }

    @Override
    protected void attachBaseContext(final Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public static FuguConfig getInstance() {
        if (fuguConfig == null)
            fuguConfig = new FuguConfig();
        return fuguConfig;
    }

    public static void clearFuguData(@Nullable Activity activity) {
        FuguConfig.getInstance().isDataCleared = true;
        try {
            CommonData.clearData();
        } catch (Exception e) {

        }
    }

    public boolean isDataCleared() {
        return isDataCleared;
    }

    public boolean isPermissionGranted(Context activity, String permission) {

        PackageManager pm = activity.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(FuguConfig.getInstance().activity.getPackageName(), 0);
            if (applicationInfo != null) {
                targetSDKVersion = applicationInfo.targetSdkVersion;
            }
        } catch (Exception e) {

        }

        if (targetSDKVersion > 22) {
            return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return PermissionChecker.checkSelfPermission(activity, permission) == PermissionChecker.PERMISSION_GRANTED;
        }
    }

    public boolean askUserToGrantPermission(Activity activity, String permission, String explanation, int code) {
        FuguLog.e(TAG, "permissions" + permission);
        return askUserToGrantPermission(activity, new String[]{permission}, explanation, code);
    }

    public boolean askUserToGrantPermission(Activity activity, String[] permissions, String explanation, int requestCode) {
        String permissionRequired = null;

        for (String permission : permissions)
            if (!isPermissionGranted(activity, permission)) {
                permissionRequired = permission;
                break;
            }

        // Check if the Permission is ALREADY GRANTED
        if (permissionRequired == null) return true;

        // Check if there is a need to show the PERMISSION DIALOG
        boolean explanationRequired = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionRequired);
        FuguLog.e(TAG, "askUserToGrantPermission: explanationRequired(" + explanationRequired + "): " + permissionRequired);

        // Convey the EXPLANATION if required
        if (explanationRequired) {

            if (explanation == null) explanation = "Please grant permission";
            Toast.makeText(activity, explanation, Toast.LENGTH_SHORT).show();
        } else {

            // We can request the permission, if no EXPLANATIONS required
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }

        return false;
    }
}