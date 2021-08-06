package com.skeleton.mvp.util;

import android.content.Context;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.skeleton.mvp.activity.ChatActivity;

public class TrimmedTextView extends TextView {
    public static enum EllipsizeRange {
        ELLIPSIS_AT_START, ELLIPSIS_AT_END;
    }

    private CharSequence originalText;
    private SpannableStringBuilder builder = new SpannableStringBuilder();

    private final TextWatcher textCacheInvalidator = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            originalText = null;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public TrimmedTextView(Context context) {
        this(context, null, 0);
    }

    public TrimmedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrimmedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addTextChangedListener(textCacheInvalidator);
        Log.v("TEXT", "Set!");
    }

    public CharSequence getText() {
        if (originalText == null) {
            return super.getText();
        }
        return originalText;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();

        CharSequence text = this.getText();
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            int ellipsisStart;
            int ellipsisEnd;
            TruncateAt where = null;
            ellipsisStart = spanned.getSpanStart(EllipsizeRange.ELLIPSIS_AT_START);
            if (ellipsisStart >= 0) {
                where = TruncateAt.START;
                ellipsisEnd = spanned.getSpanEnd(EllipsizeRange.ELLIPSIS_AT_START);
            } else {
                ellipsisStart = spanned.getSpanStart(EllipsizeRange.ELLIPSIS_AT_END);
                if (ellipsisStart >= 0) {
                    where = TruncateAt.END;
                    ellipsisEnd = spanned.getSpanEnd(EllipsizeRange.ELLIPSIS_AT_END);
                } else {
                    // No EllipsisRange spans in this text
                    return;
                }
            }

            Log.v("TEXT", "ellipsisStart: " + ellipsisStart);
            Log.v("TEXT", "ellipsisEnd:   " + ellipsisEnd);
            Log.v("TEXT", "where:         " + where);

            builder.clear();
            builder.append(text, 0, ellipsisStart).append(text, ellipsisEnd, text.length());
            float consumed = Layout.getDesiredWidth(builder, layout.getPaint());
            CharSequence ellipsisText = text.subSequence(ellipsisStart, ellipsisEnd);
            CharSequence ellipsizedText = TextUtils.ellipsize(ellipsisText, layout.getPaint(),
                    this.getMeasuredWidth() - consumed, where);


            if (ellipsizedText.length() < ellipsisText.length()) {
                builder.clear();
                CharSequence isOnString = text.subSequence(ellipsisEnd, text.length());
                builder.append(text, 0, ellipsisStart).append(ellipsizedText)
                        .append(smallText(isOnString,0,isOnString.length()));
                setText(builder);
                originalText = text;
                requestLayout();
                invalidate();
            }
        }
    }

    public CharSequence smallText(CharSequence text,int start , int end){
        SpannableString s = new SpannableString(text);
        s.setSpan(new RelativeSizeSpan(0.7f), start,end, 0); // set size
        return s;
    }
}

