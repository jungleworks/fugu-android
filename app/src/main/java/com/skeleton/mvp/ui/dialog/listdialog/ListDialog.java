package com.skeleton.mvp.ui.dialog.listdialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.skeleton.mvp.R;

import java.util.ArrayList;

/**
 * Developer: Click Labs
 */
public final class ListDialog {
    private static final float DIM_AMOUNT = 0.6f;
    private final Activity activity;
    private OnListItemClickListener nnListItemClickListener;

    /**
     * @param act , Activity instance
     */
    private ListDialog(final Activity act) {
        activity = act;
    }

    /**
     * With common dialog with list.
     *
     * @param activity the activity
     * @return common dialog with list
     */
    public static ListDialog with(final Activity activity) {
        return new ListDialog(activity);
    }

    /**
     * Show.
     *
     * @param header    the header
     * @param arrayList the array list
     * @param listener  the listener
     */
    public void show(final String header, final ArrayList<String> arrayList, final OnListItemClickListener listener) {
        try {
            nnListItemClickListener = listener;
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_list);
            final WindowManager.LayoutParams layoutParams = dialog.getWindow()
                    .getAttributes();
            layoutParams.dimAmount = DIM_AMOUNT;
            dialog.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            ListView dlgPriorityLvw = (ListView) dialog.findViewById(R.id.listView);
            ListAdapter mListAdapter = new ListAdapter(activity);
            mListAdapter.setData(arrayList);
            dlgPriorityLvw.setAdapter(mListAdapter);
            dlgPriorityLvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> arg0, final View arg1,
                                        final int arg2, final long arg3) {
                    nnListItemClickListener.onListItemSelected(arg2, arrayList.get(arg2));
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The interface On list item click listener.
     */
    public interface OnListItemClickListener {
        /**
         * On list item selected.
         *
         * @param pos        the pos
         * @param itemString the item string
         */
        void onListItemSelected(int pos, String itemString);
    }
}
