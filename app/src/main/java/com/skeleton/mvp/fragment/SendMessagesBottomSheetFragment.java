package com.skeleton.mvp.fragment;

/**
 * Created by rajatdhamija
 * 19/04/18.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.GroupInformationActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.ui.groupspecific.OldGroupSpecificActivity;


public class SendMessagesBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private Context context;
    private Boolean notificationLevel;
    private RadioButton radioOne, radioTwo;
    private LinearLayout llOne, llTwo;
    private int pos;
    private Long channelId;
    public SendMessagesBottomSheetFragment() {

    }

    public static SendMessagesBottomSheetFragment newInstance(int arg, Boolean notificationLevel, Context context, int pos, Long channelId) {
        SendMessagesBottomSheetFragment frag = new SendMessagesBottomSheetFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setNotificationLevel(notificationLevel);
        frag.setContext(context);
        frag.setPos(pos);
        frag.setChannelId(channelId);
        return frag;
    }

    private void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    private void setPos(int pos) {
        this.pos = pos;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private void setNotificationLevel(Boolean notificationLevel) {
        this.notificationLevel = notificationLevel;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_messages_bottom_sheet, container, false);
        radioOne = view.findViewById(R.id.radioOne);
        radioTwo = view.findViewById(R.id.radioTwo);
        llOne = view.findViewById(R.id.llOne);
        llTwo = view.findViewById(R.id.llTwo);
        radioOne.setOnClickListener(null);
        radioOne.setClickable(false);
        radioOne.setFocusableInTouchMode(false);
        radioOne.setFocusable(false);

        radioTwo.setOnClickListener(null);
        radioTwo.setClickable(false);
        radioTwo.setFocusableInTouchMode(false);
        radioTwo.setFocusable(false);
        String name = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName();

        try {
            name = name.split(" ")[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (notificationLevel) {
            radioOne.setChecked(false);
            radioTwo.setChecked(true);
        } else {
            radioOne.setChecked(true);
            radioTwo.setChecked(false);
        }


        llOne.setOnClickListener(v -> {
            radioOne.setChecked(true);
            radioTwo.setChecked(false);
            if (context instanceof GroupInformationActivity) {
                ((GroupInformationActivity) context).updateSendMessages(false);
            } else {
                ((OldGroupSpecificActivity) context).updateNotificationSettings(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MESSAGES.toString(), pos, channelId);
            }
            dismiss();
        });

        llTwo.setOnClickListener(v -> {
            radioOne.setChecked(false);
            radioTwo.setChecked(true);
            if (context instanceof GroupInformationActivity) {
                ((GroupInformationActivity) context).updateSendMessages(true);
            } else {
                ((OldGroupSpecificActivity) context).updateNotificationSettings(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MENTIONS.toString(), pos, channelId);
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {


        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
