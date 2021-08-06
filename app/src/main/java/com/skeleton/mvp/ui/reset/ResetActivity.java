package com.skeleton.mvp.ui.reset;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.hbb20.CountryCodePicker;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.data.model.setPassword.CommonResponseFugu;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.util.ValidationUtil;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.DOMAIN;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;

public class ResetActivity extends BaseActivity implements View.OnClickListener {
    private EditText etEmail;
    private CountryCodePicker etCountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        AppCompatButton btnResetPassword = findViewById(R.id.btnResetPassword);
        etEmail = findViewById(R.id.etEmail);
        etCountryCode = findViewById(R.id.etCountryCode);
        btnResetPassword.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnResetPassword:
                if ((etCountryCode.getVisibility() == View.VISIBLE) && !etEmail.getText().toString().trim().matches("^[0-9]*$")) {
                    showErrorMessage("Please enter valid phone number");
                } else if (!(etCountryCode.getVisibility() == View.VISIBLE) && !ValidationUtil.checkEmail(etEmail.getText().toString().trim())) {
                    showErrorMessage("Please enter valid email/phone number");
                } else if (isNetworkConnected()) {
                    showLoading();
                    apiResetPassword();
                } else {
                    showErrorMessage(R.string.error_internet_not_connected);
                }
                break;
            default:
                break;
        }
    }

    private void apiResetPassword() {
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        if (etCountryCode.getVisibility() == View.VISIBLE) {
            commonParams.add("contact_number", "+" + etCountryCode.getSelectedCountryCode() + "-" + etEmail.getText().toString().trim());
            commonParams.add("country_code", etCountryCode.getSelectedCountryNameCode());
        } else {
            commonParams.add(EMAIL, etEmail.getText().toString().trim());
        }
        RestClient.getApiInterface(true).resetPassword(BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<CommonResponseFugu>() {
                    @Override
                    public void onSuccess(CommonResponseFugu commonResponseFugu) {
                        hideLoading();
                        showErrorMessage("Password reset mail/message has been sent. Please check your email/phone number.", new OnErrorHandleCallback() {
                            @Override
                            public void onErrorCallback() {
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(ApiError error) {
                        showErrorMessage(error.getMessage(), new OnErrorHandleCallback() {
                            @Override
                            public void onErrorCallback() {
                                finish();
                            }
                        });

                        hideLoading();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                    }
                });
    }
}
