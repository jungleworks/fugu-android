package com.skeleton.mvp.ui.customview;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.skeleton.mvp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.padding;
/**
 * EditText in Material Design
 * <p/>
 * author:rengwuxian
 * <p/>
 */
public class MaterialEditText extends AppCompatEditText {
    /**
     * The interface Floating label type.
     */
    @IntDef({FLOATING_LABEL_NONE, FLOATING_LABEL_NORMAL, FLOATING_LABEL_HIGHLIGHT})
    public @interface FloatingLabelType {
    }
    /**
     * The constant FLOATING_LABEL_NONE.
     */
    public static final int FLOATING_LABEL_NONE = 0;
    /**
     * The constant FLOATING_LABEL_NORMAL.
     */
    public static final int FLOATING_LABEL_NORMAL = 1;
    /**
     * The constant FLOATING_LABEL_HIGHLIGHT.
     */
    public static final int FLOATING_LABEL_HIGHLIGHT = 2;
    private static final int ICON_SIZE = 32;
    private static final int ICON_OUTER_WIDTH = 48;
    private static final int ICON_OUTER_HEIGHT = 32;
    private static final int ICON_PADDING = 16;
    private static final int LEFT_INDEX = 1;
    private static final int TOP_INDEX = 2;
    private static final int RIGHT_INDEX = 3;
    private static final int BOTTOM_INDEX = 4;
    private static final int HEX_WHITE = 0x00ffffff;
    private static final int HEX_BLACK_44 = 0x44000000;
    private static final int HEX_BLACK_8A = 0x8a000000;
    private static final int HEX_BLACK_4C = 0x4c000000;
    private static final int HEX_BLACK_42 = 0x42000000;
    private static final int HEX_BLACK_DF = 0xdf000000;
    private static final int HEX_BLACK_1E = 0x1E000000;
    private static final int HEX_BLACK = 0xff000000;
    private static final int VALUE_THREE = 3;
    private static final int VALUE_FOUR = 4;
    private static final int VALUE_FIVE = 5;
    private static final int VALUE_NINE = 9;
    private static final int VALUE_130 = 130;
    private static final int INT_VALUE_0XFF = 0xff;
    private static final int FLOATING_LABEL_ANIM_DURATION = 300;
    private static final int ALPHA_OPAQUE = 255;
    private static final float FLOAT_POINT_74 = 0.74f;
    private static final float FLOAT_POINT_26 = 0.26f;
    private static final float FLOAT_POINT_241 = 0.241f;
    private static final float FLOAT_POINT_691 = 0.691f;
    private static final float FLOAT_POINT_068 = 0.068f;
    /**
     * the spacing between the main text and the inner top padding.
     */
    private int extraPaddingTop;
    /**
     * the spacing between the main text and the inner bottom padding.
     */
    private int extraPaddingBottom;
    /**
     * the extra spacing between the main text and the left, actually for the left icon.
     */
    private int extraPaddingLeft;
    /**
     * the extra spacing between the main text and the right, actually for the right icon.
     */
    private int extraPaddingRight;
    /**
     * the floating label's text size.
     */
    private int floatingLabelTextSize;
    /**
     * the floating label's text color.
     */
    private int floatingLabelTextColor;
    /**
     * the bottom texts' size.
     */
    private int bottomTextSize;
    /**
     * the spacing between the main text and the floating label.
     */
    private int floatingLabelPadding;
    /**
     * the spacing between the main text and the bottom components (bottom ellipsis, helper/error text, characters counter).
     */
    private int bottomSpacing;
    /**
     * whether the floating label should be shown. default is false.
     */
    private boolean floatingLabelEnabled;
    /**
     * whether to highlight the floating label's text color when focused (with the main color). default is true.
     */
    private boolean highlightFloatingLabel;
    /**
     * the base color of the line and the texts. default is black.
     */
    private int baseColor;
    /**
     * inner top padding
     */
    private int innerPaddingTop;
    /**
     * inner bottom padding
     */
    private int innerPaddingBottom;
    /**
     * inner left padding
     */
    private int innerPaddingLeft;
    /**
     * inner right padding
     */
    private int innerPaddingRight;
    /**
     * the underline's highlight color, and the highlight color of the floating label
     * if app:highlightFloatingLabel is set true in the xml.
     * default is black(when app:darkTheme is false) or white(when app:darkTheme is true)
     */
    private int primaryColor;
    /**
     * the color for when something is wrong.(e.g. exceeding max characters)
     */
    private int errorColor;
    /**
     * min characters count limit. 0 means no limit. default is 0. NOTE: the character counter will increase the View's height.
     */
    private int minCharacters;
    /**
     * max characters count limit. 0 means no limit. default is 0. NOTE: the character counter will increase the View's height.
     */
    private int maxCharacters;
    /**
     * whether to show the bottom ellipsis in singleLine mode. default is false. NOTE: the bottom ellipsis will increase the View's height.
     */
    private boolean singleLineEllipsis;
    /**
     * Always show the floating label, instead of animating it in/out. False by default.
     */
    private boolean floatingLabelAlwaysShown;
    /**
     * Always show the helper text, no matter if the edit text is focused. False by default.
     */
    private boolean helperTextAlwaysShown;
    /**
     * bottom ellipsis's height
     */
    private int bottomEllipsisSize;
    /**
     * min bottom lines count.
     */
    private int minBottomLines;
    /**
     * reserved bottom text lines count, no matter if there is some helper/error text.
     */
    private int minBottomTextLines;
    /**
     * real-time bottom lines count. used for bottom extending/collapsing animation.
     */
    private float currentBottomLines;
    /**
     * bottom lines count.
     */
    private float bottomLines;
    /**
     * Helper text at the bottom
     */
    private String helperText;
    /**
     * Helper text color
     */
    private int helperTextColor = -1;
    /**
     * error text for manually invoked {@link #setError(CharSequence)}
     */
    private String tempErrorText;
    /**
     * animation fraction of the floating label (0 as totally hidden).
     */
    private float floatingLabelFraction;
    /**
     * whether the floating label is being shown.
     */
    private boolean floatingLabelShown;
    /**
     * the floating label's focusFraction
     */
    private float focusFraction;
    /**
     * The font used for the accent texts (floating label, error/helper text, character counter, etc.)
     */
    private Typeface accentTypeface;
    /**
     * The font used on the view (EditText content)
     */
    private Typeface typeface;
    /**
     * Text for the floatLabel if different from the hint
     */
    private CharSequence floatingLabelText;
    /**
     * Whether or not to show the underline. Shown by default
     */
    private boolean hideUnderline;
    /**
     * Underline's color
     */
    private int underlineColor;
    /**
     * Whether to validate as soon as the text has changed. False by default
     */
    private boolean autoValidate;
    /**
     * Whether the characters count is valid
     */
    private boolean charactersCountValid;
    /**
     * Whether use animation to show/hide the floating label.
     */
    private boolean floatingLabelAnimating;
    /**
     * Left Icon
     */
    private Bitmap[] iconLeftBitmaps;
    /**
     * Right Icon
     */
    private Bitmap[] iconRightBitmaps;
    private int iconSize;
    private int iconOuterWidth;
    private int iconOuterHeight;
    private int iconPadding;
    private ColorStateList textColorStateList;
    private ColorStateList textColorHintStateList;
    private ArgbEvaluator focusEvaluator = new ArgbEvaluator();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout textLayout;
    private ObjectAnimator labelAnimator;
    private ObjectAnimator labelFocusAnimator;
    private ObjectAnimator bottomLinesAnimator;
    private OnFocusChangeListener innerFocusChangeListener;
    private OnFocusChangeListener outerFocusChangeListener;
    private List<METValidator> validators;
    /**
     * Instantiates a new Material edit text.
     *
     * @param context the context
     */
    public MaterialEditText(final Context context) {
        super(context);
        init(context, null);
    }
    /**
     * Instantiates a new Material edit text.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public MaterialEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    /**
     * Instantiates a new Material edit text.
     *
     * @param context the context
     * @param attrs   the attrs
     * @param style   the style
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialEditText(final Context context, final AttributeSet attrs, final int style) {
        super(context, attrs, style);
        init(context, attrs);
    }
    /**
     * @param context the context
     * @param attrs   the attrs
     */
    private void init(final Context context, final AttributeSet attrs) {
        iconSize = getPixel(ICON_SIZE);
        iconOuterWidth = getPixel(ICON_OUTER_WIDTH);
        iconOuterHeight = getPixel(ICON_OUTER_HEIGHT);
        bottomSpacing = getResources().getDimensionPixelSize(R.dimen.inner_components_spacing);
        bottomEllipsisSize = getResources().getDimensionPixelSize(R.dimen.bottom_ellipsis_height);
        // default baseColor is black
        int defaultBaseColor = Color.BLACK;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText);
        textColorStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColor);
        textColorHintStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColorHint);
        baseColor = typedArray.getColor(R.styleable.MaterialEditText_met_baseColor, defaultBaseColor);
        // retrieve the default primaryColor
        int defaultPrimaryColor;
        TypedValue primaryColorTypedValue = new TypedValue();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryColorTypedValue, true);
                defaultPrimaryColor = primaryColorTypedValue.data;
            } else {
                throw new RuntimeException("SDK_INT less than LOLLIPOP");
            }
        } catch (Exception e) {
            try {
                int colorPrimaryId = getResources().getIdentifier("colorPrimary", "attr", getContext().getPackageName());
                if (colorPrimaryId != 0) {
                    context.getTheme().resolveAttribute(colorPrimaryId, primaryColorTypedValue, true);
                    defaultPrimaryColor = primaryColorTypedValue.data;
                } else {
                    throw new RuntimeException("colorPrimary not found");
                }
            } catch (Exception e1) {
                defaultPrimaryColor = baseColor;
            }
        }
        primaryColor = typedArray.getColor(R.styleable.MaterialEditText_met_primaryColor, defaultPrimaryColor);
        setFloatingLabelInternal(typedArray.getInt(R.styleable.MaterialEditText_met_floatingLabel, 0));
        errorColor = typedArray.getColor(R.styleable.MaterialEditText_met_errorColor, Color.parseColor("#e7492E"));
        minCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_minCharacters, 0);
        maxCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_maxCharacters, 0);
        singleLineEllipsis = typedArray.getBoolean(R.styleable.MaterialEditText_met_singleLineEllipsis, false);
        helperText = typedArray.getString(R.styleable.MaterialEditText_met_helperText);
        helperTextColor = typedArray.getColor(R.styleable.MaterialEditText_met_helperTextColor, -1);
        minBottomTextLines = typedArray.getInt(R.styleable.MaterialEditText_met_minBottomTextLines, 0);
        String fontPathForAccent = typedArray.getString(R.styleable.MaterialEditText_met_accentTypeface);
        if (fontPathForAccent != null && !isInEditMode()) {
            accentTypeface = getCustomTypeface(fontPathForAccent);
            textPaint.setTypeface(accentTypeface);
        }
        String fontPathForView = typedArray.getString(R.styleable.MaterialEditText_met_typeface);
        if (fontPathForView != null && !isInEditMode()) {
            typeface = getCustomTypeface(fontPathForView);
            setTypeface(typeface);
        }
        floatingLabelText = typedArray.getString(R.styleable.MaterialEditText_met_floatingLabelText);
        if (floatingLabelText == null) {
            floatingLabelText = getHint();
        }
        floatingLabelPadding = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_floatingLabelPadding, bottomSpacing);
        floatingLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_floatingLabelTextSize,
                getResources().getDimensionPixelSize(R.dimen.floating_label_text_size));
        floatingLabelTextColor = typedArray.getColor(R.styleable.MaterialEditText_met_floatingLabelTextColor, -1);
        floatingLabelAnimating = typedArray.getBoolean(R.styleable.MaterialEditText_met_floatingLabelAnimating, true);
        bottomTextSize = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_bottomTextSize,
                getResources().getDimensionPixelSize(R.dimen.bottom_text_size));
        hideUnderline = typedArray.getBoolean(R.styleable.MaterialEditText_met_hideUnderline, false);
        underlineColor = typedArray.getColor(R.styleable.MaterialEditText_met_underlineColor, -1);
        autoValidate = typedArray.getBoolean(R.styleable.MaterialEditText_met_autoValidate, false);
        iconLeftBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconLeft, -1));
        iconRightBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconRight, -1));
        iconPadding = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_iconPadding, getPixel(ICON_PADDING));
        floatingLabelAlwaysShown = typedArray.getBoolean(R.styleable.MaterialEditText_met_floatingLabelAlwaysShown, false);
        helperTextAlwaysShown = typedArray.getBoolean(R.styleable.MaterialEditText_met_helperTextAlwaysShown, false);
        typedArray.recycle();
        int[] paddings = new int[]{
                // 0
                padding,
                // 1
                android.R.attr.paddingLeft,
                // 2
                android.R.attr.paddingTop,
                // 3
                android.R.attr.paddingRight,
                // 4
                android.R.attr.paddingBottom
        };
        TypedArray paddingsTypedArray = context.obtainStyledAttributes(attrs, paddings);
        int padding = paddingsTypedArray.getDimensionPixelSize(0, 0);
        innerPaddingLeft = paddingsTypedArray.getDimensionPixelSize(LEFT_INDEX, padding);
        innerPaddingTop = paddingsTypedArray.getDimensionPixelSize(TOP_INDEX, padding);
        innerPaddingRight = paddingsTypedArray.getDimensionPixelSize(RIGHT_INDEX, padding);
        innerPaddingBottom = paddingsTypedArray.getDimensionPixelSize(BOTTOM_INDEX, padding);
        paddingsTypedArray.recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }
        if (singleLineEllipsis) {
            TransformationMethod transformationMethod = getTransformationMethod();
            setSingleLine();
            setTransformationMethod(transformationMethod);
        }
        initMinBottomLines();
        initPadding();
        initText();
        initFloatingLabel();
        initTextWatcher();
        checkCharactersCount();
    }
    /**
     *
     */
    private void initText() {
        if (!TextUtils.isEmpty(getText())) {
            CharSequence text = getText();
            setText(null);
            resetHintTextColor();
            setText(text);
            setSelection(text.length());
            floatingLabelFraction = 1;
            floatingLabelShown = true;
        } else {
            resetHintTextColor();
        }
        resetTextColor();
    }
    /**
     *
     */
    private void initTextWatcher() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }
            @Override
            public void afterTextChanged(final Editable s) {
                checkCharactersCount();
                if (autoValidate) {
                    validate();
                } else {
                    setError(null);
                }
                postInvalidate();
            }
        });
    }
    /**
     * @param fontPath the path of font
     * @return Typeface
     */
    private Typeface getCustomTypeface(@NonNull final String fontPath) {
        return Typeface.createFromAsset(getContext().getAssets(), fontPath);
    }
    /**
     * Sets icon left.
     *
     * @param res the res
     */
    public void setIconLeft(@DrawableRes final int res) {
        iconLeftBitmaps = generateIconBitmaps(res);
        initPadding();
    }
    /**
     * Sets icon left.
     *
     * @param bitmap the bitmap
     */
    public void setIconLeft(final Bitmap bitmap) {
        iconLeftBitmaps = generateIconBitmaps(bitmap);
        initPadding();
    }
    /**
     * Sets icon right.
     *
     * @param res the res
     */
    public void setIconRight(@DrawableRes final int res) {
        iconRightBitmaps = generateIconBitmaps(res);
        initPadding();
    }
    /**
     * Sets icon right.
     *
     * @param bitmap the bitmap
     */
    public void setIconRight(final Bitmap bitmap) {
        iconRightBitmaps = generateIconBitmaps(bitmap);
        initPadding();
    }
    /**
     * @param origin the origin
     * @return array of bitmap
     */
    private Bitmap[] generateIconBitmaps(@DrawableRes final int origin) {
        if (origin == -1) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), origin, options);
        int size = Math.max(options.outWidth, options.outHeight);
        options.inSampleSize = size > iconSize ? size / iconSize : 1;
        options.inJustDecodeBounds = false;
        return generateIconBitmaps(BitmapFactory.decodeResource(getResources(), origin, options));
    }
    /**
     * @param mOrigin the origin
     * @return array of bitmap
     */
    private Bitmap[] generateIconBitmaps(final Bitmap mOrigin) {
        Bitmap origin = mOrigin;
        if (origin == null) {
            return null;
        }
        Bitmap[] iconBitmaps = new Bitmap[VALUE_FOUR];
        origin = scaleIcon(origin);
        iconBitmaps[0] = origin.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(iconBitmaps[0]);
        canvas.drawColor(baseColor & HEX_WHITE | (isLight(baseColor) ? HEX_BLACK : HEX_BLACK_8A), PorterDuff.Mode.SRC_IN);
        iconBitmaps[1] = origin.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(iconBitmaps[1]);
        canvas.drawColor(primaryColor, PorterDuff.Mode.SRC_IN);
        iconBitmaps[2] = origin.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(iconBitmaps[2]);
        canvas.drawColor(baseColor & HEX_WHITE | (isLight(baseColor) ? HEX_BLACK_4C : HEX_BLACK_42), PorterDuff.Mode.SRC_IN);
        iconBitmaps[VALUE_THREE] = origin.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(iconBitmaps[VALUE_THREE]);
        canvas.drawColor(errorColor, PorterDuff.Mode.SRC_IN);
        return iconBitmaps;
    }
    /**
     * @param origin the origin
     * @return array of bitmap
     */
    private Bitmap scaleIcon(final Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int size = Math.max(width, height);
        if (size == iconSize) {
            return origin;
        } else if (size > iconSize) {
            int scaledWidth;
            int scaledHeight;
            if (width > iconSize) {
                scaledWidth = iconSize;
                scaledHeight = (int) (iconSize * ((float) height / width));
            } else {
                scaledHeight = iconSize;
                scaledWidth = (int) (iconSize * ((float) width / height));
            }
            return Bitmap.createScaledBitmap(origin, scaledWidth, scaledHeight, false);
        } else {
            return origin;
        }
    }
    /**
     * Gets floating label fraction.
     *
     * @return the floating label fraction
     */
    public float getFloatingLabelFraction() {
        return floatingLabelFraction;
    }
    /**
     * Sets floating label fraction.
     *
     * @param floatingLabelFraction the floating label fraction
     */
    public void setFloatingLabelFraction(final float floatingLabelFraction) {
        this.floatingLabelFraction = floatingLabelFraction;
        invalidate();
    }
    /**
     * Gets focus fraction.
     *
     * @return the focus fraction
     */
    public float getFocusFraction() {
        return focusFraction;
    }
    /**
     * Sets focus fraction.
     *
     * @param focusFraction the focus fraction
     */
    public void setFocusFraction(final float focusFraction) {
        this.focusFraction = focusFraction;
        invalidate();
    }
    /**
     * Gets current bottom lines.
     *
     * @return the current bottom lines
     */
    public float getCurrentBottomLines() {
        return currentBottomLines;
    }
    /**
     * Sets current bottom lines.
     *
     * @param currentBottomLines the current bottom lines
     */
    public void setCurrentBottomLines(final float currentBottomLines) {
        this.currentBottomLines = currentBottomLines;
        initPadding();
    }
    /**
     * Is floating label always shown boolean.
     *
     * @return the boolean
     */
    public boolean isFloatingLabelAlwaysShown() {
        return floatingLabelAlwaysShown;
    }
    /**
     * Sets floating label always shown.
     *
     * @param floatingLabelAlwaysShown the floating label always shown
     */
    public void setFloatingLabelAlwaysShown(final boolean floatingLabelAlwaysShown) {
        this.floatingLabelAlwaysShown = floatingLabelAlwaysShown;
        invalidate();
    }
    /**
     * Is helper text always shown boolean.
     *
     * @return the boolean
     */
    public boolean isHelperTextAlwaysShown() {
        return helperTextAlwaysShown;
    }
    /**
     * Sets helper text always shown.
     *
     * @param helperTextAlwaysShown the helper text always shown
     */
    public void setHelperTextAlwaysShown(final boolean helperTextAlwaysShown) {
        this.helperTextAlwaysShown = helperTextAlwaysShown;
        invalidate();
    }
    /**
     * Gets accent typeface.
     *
     * @return the accent typeface
     */
    @Nullable
    public Typeface getAccentTypeface() {
        return accentTypeface;
    }
    /**
     * Set typeface used for the accent texts (floating label, error/helper text, character counter, etc.)
     *
     * @param accentTypeface the accent typeface
     */
    public void setAccentTypeface(final Typeface accentTypeface) {
        this.accentTypeface = accentTypeface;
        this.textPaint.setTypeface(accentTypeface);
        postInvalidate();
    }
    /**
     * Is hide underline boolean.
     *
     * @return the boolean
     */
    public boolean isHideUnderline() {
        return hideUnderline;
    }
    /**
     * Set whether or not to hide the underline (shown by default).
     * <p/>
     * The positions of text below will be adjusted accordingly (error/helper text, character counter, ellipses, etc.)
     * <p/>
     * NOTE: You probably don't want to hide this
     * if you have any subtext features of this enabled, as it can look weird to not have a dividing line between them.
     *
     * @param hideUnderline the hide underline
     */
    public void setHideUnderline(final boolean hideUnderline) {
        this.hideUnderline = hideUnderline;
        initPadding();
        postInvalidate();
    }
    /**
     * get the color of the underline for normal state
     *
     * @return the underline color
     */
    public int getUnderlineColor() {
        return underlineColor;
    }
    /**
     * Set the color of the underline for normal state
     *
     * @param color the color
     */
    public void setUnderlineColor(final int color) {
        this.underlineColor = color;
        postInvalidate();
    }
    /**
     * Gets floating label text.
     *
     * @return the floating label text
     */
    public CharSequence getFloatingLabelText() {
        return floatingLabelText;
    }
    /**
     * Set the floating label text.
     * <p/>
     * Pass null to force fallback to use hint's value.
     *
     * @param floatingLabelText the floating label text
     */
    public void setFloatingLabelText(@Nullable final CharSequence floatingLabelText) {
        this.floatingLabelText = floatingLabelText == null ? getHint() : floatingLabelText;
        postInvalidate();
    }
    /**
     * Gets floating label text size.
     *
     * @return the floating label text size
     */
    public int getFloatingLabelTextSize() {
        return floatingLabelTextSize;
    }
    /**
     * Sets floating label text size.
     *
     * @param size the size
     */
    public void setFloatingLabelTextSize(final int size) {
        floatingLabelTextSize = size;
        initPadding();
    }
    /**
     * Gets floating label text color.
     *
     * @return the floating label text color
     */
    public int getFloatingLabelTextColor() {
        return floatingLabelTextColor;
    }
    /**
     * Sets floating label text color.
     *
     * @param color the color
     */
    public void setFloatingLabelTextColor(final int color) {
        this.floatingLabelTextColor = color;
        postInvalidate();
    }
    /**
     * Gets bottom text size.
     *
     * @return the bottom text size
     */
    public int getBottomTextSize() {
        return bottomTextSize;
    }
    /**
     * Sets bottom text size.
     *
     * @param size the size
     */
    public void setBottomTextSize(final int size) {
        bottomTextSize = size;
        initPadding();
    }
    /**
     * @param dp value that need to convert in to pixel
     * @return pixel value
     */
    private int getPixel(final int dp) {
        return dp2px(getContext(), dp);
    }
    /**
     *
     */
    private void initPadding() {
        extraPaddingTop = floatingLabelEnabled ? floatingLabelTextSize + floatingLabelPadding : floatingLabelPadding;
        textPaint.setTextSize(bottomTextSize);
        Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
        extraPaddingBottom = (int) ((textMetrics.descent - textMetrics.ascent) * currentBottomLines)
                + (hideUnderline ? bottomSpacing : bottomSpacing * 2);
        extraPaddingLeft = iconLeftBitmaps == null ? 0 : (iconOuterWidth + iconPadding);
        extraPaddingRight = iconRightBitmaps == null ? 0 : (iconOuterWidth + iconPadding);
        correctPaddings();
    }
    /**
     * calculate {@link #minBottomLines}
     */
    private void initMinBottomLines() {
        boolean extendBottom = minCharacters > 0 || maxCharacters > 0 || singleLineEllipsis || tempErrorText != null || helperText != null;
        minBottomLines = minBottomTextLines > 0 ? minBottomTextLines : extendBottom ? 1 : 0;
        currentBottomLines = minBottomLines;
    }
    /**
     * use {@link #setPaddings(int, int, int, int)} instead, or the paddingTop and the paddingBottom may be set incorrectly.
     *
     * @param left   the left
     * @param top    the top
     * @param right  the right
     * @param bottom the bottom
     */
    @Deprecated
    @Override
    public final void setPadding(final int left, final int top, final int right, final int bottom) {
        super.setPadding(left, top, right, bottom);
    }
    /**
     * Use this method instead of {@link #setPadding(int, int, int, int)} to automatically set the paddingTop and the paddingBottom correctly.
     *
     * @param left   the left
     * @param top    the top
     * @param right  the right
     * @param bottom the bottom
     */
    public void setPaddings(final int left, final int top, final int right, final int bottom) {
        innerPaddingTop = top;
        innerPaddingBottom = bottom;
        innerPaddingLeft = left;
        innerPaddingRight = right;
        correctPaddings();
    }
    /**
     * Set paddings to the correct values
     */
    private void correctPaddings() {
        int buttonsWidthLeft = 0, buttonsWidthRight = 0;
        int buttonsWidth = 0;
        if (isRTL()) {
            buttonsWidthLeft = buttonsWidth;
        } else {
            buttonsWidthRight = buttonsWidth;
        }
        super.setPadding(innerPaddingLeft + extraPaddingLeft + buttonsWidthLeft,
                innerPaddingTop + extraPaddingTop,
                innerPaddingRight + extraPaddingRight + buttonsWidthRight,
                innerPaddingBottom + extraPaddingBottom);
    }
    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            adjustBottomLines();
        }
    }
    /**
     * @return True, if adjustments were made that require the view to be invalidated.
     */
    private boolean adjustBottomLines() {
        // Bail out if we have a zero width; lines will be adjusted during next layout.
        if (getWidth() == 0) {
            return false;
        }
        int destBottomLines;
        textPaint.setTextSize(bottomTextSize);
        if (tempErrorText != null || helperText != null) {
            Layout.Alignment alignment = (getGravity() & Gravity.RIGHT) == Gravity.RIGHT || isRTL()
                    ? Layout.Alignment.ALIGN_OPPOSITE : (getGravity() & Gravity.LEFT) == Gravity.LEFT
                    ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_CENTER;
            textLayout = new StaticLayout(tempErrorText != null ? tempErrorText : helperText,
                    textPaint,
                    getWidth() - getBottomTextLeftOffset() - getBottomTextRightOffset() - getPaddingLeft() - getPaddingRight(),
                    alignment,
                    1.0f,
                    0.0f,
                    true);
            destBottomLines = Math.max(textLayout.getLineCount(), minBottomTextLines);
        } else {
            destBottomLines = minBottomLines;
        }
        if (bottomLines != destBottomLines) {
            getBottomLinesAnimator(destBottomLines).start();
        }
        bottomLines = destBottomLines;
        return true;
    }
    /**
     * get inner top padding, not the real paddingTop
     *
     * @return the inner padding top
     */
    public int getInnerPaddingTop() {
        return innerPaddingTop;
    }
    /**
     * get inner bottom padding, not the real paddingBottom
     *
     * @return the inner padding bottom
     */
    public int getInnerPaddingBottom() {
        return innerPaddingBottom;
    }
    /**
     * get inner left padding, not the real paddingLeft
     *
     * @return the inner padding left
     */
    public int getInnerPaddingLeft() {
        return innerPaddingLeft;
    }
    /**
     * get inner right padding, not the real paddingRight
     *
     * @return the inner padding right
     */
    public int getInnerPaddingRight() {
        return innerPaddingRight;
    }
    /**
     *
     */
    private void initFloatingLabel() {
        // observe the text changing
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }
            @Override
            public void afterTextChanged(final Editable s) {
                if (floatingLabelEnabled) {
                    if (s.length() == 0) {
                        if (floatingLabelShown) {
                            floatingLabelShown = false;
                            getLabelAnimator().reverse();
                        }
                    } else if (!floatingLabelShown) {
                        floatingLabelShown = true;
                        if (getLabelAnimator().isStarted()) {
                            getLabelAnimator().reverse();
                        } else {
                            getLabelAnimator().start();
                        }
                    }
                }
            }
        });
        // observe the focus state to animate the floating label's text color appropriately
        innerFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (floatingLabelEnabled && highlightFloatingLabel) {
                    if (hasFocus) {
                        if (getLabelFocusAnimator().isStarted()) {
                            getLabelFocusAnimator().reverse();
                        } else {
                            getLabelFocusAnimator().start();
                        }
                    } else {
                        getLabelFocusAnimator().reverse();
                    }
                }
                if (outerFocusChangeListener != null) {
                    outerFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        };
        super.setOnFocusChangeListener(innerFocusChangeListener);
    }
    /**
     * Sets base color.
     *
     * @param color the color
     */
    public void setBaseColor(final int color) {
        if (baseColor != color) {
            baseColor = color;
        }
        initText();
        postInvalidate();
    }
    /**
     * Sets primary color.
     *
     * @param color the color
     */
    public void setPrimaryColor(final int color) {
        primaryColor = color;
        postInvalidate();
    }
    /**
     * Same function as {@link #setTextColor(int)}. (Directly overriding the built-in one could cause some error, so use this method instead.)
     *
     * @param color the color
     */
    public void setMetTextColor(final int color) {
        textColorStateList = ColorStateList.valueOf(color);
        resetTextColor();
    }
    /**
     * Same function as {@link #setTextColor(ColorStateList)}.
     * (Directly overriding the built-in one could cause some error, so use this method instead.)
     *
     * @param colors the colors
     */
    public void setMetTextColor(final ColorStateList colors) {
        textColorStateList = colors;
        resetTextColor();
    }
    /**
     *
     */
    private void resetTextColor() {
        if (textColorStateList == null) {
            textColorStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_enabled}, EMPTY_STATE_SET},
                    new int[]{baseColor & HEX_WHITE | HEX_BLACK_DF, baseColor & HEX_WHITE | HEX_BLACK_44});
            setTextColor(textColorStateList);
        } else {
            setTextColor(textColorStateList);
        }
    }
    /**
     * Same function as {@link #setHintTextColor(int)}. (The built-in one is a final method that can't be overridden, so use this method instead.)
     *
     * @param color the color
     */
    public void setMetHintTextColor(final int color) {
        textColorHintStateList = ColorStateList.valueOf(color);
        resetHintTextColor();
    }
    /**
     * Same function as {@link #setHintTextColor(ColorStateList)}.
     * (The built-in one is a final method that can't be overridden, so use this method instead.)
     *
     * @param colors the colors
     */
    public void setMetHintTextColor(final ColorStateList colors) {
        textColorHintStateList = colors;
        resetHintTextColor();
    }
    /**
     *
     */
    private void resetHintTextColor() {
        if (textColorHintStateList == null) {
            setHintTextColor(baseColor & HEX_WHITE | HEX_BLACK_44);
        } else {
            setHintTextColor(textColorHintStateList);
        }
    }
    /**
     * @param mode of floating label
     */
    private void setFloatingLabelInternal(final int mode) {
        switch (mode) {
            case FLOATING_LABEL_NORMAL:
                floatingLabelEnabled = true;
                highlightFloatingLabel = false;
                break;
            case FLOATING_LABEL_HIGHLIGHT:
                floatingLabelEnabled = true;
                highlightFloatingLabel = true;
                break;
            default:
                floatingLabelEnabled = false;
                highlightFloatingLabel = false;
                break;
        }
    }
    /**
     * Sets floating label.
     *
     * @param mode the mode
     */
    public void setFloatingLabel(@FloatingLabelType final int mode) {
        setFloatingLabelInternal(mode);
        initPadding();
    }
    /**
     * Gets floating label padding.
     *
     * @return the floating label padding
     */
    public int getFloatingLabelPadding() {
        return floatingLabelPadding;
    }
    /**
     * Sets floating label padding.
     *
     * @param padding the padding
     */
    public void setFloatingLabelPadding(final int padding) {
        floatingLabelPadding = padding;
        postInvalidate();
    }
    /**
     * Is floating label animating boolean.
     *
     * @return the boolean
     */
    public boolean isFloatingLabelAnimating() {
        return floatingLabelAnimating;
    }
    /**
     * Sets floating label animating.
     *
     * @param animating the animating
     */
    public void setFloatingLabelAnimating(final boolean animating) {
        floatingLabelAnimating = animating;
    }
    /**
     * Sets single line ellipsis.
     */
    public void setSingleLineEllipsis() {
        setSingleLineEllipsis(true);
    }
    /**
     * Sets single line ellipsis.
     *
     * @param enabled the enabled
     */
    public void setSingleLineEllipsis(final boolean enabled) {
        singleLineEllipsis = enabled;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }
    /**
     * Gets max characters.
     *
     * @return the max characters
     */
    public int getMaxCharacters() {
        return maxCharacters;
    }
    /**
     * Sets max characters.
     *
     * @param max the max
     */
    public void setMaxCharacters(final int max) {
        maxCharacters = max;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }
    /**
     * Gets min characters.
     *
     * @return the min characters
     */
    public int getMinCharacters() {
        return minCharacters;
    }
    /**
     * Sets min characters.
     *
     * @param min the min
     */
    public void setMinCharacters(final int min) {
        minCharacters = min;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }
    /**
     * Gets min bottom text lines.
     *
     * @return the min bottom text lines
     */
    public int getMinBottomTextLines() {
        return minBottomTextLines;
    }
    /**
     * Sets min bottom text lines.
     *
     * @param lines the lines
     */
    public void setMinBottomTextLines(final int lines) {
        minBottomTextLines = lines;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }
    /**
     * Is auto validate boolean.
     *
     * @return the boolean
     */
    public boolean isAutoValidate() {
        return autoValidate;
    }
    /**
     * Sets auto validate.
     *
     * @param autoValidate the auto validate
     */
    public void setAutoValidate(final boolean autoValidate) {
        this.autoValidate = autoValidate;
        if (autoValidate) {
            validate();
        }
    }
    /**
     * Gets error color.
     *
     * @return the error color
     */
    public int getErrorColor() {
        return errorColor;
    }
    /**
     * Sets error color.
     *
     * @param color the color
     */
    public void setErrorColor(final int color) {
        errorColor = color;
        postInvalidate();
    }
    /**
     * Sets helper text.
     *
     * @param helperText the helper text
     */
    public void setHelperText(final CharSequence helperText) {
        this.helperText = helperText == null ? null : helperText.toString();
        if (adjustBottomLines()) {
            postInvalidate();
        }
    }
    /**
     * Gets helper text.
     *
     * @return the helper text
     */
    public String getHelperText() {
        return helperText;
    }
    /**
     * Gets helper text color.
     *
     * @return the helper text color
     */
    public int getHelperTextColor() {
        return helperTextColor;
    }
    /**
     * Sets helper text color.
     *
     * @param color the color
     */
    public void setHelperTextColor(final int color) {
        helperTextColor = color;
        postInvalidate();
    }
    @Override
    public void setError(final CharSequence errorText) {
        tempErrorText = errorText == null ? null : errorText.toString();
        if (adjustBottomLines()) {
            postInvalidate();
        }
    }
    @Override
    public CharSequence getError() {
        return tempErrorText;
    }
    /**
     * only used to draw the bottom line
     *
     * @return boolean
     */
    private boolean isInternalValid() {
        return tempErrorText == null && isCharactersCountValid();
    }
    /**
     * if the main text matches the regex
     *
     * @param regex the regex
     * @return the boolean
     * @deprecated use the new validator interface to add your own custom validator
     */
    @Deprecated
    public boolean isValid(final String regex) {
        if (regex == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(getText());
        return matcher.matches();
    }
    /**
     * check if the main text matches the regex, and set the error text if not.
     *
     * @param regex     the regex
     * @param errorText the error text
     * @return true if it matches the regex, false if not.
     * @deprecated use the new validator interface to add your own custom validator
     */
    @Deprecated
    public boolean validate(final String regex, final CharSequence errorText) {
        boolean isValid = isValid(regex);
        if (!isValid) {
            setError(errorText);
        }
        postInvalidate();
        return isValid;
    }
    /**
     * Run validation on a single validator instance
     *
     * @param validator Validator to check
     * @return True if valid, false if not
     */
    public boolean validateWith(@NonNull final METValidator validator) {
        CharSequence text = getText();
        boolean isValid = validator.isValid(text, text.length() == 0);
        if (!isValid) {
            setError(validator.getErrorMessage());
        }
        postInvalidate();
        return isValid;
    }
    /**
     * Check all validators, sets the error text if not
     * <p/>
     * NOTE: this stops at the first validator to report invalid.
     *
     * @return True if all validators pass, false if not
     */
    public boolean validate() {
        if (validators == null || validators.isEmpty()) {
            return true;
        }
        CharSequence text = getText();
        boolean isEmpty = text.length() == 0;
        boolean isValid = true;
        for (METValidator validator : validators) {
            //noinspection ConstantConditions
            isValid = isValid && validator.isValid(text, isEmpty);
            if (!isValid) {
                setError(validator.getErrorMessage());
                break;
            }
        }
        if (isValid) {
            setError(null);
        }
        postInvalidate();
        return isValid;
    }
    /**
     * Has validators boolean.
     *
     * @return the boolean
     */
    public boolean hasValidators() {
        return this.validators != null && !this.validators.isEmpty();
    }
    /**
     * Adds a new validator to the View's list of validators
     * <p/>
     * This will be checked with the others in {@link #validate()}
     *
     * @param validator Validator to add
     * @return This instance, for easy chaining
     */
    public MaterialEditText addValidator(final METValidator validator) {
        if (validators == null) {
            this.validators = new ArrayList<>();
        }
        this.validators.add(validator);
        return this;
    }
    /**
     * Clear validators.
     */
    public void clearValidators() {
        if (this.validators != null) {
            this.validators.clear();
        }
    }
    /**
     * Gets validators.
     *
     * @return the validators
     */
    @Nullable
    public List<METValidator> getValidators() {
        return this.validators;
    }
    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener listener) {
        if (innerFocusChangeListener == null) {
            super.setOnFocusChangeListener(listener);
        } else {
            outerFocusChangeListener = listener;
        }
    }
    /**
     * @return ObjectAnimator
     */
    private ObjectAnimator getLabelAnimator() {
        if (labelAnimator == null) {
            labelAnimator = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0f, 1f);
        }
        labelAnimator.setDuration(floatingLabelAnimating ? FLOATING_LABEL_ANIM_DURATION : 0);
        return labelAnimator;
    }
    /**
     * @return ObjectAnimator
     */
    private ObjectAnimator getLabelFocusAnimator() {
        if (labelFocusAnimator == null) {
            labelFocusAnimator = ObjectAnimator.ofFloat(this, "focusFraction", 0f, 1f);
        }
        return labelFocusAnimator;
    }
    /**
     * @param destBottomLines the destBottomLines
     * @return ObjectAnimator
     */
    private ObjectAnimator getBottomLinesAnimator(final float destBottomLines) {
        if (bottomLinesAnimator == null) {
            bottomLinesAnimator = ObjectAnimator.ofFloat(this, "currentBottomLines", destBottomLines);
        } else {
            bottomLinesAnimator.cancel();
            bottomLinesAnimator.setFloatValues(destBottomLines);
        }
        return bottomLinesAnimator;
    }
    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        int startX = getScrollX() + (iconLeftBitmaps == null ? 0 : (iconOuterWidth + iconPadding));
        int endX = getScrollX() + (iconRightBitmaps == null ? getWidth() : getWidth() - iconOuterWidth - iconPadding);
        int lineStartY = getScrollY() + getHeight() - getPaddingBottom();
        // draw the icon(s)
        paint.setAlpha(ALPHA_OPAQUE);
        if (iconLeftBitmaps != null) {
            Bitmap icon = iconLeftBitmaps[!isInternalValid() ? VALUE_THREE : !isEnabled() ? 2 : hasFocus() ? 1 : 0];
            int iconLeft = startX - iconPadding - iconOuterWidth + (iconOuterWidth - icon.getWidth()) / 2;
            int iconTop = lineStartY + bottomSpacing - iconOuterHeight + (iconOuterHeight - icon.getHeight()) / 2;
            canvas.drawBitmap(icon, iconLeft, iconTop, paint);
        }
        if (iconRightBitmaps != null) {
            Bitmap icon = iconRightBitmaps[!isInternalValid() ? VALUE_THREE : !isEnabled() ? 2 : hasFocus() ? 1 : 0];
            int iconRight = endX + iconPadding + (iconOuterWidth - icon.getWidth()) / 2;
            int iconTop = lineStartY + bottomSpacing - iconOuterHeight + (iconOuterHeight - icon.getHeight()) / 2;
            canvas.drawBitmap(icon, iconRight, iconTop, paint);
        }
        // draw the underline
        if (!hideUnderline) {
            lineStartY += bottomSpacing;
            if (!isInternalValid()) {
                // not valid
                paint.setColor(errorColor);
                canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(2), paint);
            } else if (!isEnabled()) {
                // disabled
                paint.setColor(underlineColor != -1 ? underlineColor : baseColor & HEX_WHITE | HEX_BLACK_44);
                float interval = getPixel(1);
                for (float xOffset = 0; xOffset < getWidth(); xOffset += interval * VALUE_THREE) {
                    canvas.drawRect(startX + xOffset, lineStartY, startX + xOffset + interval, lineStartY + getPixel(1), paint);
                }
            } else if (hasFocus()) {
                // focused
                paint.setColor(primaryColor);
                canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(2), paint);
            } else {
                // normal
                paint.setColor(underlineColor != -1 ? underlineColor : baseColor & HEX_WHITE | HEX_BLACK_1E);
                canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(1), paint);
            }
        }
        textPaint.setTextSize(bottomTextSize);
        Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
        float relativeHeight = -textMetrics.ascent - textMetrics.descent;
        float bottomTextPadding = bottomTextSize + textMetrics.ascent + textMetrics.descent;
        // draw the characters counter
        if ((hasFocus() && hasCharatersCounter()) || !isCharactersCountValid()) {
            textPaint.setColor(isCharactersCountValid() ? (baseColor & HEX_WHITE | HEX_BLACK_44) : errorColor);
            String charactersCounterText = getCharactersCounterText();
            canvas.drawText(charactersCounterText, isRTL() ? startX : endX - textPaint.measureText(charactersCounterText),
                    lineStartY + bottomSpacing + relativeHeight, textPaint);
        }
        // draw the bottom text
        if (textLayout != null) {
            if (tempErrorText != null || ((helperTextAlwaysShown || hasFocus()) && !TextUtils.isEmpty(helperText))) {
                // error text or helper text
                int errorTextColor = tempErrorText != null ? errorColor
                        : helperTextColor != -1 ? helperTextColor : (baseColor & HEX_WHITE | HEX_BLACK_44);
                textPaint.setColor(errorTextColor);
                canvas.save();
                canvas.translate(startX + getBottomTextLeftOffset(), lineStartY + bottomSpacing - bottomTextPadding);
                textLayout.draw(canvas);
                canvas.restore();
            }
        }
        // draw the floating label
        if (floatingLabelEnabled && !TextUtils.isEmpty(floatingLabelText)) {
            textPaint.setTextSize(floatingLabelTextSize);
            // calculate the text color
            textPaint.setColor((Integer) focusEvaluator.evaluate(focusFraction,
                    floatingLabelTextColor != -1 ? floatingLabelTextColor : (baseColor & HEX_WHITE | HEX_BLACK_44), primaryColor));
            // calculate the horizontal position
            float floatingLabelWidth = textPaint.measureText(floatingLabelText.toString());
            int floatingLabelStartX;
            if ((getGravity() & Gravity.RIGHT) == Gravity.RIGHT || isRTL()) {
                floatingLabelStartX = (int) (endX - floatingLabelWidth);
            } else if ((getGravity() & Gravity.LEFT) == Gravity.LEFT) {
                floatingLabelStartX = startX;
            } else {
                floatingLabelStartX = startX + (int) (getInnerPaddingLeft()
                        + (getWidth() - getInnerPaddingLeft() - getInnerPaddingRight() - floatingLabelWidth) / 2);
            }
            // calculate the vertical position
            int floatingLabelStartY = innerPaddingTop + floatingLabelTextSize + floatingLabelPadding;
            int distance = floatingLabelPadding;
            int position = (int) (floatingLabelStartY - distance * (floatingLabelAlwaysShown ? 1 : floatingLabelFraction));
            // calculate the alpha
            int alpha = (int) ((floatingLabelAlwaysShown ? 1
                    : floatingLabelFraction) * INT_VALUE_0XFF * (floatingLabelTextColor != -1 ? 1
                    : (FLOAT_POINT_74 * focusFraction + FLOAT_POINT_26)));
            textPaint.setAlpha(alpha);
            // draw the floating label
            canvas.drawText(floatingLabelText.toString(), floatingLabelStartX, position, textPaint);
        }
        // draw the bottom ellipsis
        if (hasFocus() && singleLineEllipsis && getScrollX() != 0) {
            paint.setColor(primaryColor);
            float startY = lineStartY + bottomSpacing;
            int ellipsisStartX;
            if (isRTL()) {
                ellipsisStartX = endX;
            } else {
                ellipsisStartX = startX;
            }
            int signum = isRTL() ? -1 : 1;
            canvas.drawCircle(ellipsisStartX + signum * bottomEllipsisSize / 2, startY + bottomEllipsisSize / 2, bottomEllipsisSize / 2, paint);
            canvas.drawCircle(ellipsisStartX + signum * bottomEllipsisSize * VALUE_FIVE / 2,
                    startY + bottomEllipsisSize / 2,
                    bottomEllipsisSize / 2,
                    paint);
            canvas.drawCircle(ellipsisStartX + signum * bottomEllipsisSize * VALUE_NINE / 2,
                    startY + bottomEllipsisSize / 2, bottomEllipsisSize / 2, paint);
        }
        // draw the original things
        super.onDraw(canvas);
    }
    /**
     * @return boolean
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRTL() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        }
        Configuration config = getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
    /**
     * @return leftOffset
     */
    private int getBottomTextLeftOffset() {
        return isRTL() ? getCharactersCounterWidth() : getBottomEllipsisWidth();
    }
    /**
     * @return rightOffset
     */
    private int getBottomTextRightOffset() {
        return isRTL() ? getBottomEllipsisWidth() : getCharactersCounterWidth();
    }
    /**
     * @return counter width
     */
    private int getCharactersCounterWidth() {
        return hasCharatersCounter() ? (int) textPaint.measureText(getCharactersCounterText()) : 0;
    }
    /**
     * @return Ellipsis Width
     */
    private int getBottomEllipsisWidth() {
        return singleLineEllipsis ? (bottomEllipsisSize * VALUE_FIVE + getPixel(VALUE_FOUR)) : 0;
    }
    /**
     *
     */
    private void checkCharactersCount() {
        if (!hasCharatersCounter()) {
            charactersCountValid = true;
        } else {
            CharSequence text = getText();
            int count = text == null ? 0 : text.length();
            charactersCountValid = count >= minCharacters && (maxCharacters <= 0 || count <= maxCharacters);
        }
    }
    /**
     * Is characters count valid boolean.
     *
     * @return the boolean
     */
    public boolean isCharactersCountValid() {
        return charactersCountValid;
    }
    /**
     * @return boolean
     */
    private boolean hasCharatersCounter() {
        return minCharacters > 0 || maxCharacters > 0;
    }
    /**
     * @return Characters Counter Text
     */
    private String getCharactersCounterText() {
        String text;
        if (minCharacters <= 0) {
            text = isRTL() ? maxCharacters + " / " + getText().length() : getText().length() + " / " + maxCharacters;
        } else if (maxCharacters <= 0) {
            text = isRTL() ? "+" + minCharacters + " / " + getText().length() : getText().length() + " / " + minCharacters + "+";
        } else {
            text = isRTL() ? maxCharacters + "-" + minCharacters + " / "
                    + getText().length() : getText().length() + " / " + minCharacters + "-" + maxCharacters;
        }
        return text;
    }
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (singleLineEllipsis
                && getScrollX() > 0
                && event.getAction() == MotionEvent.ACTION_DOWN
                && event.getX() < getPixel(VALUE_FOUR * VALUE_FIVE)
                && event.getY() > getHeight() - extraPaddingBottom - innerPaddingBottom
                && event.getY() < getHeight() - innerPaddingBottom) {
            setSelection(0);
            return false;
        }
        return super.onTouchEvent(event);
    }
    //=======================================================================================================================
    //=======================================================================================================================
    /**
     * Is light boolean.
     *
     * @param color the color
     * @return the boolean
     */
    public boolean isLight(final int color) {
        return Math.sqrt(
                Color.red(color) * Color.red(color) * FLOAT_POINT_241
                        + Color.green(color) * Color.green(color) * FLOAT_POINT_691
                        + Color.blue(color) * Color.blue(color) * FLOAT_POINT_068) > VALUE_130;
    }
    /**
     * Dp 2 px int.
     *
     * @param context the context
     * @param dp      the dp
     * @return the int
     */
    public int dp2px(final Context context, final float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }
    /**
     * Base Validator class to either implement or inherit from for custom validation
     */
    public abstract class METValidator {
        /**
         * Error message that the view will display if validation fails.
         * <p/>
         * This is protected, so you can change this dynamically in your {@link #isValid(CharSequence, boolean)}
         * implementation. If necessary, you can also interact with this via its getter and setter.
         */
        private String errorMessage;
        /**
         * Instantiates a new Met validator.
         *
         * @param errorMessage the error message
         */
        public METValidator(@NonNull final String errorMessage) {
            this.errorMessage = errorMessage;
        }
        /**
         * Sets error message.
         *
         * @param errorMessage the error message
         */
        public void setErrorMessage(@NonNull final String errorMessage) {
            this.errorMessage = errorMessage;
        }
        /**
         * Gets error message.
         *
         * @return the error message
         */
        @NonNull
        String getErrorMessage() {
            return this.errorMessage;
        }
        /**
         * Abstract method to implement your own validation checking.
         *
         * @param text    The CharSequence representation of the text in the EditText field. Cannot be null, but may be empty.
         * @param isEmpty Boolean indicating whether or not the text param is empty
         * @return True if valid, false if not
         */
        abstract boolean isValid(@NonNull final CharSequence text, final boolean isEmpty);
    }
}