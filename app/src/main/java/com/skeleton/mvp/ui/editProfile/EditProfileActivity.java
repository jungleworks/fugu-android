package com.skeleton.mvp.ui.editProfile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.EditPhoneActivity;
import com.skeleton.mvp.adapter.CustomContactsListAdapter;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.editprofile.EditProfileResponse;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.MultipartParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.ContactsList;
import com.skeleton.mvp.model.GetAllMembers;
import com.skeleton.mvp.model.getAllMembers.AllMember;
import com.skeleton.mvp.model.getAllMembers.AllMemberResponse;
import com.skeleton.mvp.model.userSearch.User;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.intro.IntroActivity;
import com.skeleton.mvp.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.FUGU_USER_ID;
import static com.skeleton.mvp.ui.AppConstants.FULL_NAME;
import static com.skeleton.mvp.ui.AppConstants.SEARCH_TEXT;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE_ID;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener {
    private FcCommonResponse fcCommonResponse;
    private EditText etName, etContact, etLocation, etDepartment, etDesignation;
    private AutoCompleteTextView etManager;
    private AppCompatImageView ivBack;
    private TextView tvSave, tvCancel;
    private String name, location, number, department, designation, manager, userId;
    private LinearLayout llAdmin;
    private String isAdmin = "";
    private LinearLayout llContact;
    private ImageView ivEditPhone;
    private ArrayList<GetAllMembers> allContacts = new ArrayList<>();
    private ArrayList<ContactsList> workspaceContactsList = new ArrayList<>();
    private ArrayList<ContactsList> contactsLists = new ArrayList<>();
    private CustomContactsListAdapter adapter;
    private HashMap<Long, com.skeleton.mvp.model.GetAllMembers> allMemberMap = new HashMap<>();
    private HashMap<Long, GetAllMembers> recentlyContactedMap = new HashMap<>();
    private ArrayList<GetAllMembers> allMembersArrayList = new ArrayList<>();
    private GetAllMembers getAllMembers;
    private Long profileUserId = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        etName = findViewById(R.id.etName);
        etContact = findViewById(R.id.etContact);
        etLocation = findViewById(R.id.etLocation);
        ivEditPhone = findViewById(R.id.ivEdit);
        etDepartment = findViewById(R.id.etDepartment);
        etDesignation = findViewById(R.id.etDesignation);
        etManager = findViewById(R.id.etManager);
        etLocation = findViewById(R.id.etLocation);
        ivBack = findViewById(R.id.ivBack);
        tvSave = findViewById(R.id.tvSave);
        tvCancel = findViewById(R.id.tvCancel);
        llAdmin = findViewById(R.id.llAdmin);
        llContact = findViewById(R.id.llContact);
        name = getIntent().getStringExtra("name");
        location = getIntent().getStringExtra("location");
        number = getIntent().getStringExtra("phone");
        department = getIntent().getStringExtra("department");
        designation = getIntent().getStringExtra("designation");
        manager = getIntent().getStringExtra("manager");
        userId = getIntent().getStringExtra("userId");
        isAdmin = getIntent().getStringExtra("isAdmin");
        userId = getIntent().getStringExtra("email");
        etName.setText(name);
        etContact.setText(number);
        etLocation.setText(location);
        etDesignation.setText(designation);
        etDepartment.setText(department);
        etManager.setText(manager);
        etName.clearFocus();
        etLocation.clearFocus();
        etContact.clearFocus();
        ivBack.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        ivEditPhone.setOnClickListener(this);
        if (isAdmin.equals("OWNER") || isAdmin.equals("ADMIN")) {
            llAdmin.setVisibility(View.VISIBLE);
        } else {
            llAdmin.setVisibility(View.GONE);
        }
        llContact.setVisibility(View.VISIBLE);
        etName.setSelection(etName.getText().length());
        etContact.setFocusable(false);
        etContact.setFocusableInTouchMode(false);

        etManager.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    tvSave.performClick();
                }
                return false;
            }
        });

        String etManagerText = etManager.getText().toString();
        if (isAdmin.equals("OWNER") || isAdmin.equals("ADMIN")) {
            new Thread(new Runnable() {
                @Override
                public void run() {


                    allMemberMap = com.skeleton.mvp.fugudatabase.CommonData.getPaperAllMembersMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                    allMembersArrayList = new ArrayList(allMemberMap.values());

                    etManager.setAdapter(adapter);

                    etManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String[] selectedItem = parent.getItemAtPosition(position).toString().split("-");
                            if (TextUtils.isDigitsOnly(selectedItem[selectedItem.length - 1])) {
                                etManager.setText(selectedItem[selectedItem.length - 1]);
                                etManager.setSelection(selectedItem[selectedItem.length - 1].length());
                            } else {
                                getAllMembers = (GetAllMembers) parent.getItemAtPosition(position);
                                etManager.setText(getAllMembers.getFullName());
                                etManager.setSelection(getAllMembers.getFullName().length());
                            }
                        }
                    });
                }
            }).start();
        }

        if (isAdmin.equals("OWNER") || isAdmin.equals("ADMIN")) {
            apiGetAllMembers();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvSave:
                if (isNetworkConnected()) {
                    if (!TextUtils.isEmpty(etName.getText().toString().trim())) {
                        showLoading();
                        apiEditProfile();
                    } else {
                        showErrorMessage("Please enter you name!");
                    }
                } else {
                    showErrorMessage(R.string.error_internet_not_connected);
                }
                break;
            case R.id.tvCancel:
                onBackPressed();
                break;

            case R.id.ivEdit:
                Intent intent = new Intent(EditProfileActivity.this, EditPhoneActivity.class);
                startActivityForResult(intent, 2003);
                break;
        }
    }

    private void apiEditProfile() {
        fcCommonResponse = CommonData.getCommonResponse();
        MultipartParams.Builder commonParams = new MultipartParams.Builder();
        if (TextUtils.isDigitsOnly(userId)) {
            commonParams.add(FUGU_USER_ID, userId);
        } else {
            commonParams.add(EMAIL, userId);
        }
        commonParams.add(FULL_NAME, etName.getText().toString().trim());
        commonParams.add(WORKSPACE_ID, fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        commonParams.add("location", etLocation.getText().toString().trim());
        if (isAdmin.equals("OWNER") || isAdmin.equals("ADMIN")) {
            commonParams.add("designation", etDesignation.getText().toString().trim());
            commonParams.add("department", etDepartment.getText().toString().trim());
            JSONObject managerDataJsonObject = new JSONObject();
            try {
                if ((isAdmin.equals("OWNER") || isAdmin.equals("ADMIN")) && getAllMembers == null) {
                    if (getIntent().hasExtra("managerUserId") && getIntent().hasExtra("managerFullName")) {
                        if (etManager.getText().toString().trim().toLowerCase().equals(getIntent().getStringExtra("managerFullName").toLowerCase().trim())) {
                            managerDataJsonObject.put("fugu_user_id", getIntent().getLongExtra("managerUserId", 0));
                            managerDataJsonObject.put("full_name", getIntent().getStringExtra("managerFullName"));
                        } else {
                            managerDataJsonObject.put("full_name", etManager.getText().toString().trim());
                        }
                    }
                } else if ((isAdmin.equals("OWNER") || isAdmin.equals("ADMIN")) && getAllMembers != null && etManager.getText().toString().trim().length() != 0 &&
                        !etManager.getText().toString().trim().equals(getAllMembers.getFullName())) {
                    managerDataJsonObject.put("full_name", etManager.getText().toString().trim());
                } else {
                    managerDataJsonObject.put("fugu_user_id", getAllMembers.getUserId());
                    managerDataJsonObject.put("full_name", getAllMembers.getFullName());
                }
                commonParams.add("manager_data", managerDataJsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        RestClient.getApiInterface(true).editProfile(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<EditProfileResponse>() {
                    @Override
                    public void onSuccess(EditProfileResponse editProfileResponse) {
                        try {
                            CommonData.updateFcmToken(FirebaseInstanceId.getInstance().getToken());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideLoading();
                                onBackPressed();
                            }
                        }, 2000);
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(EditProfileActivity.this);
                            finishAffinity();
                            startActivity(new Intent(EditProfileActivity.this, IntroActivity.class));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 2003 && resultCode == RESULT_OK) {
                etContact.setText(data.getStringExtra("phone"));
            }
        }
    }

    private void apiGetAllMembers() {
        CommonParams commonParams = new CommonParams.Builder().add("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getWorkspaceId())
                .add("user_type", "ALL_MEMBERS")
                .add("user_status", "ENABLED")
                .build();

        RestClient.getApiInterface(true).getAllMembers(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap())
                .enqueue(new ResponseResolver<AllMemberResponse>() {

                    @Override
                    public void onSuccess(AllMemberResponse allMemberResponse) {

                        allMemberMap = new LinkedHashMap<>();
                        allMembersArrayList = new ArrayList<>();

                        String myEmail = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getEmail();
                        String myNumber = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getContactNumber();

                        Long myuserId = Long.parseLong(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getUserId());
                        if (getIntent().hasExtra("userId")) {
                            profileUserId = Long.parseLong(getIntent().getStringExtra("userId"));
                        }
                        for (int i = 0; i < allMemberResponse.getData().getAllMemberResponse().size(); i++) {
                            AllMember allMember = allMemberResponse.getData().getAllMemberResponse().get(i);
                            if (allMember.getFuguUserId() != null && (!TextUtils.isEmpty(allMember.getEmail()) || !TextUtils.isEmpty(allMember.getContactNumber()) || !TextUtils.isEmpty(allMember.getFullName()))) {
                                if (allMember.getFuguUserId().compareTo(profileUserId) != 0) {
                                    GetAllMembers getAllMembers = new GetAllMembers(allMember.getFuguUserId(), allMember.getFullName(),
                                            allMember.getEmail(),
                                            allMember.getUserImage(),
                                            allMember.getUserThumbnailImage(),
                                            allMember.getRole(),
                                            0, allMember.getContactNumber(),"");
                                    allMemberMap.put(allMember.getFuguUserId(), getAllMembers);
                                }
                            }
                        }

                        allMembersArrayList = new ArrayList(allMemberMap.values());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                com.skeleton.mvp.fugudatabase.CommonData.setPaperAllMembersMap(allMemberMap, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                            }
                        }).start();

                        adapter = new CustomContactsListAdapter(EditProfileActivity.this,
                                R.layout.simple_dropdown_two_line, allMembersArrayList);

                        etManager.setAdapter(adapter);
                        etManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String[] selectedItem = parent.getItemAtPosition(position).toString().split("-");
                                if (TextUtils.isDigitsOnly(selectedItem[selectedItem.length - 1])) {
                                    etManager.setText(selectedItem[selectedItem.length - 1]);
                                    etManager.setSelection(selectedItem[selectedItem.length - 1].length());
                                } else {
                                    getAllMembers = (GetAllMembers) parent.getItemAtPosition(position);
                                    etManager.setText(getAllMembers.getFullName());
                                    etManager.setSelection(getAllMembers.getFullName().length());
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(ApiError error) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("Test", "test");
                    }
                });
    }


    public void apiUserSearch(String searchStrLowerCase) {
        WorkspacesInfo workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition());
        com.skeleton.mvp.retrofit.CommonParams.Builder commonParams = new com.skeleton.mvp.retrofit.CommonParams.Builder();
        commonParams.add(EN_USER_ID, workspaceInfo.getEnUserId());
        commonParams.add(SEARCH_TEXT, searchStrLowerCase);
        commonParams.add("include_current_user", true);
        RestClient.getApiInterface(true).userSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.build().getMap())
                .enqueue(new ResponseResolver<com.skeleton.mvp.model.userSearch.UserSearch>() {
                    @Override
                    public void onSuccess(com.skeleton.mvp.model.userSearch.UserSearch userSearch) {
                        ArrayList<GetAllMembers> matchValues = new ArrayList<>();
                        for (User user : userSearch.getData().getUsers()) {
                            if (user.getUserId().compareTo(profileUserId) != 0) {
                                GetAllMembers getAllMembers = new GetAllMembers(user.getUserId(), user.getFullName(),
                                        user.getEmail(),
                                        user.getUserImage(),
                                        user.getUserThumbnailImage(),
                                        user.getRole(),
                                        0, user.getContactNumber(),"");
                                matchValues.add(getAllMembers);
                            }
                        }
                        adapter.updateList(matchValues);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ApiError error) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }
}
