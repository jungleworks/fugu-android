package com.skeleton.mvp.ui.domain;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.ui.base.BaseActivity;

import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;

public class DomainActivity extends BaseActivity {
    private EditText etWorkspaceUrl;
    private AppCompatButton btnContinue;
    private TextView tvForgot;
    private boolean isAlreadyLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain);
        etWorkspaceUrl = findViewById(R.id.etWorkspaceUrl);
        btnContinue = findViewById(R.id.btnContinue);
        tvForgot = findViewById(R.id.tvForgot);
        etWorkspaceUrl.addTextChangedListener(new MyTextWatcher());
        etWorkspaceUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btnContinue.performClick();
                }
                return false;
            }
        });
        etWorkspaceUrl.requestFocus();
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etWorkspaceUrl.getText().toString().trim())) {
                    if (CommonData.getCommonResponse() != null) {
                        for (int i = 0; i < CommonData.getCommonResponse().getData().getWorkspacesInfo().size(); i++) {
                            if (etWorkspaceUrl.getText().toString().equals(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(i).getWorkspace())) {
                                isAlreadyLoggedIn = true;
                            }
                        }
                        if (isAlreadyLoggedIn) {
                            showErrorMessage("You are already logged in using this domain !");
                            isAlreadyLoggedIn = false;
                        } else {
//                            Intent signinIntent = new Intent(DomainActivity.this, FinalSigninActivity.class);
//                            signinIntent.putExtra(EXTRA_DOMAIN, etWorkspaceUrl.getText().toString());
//                            startActivity(signinIntent);
                        }
                    } else {
//                        Intent signinIntent = new Intent(DomainActivity.this, FinalSigninActivity.class);
//                        signinIntent.putExtra(EXTRA_DOMAIN, etWorkspaceUrl.getText().toString());
//                        startActivity(signinIntent);
                    }

                } else {
                    showErrorMessage(R.string.error_please_enter_workspace_url);
                }
            }
        });
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    showLoading();
                    apiForgotDomain();
                } else {
                    showErrorMessage(R.string.error_internet_not_connected);
                }
            }
        });
    }

    private void apiForgotDomain() {
        CommonParams commonParams = new CommonParams.Builder()
                .add(EMAIL, CommonData.getEmail())
                .build();
        RestClient.getApiInterface(true).sendDomains(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap())
                .enqueue(new ResponseResolver<CommonResponse>() {
                    @Override
                    public void onSuccess(CommonResponse commonResponse) {
                        hideLoading();
                        showErrorMessage("Email sent! PLease check you email.");
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

    private class MyTextWatcher implements TextWatcher {

        String etHint = getString(R.string.hint_your_domain);


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                etWorkspaceUrl.setHint(etHint);
            } else {
                etWorkspaceUrl.setHint(null);
            }


        }
    }
}
