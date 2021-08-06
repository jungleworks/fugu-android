package com.skeleton.mvp.util

import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.FuguUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class DateStringUtil {
    object FormatString {
        fun getFormattedString(message: String?): ArrayList<String> {
            val formattedStrings = ArrayList<String>()
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            today.add(Calendar.MINUTE, -1 * FuguUtils.getTimeZoneOffset())
            try {
                var matches = 0
                val dateRegex = "(\\d+(\\.|-|\\/)\\d+(\\.|-|\\/)\\d)|(\\d+(\\.|-|\\/)\\d+)"
                val datePattern = Pattern.compile(dateRegex, Pattern.MULTILINE)
                val datePatternMatcher = datePattern.matcher(message)
                val currentDate = DateUtils.getFormattedDate(today.time)
                while (datePatternMatcher.find()) {
                    matches += 1
                    if (matches > 2) {
                        val formattedStrings = ArrayList<String>()
                        formattedStrings.add(currentDate)
                        return formattedStrings
                    }

                    val unparsedDate = datePatternMatcher.group(0)
                    val monthDate = if (unparsedDate.contains(".")) {
                        unparsedDate.split(".") as ArrayList<String>
                    } else if (unparsedDate.contains("/")) {
                        unparsedDate.split("/") as ArrayList<String>
                    } else if (unparsedDate.contains("-")) {
                        unparsedDate.split("-") as ArrayList<String>
                    } else {
                        val formattedStrings = ArrayList<String>()
                        formattedStrings.add(currentDate)
                        return formattedStrings
                    }
                    if (monthDate[0].toInt() > 31 || monthDate[1].toInt() > 12) {
                        val formattedStrings = ArrayList<String>()
                        formattedStrings.add(currentDate)
                        return formattedStrings
                    }
                    val inputDate = "" + monthDate[0] + "/" + monthDate[1]
                    val format1 = SimpleDateFormat("dd/MM", Locale.ENGLISH)
                    val c = Calendar.getInstance().time
                    val format2 = SimpleDateFormat("yyyy", Locale.ENGLISH)
                    var dt1 = format1.parse(inputDate)
                    val calendar = Calendar.getInstance()
                    calendar.time = dt1
                    calendar.add(Calendar.MINUTE, -1 * FuguUtils.getTimeZoneOffset())
                    dt1 = calendar.time
                    val currentYear = format2.format(c).toString().toInt()
                    try {
                        var previousYear = false
                        val monthFormat = SimpleDateFormat("MM", Locale.ENGLISH)
                        val dayFormat = SimpleDateFormat("dd", Locale.ENGLISH)
                        val currentMonth = monthFormat.format(c).toString().toInt()
                        val currentDay = dayFormat.format(c).toString().toInt()
                        val endMonth = monthDate[1].toInt()
                        val endDay = monthDate[0].toInt()

                        val startDate = Date(currentYear, currentMonth, currentDay)
                        var endDate = Date(currentYear, endMonth, endDay)
                        if (currentMonth == 1 && endMonth == 12) {
                            previousYear = true
                            endDate = Date(currentYear - 1, endMonth, endDay)
                        }
                        val difInDays = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt()
                        if (difInDays < 0 && difInDays >= -31 && previousYear) {
                            dt1.year = Math.abs(1970 - currentYear) + 69
                        } else {
                            if (difInDays < -31) {
                                dt1.year = Math.abs(1970 - currentYear) + 71
                            } else {
                                dt1.year = Math.abs(1970 - currentYear) + 70
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                    val format3 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                    formattedStrings.add(format3.format(dt1))
                }
                if (formattedStrings.size == 0) {
                    val currentDate = DateUtils.getFormattedDate(today.time)
                    val formattedStrings = ArrayList<String>()
                    formattedStrings.add(currentDate)
                    return formattedStrings
                }
            } catch (e: Exception) {
                val currentDate = DateUtils.getFormattedDate(today.time)
                val formattedStrings = ArrayList<String>()
                formattedStrings.add(currentDate)
                return formattedStrings
            }
            return formattedStrings
        }
    }
}