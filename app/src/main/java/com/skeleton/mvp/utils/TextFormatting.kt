package com.skeleton.mvp.utils

import android.text.Spannable
import android.text.SpannableStringBuilder
import java.lang.StringBuilder

object TextFormatting {
    var preFormattedString = ""
    var postFormattedString: StringBuilder? = null
    var finalFormattedString: SpannableStringBuilder? = null
    var doubleAsterik = "**"
    var doubleUnderscore = "__"
    var charArray: CharArray? = null
    var formatPositions = ArrayList<Int>()
    var isFormatCharFound = false
    var skipLoopIndex = false

    fun formatString(toBeFormattedString: String): SpannableStringBuilder? {
        this.preFormattedString = toBeFormattedString
        addPreAndPostSpace()
        charArray = this.preFormattedString.toCharArray()
        formatPositions = getCharactersToBeManipukated()
        if (formatPositions.size > 1) {
            this.postFormattedString = removeAllFormatCharacters(this.preFormattedString)
            finalFormattedString = SpannableStringBuilder(this.postFormattedString.toString())
            var message = this.postFormattedString.toString()
            for (index in 0..formatPositions.size - 1 step 2) {
                if (formatPositions.size > index + 1) {
                    if (finalFormattedString.toString().substring(formatPositions[index], formatPositions[index + 1] + 1).trim().isNotEmpty()) {
                        finalFormattedString?.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                                formatPositions[index], formatPositions[index + 1] + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            return finalFormattedString
        } else {
            return SpannableStringBuilder(toBeFormattedString)
        }
    }

    private fun addPreAndPostSpace() {
        if (!this.preFormattedString.toString().equals(doubleAsterik)) {
            this.preFormattedString = " $preFormattedString "
        }
    }

    private fun getCharactersToBeManipukated(): ArrayList<Int> {
        val formatPositions = ArrayList<Int>()
        for (index in 0..charArray!!.size - 2) {
            if (skipLoopIndex) {
                skipLoopIndex = false
                continue
            }
            if (!isFormatCharFound && charArray!![index].toString().equals(" ") && charArray!![index + 1].toString().equals("*")) {
                isFormatCharFound = true
                skipLoopIndex = true
                formatPositions.add(index + 1)
            } else if (isFormatCharFound && charArray!![index].toString().equals("*") && charArray!![index + 1].toString().equals(" ")) {
                formatPositions.add(index)
                skipLoopIndex = true
                isFormatCharFound = false
            }
        }
        return formatPositions
    }

    private fun removeAllFormatCharacters(preFormattedString: String): StringBuilder {
        var stringWithRemovedFormatCharacters = StringBuilder(preFormattedString)
        var decValue = 0
        for (index in 0..formatPositions.size - 1) {
            stringWithRemovedFormatCharacters = StringBuilder(stringWithRemovedFormatCharacters.removeRange(formatPositions[index] - decValue, formatPositions[index] + 1 - decValue))
            decValue += 1
            formatPositions[index] = formatPositions[index] - decValue
        }
        return stringWithRemovedFormatCharacters
    }

}
