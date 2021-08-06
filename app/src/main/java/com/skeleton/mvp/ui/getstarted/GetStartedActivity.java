package com.skeleton.mvp.ui.getstarted;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatButton;

import com.hbb20.CountryCodePicker;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.CreateWorkspaceActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.checkemail.CheckEmailResponse;
import com.skeleton.mvp.data.model.fcsignup.FcSignupResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.otp.OTPActivity;
import com.skeleton.mvp.util.ValidationUtil;

import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.DOMAIN;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_EMAIL;

/**
 * Rajat Dhamija
 * 28/12/2017
 */
public class GetStartedActivity extends BaseActivity implements View.OnClickListener {
    private AppCompatButton btnContinue;
    private EditText etEmail, etPhoneNumber;
    private CountryCodePicker etCountryCode;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        initViews();
        clickListenters();
        etEmail.requestFocus();
    }

    /**
     * Initialize Views
     */
    private void initViews() {
        btnContinue = findViewById(R.id.btnContinue);
        etCountryCode = findViewById(R.id.etCountryCode);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhone);
    }

    /**
     * Click Listeners
     */
    private void clickListenters() {
        btnContinue.setOnClickListener(this);
        etPhoneNumber.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                btnContinue.performClick();
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btnContinue:

                if (TextUtils.isEmpty(etEmail.getText().toString().trim()) && !ValidationUtil.checkEmail(etEmail.getText().toString().trim())) {
                    showErrorMessage(R.string.error_invalid_email);
                } else if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim()) && !etEmail.getText().toString().trim().matches("^[0-9]*$")) {
                    showErrorMessage(R.string.error_invalid_phone_number);
                } else {
                    if (isNetworkConnected()) {
                        showLoading();
                        apiSignup();
                    } else {
                        showErrorMessage(R.string.error_internet_not_connected);
                    }
                }

//                if (!etEmail.getText().toString().trim().matches("^[0-9]*$") && !ValidationUtil.checkEmail(etEmail.getText().toString().trim())) {
//                    showErrorMessage(R.string.error_invalid_email);
//                    return;
//                } else {
//
//
//
//                }
                break;
            default:
                break;
        }
    }

    private void apiCheckEmail() {
        RestClient.getApiInterface(true).checkEmail(BuildConfig.VERSION_CODE, ANDROID, etEmail.getText().toString())
                .enqueue(new ResponseResolver<CheckEmailResponse>() {
                    @Override
                    public void onSuccess(CheckEmailResponse checkEmailResponse) {
                        hideLoading();
                        Intent signupIntent = new Intent(GetStartedActivity.this, CreateWorkspaceActivity.class);

                        signupIntent.putExtra(EXTRA_EMAIL, etEmail.getText().toString().trim());
                        CommonData.setEmail(etEmail.getText().toString().trim());
                        startActivity(signupIntent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == 403) {
//                            Intent signupIntent = new Intent(GetStartedActivity.this, FinalSigninActivity.class);
//                            signupIntent.putExtra(EXTRA_EMAIL, etEmail.getText().toString().trim());
//                            signupIntent.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER);
//                            CommonData.setEmail(etEmail.getText().toString().trim());
//                            startActivity(signupIntent);
                        } else if (error.getStatusCode() == 410) {
                            Intent signupIntent = new Intent(GetStartedActivity.this, CreateWorkspaceActivity.class);
                            signupIntent.putExtra(EXTRA_EMAIL, etEmail.getText().toString().trim());
                            CommonData.setEmail(etEmail.getText().toString().trim());
                            startActivity(signupIntent);
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
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


    private void apiSignup() {
        CommonData.setEmail(etEmail.getText().toString().trim());
        CommonParams.Builder signupParams = new CommonParams.Builder();
        signupParams.add("contact_number", "+" + etCountryCode.getSelectedCountryCode() + "-" + etPhoneNumber.getText().toString().trim());
        signupParams.add("country_code", etCountryCode.getSelectedCountryNameCode().trim());
        signupParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        signupParams.add(EMAIL, etEmail.getText().toString().trim());
        RestClient.getApiInterface(true).signUp(BuildConfig.VERSION_CODE, ANDROID, signupParams.build().getMap())
                .enqueue(new ResponseResolver<FcSignupResponse>() {
                    @Override
                    public void onSuccess(FcSignupResponse fcSignupResponse) {
                        CommonData.setSignUpResponse(fcSignupResponse);
                        hideLoading();
                        Intent otpIntent = new Intent(GetStartedActivity.this, OTPActivity.class);
                        otpIntent.putExtra(EXTRA_EMAIL, etEmail.getText().toString().trim());
                        startActivity(otpIntent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == 403 || error.getStatusCode() == 410) {
                            Intent otpIntent = new Intent(GetStartedActivity.this, OTPActivity.class);
                            if (etCountryCode.getVisibility() == View.VISIBLE) {
                                otpIntent.putExtra("contact_number", "+" + etCountryCode.getSelectedCountryCode() + "-" + etEmail.getText().toString().trim());
                                otpIntent.putExtra("country_code", etCountryCode.getSelectedCountryNameCode().trim());
                            } else {
                                otpIntent.putExtra(EXTRA_EMAIL, etEmail.getText().toString().trim());
                            }
                            startActivity(otpIntent);
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        } else {
                            try {
                                showErrorMessage(error.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                    }
                });
    }
}
