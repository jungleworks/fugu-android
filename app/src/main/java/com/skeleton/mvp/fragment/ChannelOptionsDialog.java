package com.skeleton.mvp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.model.editInfo.EditInfoResponse;
import com.skeleton.mvp.model.group.GroupResponse;
import com.skeleton.mvp.model.media.MediaResponse;
import com.skeleton.mvp.retrofit.APIError;
import com.skeleton.mvp.retrofit.CommonParams;
import com.skeleton.mvp.retrofit.CommonResponse;
import com.skeleton.mvp.retrofit.ResponseResolver;
import com.skeleton.mvp.retrofit.RestClient;
import com.skeleton.mvp.ui.base.BaseView;
import com.skeleton.mvp.ui.profile.ProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.GET_DATA_TYPE;

/**
 * Created by rajatdhamija on 16/05/18.
 */

public class ChannelOptionsDialog extends DialogFragment implements View.OnClickListener {
    private int position;
    private String Muid;
    private TextView tvMute, tvPin, tvLeave, tvClear, tvProfile;
    private MainActivity homeActivity;
    private Long ChannelId;
    private int ChatType;
    private boolean isMuted, isPinned;

    public static ChannelOptionsDialog newInstance(int arg, int pos, String muid, Long channelId, int chatType, boolean isMuted, boolean isPinned) {
        ChannelOptionsDialog frag = new ChannelOptionsDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setPostion(pos);
        frag.setMuid(muid);
        frag.setChatType(chatType);
        frag.setChannelId(channelId);
        frag.setIsMuted(isMuted);
        frag.setIsPinned(isPinned);
        return frag;
    }


    private void setIsPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    private void setIsMuted(boolean isMuted) {
        this.isMuted = isMuted;
    }

    private void setChatType(int chatType) {
        ChatType = chatType;
    }

