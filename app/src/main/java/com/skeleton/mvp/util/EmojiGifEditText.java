package com.skeleton.mvp.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.CallSuper;
import androidx.annotation.DimenRes;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;

import com.skeleton.mvp.util.googlemap.MyEmojiManager;
import com.vanniktech.emoji.emoji.Emoji;

/**
 * Created by rajatdhamija on 04/05/18.
 */

public class EmojiGifEditText extends AppCompatEditText {
    private float emojiSize;

    private CommitListener commitListener;

    public EmojiGifEditText(Context context) {
        super(context);
    }

    public EmojiGifEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            MyEmojiManager.getInstance().verifyInstalled();
        }

        final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        final float defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;

        if (attrs == null) {
            emojiSize = defaultEmojiSize;
        } else {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, com.vanniktech.emoji.R.styleable.EmojiEditText);

            try {
                emojiSize = a.getDimension(com.vanniktech.emoji.R.styleable.EmojiEditText_emojiSize, defaultEmojiSize);
            } finally {
                a.recycle();
            }
        }

        setText(getText());
    }

    public EmojiGifEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //    public EmojiGifEditText(final Context context) {
//        this(context, null);
//    }
//
//    public EmojiGifEditText(final Context context, final AttributeSet attrs) {
//        super(context, attrs);
//
//        if (!isInEditMode()) {
//            MyEmojiManager.getInstance().verifyInstalled();
//        }
//
//        final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
//        final float defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;
//
//        if (attrs == null) {
//            emojiSize = defaultEmojiSize;
//        } else {
//            final TypedArray a = getContext().obtainStyledAttributes(attrs, com.vanniktech.emoji.R.styleable.EmojiEditText);
//
//            try {
//                emojiSize = a.getDimension(com.vanniktech.emoji.R.styleable.EmojiEditText_emojiSize, defaultEmojiSize);
//            } finally {
//                a.recycle();
//            }
//        }
//
//        formatString(getText());
//    }
//
//    public EmojiGifEditText(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
    @Override
    @CallSuper
    protected void onTextChanged(final CharSequence text, final int start, final int lengthBefore, final int lengthAfter) {
        MyEmojiManager.replaceWithImages(getContext(), getText(), emojiSize);
    }

    @CallSuper
    public void backspace() {
        final KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        dispatchKeyEvent(event);
    }

    @CallSuper
    public void input(final Emoji emoji) {
        if (emoji != null) {
            final int start = getSelectionStart();
            final int end = getSelectionEnd();

            if (start < 0) {
                append(emoji.getUnicode());
            } else {
                getText().replace(Math.min(start, end), Math.max(start, end), emoji.getUnicode(), 0, emoji.getUnicode().length());
            }
        }
    }

    /**
     * sets the emoji size in pixels and automatically invalidates the text and renders it with the new size
     */
    public final void setEmojiSize(@Px final int pixels) {
        setEmojiSize(pixels, true);
    }

    /**
     * sets the emoji size in pixels and automatically invalidates the text and renders it with the new size when {@code shouldInvalidate} is true
     */
    public final void setEmojiSize(@Px final int pixels, final boolean shouldInvalidate) {
        emojiSize = pixels;

        if (shouldInvalidate) {
            setText(getText());
        }
    }

    /**
     * sets the emoji size in pixels with the provided resource and automatically invalidates the text and renders it with the new size
     */
    public final void setEmojiSizeRes(@DimenRes final int res) {
        setEmojiSizeRes(res, true);
    }

    /**
     * sets the emoji size in pixels with the provided resource and invalidates the text and renders it with the new size when {@code shouldInvalidate} is true
     */
    public final void setEmojiSizeRes(@DimenRes final int res, final boolean shouldInvalidate) {
        setEmojiSize(getResources().getDimensionPixelSize(res), shouldInvalidate);
    }

    @Override
    public InputConnection onCreateInputConnection(final EditorInfo info) {
        final InputConnection ic = super.onCreateInputConnection(info);
        EditorInfoCompat.setContentMimeTypes(info, new String[]{"image/gif"});
        final InputConnectionCompat.OnCommitContentListener callback = (info1, flags, opts) -> {
            if (BuildCompat.isAtLeastNMR1() && (flags & InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                try {
                    info1.requestPermission();
                } catch (Exception e) {
                    return false; // return false if failed
                }
            }
            if (commitListener != null) {
                commitListener.onCommitContent(info1.getContentUri(), info1.getLinkUri());
            }
            return true;  // return true if succeeded
        };
        return InputConnectionCompat.createWrapper(ic, info, callback);
    }

    public void setCommitListener(CommitListener listener) {
        this.commitListener = listener;
    }

    public interface CommitListener {
        void onCommitContent(Uri uri, Uri sendUri);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        // React:
        switch (id) {
            case android.R.id.cut:
                onTextCut();
                break;
            case android.R.id.paste:
                onTextPaste();
                break;
            case android.R.id.copy:
                onTextCopy();
        }
        return consumed;
    }

    /**
     * Text was cut from this EditText.
     */
    public void onTextCut() {
//        Toast.makeText(getContext(), "Cut!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Text was copied from this EditText.
     */
    public void onTextCopy() {
//        Toast.makeText(getContext(), "Copy!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Text was pasted into the EditText.
     */
    public void onTextPaste() {
//        Toast.makeText(getContext(), "Paste!", Toast.LENGTH_SHORT).show();
    }
}
