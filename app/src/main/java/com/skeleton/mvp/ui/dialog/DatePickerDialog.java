package com.skeleton.mvp.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Calling for Date Picker
 * <p>
 * DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
 * <p>
 * public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
 * <p>
 * Do whatever you want to do with selected date
 * <p>
 * }
 * }).show(getSupportFragmentManager(), "datePicker");
 */
public class DatePickerDialog extends DialogFragment
        implements android.app.DatePickerDialog.OnDateSetListener {
    private android.app.DatePickerDialog.OnDateSetListener listener;

    /**
     * @param listener instance of DatePickerDialog.OnDateSetListener
     * @return object of DatePickerDialog
     */
    public static DatePickerDialog newInstance(final android.app.DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog fragment = new DatePickerDialog();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        android.app.DatePickerDialog dialog = new android.app.DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMinDate(c.getTime().getTime());
        return dialog;
        // Create a new instance of DatePickerDialog and return it
        //return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * @param view  view of DatePicker
     * @param year  year that set on DatePicker
     * @param month month that set on DatePicker
     * @param day   day that set on DatePicker
     */
    public void onDateSet(final DatePicker view, final int year, final int month, final int day) {
        // Do something with the date chosen by the user
        listener.onDateSet(view, year, month, day);
    }
}
