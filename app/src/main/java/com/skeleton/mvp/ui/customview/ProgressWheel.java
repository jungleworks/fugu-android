package com.skeleton.mvp.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.skeleton.mvp.R;


/**
 * A Material style progress wheel, compatible up to 2.2.
 * Todd Davies' Progress Wheel https://github.com/Todd-Davies/ProgressWheel
 *
 * @author Nico Hormazábal
 *         <p/>
 *         Licensed under the Apache License 2.0 license see:
 *         http://www.apache.org/licenses/LICENSE-2.0
 */
public class ProgressWheel extends View {
    private static final float DEGREE_360 = 360.0f;
    private static final int HEX_BLACK = 0xAA000000;
    private static final int HEX_WHITE = 0x00FFFFFF;
    private static final int CIRCLE_RADIUS = 28;
    private static final int DEGREE_90 = 90;
    private static final int HUNDRED = 100;
    private static final int BAR_SPIN_CYCLE_TIME = 460;
    private static final float THOUSAND = 1000.0f;
    private static final float HALF = 0.5f;
    private static final float EDIT_MODE_SWEEP_ANGLE = 135;
    private static final float SPIN_SPEED = 230.0f;
    private static final String TAG = ProgressWheel.class.getSimpleName();
    private final int barLength = 16;
    private final int barMaxLength = 270;
    private final long pauseGrowingTime = 200;
    // Sizes (with defaults in DP)
    private int circleRadius = CIRCLE_RADIUS;
    private int barWidth = 4;
    private int rimWidth = 4;
    private boolean fillRadius = false;
    private double timeStartGrowing = 0;
    private double barSpinCycleTime = BAR_SPIN_CYCLE_TIME;
    private float barExtraLength = 0;
    private boolean barGrowingFromFront = true;
    private long pausedTimeWithoutGrowing = 0;
    // Colors (with defaults)
    private int barColor = HEX_BLACK;
    private int rimColor = HEX_WHITE;
    // Paints
    private Paint barPaint = new Paint();
    private Paint rimPaint = new Paint();
    // Rectangles
    private RectF circleBounds = new RectF();
    // Animation
    // The amount of degrees per second
    private float spinSpeed = SPIN_SPEED;
    // private float spinSpeed = 120.0f;
    // The last time the spinner was animated
    private long lastTimeAnimated = 0;
    private boolean linearProgress;
    private float mProgress = 0.0f;
    private float mTargetProgress = 0.0f;
    private boolean isSpinning = false;
    private ProgressCallback callback;

    /**
     * The constructor for the ProgressWheel
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ProgressWheel(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context.obtainStyledAttributes(attrs,
                R.styleable.ProgressWheel));
    }

    /**
     * The constructor for the ProgressWheel
     *
     * @param context the context
     */
    public ProgressWheel(final Context context) {
        super(context);
    }

