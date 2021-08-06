package com.skeleton.mvp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.skeleton.mvp.R
import com.skeleton.mvp.utils.StylingOptions.Property.CONTAINER_PADDING
import com.skeleton.mvp.utils.StylingOptions.Property.HIDE_STATUS_BAR
import com.skeleton.mvp.utils.StylingOptions.Property.IMAGES_MARGIN
import com.skeleton.mvp.utils.StylingOptions.Property.RANDOM_BACKGROUND
import com.skeleton.mvp.utils.StylingOptions.Property.SHOW_OVERLAY
import com.skeleton.mvp.utils.StylingOptions.Property.SHOW_TRANSITION
import com.skeleton.mvp.utils.StylingOptions.Property.SWIPE_TO_DISMISS
import com.skeleton.mvp.utils.StylingOptions.Property.ZOOMING
import com.skeleton.mvp.utils.StylingOptions.Property.values
class StylingOptions {

    private val options = sortedMapOf(
           HIDE_STATUS_BAR to true,
            IMAGES_MARGIN to true,
            CONTAINER_PADDING to false,
            SHOW_TRANSITION to true,
            SWIPE_TO_DISMISS to true,
            ZOOMING to true,
            SHOW_OVERLAY to true,
            RANDOM_BACKGROUND to false)

    fun isPropertyEnabled(property: Property): Boolean {
        return options[property] == true
    }

    fun showDialog(context: Context) {
        AlertDialog.Builder(context)
                .setMultiChoiceItems(
                        context.resources.getStringArray(R.array.styling_options),
                        options.values.toBooleanArray()
                ) { _, indexSelected, isChecked ->
                    options[values()[indexSelected]] = isChecked
                }.show()
    }

    enum class Property {
        HIDE_STATUS_BAR,
        IMAGES_MARGIN,
        CONTAINER_PADDING,
        SHOW_TRANSITION,
        SWIPE_TO_DISMISS,
        ZOOMING,
        SHOW_OVERLAY,
        RANDOM_BACKGROUND
    }
}