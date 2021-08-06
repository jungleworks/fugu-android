package com.skeleton.mvp.util;

import android.content.Context;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class UserLockBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    private boolean mAllowUserDragging = true;
    /**
     * Default constructor for instantiating BottomSheetBehaviors.
     */
    public UserLockBottomSheetBehavior() {
        super();
    }

    /**
     * Default constructor for inflating BottomSheetBehaviors from layout.
     *
     * @param context The {@link Context}.
     * @param attrs   The {@link AttributeSet}.
     */
    public UserLockBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAllowUserDragging(boolean allowUserDragging) {
        mAllowUserDragging = allowUserDragging;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!mAllowUserDragging) {
            return false;
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }
}
