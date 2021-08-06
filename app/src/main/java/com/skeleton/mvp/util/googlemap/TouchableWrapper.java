package com.skeleton.mvp.util.googlemap;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

/**
 * Developer: Click Labs
 */
public final class TouchableWrapper extends FrameLayout {
    private static final int MIN_MILLISECONDS = 50;
    private static final int MAP_ZOOM_ANIM_DURATION = 400;
    private static final double ZOOM_VALUE_DIVIDER = 1.55d;
    private int fingers = 0;
    private long lastZoomTime = 0;
    private float lastSpan = -1;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    /**
     * @param context of calling activity
     */
    public TouchableWrapper(final Context context) {
        super(context);
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(final ScaleGestureDetector detector) {
                if (lastSpan == -1) {
                    lastSpan = detector.getCurrentSpan();
                } else if (detector.getEventTime() - lastZoomTime >= MIN_MILLISECONDS) {
                    lastZoomTime = detector.getEventTime();
                    lastSpan = detector.getCurrentSpan();
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(final ScaleGestureDetector detector) {
                lastSpan = -1;
                return true;
            }

            @Override
            public void onScaleEnd(final ScaleGestureDetector detector) {
                lastSpan = -1;
            }
        });
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTapEvent(final MotionEvent e) {
                disableScrolling();
                return true;
            }
        });
    }

    /**
     * @param gMap instance of google map
     */

    /**
     * @param currentSpan current span of map
     * @param mLastSpan   last span of map
     * @return zoom value of map
     */
    private float getZoomValue(final float currentSpan, final float mLastSpan) {
        double value = Math.log(currentSpan / mLastSpan) / Math.log(ZOOM_VALUE_DIVIDER);
        return (float) value;
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                fingers = fingers + 1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                fingers = fingers - 1;
                break;
            case MotionEvent.ACTION_UP:
                fingers = 0;
                break;
            case MotionEvent.ACTION_DOWN:
                fingers = 1;
                break;
            default:
                break;
        }
        if (fingers > 1) {
            disableScrolling();
        } else if (fingers < 1) {
            enableScrolling();
        }
        if (fingers > 1) {
            return scaleGestureDetector.onTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    /**
     *
     */
    private void enableScrolling() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, MIN_MILLISECONDS);
    }

    /**
     *
     */
    private void disableScrolling() {
    }
}