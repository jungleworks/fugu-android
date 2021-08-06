package com.skeleton.mvp.ui.profile;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.activity.EditPhoneActivity;
import com.skeleton.mvp.activity.ImageDisplayActivity;
import com.skeleton.mvp.activity.MediaActivity;
import com.skeleton.mvp.activity.MembersSearchActivity;
import com.skeleton.mvp.activity.ShowMoreActivity;
import com.skeleton.mvp.adapter.CustomContactsListAdapter;
import com.skeleton.mvp.adapter.GalleryAdapter;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.editprofile.EditProfileResponse;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.model.getUserInfo.Data;
import com.skeleton.mvp.data.model.getUserInfo.GetInfoResponse;
import com.skeleton.mvp.data.model.onetoone.CreateChatResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.MultipartParams;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.model.FuguFileDetails;
import com.skeleton.mvp.model.GetAllMembers;
import com.skeleton.mvp.model.Image;
import com.skeleton.mvp.model.Media;
import com.skeleton.mvp.model.getAllMembers.AllMember;
import com.skeleton.mvp.model.getAllMembers.AllMemberResponse;
import com.skeleton.mvp.model.media.MediaResponse;
import com.skeleton.mvp.model.userSearch.User;
import com.skeleton.mvp.model.userSearch.UserSearch;
import com.skeleton.mvp.retrofit.APIError;
import com.skeleton.mvp.retrofit.CommonParams;
import com.skeleton.mvp.retrofit.ResponseResolver;
import com.skeleton.mvp.retrofit.RestClient;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.dialog.CustomAlertDialog;
import com.skeleton.mvp.ui.intro.IntroActivity;
import com.skeleton.mvp.util.KeyboardUtil;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.util.ValidationUtil;
import com.skeleton.mvp.utils.FuguImageUtils;
import com.skeleton.mvp.utils.StringUtil;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.OPEN_CAMERA_ADD_IMAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.REQUEST_CODE_PICK_AUDIO;
import static com.skeleton.mvp.constant.FuguAppConstant.REQUEST_CODE_PICK_FILE;
import static com.skeleton.mvp.constant.FuguAppConstant.REQUEST_CODE_PICK_IMAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.REQUEST_CODE_PICK_VIDEO;
import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.CHAT_WITH_USER_ID;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.FUGU_USER_ID;
import static com.skeleton.mvp.ui.AppConstants.FULL_NAME;
import static com.skeleton.mvp.ui.AppConstants.SEARCH_TEXT;
import static com.skeleton.mvp.ui.AppConstants.SELECTED_MEMBERS;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE_ID;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private AppCompatEditText tvEmail, tvName, tvPhone, tvLocation, tvDesignation, tvDepartment;
    private AutoCompleteTextView tvManager;
    private TextView tvGuest, tvSave;
    private FcCommonResponse fcCommonResponse;
    private AppCompatImageView ivBack;
    private AppCompatImageView ivEdit;
    private boolean isEditMode = false;
    private AppCompatImageView ivProfile;
    int PERMISSION_CONSTANT_CAMERA = 9;
    int PERMISSION_CONSTANT_GALLERY = 8;
    private String userId, email;
    private AppCompatImageView tvEditProfile;
    private AppCompatButton btnDeactivate;
    private boolean isAdmin = false;
    private Data datum;
    private Handler handler = new Handler();
    private boolean enabled = false;
    private String status = "ENABLED";
    private RecyclerView rvMedia;
    private GalleryAdapter mediaAdapter;
    private ArrayList<Media> mediaList = new ArrayList<>();
    private int mediaCount = 0;
    private LinearLayout llMedia;
    private LinearLayout tvShowMore;
    private String extension;
    private String hideEmail = "", hidePhone = "";
    private LinearLayout llEmail, llPhone, llLocation, llDepartMent, llDesignation, llManager;
    private boolean isEmail = false;
    boolean isSelf = false;
    private String name;
    private TextView tvCg, tvDm;
    private ImageView ivCg, ivDm;
    private String image;
    private LinearLayout llDirectMessage;
    private FuguImageUtils fuguImageUtils = null;
    private Long managerUserId;
    private String managerFullName;
    private AppCompatTextView tvVersion;
    private View ivImage;
    private HashMap<Long, com.skeleton.mvp.model.GetAllMembers> allMemberMap = new HashMap<>();
    private ArrayList<GetAllMembers> allMembersArrayList = new ArrayList<>();
    private GetAllMembers getAllMembers;
    private String isAdminText = "";
    private Long profileUserId = 0L;
    private CustomContactsListAdapter adapter;
    private LinearLayout ivImageEdit;
    private String currentUserRole;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        fcCommonResponse = CommonData.getCommonResponse();
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvGuest = findViewById(R.id.tvGuest);
        tvPhone = findViewById(R.id.tvPhone);
        tvLocation = findViewById(R.id.tvLocation);
        tvManager = findViewById(R.id.tvManager);
        tvEditProfile = findViewById(R.id.tvEditProfile);
        tvDesignation = findViewById(R.id.tvDesignation);
        tvDepartment = findViewById(R.id.tvDepartment);
        btnDeactivate = findViewById(R.id.btnDeactivate);
        ivEdit = findViewById(R.id.ivEdit);
        ivProfile = findViewById(R.id.ivProfile);
        llMedia = findViewById(R.id.llMedia);
        tvShowMore = findViewById(R.id.tvShowMore);
        llEmail = findViewById(R.id.llEmail);
        llPhone = findViewById(R.id.llPhone);
        llLocation = findViewById(R.id.llLocation);
        llDepartMent = findViewById(R.id.llDepartment);
        llDesignation = findViewById(R.id.llDesignation);
        llManager = findViewById(R.id.llManager);
        tvVersion = findViewById(R.id.tvVersion);
        ivImage = findViewById(R.id.ivImage);
        ivImageEdit = findViewById(R.id.ivImageEdit);
        currentUserRole = fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getRole();
        tvEmail.setOnLongClickListener(v -> {
            if (!TextUtils.isEmpty(tvEmail.getText()) && !isEditMode) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", tvEmail.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(ProfileActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT);
                TextView tv = toast.getView().findViewById(android.R.id.message);
                if (tv != null) tv.setGravity(Gravity.CENTER);
                toast.show();
                return true;
            } else {
                return false;
            }
        });

        if (!getIntent().hasExtra("isEditable")) {
            tvEmail.setFocusable(false);
            tvEmail.setFocusableInTouchMode(false);
            tvEmail.getBackground().setColorFilter(Color.parseColor("#f9f9f9"), PorterDuff.Mode.SRC_IN);
//            tvEmail.setBackgroundColor(Color.TRANSPARENT);

            tvName.setFocusable(false);
            tvName.setFocusableInTouchMode(false);
            tvName.getBackground().setColorFilter(Color.parseColor("#f9f9f9"), PorterDuff.Mode.SRC_IN);


            tvPhone.setFocusable(false);
            tvPhone.setFocusableInTouchMode(false);
            tvPhone.getBackground().setColorFilter(Color.parseColor("#f9f9f9"), PorterDuff.Mode.SRC_IN);
//            tvPhone.setBackgroundColor(Color.TRANSPARENT);

            tvLocation.setFocusable(false);
            tvLocation.setFocusableInTouchMode(false);
            tvLocation.getBackground().setColorFilter(Color.parseColor("#f9f9f9"), PorterDuff.Mode.SRC_IN);
//            tvLocation.setBackgroundColor(Color.TRANSPARENT);

            tvDesignation.setFocusable(false);
            tvDesignation.setFocusableInTouchMode(false);
            tvDesignation.getBackground().setColorFilter(Color.parseColor("#f9f9f9"), PorterDuff.Mode.SRC_IN);
//            tvDesignation.setBackgroundColor(Color.TRANSPARENT);

            tvDepartment.setFocusable(false);
            tvDepartment.setFocusableInTouchMode(false);
            tvDepartment.getBackground().setColorFilter(Color.parseColor("#f9f9f9"), PorterDuff.Mode.SRC_IN);
//            tvDepartment.setBackgroundColor(Color.TRANSPARENT);

            tvManager.setFocusable(false);
            tvManager.setFocusableInTouchMode(false);
            tvManager.getBackground().setColorFilter(Color.parseColor("#f9f9f9"), PorterDuff.Mode.SRC_IN);
//            tvManager.setBackgroundColor(Color.TRANSPARENT);


        } else {
            tvSave = findViewById(R.id.tvSave);
            tvSave.setVisibility(View.VISIBLE);
            tvSave.setOnClickListener(v -> {
                showLoading();
                apiEditProfile();
            });
        }
        tvLocation.setMovementMethod(null);


        ivImage.setOnClickListener(v -> ivProfile.performClick());


        LinearLayout llToolbar = findViewById(R.id.llToolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);


