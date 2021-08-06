package com.skeleton.mvp.ui.intro;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.AlreadySignInGoogle;
import com.skeleton.mvp.activity.CheckEmailActivity;
import com.skeleton.mvp.activity.CreateWorkspaceActivity;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.activity.PhoneNumberActivity;
import com.skeleton.mvp.activity.ValidateOtpActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.fragment.TestcodeFragment;
import com.skeleton.mvp.model.googleSignin.GoogleSigninResponse;
import com.skeleton.mvp.pushNotification.PushReceiver;
import com.skeleton.mvp.socket.SocketConnection;
import com.skeleton.mvp.ui.AppConstants;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.getstarted.GetStartedActivity;
import com.skeleton.mvp.ui.onboarding.signin.SignInActivity;
import com.skeleton.mvp.ui.otp.OTPActivity;
import com.skeleton.mvp.ui.setpassword.SetPasswordActivity;
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity;
import com.skeleton.mvp.util.KeyboardUtil;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.utils.FuguImageUtils;
import com.skeleton.mvp.utils.FuguUtils;
import com.skeleton.mvp.utils.UniqueIMEIID;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.branch.referral.Branch;
import static com.skeleton.mvp.constant.FuguAppConstant.PERMISSION_CONSTANT_GALLERY;
import static com.skeleton.mvp.ui.AppConstants.ACCESS_TOKEN;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.BRANCH_IO;
import static com.skeleton.mvp.ui.AppConstants.BUNDLE;
import static com.skeleton.mvp.ui.AppConstants.CONTACT_NUMBER;
import static com.skeleton.mvp.ui.AppConstants.CONTACT_NUMBER_INTENT;
import static com.skeleton.mvp.ui.AppConstants.CONVERSATION;
import static com.skeleton.mvp.ui.AppConstants.COUNTRY_CODE;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_DETAILS;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_ID;
import static com.skeleton.mvp.ui.AppConstants.DOMAIN;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_EMAIL;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_EMAIL_ID;
import static com.skeleton.mvp.ui.AppConstants.HARD_UPDATE;
import static com.skeleton.mvp.ui.AppConstants.IMAGE_SLASH;
import static com.skeleton.mvp.ui.AppConstants.OPEN_HOME;
import static com.skeleton.mvp.ui.AppConstants.REQ_CODE_NEW_SIGNUP;
import static com.skeleton.mvp.ui.AppConstants.SHARED_IMAGE_URI;
import static com.skeleton.mvp.ui.AppConstants.SHARED_TEXT;
import static com.skeleton.mvp.ui.AppConstants.SOFT_UPDATE;
import static com.skeleton.mvp.ui.AppConstants.SOURCE;
import static com.skeleton.mvp.ui.AppConstants.TEXT_SLASH;
import static com.skeleton.mvp.ui.AppConstants.TOKEN;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE;

/**
 * Rajat Dhamija
 * 28/12/2017
 */
