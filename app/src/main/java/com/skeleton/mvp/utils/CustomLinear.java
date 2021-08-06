package com.skeleton.mvp.utils;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Bhavya Rattan on 10/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class CustomLinear extends LinearLayout {
    private OnKeyboardOpened mOnKeyboardOpened;

    public CustomLinear(Context context) {
        super(context);
    }

    public CustomLinear(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomLinear(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomLinear(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public interface OnKeyboardOpened {
        public boolean onKeyBoardStateChanged(boolean isVisible);
    }

    public void setOnKeyBoardStateChanged(OnKeyboardOpened onKeyboardOpened) {
        mOnKeyboardOpened = onKeyboardOpened;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
        final int actualHeight = getHeight();

        if (actualHeight > proposedheight) {
            // Keyboard is shown
            mOnKeyboardOpened.onKeyBoardStateChanged(true);
        } else {
            // Keyboard is hidden
            if (mOnKeyboardOpened!=null) {
                mOnKeyboardOpened.onKeyBoardStateChanged(false);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
