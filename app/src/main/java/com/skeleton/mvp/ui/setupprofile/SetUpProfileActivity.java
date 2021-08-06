package com.skeleton.mvp.ui.setupprofile;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.editprofile.EditProfileResponse;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.MultipartParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.intro.IntroActivity;
import com.skeleton.mvp.util.ValidationUtil;

import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.FULL_NAME;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE_ID;

public class SetUpProfileActivity extends BaseActivity implements View.OnClickListener {
    private EditText etFullName;
    private AppCompatButton btnSave;
    private FcCommonResponse fcCommonResponse;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);
        etFullName = findViewById(R.id.etFullName);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        fcCommonResponse = CommonData.getCommonResponse();
        etFullName.requestFocus();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                if (ValidationUtil.checkName(etFullName.getText().toString())) {
                    showLoading();
                    if (isNetworkConnected()) {
                        apiEditInfo();
                    } else {
                        showErrorMessage(R.string.error_internet_not_connected);
                    }
                } else {
                    showErrorMessage(getString(R.string.please_enter_your_full_name));
                }
                break;
            default:
                break;
        }
    }

    private void apiEditInfo() {
        MultipartParams commonParams = new MultipartParams.Builder()
                .add(FULL_NAME, etFullName.getText().toString())
                .add(EMAIL, fcCommonResponse.getData().getUserInfo().getEmail())
                .add(WORKSPACE_ID, fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId())
                .build();
        RestClient.getApiInterface(true).editProfile(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap())
                .enqueue(new ResponseResolver<EditProfileResponse>() {
                    @Override
                    public void onSuccess(EditProfileResponse editProfileResponse) {
                        hideLoading();
                        fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).setFullName(etFullName.getText().toString());
                        CommonData.setCommonResponse(fcCommonResponse);
                        for (int j = 0; j < fcCommonResponse.getData().getWorkspacesInfo().size(); j++) {
                            com.skeleton.mvp.fugudatabase.CommonData.setFullName(fcCommonResponse.getData().getWorkspacesInfo().get(j).getFuguSecretKey(),
                                    fcCommonResponse.getData().getWorkspacesInfo().get(j).getFullName());
                        }
                        finish();
                        startActivity(new Intent(SetUpProfileActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }

                    @Override
                    public void onError(ApiError error) {
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(SetUpProfileActivity.this);
                            finishAffinity();
                            startActivity(new Intent(SetUpProfileActivity.this, IntroActivity.class));
                        } else {
                            showErrorMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }
}
