package com.skeleton.mvp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.adapter.GalleryAdapter;
import com.skeleton.mvp.adapter.MembersAdapter;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.ChatDatabase;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.model.FuguFileDetails;
import com.skeleton.mvp.model.GroupMember;
import com.skeleton.mvp.model.Media;
import com.skeleton.mvp.model.editInfo.EditInfoResponse;
import com.skeleton.mvp.model.group.GroupResponse;
import com.skeleton.mvp.model.media.ChatMember;
import com.skeleton.mvp.model.media.MediaResponse;
import com.skeleton.mvp.retrofit.APIError;
import com.skeleton.mvp.retrofit.CommonParams;
import com.skeleton.mvp.retrofit.MultipartParams;
import com.skeleton.mvp.retrofit.ResponseResolver;
import com.skeleton.mvp.retrofit.RestClient;
import com.skeleton.mvp.ui.dialog.CustomAlertDialog;
import com.skeleton.mvp.utils.CircleLinearLayout;
import com.skeleton.mvp.utils.CornerLabel;
import com.skeleton.mvp.utils.FuguImageUtils;
import com.skeleton.mvp.utils.FuguUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GroupInfoActivity extends FuguBaseActivity implements View.OnClickListener, MembersAdapter.DecreaseCount {
    private RecyclerView rvMedia;
    private MembersAdapter membersAdapter;
    private GalleryAdapter mediaAdapter;
    private ArrayList<GroupMember> membersList = new ArrayList<>();
    private ArrayList<Media> mediaList = new ArrayList<>();
    private LinearLayout llToolbar;
    private TextView tvTitle, tvLeave, tvCoworkers;
    private TextView tvGroupName;
    private ImageView ivBack;
    private SwitchCompat isMuted;
    private LinearLayout llMedia, tvShowMore;
    private int mediaCount = 0, initialMembersSize;
    boolean isChannelMuted = false;
    private ImageView ivGroupPhoto;
    private String imageUri, extension, url, thumbnailUrl;
    private boolean isImageUpload = false, isDilogOpened = false, isJoined;
    private CornerLabel viewImage, viewGroupName;
    private CircleLinearLayout llCircle;
    private RelativeLayout rlOne, rlTwo, rlThree;
    private TextView tvOne, tvTwo, tvThree;
    private ImageView ivOne, ivTwo, ivThree;
    private LinearLayout llRight;
    private HashMap<Integer, Integer> dummyImagesArray2 = new HashMap<>();
    private boolean isCustomLabel = false;
    private boolean isImageDisplayed = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        RecyclerView rvMembers = findViewById(R.id.rvMembers);
        llToolbar = findViewById(R.id.llToolbar);
        tvTitle = findViewById(R.id.tvTitle);
        tvGroupName = findViewById(R.id.tvGroupName);
        LinearLayout llMute = findViewById(R.id.llMute);
        tvLeave = findViewById(R.id.tvLeave);
        ivBack = findViewById(R.id.ivBack);
        TextView tvAddMember = findViewById(R.id.tvAddMember);
        tvCoworkers = findViewById(R.id.tvCoworkers);
        isMuted = findViewById(R.id.isMuted);
        TextView tvSave = findViewById(R.id.tvSave);
        LinearLayout llIcon = findViewById(R.id.llIcon);
        rvMedia = findViewById(R.id.rvMedia);
        llMedia = findViewById(R.id.llMedia);
        tvShowMore = findViewById(R.id.tvShowMore);
        rvMedia.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ivBack.setOnClickListener(this);
        tvLeave.setOnClickListener(this);
        tvAddMember.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        ivGroupPhoto = findViewById(R.id.ivGroupPhoto);
        viewImage = findViewById(R.id.viewImage);
        viewGroupName = findViewById(R.id.viewGroupName);
        tvOne = findViewById(R.id.tvOne);
        tvTwo = findViewById(R.id.tvTwo);
        tvThree = findViewById(R.id.tvThree);
        ivOne = findViewById(R.id.ivOne);
        ivTwo = findViewById(R.id.ivTwo);
        ivThree = findViewById(R.id.ivThree);
        rlOne = findViewById(R.id.rlOne);
        rlTwo = findViewById(R.id.rlTwo);
        rlThree = findViewById(R.id.rlThree);
        llRight = findViewById(R.id.llRight);
        llCircle = findViewById(R.id.llCircle);
        llCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomAlertDialog.Builder(GroupInfoActivity.this)
                        .setTitle("Select option")
                        .setPositiveButton("Camera", () -> startCamera())
                        .setNegativeButton("Gallery", () -> openGallery())
                        .show();
            }
        });
        dummyImagesArray2.put(1, R.drawable.rectangle_grey);
        dummyImagesArray2.put(2, R.drawable.rectangle_indigo);
        dummyImagesArray2.put(3, R.drawable.rectangle_purple);
        dummyImagesArray2.put(4, R.drawable.rectangle_red);
        dummyImagesArray2.put(0, R.drawable.rectangle_teal);
        tvGroupName.setOnClickListener(this);
        int chatType = CommonData.getChatType();
        isJoined = getIntent().getBooleanExtra("isJoined", true);
        isCustomLabel = getIntent().getBooleanExtra("isCustomLabel", false);
        if (chatType == ChatType.GENERAL_GROUP || chatType == ChatType.DEFAULT_GROUP || !isJoined) {
            tvLeave.setVisibility(View.INVISIBLE);
            viewImage.setVisibility(View.GONE);
            viewGroupName.setVisibility(View.GONE);
        } else {
            tvLeave.setVisibility(View.VISIBLE);
            viewImage.setVisibility(View.VISIBLE);
            viewGroupName.setVisibility(View.VISIBLE);
        }
        if (isJoined) {
            llMute.setVisibility(View.VISIBLE);
        } else {
            llMute.setVisibility(View.GONE);
            tvAddMember.setVisibility(View.GONE);
            llIcon.setVisibility(View.GONE);
        }
        setToolbar();
        mediaAdapter = new GalleryAdapter(this, mediaList);
        rvMedia.setAdapter(mediaAdapter);
        ViewCompat.setNestedScrollingEnabled(rvMedia, false);
        membersAdapter = new MembersAdapter(membersList, GroupInfoActivity.this, getIntent().getLongExtra("channelId", -1L), isJoined, null, false, 0L);
        rvMembers.setLayoutManager(new LinearLayoutManager(GroupInfoActivity.this));
        rvMembers.setAdapter(membersAdapter);
        if (getIntent().hasExtra("isMuted") && !TextUtils.isEmpty(getIntent().getStringExtra("isMuted"))) {
            if (getIntent().getStringExtra("isMuted").equals("MUTED")) {
                isChannelMuted = true;
                isMuted.setChecked(true);
            } else {
                isChannelMuted = false;
                isMuted.setChecked(false);
            }
        }
        isMuted.setOnCheckedChangeListener((compoundButton, b) -> apiManipulateChannel(compoundButton.isChecked()));

    }

    private void showImageDialog(Context activity, String imgUrl, Drawable drawable, String imageUri) {
        try {
            String image = "";
            if (!TextUtils.isEmpty(imageUri)) {
                image = imageUri;
            } else {
                image = imgUrl;
            }
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.image_dialog);
            @SuppressWarnings("ConstantConditions") WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 1.0f;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            PhotoView ivImage = dialog.findViewById(R.id.ivImage);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH);

            Glide.with(activity)
                    .asBitmap()
                    .apply(options)
                    .load(image)
                    .into(ivImage);


