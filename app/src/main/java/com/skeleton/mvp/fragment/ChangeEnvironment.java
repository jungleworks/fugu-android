package com.skeleton.mvp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.skeleton.mvp.R;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.retrofit.RestClient;

import static com.skeleton.mvp.constant.FuguAppConstant.BETA_SERVER;
import static com.skeleton.mvp.constant.FuguAppConstant.CONFERENCING_LIVE_NEW;
import static com.skeleton.mvp.constant.FuguAppConstant.CONFERENCING_TEST;
import static com.skeleton.mvp.constant.FuguAppConstant.DOMAIN_URL_LIVE;
import static com.skeleton.mvp.constant.FuguAppConstant.DOMAIN_URL_TEST;
import static com.skeleton.mvp.constant.FuguAppConstant.LIVE_SERVER;
import static com.skeleton.mvp.constant.FuguAppConstant.SOCKET_BETA_SERVER;
import static com.skeleton.mvp.constant.FuguAppConstant.SOCKET_LIVE_SERVER;
import static com.skeleton.mvp.constant.FuguAppConstant.SOCKET_TEST_SERVER;
import static com.skeleton.mvp.constant.FuguAppConstant.TEST_SERVER;

public class ChangeEnvironment extends DialogFragment implements View.OnClickListener {
    private Button liveButton;
    private Button devButton;
    private Button betaButton;
    private CheckBox cbOldOnboardingFlow, cbNewOnboardingFlow;
    private EditText customUrlFugu, customUrlSocket, etMeetURL, etCustomDomain;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_screen2, container);
        liveButton = view.findViewById(R.id.btnLive);
        liveButton.setOnClickListener(this);
        devButton = view.findViewById(R.id.btnDev);
        betaButton = view.findViewById(R.id.btnBeta);
        devButton.setOnClickListener(this);
        betaButton.setOnClickListener(this);
        Button submitButton = view.findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(this);
        customUrlFugu = view.findViewById(R.id.etCustomUrlFugu);
        customUrlSocket = view.findViewById(R.id.etCustomUrlFaye);
        etMeetURL = view.findViewById(R.id.etMeetURL);
        etCustomDomain = view.findViewById(R.id.etCustomDomain);
        cbNewOnboardingFlow = view.findViewById(R.id.cbNewOnboardingFlow);
        cbOldOnboardingFlow = view.findViewById(R.id.cbOldOnboardingFlow);
        cbNewOnboardingFlow.setOnClickListener(this);
        cbOldOnboardingFlow.setOnClickListener(this);
        boolean isOldFlow = CommonData.getOnboardingFlow().equals("old");
        cbOldOnboardingFlow.setChecked(isOldFlow);
        cbNewOnboardingFlow.setChecked(!isOldFlow);
        customUrlFugu.setText(CommonData.getFuguServerUrl());
        customUrlSocket.setText(CommonData.getSocketUrl());
        etMeetURL.setText(CommonData.getConferenceUrl());
        etCustomDomain.setText(CommonData.getDomain());
        return view;
    }

    public static ChangeEnvironment newInstance(int arg) {
        ChangeEnvironment frag = new ChangeEnvironment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cbOldOnboardingFlow:
                cbNewOnboardingFlow.setChecked(false);
                cbOldOnboardingFlow.setChecked(true);
                return;

            case R.id.cbNewOnboardingFlow:
                cbOldOnboardingFlow.setChecked(false);
                cbNewOnboardingFlow.setChecked(true);
                return;

            case R.id.btnLive:
                liveButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                liveButton.setTextColor(getResources().getColor(R.color.white));
                devButton.setBackgroundColor(getResources().getColor(R.color.white));
                devButton.setTextColor(getResources().getColor(R.color.black));
                betaButton.setBackgroundColor(getResources().getColor(R.color.white));
                betaButton.setTextColor(getResources().getColor(R.color.black));
                customUrlFugu.setText(LIVE_SERVER);
                customUrlSocket.setText(SOCKET_LIVE_SERVER);
                etMeetURL.setText(CONFERENCING_LIVE_NEW);
                etCustomDomain.setText(DOMAIN_URL_LIVE);
                RestClient.retrofit = null;
                break;

            case R.id.btnDev:
                devButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                devButton.setTextColor(getResources().getColor(R.color.white));
                liveButton.setBackgroundColor(getResources().getColor(R.color.white));
                liveButton.setTextColor(getResources().getColor(R.color.black));
                betaButton.setBackgroundColor(getResources().getColor(R.color.white));
                betaButton.setTextColor(getResources().getColor(R.color.black));
                customUrlFugu.setText(TEST_SERVER);
                customUrlSocket.setText(SOCKET_TEST_SERVER);
                etMeetURL.setText(CONFERENCING_TEST);
                etCustomDomain.setText(DOMAIN_URL_TEST);
                RestClient.retrofit = null;
                break;
            case R.id.btnBeta:
                betaButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                betaButton.setTextColor(getResources().getColor(R.color.white));
                devButton.setBackgroundColor(getResources().getColor(R.color.white));
                devButton.setTextColor(getResources().getColor(R.color.black));
                liveButton.setBackgroundColor(getResources().getColor(R.color.white));
                liveButton.setTextColor(getResources().getColor(R.color.black));
                customUrlFugu.setText(BETA_SERVER);
                customUrlSocket.setText(SOCKET_BETA_SERVER);
                etMeetURL.setText(CONFERENCING_LIVE_NEW);
                etCustomDomain.setText(DOMAIN_URL_LIVE);
                RestClient.retrofit = null;
                break;

            case R.id.btnSubmit:
                if (!TextUtils.isEmpty(customUrlSocket.getText().toString())) {
                    CommonData.setSocketUrl(customUrlSocket.getText().toString());
                }
                if (!TextUtils.isEmpty(customUrlFugu.getText().toString())) {
                    CommonData.setFuguServerUrl(customUrlFugu.getText().toString());
                }
                if (!TextUtils.isEmpty(etMeetURL.getText().toString())) {
                    CommonData.setConferenceUrl(etMeetURL.getText().toString());
                }
                if (!TextUtils.isEmpty(etCustomDomain.getText().toString())) {
                    CommonData.setDomain(etCustomDomain.getText().toString());
                }
                if (cbNewOnboardingFlow.isChecked()) {
                    CommonData.setOnboardingFlow("new");
                } else {
                    CommonData.setOnboardingFlow("old");
                }
                RestClient.retrofit = null;
                dismiss();
                break;

            default:
                break;
        }


    }
}
