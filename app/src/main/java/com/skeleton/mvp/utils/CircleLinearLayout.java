package com.skeleton.mvp.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.skeleton.mvp.R;

/**
 * Created by rajatdhamija on 10/07/18.
 */

public class CircleLinearLayout extends LinearLayout {

    private Bitmap maskBitmap;
    private Paint paint, maskPaint;
    private float radius;

    public CircleLinearLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CircleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CircleLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        if(attrs != null)
        {
            TypedArray a = context.getTheme().obtainStyledAttributes( attrs, R.styleable.CircleLinearLayout, defStyle , 0);

            try {
                radius = a.getDimension(R.styleable.CircleLinearLayout_diameter, getResources().getDimension(R.dimen.messaging_profile_pic_size)) / 2;
            } finally {
                a.recycle();
            }
        }

        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap offscreenBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas offscreenCanvas = new Canvas(offscreenBitmap);

        super.draw(offscreenCanvas);

        if (maskBitmap == null) {
            maskBitmap = createMask(canvas.getWidth(), canvas.getHeight());
        }

        offscreenCanvas.drawBitmap(maskBitmap, 0f, 0f, maskPaint);
        canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint);
    }

    private Bitmap createMask(int width, int height) {
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(mask);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);

        canvas.drawRect(0, 0, width, height, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        canvas.drawCircle(radius , radius , radius , paint);

        return mask;
    }
}
