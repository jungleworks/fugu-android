package com.skeleton.mvp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatButton;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.UserInfo;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.model.object.ObjectResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.socket.SocketConnection;
import com.skeleton.mvp.ui.UniqueIMEIID;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.util.ValidationUtil;
import java.util.ArrayList;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_DETAILS;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_ID;
import static com.skeleton.mvp.ui.AppConstants.DOMAIN;
import static com.skeleton.mvp.ui.AppConstants.TOKEN;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE_NAME;

/**
 * Rajat Dhamija
 * 29/12/2017
 */
public class CreateWorkspaceActivity extends BaseActivity implements View.OnClickListener {
    private String email;
    private EditText etBusinessName;
    private AppCompatButton btnSignUp;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UserInfo.WhitelabelProperties whitelabelProperties = CommonData.getCommonResponse().data.userInfo.getWhitelabelProperties();

            if (whitelabelProperties != null && !whitelabelProperties.isCreateWorkspaceEnabled()) {
                showErrorMessage(getString(R.string.no_workspace_error) + getString(R.string.support_email_text) + ".", this::finish);
            } else {
                setContentView(R.layout.activity_create_new_workspace);
//        if (getIntent().hasExtra(EXTRA_EMAIL)) {
//            email = getIntent().getStringExtra(EXTRA_EMAIL);
//        }
                initViews();
                clickListeners();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setContentView(R.layout.activity_create_new_workspace);
//        if (getIntent().hasExtra(EXTRA_EMAIL)) {
//            email = getIntent().getStringExtra(EXTRA_EMAIL);
//        }
            initViews();
            clickListeners();
        }
    }

    /**
     * Initialize Views
     */
    private void initViews() {
//        etWorksapceUrl = findViewById(R.id.etWorksapceUrl);
        etBusinessName = findViewById(R.id.etBusinessName);
        btnSignUp = findViewById(R.id.btnSignUp);
//        etWorksapceUrl.addTextChangedListener(new MyTextWatcher());
//        etWorksapceUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    btnSignUp.performClick();
//                }
//                return false;
//            }
//        });
        etBusinessName.requestFocus();
        etBusinessName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = etBusinessName.getText().toString().toLowerCase();
                text = text.replaceAll(" ", "-");
//                etWorksapceUrl.setText(text);
            }
        });
    }

    /**
     * CLick Listeners
     */
    private void clickListeners() {
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                if (!ValidationUtil.checkName(etBusinessName.getText().toString().trim())) {
                    showErrorMessage(R.string.error_please_enter_business_name);
                } else {

                    apiAddBusiness();
                }
                break;
            default:
                break;
        }
    }

    private void apiAddBusiness() {
        showLoading();
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(WORKSPACE_NAME, etBusinessName.getText().toString().trim());
        commonParams.add(TOKEN, CommonData.getFcmToken());
        commonParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        commonParams.add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(CreateWorkspaceActivity.this));
        commonParams.add(DEVICE_DETAILS, CommonData.deviceDetails(CreateWorkspaceActivity.this));
        if(getIntent().hasExtra("isGoogleSignIn") && getIntent().getBooleanExtra("isGoogleSignIn", false)) {
            commonParams.add("is_signup", true);
        }
        RestClient.getApiInterface(true).createWorkspace(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap()).enqueue(new ResponseResolver<ObjectResponse>() {
            @Override
            public void onSuccess(ObjectResponse createWorkspaceResponse) {
                SocketConnection.INSTANCE.disconnectSocket();
                SocketConnection.INSTANCE.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                        createWorkspaceResponse.getData().getEnUserId(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.getUserId(),
                        com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.getUserChannel(), "Create WorkSpace", false,
                        com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.getPushToken());

                FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
                ArrayList<WorkspacesInfo> info = new ArrayList<>();
                if (CommonData.getCommonResponse().getData().getWorkspacesInfo().size() > 0 || (getIntent().hasExtra("isGoogleSignIn") && getIntent().getBooleanExtra("isGoogleSignIn", false))) {
                    info.clear();
                    info.addAll(CommonData.getCommonResponse().getData().getWorkspacesInfo());
                    info.add(createWorkspaceResponse.getData());
                    fcCommonResponse.getData().setWorkspacesInfo(info);
                    CommonData.setCommonResponse(fcCommonResponse);
                    CommonData.setCurrentSignedInPosition(CommonData.getCommonResponse().getData().getWorkspacesInfo().size() - 1);
//                    com.skeleton.mvp.fugudatabase.CommonData.setAppSecretKey(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                    hideLoading();
                    Log.i("boolean", Boolean.toString(getIntent().getBooleanExtra("isGoogleSignIn", false)));
                    if(getIntent().hasExtra("isGoogleSignIn") && getIntent().getBooleanExtra("isGoogleSignIn", false)) {
                        startActivity(new Intent(CreateWorkspaceActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else if (fcCommonResponse.getData().getUserInfo().isHasGoogleContacts() && fcCommonResponse.getData().getWorkspacesInfo().size() == 1) {
                        startActivity(new Intent(CreateWorkspaceActivity.this, SendInvitesActivity.class));
                    } else {
                        startActivity(new Intent(CreateWorkspaceActivity.this, InviteOnboardActivity.class));
                    }
                    finishAffinity();
                } else {
                    info.add(createWorkspaceResponse.getData());
                    CommonData.setCurrentSignedInPosition(0);
                    fcCommonResponse.getData().setWorkspacesInfo(info);
                    CommonData.setCommonResponse(fcCommonResponse);
//                    com.skeleton.mvp.fugudatabase.CommonData.setAppSecretKey(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                    hideLoading();
                    Log.i("boolean", Boolean.toString(getIntent().getBooleanExtra("isGoogleSignIn", false)));


                    if (fcCommonResponse.getData().getUserInfo().isHasGoogleContacts() && fcCommonResponse.getData().getWorkspacesInfo().size() == 1) {
                        startActivity(new Intent(CreateWorkspaceActivity.this, SendInvitesActivity.class));
                    } else {
                        startActivity(new Intent(CreateWorkspaceActivity.this, InviteOnboardActivity.class));
                    }
                    finishAffinity();
                }

            }

            @Override
            public void onError(ApiError error) {
                hideLoading();
                showErrorMessage(error.getMessage());
            }

            @Override
            public void onFailure(Throwable throwable) {
                hideLoading();
            }
        });
    }

    /**
     * Api SignUp Space
     */
//    private void apiSignUpBusiness() {
//        CommonParams commonParams = new CommonParams.Builder()
//                .add(EMAIL, email)
//                .add(WORKSPACE, etWorkspaceUrl.getText().toString().trim())
//                .add(WORKSPACE_NAME, etBusinessName.getText().toString().trim())
//                .build();
//        if (isNetworkConnected()) {
//            RestClient.getApiInterface(true).signUpBusiness(BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap()).enqueue(new ResponseResolver<SignUpResponse>() {
//                @Override
//                public void onSuccess(SignUpResponse signUpResponse) {
//                    Intent otpIntent = new Intent(SignUpActivity.this, OTPActivity.class);
//                    otpIntent.putExtra(EXTRA_EMAIL, signUpResponse.getData().getEmail());
//                    startActivity(otpIntent);
//                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
//                    hideLoading();
//                }
//
//                @Override
//                public void onError(final ApiError error) {
//                    hideLoading();
//                    new CustomAlertDialog.Builder(SignUpActivity.this)
//                            .setMessage(error.getMessage())
//                            .setPositiveButton(R.string.text_ok, new CustomAlertDialog.CustomDialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick() {
//                                    if (error.getStatusCode() == 410) {
//                                        Intent otpIntent = new Intent(SignUpActivity.this, OTPActivity.class);
//                                        otpIntent.putExtra(EXTRA_EMAIL, email);
//                                        startActivity(otpIntent);
//                                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
//                                    }
//                                }
//                            })
//                            .show();
//                }
//
//                @Override
//                public void onFailure(Throwable throwable) {
//
//                }
//            });
//        } else {
//            showErrorMessage(R.string.error_internet_not_connected);
////        }
//    }


    private class MyTextWatcher implements TextWatcher {

        String etHint = getString(R.string.your_workspace);


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    }
}
