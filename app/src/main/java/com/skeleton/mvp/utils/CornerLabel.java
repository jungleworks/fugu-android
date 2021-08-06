package com.skeleton.mvp.utils;

/**
 * Created by rajatdhamija on 09/07/18.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import androidx.annotation.IntDef;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.skeleton.mvp.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Corner label.
 *
 * @author Junhao Zhou 2016/09/05
 */
public class CornerLabel extends View {

    private static final int TOP_LEFT = 0;

    private static final int TOP_RIGHT = 1;

    private static final int BOTTOM_LEFT = 2;

    private static final int BOTTOM_RIGHT = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT})
    public @interface Position {
    }

    private int mPosition;

    private TextPaint mTextPint;

    private Rect mTextRect;

    private Paint mBackgroundPaint;

    private Path mTrianglePath;

    private int mBackground;

    private int mLabelColor;

    private int mLabelSize;

    private String mLabel;

    public CornerLabel(Context context) {
        this(context, null);
    }

    public CornerLabel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CornerLabel);
        mPosition = array.getInt(R.styleable.CornerLabel_tl_position, TOP_LEFT);
        mLabel = array.getString(R.styleable.CornerLabel_tl_label);
        int defaultColor = Color.parseColor("#2196F3");
        int defaultSize = (int) sp2px(14);
        mLabelSize = array.getDimensionPixelSize(R.styleable.CornerLabel_tl_labelSize, defaultSize);
        mLabelColor = array.getColor(R.styleable.CornerLabel_tl_labelColor, Color.WHITE);
        mBackground = array.getColor(R.styleable.CornerLabel_tl_background, defaultColor);
        array.recycle();

        mTextPint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextRect = new Rect();
        mBackgroundPaint = new Paint();
        mTrianglePath = new Path();

        updateLabel();
    }

    public int getDirection() {
        return mPosition;
    }

    /**
     * update the label position
     *
     * @param position position,{@link #TOP_LEFT},{@link #TOP_RIGHT},{@link #BOTTOM_LEFT},{@link #BOTTOM_RIGHT}
     */
    public void setDirection(@Position int position) {
        this.mPosition = position;
        updateLabel();
        invalidate();
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
        updateLabel();
        invalidate();
    }

    public int getLabelBackground() {
        return mBackground;
    }

    public void setLabelBackground(int background) {
        this.mBackground = background;
        updateLabel();
        invalidate();
    }

    public int getLabelColor() {
        return mLabelColor;
    }

    public void setLabelColor(int labelColor) {
        this.mLabelColor = labelColor;
        updateLabel();
        invalidate();
    }

    public int getLabelSize() {
        return mLabelSize;
    }

    public void setLabelSize(int labelSize) {
        this.mLabelSize = labelSize;
        updateLabel();
        invalidate();
    }

    private void updateLabel() {
        mLabel = mLabel == null ? "" : mLabel;
        mTextPint.setTextSize(mLabelSize);
        mTextPint.setColor(mLabelColor);
        mTextPint.getTextBounds(mLabel, 0, mLabel.length(), mTextRect);

        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackground);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width;
        int height;

        int textWidth = mTextRect.width() + getPaddingLeft() + getPaddingRight();
        int textHeight = mTextRect.height() + getPaddingTop() + getPaddingBottom();
        width = (int) ((textWidth + 2 * textHeight) / Math.sqrt(2));
        height = width;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int textWidth = mTextRect.width() + getPaddingLeft() + getPaddingRight();
        int textHeight = mTextRect.height() + getPaddingTop() + getPaddingBottom();
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        Paint.FontMetrics metrics = mTextPint.getFontMetrics();
        mTrianglePath.reset();
        switch (mPosition) {
            case TOP_LEFT:
                mTrianglePath.moveTo(0, 0);
                mTrianglePath.lineTo(width, 0);
                mTrianglePath.lineTo(0, height);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mBackgroundPaint);
                canvas.rotate(-45, width / 2, height / 2);
                canvas.drawText(mLabel, (width - textWidth) / 2f + getPaddingLeft(), height / 2f - getPaddingBottom(), mTextPint);
                break;
            case TOP_RIGHT:
                mTrianglePath.moveTo(width, 0);
                mTrianglePath.lineTo(0, 0);
                mTrianglePath.lineTo(width, height);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mBackgroundPaint);
                canvas.rotate(45, width / 2, height / 2);
                canvas.drawText(mLabel, (width - textWidth) / 2f + getPaddingLeft(), height / 2f - getPaddingBottom(), mTextPint);
                break;
            case BOTTOM_LEFT:
                mTrianglePath.moveTo(0, height);
                mTrianglePath.lineTo(0, 0);
                mTrianglePath.lineTo(width, height);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mBackgroundPaint);
                canvas.rotate(45, width / 2, height / 2);
                canvas.drawText(mLabel, (width - textWidth) / 2f + getPaddingLeft(), height / 2f + textHeight - getPaddingBottom(), mTextPint);
                break;
            case BOTTOM_RIGHT:
                mTrianglePath.moveTo(width, height);
                mTrianglePath.lineTo(0, height);
                mTrianglePath.lineTo(width, 0);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mBackgroundPaint);
                canvas.rotate(-45, width / 2, height / 2);
                canvas.drawText(mLabel, (width - textWidth) / 2f + getPaddingLeft(), height / 2f + textHeight - getPaddingBottom(), mTextPint);
                break;
            default:
                break;
        }
    }

    public float sp2px(float spValue) {
        final float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return spValue * scale;
    }
}