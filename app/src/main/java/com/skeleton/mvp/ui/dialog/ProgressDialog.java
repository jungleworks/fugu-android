package com.skeleton.mvp.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.BallClipRotateMultipleIndicator;

/**
 * Developer: Click Labs
 */
public final class ProgressDialog {
    private static final float DIM_AMOUNT = 0.6f;
    private static Dialog progressDialog;
    private static TextView tvProgress;
    private static TextView innerProgress;

    /**
     * Empty Constructor
     * not called
     */
    private ProgressDialog() {
    }

    /**
     * Is showing boolean.
     *
     * @return the boolean
     */
    public static boolean isShowing() {
        return progressDialog != null && progressDialog.isShowing();
    }

    /**
     * Shows the progress dialog
     *
     * @param context the context
     */
    public static void showProgressDialog(final Context context) {
        showProgressDialog(context, context.getString(R.string.text_loading));
    }

    /**
     * Method to show the progress dialog with a message
     *
     * @param context the context
     * @param message the message
     * @return
     */
    public static void showProgressDialog(final Context context, final String message) {
        try {
            /* Check if the last instance is alive */
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    tvProgress.setText(message);
                    return;
                }
            }
            /*  Ends Here   */
            progressDialog = new Dialog(context,
                    android.R.style.Theme_Translucent_NoTitleBar);
            progressDialog.setContentView(R.layout.dialog_progress);
            AVLoadingIndicatorView avi=(AVLoadingIndicatorView)progressDialog.findViewById(R.id.avi);
            avi.setIndicator(new BallClipRotateMultipleIndicator());

            tvProgress = (TextView) progressDialog
                    .findViewById(R.id.tvProgress);
            innerProgress = (TextView) progressDialog
                    .findViewById(R.id.progress);
//            tvProgress.setTypeface(Font.getRegular(activity));
            tvProgress.setText(message);
            innerProgress.setText("");
            Window dialogWindow = progressDialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow
                    .getAttributes();
            layoutParams.dimAmount = DIM_AMOUNT;
            dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update progress.
     *
     * @param percentage the percentage
     */
    public static void updateProgress(final int percentage) {
        innerProgress.setText(percentage + "%");
    }

    /**
     * Dismisses the Progress Dialog
     *
     * @return the boolean
     */
    public static boolean dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                progressDialog = null;
                tvProgress = null;
                innerProgress = null;
                return true;
            }
        }
        return false;
    }
}