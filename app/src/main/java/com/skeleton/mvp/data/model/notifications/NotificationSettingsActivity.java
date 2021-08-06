package com.skeleton.mvp.data.model.notifications;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.model.editprofile.EditProfileResponse;
import com.skeleton.mvp.data.model.getInfo.GetInfoResponse;
import com.skeleton.mvp.data.model.setPassword.CommonResponseFugu;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.MultipartParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.fragment.SnoozeBottomSheetFragment;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.editInfo.EditInfoResponse;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.groupspecific.OldGroupSpecificActivity;
import com.skeleton.mvp.util.GeneralFunctions;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.skeleton.mvp.constant.FuguAppConstant.ANDROID_USER;
import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.APP_VERSION;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_TYPE;

public class NotificationSettingsActivity extends BaseActivity implements View.OnClickListener {
    private Spinner notificationType;
    private String[] notificationArray = new String[]{
            "All new messages", "Only direct Messages & mentions", "Nothing"};
    private SwitchCompat vibrate;
    private ImageView ivBack;
    private TextView tvGroupSpecific, tvTestNotification;
    private LinearLayout snooze_notifications;
    private Boolean isExpired = false;
    private String snoozeTime = "";
    private AppCompatTextView tvSnooze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        notificationType = findViewById(R.id.notificationType);
        tvTestNotification = findViewById(R.id.tvTestNotification);
        snooze_notifications = findViewById(R.id.snooze_notifications);
        tvSnooze = findViewById(R.id.tvSnooze);
        ivBack = findViewById(R.id.ivBack);
        vibrate = findViewById(R.id.vibrate);
        tvGroupSpecific = findViewById(R.id.tvGroupSpecific);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.item_notification_type, notificationArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(getAssets(), FuguAppConstant.FONT_REGULAR);
                ((TextView) v).setTypeface(externalFont);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(getAssets(), FuguAppConstant.FONT_REGULAR);
                ((TextView) v).setTypeface(externalFont);


