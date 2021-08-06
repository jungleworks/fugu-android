package com.skeleton.mvp.calendarView;

import android.app.Activity;
import android.content.Context;
import android.text.format.Time;

import com.skeleton.mvp.util.Log;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class DateUtils {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh:mm aa";

    private static DateUtils dateUtils;

    private final int DEFAULT_DATE = 1;
    private final int DEFAULT_YEAR = 2015;

    /**
     * Method to get instance of this class
     *
     * @return
     */
    public static DateUtils getInstance() {

        if (dateUtils == null)
            dateUtils = new DateUtils();

        return dateUtils;
    }

    /**
     * Method to get Todays date in
     * yyyy-MM-dd HH:mmZ format
     *
     * @return
     */
    public String getTodaysDate() {

        return getTodaysDate(DEFAULT_DATE_FORMAT);
    }

    /**
     * Method to get Todays date in a specified format
     *
     * @return
     */
    public String getTodaysDate(String format) {
        return getFormattedDate(new Date(), format);
    }

    /**
     * Method to get Todays date in a specified format
     *
     * @return
     */
    public String getTodaysDate(String format, Locale locale) {
        return getFormattedDate(new Date(), format, locale);
    }

    /**
     * Method to get Todays date in a specified format
     *
     * @return
     */
    public String getFormattedDate(Date date, String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }

    /**
     * Returns the desired time from a standard formatted time
     *
     * @param formattedDate 2015-06-01 13:25:00 or 2015-06-01'T'13:25:00'Z'
     * @param currentFormat
     * @param desiredFormat
     * @return
     */
    public String parseDateAs(String formattedDate, String currentFormat, String desiredFormat) {
        return parseDateAs(formattedDate, currentFormat, desiredFormat, Locale.ENGLISH);
    }

    public int getPreviousMonth() {
        return getCurrentMonth() > 0 ? getCurrentMonth() - 1 : 11;
    }

    public boolean isDateAfterCurrentDate(String currentFormat, String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(currentFormat, Locale.ENGLISH);
        String currentDate = getTodaysDate(currentFormat);
        Date date1 = sdf.parse(currentDate);
        Date date2 = sdf.parse(date);

        return !date2.after(date1);
    }

    /**
     * Returns the desired time from a standard formatted time
     *
     * @param formattedDate 2015-06-01 13:25:00 or 2015-06-01'T'13:25:00'Z'
     * @param currentFormat
     * @param desiredFormat
     * @return
     */
    public String parseDateAs(String formattedDate, String currentFormat, String desiredFormat, Locale locale) {

        Date date;

        try {

            date = new SimpleDateFormat(currentFormat, locale).parse(formattedDate);
        } catch (Exception exTz) {
            exTz.printStackTrace();
            date = new Date();
        }

        return new SimpleDateFormat(desiredFormat, locale).format(date);
    }

    /**
     * Returns the desired time from a standard formatted time
     *
     * @param formattedDate 2015-06-01 13:25:00 or 2015-06-01'T'13:25:00'Z'
     * @param desiredFormat
     * @return
     */
    public String parseDateAs(String formattedDate, String desiredFormat, Context context) {
        return parseDateAs(formattedDate, desiredFormat, Locale.ENGLISH, context);
    }

    public String parseDateAs(String formattedDate, String desiredFormat, Locale locale, Context context) {
        //
        return new SimpleDateFormat(desiredFormat, locale).format(getDate(formattedDate, context));
    }


    public Date getDate(String formattedDate, String formate) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat(formate, Locale.ENGLISH).parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Date getDate(String formattedDate, Context context) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(formattedDate);
        } catch (ParseException e) {

            try {

                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(formattedDate);
            } catch (Exception exTz) {

                try {
                    date = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH).parse(formattedDate);
                } catch (Exception e1) {
                    try {
                        date = new SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH).parse(formattedDate);
                    } catch (Exception e3) {
                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH).parse(formattedDate);
                        } catch (Exception exDate) {
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(formattedDate);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                date = new Date();
                            }
                        }
                    }

                }


            }
        }

        return date;
    }

