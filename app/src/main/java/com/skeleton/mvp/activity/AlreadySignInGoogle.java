package com.skeleton.mvp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.googleSignin.GoogleSigninResponse;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.utils.FuguUtils;
import com.skeleton.mvp.utils.UniqueIMEIID;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.DOMAIN;
import static com.skeleton.mvp.ui.AppConstants.SOURCE;
import static com.skeleton.mvp.ui.AppConstants.TOKEN;

public class AlreadySignInGoogle extends BaseActivity {
    private SignInButton googleSignInBtn;
    private TextView tvEmailid;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_sign_in_google);
        initViews();
        tvEmailid.setText(getIntent().getStringExtra("emailId"));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("1067615629789-qs9p7v7o3ur01tf7oanq3ds4k9ashmjh.apps.googleusercontent.com")
                .requestServerAuthCode("1067615629789-qs9p7v7o3ur01tf7oanq3ds4k9ashmjh.apps.googleusercontent.com")
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if (resultCode == Activity.RESULT_OK)
        switch (requestCode) {
            case 101:
                try {

                    //Log.i("onActivityResult","onActivityResult");
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.i("Name",account.getDisplayName());
                    onLoggedIn(account);
                } catch (ApiException e) {
                    // The ApiException status code indicates the detailed failure reason.
                    Log.i("Sign In Failed", "signInResult:failed code=" + e.getStatusCode());
                }
                break;
        }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Log.i("idtoken",googleSignInAccount.getIdToken());
        Log.i("AuthCode",googleSignInAccount.getServerAuthCode());
        Log.i("Sign In","Successfull");

        apiLoginViaGoogle(googleSignInAccount.getServerAuthCode());

    }

    private void apiLoginViaGoogle(String serverAuthCode){

        CommonParams.Builder googleSignInParams = new CommonParams.Builder();
        googleSignInParams.add("authorized_code", serverAuthCode);
        showLoading();
        RestClient.getApiInterface(true).googleSignIn(BuildConfig.VERSION_CODE, ANDROID, googleSignInParams.build().getMap())
                .enqueue(new ResponseResolver<GoogleSigninResponse>() {
                    @Override
                    public void onSuccess(GoogleSigninResponse googleSigninResponse) {
                        apiLoginViaAccessToken("",googleSigninResponse.getData().getAccessToken(),"",true);
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

    private void apiLoginViaAccessToken(String source, String accessToken, final String workspaceName, boolean isSignInFromGoogle) {
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add(TOKEN, CommonData.getFcmToken());
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
        commonParams.add("time_zone", FuguUtils.Companion.getTimeZoneOffset());
        if (!TextUtils.isEmpty(source)) {
            commonParams.add(SOURCE, source);
        }
        RestClient.getApiInterface(true).accessTokenLogin(accessToken, BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<FcCommonResponse>() {
                    @Override
                    public void onSuccess(FcCommonResponse fcCommonResponse) {
                        hideLoading();
                        checkVersion(fcCommonResponse, workspaceName, isSignInFromGoogle);
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == 401) {
                            CommonData.clearData();

                            FuguConfig.clearFuguData(AlreadySignInGoogle.this);
                            //llButtons.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                        if (throwable instanceof SocketTimeoutException) {
                            showErrorMessage("Please check your Internet connection and Try Again !", new OnErrorHandleCallback() {
                                @Override
                                public void onErrorCallback() {
                                    FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
                                    showLoading();
                                    apiLoginViaAccessToken("", fcCommonResponse.getData().getUserInfo().getAccessToken(), "",isSignInFromGoogle);
                                }
                            });
                        }
                    }
                });
    }

    private void checkVersion(final FcCommonResponse fcCommonResponse, final String workspaceName, final boolean isSignInFromGoogle) {
        hideLoading();
        com.skeleton.mvp.fugudatabase.CommonData.setSupportedFileTypes(fcCommonResponse.getData().getFuguConfig().getSupportedFileType());
        try {
            switch (fcCommonResponse.getData().getUserInfo().getAppUpdateConfig().getAppUpdateMessage()) {
               // case HARD_UPDATE:
                    //showUpdateMessage(fcCommonResponse, HARD_UPDATE, "Exit", workspaceName);
                    //break;
                //case SOFT_UPDATE:
                    //showUpdateMessage(fcCommonResponse, SOFT_UPDATE, "Cancel", workspaceName);
                   // break;
                default:
                    if(isSignInFromGoogle){
                        CommonData.setCommonResponse(fcCommonResponse);
                        Intent yourWorkspacesIntent;
                        if((fcCommonResponse.getData().getInvitationToWorkspaces().size()
                                + fcCommonResponse.getData().getOpenWorkspacesToJoin().size()) == 0
                                && fcCommonResponse.getData().getWorkspacesInfo().size() == 1){
                            yourWorkspacesIntent = new Intent(AlreadySignInGoogle.this, MainActivity.class);
                        }
                        else if(fcCommonResponse.getData().getInvitationToWorkspaces().size() == 0 &&
                                fcCommonResponse.getData().getOpenWorkspacesToJoin().size() == 0 &&
                                fcCommonResponse.getData().getWorkspacesInfo().size() == 0){
                            yourWorkspacesIntent = new Intent(AlreadySignInGoogle.this, CreateWorkspaceActivity.class);
                        }
                        else if (fcCommonResponse.getData().getWorkspacesInfo().size() > 0) {
                            yourWorkspacesIntent = new Intent(AlreadySignInGoogle.this, MainActivity.class);
                        } else {
                            yourWorkspacesIntent = new Intent(AlreadySignInGoogle.this, YourSpacesActivity.class);
                        }

                        startActivity(yourWorkspacesIntent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                    //else{
                       // getInsideApp(fcCommonResponse, workspaceName);
                    //}
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void initViews(){
        googleSignInBtn = findViewById(R.id.sign_in_button);
        googleSignInBtn.setSize(SignInButton.SIZE_WIDE);
        googleSignInBtn.setColorScheme(SignInButton.COLOR_DARK);
        tvEmailid = findViewById(R.id.tv_emailid);
        tvEmailid.setText(Html.fromHtml(getString(R.string.google_authorization_string)));
    }
}
