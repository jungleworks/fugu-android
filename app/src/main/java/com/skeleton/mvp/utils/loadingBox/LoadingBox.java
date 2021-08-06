package com.skeleton.mvp.utils.loadingBox;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.utils.FuguLog;


/**
 * Class that holds ready to use dialogs
 */
public class LoadingBox {

    private static Dialog progressDialog;
    private static TextView tvProgress;
    private static TextView innerProgress;

    /**
     * Shows the progress dialog
     *
     * @param activity
     * @return the {@link TextView} on which progress has to be set
     */
    public static void showOn(Activity activity) {

        showOn(activity, activity.getString(R.string.fugu_loading));
    }



    /**
     * Method to show the progress dialog with a message
     *
     * @param activity
     * @param message
     * @return
     */
    public static void showOn(final Activity activity, final String message) {

        try {
            /* Check if the last instance is alive */
            if (progressDialog != null)
                if (progressDialog.isShowing()) {
                    tvProgress.setText(message);
                    return;
                }

            /*  Ends Here   */

            progressDialog = new Dialog(activity,
                  R.style.Theme_AppCompat_Translucent);

            progressDialog.setContentView(R.layout.loading_box);

            tvProgress = (TextView) progressDialog
                    .findViewById(R.id.tvProgress);
            innerProgress = (TextView) progressDialog
                    .findViewById(R.id.progress);
//            tvProgress.setTypeface(Font.getRegular(context));
            tvProgress.setText(message);
            innerProgress.setText("");

            ((ProgressWheel) progressDialog.findViewById(R.id.progress_wheel))
                    .spin();

            Window dialogWindow = progressDialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow
                    .getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            FuguLog.e("PROGRESS DIALOG", "EXCEPTION: " + e.getMessage());
        }
    }

    public static void updateProgress(int percentage) {
        innerProgress.setText(percentage + "%");
    }

    /**
     * Dismisses the Progress Dialog
     */
    public static boolean hide() {

        FuguLog.e("PROGRESS DIALOG", "DISMISSED");

        if (progressDialog != null)
            if (progressDialog.isShowing()) {

                try {
                    progressDialog.dismiss();
                } catch (Exception ex) {
                    FuguLog.e("Dismiss Loading Dialog", ex.toString());
                }
                progressDialog = null;
                tvProgress = null;
                innerProgress = null;
                return true;
            }

        return false;
    }
}