    private void setChannelId(Long channelId) {
        ChannelId = channelId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_channel_options, container);
        tvMute = view.findViewById(R.id.tvMute);
        tvPin = view.findViewById(R.id.tvPin);
        tvLeave = view.findViewById(R.id.tvLeave);
        tvClear = view.findViewById(R.id.tvClear);
        tvProfile = view.findViewById(R.id.tvViewProfile);
        homeActivity = (MainActivity) getActivity();
        tvMute.setOnClickListener(this);
        tvPin.setOnClickListener(this);
        tvLeave.setOnClickListener(this);
        tvClear.setOnClickListener(this);
        tvProfile.setOnClickListener(this);
        if (ChatType == FuguAppConstant.ChatType.O2O) {
            tvLeave.setVisibility(View.GONE);
            tvMute.setVisibility(View.GONE);
            tvProfile.setVisibility(View.VISIBLE);
        } else if (ChatType == FuguAppConstant.ChatType.GENERAL_GROUP || ChatType == FuguAppConstant.ChatType.DEFAULT_GROUP || ChatType == FuguAppConstant.ChatType.BOT) {
            tvLeave.setVisibility(View.GONE);
            tvProfile.setVisibility(View.GONE);
        } else {
            tvLeave.setVisibility(View.VISIBLE);
            tvMute.setVisibility(View.VISIBLE);
            tvProfile.setVisibility(View.GONE);
        }
        if (CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getConfig().getClearChatHistory() == 1) {
            tvClear.setVisibility(View.VISIBLE);
        } else {
            tvClear.setVisibility(View.GONE);
        }
        if (isMuted) {
            tvMute.setText("Unmute Group");
        } else {
            tvMute.setText("Mute Group");
        }
        if (isPinned) {
            tvPin.setText("Unpin Chat");
        } else {
            tvPin.setText("Pin Chat");
        }
        return view;
    }

    private void setMuid(String muid) {
        Muid = muid;
    }

    private void setPostion(int pos) {
        position = pos;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(900, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvMute:
                apiManipulateChannel(isMuted);
                break;
            case R.id.tvPin:
                apiTogglePinChannel(isPinned);
                break;
            case R.id.tvLeave:
                dismiss();
                homeActivity.showErrorMessage("Are you sure you want to leave this group ?", new BaseView.OnErrorHandleCallback() {
                    @Override
                    public void onErrorCallback() {
                        apiLeaveGroup();
                    }
                }, new BaseView.OnPositiveButtonCallback() {
                    @Override
                    public void onPositiveButtonClick() {

                    }
                }, "Yes", "No");
                break;
            case R.id.tvClear:
                if (!TextUtils.isEmpty(Muid)) {
                    homeActivity.showErrorMessage("Are you sure you want to clear this chat ?", new BaseView.OnErrorHandleCallback() {
                        @Override
                        public void onErrorCallback() {
                            homeActivity.apiClearChat(Muid, ChannelId);
                        }
                    }, new BaseView.OnPositiveButtonCallback() {
                        @Override
                        public void onPositiveButtonClick() {

                        }
                    }, "Delete for me", "No");
                    dismiss();
                }
                break;
            case R.id.tvViewProfile:
                homeActivity.showLoading();
                fetchMembers();
                break;
        }
    }

    private void apiLeaveGroup() {
        CommonParams commonParams = new CommonParams.Builder()
                .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add(CHANNEL_ID, ChannelId)
                .build();
        RestClient.getApiInterface().leaveGroup(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<GroupResponse>(homeActivity, true, false) {
            @Override
            public void success(GroupResponse groupResponse) {
                homeActivity.removeChat(ChannelId);
                dismiss();
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }


    private void apiTogglePinChannel(final boolean isPinned) {
        String status = "PIN_CHAT";
        if (isPinned) {
            status = "UNPIN_CHAT";
        }
        CommonParams commonParams = new CommonParams.Builder()
                .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add(CHANNEL_ID, ChannelId)
                .add("conversation_status", status)
                .build();
        RestClient.getApiInterface().updateConversationStatus(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap())
                .enqueue(new ResponseResolver<CommonResponse>(homeActivity, true, false) {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        homeActivity.togglePinChannel(ChannelId, !isPinned);
                        dismiss();
                    }

                    @Override
                    public void failure(APIError error) {
                        dismiss();
                        homeActivity.showErrorMessage(error.getMessage());
                    }
                });
    }

    private void apiManipulateChannel(final boolean isMutedTrue) {
        JSONObject user_properties = new JSONObject();
        try {
            user_properties.put("enable_vibration", isMutedTrue);
            user_properties.put("push_notification_sound", "test.mp3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CommonParams.Builder commonParams;
        commonParams = new CommonParams.Builder();
        commonParams.add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId());
        commonParams.add("channel_id", ChannelId);
        if (isMutedTrue) {
            commonParams.add("notification", FuguAppConstant.NOTIFICATION_LEVEL.ALL_MESSAGES.toString());
        } else {
            commonParams.add("notification", FuguAppConstant.NOTIFICATION_LEVEL.ALL_MENTIONS.toString());
        }
        commonParams.build();

        RestClient.getApiInterface().editInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.build().getMap()).enqueue(new ResponseResolver<EditInfoResponse>(homeActivity, true, false) {
            @Override
            public void success(EditInfoResponse editInfoResponse) {
                String isMuted = "";
                if (isMutedTrue) {
                    isMuted = FuguAppConstant.NOTIFICATION_LEVEL.ALL_MESSAGES.toString();
                } else {
                    isMuted = FuguAppConstant.NOTIFICATION_LEVEL.ALL_MENTIONS.toString();
                }
                homeActivity.muteGroup(ChannelId, isMuted);
                dismiss();
            }

            @Override
            public void failure(APIError error) {

            }
        });
    }

    /**
     * Fetches members involved
     */
    private void fetchMembers() {
        CommonParams commonParams = new CommonParams.Builder()
                .add(CHANNEL_ID, ChannelId)
                .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add(GET_DATA_TYPE, "MEMBERS")
                .build();

        RestClient.getApiInterface().getGroupInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap())
                .enqueue(new ResponseResolver<MediaResponse>(homeActivity, false, false) {
                    @Override
                    public void success(final MediaResponse getMembersResponse) {
                        for (int i = 0; i < getMembersResponse.getData().getChatMembers().size(); i++) {
                            if (Long.valueOf(getMembersResponse.getData().getChatMembers().get(i).getUserId()).compareTo(Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId())) != 0) {
                                Intent mIntent = new Intent(homeActivity, ProfileActivity.class);
                                mIntent.putExtra("open_profile", getMembersResponse.getData().getChatMembers().get(i).getUserId() + "");
                                mIntent.putExtra("email", getMembersResponse.getData().getChatMembers().get(i).getUserId() + "");
                                mIntent.putExtra("channelId", ChannelId);
                                startActivity(mIntent);
                                break;
                            }
                        }

                        homeActivity.hideLoading();
                        dismiss();
                    }

                    @Override
                    public void failure(APIError error) {
                        homeActivity.hideLoading();
                    }
                });
    }
}
