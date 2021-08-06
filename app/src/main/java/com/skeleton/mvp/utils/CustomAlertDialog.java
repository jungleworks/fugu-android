package com.skeleton.mvp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skeleton.mvp.R;


public class CustomAlertDialog {
    /**
     * Instantiates a new Custom alert dialog.
     *
     * @param builder the builder
     */
    public CustomAlertDialog(final Builder builder) {
    }

    /**
     * The interface Custom dialog interface.
     */
    public interface CustomDialogInterface {
        /**
         * The interface On click listener.
         */
        interface OnClickListener {
            /**
             * On click.
             */
            void onClick();
        }

        /**
         * The interface On dismiss listener.
         */
        interface OnDismissListener {
            /**
             * On dismiss.
             */
            void onDismiss();
        }

        /**
         * The interface On cancel listener.
         */
        interface OnCancelListener {
            /**
             * On cancel.
             */
            void onCancel();
        }
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private final Context mContext;
        private final LayoutInflater mInflater;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mPositiveButtonText;
        private CustomDialogInterface.OnClickListener mPositiveButtonListener;
        private CharSequence mNegativeButtonText;
        private CustomDialogInterface.OnClickListener mNegativeButtonListener;
        private boolean mCancelable;
        private CustomDialogInterface.OnCancelListener mOnCancelListener;
        private CustomDialogInterface.OnDismissListener mOnDismissListener;
        private AlertDialog.Builder mBuilder;
        private AlertDialog mAlertDialog;

        /**
         * Instantiates a new Builder.
         *
         * @param context the context
         */
        public Builder(@NonNull final Context context) {
            mContext = context;
            mCancelable = true;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * Set the title using the given resource id.
         *
         * @param titleId the title id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@StringRes final int titleId) {
            mTitle = mContext.getText(titleId);
            return this;
        }

        /**
         * Set the title displayed in the {@link Dialog}.
         *
         * @param title the title
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@Nullable final CharSequence title) {
            mTitle = title;
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         *
         * @param messageId the message id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(@StringRes final int messageId) {
            mMessage = mContext.getText(messageId);
            return this;
        }

        /**
         * Set the message to display.
         *
         * @param message the message
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(@Nullable final CharSequence message) {
            mMessage = message;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param textId   The resource id of the text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(@StringRes final int textId, final CustomDialogInterface.OnClickListener listener) {
            mPositiveButtonText = mContext.getText(textId);
            mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param text     The text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(final CharSequence text, final CustomDialogInterface.OnClickListener listener) {
            mPositiveButtonText = text;
            mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param textId   The resource id of the text to display in the negative button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(@StringRes final int textId, final CustomDialogInterface.OnClickListener listener) {
            mNegativeButtonText = mContext.getText(textId);
            mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param text     The text to display in the negative button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(final CharSequence text, final CustomDialogInterface.OnClickListener listener) {
            mNegativeButtonText = text;
            mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @param cancelable the cancelable
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(final boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }


        public Builder setOnCancelListener(final CustomDialogInterface.OnCancelListener onCancelListener) {
            mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @param onDismissListener the on dismiss listener
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnDismissListener(final CustomDialogInterface.OnDismissListener onDismissListener) {
            mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * Creates an {@link AlertDialog} with the arguments supplied to this
         * builder.
         * <p>
         * Calling this method does not display the dialog. If no additional
         * processing is needed, {@link #show()} may be called instead to both
         * create and display the dialog.
         *
         * @return the alert dialog
         */
        public AlertDialog create() {
            mBuilder = new AlertDialog.Builder(mContext);
            final View dialogView = mInflater.inflate(R.layout.dialog_custom, null);
            mBuilder.setView(dialogView);
            mAlertDialog = mBuilder.create();
            TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
            TextView tvMessage = (TextView) dialogView.findViewById(R.id.tvMessage);
            Button btnNegative = (Button) dialogView.findViewById(R.id.btnNegative);
            Button btnPositive = (Button) dialogView.findViewById(R.id.btnPositive);
            //set title
            if (mTitle != null) {
                tvTitle.setText(mTitle);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
            if (mMessage != null) {
                tvMessage.setText(mMessage);
            }
            //set positive button
            if (mPositiveButtonText != null) {
                btnPositive.setText(mPositiveButtonText);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mAlertDialog.dismiss();
                        if (mPositiveButtonListener != null) {
                            mPositiveButtonListener.onClick();
                        }
                    }
                });
            } else {
                btnPositive.setVisibility(View.GONE);
            }
            //set negative button
            if (mNegativeButtonText != null) {
                btnNegative.setText(mNegativeButtonText);
                btnNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mAlertDialog.dismiss();
                        if (mNegativeButtonListener != null) {
                            mNegativeButtonListener.onClick();
                        }
                    }
                });
            } else {
                btnNegative.setVisibility(View.GONE);
            }
            //set Cancelable
            mAlertDialog.setCancelable(mCancelable);
            mAlertDialog.setCanceledOnTouchOutside(mCancelable);
            //set cancel listener
            if (mOnCancelListener != null) {
                mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        if (mOnCancelListener != null) {
                            mOnCancelListener.onCancel();
                        }
                    }
                });
            }
            //set dismiss listener
            if (mOnDismissListener != null) {
                mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface dialog) {
                        if (mOnDismissListener != null) {
                            mOnDismissListener.onDismiss();
                        }
                    }
                });
            }
            return mAlertDialog;
        }

        /**
         * Creates an {@link AlertDialog} with the arguments supplied to this
         * builder and immediately displays the dialog.
         * <p>
         * Calling this method is functionally identical to:
         * <pre>
         *     AlertDialog dialog = builder.create();
         *     dialog.show();
         * </pre>
         *
         * @return the alert dialog
         */
        public AlertDialog show() {
            final AlertDialog dialog = create();
            dialog.show();
            return dialog;
        }

        /**
         * Build custom alert dialog.
         *
         * @return the custom alert dialog
         */
        public CustomAlertDialog build() {
            return new CustomAlertDialog(this);
        }
    }
}
