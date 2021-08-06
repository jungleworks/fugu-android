package com.easyfilepicker.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.easyfilepicker.FolderListHelper;
import com.easyfilepicker.R;
import com.easyfilepicker.easypermissions.AfterPermissionGranted;
import com.easyfilepicker.easypermissions.AppSettingsDialog;
import com.easyfilepicker.easypermissions.EasyPermissions;

import java.util.List;

import static com.easyfilepicker.Constant.RC_READ_EXTERNAL_STORAGE;

public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = BaseActivity.class.getName();

    protected FolderListHelper mFolderHelper;
    protected boolean isNeedFolderList;
    public static final String IS_NEED_FOLDER_LIST = "isNeedFolderList";

    protected abstract void permissionGranted();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        isNeedFolderList = getIntent().getBooleanExtra(IS_NEED_FOLDER_LIST, false);
//        if (isNeedFolderList) {
//            mFolderHelper = new FolderListHelper();
//            mFolderHelper.initFolderListView(this);
//        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        readExternalStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * Read external storage file
     */
    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    private void readExternalStorage() {
        boolean isGranted = EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
        if (isGranted) {
            permissionGranted();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.hippo_rationale_storage),
                    RC_READ_EXTERNAL_STORAGE, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        permissionGranted();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        // If Permission permanently denied, ask user again
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            if (EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")) {
                permissionGranted();
            } else {
                finish();
            }
        }
    }


}
