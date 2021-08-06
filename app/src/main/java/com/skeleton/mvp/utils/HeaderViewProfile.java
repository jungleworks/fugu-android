package com.skeleton.mvp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skeleton.mvp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by anton on 11/12/15.
 */

public class HeaderViewProfile extends LinearLayout {

    @Bind(R.id.group_info_name)
    TextView name;


    public HeaderViewProfile(Context context) {
        super(context);
    }

    public HeaderViewProfile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderViewProfile(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderViewProfile(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(String name, String lastSeen) {
        this.name.setText(name);
    }

    public void setName(String name){
        this.name.setText(name);
    }

    public void setTextSize(float size) {
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }
}