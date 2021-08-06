package com.skeleton.mvp.ui.onboarding.signin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.CreateWorkspaceActivity;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.reset.ResetActivity;
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity;
import com.skeleton.mvp.util.ValidationUtil;
import com.skeleton.mvp.utils.FuguUtils;
import com.skeleton.mvp.utils.UniqueIMEIID;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_ID;
import static com.skeleton.mvp.ui.AppConstants.DOMAIN;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.PASSWORD;
import static com.skeleton.mvp.ui.AppConstants.TOKEN;


/**
 * Developer: Rajat Dhamija
 * 02/01/2018
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private EditText etEmail, etPassword;
    private CountryCodePicker etCountryCode;
    private AppCompatButton btnSignin;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        init();
        etPassword.requestFocus();
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String regexStr = "^[0-9]*$";
                if (s.length() > 0) {
                    if (s.toString().matches(regexStr)) {
                        etCountryCode.setVisibility(View.VISIBLE);
                        etEmail.setPadding(0, 18, 7, 18);
                    } else {
                        etCountryCode.setVisibility(View.GONE);
                        etEmail.setPadding(40, 18, 7, 18);
                    }
                } else if (s.length() == 0) {
                    etCountryCode.setVisibility(View.GONE);
                    etEmail.setPadding(40, 18, 7, 18);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Init the views
     */
    private void init() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignin = findViewById(R.id.btnSignIn);
        etCountryCode = findViewById(R.id.etCountryCode);
        btnSignin.setOnClickListener(this);
        findViewById(R.id.tvForgot).setOnClickListener(this);
        etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btnSignin.performClick();
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                //if ((etCountryCode.getVisibility() == View.VISIBLE) && !etEmail.getText().toString().trim().matches("^[0-9]*$")) {
                    //showErrorMessage("Please enter valid phone number");
                //} else if (!(etCountryCode.getVisibility() == View.VISIBLE) && !ValidationUtil.checkEmail(etEmail.getText().toString().trim())) {
                   // showErrorMessage("Please enter valid email/phone number");
                //}
                if (!ValidationUtil.checkPassword(etPassword.getText().toString().trim())) {
                    showErrorMessage(R.string.error_invalid_password);
                } else {
                    showLoading();
                    if (isNetworkConnected()) {
                        apiSignIn();
                    } else {
                        showErrorMessage(R.string.error_internet_not_connected);
                    }
                }
                break;
            case R.id.tvForgot:
                startActivity(new Intent(SignInActivity.this, ResetActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private void apiSignIn() {
        String value = "";
        if (TextUtils.isEmpty(CommonData.getFcmToken())) {
            String token = FirebaseInstanceId.getInstance().getToken();
            CommonData.updateFcmToken(token);
//            FuguNotificationConfig.updateFcmRegistrationToken(token);
        }
        CommonParams.Builder commonParams = new CommonParams.Builder();
        if (getIntent().hasExtra("contact_number")) {
            value = getIntent().getStringExtra("contact_number");
            commonParams.add("contact_number", value);
            commonParams.add("country_code", getIntent().getStringExtra("country_code"));
//            commonParams.add("country_code", etCountryCode.getSelectedCountryNameCode());
        } else {
            value = getIntent().getStringExtra("emailId");
            commonParams.add(EMAIL, value);
        }
        commonParams.add(PASSWORD, etPassword.getText().toString().trim());
        commonParams.add(TOKEN, CommonData.getFcmToken());
        commonParams.add("time_zone", FuguUtils.Companion.getTimeZoneOffset());
        commonParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        commonParams.add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(SignInActivity.this));
        RestClient.getApiInterface(true).signInUser(BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap()).enqueue(new ResponseResolver<FcCommonResponse>() {
            @Override
            public void onSuccess(final FcCommonResponse fcCommonResponse) {
                if (fcCommonResponse.getData().getWorkspacesInfo().size() + fcCommonResponse.getData().getInvitationToWorkspaces().size() + fcCommonResponse.getData().getOpenWorkspacesToJoin().size() == 0) {
                    CommonData.setCommonResponse(fcCommonResponse);
                    startActivity(new Intent(SignInActivity.this, CreateWorkspaceActivity.class));
                    finishAffinity();
                    return;
                }
                if (fcCommonResponse.getData().getWorkspacesInfo().size() == 0 && fcCommonResponse.getData().getInvitationToWorkspaces().size() + fcCommonResponse.getData().getOpenWorkspacesToJoin().size() > 0) {
                    CommonData.setCommonResponse(fcCommonResponse);
                    startActivity(new Intent(SignInActivity.this, YourSpacesActivity.class));
                    finishAffinity();
                    return;
                }

                com.skeleton.mvp.fugudatabase.CommonData.setSupportedFileTypes(fcCommonResponse.getData().getFuguConfig().getSupportedFileType());
//                CaptureUserData userData = new CaptureUserData.Builder()
//                        .userUniqueKey(fcCommonResponse.getData().getUserInfo().getUserId())
//                        .fullName(fcCommonResponse.getData().getWorkspacesInfo().get(0).getFullName())
//                        .email(fcCommonResponse.getData().getUserInfo().getEmail())
//                        .build();
//                FuguNotificationConfig.updateFcmRegistrationToken(CommonData.getFcmToken());
//                FuguConfig.init(1, fcCommonResponse.getData().getWorkspacesInfo().get(0).getFuguSecretKey(), SignInActivity.this,
//                        (BuildConfig.FLAVOR.equals("dev")) ? "test" : "live", userData, BuildConfig.APPLICATION_ID + ".provider", new FuguConfig.PutCallback() {
//                            @Override
//                            public void onClick() {
//                                SetFuguColors.setFuguColors();
//                            }
//                        });


                CommonData.setCurrentSignedInPosition(0);
                fcCommonResponse.getData().getWorkspacesInfo().get(0).setCurrentLogin(true);
                for (int i = 1; i < fcCommonResponse.getData().getWorkspacesInfo().size(); i++) {
                    fcCommonResponse.getData().getWorkspacesInfo().get(i).setCurrentLogin(false);
                }
                CommonData.setCommonResponse(fcCommonResponse);
                for (int j = 0; j < fcCommonResponse.getData().getWorkspacesInfo().size(); j++) {
                    com.skeleton.mvp.fugudatabase.CommonData.setFullName(fcCommonResponse.getData().getWorkspacesInfo().get(j).getFuguSecretKey(),
                            fcCommonResponse.getData().getWorkspacesInfo().get(j).getFullName());
                    com.skeleton.mvp.fugudatabase.CommonData.setWorkspaceResponse(fcCommonResponse.getData().getWorkspacesInfo().get(j).getFuguSecretKey(), fcCommonResponse.getData().getWorkspacesInfo().get(j));
                }

//                FuguNotificationConfig.updateFcmRegistrationToken(CommonData.getFcmToken());
//                if (fcCommonResponse.getData().getWorkspacesInfo().size() == 1) {
                    hideLoading();
                    CommonData.setEmail(etEmail.getText().toString().trim());
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
//                } else {
//                    hideLoading();
//                    Intent intent = new Intent(SignInActivity.this, YourSpacesActivity.class);
//                    if (getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
//                        intent.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER);
//                    }
                    CommonData.setEmail(etEmail.getText().toString().trim());
                    finishAffinity();
//                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
//                }
            }

            @Override
            public void onError(ApiError error) {
                hideLoading();
                showErrorMessage(error.getMessage());
            }

            @Override
            public void onFailure(Throwable throwable) {
                hideLoading();
                throwable.getLocalizedMessage();
                throwable.printStackTrace();
            }
        });
    }
}
