package com.skeleton.mvp.ui.otp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.gplusverification.GPlusVerification;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.setpassword.SetPasswordActivity;
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.util.ValidationUtil;
import static com.skeleton.mvp.ui.AppConstants.ACCESS_TOKEN;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.CONTACT_NUMBER;
import static com.skeleton.mvp.ui.AppConstants.CONTACT_NUMBER_INTENT;
import static com.skeleton.mvp.ui.AppConstants.COUNTRY_CODE;
import static com.skeleton.mvp.ui.AppConstants.DOMAIN;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_EMAIL;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_OTP;
import static com.skeleton.mvp.ui.AppConstants.NEW_SIGNUP;
import static com.skeleton.mvp.ui.AppConstants.OTP;
import static com.skeleton.mvp.ui.AppConstants.SIGNUP_SOURCE;

/**
 * Rajat Dhamija
 * 29/12/2017
 */
public class OTPActivity extends BaseActivity implements View.OnClickListener {
    private EditText etOne, etTwo, etThree, etFour, etFive, etSix;
    private String otp;
    private AppCompatButton btnContinue;
    private final int OTP_COUNT = 6;
    private LinearLayout otpContainer;
    private TextView tvSubtitle, tvCheck;
    private String text;
    public FcCommonResponse fcCommonResponse;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initViews();
        clickListeners();
        etOne.requestFocus();
    }

    /**
     * Initialize Views
     */
    private void initViews() {
        etOne = findViewById(R.id.etOne);
        etTwo = findViewById(R.id.etTwo);
        etThree = findViewById(R.id.etThree);
        etFour = findViewById(R.id.etFour);
        etFive = findViewById(R.id.etFive);
        etSix = findViewById(R.id.etSix);
        btnContinue = findViewById(R.id.btnContinue);
        otpContainer = findViewById(R.id.llOTP);
        tvSubtitle = findViewById(R.id.tvSubtitile);
        tvCheck = findViewById(R.id.tvCheck);
        if (getIntent().hasExtra(EXTRA_EMAIL) && getIntent().hasExtra(CONTACT_NUMBER)) {
            text = getIntent().getStringExtra(CONTACT_NUMBER);
        } else if (getIntent().hasExtra(EXTRA_EMAIL)) {
            text = getIntent().getStringExtra(EXTRA_EMAIL);
        } else {
            text = getIntent().getStringExtra(CONTACT_NUMBER);
        }
        if(getIntent().hasExtra("EditPhone") && getIntent().getStringExtra("EditPhone").equals("EditPhone"))
            tvCheck.setText("Check your SMS");
        tvSubtitle.setText("We’ve sent a six-digit confirmation code to " + text);
        handleOtpContainerFields();
    }

    private void clickListeners() {
        btnContinue.setOnClickListener(this);
        etSix.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        switch (view.getId()) {
            case R.id.btnContinue:
                if (Validated()) {
                    otp = etOne.getText().toString().trim() +
                            etTwo.getText().toString().trim() +
                            etThree.getText().toString().trim() +
                            etFour.getText().toString().trim() +
                            etFive.getText().toString().trim() +
                            etSix.getText().toString().trim();
                    if (isNetworkConnected()) {
                        showLoading();
                        if (getIntent().hasExtra(NEW_SIGNUP)) {
                            apiCheckIfGoogleUserAlreadyRegistered();
                        } else if (getIntent().hasExtra("EditPhone")) {
                            apiVerifyEditOtp();
                        } else {
                            apiVerifyOtp();
                        }
                    } else {
                        showErrorMessage(R.string.error_internet_not_connected);
                    }
                } else {
                    showErrorMessage(R.string.invalid_otp);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Validate OTP fields
     *
     * @return boolean
     */
    private boolean Validated() {
        return ValidationUtil.checkName(etOne.getText().toString()) &&
                ValidationUtil.checkName(etTwo.getText().toString()) &&
                ValidationUtil.checkName(etThree.getText().toString()) &&
                ValidationUtil.checkName(etFour.getText().toString()) &&
                ValidationUtil.checkName(etFive.getText().toString()) &&
                ValidationUtil.checkName(etSix.getText().toString());
    }

    private void apiCheckIfGoogleUserAlreadyRegistered() {
        CommonParams commonParams = new CommonParams.Builder()
                .add(EMAIL, getIntent().getStringExtra(EXTRA_EMAIL))
                .add(OTP, otp)
                .add(CONTACT_NUMBER, getIntent().getStringExtra(CONTACT_NUMBER))
                .add(COUNTRY_CODE, getIntent().getStringExtra(COUNTRY_CODE))
                .add(SIGNUP_SOURCE, "GOOGLE")
                .build();

        RestClient.getApiInterface(true).checkIfGoogleUserAlreadyRegistered(BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap())
                .enqueue(new ResponseResolver<GPlusVerification>() {
                    @Override
                    public void onSuccess(GPlusVerification gPlusVerification) {
                        hideLoading();
                        Intent intent = new Intent();
                        intent.putExtra(ACCESS_TOKEN, gPlusVerification.getData().getAccessToken());
                        setResult(RESULT_OK, intent);
                        finish();
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
     * Verify OTP API Hit
     */
    private void apiVerifyOtp() {
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(OTP, otp);
        if (getIntent().hasExtra(EXTRA_EMAIL)) {
            commonParams.add(EMAIL, getIntent().getStringExtra(EXTRA_EMAIL));
        } else {
            commonParams.add(CONTACT_NUMBER, getIntent().getStringExtra(CONTACT_NUMBER));
            commonParams.add(COUNTRY_CODE, getIntent().getStringExtra(COUNTRY_CODE));
        }
        commonParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        RestClient.getApiInterface(true).verifyOtp(BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap()).enqueue(new ResponseResolver<FcCommonResponse>() {
            @Override
            public void onSuccess(FcCommonResponse fcCommonResponse) {
                hideLoading();
                CommonData.setCommonResponse(fcCommonResponse);
                CommonData.setToken(fcCommonResponse.getData().getUserInfo().getAccessToken());
                Intent intent = new Intent(OTPActivity.this, YourSpacesActivity.class);
                startActivity(intent);
                finishAffinity();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }

            @Override
            public void onError(ApiError error) {
                hideLoading();
                if (error.getStatusCode() == 403) {
                    Intent setPasswordIntent = new Intent(OTPActivity.this, SetPasswordActivity.class);
                    setPasswordIntent.putExtra(EXTRA_OTP, otp);
                    if (getIntent().hasExtra(CONTACT_NUMBER_INTENT)) {
                        boolean isContactNumber = getIntent().getBooleanExtra(CONTACT_NUMBER_INTENT, false);
                        setPasswordIntent.putExtra(CONTACT_NUMBER_INTENT, isContactNumber);
                    }
                    if (getIntent().hasExtra(EXTRA_EMAIL)) {
                        setPasswordIntent.putExtra(EMAIL, getIntent().getStringExtra(EXTRA_EMAIL));
                    } else {
                        setPasswordIntent.putExtra(CONTACT_NUMBER, getIntent().getStringExtra(CONTACT_NUMBER));
                        setPasswordIntent.putExtra(COUNTRY_CODE, getIntent().getStringExtra(COUNTRY_CODE));
                    }
                    startActivity(setPasswordIntent);
                    finishAffinity();
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


    /**
     * Verify editPhone OTP API Hit
     */
    public void apiVerifyEditOtp() {

        fcCommonResponse = CommonData.getCommonResponse();
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(OTP, otp);
        RestClient.getApiInterface(true).verifyEdit_Phone_Otp(fcCommonResponse.data.userInfo.accessToken, FuguAppConstant.ANDROIDS, BuildConfig.VERSION_CODE, commonParams.build().getMap())
                .enqueue(new ResponseResolver<CommonResponse>() {
                    @Override
                    public void onSuccess(CommonResponse fcCommonResponse) {
                        hideLoading();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        showErrorMessage(error.getMessage());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                        if(isNetworkConnected())
                            apiVerifyEditOtp();
                    }
                });


    }


    private void handleOtpContainerFields() {
        for (int i = 0; i < OTP_COUNT; i++) {
            final EditText editText = (EditText) otpContainer.getChildAt(i);
            final int j = i;

            if (j < OTP_COUNT) {
                editText.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                        Log.e("onKey pressed", "==" + keyCode);

                        Log.e("text", "==" + editText.getText().toString());

                        if (keyCode == KeyEvent.KEYCODE_DEL &&
                                keyEvent.getAction() == KeyEvent.ACTION_DOWN && editText.getText().toString().isEmpty() && j != 0) {
                            otpContainer.getChildAt(j - 1).requestFocus();
                        }
                        return false;
                    }
                });

                editText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!editText.getText().toString().isEmpty() && j != (OTP_COUNT - 1))
                            otpContainer.getChildAt(j + 1).requestFocus();
                    }
                });
            }
        }
    }
}
