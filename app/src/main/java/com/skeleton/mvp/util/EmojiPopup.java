package com.skeleton.mvp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.skeleton.mvp.util.googlemap.MyEmojiManager;
import com.vanniktech.emoji.RecentEmoji;
import com.vanniktech.emoji.VariantEmoji;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import static com.skeleton.mvp.util.Utils.checkNotNull;


/**
 * Created by rajatdhamija on 04/05/18.
 */

public final class EmojiPopup {
    static final int MIN_KEYBOARD_HEIGHT = 100;

    final View rootView;
    final Activity context;

    @NonNull
    final RecentEmoji recentEmoji;
    @NonNull
    final VariantEmoji variantEmoji;
    @NonNull
    final EmojiVariantPopup variantPopup;

    final PopupWindow popupWindow;
    final EmojiGifEditText emojiEditText;

    boolean isPendingOpen;
    boolean isKeyboardOpen;

    @Nullable
    OnEmojiPopupShownListener onEmojiPopupShownListener;
    @Nullable
    OnSoftKeyboardCloseListener onSoftKeyboardCloseListener;
    @Nullable
    OnSoftKeyboardOpenListener onSoftKeyboardOpenListener;

    @Nullable
    OnEmojiBackspaceClickListener onEmojiBackspaceClickListener;
    @Nullable
    com.skeleton.mvp.util.OnEmojiClickListener onEmojiClickListener;
    @Nullable
    OnEmojiPopupDismissListener onEmojiPopupDismissListener;

    final ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final Rect rect = Utils.windowVisibleDisplayFrame(context);
            final int heightDifference = Utils.screenHeight(context) - rect.bottom;

