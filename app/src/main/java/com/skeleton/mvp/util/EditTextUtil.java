package com.skeleton.mvp.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
/**
 * Developer: Click Labs
 */
public final class EditTextUtil {
    /**
     * Filter on editText to block space character
     */
    private static InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(final CharSequence source,
                                   final int start,
                                   final int end,
                                   final Spanned dest,
                                   final int dstart,
                                   final int dend) {
            String blockCharacterSet = " ";
            if (source != null && blockCharacterSet.contains("" + source)) {
                return "";
            }
            return null;
        }
    };
    /**
     * Empty Constructor
     * not called
     */
    private EditTextUtil() {
    }
    /**
     * @param editText instance of that edittext on which no space functionality want
     */
    public static void blockSpace(final EditText editText) {
        editText.setFilters(new InputFilter[]{filter});
    }
}