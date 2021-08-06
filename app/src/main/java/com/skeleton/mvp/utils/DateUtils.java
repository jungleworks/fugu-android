package com.skeleton.mvp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Bhavya Rattan on 09/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class DateUtils {

    private static DateUtils dateUtils;

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
     * Method to get Todays date in a specified format
     *
     * @return
     */
    public static String getFormattedDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(date);
    }

    /**
     * Method to convert the UTC date time to Local
     *
     * @return
     */
    public String convertToLocal(String dateTime) {
        return convertToLocal(dateTime, STANDARD_DATE_FORMAT_TZ);
    }

    /**
     * Method to convert the UTC date time to Local Date object
     *
     * @return
     */
    public Date getLocalDateObject(String dateTime) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(convertToLocal(dateTime, STANDARD_DATE_FORMAT_TZ));
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * Method to convert the UTC date time to UTC Date object
     *
     * @return
     */
    public Date getDateObject(String dateTime) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public String convertToLocalWithoutYear(String dateTime) {

        return convertToLocalWithoutYear(dateTime, STANDARD_DATE_FORMAT_TZ);
    }

    public String convertToLocal(String dateTime, String format) {

        //FuguLog.e("UTC Date", dateTime + "");

        DateFormat utcFormat = new SimpleDateFormat(format);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date;
        try {
            date = utcFormat.parse(dateTime);
        } catch (Exception e) {
            date = new Date();
            e.printStackTrace();
        }

        DateFormat pstFormat = new SimpleDateFormat(STANDARD_DATE_FORMAT_TZ);
        pstFormat.setTimeZone(TimeZone.getDefault());

        String result = pstFormat.format(date);

        //System.out.println(result);

        // FuguLog.e("Local Date", result + "");
        return result;
    }


    static String STANDARD_DATE_FORMAT_TZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Method to convert Local date time to UTC
     *
     * @return
     */
    public String convertToUTC(String dateTime) {

        // FuguLog.e("Local Date", dateTime + "");

        SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_DATE_FORMAT_TZ);
        Date localTime = null;
        try {
            localTime = sdf.parse(dateTime);
        } catch (ParseException e) {
            localTime = new Date();
            e.printStackTrace();
        }

        // Convert Local Time to UTC (Works Fine)
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String result = sdf.format(localTime);

        //FuguLog.e("UTC Date", result + "");
        return result;
    }

    public String convertToLocalWithoutYear(String dateTime, String format) {

        //FuguLog.e("UTC Date", dateTime + "");

        DateFormat utcFormat = new SimpleDateFormat(format);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date;
        try {
            date = utcFormat.parse(dateTime);
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }

        DateFormat pstFormat = new SimpleDateFormat("dd MMM");
        pstFormat.setTimeZone(TimeZone.getDefault());

        String result = pstFormat.format(date);

        //System.out.println(result);

        // FuguLog.e("Local Date", result + "");
        return result;
    }


    public static String getTime(String dateTime) {
        try {

            String time = dateTime.split("T")[1].replace("Z", "");

            String hour = time.split(":")[0];
            String min = time.split(":")[1];

            String amOrpm = Integer.parseInt(hour) >= 12 ? "PM" : "AM";
            String newHour = "";

            if (Integer.parseInt(hour) == 0) {
                newHour = "12";
            } else if (Integer.parseInt(hour) <= 12) {
                newHour = hour;
            } else {
                newHour = "" + (Integer.parseInt(hour) - 12);
            }

            String finalTime = "" + newHour + ":" + min + " " + amOrpm;

            SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_DATE_FORMAT_TZ);
            Date tempDate = sdf.parse(dateTime);

            return finalTime;
        } catch (Exception e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public static String getDate(String dateTime) {
        try {

            String finalDate = "";

            if (!dateTime.isEmpty()) {
                String date = dateTime.split("T")[0];

                String year = date.split("-")[0];
                String month = date.split("-")[1];
                String day = date.split("-")[2];

                SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_DATE_FORMAT_TZ);
                Date tempDate = sdf.parse(dateTime);
                if (android.text.format.DateUtils.isToday(tempDate.getTime())) {
                    return "Today";
                } else if (String.valueOf(android.text.format.DateUtils.getRelativeTimeSpanString(tempDate.getTime(),
                        sdf.parse(getFormattedDate(new Date())).getTime(),
                        android.text.format.DateUtils.FORMAT_NUMERIC_DATE
                )).equalsIgnoreCase("yesterday")|| String.valueOf(android.text.format.DateUtils.getRelativeTimeSpanString(tempDate.getTime(),
                        sdf.parse(getFormattedDate(new Date())).getTime(),
                        android.text.format.DateUtils.FORMAT_NUMERIC_DATE
                )).contains("hours ago")) {
                    return "Yesterday";
                }


                if ("01".equalsIgnoreCase(month)) {
                    month = "Jan";
                } else if ("02".equalsIgnoreCase(month)) {
                    month = "Feb";
                } else if ("03".equalsIgnoreCase(month)) {
                    month = "Mar";
                } else if ("04".equalsIgnoreCase(month)) {
                    month = "Apr";
                } else if ("05".equalsIgnoreCase(month)) {
                    month = "May";
                } else if ("06".equalsIgnoreCase(month)) {
                    month = "Jun";
                } else if ("07".equalsIgnoreCase(month)) {
                    month = "Jul";
                } else if ("08".equalsIgnoreCase(month)) {
                    month = "Aug";
                } else if ("09".equalsIgnoreCase(month)) {
                    month = "Sept";
                } else if ("10".equalsIgnoreCase(month)) {
                    month = "Oct";
                } else if ("11".equalsIgnoreCase(month)) {
                    month = "Nov";
                } else if ("12".equalsIgnoreCase(month)) {
                    month = "Dec";
                }

                finalDate = day + " " + month + " " + year;
            }

            return finalDate;
        } catch (Exception e) {
            return dateTime;
        }
    }

    public static String getRelativeDate(String dateTime, boolean isTime) {

        String finalDate = "";


        try {
            SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_DATE_FORMAT_TZ);
            Date tempDate = sdf.parse(dateTime);

            if (android.text.format.DateUtils.isToday(tempDate.getTime()) && isTime) {
                String time = dateTime.split("T")[1].replace("Z", "");

                String hour = time.split(":")[0];
                String min = time.split(":")[1];

                String amOrpm = Integer.parseInt(hour) >= 12 ? "PM" : "AM";
                String newHour = "";

                if (Integer.parseInt(hour) == 0) {
                    newHour = "12";
                } else if (Integer.parseInt(hour) <= 12) {
                    newHour = hour;
                } else {
                    newHour = "" + (Integer.parseInt(hour) - 12);
                }

                String finalTime = "" + newHour + ":" + min + " " + amOrpm;

                finalDate = finalTime;
            } else {
                finalDate = String.valueOf(android.text.format.DateUtils.getRelativeTimeSpanString(tempDate.getTime(),
                        sdf.parse(getFormattedDate(new Date())).getTime(),
                        android.text.format.DateUtils.DAY_IN_MILLIS,
                        android.text.format.DateUtils.FORMAT_ABBREV_MONTH |
                                android.text.format.DateUtils.FORMAT_SHOW_YEAR
                ));
            }

            // finalDate = dateTime.split("T")[0];
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return finalDate;
    }

    public static int getTimeInMinutes(String dateTime) {
        try {
            String time = dateTime.split("T")[1].replace("Z", "");
            int hour = Integer.parseInt(time.split(":")[0]);
            int min = Integer.parseInt(time.split(":")[1]);
            return (hour) * 60 + min;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