            if (heightDifference > Utils.dpToPx(context, MIN_KEYBOARD_HEIGHT)) {
                popupWindow.setHeight(heightDifference);
                popupWindow.setWidth(rect.right);

                if (!isKeyboardOpen && onSoftKeyboardOpenListener != null) {
                    onSoftKeyboardOpenListener.onKeyboardOpen(heightDifference);
                }

                isKeyboardOpen = true;

                if (isPendingOpen) {
                    showAtBottom();
                    isPendingOpen = false;
                }
            } else {
                if (isKeyboardOpen) {
                    isKeyboardOpen = false;

                    if (onSoftKeyboardCloseListener != null) {
                        onSoftKeyboardCloseListener.onKeyboardClose();
                    }

                    dismiss();
                    Utils.removeOnGlobalLayoutListener(context.getWindow().getDecorView(), onGlobalLayoutListener);
                }
            }
        }
    };

    EmojiPopup(@NonNull final View rootView, @NonNull final EmojiGifEditText emojiEditText,
               @Nullable final RecentEmoji recent, @Nullable final VariantEmoji variant) {
        this.context = Utils.asActivity(rootView.getContext());
        this.rootView = rootView.getRootView();
        this.emojiEditText = emojiEditText;
        this.recentEmoji = recent != null ? recent : new RecentEmojiManager(context);
        this.variantEmoji = variant != null ? variant : new VariantEMojiManager(context);

        popupWindow = new PopupWindow(context);

        final OnEMojiLongClickListener longClickListener = new OnEMojiLongClickListener() {
            @Override
            public void onEmojiLongClick(@NonNull final EmojiImageView view, @NonNull final Emoji emoji) {
                variantPopup.show(view, emoji);
            }
        };

        final com.skeleton.mvp.util.OnEmojiClickListener clickListener = new com.skeleton.mvp.util.OnEmojiClickListener() {
            @Override
            public void onEmojiClick(@NonNull final EmojiImageView imageView, @NonNull final Emoji emoji) {
                emojiEditText.input(emoji);

                recentEmoji.addEmoji(emoji);
                variantEmoji.addVariant(emoji);
                imageView.updateEmoji(emoji);

                if (onEmojiClickListener != null) {
                    onEmojiClickListener.onEmojiClick(imageView, emoji);
                }

                variantPopup.dismiss();
            }
        };

        variantPopup = new EmojiVariantPopup(this.rootView, clickListener);

        final EmojiView emojiView = new EmojiView(context, clickListener, longClickListener, recentEmoji, variantEmoji);
        emojiView.setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
            @Override
            public void onEmojiBackspaceClick(final View v) {
                emojiEditText.backspace();

                if (onEmojiBackspaceClickListener != null) {
                    onEmojiBackspaceClickListener.onEmojiBackspaceClick(v);
                }
            }
        });

        popupWindow.setContentView(emojiView);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null)); // To avoid borders and overdraw.
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onEmojiPopupDismissListener != null) {
                    onEmojiPopupDismissListener.onEmojiPopupDismiss();
                }
            }
        });
    }

    public void toggle() {
        if (!popupWindow.isShowing()) {
            // Remove any previous listeners to avoid duplicates.
            Utils.removeOnGlobalLayoutListener(context.getWindow().getDecorView(), onGlobalLayoutListener);
            context.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

            if (isKeyboardOpen) {
                // If the keyboard is visible, simply show the emoji popup.
                showAtBottom();
            } else {
                // Open the text keyboard first and immediately after that show the emoji popup.
                emojiEditText.setFocusableInTouchMode(true);
                emojiEditText.requestFocus();

                showAtBottomPending();

                final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(emojiEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            dismiss();
        }

        // Manually dispatch the event. In some cases this does not work out of the box reliably.
        context.getWindow().getDecorView().getViewTreeObserver().dispatchOnGlobalLayout();
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    public void dismiss() {
        popupWindow.dismiss();
        variantPopup.dismiss();
        recentEmoji.persist();
        variantEmoji.persist();
    }

    void showAtBottom() {
        final Point desiredLocation = new Point(0, Utils.screenHeight(context) - popupWindow.getHeight());

        popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, desiredLocation.x, desiredLocation.y);
        Utils.fixPopupLocation(popupWindow, desiredLocation);

        if (onEmojiPopupShownListener != null) {
            onEmojiPopupShownListener.onEmojiPopupShown();
        }
    }

    private void showAtBottomPending() {
        if (isKeyboardOpen) {
            showAtBottom();
        } else {
            isPendingOpen = true;
        }
    }

    public static final class Builder {
        @NonNull
        private final View rootView;
        @Nullable
        private OnEmojiPopupShownListener onEmojiPopupShownListener;
        @Nullable
        private OnSoftKeyboardCloseListener onSoftKeyboardCloseListener;
        @Nullable
        private OnSoftKeyboardOpenListener onSoftKeyboardOpenListener;
        @Nullable
        private OnEmojiBackspaceClickListener onEmojiBackspaceClickListener;
        @Nullable
        private com.skeleton.mvp.util.OnEmojiClickListener onEmojiClickListener;
        @Nullable
        private OnEmojiPopupDismissListener onEmojiPopupDismissListener;
        @Nullable
        private RecentEmoji recentEmoji;
        @Nullable
        private VariantEmoji variantEmoji;

        private Builder(final View rootView) {
            this.rootView = checkNotNull(rootView, "The root View can't be null");
        }

        /**
         * @param rootView The root View of your layout.xml which will be used for calculating the height
         *                 of the keyboard.
         * @return builder For building the {@link com.vanniktech.emoji.EmojiPopup}.
         */
        @CheckResult
        public static EmojiPopup.Builder fromRootView(final View rootView) {
            return new EmojiPopup.Builder(rootView);
        }

        @CheckResult
        public EmojiPopup.Builder setOnSoftKeyboardCloseListener(@Nullable final OnSoftKeyboardCloseListener listener) {
            onSoftKeyboardCloseListener = listener;
            return this;
        }

        @CheckResult
        public EmojiPopup.Builder setOnEmojiClickListener(@Nullable final com.skeleton.mvp.util.OnEmojiClickListener listener) {
            onEmojiClickListener = listener;
            return this;
        }

        @CheckResult
        public EmojiPopup.Builder setOnSoftKeyboardOpenListener(@Nullable final OnSoftKeyboardOpenListener listener) {
            onSoftKeyboardOpenListener = listener;
            return this;
        }

        @CheckResult
        public EmojiPopup.Builder setOnEmojiPopupShownListener(@Nullable final OnEmojiPopupShownListener listener) {
            onEmojiPopupShownListener = listener;
            return this;
        }

        @CheckResult
        public EmojiPopup.Builder setOnEmojiPopupDismissListener(@Nullable final OnEmojiPopupDismissListener listener) {
            onEmojiPopupDismissListener = listener;
            return this;
        }

        @CheckResult
        public EmojiPopup.Builder setOnEmojiBackspaceClickListener(@Nullable final OnEmojiBackspaceClickListener listener) {
            onEmojiBackspaceClickListener = listener;
            return this;
        }

        /**
         * Allows you to pass your own implementation of recent emojis. If not provided the default one
         * {@link RecentEmojiManager} will be used.
         *
         * @since 0.2.0
         */
        @CheckResult
        public EmojiPopup.Builder setRecentEmoji(@Nullable final RecentEmoji recent) {
            recentEmoji = recent;
            return this;
        }

        @CheckResult
        public EmojiPopup.Builder setVariantEmoji(@Nullable final VariantEmoji variant) {
            variantEmoji = variant;
            return this;
        }

        @CheckResult
        public EmojiPopup build(@NonNull final EmojiGifEditText emojiEditText) {
            MyEmojiManager.getInstance().verifyInstalled();
            checkNotNull(emojiEditText, "EmojiEditText can't be null");

            final EmojiPopup emojiPopup = new EmojiPopup(rootView, emojiEditText, recentEmoji, variantEmoji);
            emojiPopup.onSoftKeyboardCloseListener = onSoftKeyboardCloseListener;
            emojiPopup.onEmojiClickListener = onEmojiClickListener;
            emojiPopup.onSoftKeyboardOpenListener = onSoftKeyboardOpenListener;
            emojiPopup.onEmojiPopupShownListener = onEmojiPopupShownListener;
            emojiPopup.onEmojiPopupDismissListener = onEmojiPopupDismissListener;
            emojiPopup.onEmojiBackspaceClickListener = onEmojiBackspaceClickListener;
            return emojiPopup;
        }
    }
}