    // ----------------------------------
    // Setting up stuff
    // ----------------------------------
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int viewWidth = circleRadius + this.getPaddingLeft()
                + this.getPaddingRight();
        final int viewHeight = circleRadius + this.getPaddingTop()
                + this.getPaddingBottom();
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int width;
        final int height;
        // Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            // Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            width = Math.min(viewWidth, widthSize);
        } else {
            // Be whatever you want
            width = viewWidth;
        }
        // Measure Height
        if (heightMode == MeasureSpec.EXACTLY
                || widthMode == MeasureSpec.EXACTLY) {
            // Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            height = Math.min(viewHeight, heightSize);
        } else {
            // Be whatever you want
            height = viewHeight;
        }
        setMeasuredDimension(width, height);
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of
     * the view, because this method is called after measuring the dimensions of
     * MATCH_PARENT & WRAP_CONTENT. Use this dimensions to setup the bounds and
     * paints.
     *
     * @param w    width
     * @param h    height
     * @param oldw old width
     * @param oldh old height
     */
    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupBounds(w, h);
        setupPaints();
        invalidate();
    }

    /**
     * Set the properties of the paints we're using to draw the progress wheel
     */
    private void setupPaints() {
        barPaint.setColor(barColor);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeWidth(barWidth);
        rimPaint.setColor(rimColor);
        rimPaint.setAntiAlias(true);
        rimPaint.setStyle(Paint.Style.STROKE);
        rimPaint.setStrokeWidth(rimWidth);
    }

    /**
     * Set the bounds of the component
     *
     * @param layoutWidth  width of layout
     * @param layoutHeight height of layout
     */
    private void setupBounds(final int layoutWidth, final int layoutHeight) {
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        if (!fillRadius) {
            // Width should equal to Height, find the min value to setup the
            // circle_blue
            final int minValue = Math.min(layoutWidth - paddingLeft - paddingRight,
                    layoutHeight - paddingBottom - paddingTop);
            final int circleDiameter = Math.min(minValue, circleRadius * 2 - barWidth
                    * 2);
            // Calc the Offset if needed for centering the wheel in the
            // available space
            final int xOffset = (layoutWidth - paddingLeft - paddingRight - circleDiameter)
                    / 2 + paddingLeft;
            final int yOffset = (layoutHeight - paddingTop - paddingBottom - circleDiameter)
                    / 2 + paddingTop;
            circleBounds = new RectF(xOffset + barWidth, yOffset + barWidth,
                    xOffset + circleDiameter - barWidth, yOffset
                    + circleDiameter - barWidth);
        } else {
            circleBounds = new RectF(paddingLeft + barWidth, paddingTop
                    + barWidth, layoutWidth - paddingRight - barWidth,
                    layoutHeight - paddingBottom - barWidth);
        }
    }

    /**
     * Parse the attributes passed to the view from the XML
     *
     * @param a the attributes to parse
     */
    private void parseAttributes(final TypedArray a) {
        // We transform the default values from DIP to pixels
        final DisplayMetrics metrics = getContext().getResources()
                .getDisplayMetrics();
        barWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                barWidth, metrics);
        rimWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                rimWidth, metrics);
        circleRadius = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, circleRadius, metrics);
        circleRadius = (int) a.getDimension(
                R.styleable.ProgressWheel_matProg_circleRadius, circleRadius);
        fillRadius = a.getBoolean(R.styleable.ProgressWheel_matProg_fillRadius,
                false);
        barWidth = (int) a.getDimension(
                R.styleable.ProgressWheel_matProg_barWidth, barWidth);
        rimWidth = (int) a.getDimension(
                R.styleable.ProgressWheel_matProg_rimWidth, rimWidth);
        final float baseSpinSpeed = a
                .getFloat(R.styleable.ProgressWheel_matProg_spinSpeed,
                        spinSpeed / DEGREE_360);
        spinSpeed = baseSpinSpeed * DEGREE_360;
        barSpinCycleTime = a.getInt(
                R.styleable.ProgressWheel_matProg_barSpinCycleTime,
                (int) barSpinCycleTime);
        barColor = a.getColor(R.styleable.ProgressWheel_matProg_barColor,
                barColor);
        rimColor = a.getColor(R.styleable.ProgressWheel_matProg_rimColor,
                rimColor);
        linearProgress = a.getBoolean(
                R.styleable.ProgressWheel_matProg_linearProgress, false);
        if (a.getBoolean(
                R.styleable.ProgressWheel_matProg_progressIndeterminate, false)) {
            spin();
        }
        // Recycle
        a.recycle();
    }

    /**
     * Sets callback.
     *
     * @param progressCallback the progress callback
     */
    public void setCallback(final ProgressCallback progressCallback) {
        callback = progressCallback;
        if (!isSpinning) {
            runCallback();
        }
    }

    /**
     * Animation stuff
     *
     * @param canvas canvas on which progress wheel draw
     */
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(circleBounds, DEGREE_360, DEGREE_360, false, rimPaint);
        boolean mustInvalidate = false;
        if (isSpinning) {
            // Draw the spinning bar
            mustInvalidate = true;
            final long deltaTime = SystemClock.uptimeMillis() - lastTimeAnimated;
            final float deltaNormalized = deltaTime * spinSpeed / THOUSAND;
            updateBarLength(deltaTime);
            mProgress += deltaNormalized;
            if (mProgress > DEGREE_360) {
                mProgress -= DEGREE_360;
            }
            lastTimeAnimated = SystemClock.uptimeMillis();
            float from = mProgress - DEGREE_90;
            float length = barLength + barExtraLength;
            if (isInEditMode()) {
                from = 0;
                length = EDIT_MODE_SWEEP_ANGLE;
            }
            canvas.drawArc(circleBounds, from, length, false, barPaint);
        } else {
            final float oldProgress = mProgress;
            if (mProgress != mTargetProgress) {
                // We smoothly increase the progress bar
                mustInvalidate = true;
                final float deltaTime = (float) (SystemClock.uptimeMillis() - lastTimeAnimated) / THOUSAND;
                final float deltaNormalized = deltaTime * spinSpeed;
                mProgress = Math.min(mProgress + deltaNormalized,
                        mTargetProgress);
                lastTimeAnimated = SystemClock.uptimeMillis();
            }
            if (oldProgress != mProgress) {
                runCallback();
            }
            float offset = 0.0f;
            float progress = mProgress;
            if (!linearProgress) {
                final float factor = 2.0f;
                offset = (float) (1.0f - Math.pow(1.0f - mProgress / DEGREE_360,
                        2.0f * factor)) * DEGREE_360;
                progress = (float) (1.0f - Math.pow(1.0f - mProgress / DEGREE_360,
                        factor)) * DEGREE_360;
            }
            if (isInEditMode()) {
                progress = DEGREE_360;
            }
            canvas.drawArc(circleBounds, offset - DEGREE_90, progress, false, barPaint);
        }
        if (mustInvalidate) {
            invalidate();
        }
    }

    @Override
    protected void onVisibilityChanged(final View changedView, final int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            lastTimeAnimated = SystemClock.uptimeMillis();
        }
    }

    /**
     * @param deltaTimeInMilliSeconds time in milli seconds after which bar update
     */
    private void updateBarLength(final long deltaTimeInMilliSeconds) {
        if (pausedTimeWithoutGrowing >= pauseGrowingTime) {
            timeStartGrowing += deltaTimeInMilliSeconds;
            if (timeStartGrowing > barSpinCycleTime) {
                // We completed a size change cycle
                // (growing or shrinking)
                timeStartGrowing -= barSpinCycleTime;
                // if(barGrowingFromFront) {
                pausedTimeWithoutGrowing = 0;
                // }
                barGrowingFromFront = !barGrowingFromFront;
            }
            final float distance = (float) Math.cos((timeStartGrowing
                    / barSpinCycleTime + 1)
                    * Math.PI) / 2 + HALF;
            final float destLength = barMaxLength - barLength;
            if (barGrowingFromFront) {
                barExtraLength = distance * destLength;
            } else {
                final float newLength = destLength * (1 - distance);
                mProgress += barExtraLength - newLength;
                barExtraLength = newLength;
            }
        } else {
            pausedTimeWithoutGrowing += deltaTimeInMilliSeconds;
        }
    }

    /**
     * Check if the wheel is currently spinning
     *
     * @return the boolean
     */
    public boolean isSpinning() {
        return isSpinning;
    }

    /**
     * Reset the count (in increment mode)
     */
    public void resetCount() {
        mProgress = 0.0f;
        mTargetProgress = 0.0f;
        invalidate();
    }

    /**
     * Turn off spin mode
     */
    public void stopSpinning() {
        isSpinning = false;
        mProgress = 0.0f;
        mTargetProgress = 0.0f;
        invalidate();
    }

    /**
     * Puts the view on spin mode
     */
    public void spin() {
        lastTimeAnimated = SystemClock.uptimeMillis();
        isSpinning = true;
        invalidate();
    }

    /**
     *
     */
    private void runCallback() {
        if (callback != null) {
            final float normalizedProgress = (float) Math
                    .round(mProgress * HUNDRED / DEGREE_360) / HUNDRED;
            callback.onProgressUpdate(normalizedProgress);
        }
    }

    /**
     * Set the progress to a specific value, the bar will be set instantly to
     * that value
     *
     * @param prog the progress between 0 and 1
     */
    public void setInstantProgress(final float prog) {
        float progress = prog;
        if (isSpinning) {
            mProgress = 0.0f;
            isSpinning = false;
        }
        if (progress > 1.0f) {
            progress -= 1.0f;
        } else if (progress < 0) {
            progress = 0;
        }
        if (progress == mTargetProgress) {
            return;
        }
        mTargetProgress = Math.min(progress * DEGREE_360, DEGREE_360);
        mProgress = mTargetProgress;
        lastTimeAnimated = SystemClock.uptimeMillis();
        invalidate();
    }

    // Great way to save a view's state
    // http://stackoverflow.com/a/7089687/1991053
    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final WheelSavedState ss = new WheelSavedState(superState);
        // We save everything that can be changed at runtime
        ss.mProgress = this.mProgress;
        ss.mTargetProgress = this.mTargetProgress;
        ss.isSpinning = this.isSpinning;
        ss.spinSpeed = this.spinSpeed;
        ss.barWidth = this.barWidth;
        ss.barColor = this.barColor;
        ss.rimWidth = this.rimWidth;
        ss.rimColor = this.rimColor;
        ss.circleRadius = this.circleRadius;
        ss.linearProgress = this.linearProgress;
        ss.fillRadius = this.fillRadius;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        if (!(state instanceof WheelSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        final WheelSavedState ss = (WheelSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mProgress = ss.mProgress;
        this.mTargetProgress = ss.mTargetProgress;
        this.isSpinning = ss.isSpinning;
        this.spinSpeed = ss.spinSpeed;
        this.barWidth = ss.barWidth;
        this.barColor = ss.barColor;
        this.rimWidth = ss.rimWidth;
        this.rimColor = ss.rimColor;
        this.circleRadius = ss.circleRadius;
        this.linearProgress = ss.linearProgress;
        this.fillRadius = ss.fillRadius;
    }

    /**
     * Gets progress.
     *
     * @return the current progress between 0.0 and 1.0, if the wheel is indeterminate, then the result is -1
     */
    public float getProgress() {
        if (isSpinning) {
            return -1;
        } else {
            return mProgress / DEGREE_360;
        }
    }
    // ----------------------------------
    // Getters + setters
    // ----------------------------------

    /**
     * Set the progress to a specific value, the bar will smoothly animate until
     * that value
     *
     * @param prog the progress between 0 and 1
     */
    public void setProgress(final float prog) {
        float progress = prog;
        if (isSpinning) {
            mProgress = 0.0f;
            isSpinning = false;
            runCallback();
        }
        if (progress > 1.0f) {
            progress -= 1.0f;
        } else if (progress < 0) {
            progress = 0;
        }
        if (progress == mTargetProgress) {
            return;
        }
        // If we are currently in the right position
        // we set again the last time animated so the
        // animation starts smooth from here
        if (mProgress == mTargetProgress) {
            lastTimeAnimated = SystemClock.uptimeMillis();
        }
        mTargetProgress = Math.min(progress * DEGREE_360, DEGREE_360);
        invalidate();
    }

    /**
     * Sets the determinate progress mode
     *
     * @param isLinear if the progress should increase linearly
     */
    public void setLinearProgress(final boolean isLinear) {
        linearProgress = isLinear;
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * Gets circle_blue radius.
     *
     * @return the radius of the wheel in pixels
     */
    public int getCircleRadius() {
        return circleRadius;
    }

    /**
     * Sets the radius of the wheel
     *
     * @param circleRadius the expected radius, in pixels
     */
    public void setCircleRadius(final int circleRadius) {
        this.circleRadius = circleRadius;
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * Gets bar width.
     *
     * @return the width of the spinning bar
     */
    public int getBarWidth() {
        return barWidth;
    }

    /**
     * Sets the width of the spinning bar
     *
     * @param barWidth the spinning bar width in pixels
     */
    public void setBarWidth(final int barWidth) {
        this.barWidth = barWidth;
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * Gets bar color.
     *
     * @return the color of the spinning bar
     */
    public int getBarColor() {
        return barColor;
    }

    /**
     * Sets the color of the spinning bar
     *
     * @param barColor The spinning bar color
     */
    public void setBarColor(final int barColor) {
        this.barColor = barColor;
        setupPaints();
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * Gets rim color.
     *
     * @return the color of the wheel's contour
     */
    public int getRimColor() {
        return rimColor;
    }

    /**
     * Sets the color of the wheel's contour
     *
     * @param rimColor the color for the wheel
     */
    public void setRimColor(final int rimColor) {
        this.rimColor = rimColor;
        setupPaints();
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * Gets spin speed.
     *
     * @return the base spinning speed, in full circle_blue turns per second (1.0 equals on full turn in one second),
     * this value also is applied for the smoothness when setting a progress
     */
    public float getSpinSpeed() {
        return spinSpeed / DEGREE_360;
    }

    /**
     * Sets the base spinning speed, in full circle_blue turns per second (1.0 equals
     * on full turn in one second), this value also is applied for the
     * smoothness when setting a progress
     *
     * @param spinSpeed the desired base speed in full turns per second
     */
    public void setSpinSpeed(final float spinSpeed) {
        this.spinSpeed = spinSpeed * DEGREE_360;
    }

    /**
     * Gets rim width.
     *
     * @return the width of the wheel's contour in pixels
     */
    public int getRimWidth() {
        return rimWidth;
    }

    /**
     * Sets the width of the wheel's contour
     *
     * @param rimWidth the width in pixels
     */
    public void setRimWidth(final int rimWidth) {
        this.rimWidth = rimWidth;
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * The interface Progress callback.
     */
    public interface ProgressCallback {
        /**
         * Method to call when the progress reaches a value in order to avoid
         * float precision issues, the progress is rounded to a long with two
         * decimals
         *
         * @param progress a double value between 0.00 and 1.00 both included
         */
        void onProgressUpdate(float progress);
    }

    /**
     * The type Wheel saved state.
     */
    static class WheelSavedState extends View.BaseSavedState {
        /**
         * The constant CREATOR.
         */
// required field that makes Parcelables from a Parcel
        public static final Creator<WheelSavedState> CREATOR = new Creator<WheelSavedState>() {
            public WheelSavedState createFromParcel(final Parcel in) {
                return new WheelSavedState(in);
            }

            public WheelSavedState[] newArray(final int size) {
                return new WheelSavedState[size];
            }
        };
        /**
         * The M progress.
         */
        private float mProgress;
        /**
         * The M target progress.
         */
        private float mTargetProgress;
        /**
         * The Is spinning.
         */
        private boolean isSpinning;
        /**
         * The Spin speed.
         */
        private float spinSpeed;
        /**
         * The Bar width.
         */
        private int barWidth;
        /**
         * The Bar color.
         */
        private int barColor;
        /**
         * The Rim width.
         */
        private int rimWidth;
        /**
         * The Rim color.
         */
        private int rimColor;
        /**
         * The Circle radius.
         */
        private int circleRadius;
        /**
         * The Linear progress.
         */
        private boolean linearProgress;
        /**
         * The Fill radius.
         */
        private boolean fillRadius;

        /**
         * Instantiates a new Wheel saved state.
         *
         * @param superState the super state
         */
        WheelSavedState(final Parcelable superState) {
            super(superState);
        }

        /**
         * @param in Parcel
         */
        private WheelSavedState(final Parcel in) {
            super(in);
            this.mProgress = in.readFloat();
            this.mTargetProgress = in.readFloat();
            this.isSpinning = in.readByte() != 0;
            this.spinSpeed = in.readFloat();
            this.barWidth = in.readInt();
            this.barColor = in.readInt();
            this.rimWidth = in.readInt();
            this.rimColor = in.readInt();
            this.circleRadius = in.readInt();
            this.linearProgress = in.readByte() != 0;
            this.fillRadius = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(this.mProgress);
            out.writeFloat(this.mTargetProgress);
            if (isSpinning) {
                out.writeByte((byte) 1);
            } else {
                out.writeByte((byte) 0);
            }
            out.writeFloat(this.spinSpeed);
            out.writeInt(this.barWidth);
            out.writeInt(this.barColor);
            out.writeInt(this.rimWidth);
            out.writeInt(this.rimColor);
            out.writeInt(this.circleRadius);
            if (linearProgress) {
                out.writeByte((byte) 1);
            } else {
                out.writeByte((byte) 0);
            }
            if (fillRadius) {
                out.writeByte((byte) 1);
            } else {
                out.writeByte((byte) 0);
            }
        }
    }
}