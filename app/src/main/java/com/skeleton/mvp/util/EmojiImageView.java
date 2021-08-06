package com.skeleton.mvp.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;


import com.vanniktech.emoji.emoji.Emoji;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * Created by rajatdhamija on 04/05/18.
 */

@RestrictTo(LIBRARY)
public final class EmojiImageView extends AppCompatImageView {
    private static final int VARIANT_INDICATOR_PART_AMOUNT = 6;
    private static final int VARIANT_INDICATOR_PART = 5;

    Emoji currentEmoji;

    com.skeleton.mvp.util.OnEmojiClickListener clickListener;
    OnEMojiLongClickListener longClickListener;

    private final Paint variantIndicatorPaint = new Paint();
    private final Path variantIndicatorPath = new Path();

    private final Point variantIndicatorTop = new Point();
    private final Point variantIndicatorBottomRight = new Point();
    private final Point variantIndicatorBottomLeft = new Point();

    private ImageLoadingTask imageLoadingTask;

    private boolean hasVariants;

    public EmojiImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        variantIndicatorPaint.setColor(ContextCompat.getColor(context, com.vanniktech.emoji.R.color.emoji_divider));
        variantIndicatorPaint.setStyle(Paint.Style.FILL);
        variantIndicatorPaint.setAntiAlias(true);
    }

    @Override
    public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int measuredWidth = getMeasuredWidth();
        //noinspection SuspiciousNameCombination
        setMeasuredDimension(measuredWidth, measuredWidth);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        variantIndicatorTop.x = w;
        variantIndicatorTop.y = h / VARIANT_INDICATOR_PART_AMOUNT * VARIANT_INDICATOR_PART;
        variantIndicatorBottomRight.x = w;
        variantIndicatorBottomRight.y = h;
        variantIndicatorBottomLeft.x = w / VARIANT_INDICATOR_PART_AMOUNT * VARIANT_INDICATOR_PART;
        variantIndicatorBottomLeft.y = h;

        variantIndicatorPath.rewind();
        variantIndicatorPath.moveTo(variantIndicatorTop.x, variantIndicatorTop.y);
        variantIndicatorPath.lineTo(variantIndicatorBottomRight.x, variantIndicatorBottomRight.y);
        variantIndicatorPath.lineTo(variantIndicatorBottomLeft.x, variantIndicatorBottomLeft.y);
        variantIndicatorPath.close();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (hasVariants) {
            canvas.drawPath(variantIndicatorPath, variantIndicatorPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (imageLoadingTask != null) {
            imageLoadingTask.cancel(true);
            imageLoadingTask = null;
        }
    }

    void setEmoji(@NonNull final Emoji emoji) {
        if (!emoji.equals(currentEmoji)) {
            setImageDrawable(null);

            currentEmoji = emoji;
            hasVariants = emoji.getBase().hasVariants();

            if (imageLoadingTask != null) {
                imageLoadingTask.cancel(true);
            }

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (clickListener != null) {
                        clickListener.onEmojiClick(EmojiImageView.this, currentEmoji);
                    }
                }
            });

            setOnLongClickListener(hasVariants ? new OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    longClickListener.onEmojiLongClick(EmojiImageView.this, currentEmoji);

                    return true;
                }
            } : null);

            imageLoadingTask = new ImageLoadingTask(this);
            imageLoadingTask.execute(emoji.getResource());
        }
    }

    /**
     * Updates the emoji image directly. This should be called only for updating the variant
     * displayed (of the same base emoji), since it does not run asynchronously and does not update
     * the internal listeners.
     *
     * @param emoji The new emoji variant to show.
     */
    void updateEmoji(@NonNull final Emoji emoji) {
        if (!emoji.equals(currentEmoji)) {
            currentEmoji = emoji;

            setImageResource(emoji.getResource());
        }
    }

    void setOnEmojiClickListener(@Nullable final com.skeleton.mvp.util.OnEmojiClickListener listener) {
        this.clickListener = listener;
    }

    void setOnEmojiLongClickListener(@Nullable final OnEMojiLongClickListener listener) {
        this.longClickListener = listener;
    }
}