//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            int scrollRange = -1;
//            boolean isShow = false;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    isShow = true;
//                    llToolbar.setVisibility(View.VISIBLE);
//                    collapsingToolbarLayout.setTitle("Profile");
//                } else if (isShow) {
//                    isShow = false;
//                    llToolbar.setVisibility(View.GONE);
//                    collapsingToolbarLayout.setTitle(" ");
//                }
//                Log.e("verticalOffset", verticalOffset + "");
//            }
//        });

        try {
            userId = getIntent().getStringExtra("open_profile");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (userId != null && userId.equals(fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId())) {
                tvVersion.setText("V. " + BuildConfig.VERSION_NAME);
            }
            if (getIntent().hasExtra("isEditable")) {
                tvVersion.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            tvVersion.setVisibility(View.GONE);
        }


        fuguImageUtils = new FuguImageUtils(this);
        fuguImageUtils.setCallbaks(FuguAppConstant.OPEN_CAMERA_ADD_IMAGE, REQUEST_CODE_PICK_IMAGE, REQUEST_CODE_PICK_FILE,
                REQUEST_CODE_PICK_AUDIO, REQUEST_CODE_PICK_VIDEO, FuguAppConstant.START_POLL, true, true);
        ivProfile.setOnClickListener(this);
        ivDm = findViewById(R.id.ivDm);
        tvDm = findViewById(R.id.tvDm);
        ivCg = findViewById(R.id.ivCg);
        tvCg = findViewById(R.id.tvCg);
        llDirectMessage = findViewById(R.id.llDirectMessage);
        ivDm.setOnClickListener(this);
        ivCg.setOnClickListener(this);
        tvDm.setOnClickListener(this);
        tvCg.setOnClickListener(this);
        if (ValidationUtil.checkEmail(userId)) {
            isEmail = true;
            email = userId;
            if (userId.equals(fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId())
                    || currentUserRole.equals("OWNER")
                    || currentUserRole.equals("ADMIN")) {
                isSelf = true;
                //Editable
                llDirectMessage.setVisibility(View.GONE);
                ivEdit.setVisibility(View.VISIBLE);
                tvEditProfile.setOnClickListener(this);
                tvEditProfile.setVisibility(View.GONE);
            } else {
                isSelf = false;
                ivEdit.setVisibility(View.GONE);
                tvEditProfile.setOnClickListener(null);
                tvEditProfile.setVisibility(View.GONE);
            }
        } else {
            isEmail = false;
            if (userId.equals(fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId())
                    || currentUserRole.equals("OWNER")
                    || currentUserRole.equals("ADMIN")) {
                isSelf = true;
                //Editable
                llDirectMessage.setVisibility(View.GONE);
                ivEdit.setVisibility(View.VISIBLE);
                tvEditProfile.setOnClickListener(this);
                tvEditProfile.setVisibility(View.GONE);
            } else {
                isSelf = false;
                ivEdit.setVisibility(View.GONE);
                tvEditProfile.setOnClickListener(null);
                tvEditProfile.setVisibility(View.GONE);
            }
        }
        showLoading();
        apiGetInfo();

        ivBack = findViewById(R.id.ivBack);
        ivEdit = findViewById(R.id.ivEdit);
        ivBack.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        tvPhone.setOnClickListener(this);
        btnDeactivate.setOnClickListener(this);
        rvMedia = findViewById(R.id.rvMedia);
        rvMedia.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mediaAdapter = new GalleryAdapter(this, mediaList);
        rvMedia.setAdapter(mediaAdapter);
//        apiGetMedia();
    }

    public void apiUserSearch(String searchStrLowerCase, CustomContactsListAdapter.UserSearch userSearchInterface) {
        WorkspacesInfo workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition());
        com.skeleton.mvp.retrofit.CommonParams.Builder commonParams = new com.skeleton.mvp.retrofit.CommonParams.Builder();
        commonParams.add(EN_USER_ID, workspaceInfo.getEnUserId());
        commonParams.add(SEARCH_TEXT, searchStrLowerCase);
        commonParams.add("include_current_user", true);
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).userSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.build().getMap())
                .enqueue(new com.skeleton.mvp.data.network.ResponseResolver<UserSearch>() {
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
                                        0, user.getContactNumber(), "");
                                matchValues.add(getAllMembers);
                            }
                        }
                        userSearchInterface.onSuccess(matchValues);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.edit_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == R.id.edit) {
//            ivEdit.performClick();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void apiEditProfile() {
        fcCommonResponse = CommonData.getCommonResponse();
        MultipartParams.Builder commonParams = new MultipartParams.Builder();
        if (TextUtils.isDigitsOnly(userId)) {
            commonParams.add(FUGU_USER_ID, userId);
        } else {
            commonParams.add(EMAIL, userId);
        }
        commonParams.add(FULL_NAME, tvName.getText().toString().trim());
        commonParams.add(WORKSPACE_ID, fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        commonParams.add("location", tvLocation.getText().toString().trim());
        if (isAdminText.equals("OWNER") || isAdminText.equals("ADMIN")) {
            commonParams.add("designation", tvDesignation.getText().toString().trim());
            commonParams.add("department", tvDepartment.getText().toString().trim());
            JSONObject managerDataJsonObject = new JSONObject();
            try {
                if ((isAdminText.equals("OWNER") || isAdminText.equals("ADMIN")) && getAllMembers == null) {
                    if (tvManager.getText().toString().trim().toLowerCase().equals(managerFullName.toLowerCase().trim())) {
                        managerDataJsonObject.put("fugu_user_id", managerUserId);
                        managerDataJsonObject.put("full_name", managerFullName);
                    } else {
                        managerDataJsonObject.put("full_name", tvManager.getText().toString().trim());
                    }
                } else if ((isAdminText.equals("OWNER") || isAdminText.equals("ADMIN")) && getAllMembers != null && tvManager.getText().toString().trim().length() != 0 &&
                        !tvManager.getText().toString().trim().equals(getAllMembers.getFullName())) {
                    managerDataJsonObject.put("full_name", tvManager.getText().toString().trim());
                } else {
                    managerDataJsonObject.put("fugu_user_id", getAllMembers.getUserId());
                    managerDataJsonObject.put("full_name", getAllMembers.getFullName());
                }
                commonParams.add("manager_data", managerDataJsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).editProfile(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new com.skeleton.mvp.data.network.ResponseResolver<EditProfileResponse>() {
                    @Override
                    public void onSuccess(EditProfileResponse editProfileResponse) {
                        try {
                            CommonData.updateFcmToken(FirebaseInstanceId.getInstance().getToken());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new Handler().postDelayed(() -> {
                            hideLoading();
                            onBackPressed();
                        }, 2000);
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(ProfileActivity.this);
                            finishAffinity();
                            startActivity(new Intent(ProfileActivity.this, IntroActivity.class));
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

    private void apiGetInfo() {

        HashMap<String, String> paramMap = new HashMap<>();

        if (isEmail) {
            paramMap.put(EMAIL, email);
        } else {
            paramMap.put("fugu_user_id", userId);
        }

        paramMap.put(WORKSPACE_ID, String.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId()));
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).getUserInfo(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE,
                ANDROID, paramMap)
                .enqueue(new com.skeleton.mvp.data.network.ResponseResolver<GetInfoResponse>() {
                    @Override
                    public void onSuccess(GetInfoResponse getInfoResponse) {
                        hideLoading();


                        managerUserId = getInfoResponse.getData().getManagerData().getManagerUserId();
                        managerFullName = getInfoResponse.getData().getManagerData().getManagerFullName();
                        hideEmail = fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getConfig().getHideEmail();
                        hidePhone = fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getConfig().getHideContactNumber();
                        isAdminText = currentUserRole;


                        if (isAdminText.equals("OWNER") || isAdminText.equals("ADMIN")) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
//                                    runOnUiThread(() -> {
//                                        tvManager.setFocusable(true);
//                                        tvManager.setFocusableInTouchMode(true);
//                                    });

                                    allMemberMap = com.skeleton.mvp.fugudatabase.CommonData.getPaperAllMembersMap(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                                    allMembersArrayList = new ArrayList(allMemberMap.values());

                                    tvManager.setAdapter(adapter);

                                    tvManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String[] selectedItem = parent.getItemAtPosition(position).toString().split("-");
                                            if (TextUtils.isDigitsOnly(selectedItem[selectedItem.length - 1])) {
                                                tvManager.setText(selectedItem[selectedItem.length - 1]);
                                                tvManager.setSelection(selectedItem[selectedItem.length - 1].length());
                                            } else {
                                                getAllMembers = (GetAllMembers) parent.getItemAtPosition(position);
                                                tvManager.setText(getAllMembers.getFullName());
                                                tvManager.setSelection(getAllMembers.getFullName().length());
                                            }
                                        }
                                    });
                                }
                            }).start();
                        } else {
                            tvManager.setFocusable(false);
                            tvManager.setBackgroundColor(Color.TRANSPARENT);
                        }

                        if (isAdminText.equals("OWNER") || isAdminText.equals("ADMIN")) {
                            apiGetAllMembers();
                        }


                        if (!getInfoResponse.getData().getEmail().equals(fcCommonResponse.getData().getUserInfo().getEmail())) {
                            if (!isSelf) {
                                if (hideEmail.equals("1")) {
                                    llEmail.setVisibility(View.GONE);
                                } else {
                                    if (!TextUtils.isEmpty(getInfoResponse.getData().getEmail()) || isSelf) {
                                        llEmail.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                                if (!TextUtils.isEmpty(getInfoResponse.getData().getEmail()) || isSelf) {
                                    llEmail.setVisibility(View.VISIBLE);
                                }
                            }
                            if (!isSelf) {
                                if (hidePhone.equals("1")) {
                                    llPhone.setVisibility(View.GONE);
                                } else {
                                    if (!TextUtils.isEmpty(getInfoResponse.getData().getContactNumber()) || isSelf) {
                                        llPhone.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                                if (!TextUtils.isEmpty(getInfoResponse.getData().getContactNumber()) || isSelf) {
                                    llPhone.setVisibility(View.VISIBLE);

                                } else {
                                    llPhone.setVisibility(View.VISIBLE);
                                }
                            }
                            if (!isSelf) {
                                if (!TextUtils.isEmpty(getInfoResponse.getData().getDepartment())) {
                                    llDepartMent.setVisibility(View.VISIBLE);
                                } else {
                                    llDepartMent.setVisibility(View.GONE);
                                }
                            }

                            if (!isSelf) {
                                if (!TextUtils.isEmpty(getInfoResponse.getData().getDesignation())) {
                                    llDesignation.setVisibility(View.VISIBLE);
                                } else {
                                    llDesignation.setVisibility(View.GONE);
                                }
                            }
                            if (!isSelf) {
                                if (!TextUtils.isEmpty(getInfoResponse.getData().getManager())) {
                                    llManager.setVisibility(View.VISIBLE);
                                } else {
                                    llManager.setVisibility(View.GONE);
                                }
                            }
                            if (!isSelf) {
                                if (!TextUtils.isEmpty(getInfoResponse.getData().getLocation())) {
                                    llLocation.setVisibility(View.VISIBLE);
                                } else {
                                    llLocation.setVisibility(View.GONE);
                                }
                            }


                        } else {
                            llEmail.setVisibility(View.VISIBLE);
                            llEmail.setVisibility(View.VISIBLE);
                            llPhone.setVisibility(View.VISIBLE);
                        }
                        datum = getInfoResponse.getData();
                        status = datum.getStatus();
                        if (datum.getStatus().equals("ENABLED")) {
                            ivProfile.setAlpha(1f);
                            tvName.setAlpha(1f);
                            if (getIntent().hasExtra("no_chat")) {
                                llDirectMessage.setVisibility(View.GONE);
                            } else {
                                if (!isEmail) {
                                    llDirectMessage.setVisibility(View.VISIBLE);
                                } else {
                                    llDirectMessage.setVisibility(View.GONE);
                                }
                            }

                            if (getDirectMessageRoles().contains(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getRole())) {
                                if (getIntent().hasExtra("onlyGroup")) {
                                    ivDm.setVisibility(View.INVISIBLE);
                                    tvDm.setVisibility(View.INVISIBLE);
                                } else {
                                    ivDm.setVisibility(View.VISIBLE);
                                    tvDm.setVisibility(View.VISIBLE);
                                }
                            } else {
                                ivDm.setVisibility(View.GONE);
                                tvDm.setVisibility(View.GONE);
                            }

                        } else {
                            ivProfile.setAlpha(0.5f);
                            tvName.setAlpha(0.5f);
                            llDirectMessage.setVisibility(View.GONE);
                        }
                        if (!datum.getEmail().equals(fcCommonResponse.getData().getUserInfo().getEmail())) {
                            if (currentUserRole.equals("OWNER")
                                    || currentUserRole.equals("ADMIN")) {
                                enabled = true;
                                if (datum.getStatus().equals("ENABLED")) {
                                    btnDeactivate.setText("Deactivate " + datum.getFullName());
                                    btnDeactivate.setBackgroundResource(R.drawable.curved_button_red);
                                    btnDeactivate.setTextColor(Color.WHITE);
                                    ivProfile.setAlpha(1f);
                                    //Editable
                                    llDirectMessage.setVisibility(View.GONE);
                                    ivEdit.setVisibility(View.VISIBLE);
                                    tvEditProfile.setVisibility(View.GONE);
                                    tvName.setAlpha(1f);
                                } else {
                                    btnDeactivate.setText("Activate " + datum.getFullName());
                                    btnDeactivate.setBackgroundResource(R.drawable.curved_button_green);
                                    btnDeactivate.setTextColor(Color.WHITE);
                                    ivProfile.setAlpha(0.5f);
                                    ivEdit.setVisibility(View.GONE);
                                    tvEditProfile.setVisibility(View.GONE);
                                    tvName.setAlpha(0.5f);
                                }
                            }
                            if (datum.getRole().equals("OWNER")) {
                                enabled = false;
                            }
                        } else {
                            enabled = false;
                        }
                        if (enabled) {
                            btnDeactivate.setVisibility(View.VISIBLE);
                        } else {
                            btnDeactivate.setVisibility(View.GONE);
                        }
                        if (fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getConfig().getEnablePublicInvite().equals("1")) {
                            btnDeactivate.setVisibility(View.GONE);
                        }
                        if (datum.getUserImage() != null) {
                            if (this != null) {
                                try {


                                    RequestOptions options = new RequestOptions()
                                            .centerCrop()
                                            .dontAnimate()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .placeholder(R.drawable.profile_placeholder)
                                            .error(R.drawable.profile_placeholder)
                                            .fitCenter()
                                            .priority(Priority.HIGH);
                                    if (!isFinishing()) {
                                        Glide.with(ProfileActivity.this)
                                                .asBitmap()
                                                .apply(options)
                                                .load(datum.getUserImage())
                                                .into(ivProfile);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        image = datum.getUserImage();
                        handler = new Handler();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (handler != null) {
                                    if (this != null) {
                                        try {

                                            RequestOptions options = new RequestOptions()
                                                    .centerCrop()
                                                    .dontAnimate()
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .placeholder(R.drawable.profile_placeholder)
                                                    .error(R.drawable.profile_placeholder)
                                                    .fitCenter()
                                                    .priority(Priority.HIGH);
                                            if (!isFinishing()) {
                                                Glide.with(ProfileActivity.this)
                                                        .asBitmap()
                                                        .apply(options)
                                                        .load(datum.getUserImage())

                                                        .into(ivProfile);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }, 2000);
                        if (!TextUtils.isEmpty(datum.getFullName())) {
                            tvName.setText(datum.getFullName());
                            name = datum.getFullName();
                            tvName.setSelection(tvName.getText().length());
                        } else {
                            tvName.setText(CommonData.getCommonResponse().getData().getUserInfo().getFullName());
                            name = CommonData.getCommonResponse().getData().getUserInfo().getFullName();
                        }
                        tvEmail.setText(datum.getEmail().endsWith("@junglework.auth") ? "" : datum.getEmail());
                        if (!TextUtils.isEmpty(datum.getContactNumber())) {
                            tvPhone.setText(datum.getContactNumber());
                        } else {
                            tvPhone.setHint("Add Phone");
                            tvPhone.setHintTextColor(Color.parseColor("#b3bec9"));
                            tvPhone.setVisibility(View.VISIBLE);
                        }

                        if (!TextUtils.isEmpty(datum.getLocation())) {
                            tvLocation.setText(datum.getLocation());
                        } else {
                            if (isSelf) {
                                tvLocation.setHint("Add Location");
                                tvLocation.setHintTextColor(Color.parseColor("#b3bec9"));
                                tvLocation.setVisibility(View.VISIBLE);
                                tvLocation.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        maketextViewEditable(tvLocation, "Add Location");
                                    }
                                });
                            } else {
                                tvLocation.setText("");
                            }
                        }
                        if (!TextUtils.isEmpty(datum.getDesignation())) {
                            tvDesignation.setText(datum.getDesignation());
                            tvDesignation.setVisibility(View.VISIBLE);
                        } else {
                            if (isSelf) {
                                tvDesignation.setHint("Add Designation");
                                tvDesignation.setHintTextColor(Color.parseColor("#b3bec9"));
                                tvDesignation.setVisibility(View.VISIBLE);
                                tvDesignation.setOnClickListener(v -> maketextViewEditable(tvDesignation, "Add Designation"));
                            } else {
                                tvDesignation.setText("");
                            }

                        }
                        if (!TextUtils.isEmpty(datum.getDepartment())) {
                            tvDepartment.setText(datum.getDepartment());
                            tvDepartment.setVisibility(View.VISIBLE);
                        } else {
                            if (isSelf) {
                                tvDepartment.setHint("Add Department");
                                tvDepartment.setHintTextColor(Color.parseColor("#b3bec9"));
                                tvDepartment.setVisibility(View.VISIBLE);
                                tvDepartment.setOnClickListener(v -> maketextViewEditable(tvDepartment, "Add Department"));
                            } else {
                                tvDepartment.setText("");
                            }
                        }
                        if (!TextUtils.isEmpty(datum.getManager())) {
                            //tvManager.setText(datum.getManager());
                            tvManager.setText(datum.getManagerData().getManagerFullName());
                            tvManager.setVisibility(View.VISIBLE);
                            tvManager.setOnClickListener(v -> {
                                if (!isEditMode) {
                                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                                    intent.putExtra("open_profile", datum.getManagerData().getManagerUserId().toString());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            if (isSelf) {
                                tvManager.setHint("Add Manager");
                                tvManager.setHintTextColor(Color.parseColor("#b3bec9"));
                                tvManager.setVisibility(View.VISIBLE);
                                tvManager.setOnClickListener(v -> maketextViewEditable(tvManager, "Add Manager"));
                            } else {
                                tvManager.setText("");
                            }
                        }
                        if (tvDepartment.getVisibility() == View.GONE &&
                                tvDesignation.getVisibility() == View.GONE &&
                                tvManager.getVisibility() == View.GONE) {
                        } else {
                        }
                        if (getInfoResponse.getData().getStatus().equalsIgnoreCase("INVITED")) {
                            tvGuest.setText("(Pending)");
                            tvGuest.setVisibility(View.VISIBLE);

                            if (getCreategroupRoles().contains(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getRole())) {
                                tvCg.setVisibility(View.VISIBLE);
                                ivCg.setVisibility(View.VISIBLE);
                            } else {
                                tvCg.setVisibility(View.GONE);
                                ivCg.setVisibility(View.GONE);
                            }
                            if (getDirectMessageRoles().contains(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getRole())) {
                                if (getInfoResponse.getData().getRole().equalsIgnoreCase("GUEST")) {
                                    llDirectMessage.setVisibility(View.GONE);
                                } else {
                                    llDirectMessage.setVisibility(View.VISIBLE);
                                }
                            } else {
                                llDirectMessage.setVisibility(View.GONE);
                            }
                        } else if (getInfoResponse.getData().getRole().equalsIgnoreCase("GUEST")) {
                            tvGuest.setText("(Guest)");
                            tvGuest.setVisibility(View.VISIBLE);
                            tvCg.setVisibility(View.GONE);
                            ivCg.setVisibility(View.GONE);
                            if (getInfoResponse.getData().isIsmessageAllowed()) {
                                llDirectMessage.setVisibility(View.VISIBLE);
                            } else {
                                llDirectMessage.setVisibility(View.GONE);
                            }
                        } else if (!userId.equals(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId())) {

                            if (datum.getStatus().equals("ENABLED")) {
                                llDirectMessage.setVisibility(View.VISIBLE);
                            } else {
                                llDirectMessage.setVisibility(View.GONE);
                            }
//                            ivDm.setVisibility(View.VISIBLE);
//                            tvDm.setVisibility(View.VISIBLE);
                            tvGuest.setVisibility(View.GONE);
                            if (getCreategroupRoles().contains(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getRole())) {
                                tvCg.setVisibility(View.VISIBLE);
                                ivCg.setVisibility(View.VISIBLE);
                            } else {
                                tvCg.setVisibility(View.GONE);
                                ivCg.setVisibility(View.GONE);
                            }
                        }
                        if (llEmail.getVisibility() == View.GONE && llPhone.getVisibility() == View.GONE && llLocation.getVisibility() == View.GONE) {
                        }
                        if (ivCg.getVisibility() == View.GONE && tvDm.getVisibility() == View.GONE) {
                            llDirectMessage.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(ProfileActivity.this);
                            finishAffinity();
                            startActivity(new Intent(ProfileActivity.this, IntroActivity.class));
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

    private void maketextViewEditable(EditText v, String text) {
        ivEdit.performClick();
        v.setHint(text);
        v.setHintTextColor(Color.parseColor("#b3bec9"));
        v.setText("");
        v.setTextColor(Color.BLACK);
        v.requestFocus();
        KeyboardUtil.toggleKeyboardVisibility(ProfileActivity.this);
    }


    private void apiGetAllMembers() {
        com.skeleton.mvp.data.network.CommonParams commonParams = new com.skeleton.mvp.data.network.CommonParams.Builder().add("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getWorkspaceId())
                .add("user_type", "ALL_MEMBERS")
                .add("user_status", "ENABLED")
                .build();

        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).getAllMembers(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap())
                .enqueue(new com.skeleton.mvp.data.network.ResponseResolver<AllMemberResponse>() {

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
                                            0, allMember.getContactNumber(), "");
                                    allMemberMap.put(allMember.getFuguUserId(), getAllMembers);
                                }
                            }
                        }

                        allMembersArrayList = new ArrayList(allMemberMap.values());

                        new Thread(() -> com.skeleton.mvp.fugudatabase.CommonData.setPaperAllMembersMap(allMemberMap, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey())).start();

                        adapter = new CustomContactsListAdapter(ProfileActivity.this,
                                R.layout.simple_dropdown_two_line, allMembersArrayList);

                        tvManager.setAdapter(adapter);
                        tvManager.setOnItemClickListener((parent, view, position, id) -> {
                            String[] selectedItem = parent.getItemAtPosition(position).toString().split("-");
                            if (TextUtils.isDigitsOnly(selectedItem[selectedItem.length - 1])) {
                                tvManager.setText(selectedItem[selectedItem.length - 1]);
                                tvManager.setSelection(selectedItem[selectedItem.length - 1].length());
                            } else {
                                getAllMembers = (GetAllMembers) parent.getItemAtPosition(position);
                                tvManager.setText(getAllMembers.getFullName());
                                tvManager.setSelection(getAllMembers.getFullName().length());
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
//
//    private void setUpToolBar(int mutedColor) {
//        collapsingToolbarLayout.setContentScrimColor(mutedColor);
//        getWindow().setStatusBarColor(manipulateColor(mutedColor, 0.7f));
//    }

//    private int manipulateColor(int color, float factor) {
//        int a = Color.alpha(color);
//        int r = Math.round(Color.red(color) * factor);
//        int g = Math.round(Color.green(color) * factor);
//        int b = Math.round(Color.blue(color) * factor);
//        return Color.argb(a,
//                Math.min(r, 255),
//                Math.min(g, 255),
//                Math.min(b, 255));
//    }

    @Override
    public void onBackPressed() {
        handler = null;
        Intent intent = new Intent();
        intent.putExtra("remove", "remove");
        setResult(RESULT_OK, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivEdit:
//                Intent ediProfileIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
//                ediProfileIntent.putExtra("name", tvName.getText().toString());
//                ediProfileIntent.putExtra("email", userId);
//                ediProfileIntent.putExtra("phone", tvPhone.getText().toString());
//                ediProfileIntent.putExtra("location", tvLocation.getText().toString());
//                ediProfileIntent.putExtra("designation", tvDesignation.getText().toString());
//                ediProfileIntent.putExtra("department", tvDepartment.getText().toString());
//                ediProfileIntent.putExtra("manager", tvManager.getText().toString());
//                ediProfileIntent.putExtra("isAdmin", currentUserRole);
//                ediProfileIntent.putExtra("userId", userId);
//                ediProfileIntent.putExtra("managerUserId", managerUserId);
//                ediProfileIntent.putExtra("managerFullName", managerFullName);
//
//
//                startActivity(ediProfileIntent);
//                overridePendingTransition(R.anim.right_in, R.anim.left_out);

                isEditMode = true;
                enableTextView(tvName);
                enableTextView(tvLocation);
                tvPhone.setClickable(true);
                if(currentUserRole.equals("ADMIN") || currentUserRole.equals("OWNER")) {
                    tvDesignation.setEnabled(true);
                    tvDepartment.setEnabled(true);
                    enableTextView(tvDesignation);
                    enableTextView(tvDepartment);
                    tvManager.setFocusable(true);
                    tvManager.setFocusableInTouchMode(true);
                    tvManager.setClickable(true);
                    tvManager.invalidate();
                    tvManager.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                } else {
                    tvDesignation.setEnabled(false);
                    tvDepartment.setEnabled(false);
                }
                llDirectMessage.setVisibility(View.GONE);
                tvSave = findViewById(R.id.tvSave);
                tvSave.setVisibility(View.VISIBLE);
                tvName.setSelection(tvName.getText().length());

                tvSave.setOnClickListener(v -> {
                    showLoading();
                    apiEditProfile();
                });

                ivImageEdit.setVisibility(View.VISIBLE);
                ivImageEdit.setOnClickListener(v -> tvEditProfile.performClick());

                break;
            case R.id.tvEditProfile:

                if (datum != null && !TextUtils.isEmpty(datum.getUserImage())) {
                    new CustomAlertDialog.Builder(this)
                            .setTitle("Select option")
                            .setPositiveButton("Camera", () -> fuguImageUtils.startCamera())
                            .setNegativeButton("Gallery", () -> openGallery())
                            .setNeutralButton("Remove Photo", () -> apiEditInfo(""))
                            .show();
                } else {
                    new CustomAlertDialog.Builder(this)
                            .setTitle("Select option")
                            .setPositiveButton("Camera", () -> fuguImageUtils.startCamera())
                            .setNegativeButton("Gallery", () -> openGallery()).show();
                }
                break;
            case R.id.ivProfile:
                if (datum != null && datum.getUserImage() != null) {
                    try {
//                        showImageDialog(ProfileActivity.this, datum.getUserImage());
                        Intent profileImageIntent = new Intent(ProfileActivity.this, ImageDisplayActivity.class);
                        Image profileImage = new Image(datum.getUserImage(), datum.getUserImage(), "imageOne", "", "");
                        profileImageIntent.putExtra("image", profileImage);
                        profileImageIntent.putExtra("isFromProfileActivity", true);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this,
                                ivProfile, "imageOne");
                        startActivity(profileImageIntent, options.toBundle());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.tvPhone:
                if (isEditMode) {
                    Intent intent = new Intent(ProfileActivity.this, EditPhoneActivity.class);
                    startActivityForResult(intent, 2003);
                } else if (userId.equals(fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId())) {

                } else {
                    if (!TextUtils.isEmpty(tvPhone.getText().toString())) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));
                        startActivity(intent);
                    }
                }
                break;
            case R.id.btnDeactivate:
                String message = "";
                if (status.equals("ENABLED")) {
                    message = " deactivate ";
                } else {
                    message = " activate ";
                }
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setMessage("Are you sure, you want to" + message + datum.getFullName() + "?")
                        .setPositiveButton("No", null)
                        .setNegativeButton("Yes", (dialog, which) -> {
                            if (status.equals("ENABLED")) {
                                status = "DISABLED";
                            } else {
                                status = "ENABLED";
                            }
                            apiEditInfo("");
                        }).show();

                break;
            case R.id.tvShowMore:
                Intent searchIntent = (new Intent(ProfileActivity.this, ShowMoreActivity.class));
                searchIntent.putExtra(CHANNEL_ID, getIntent().getLongExtra("channelId", -1l));
                startActivity(searchIntent);
                break;
            case R.id.tvDm:
            case R.id.ivDm:
                if (!isEmail) {
                    initiateChat();
                }
                break;
            case R.id.tvCg:
            case R.id.ivCg:
                LinkedHashMap<Long, GetAllMembers> fuguCreateGroupMap = new LinkedHashMap<>();
                GetAllMembers fuguSearchResult = new GetAllMembers(Long.valueOf(userId), tvName.getText().toString(), tvEmail.getText().toString(), image, image, "", 0, "", "");
                fuguCreateGroupMap.put(Long.valueOf(userId), fuguSearchResult);
                Intent mIntent = new Intent(ProfileActivity.this, MembersSearchActivity.class);
                Gson gson = new Gson();
                String list = gson.toJson(fuguCreateGroupMap);
                mIntent.putExtra(SELECTED_MEMBERS, list);
                startActivity(mIntent);
                break;
        }

    }

    private void enableTextView(AppCompatEditText tvName) {
        tvName.setFocusable(true);
        tvName.setFocusableInTouchMode(true);
        tvName.setClickable(true);
        tvName.invalidate();
        tvName.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }

    private void initiateChat() {
        com.skeleton.mvp.data.network.CommonParams commonParams = new com.skeleton.mvp.data.network.CommonParams.Builder()
                .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add(CHAT_WITH_USER_ID, userId)
                .build();
        showLoading();
        if (isNetworkConnected()) {
            com.skeleton.mvp.data.network.RestClient.getApiInterface(false).createOneToOneChat(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(),
                    1,
                    BuildConfig.VERSION_CODE,
                    commonParams.getMap()).enqueue(new com.skeleton.mvp.data.network.ResponseResolver<CreateChatResponse>() {
                @Override
                public void onSuccess(CreateChatResponse createChatResponse) {
                    Long channelId = Long.valueOf(createChatResponse.getData().getChannelId());
                    Intent chatIntent = new Intent(ProfileActivity.this, ChatActivity.class);
                    FuguConversation conversation = new FuguConversation();
                    conversation.setBusinessName(name);
                    conversation.setOpenChat(true);
                    conversation.setChannelId(channelId);
                    conversation.setLabel(name);
                    conversation.setChat_type(2);
                    conversation.setUserName(StringUtil.toCamelCase(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName()));
                    conversation.setUserId(Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId()));
                    conversation.setEnUserId(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId());
                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    startActivity(chatIntent);
                    hideLoading();
                }

                @Override
                public void onError(ApiError error) {
                    hideLoading();
                    if (error.getStatusCode() == SESSION_EXPIRE) {
                        CommonData.clearData();
                        FuguConfig.clearFuguData(ProfileActivity.this);
                        finishAffinity();
                        startActivity(new Intent(ProfileActivity.this, IntroActivity.class));
                    } else {
                        showErrorMessage(error.getMessage());
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    hideLoading();
                }
            });
        } else {
            showErrorMessage(R.string.error_internet_not_connected);
        }
    }

    private void apiEditInfo(String imageUri) {
        showLoading();

        MultipartParams.Builder commonParams = new MultipartParams.Builder();
        if (!TextUtils.isEmpty(imageUri)) {
            commonParams.addFile("files", new File(imageUri));
        } else {
            commonParams.add("remove_profile_image", true);
        }
        if (isEmail) {
            commonParams.add(EMAIL, email);
        } else {
            commonParams.add("fugu_user_id", userId);
        }
        commonParams.add(WORKSPACE_ID, fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        if (TextUtils.isEmpty(imageUri)) {
            commonParams.add("status", status);
        }
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).editProfile(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new com.skeleton.mvp.data.network.ResponseResolver<EditProfileResponse>() {
                    @Override
                    public void onSuccess(final EditProfileResponse editProfileResponse) {
                        Log.i("uriimage", "success");
                        hideLoading();
                        FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
                        if (fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId().equals(userId)) {
                            fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).setUserImage(editProfileResponse.getData().getUserImage());
                            CommonData.setCommonResponse(fcCommonResponse);
                        }
                        if (editProfileResponse.getData().getStatus().equals("ENABLED")) {
                            status = "ENABLED";
                            btnDeactivate.setText("DEACTIVATE " + datum.getFullName());
                            btnDeactivate.setBackgroundResource(R.drawable.curved_button_red);
                            btnDeactivate.setTextColor(Color.RED);
                            ivProfile.setAlpha(1f);
                            ivEdit.setVisibility(View.VISIBLE);
                            tvEditProfile.setVisibility(View.GONE);
                            tvName.setAlpha(1f);
                        } else {
                            status = "DISABLED";
                            btnDeactivate.setText("ACTIVATE " + datum.getFullName());
                            btnDeactivate.setBackgroundResource(R.drawable.curved_button_green);
                            btnDeactivate.setTextColor(Color.parseColor("#009688"));
                            ivProfile.setAlpha(0.5f);
                            ivEdit.setVisibility(View.GONE);
                            tvEditProfile.setVisibility(View.GONE);
                            tvName.setAlpha(0.5f);
                        }

                        if (editProfileResponse.getData().getUserImage() != null && this != null) {

                            RequestOptions options = new RequestOptions()
                                    .centerCrop()
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.profile_placeholder)
                                    .error(R.drawable.profile_placeholder)
                                    .fitCenter()
                                    .priority(Priority.HIGH);
                            if (!TextUtils.isEmpty(editProfileResponse.getData().getUserImage())) {
                                if (!isFinishing()) {
                                    Glide.with(ProfileActivity.this)
                                            .asBitmap()
                                            .apply(options)
                                            .load(editProfileResponse.getData().getUserImage())

                                            .into(ivProfile);
                                }
                            } else {
                                if (!isFinishing()) {
                                    Glide.with(ProfileActivity.this)
                                            .asBitmap()
                                            .apply(options)
                                            .load(R.drawable.profile_placeholder)
                                            .into(ivProfile);
                                }
                            }

                            datum.setUserImage(editProfileResponse.getData().getUserImage());
                        }
                        handler = new Handler();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (handler != null && this != null) {


                                    RequestOptions options = new RequestOptions()
                                            .centerCrop()
                                            .dontAnimate()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .placeholder(R.drawable.profile_placeholder)
                                            .error(R.drawable.profile_placeholder)
                                            .fitCenter()
                                            .priority(Priority.HIGH);
                                    if (!isFinishing()) {
                                        Glide.with(ProfileActivity.this)
                                                .asBitmap()
                                                .apply(options)
                                                .load(editProfileResponse.getData().getUserImage())

                                                .into(ivProfile);
                                    }
                                    datum.setUserImage(editProfileResponse.getData().getUserImage());

                                }
                            }
                        }, 3000);

                        try {
                            CommonData.updateFcmToken(FirebaseInstanceId.getInstance().getToken());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(ProfileActivity.this);
                            finishAffinity();
                            startActivity(new Intent(ProfileActivity.this, IntroActivity.class));
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
     * Method to open the Gallery view
     */

    public void openGallery() {

        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                PERMISSION_CONSTANT_GALLERY)) return;

        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1000);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_gallery), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to retrieve the App Directory,MA
     * where files like logs can be Saved
     *
     * @param type The FileType
     * @return directory corresponding to the FileType
     */
    private File getDirectory(FuguAppConstant.FileType type) {

        try {
            String strFolder = Environment.getExternalStorageDirectory()
                    + File.separator + com.skeleton.mvp.fugudatabase.CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + type.directory;

            File folder = new File(strFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            return folder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to check whether the Camera feature
     * is Available or not
     *
     * @return
     */
    private boolean isCameraAvailable() {

        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Method to check whether the Camera feature
     * is Available or not
     *
     * @return
     */
    private boolean isExternalStorageAvailable() {


        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri tempUri = null;
        if (requestCode == OPEN_CAMERA_ADD_IMAGE && resultCode != 0) {
            FuguFileDetails fuguFileDetails = null;
            try {
                String fileName = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + "_" + com.skeleton.mvp.fugudatabase.CommonData.getTime() + ".jpg";
                tempUri = Uri.fromFile(new File(fuguImageUtils.getDirectory(FuguAppConstant.FileType.IMAGE_FILE), fileName));

                CropImage.activity(tempUri)
                        .setFixAspectRatio(true)
                        .start(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == 1000 && data != null) {
            if (this != null) {

                try {
                    CropImage.activity(data.getData())
                            .setFixAspectRatio(true)
                            .start(this);
                } catch (Exception e) {

                }
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                Log.i("resultUri", resultUri.getPath());

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH);

                File file = new File(resultUri.getPath());
                Uri imageUri = Uri.fromFile(file);

                Log.i("resulturi2", imageUri.toString());

                if (!isFinishing()) {
                    Glide.with(ProfileActivity.this)
                            .asBitmap()
                            .apply(options)
                            .load(imageUri)
                            .into(ivProfile);
                }

                //FuguImageUtils fuguImageUtils = new FuguImageUtils(this);
                //FuguFileDetails fuguFileDetails = null;
                //Cursor cursor = ProfileActivity.this.getContentResolver().query(resultUri, null, null, null, null);

                showLoading();
                apiEditInfo(resultUri.getPath());


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiGetInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 8 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else if (grantResults[1] == PackageManager.PERMISSION_GRANTED && requestCode == 9) {
            fuguImageUtils.startCamera();
        }
    }

    private void showImageDialog(Context activity, String imgUrl) {
        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            //setting custom layout to dialog
            dialog.setContentView(R.layout.fugu_image_dialog);
            @SuppressWarnings("ConstantConditions") WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 1.0f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            PhotoView ivImage = dialog.findViewById(R.id.ivImage);
            if (activity != null) {

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH);
                if (!TextUtils.isEmpty(imgUrl)) {
                    if (!isFinishing()) {
                        Glide.with(ProfileActivity.this)
                                .load(imgUrl)
                                .apply(options)
                                .into(ivImage);
                    }
                } else {
                    if (!isFinishing()) {
                        Glide.with(ProfileActivity.this)
                                .load(R.drawable.profile_placeholder)
                                .apply(options)
                                .into(ivImage);
                    }
                }

            }
            TextView tvCross = dialog.findViewById(R.id.tvCross);
            tvCross.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void apiGetMedia() {
        CommonParams commonParams = new CommonParams.Builder()
                .add(CHANNEL_ID, getIntent().getLongExtra("channelId", -1l))
                .add("en_user_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add("get_data_type", "DEFAULT")
                .build();
        RestClient.getApiInterface().getGroupInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<MediaResponse>(this, true, false) {
            @Override
            public void success(MediaResponse mediaResponse) {
                mediaList.clear();
                for (int i = 0; i < mediaResponse.getData().getChatMedia().size(); i++) {
                    if (mediaResponse.getData().getChatMedia().get(i).getMessageType() == 10 || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(mediaResponse.getData().getChatMedia().get(i).getMessage().getUrl(), mediaResponse.getData().getChatMedia().get(i).getMuid()))) {
                        String localPath = "";
                        localPath = com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(mediaResponse.getData().getChatMedia().get(i).getMessage().getUrl(), mediaResponse.getData().getChatMedia().get(i).getMuid());
                        mediaList.add(new Media(mediaResponse.getData().getChatMedia().get(i).getMessage().getImageUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getThumbnailUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessageType(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getFileName(), localPath,
                                mediaResponse.getData().getChatMedia().get(i).getMessage(),
                                mediaResponse.getData().getChatMedia().get(i).getMuid(),
                                mediaResponse.getData().getChatMedia().get(i).getIsThreadMessage(),
                                mediaResponse.getData().getChatMedia().get(i).getCreatedAt(),
                                mediaResponse.getData().getChatMedia().get(i).getMessageId(),
                                mediaResponse.getData().getChatMedia().get(i).getThreadMessageId()));
                    }
                    mediaCount = mediaCount++;
                }
                mediaAdapter.notifyDataSetChanged();

                if (mediaList.size() == 0) {
                    llMedia.setVisibility(View.GONE);
                } else {
                    llMedia.setVisibility(View.GONE);
                    if (mediaResponse.getData().getChatMedia().size() > 10) {
                        tvShowMore.setVisibility(View.GONE);
                        tvShowMore.setOnClickListener(ProfileActivity.this);
                    } else {
                        tvShowMore.setVisibility(View.GONE);
                        tvShowMore.setOnClickListener(null);
                    }
                }


                rvMedia.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), rvMedia, new GalleryAdapter.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(ProfileActivity.this, MediaActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("images", mediaList);
                        startActivity(intent);
                    }

                }));
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }

    private ArrayList<String> getDirectMessageRoles() {
        WorkspacesInfo workspacesInfo = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition());
        String roles = workspacesInfo.getConfig().getEnableOneToOneChat();
        roles = roles.replace("[", "");
        roles = roles.replace("]", "");
        roles = roles.replaceAll("\"", "");
        String[] rolesArray = roles.split(",");
        return new ArrayList<>(Arrays.asList(rolesArray));
    }

    private ArrayList<String> getCreategroupRoles() {
        WorkspacesInfo workspacesInfo = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition());
        String roles = workspacesInfo.getConfig().getEnableCreateGroup();
        roles = roles.replace("[", "");
        roles = roles.replace("]", "");
        roles = roles.replaceAll("\"", "");
        String[] rolesArray = roles.split(",");
        return new ArrayList<>(Arrays.asList(rolesArray));
    }

    private int pxToDp(int dpParam) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (dpParam * d);
    }
}