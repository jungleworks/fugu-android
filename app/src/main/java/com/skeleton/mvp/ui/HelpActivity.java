package com.skeleton.mvp.ui;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.retrofit.APIError;
import com.skeleton.mvp.retrofit.ResponseResolver;
import com.skeleton.mvp.ui.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.skeleton.mvp.ui.AppConstants.ANDROID;

public class HelpActivity extends BaseActivity {
    private EditText etFeedback, etEmail;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView tvVersion = findViewById(R.id.tvVersion);
        AppCompatButton btnSend = findViewById(R.id.btnSend);
        etFeedback = findViewById(R.id.etFeedback);
        etEmail = findViewById(R.id.etEmail);
        ivBack = findViewById(R.id.ivBack);
        tvVersion.setText("Version " + BuildConfig.VERSION_NAME);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etFeedback.getText().toString().trim())) {
                    if (isNetworkConnected()) {
                        if (etEmail.getVisibility() == View.VISIBLE) {
                            if (!TextUtils.isEmpty(etEmail.getText().toString().trim())) {
                                showLoading();
                                apiSendFeedback();
                            } else {
                                showErrorMessage("Please enter your email!");
                            }
                        } else {
                            showLoading();
                            apiSendFeedback();
                        }
                    } else {
                        showErrorMessage("Please check your Internet connection!");
                    }
                } else {
                    showErrorMessage("Please enter your feedback!");
                }
            }
        });
        etFeedback.requestFocus();
        if (TextUtils.isEmpty(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getEmail())) {
            etEmail.setVisibility(View.VISIBLE);
        } else {
            if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getEmail().contains("@fuguchat.com") & com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getEmail().contains("@junglework.auth")) {
                etEmail.setVisibility(View.VISIBLE);
            } else {
                etEmail.setVisibility(View.GONE);
            }
        }
    }

    private void apiSendFeedback() {

        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        String json = gson.toJson(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo());
        try {
            jsonObject.put("user_details", json + "   email: " + etEmail.getText().toString().trim());
            jsonObject.put("workspace_name", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getWorkspaceName());
            jsonObject.put("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getWorkspaceId());
            jsonObject.put("type", FuguAppConstant.Feedback.HELP.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        com.skeleton.mvp.data.network.CommonParams commonParams = new com.skeleton.mvp.data.network.CommonParams.Builder()
                .add("feedback", etFeedback.getText().toString().trim())
                .add("extra_details", jsonObject.toString())
                .build();

        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).sendFeedback(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap())
                .enqueue(new ResponseResolver<CommonResponse>() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        hideLoading();
                        etFeedback.setText("");
                        etEmail.setText("");
                        showErrorMessage("Feedback sent. Thank you!", new OnErrorHandleCallback() {
                            @Override
                            public void onErrorCallback() {
                                onBackPressed();
                            }
                        }, new OnPositiveButtonCallback() {
                            @Override
                            public void onPositiveButtonClick() {

                            }
                        }, "Ok", "");
                    }

                    @Override
                    public void failure(APIError error) {
                        hideLoading();
                        showErrorMessage(error.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
