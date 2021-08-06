package com.skeleton.mvp.ui.setpassword;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.CreateWorkspaceActivity;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.UserInfo;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.model.setUserPassword.SetUserPasswordResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.fragment.TermsConditionFragment;
import com.skeleton.mvp.ui.UniqueIMEIID;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity;
import com.skeleton.mvp.util.ValidationUtil;
import com.skeleton.mvp.utils.FuguUtils;

import java.util.ArrayList;

import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.CONTACT_NUMBER;
import static com.skeleton.mvp.ui.AppConstants.COUNTRY_CODE;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_DETAILS;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_ID;
import static com.skeleton.mvp.ui.AppConstants.DOMAIN;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_EMAIL;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_OTP;
import static com.skeleton.mvp.ui.AppConstants.FULL_NAME;
import static com.skeleton.mvp.ui.AppConstants.OTP;
import static com.skeleton.mvp.ui.AppConstants.PASSWORD;
import static com.skeleton.mvp.ui.AppConstants.SOURCE;
import static com.skeleton.mvp.ui.AppConstants.TOKEN;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE;

public class SetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText etPassword, etFullName, etPhoneNumber;
    private AppCompatButton btnContinue;
    private CheckBox termsConditionCheckbox;
    private TextView termsConditionTextView;
    private TextView tvPhoneNumber;
    private LinearLayout llPhoneNumber;
    private CountryCodePicker etCountryCode;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        initViews();
        clickListeners();
        etFullName.requestFocus();
        if (getIntent().hasExtra(EMAIL)) {

            tvPhoneNumber.setVisibility(View.VISIBLE);
            llPhoneNumber.setVisibility(View.VISIBLE);
            etPhoneNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String regexStr = "^[0-9]*$";
                    if (s.length() > 0) {
                        if (s.toString().matches(regexStr)) {
                            etCountryCode.setVisibility(View.VISIBLE);
                            etPhoneNumber.setPadding(0, 18, 7, 18);
                        } else {
                            etCountryCode.setVisibility(View.GONE);
                            etPhoneNumber.setPadding(40, 18, 7, 18);
                        }
                    } else if (s.length() == 0) {
                        etCountryCode.setVisibility(View.GONE);
                        etPhoneNumber.setPadding(40, 18, 7, 18);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }
    }

    /**
     * Initialize Views
     */
    private void initViews() {
        etCountryCode = findViewById(R.id.etCountryCode);
        llPhoneNumber = findViewById(R.id.llPhoneNumber);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        btnContinue = findViewById(R.id.btnContinue);
        etFullName = findViewById(R.id.etFullName);
        termsConditionCheckbox = findViewById(R.id.termsCondition);
        termsConditionTextView = findViewById(R.id.textView2);
        SpannableString ss = new SpannableString("I accept Terms of Service and Privacy Policy");
        ss.setSpan(new MyCLikableText(1), 9, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new MyCLikableText(2), 30, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsConditionTextView.setText(ss);
        termsConditionTextView.setMovementMethod(LinkMovementMethod.getInstance());
        termsConditionTextView.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * Click Listeners
     */
    private void clickListeners() {
        btnContinue.setOnClickListener(this);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btnContinue.performClick();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnContinue) {
            if (llPhoneNumber.getVisibility() == View.VISIBLE && etPhoneNumber.getText().toString().trim().length() == 0) {
                showErrorMessage("Phone Number Field can't be left blank");
            } else if (!ValidationUtil.checkPassword(etPassword.getText().toString())) {
                showErrorMessage(R.string.error_invalid_password);
            } else {

                if (termsConditionCheckbox.isChecked()) {
                    if (isNetworkConnected()) {
                        showLoading();
                        if (getIntent().hasExtra("email_token")) // Means it's a SignIn case user whose password is not set
                            apiSetUserPassword();
                        else
                            apiSetPassword();
                    } else {
                        showErrorMessage(R.string.error_internet_not_connected);
                    }
                } else {
                    showErrorMessage("Please accept the Terms of Service and Privacy Policy.");
                }
            }
        }
    }

    /**
     * Api set Password
     */
    private void apiSetPassword() {
        if (TextUtils.isEmpty(CommonData.getFcmToken())) {
            String token = FirebaseInstanceId.getInstance().getToken();
            CommonData.updateFcmToken(token);
//            FuguNotificationConfig.updateFcmRegistrationToken(token);
        }
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(OTP, getIntent().getStringExtra(EXTRA_OTP));
        commonParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        if (llPhoneNumber.getVisibility() == View.VISIBLE) {
            commonParams.add(COUNTRY_CODE, etCountryCode.getSelectedCountryNameCode());
            commonParams.add(CONTACT_NUMBER, "+" + etCountryCode.getSelectedCountryCode() + "-" + etPhoneNumber.getText().toString().trim());
        }
        if (getIntent().hasExtra(EXTRA_EMAIL)) {
            commonParams.add(EMAIL, getIntent().getStringExtra(EXTRA_EMAIL));
        } else {
            commonParams.add(CONTACT_NUMBER, getIntent().getStringExtra(CONTACT_NUMBER));
            commonParams.add(COUNTRY_CODE, getIntent().getStringExtra(COUNTRY_CODE));
        }
        commonParams.add(FULL_NAME, etFullName.getText().toString());
        commonParams.add(PASSWORD, etPassword.getText().toString().trim());
        commonParams.add(TOKEN, CommonData.getFcmToken());
        commonParams.add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(SetPasswordActivity.this));
        commonParams.add(DEVICE_DETAILS, CommonData.deviceDetails(SetPasswordActivity.this));

        RestClient.getApiInterface(true).setPassword(BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap()).enqueue(new ResponseResolver<FcCommonResponse>() {
            @Override
            public void onSuccess(final FcCommonResponse fcCommonResponse) {
                hideLoading();
                CommonData.setCommonResponse(fcCommonResponse);
//                com.fugu.database.CommonData.setSupportedFileTypes(fcCommonResponse.getData().getSupportedFileType());
                CommonData.setToken(fcCommonResponse.getData().getUserInfo().getAccessToken());
                if (fcCommonResponse.data.getOpenWorkspacesToJoin().size() + fcCommonResponse.data.getInvitationToWorkspaces().size() > 0) {
                    startActivity(new Intent(SetPasswordActivity.this, YourSpacesActivity.class).putExtra("isCreateWorkspaceAllowed", true));
                } else {
                    startActivity(new Intent(SetPasswordActivity.this, CreateWorkspaceActivity.class));
                }
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finishAffinity();
            }

            @Override
            public void onError(ApiError error) {
                hideLoading();
                showErrorMessage(error.getMessage());
            }

            @Override
            public void onFailure(Throwable throwable) {
                hideLoading();
                throwable.printStackTrace();
            }
        });
    }


    /**
     * Api set Password
     */
    private void apiSetUserPassword() {
        if (TextUtils.isEmpty(CommonData.getFcmToken())) {
            String token = FirebaseInstanceId.getInstance().getToken();
            CommonData.updateFcmToken(token);
//            FuguNotificationConfig.updateFcmRegistrationToken(token);
        }
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(WORKSPACE, getIntent().getStringExtra(WORKSPACE));
        commonParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        commonParams.add("email_token", getIntent().getStringExtra("email_token"));
        if (llPhoneNumber.getVisibility() == View.VISIBLE) {
            commonParams.add(COUNTRY_CODE, etCountryCode.getSelectedCountryNameCode());
            commonParams.add(CONTACT_NUMBER, "+" + etCountryCode.getSelectedCountryCode() + "-" + etPhoneNumber.getText().toString().trim());
        }
        if (getIntent().hasExtra(EXTRA_EMAIL)) {
            commonParams.add(EMAIL, getIntent().getStringExtra(EXTRA_EMAIL));
        } else {
            commonParams.add(CONTACT_NUMBER, getIntent().getStringExtra(CONTACT_NUMBER));
            commonParams.add(COUNTRY_CODE, getIntent().getStringExtra(COUNTRY_CODE));
        }
        commonParams.add(FULL_NAME, etFullName.getText().toString());
        commonParams.add(PASSWORD, etPassword.getText().toString().trim());
        commonParams.add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(SetPasswordActivity.this));
        commonParams.add(DEVICE_DETAILS, CommonData.deviceDetails(SetPasswordActivity.this));

        RestClient.getApiInterface(true).setUserPassword(BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap()).enqueue(new ResponseResolver<SetUserPasswordResponse>() {

            @Override
            public void onSuccess(SetUserPasswordResponse setUserPasswordResponse) {
                hideLoading();
                apiLoginViaAccessToken("SetPasswordActivity", setUserPasswordResponse.getAccessToken());
            }

            @Override
            public void onError(ApiError error) {
                hideLoading();
                showErrorMessage(error.getMessage());
            }

            @Override
            public void onFailure(Throwable throwable) {
                hideLoading();
                throwable.printStackTrace();
            }
        });
    }


    /**
     * login via access token hit
     */
    private void apiLoginViaAccessToken(String source, String accessToken) {
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(TOKEN, CommonData.getFcmToken());
        commonParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        commonParams.add(FuguAppConstant.DEVICE_ID, com.skeleton.mvp.utils.UniqueIMEIID.getUniqueIMEIId(this));
        ArrayList<Integer> workspaceIds = new ArrayList<>();
        try {
            ArrayList<WorkspacesInfo> workspacesInfos = (ArrayList<WorkspacesInfo>) CommonData.getCommonResponse().data.getWorkspacesInfo();
            for (int i = 0; i < workspacesInfos.size() - 1; i++) {
                workspaceIds.add(workspacesInfos.get(i).getWorkspaceId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        commonParams.add("user_workspace_ids", workspaceIds);
        commonParams.add("time_zone", FuguUtils.Companion.getTimeZoneOffset());
        if (!TextUtils.isEmpty(source)) {
            commonParams.add(SOURCE, source);
        }
        RestClient.getApiInterface(true).accessTokenLogin(accessToken, BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<FcCommonResponse>() {
                    @Override
                    public void onSuccess(FcCommonResponse fcCommonResponse) {
                        CommonData.setCommonResponse(fcCommonResponse);
//                com.fugu.database.CommonData.setSupportedFileTypes(fcCommonResponse.getData().getSupportedFileType());
                        CommonData.setToken(fcCommonResponse.getData().getUserInfo().getAccessToken());
//                        if (fcCommonResponse.data.getWorkspacesInfo().size() + fcCommonResponse.data.getOpenWorkspacesToJoin().size() + fcCommonResponse.data.getInvitationToWorkspaces().size() > 0) {
//                            startActivity(new Intent(SetPasswordActivity.this, YourSpacesActivity.class));
//                        } else {
//                            startActivity(new Intent(SetPasswordActivity.this, CreateWorkspaceActivity.class));
//                        }
                        startActivity(new Intent(SetPasswordActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        finishAffinity();
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == 401) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(SetPasswordActivity.this);
                        } else {
                            showErrorMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                        throwable.printStackTrace();
                    }
                });
    }


    private class MyCLikableText extends ClickableSpan {

        int position;

        public MyCLikableText(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View widget) {
            switch (position) {
                case 1:
                    openTermsConditionFrag("https://jungleworks.co/terms-of-service/");
                    break;
                case 2:
                    openTermsConditionFrag("https://jungleworks.co/privacy-policy/");
                    break;
                default:
                    break;
            }
        }

        public void openTermsConditionFrag(String url) {
            FragmentManager manager = (SetPasswordActivity.this.getSupportFragmentManager());
            FragmentTransaction ft = manager.beginTransaction();
            TermsConditionFragment newFragment = TermsConditionFragment.newInstance(0, url);
            newFragment.show(ft, "TermsConditionFragment");
        }
    }
}