                return v;
            }
        };


        adapter.setDropDownViewResource(R.layout.item_notification_type);
        notificationType.setAdapter(adapter);
        showLoading();
        ivBack.setOnClickListener(this);
        tvGroupSpecific.setOnClickListener(this);
        tvTestNotification.setOnClickListener(this);
        snooze_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnoozeBottomSheetFragment newFragment = SnoozeBottomSheetFragment.newInstance(0, NotificationSettingsActivity.this, isExpired, snoozeTime);
                newFragment.show(getSupportFragmentManager(), "SnoozeBottomSheetFragment");
            }
        });
        apiGetInfo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvGroupSpecific:
                startActivity(new Intent(NotificationSettingsActivity.this, OldGroupSpecificActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.tvTestNotification:
                if (isNetworkConnected()) {
                    showLoading();
                    apiTestNotifiction();
                } else {
                    showErrorMessage(R.string.error_internet_not_connected);
                }
                break;
            default:
                break;
        }
    }


    private void apiGetInfo() {
        CommonParams commonParams = new CommonParams.Builder()
                .add(DEVICE_TYPE, "1")
                .add(APP_VERSION, BuildConfig.VERSION_NAME)
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                .build();
        RestClient.getApiInterface(false).getInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<GetInfoResponse>() {
            @Override
            public void onSuccess(GetInfoResponse getInfoResponse) {
                hideLoading();
                snoozeTime = getInfoResponse.getData().get(0).getNotification_snooze_time();
                isExpired = new GeneralFunctions().checkIfExpired(getInfoResponse.getData().get(0).getNotification_snooze_time());
                if (isExpired) {
                    tvSnooze.setText("Snooze is OFF");
                } else {
                    tvSnooze.setText("Snooze scheduled until " + new DateUtils().getDate(new DateUtils().convertToLocal(snoozeTime)) + ", " + new DateUtils().getTime(new DateUtils().convertToLocal(snoozeTime)));
                }

                vibrate.setChecked(getInfoResponse.getData().get(0).getUserProperties().getEnableVibration());
                if (getInfoResponse.getData().get(0).getNotificationLevel().equals("ALL_CHATS")) {
                    notificationType.setSelection(0);
                } else if (getInfoResponse.getData().get(0).getNotificationLevel().equals("DIRECT_MESSAGES")) {
                    notificationType.setSelection(1);
                } else {
                    notificationType.setSelection(2);
                }
                CommonData.setVibration(getInfoResponse.getData().get(0).getUserProperties().getEnableVibration());
                vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        apiManipulateChannel(compoundButton.isChecked());
                        CommonData.setVibration(compoundButton.isChecked());
                    }
                });
                notificationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        apiManipulateChannelLevel(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onError(ApiError error) {
                hideLoading();
            }

            @Override
            public void onFailure(Throwable throwable) {
                hideLoading();
            }
        });
    }

    private void apiManipulateChannel(final boolean isMutedTrue) {
        Log.e("test", "test click");
        JSONObject user_properties = new JSONObject();
        try {
            user_properties.put("enable_vibration", isMutedTrue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CommonParams commonParams = new CommonParams.Builder()
                .add(FuguAppConstant.DEVICE_TYPE, ANDROID_USER)
                .add(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add("user_properties", user_properties.toString())
                .build();
        RestClient.getApiInterface(false).editInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<EditInfoResponse>() {
            @Override
            public void onSuccess(EditInfoResponse editInfoResponse) {
                vibrate.setChecked(isMutedTrue);
            }

            @Override
            public void onError(ApiError error) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    private void apiManipulateChannelLevel(final int notificationLevel) {
        String notifyLevel = "";
        switch (notificationLevel) {
            case 0:
                notifyLevel = "ALL_CHATS";
                break;
            case 1:
                notifyLevel = "DIRECT_MESSAGES";
                break;
            case 2:
                notifyLevel = "NONE";
                break;
        }
        CommonParams commonParams = new CommonParams.Builder()
                .add(FuguAppConstant.DEVICE_TYPE, ANDROID_USER)
                .add(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add("notification_level", notifyLevel)
                .build();
        RestClient.getApiInterface(false).editInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<EditInfoResponse>() {
            @Override
            public void onSuccess(EditInfoResponse editInfoResponse) {
            }

            @Override
            public void onError(ApiError error) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    private void apiTestNotifiction() {
        com.skeleton.mvp.retrofit.CommonParams commonParams = new com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add(FuguAppConstant.DEVICE_TYPE, "1")
                .add(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                .build();
        RestClient.getApiInterface(false).testPushNotification(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<CommonResponseFugu>() {
            @Override
            public void onSuccess(CommonResponseFugu commonResponseFugu) {
                hideLoading();
            }

            @Override
            public void onError(ApiError error) {
                hideLoading();
            }

            @Override
            public void onFailure(Throwable throwable) {
                hideLoading();
            }
        });
    }

    public void snoozeNotifications(String time_slot) {
        showLoading();
        MultipartParams.Builder commonParams = new MultipartParams.Builder();
        commonParams.add("notification_snooze_time", time_slot);
        commonParams.add("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        commonParams.add("fugu_user_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getUserId());

        RestClient.getApiInterface(true).editProfile(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<EditProfileResponse>() {
                    @Override
                    public void onSuccess(EditProfileResponse editProfileResponse) {
                        isExpired = new GeneralFunctions().checkIfExpired(editProfileResponse.getData().getNotification_snooze_time());
                        snoozeTime = editProfileResponse.getData().getNotification_snooze_time();
                        if (isExpired) {
                            tvSnooze.setText("Snooze is OFF");
                        } else {
                            tvSnooze.setText("Snooze scheduled until " + new DateUtils().getDate(new DateUtils().convertToLocal(snoozeTime)) + ", " + new DateUtils().getTime(new DateUtils().convertToLocal(snoozeTime)));
                        }
                        hideLoading();
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                    }
                });

    }

    public void endSnooze() {
        showLoading();
        MultipartParams.Builder commonParams = new MultipartParams.Builder();
        commonParams.add("end_snooze", true);
        commonParams.add("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        commonParams.add("fugu_user_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getUserId());

        RestClient.getApiInterface(true).editProfile(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<EditProfileResponse>() {
                    @Override
                    public void onSuccess(EditProfileResponse editProfileResponse) {
                        isExpired = new GeneralFunctions().checkIfExpired(editProfileResponse.getData().getNotification_snooze_time());
                        snoozeTime = editProfileResponse.getData().getNotification_snooze_time();
                        if (isExpired) {
                            tvSnooze.setText("Snooze is OFF");
                        } else {
                            tvSnooze.setText("Snooze scheduled until " + new DateUtils().getDate(new DateUtils().convertToLocal(snoozeTime)) + ", " + new DateUtils().getTime(new DateUtils().convertToLocal(snoozeTime)));
                        }
                        hideLoading();
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                    }
                });
    }


}