//    public Date getDate(String formattedDate ) {
////
//        Date date;
//        try {
//            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(formattedDate);
//        } catch (ParseException e) {
//
//            try {
//
//                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"_TZ, Locale.ENGLISH).parse(formattedDate);
//            } catch (Exception exTz) {
//
//                try {
//                    date = new SimpleDateFormat(Constants.DateFormat.ONLY_DATE, Locale.ENGLISH).parse(formattedDate);
//                } catch (Exception e1) {
//                    try {
//                        date = new SimpleDateFormat(Constants.DateFormat.END_USER_DATE_FORMAT, Locale.ENGLISH).parse(formattedDate);
//                    } catch (Exception e3) {
//                        try {
//                            date = new SimpleDateFormat(Constants.DateFormat.END_USER_DATE_TIME_FORMAT, Locale.ENGLISH).parse(formattedDate);
//                        } catch (Exception exDate) {
//                            try {
//                                date = new SimpleDateFormat(Constants.DateFormat.HISTORY_DATE_FORMAT, Locale.ENGLISH).parse(formattedDate);
//                            } catch (Exception e2) {
//                                e2.printStackTrace();
//                                date = new Date();
//                            }
//                        }
//                    }
//
//                }
//
//
//            }
//        }
//
//        return date;
//    }


    /**
     * Method to get Todays date in a specified format
     *
     * @return
     */
    public int getCurrentDay() {
        return getCurrent(Calendar.DAY_OF_MONTH);
    }

    /**
     * Method to get current month
     *
     * @return
     */
    public int getCurrentMonth() {
        return getCurrent(Calendar.MONTH);
    }

    /**
     * Method to get current month
     *
     * @return
     */
    public int getCurrentYear() {
        return getCurrent(Calendar.YEAR);
    }

    /**
     * Method to get value of field
     *
     * @param field
     * @return
     */
    private int getCurrent(int field) {
        return get(Calendar.getInstance(), field);
    }

    /**
     * Method to get value of field
     *
     * @param calendar
     * @param field
     * @return
     */
    public int get(Calendar calendar, int field) {
        return calendar.get(field);
    }

    /**
     * Method to return the name of the day
     *
     * @param day
     * @param month
     * @param year
     * @return
     */
    public String getDayName(int day, int month, int year) {

        SimpleDateFormat formatter = new SimpleDateFormat("E", Locale.ENGLISH);
        Date intendedDate = new GregorianCalendar(year, month, day).getTime();
        return formatter.format(intendedDate);
    }

    public String getFormattedDate(Date date, String format, Locale locale) {
        return new SimpleDateFormat(format, locale).format(date);
    }

    /**
     * Method to get current month
     *
     * @return
     */
    private String getMonthName(int month) {

        SimpleDateFormat formatter = new SimpleDateFormat("MMM", Locale.ENGLISH);
        Date intendedDate = new GregorianCalendar(DEFAULT_YEAR, month, DEFAULT_DATE).getTime();
        return formatter.format(intendedDate);
    }

    /**
     * Method to get current month name by date
     *
     * @param date
     * @return
     */
    public String getMonthByDate(String date) {
        int month = 0;
        try {
            Date d = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            month = cal.get(Calendar.MONTH);

        } catch (ParseException p) {
            p.printStackTrace();
        }
        return DateUtils.getInstance().getMonthName(month);

    }

    public String getYearByDate(String date) {
        String year = "January";
        try {
            Date d = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH).parse(date);
            SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            year = df.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return year;
    }

    /**
     * Calculates the maximum number of
     * days in the given month of year
     *
     * @return
     */
    public int getDaysCount(int month, int year) {

        Calendar calendar = new GregorianCalendar(year, month, DEFAULT_DATE);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Method to convert the UTC date time to Local
     *
     * @return
     */
    public String convertToLocal(String dateTime) {
        return convertToLocal(dateTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    private String convertToLocal(String dateTime, String format, String desiredFormat, Locale locale) {

        Log.e("UTC Date", dateTime + "");

        DateFormat utcFormat = new SimpleDateFormat(format, locale);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date;
        try {
            date = utcFormat.parse(dateTime);
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }

        DateFormat pstFormat = new SimpleDateFormat(desiredFormat, Locale.ENGLISH);

        String result = pstFormat.format(date);

        System.out.println(result);

        Log.e("Local Date", result + "");
        return result;
    }


    public String convertToLocal(String dateTime, String format) {
        return convertToLocal(dateTime, format, format, Locale.ENGLISH);
    }

    public String convertToLocal(String dateTime, String format, String desiredFormat) {

        Log.e("UTC Date", dateTime + "");

        DateFormat utcFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date;
        try {
            date = utcFormat.parse(dateTime);
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }

        DateFormat pstFormat = new SimpleDateFormat(desiredFormat, Locale.ENGLISH);
        pstFormat.setTimeZone(TimeZone.getDefault());

        String result = pstFormat.format(date);

        System.out.println(result);

        Log.e("Local Date", result + "");
        return result;
    }

    /**
     * Method to convert the UTC date time to Local
     *
     * @return
     */
    public String convertToUTC(String dateTime) {

        Log.e("Local Date", dateTime + "");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        Date localTime;
        try {
            localTime = sdf.parse(dateTime);
        } catch (ParseException e) {
            localTime = new Date();
            e.printStackTrace();
        }

        // Convert Local Time to UTC (Works Fine)
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String result = sdf.format(localTime);

        Log.e("Local Date", result + "");
        return result;
    }


    public String getPreSevenDate(String date) {
        String newDate = DateUtils.getInstance().getTodaysDate();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH);
            Date d = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DATE, -7);  // number of days to go back
            newDate = simpleDateFormat.format(cal.getTime());

        } catch (ParseException p) {
            p.printStackTrace();
        }
        return newDate;
    }

    public String getPostSevenDate(String date) {
        String newDate = DateUtils.getInstance().getTodaysDate();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH);
            Date d = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DATE, 6);  // number of days to add
            newDate = simpleDateFormat.format(cal.getTime());

        } catch (ParseException p) {
            p.printStackTrace();
        }
        return newDate;
    }

    public String getMondayDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        try {
            Date newdate = simpleDateFormat.parse(date);
            cal.setTime(newdate);

            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                cal.add(Calendar.DATE, -1);
            }
        } catch (ParseException p) {
            p.printStackTrace();
        }
        return simpleDateFormat.format(cal.getTime());
    }

    public String getMonthForInt(int m) {
        String month = "invalid";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (m >= 0 && m <= 11) {
            month = months[m];
        }
        return month;
    }

    public static Date getDate(long timeInMillis) {
        return new Date(timeInMillis);
    }

    public String getTimeIn12Hours(int hourOfDay, int minute) {
        int hours = hourOfDay;
        String timeSet;
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String min;
        if (minute < 10)
            min = "0" + minute;
        else
            min = String.valueOf(minute);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(min).append(" ").append(timeSet).toString();
        return aTime;
    }

    public Date convertTimeIntoTimezone(final String mDate, final Long mTimeZone, Activity activity) {
        if (mTimeZone == 0) {
            return getDate(mDate, activity);
        } else {
            Date date1 = getDate(mDate, activity);
            date1.setTime(date1.getTime() + (mTimeZone * 60 * 1000));
            return date1;
        }
    }

    public Date convertTimeIntoTimezone(final String mDate, final Long mTimeZone, Context context) {
        if (mTimeZone == 0) {
            return getDate(mDate, context);
        } else {
            Date date1 = getDate(mDate, context);
            date1.setTime(date1.getTime() + (mTimeZone * 60 * 1000));
            return date1;
        }
    }

    public boolean isSelectedDate(final Date selectedDate, final Date currentDate) {

        Time time = new Time();
        time.set(currentDate.getTime());

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(selectedDate.getTime());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    /**
     * Method to get Todays date in a specified format
     *
     */
    public String getFormattedDate(long date, String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }

}
