package com.skeleton.mvp.fragment;

/**
 * Created by rajatdhamija
 * 19/04/18.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.skeleton.mvp.R;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.NotificationSnoozeTime;
import com.skeleton.mvp.data.model.notifications.NotificationSettingsActivity;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.util.Utils;
import com.skeleton.mvp.utils.DateUtils;

import java.util.ArrayList;


public class SnoozeBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private Context context;
    private LinearLayout llRadioButtons;
    private ArrayList<NotificationSnoozeTime> notificationSnoozeTimes = new ArrayList<>();
    private Boolean isExpired = false;
    private LinearLayout llSnoozeOptions, llSnoozeView;
    private AppCompatTextView tvSnoozeTime, tvEndSnooze;
    private String snoozeTime;

    public SnoozeBottomSheetFragment() {

    }

    public static SnoozeBottomSheetFragment newInstance(int arg, Context context, Boolean isExpired, String snoozeTime) {
        SnoozeBottomSheetFragment frag = new SnoozeBottomSheetFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setContext(context);
        frag.setIsExpired(isExpired);
        frag.setSnoozeTime(snoozeTime);
        return frag;
    }

    private void setSnoozeTime(String snoozeTime) {
        this.snoozeTime = snoozeTime;
    }

    private void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }


    private void setContext(Context context) {
        this.context = context;
    }

    private void createRadioButton() {
        final RadioButton[] rb = new RadioButton[notificationSnoozeTimes.size()];
        RadioGroup rg = new RadioGroup(context); //create the RadioGroup
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        for (int i = 0; i < notificationSnoozeTimes.size() - 1; i++) {
            rb[i] = new RadioButton(context);
            rb[i].setText(notificationSnoozeTimes.get(i).getDescription());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rb[i].setTextAppearance(R.style.CustomTextAppearance_TitilliumWeb);
            }
            rb[i].setTextSize(17f);
            rb[i].setPadding(0, Utils.dpToPx(context, 20), 0, Utils.dpToPx(context, 20));
            rb[i].setId(i);
            rg.addView(rb[i]);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("Position:", checkedId + "");
                ((NotificationSettingsActivity) context).snoozeNotifications(notificationSnoozeTimes.get(checkedId).getTime_slot());
                dismiss();
            }
        });
        llRadioButtons.addView(rg);//you add the whole RadioGroup to the layout

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.snooze_bottom_sheet, container, false);
        llRadioButtons = view.findViewById(R.id.llRadioButtons);
        llSnoozeOptions = view.findViewById(R.id.llSnoozeOptions);
        llSnoozeView = view.findViewById(R.id.llSnoozeView);
        tvSnoozeTime = view.findViewById(R.id.tvSnoozeTime);
        tvEndSnooze = view.findViewById(R.id.tvEndSnooze);
        notificationSnoozeTimes = (ArrayList<NotificationSnoozeTime>) CommonData.getCommonResponse().getData().getUserInfo().getNotificationSnoozeTime();
        createRadioButton();

        if (isExpired) {
            llSnoozeOptions.setVisibility(View.VISIBLE);
            llSnoozeView.setVisibility(View.GONE);
        } else {
            llSnoozeOptions.setVisibility(View.GONE);
            llSnoozeView.setVisibility(View.VISIBLE);
        }


        tvSnoozeTime.setText("Scheduled until " + new DateUtils().getDate(new DateUtils().convertToLocal(snoozeTime)) + ", " + new DateUtils().getTime(new DateUtils().convertToLocal(snoozeTime)));
        tvEndSnooze.setOnClickListener(v -> {
            ((NotificationSettingsActivity) context).endSnooze();
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