public class IntroActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {
    public static final int REQUEST_CODE_PLAY_STORE = 12221;
    private static final String TAG = IntroActivity.class.getSimpleName();
    private AppCompatButton btnGetstarted, btnSignIn;
    private LinearLayout llButtons;
    private boolean startIntent = false;
    private boolean invite = false;
    private String imageUri = "", sharedText = "";
    private String extension;
    private ImageView leftView, rightView;
    private boolean isLeftClicked = false;
    private boolean isRightClicked = false;
    private RelativeLayout realtiveviewContainer;
    private RelativeLayout rlOrDivider;
    private GoogleSignInClient mGoogleSignInClient;
    private LinearLayout signInButton;
    private ImageView signInImage;
    private EditText etEmail;
    private CountryCodePicker etCountryCode;
    private AppCompatButton btnLogin;
    private ScrollView scrollView;
    private KeyboardUtil.SoftKeyboardToggleListener keyboardListener = new KeyboardUtil.SoftKeyboardToggleListener() {
        @Override
        public void onToggleSoftKeyboard(boolean isVisible) {
            scrollView.post(() -> {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                scrollView.clearFocus();
                etEmail.requestFocus();
            });
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        CommonData.setCurrentVersion(BuildConfig.VERSION_CODE);
        if (getIntent().hasExtra(OPEN_HOME)) {
            Intent mainIntent = new Intent(IntroActivity.this, MainActivity.class);
            if (getIntent().hasExtra(CONVERSATION)) {
                mainIntent.putExtra(BUNDLE, getIntent().getExtras());
            }
            startActivity(mainIntent);
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            finishAffinity();
        }
        clearFuguData();
        initViews();
        longClickListeners();
        clickListeners();
        fileSharingSystem();
        KeyboardUtil.addKeyboardToggleListener(this, keyboardListener);
        etEmail.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                btnLogin.performClick();
            }
            return false;
        });
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("1067615629789-qs9p7v7o3ur01tf7oanq3ds4k9ashmjh.apps.googleusercontent.com")
                .requestServerAuthCode("1067615629789-qs9p7v7o3ur01tf7oanq3ds4k9ashmjh.apps.googleusercontent.com")
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestScopes(new Scope(Scopes.PROFILE))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        if (FuguUtils.Companion.isWhiteLabel()) {
            signInButton.setVisibility(View.GONE);
            rlOrDivider.setVisibility(View.GONE);
        } else
            signInButton.setOnClickListener(v -> {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyboardUtil.removeKeyboardToggleListener(keyboardListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                try {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        onLoggedIn(account);
                    }
                } catch (ApiException e) {
                    Log.i("Sign In Failed", "signInResult:failed code=" + e.getStatusCode());
                }
                break;
            case REQ_CODE_NEW_SIGNUP:
                if (resultCode == RESULT_OK) {
                    if (isNetworkConnected()) {
                        showLoading();
                        apiLoginViaAccessToken("", data.getStringExtra(ACCESS_TOKEN), "", true);
                    } else {
                        showErrorMessage(R.string.error_internet_not_connected);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        apiLoginViaGoogle(googleSignInAccount.getServerAuthCode(), googleSignInAccount.getEmail());
    }

    private void apiLoginViaGoogle(String serverAuthCode, String email) {
        CommonParams.Builder googleSignInParams = new CommonParams.Builder();
        googleSignInParams.add("authorized_code", serverAuthCode);
        showLoading();
        RestClient.getApiInterface(true).googleSignIn(BuildConfig.VERSION_CODE, ANDROID, googleSignInParams.build().getMap())
                .enqueue(new ResponseResolver<GoogleSigninResponse>() {
                    @Override
                    public void onSuccess(GoogleSigninResponse googleSigninResponse) {

                        if (googleSigninResponse.getStatusCode() == 200) {
                            apiLoginViaAccessToken("", googleSigninResponse.getData().getAccessToken(), "", true);
                        } else if (googleSigninResponse.getStatusCode() == 206) {
                            hideLoading();
                            Intent intent = new Intent(IntroActivity.this, PhoneNumberActivity.class);
                            intent.putExtra(EXTRA_EMAIL, email);
                            startActivityForResult(intent, REQ_CODE_NEW_SIGNUP);
                        } else {
                            apiLoginViaAccessToken("", googleSigninResponse.getData().getAccessToken(), "", true);
                        }
                        mGoogleSignInClient.signOut();
                    }

                    @Override
                    public void onError(ApiError error) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

    /**
     * Share file from external applications
     */
    private void fileSharingSystem() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        FuguImageUtils fuguImageUtils = new FuguImageUtils(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null && (type.startsWith(IMAGE_SLASH) || type.startsWith(TEXT_SLASH))) {

            if (type.startsWith(IMAGE_SLASH)) {

                try {
                    if (!FuguConfig.getInstance().askUserToGrantPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                            PERMISSION_CONSTANT_GALLERY)) return;
                    Cursor cursor = IntroActivity.this.getContentResolver().query(intent.getParcelableExtra(Intent.EXTRA_STREAM), null, null, null, null);
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            try {
                                extension = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.")[1].toLowerCase();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    fuguImageUtils.copyFileFromUri(intent.getParcelableExtra(Intent.EXTRA_STREAM), extension);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                com.skeleton.mvp.fugudatabase.CommonData.setImageUri(imageUri);
                com.skeleton.mvp.fugudatabase.CommonData.setSharedText("");
            } else if (type.startsWith("text/") && intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.e(SHARED_TEXT, sharedText);
                com.skeleton.mvp.fugudatabase.CommonData.setImageUri("");
                com.skeleton.mvp.fugudatabase.CommonData.setSharedText(sharedText);
            }

        } else {
            com.skeleton.mvp.fugudatabase.CommonData.setImageUri("");
            com.skeleton.mvp.fugudatabase.CommonData.setSharedText("");
        }
    }

    /**
     * Clearing Fugu Data --emergency crash jugaad
     */
    private void clearFuguData() {
        if (!com.skeleton.mvp.fugudatabase.CommonData.getClearFuguDataKeyOffice()) {
            try {
                CommonData.clearData();

                com.skeleton.mvp.fugudatabase.CommonData.clearData();
                com.skeleton.mvp.fugudatabase.CommonData.setClearFuguDataKeyOffice(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Login in app via branch
     */
    private void loginViaBranch() {
        Branch.getInstance().initSession((referringParams, error) -> {
            if (error == null) {
                try {
                    if (referringParams.has(AppConstants.EMAIL) && referringParams.has(AppConstants.TOKEN) && referringParams.has(AppConstants.SIGNUP_TYPE)) {
                        Log.d("", "Branch Login Via E-mail");
                        String email = referringParams.getString(AppConstants.EMAIL);
                        String token = referringParams.getString(AppConstants.TOKEN);
                        int signupType = referringParams.getInt("signup_type");
                        String verificationToken = referringParams.getString("verification_token");
                        Intent intent = new Intent(IntroActivity.this, CheckEmailActivity.class);
                        intent.putExtra(AppConstants.EXTRA_EMAIL, email);
                        intent.putExtra(AppConstants.TOKEN, token);
                        intent.putExtra("signup_type", signupType);
                        startActivity(intent);
                        finishAffinity();
                    } else if (CommonData.getCommonResponse() != null && !referringParams.getString(EMAIL).equals(CommonData.getCommonResponse().getData().getUserInfo().getEmail())) {
                        Log.e(TAG, "Nothing");
                    } else {
                        if ((referringParams.has(EMAIL) || referringParams.has(CONTACT_NUMBER)) && referringParams.has(WORKSPACE)) {
                            invite = true;
                            llButtons.setVisibility(View.GONE);
                            showLoading();
                            apiLoginViaAccessToken(BRANCH_IO, referringParams.getString(TOKEN), referringParams.getString(WORKSPACE), false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, error.getMessage());
            }
        }, this.getIntent().getData(), this);
    }

    /**
     * Initialize Views
     */
    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etCountryCode = findViewById(R.id.etCountryCode);
        signInImage = findViewById(R.id.sign_in_image);
        signInButton = findViewById(R.id.sign_in_button);
        btnGetstarted = findViewById(R.id.btnGetStarted);
        btnSignIn = findViewById(R.id.btnSignIn);
        llButtons = findViewById(R.id.llButtons);
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        leftView = findViewById(R.id.leftView);
        rightView = findViewById(R.id.rightView);
        realtiveviewContainer = findViewById(R.id.viewContainer);
        rlOrDivider = findViewById(R.id.rlOrDivider);
        scrollView = findViewById(R.id.scrollView);
        tvWelcome.setOnClickListener(this);
        if (startIntent) {
            new Handler().postDelayed(() -> {
                if (CommonData.getCommonResponse() != null) {
                    final FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
                    new Handler().postDelayed(() -> {
                        try {
                            if (!invite) {
                                showLoading();
                                apiLoginViaAccessToken("", fcCommonResponse.getData().getUserInfo().getAccessToken(), "", false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 500);
                } else {
                    Animation fadeIn = new AlphaAnimation(0, 1);
                    fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                    fadeIn.setDuration(500);
                }
            }, 500);
        }

    }

    /**
     * Button CLick Listeners
     */
    private void clickListeners() {
        btnGetstarted.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    /**
     * Image longCLick Listeners
     */
    private void longClickListeners() {
        rightView.setOnLongClickListener(this);
        leftView.setOnLongClickListener(this);
        signInImage.setOnLongClickListener(this);
    }

    /**
     * login via access token hit
     */
    private void apiLoginViaAccessToken(String source, String accessToken, final String workspaceName, boolean isSignInFromGoogle) {
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(TOKEN, CommonData.getFcmToken());
        commonParams.add("time_zone", FuguUtils.Companion.getTimeZoneOffset());
        commonParams.add(DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        commonParams.add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this));
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
        if (!TextUtils.isEmpty(source)) {
            commonParams.add(SOURCE, source);
        }
        RestClient.getApiInterface(true).accessTokenLogin(accessToken, BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<FcCommonResponse>() {
                    @Override
                    public void onSuccess(FcCommonResponse fcCommonResponse) {
                        apiUpdateDeviceToken(fcCommonResponse);
                        hideLoading();
                        if (invite && fcCommonResponse.data.getWorkspacesInfo().size() == 1)
                            getSharedPreferences("General", Context.MODE_PRIVATE).edit().putBoolean("isFirstTimeOpened", true).commit();
                        checkVersion(fcCommonResponse, workspaceName, isSignInFromGoogle);
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == 401) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(IntroActivity.this);
                        } else {
                            showErrorMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                        if (throwable instanceof SocketTimeoutException) {
                            showErrorMessage(R.string.error_internet_not_connected, () -> {
                                FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
                                showLoading();
                                try {
                                    if (fcCommonResponse != null && fcCommonResponse.getData() != null) {
                                        apiLoginViaAccessToken("", fcCommonResponse.getData().getUserInfo().getAccessToken(), "", isSignInFromGoogle);
                                    }
                                } catch (Exception e) {

                                }
                            });
                        }
                    }
                });
    }

    private void apiUpdateDeviceToken(FcCommonResponse fcCommonResponse) {
        CommonParams.Builder commonParams = new CommonParams.Builder();
        CommonData.updateFcmToken(FirebaseInstanceId.getInstance().getToken());
        commonParams.add(TOKEN, CommonData.getFcmToken());
        commonParams.add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this));
        RestClient.getApiInterface(true).updateDeviceToken(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<CommonResponse>() {
                    @Override
                    public void onSuccess(CommonResponse commonResponse) {

                    }

                    @Override
                    public void onError(ApiError error) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

    /**
     * Check version for hard/soft update
     */
    private void checkVersion(final FcCommonResponse fcCommonResponse, final String workspaceName, final boolean isSignInFromGoogle) {
        hideLoading();
        com.skeleton.mvp.fugudatabase.CommonData.setSupportedFileTypes(fcCommonResponse.getData().getFuguConfig().getSupportedFileType());
        try {
            switch (fcCommonResponse.getData().getUserInfo().getAppUpdateConfig().getAppUpdateMessage()) {
                case HARD_UPDATE:
                    showUpdateMessage(fcCommonResponse, HARD_UPDATE, "Exit", workspaceName);
                    break;
                case SOFT_UPDATE:
                    showUpdateMessage(fcCommonResponse, SOFT_UPDATE, "Cancel", workspaceName);
                    break;
                default:
                    if (isSignInFromGoogle) {
                        CommonData.setCommonResponse(fcCommonResponse);
                        Intent yourWorkspacesIntent;
                        if ((fcCommonResponse.getData().getInvitationToWorkspaces().size()
                                + fcCommonResponse.getData().getOpenWorkspacesToJoin().size()) == 0
                                && fcCommonResponse.getData().getWorkspacesInfo().size() == 1) {
                            yourWorkspacesIntent = new Intent(IntroActivity.this, MainActivity.class);
                            finish();
                        } else if (fcCommonResponse.getData().getInvitationToWorkspaces().size() == 0 &&
                                fcCommonResponse.getData().getOpenWorkspacesToJoin().size() == 0 &&
                                fcCommonResponse.getData().getWorkspacesInfo().size() == 0) {
                            yourWorkspacesIntent = new Intent(IntroActivity.this, CreateWorkspaceActivity.class);
                            yourWorkspacesIntent.putExtra("isGoogleSignIn", true);
                        } else if (fcCommonResponse.getData().getWorkspacesInfo().size() > 0) {
                            yourWorkspacesIntent = new Intent(IntroActivity.this, MainActivity.class);
                            finish();
                        } else {
                            yourWorkspacesIntent = new Intent(IntroActivity.this, YourSpacesActivity.class);
                            finish();
                        }
                        startActivity(yourWorkspacesIntent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    } else {
                        getInsideApp(fcCommonResponse, workspaceName);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show update message har/soft
     */
    private void showUpdateMessage(final FcCommonResponse fcCommonResponse, final String hard_update, String negativeMessage, final String workspaceName) {
        showErrorMessage(fcCommonResponse.getData().getUserInfo().getAppUpdateConfig().getAppUpdateText(), () -> openPlayStore(fcCommonResponse), () -> {
            if (hard_update.equals(HARD_UPDATE)) {
                finish();
            } else {
                getInsideApp(fcCommonResponse, workspaceName);
            }
        }, "Update", negativeMessage);
    }

    /**
     * Get inside of the application
     */
    private void getInsideApp(FcCommonResponse fcCommonResponse, String workspaceName) {

        if (fcCommonResponse.getData().getWorkspacesInfo().size() == 0) {
            startActivity(new Intent(IntroActivity.this, CreateWorkspaceActivity.class));
            finishAffinity();
            return;
        }

        List<WorkspacesInfo> workspacesInfo = fcCommonResponse.getData().getWorkspacesInfo();
        for (int j = 0; j < workspacesInfo.size(); j++) {
            com.skeleton.mvp.fugudatabase.CommonData.setFullName(workspacesInfo.get(j).getFuguSecretKey(), workspacesInfo.get(j).getFullName());
            workspacesInfo.get(j).setCurrentLogin(false);
        }
        if (CommonData.getCurrentSignedInPosition() > workspacesInfo.size()) {
            CommonData.setCurrentSignedInPosition(0);
        } else {
            workspacesInfo.get(CommonData.getCurrentSignedInPosition()).setCurrentLogin(true);
        }
        if (!TextUtils.isEmpty(workspaceName)) {
            int currentposition = 0;
            for (int j = 0; j < workspacesInfo.size(); j++) {
                workspacesInfo.get(j).setCurrentLogin(false);
                com.skeleton.mvp.fugudatabase.CommonData.setFullName(workspacesInfo.get(j).getFuguSecretKey(), workspacesInfo.get(j).getFullName());
                if (workspacesInfo.get(j).getWorkspace().equals(workspaceName)) {
                    currentposition = j;
                }
            }
            CommonData.setCurrentSignedInPosition(currentposition);
            workspacesInfo.get(currentposition).setCurrentLogin(true);
        }

        CommonData.setCommonResponse(fcCommonResponse);

        SocketConnection.INSTANCE.disconnectSocket();
        SocketConnection.INSTANCE.initSocketConnection(fcCommonResponse.getData().getUserInfo().getAccessToken(),
                workspacesInfo.get(CommonData.getCurrentSignedInPosition()).getEnUserId(), fcCommonResponse.data.userInfo.getUserId(),
                fcCommonResponse.data.userInfo.getUserChannel(), "Invited To WorkSpace", false,
                fcCommonResponse.data.userInfo.getPushToken());
        Intent homeIntent = new Intent(IntroActivity.this, MainActivity.class);
        if (!TextUtils.isEmpty(imageUri)) {
            homeIntent.putExtra(SHARED_IMAGE_URI, imageUri);
        } else if (!TextUtils.isEmpty(sharedText)) {
            homeIntent.putExtra(SHARED_TEXT, sharedText);
        }
        if (getIntent().hasExtra(CONVERSATION)) {
            homeIntent.putExtra(BUNDLE, getIntent().getExtras());
        }
        startActivity(homeIntent);
        hideLoading();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    /**
     * open playstore
     */
    private void openPlayStore(FcCommonResponse fcCommonResponse) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        try {
            startActivityForResult(intent, REQUEST_CODE_PLAY_STORE);
        } catch (final Exception e) {
            intent.setData(Uri.parse(fcCommonResponse.getData().getUserInfo().getAppUpdateConfig().getAppLink()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showErrorMessage(R.string.one_or_more_permissions_denied, this::finishAffinity);
        } else {
            startIntent = true;
            initViews();
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btnGetStarted:
                startActivity(new Intent(IntroActivity.this, GetStartedActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.btnSignIn:
                startActivity(new Intent(IntroActivity.this, SignInActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.btnLogin:
                String inputText = etEmail.getText().toString();
                if (inputText.length() > 3 && validate(inputText)) {
                    apiLogin();
                } else {
                    showErrorMessage(R.string.error_email_phoneno);
                }
            default:
                break;
        }
    }

    public Boolean validate(String input) {
        final String regex = "(^[0-9]+$)|(\\S+@\\S+)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        PushReceiver.PushChannel.isEmailVerificationScreen = false;
        super.onBackPressed();
    }

    private void apiLogin() {
        showLoading();
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(DEVICE_DETAILS, CommonData.deviceDetails(IntroActivity.this));
        commonParams.add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(IntroActivity.this));
        if (etCountryCode.getVisibility() == View.VISIBLE) {
            commonParams.add(CONTACT_NUMBER, "+" + etCountryCode.getSelectedCountryCode() + "-" + etEmail.getText().toString().trim());
            commonParams.add(COUNTRY_CODE, etCountryCode.getSelectedCountryNameCode());
        } else {
            commonParams.add(EMAIL, etEmail.getText().toString().trim());
        }
        commonParams.add(AppConstants.DOMAIN, com.skeleton.mvp.fugudatabase.CommonData.getDomain());
        if (com.skeleton.mvp.fugudatabase.CommonData.getOnboardingFlow().equals("new")) {
            RestClient.getApiInterface(true).userLogin(BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                    .enqueue(new ResponseResolver<FcCommonResponse>() {
                        @Override
                        public void onSuccess(FcCommonResponse userLoginResponse) {
                            hideLoading();
                            if (userLoginResponse.getStatusCode() == 200) { // 200 means users is a new user and needs to signup
                                Intent intent = new Intent(IntroActivity.this, ValidateOtpActivity.class);
                                if (etCountryCode.getVisibility() == View.VISIBLE) {
                                    intent.putExtra(CONTACT_NUMBER_INTENT, true);
                                    intent.putExtra(CONTACT_NUMBER, "+" + etCountryCode.getSelectedCountryCode() + "-" + etEmail.getText().toString().trim());
                                    intent.putExtra(COUNTRY_CODE, etCountryCode.getSelectedCountryNameCode());
                                } else {
                                    intent = new Intent(IntroActivity.this, CheckEmailActivity.class);
                                    intent.putExtra(CONTACT_NUMBER_INTENT, false);
                                    intent.putExtra(EXTRA_EMAIL, etEmail.getText().toString().trim());
                                }
                                startActivity(intent);
                            } else if (userLoginResponse.getStatusCode() == 201) { // 201 means user is a google account
                                Intent intent = new Intent(IntroActivity.this, AlreadySignInGoogle.class);
                                intent.putExtra(EXTRA_EMAIL_ID, etEmail.getText().toString());
                                startActivity(intent);
                            } else if (userLoginResponse.getStatusCode() == 202) { // 202 means user is an existing user and requires to login
                                Intent intent = new Intent(IntroActivity.this, SignInActivity.class);
                                if (etCountryCode.getVisibility() == View.VISIBLE) {
                                    intent.putExtra(CONTACT_NUMBER, "+" + etCountryCode.getSelectedCountryCode() + "-" + etEmail.getText().toString().trim());
                                    intent.putExtra(COUNTRY_CODE, etCountryCode.getSelectedCountryNameCode());
                                } else {
                                    intent.putExtra(EXTRA_EMAIL_ID, etEmail.getText().toString().trim());
                                }
                                startActivity(intent);
                            } else if (userLoginResponse.getStatusCode() == 203) { // 203 means User has not set any password yet.
                                Intent intent = new Intent(IntroActivity.this, SetPasswordActivity.class);
                                intent.putExtra("email_token", userLoginResponse.getData().getSetPasswordEmailToken());
                                intent.putExtra(WORKSPACE, userLoginResponse.getData().getSetPasswordWorkspace());
                                if (etCountryCode.getVisibility() == View.VISIBLE) {
                                    intent.putExtra(CONTACT_NUMBER, "+" + etCountryCode.getSelectedCountryCode() + "-" + etEmail.getText().toString().trim());
                                    intent.putExtra(COUNTRY_CODE, etCountryCode.getSelectedCountryNameCode());
                                } else {
                                    intent.putExtra(EMAIL, etEmail.getText().toString().trim());
                                }
                                startActivity(intent);
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
        } else {
            commonParams.add("time_zone", FuguUtils.Companion.getTimeZoneOffset());
            RestClient.getApiInterface(true).userLoginV2(BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                    .enqueue(new ResponseResolver<FcCommonResponse>() {
                        @Override
                        public void onSuccess(FcCommonResponse userLoginResponse) {
                            hideLoading();
                            if (userLoginResponse.getStatusCode() == 200) { // 200 means users is a new user and needs to signup
                                Intent intent = new Intent(IntroActivity.this, OTPActivity.class);
                                if (etCountryCode.getVisibility() == View.VISIBLE) {
                                    intent.putExtra(CONTACT_NUMBER_INTENT, true);
                                    intent.putExtra(CONTACT_NUMBER, "+" + etCountryCode.getSelectedCountryCode() + "-" + etEmail.getText().toString().trim());
                                    intent.putExtra(COUNTRY_CODE, etCountryCode.getSelectedCountryNameCode());
                                } else {
                                    intent.putExtra(CONTACT_NUMBER_INTENT, false);
                                    intent.putExtra(EXTRA_EMAIL, etEmail.getText().toString().trim());
                                }
                                startActivity(intent);
                            } else if (userLoginResponse.getStatusCode() == 201) { // 201 means user is a google account user
                                Intent intent = new Intent(IntroActivity.this, AlreadySignInGoogle.class);
                                intent.putExtra(EXTRA_EMAIL_ID, etEmail.getText().toString());
                                startActivity(intent);
                            } else if (userLoginResponse.getStatusCode() == 202) { // 202 means user is an existing user and requires to login
                                Intent intent = new Intent(IntroActivity.this, SignInActivity.class);
                                if (etCountryCode.getVisibility() == View.VISIBLE) {
                                    intent.putExtra(CONTACT_NUMBER, "+" + etCountryCode.getSelectedCountryCode() + "-" + etEmail.getText().toString().trim());
                                    intent.putExtra(COUNTRY_CODE, etCountryCode.getSelectedCountryNameCode());
                                } else {
                                    intent.putExtra(EXTRA_EMAIL_ID, etEmail.getText().toString().trim());
                                }
                                startActivity(intent);
                            } else if (userLoginResponse.getStatusCode() == 203) { // 203 means User has not set any password yet.
                                Intent intent = new Intent(IntroActivity.this, SetPasswordActivity.class);
                                intent.putExtra("email_token", userLoginResponse.getData().getSetPasswordEmailToken());
                                intent.putExtra(WORKSPACE, userLoginResponse.getData().getSetPasswordWorkspace());
                                if (etCountryCode.getVisibility() == View.VISIBLE) {
                                    intent.putExtra(CONTACT_NUMBER, "+" + etCountryCode.getSelectedCountryCode() + "-" + etEmail.getText().toString().trim());
                                    intent.putExtra(COUNTRY_CODE, etCountryCode.getSelectedCountryNameCode());
                                } else {
                                    intent.putExtra(EMAIL, etEmail.getText().toString().trim());
                                }
                                startActivity(intent);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginViaBranch();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.leftView:
                isLeftClicked = true;
                break;
            case R.id.rightView:
                isRightClicked = true;

                break;

            case R.id.sign_in_image:
                allViewsClicked();
                break;
            default:
                break;
        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> {
            isLeftClicked = false;
            isRightClicked = false;
        }, 3000);
        bothViewsClicked();
        return true;
    }

    public void bothViewsClicked() {
        if (isLeftClicked && isRightClicked) {
            realtiveviewContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            Handler handler = new Handler();
            handler.postDelayed(() -> realtiveviewContainer.setBackgroundColor(getResources().getColor(R.color.normal_bg_color)), 300);
        }
    }

    public void allViewsClicked() {
        if (isLeftClicked && isRightClicked) {
            openTestingTeamFrag();
        }
    }

    public void openTestingTeamFrag() {
        FragmentManager manager = (IntroActivity.this.getSupportFragmentManager());
        FragmentTransaction ft = manager.beginTransaction();
        TestcodeFragment newFragment = TestcodeFragment.newInstance(0);
        newFragment.show(ft, "TestcodeFragment");
    }
}
