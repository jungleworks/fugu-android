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
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.GroupInformationActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.ui.groupspecific.OldGroupSpecificActivity;


public class NotificationBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private Context context;
    private String notificationLevel;
    private RadioButton radioOne, radioTwo, radioThree;
    private LinearLayout llOne, llTwo, llThree;
    private int pos;
    private Long channelId;
    private TextView tvMentions, tvDirectMentions;

    public NotificationBottomSheetFragment() {

    }

    public static NotificationBottomSheetFragment newInstance(int arg, String notificationLevel, Context context, int pos, Long channelId) {
        NotificationBottomSheetFragment frag = new NotificationBottomSheetFragment();
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

    private void setNotificationLevel(String notificationLevel) {
        this.notificationLevel = notificationLevel;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_bottom_sheet, container, false);
        radioOne = view.findViewById(R.id.radioOne);
        radioTwo = view.findViewById(R.id.radioTwo);
        radioThree = view.findViewById(R.id.radioThree);

        tvMentions = view.findViewById(R.id.tvMentions);
        tvDirectMentions = view.findViewById(R.id.tvDirectMentions);

        llOne = view.findViewById(R.id.llOne);
        llTwo = view.findViewById(R.id.llTwo);
        llThree = view.findViewById(R.id.llThree);

        radioOne.setOnClickListener(null);
        radioOne.setClickable(false);
        radioOne.setFocusableInTouchMode(false);
        radioOne.setFocusable(false);

        radioTwo.setOnClickListener(null);
        radioTwo.setClickable(false);
        radioTwo.setFocusableInTouchMode(false);
        radioTwo.setFocusable(false);

        radioThree.setOnClickListener(null);
        radioThree.setClickable(false);
        radioThree.setFocusableInTouchMode(false);
        radioThree.setFocusable(false);

        String name = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName();

        try {
            name = name.split(" ")[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvMentions.setText("@Everyone and @" + name + " will notify you");
        tvDirectMentions.setText("@" + name + " will notify you and nothing else");

        if (notificationLevel.equals(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MESSAGES.toString())) {
            radioOne.setChecked(true);
            radioTwo.setChecked(false);
            radioThree.setChecked(false);
        } else if (notificationLevel.equals(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MENTIONS.toString())) {
            radioOne.setChecked(false);
            radioTwo.setChecked(true);
            radioThree.setChecked(false);
        } else {
            radioOne.setChecked(false);
            radioTwo.setChecked(false);
            radioThree.setChecked(true);
        }


        llOne.setOnClickListener(v -> {
            radioOne.setChecked(true);
            radioTwo.setChecked(false);
            radioThree.setChecked(false);
            if (context instanceof GroupInformationActivity) {
                ((GroupInformationActivity) context).updateNotificationSettings(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MESSAGES.toString());
            } else {
                ((OldGroupSpecificActivity) context).updateNotificationSettings(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MESSAGES.toString(), pos, channelId);
            }
            dismiss();
        });

        llTwo.setOnClickListener(v -> {
            radioOne.setChecked(false);
            radioTwo.setChecked(true);
            radioThree.setChecked(false);
            if (context instanceof GroupInformationActivity) {
                ((GroupInformationActivity) context).updateNotificationSettings(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MENTIONS.toString());
            } else {
                ((OldGroupSpecificActivity) context).updateNotificationSettings(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MENTIONS.toString(), pos, channelId);
            }
            dismiss();
        });

        llThree.setOnClickListener(v -> {
            radioOne.setChecked(false);
            radioTwo.setChecked(false);
            radioThree.setChecked(true);
            if (context instanceof GroupInformationActivity) {
                ((GroupInformationActivity) context).updateNotificationSettings(FuguAppConstant.NOTIFICATION_LEVEL.DIRECT_MENTIONS.toString());
            } else {
                ((OldGroupSpecificActivity) context).updateNotificationSettings(FuguAppConstant.NOTIFICATION_LEVEL.DIRECT_MENTIONS.toString(), pos, channelId);
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