//            Glide.with(activity).load(image)
//                    .placeholder(drawable)
//                    .dontAnimate()
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .error(ContextCompat.getDrawable(activity, R.drawable.placeholder))
//                    .into(ivImage);
            ImageView ivBack = dialog.findViewById(R.id.ivBack);
            ImageView ivEdit = dialog.findViewById(R.id.ivEdit);
            ivBack.setOnClickListener(v -> dialog.dismiss());
            ivEdit.setOnClickListener(v -> new CustomAlertDialog.Builder(GroupInfoActivity.this)
                    .setTitle("Select option")
                    .setPositiveButton("Camera", () -> {
                        dialog.dismiss();
                        startCamera();
                        isImageUpload = true;
                    })
                    .setNegativeButton("Gallery", () -> {
                        dialog.dismiss();
                        openGallery();
                        isImageUpload = true;
                    })
                    .show());

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void apiGetGroupInfo() {


        CommonParams commonParams = new CommonParams.Builder()
                .add(CHANNEL_ID, getIntent().getLongExtra("channelId", -1L))
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add(GET_DATA_TYPE, DEFAULT)
                .build();
        RestClient.getApiInterface().getGroupInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(),
                1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<MediaResponse>(this, false, false) {
            @SuppressLint("SetTextI18n")
            @Override
            public void success(final MediaResponse mediaResponse) {
                membersList.clear();
                ivGroupPhoto.setOnClickListener(v -> {
                    isDilogOpened = true;
                    showImageDialog(GroupInfoActivity.this, mediaResponse.getData().getChannelImageUrl().getChannelImageUrl(), ivGroupPhoto.getDrawable(), imageUri);

                });
                if (!isImageDisplayed) {
                    isImageDisplayed = true;
                    if (mediaResponse.getData().getChannelImageUrl().getChannelThumbnailUrl().equals("https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png")) {
                        ivGroupPhoto.setVisibility(View.GONE);
                        llCircle.setVisibility(View.VISIBLE);
                        switch (mediaResponse.getData().getMembersInfo().size()) {
                            case 1:
                                rlOne.setVisibility(View.VISIBLE);
                                llRight.setVisibility(View.GONE);
                                rlTwo.setVisibility(View.GONE);
                                rlThree.setVisibility(View.GONE);
                                setImageResource(ivOne, mediaResponse.getData().getMembersInfo().get(0).getUserImage(),
                                        mediaResponse.getData().getMembersInfo().get(0).getUserId(),
                                        mediaResponse.getData().getMembersInfo().get(0).getFullName(),
                                        tvOne);
                                break;
                            case 2:
                                rlOne.setVisibility(View.VISIBLE);
                                rlTwo.setVisibility(View.VISIBLE);
                                llRight.setVisibility(View.VISIBLE);
                                rlThree.setVisibility(View.GONE);
                                setImageResource(ivOne, mediaResponse.getData().getMembersInfo().get(0).getUserImage(),
                                        mediaResponse.getData().getMembersInfo().get(0).getUserId(),
                                        mediaResponse.getData().getMembersInfo().get(0).getFullName(),
                                        tvOne);
                                setImageResource(ivTwo, mediaResponse.getData().getMembersInfo().get(1).getUserImage(),
                                        mediaResponse.getData().getMembersInfo().get(1).getUserId(),
                                        mediaResponse.getData().getMembersInfo().get(1).getFullName(),
                                        tvTwo);
                                break;
                            case 3:
                                rlOne.setVisibility(View.VISIBLE);
                                rlTwo.setVisibility(View.VISIBLE);
                                llRight.setVisibility(View.VISIBLE);
                                rlThree.setVisibility(View.VISIBLE);
                                setImageResource(ivOne, mediaResponse.getData().getMembersInfo().get(0).getUserImage(),
                                        mediaResponse.getData().getMembersInfo().get(0).getUserId(),
                                        mediaResponse.getData().getMembersInfo().get(0).getFullName(),
                                        tvOne);
                                setImageResource(ivTwo, mediaResponse.getData().getMembersInfo().get(1).getUserImage(),
                                        mediaResponse.getData().getMembersInfo().get(1).getUserId(),
                                        mediaResponse.getData().getMembersInfo().get(1).getFullName(),
                                        tvTwo);
                                setImageResource(ivThree, mediaResponse.getData().getMembersInfo().get(2).getUserImage(),
                                        mediaResponse.getData().getMembersInfo().get(2).getUserId(),
                                        mediaResponse.getData().getMembersInfo().get(2).getFullName(),
                                        tvThree);
                                break;
                            default:
                                break;
                        }

                    } else {
                        llCircle.setVisibility(View.GONE);
                        ivGroupPhoto.setVisibility(View.VISIBLE);

                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.profile_placeholder)
                                .error(R.drawable.profile_placeholder)
                                .fitCenter()
                                .priority(Priority.HIGH)
                                .transforms(new CenterCrop(), new RoundedCorners(10));

                        Glide.with(GroupInfoActivity.this)
                                .asBitmap()
                                .apply(options)
                                .load(mediaResponse.getData().getChannelImageUrl())
                                .into(ivGroupPhoto);

//                        Glide.with(GroupInfoActivity.this).load(mediaResponse.getData().getChannelImageUrl().getChannelThumbnailUrl()).asBitmap()
//                                .centerCrop()
//                                .placeholder(ContextCompat.getDrawable(GroupInfoActivity.this, R.drawable.group_purple))
//                                .error(ContextCompat.getDrawable(GroupInfoActivity.this, R.drawable.group_purple))
//                                .into(new BitmapImageViewTarget(ivGroupPhoto) {
//                                    @Override
//                                    protected void setResource(Bitmap resource) {
//                                        RoundedBitmapDrawable circularBitmapDrawable =
//                                                RoundedBitmapDrawableFactory.create(getResources(), resource);
//                                        circularBitmapDrawable.setCircular(true);
//                                        ivGroupPhoto.setImageDrawable(circularBitmapDrawable);
//                                    }
//                                });
                    }
                }
                for (int i = 0; i < mediaResponse.getData().getChatMembers().size(); i++) {
                    if (Integer.parseInt(mediaResponse.getData().getChatMembers().get(i).getStatus()) == 1) {
                        ChatMember memnersDatum = mediaResponse.getData().getChatMembers().get(i);
                        membersList.add(new GroupMember(memnersDatum.getFullName()
                                , Long.valueOf(memnersDatum.getUserId())
                                , memnersDatum.getEmail(),
                                memnersDatum.getUserImage(),
                                memnersDatum.getEmail(),
                                memnersDatum.getRole(), 0));
                    }
                }
                membersAdapter.notifyDataSetChanged();
                Collections.sort(membersList, (one, other) -> one.getName().compareTo(other.getName()));

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        CommonData.setGroupMembers(membersList);
                    }
                }.start();

                if (!isJoined) {
                    tvCoworkers.setText(mediaList.size() + getString(R.string.memberss));
                } else {
                    tvCoworkers.setText("(" + membersList.size() + getString(R.string.members));
                }
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
                    llMedia.setVisibility(View.VISIBLE);
                    if (mediaResponse.getData().getChatMedia().size() > 0) {
                        tvShowMore.setVisibility(View.VISIBLE);
                        tvShowMore.setOnClickListener(GroupInfoActivity.this);
                    } else {
                        tvShowMore.setVisibility(View.GONE);
                        tvShowMore.setOnClickListener(null);
                    }
                }


                rvMedia.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), rvMedia, (view, position) -> {
                    Intent intent = new Intent(GroupInfoActivity.this, MediaActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("images", mediaList);
                    startActivity(intent);
                }));
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }

    private void setToolbar() {
        tvTitle.setTextColor(CommonData.getColorConfig().getFuguActionBarText());
        tvLeave.setTextColor(CommonData.getColorConfig().getFuguActionBarText());
        ivBack.setColorFilter(CommonData.getColorConfig().getFuguActionBarText());
        llToolbar.setBackgroundColor(CommonData.getColorConfig().getFuguActionBarBg());
        tvGroupName.setText(getIntent().getStringExtra("groupName"));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvLeave:
                if (isNetworkAvailable()) {
                    leaveGroup();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.fugu_unable_to_connect_internet)
                            .setPositiveButton("ok", (dialog, which) -> {
                            })
                            .show();
                }
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvAddMember:
                addMembersIntent();
                break;
            case R.id.tvShowMore:
                showMoreIntent();
                break;
            case R.id.tvGroupName:
                if (isCustomLabel) {
                    showGroupNameEditDialog(GroupInfoActivity.this, "");
                } else {
                    showGroupNameEditDialog(GroupInfoActivity.this, tvGroupName.getText().toString().trim());
                }

                break;
            default:
                break;

        }
    }

    private void showMoreIntent() {
        Intent searchIntent = (new Intent(GroupInfoActivity.this, ShowMoreActivity.class));
        isDilogOpened = false;
        searchIntent.putExtra(CHANNEL_ID, getIntent().getLongExtra("channelId", -1L));
        startActivity(searchIntent);
    }

    private void addMembersIntent() {
        Intent searchIntent = (new Intent(GroupInfoActivity.this, FuguSearchsActivity.class));
        isDilogOpened = false;
        searchIntent.putExtra(CHANNEL_ID, getIntent().getLongExtra("channelId", -1L));
        int REQUEST_CODE_ADD_MEMBER = 13241;
        startActivityForResult(searchIntent, REQUEST_CODE_ADD_MEMBER);
    }

    private void leaveGroup() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to leave the group?")
                .setPositiveButton("Yes", (dialog, which) -> apiLeaveGroup())
                .setNegativeButton("No", null)
                .show();
    }

    private void apiEditChannelInfo(final String groupName) {

        MultipartParams.Builder commonParams = new MultipartParams.Builder();
        commonParams.add(CHANNEL_ID, getIntent().getLongExtra("channelId", -1L));
        commonParams.add(EN_USER_ID, CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getEnUserId());

        if (!isImageUpload) {
            commonParams.add("custom_label", groupName);
        }
        if (isImageUpload) {
            if (!TextUtils.isEmpty(imageUri)) {
//                File compressedImage = null;
//                try {
//                    compressedImage = new Compressor(this)
//                            .setMaxWidth(2048)
//                            .setMaxHeight(2048)
//                            .setQuality(75)
//                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                            .compressToFile(new File(imageUri));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                commonParams.addFile("files", new File(imageUri));
            }
        }
        Log.e("Rajat test boolen", commonParams + "");
        RestClient.getApiInterface().editChannelInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.build().getMap()).enqueue(new ResponseResolver<EditInfoResponse>(GroupInfoActivity.this, true, false) {
            @Override
            public void success(final EditInfoResponse editInfoResponse) {

                if (!TextUtils.isEmpty(editInfoResponse.getData().getChannelImageUrl().getChannelThumbnailUrl())) {
                    llCircle.setVisibility(View.GONE);
                    ivGroupPhoto.setVisibility(View.VISIBLE);


                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.group_purple)
                            .error(R.drawable.group_purple)
                            .fitCenter()
                            .priority(Priority.HIGH)
                            .transforms(new CenterCrop(), new RoundedCorners(10));

                    Glide.with(GroupInfoActivity.this)
                            .asBitmap()
                            .apply(options)
                            .load(editInfoResponse.getData().getChannelImageUrl().getChannelThumbnailUrl())
                            .into(ivGroupPhoto);

//                    Glide.with(GroupInfoActivity.this).load(editInfoResponse.getData().getChannelImageUrl().getChannelThumbnailUrl()).asBitmap()
//                            .centerCrop()
//                            .placeholder(ContextCompat.getDrawable(GroupInfoActivity.this, R.drawable.group_purple))
//                            .error(ContextCompat.getDrawable(GroupInfoActivity.this, R.drawable.group_purple))
//                            .into(new BitmapImageViewTarget(ivGroupPhoto) {
//                                @Override
//                                protected void setResource(Bitmap resource) {
//                                    RoundedBitmapDrawable circularBitmapDrawable =
//                                            RoundedBitmapDrawableFactory.create(getResources(), resource);
//                                    circularBitmapDrawable.setCircular(true);
//                                    ivGroupPhoto.setImageDrawable(circularBitmapDrawable);
//                                }
//                            });
                    ivGroupPhoto.setOnClickListener(v -> {
                        isDilogOpened = true;
                        showImageDialog(GroupInfoActivity.this, editInfoResponse.getData().getChannelImageUrl().getChannelImageUrl(), ivGroupPhoto.getDrawable(), imageUri);

                    });
                }

                tvGroupName.setText(groupName);
                Intent intent = new Intent();
                intent.putExtra(FuguAppConstant.GROUP_SIZE_CHANGED, true);
                intent.putExtra("tvGroupName", groupName);
                intent.putExtra("url", editInfoResponse.getData().getChannelImageUrl().getChannelImageUrl());
                intent.putExtra("thumbnail_url", editInfoResponse.getData().getChannelImageUrl().getChannelThumbnailUrl());
                setResult(RESULT_OK, intent);
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }

    private void apiLeaveGroup() {
        CommonParams commonParams = new CommonParams.Builder()
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add(CHANNEL_ID, getIntent().getLongExtra("channelId", -1L))
                .build();
        RestClient.getApiInterface().leaveGroup(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<GroupResponse>(GroupInfoActivity.this, true, false) {
            @Override
            public void success(GroupResponse groupResponse) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        InputMethodManager inputMethodManager = (InputMethodManager) GroupInfoActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(GroupInfoActivity.this.getCurrentFocus().getWindowToken(), 0);
        if (initialMembersSize != membersList.size()) {
            Intent intent = new Intent();
            intent.putExtra(FuguAppConstant.GROUP_SIZE_CHANGED, true);
            setResult(RESULT_OK, intent);
        }
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isDilogOpened) {
            apiGetGroupInfo();
        }
    }

    private void apiManipulateChannel(final boolean isMutedTrue) {
        JSONObject user_properties = new JSONObject();
        try {
            user_properties.put("enable_vibration", isMutedTrue);
            user_properties.put("push_notification_sound", "test.mp3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CommonParams commonParams;
        if (!isMutedTrue) {
            commonParams = new CommonParams.Builder()
                    .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                    .add("user_properties", user_properties.toString())
                    .add("unmute_channel_id", getIntent().getLongExtra("channelId", -1L))
                    .build();
        } else {
            commonParams = new CommonParams.Builder()
                    .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                    .add("user_properties", user_properties.toString())
                    .add("mute_channel_id", getIntent().getLongExtra("channelId", -1L))
                    .build();
        }
        RestClient.getApiInterface().editInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<EditInfoResponse>(GroupInfoActivity.this, true, false) {
            @Override
            public void success(EditInfoResponse editInfoResponse) {
                isMuted.setChecked(isMutedTrue);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        LinkedHashMap<Long, FuguConversation> conversationMap = ChatDatabase.INSTANCE.getConversationMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                        FuguConversation conversation = conversationMap.get(getIntent().getLongExtra("channelId", -1L));
                        try {
                            if (isMutedTrue) {
                                conversation.setNotifications("MUTED");
                            } else {
                                conversation.setNotifications("UNMUTED");
                            }
                            conversationMap.put(getIntent().getLongExtra("channelId", -1L), conversation);
                            ChatDatabase.INSTANCE.setConversationMap(conversationMap, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void decreaseCount() {
        if (!isJoined) {
            tvCoworkers.setText((membersList.size() - 1) + getString(R.string.memberss));
        } else {
            tvCoworkers.setText("(" + (membersList.size()) + getString(R.string.members));
        }
    }

    /**
     * Method to open the Gallery view
     */

    public void openGallery() {
        isDilogOpened = true;
        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(GroupInfoActivity.this,
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

    public void startCamera() {
        isDilogOpened = true;
        /** Code to check whether the Location Permission is Granted */
        String[] permissionsRequired = new String[]{android.Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        /*  Check if the Permission for the Camera was Granted  */
        if (!FuguConfig.getInstance().askUserToGrantPermission(this,
                permissionsRequired, "Please grant permission to access Camera",
                PERMISSION_CONSTANT_CAMERA)) return;


        /*  Check whether the Camera feature is available or not    */
        if (!isCameraAvailable()) {
            Toast.makeText(this, "Camera feature unavailable!", Toast.LENGTH_SHORT).show();
            return;
        }

        /*  Check for the SD CARD or External Storage   */
        if (!isExternalStorageAvailable()) {
            Toast.makeText(this, "External storage unavailable!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

//            File fileToBeWritten = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            File fileToBeWritten = new File(getDirectory(FuguAppConstant.FileType.IMAGE_FILE), "temp.jpg");

            if (!fileToBeWritten.exists()) {
                try {
                    fileToBeWritten.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(fileToBeWritten));
            startActivityForResult(takePictureIntent, 1001);
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri tempUri = null;
        if (data != null) {
            if (requestCode == 1001 && data != null) {
                Bundle extras = data.getExtras();
                FuguFileDetails fuguFileDetails = null;
                try {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    tempUri = getImageUri(getApplicationContext(), imageBitmap);
                    if (this != null) {

                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.camera_oval)
                                .error(R.drawable.camera_oval)
                                .fitCenter()
                                .priority(Priority.HIGH)
                                .transforms(new CenterCrop(), new RoundedCorners(10));

                        Glide.with(GroupInfoActivity.this)
                                .asBitmap()
                                .apply(options)
                                .load(tempUri)
                                .into(ivGroupPhoto);


                        FuguImageUtils fuguImageUtils = new FuguImageUtils(this);
                        Cursor cursor = GroupInfoActivity.this.getContentResolver().query(tempUri, null, null, null, null);
                        try {
                            if (cursor != null && cursor.moveToFirst()) {
                                try {
                                    String[] extensions = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.");
                                    extension = extensions[extensions.length - 1].toLowerCase();
                                    fuguFileDetails = fuguImageUtils.saveFile(tempUri, FuguAppConstant.FILE_TYPE_MAP.get(extension), getIntent().getLongExtra("channelId", -1L), "");
                                } catch (Exception e) {

                                }
                            }
                        } finally {
                            cursor.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FuguImageUtils fuguImageUtils = new FuguImageUtils(this);
                imageUri = fuguImageUtils.compressAndSaveBitmap(this, extension, fuguFileDetails.getFileName());
                isImageUpload = true;
                apiEditChannelInfo(tvGroupName.getText().toString().trim());

            } else if (requestCode == 1000 && data != null) {
                FuguFileDetails fuguFileDetails = null;
                if (this != null) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.group_purple)
                            .error(R.drawable.group_purple)
                            .fitCenter()
                            .priority(Priority.HIGH)
                            .transforms(new CenterCrop(), new RoundedCorners(10));

                    Glide.with(GroupInfoActivity.this)
                            .asBitmap()
                            .apply(options)
                            .load(data.getData())
                            .into(ivGroupPhoto);
                }
                FuguImageUtils fuguImageUtils = new FuguImageUtils(this);
                Cursor cursor = GroupInfoActivity.this.getContentResolver().query(data.getData(), null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        try {
                            String[] extensions = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.");
                            extension = extensions[extensions.length - 1].toLowerCase();
                            fuguFileDetails = fuguImageUtils.saveFile(tempUri, FuguAppConstant.FILE_TYPE_MAP.get(extension), getIntent().getLongExtra("channelId", -1L), "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    cursor.close();
                }
                imageUri = fuguImageUtils.compressAndSaveBitmap(this, extension, fuguFileDetails.getFileName());
                isImageUpload = true;
                apiEditChannelInfo(tvGroupName.getText().toString().trim());
            }
        }
    }

    private void showGroupNameEditDialog(Context activity, final String groupName) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.group_edit_dialog);
        isImageUpload = false;
        @SuppressWarnings("ConstantConditions") WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        final EditText etGroupName = dialog.findViewById(R.id.etGroupName);
        final AppCompatButton btnSave = dialog.findViewById(R.id.btnSave);
        final AppCompatButton btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        etGroupName.setText(groupName);
        etGroupName.setSelection(etGroupName.getText().length());
        etGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(groupName) || s.toString().length() <= 0) {
                    btnSave.setTextColor(getResources().getColor(R.color.gray_dark));
                    btnSave.setOnClickListener(null);
                } else {
                    btnSave.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            apiEditChannelInfo(etGroupName.getText().toString().trim());
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    private void setImageResource(final ImageView imageView, String url, Long userId, String name, TextView textView) {
        if (TextUtils.isEmpty(url)) {
            textView.setText(FuguUtils.Companion.getFirstCharInUpperCase(name));
            textView.setVisibility(View.VISIBLE);
//            Glide.clear(imageView);
            int value = (int) (userId % 5);
            imageView.setImageDrawable(ContextCompat.getDrawable(GroupInfoActivity.this, dummyImagesArray2.get(value)));
        } else {
            textView.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.group_purple)
                    .error(R.drawable.group_purple)
                    .fitCenter()
                    .priority(Priority.HIGH);

            Glide.with(GroupInfoActivity.this)
                    .asBitmap()
                    .apply(options)
                    .load(url)
                    .into(imageView);

//            Glide.with(GroupInfoActivity.this).load(url).asBitmap()
//                    .centerCrop()
//                    .placeholder(ContextCompat.getDrawable(GroupInfoActivity.this, R.drawable.placeholder))
//                    .error(ContextCompat.getDrawable(GroupInfoActivity.this, R.drawable.placeholder))
//                    .into(imageView);
        }
    }
}
