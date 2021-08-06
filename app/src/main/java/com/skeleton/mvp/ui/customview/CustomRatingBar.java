package com.skeleton.mvp.ui.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.skeleton.mvp.R;

/**
 * Developer: Click Labs
 */
public class CustomRatingBar extends LinearLayout {
    private static final int MAX_STARS = 5;
    private static final float CURRENT_SCORE = 2.5f;
    private static final float HALF_SCORE = 0.5f;
    private static final int ANIM_DURATION = 100;
    private static final float PRESSED_STAR_SCALE = 1.1f;
    private static final float SCORE_3 = 3f;
    private static final double FIFTY = 50;
    private int mMaxStars = MAX_STARS;
    private float mCurrentScore = 0f;
    private int mStarOnResource = R.drawable.star_blue;
    private int mStarOffResource = R.drawable.star_grey;
    private int mStarHalfResource = R.drawable.star_blue;
    private ImageView[] mStarsViews;
    private float mStarPadding;
    private IRatingBarCallbacks onScoreChanged;
    private int mLastStarId;
    private boolean mOnlyForDisplay;
    private double mLastX;
    private boolean mHalfStars = true;

    /**
     * Instantiates a new Custom rating bar.
     *
     * @param context the context
     */
    public CustomRatingBar(final Context context) {
        super(context);
        init();
    }

    /**
     * Instantiates a new Custom rating bar.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CustomRatingBar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(attrs, context);
        init();
    }

    /**
     * Instantiates a new Custom rating bar.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomRatingBar(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        initializeAttributes(attrs, context);
        init();
    }

    /**
     * Gets on score changed.
     *
     * @return the on score changed
     */
    public IRatingBarCallbacks getOnScoreChanged() {
        return onScoreChanged;
    }

    /**
     * Sets on score changed.
     *
     * @param onScoreChanged the on score changed
     */
    public void setOnScoreChanged(final IRatingBarCallbacks onScoreChanged) {
        this.onScoreChanged = onScoreChanged;
    }

    /**
     * Gets score.
     *
     * @return the score
     */
    public float getScore() {
        return mCurrentScore;
    }

    /**
     * Sets score.
     *
     * @param score the score
     */
    public void setScore(final float score) {
        float mScore = score;
        mScore = Math.round(score * 2) / 2.0f;
        if (!mHalfStars) {
            mScore = Math.round(score);
        }
        mCurrentScore = mScore;
        refreshStars();
    }

    /**
     * Sets scroll to select.
     *
     * @param enabled the enabled
     */
    public void setScrollToSelect(final boolean enabled) {
        mOnlyForDisplay = !enabled;
    }

