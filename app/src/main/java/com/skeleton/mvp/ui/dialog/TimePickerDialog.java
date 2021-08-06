package com.skeleton.mvp.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Calling for Time picker
 * <p>
 * TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
 * <p>
 * <p>
 * public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
 * <p>
 * Do whatever you want to do with selected time
 * <p>
 * }
 * }).show(getSupportFragmentManager(), "timePicker");
 */
public class TimePickerDialog extends DialogFragment
        implements android.app.TimePickerDialog.OnTimeSetListener {
    private android.app.TimePickerDialog.OnTimeSetListener listener;

    /**
     * @param listener instance of TimePickerDialog.OnTimeSetListener
     * @return object of TimePickerDialog
     */
    public static TimePickerDialog newInstance(final android.app.TimePickerDialog.OnTimeSetListener listener) {
        TimePickerDialog fragment = new TimePickerDialog();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        return new android.app.TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    /**
     * @param view      view of Time picker
     * @param hourOfDay hour to set on time picker
     * @param minute    minute to set on time picker
     */
    public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
        // Do something with the time chosen by the user
        listener.onTimeSet(view, hourOfDay, minute);
    }
}
