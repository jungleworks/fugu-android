package com.skeleton.mvp.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skeleton.mvp.R;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.fugudatabase.CommonData;

import java.util.Random;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;

/**
 * Created by rajatdhamija on 04/05/18.
 */

public final class Utils {
    static final int DONT_UPDATE_FLAG = -1;

    @TargetApi(JELLY_BEAN)
    static void removeOnGlobalLayoutListener(final View v, final ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (SDK_INT < JELLY_BEAN) {
            //noinspection deprecation
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @NonNull
    public static <T> T checkNotNull(@Nullable final T reference, final String message) {
        if (reference == null) {
            throw new IllegalArgumentException(message);
        }

        return reference;
    }

    public static int dpToPx(@NonNull final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    static int screenHeight(@NonNull final Activity context) {
        final Point size = new Point();

        context.getWindowManager().getDefaultDisplay().getSize(size);

        return size.y;
    }

    @NonNull
    public static Point locationOnScreen(@NonNull final View view) {
        final int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }

    @NonNull
    static Rect windowVisibleDisplayFrame(@NonNull final Activity context) {
        final Rect result = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(result);
        return result;
    }

    static Activity asActivity(@NonNull final Context context) {
        Context result = context;

        while (result instanceof ContextWrapper) {
            if (result instanceof Activity) {
                return (Activity) context;
            }

            result = ((ContextWrapper) context).getBaseContext();
        }

        throw new IllegalArgumentException("The passed Context is not an Activity.");
    }

    static void fixPopupLocation(@NonNull final PopupWindow popupWindow, @NonNull final Point desiredLocation) {
        popupWindow.getContentView().post(new Runnable() {
            @Override
            public void run() {
                final Point actualLocation = locationOnScreen(popupWindow.getContentView());

                if (!(actualLocation.x == desiredLocation.x && actualLocation.y == desiredLocation.y)) {
                    final int differenceX = actualLocation.x - desiredLocation.x;
                    final int differenceY = actualLocation.y - desiredLocation.y;

                    final int fixedOffsetX;
                    final int fixedOffsetY;

                    if (actualLocation.x > desiredLocation.x) {
                        fixedOffsetX = desiredLocation.x - differenceX;
                    } else {
                        fixedOffsetX = desiredLocation.x + differenceX;
                    }

                    if (actualLocation.y > desiredLocation.y) {
                        fixedOffsetY = desiredLocation.y - differenceY;
                    } else {
                        fixedOffsetY = desiredLocation.y + differenceY;
                    }

                    popupWindow.update(fixedOffsetX, fixedOffsetY, DONT_UPDATE_FLAG, DONT_UPDATE_FLAG);
                }
            }
        });
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        try{
            final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);

            final int color = Color.RED;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            bitmap.recycle();
            return output;

        }catch (Exception e){
            return bitmap;
        }

    }

    private Utils() {
        throw new AssertionError("No instances.");
    }

    public static String getDomain() {
        return CommonData.getDomain();
    }

    public static int getRandomNumberBetween(int max, int min) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }


}