    /**
     * @param attrs   AttributeSet
     * @param context of the calling activity or fragment
     */
    private void initializeAttributes(final AttributeSet attrs, final Context context) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomRatingBar);
        final int n = a.getIndexCount();
        for (int i = 0; i < n; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.CustomRatingBar_maxStars) {
                mMaxStars = a.getInt(attr, MAX_STARS);
            } else if (attr == R.styleable.CustomRatingBar_stars) {
                mCurrentScore = a.getFloat(attr, CURRENT_SCORE);
            } else if (attr == R.styleable.CustomRatingBar_starHalf) {
                mStarHalfResource = a.getResourceId(attr, android.R.drawable.star_on);
            } else if (attr == R.styleable.CustomRatingBar_starOn) {
                mStarOnResource = a.getResourceId(attr, android.R.drawable.star_on);
            } else if (attr == R.styleable.CustomRatingBar_starOff) {
                mStarOffResource = a.getResourceId(attr, android.R.drawable.star_off);
            } else if (attr == R.styleable.CustomRatingBar_starPadding) {
                mStarPadding = a.getDimension(attr, 0);
            } else if (attr == R.styleable.CustomRatingBar_onlyForDisplay) {
                mOnlyForDisplay = a.getBoolean(attr, false);
            } else if (attr == R.styleable.CustomRatingBar_halfStars) {
                mHalfStars = a.getBoolean(attr, true);
            }
        }
        a.recycle();
    }

    /**
     * Init.
     */
    void init() {
        mStarsViews = new ImageView[mMaxStars];
        for (int i = 0; i < mMaxStars; i++) {
            ImageView v = createStar();
            addView(v);
            mStarsViews[i] = v;
        }
        refreshStars();
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        return true;
    }

    /**
     * hardcore math over here
     *
     * @param x position
     * @return score
     */
    private float getScoreForPosition(final float x) {
        if (mHalfStars) {
            return (float) Math.round(((x / ((float) getWidth() / (mMaxStars * SCORE_3))) / SCORE_3) * 2f) / 2;
        }
        float value = (float) Math.round(x / ((float) getWidth() / mMaxStars));
        if (value <= 0) {
            value = 1;
        }
        if (value > mMaxStars) {
            value = mMaxStars;
        }
        return value;
    }

    /**
     * @param score for which image is required
     * @return image id for score
     */
    private int getImageForScore(final float score) {
        if (score > 0) {
            return Math.round(score) - 1;
        } else {
            return -1;
        }
    }

    /**
     *
     */
    private void refreshStars() {
        boolean flagHalf = (mCurrentScore != 0 && (mCurrentScore % HALF_SCORE == 0)) && mHalfStars;
        for (int i = 1; i <= mMaxStars; i++) {
            if (i <= mCurrentScore) {
                mStarsViews[i - 1].setImageResource(mStarOnResource);
            } else {
                if (flagHalf && i - HALF_SCORE <= mCurrentScore) {
                    mStarsViews[i - 1].setImageResource(mStarHalfResource);
                } else {
                    mStarsViews[i - 1].setImageResource(mStarOffResource);
                }
            }
        }
    }

    /**
     * @return imageView object of star
     */
    private ImageView createStar() {
        ImageView v = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        v.setPadding((int) mStarPadding, 0, (int) mStarPadding, 0);
        v.setAdjustViewBounds(true);
        v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        v.setLayoutParams(params);
        v.setImageResource(mStarOffResource);
        return v;
    }

    /**
     * @param position for which image of star
     * @return imageView of star
     */
    private ImageView getImageView(final int position) {
        try {
            return mStarsViews[position];
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull final MotionEvent event) {
        if (mOnlyForDisplay) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                animateStarRelease(getImageView(mLastStarId));
                mLastStarId = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mLastX) > FIFTY) {
                    requestDisallowInterceptTouchEvent(true);
                }
                float lastscore = mCurrentScore;
                mCurrentScore = getScoreForPosition(event.getX());
                if (lastscore != mCurrentScore) {
                    animateStarRelease(getImageView(mLastStarId));
                    animateStarPressed(getImageView(getImageForScore(mCurrentScore)));
                    mLastStarId = getImageForScore(mCurrentScore);
                    refreshStars();
                    if (onScoreChanged != null) {
                        onScoreChanged.scoreChanged(mCurrentScore);
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                lastscore = mCurrentScore;
                mCurrentScore = getScoreForPosition(event.getX());
                animateStarPressed(getImageView(getImageForScore(mCurrentScore)));
                mLastStarId = getImageForScore(mCurrentScore);
                if (lastscore != mCurrentScore) {
                    refreshStars();
                    if (onScoreChanged != null) {
                        onScoreChanged.scoreChanged(mCurrentScore);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * @param star imageview of start on which animation shows
     */
    private void animateStarPressed(final ImageView star) {
        if (star != null) {
            ViewCompat.animate(star).scaleX(PRESSED_STAR_SCALE).scaleY(PRESSED_STAR_SCALE).setDuration(ANIM_DURATION).start();
        }
    }

    /**
     * @param star imageview of start on which animation shows
     */
    private void animateStarRelease(final ImageView star) {
        if (star != null) {
            ViewCompat.animate(star).scaleX(1f).scaleY(1f).setDuration(ANIM_DURATION).start();
        }
    }

    /**
     * Is half stars boolean.
     *
     * @return the boolean
     */
    public boolean isHalfStars() {
        return mHalfStars;
    }

    /**
     * Sets half stars.
     *
     * @param halfStars the half stars
     */
    public void setHalfStars(final boolean halfStars) {
        mHalfStars = halfStars;
    }

    /**
     * The interface Rating bar callbacks.
     */
    public interface IRatingBarCallbacks {
        /**
         * Score changed.
         *
         * @param score the score
         */
        void scoreChanged(final float score);
    }
}