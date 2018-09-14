package org.docheinstein.commons.utils.time;


import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/** Contains utilities for time. */
public class TimeUtil {

    public static final long HOURS_IN_DAY = 24;
    public static final long MIN_IN_DAY = HOURS_IN_DAY * 60;
    public static final long SEC_IN_DAY = MIN_IN_DAY * 60;
    public static final long MS_IN_DAY = SEC_IN_DAY * 1000;

    /**
     * Contains some common patterns that can be used
     * for the methods of this class.
     */
    public static class Patterns {
        public static final String DATE_TIME = "dd/MM/yyyy HH:mm:ss";
        public static final String TIME = "HH:mm:ss";

        public static final String DATE_CHRONOLOGICALLY_SORTABLE = "yyyy_MM_dd";
    }

    /**
     * Returns the string that is composed using the current millis and
     * the given pattern.
     * @param pattern the date/time pattern
     * @return the string that uses the current millis for createRequest the given pattern
     *
     * @see #millisToString(String, long)
     * @see #dateToString(String, Date, TimeZone)
     */
    public static String millisToString(String pattern) {
        return millisToString(pattern, System.currentTimeMillis());
    }

    /**
     * Returns the string that is composed using the given millis and
     * the given pattern.
     * @param pattern the date/time pattern
     * @param millis the amount of millis the pattern is created with
     * @return the string that uses the millis for createRequest the given pattern
     *
     * @see #millisToString(String)
     * @see #dateToString(String, Date, TimeZone)
     */
    public static String millisToString(String pattern, long millis) {
        return dateToString(pattern, new Date(millis), TimeZone.getTimeZone("GMT"));
    }

    /**
     * Returns the string that is composed using the given pattern,
     * the default timezone and the current date.
     * @param pattern the date/time pattern
     * @return  the string that is composed using the given pattern,
     *          the default timezone and the current date
     *
     * @see #dateToString(String, Date, TimeZone)
     */
    public static String dateToString(String pattern) {
        return dateToString(pattern, new Date());
    }

    /**
     * Returns the string that is composed using the given pattern, date
     * and the default timezone.
     * @param pattern the date/time pattern
     * @param date the date
     * @return  the string that is composed using the given pattern, date and
     *          the default timezone
     *
     * @see #dateToString(String, Date, TimeZone)
     */
    public static String dateToString(String pattern, Date date) {
        return dateToString(pattern, date, TimeZone.getDefault());
    }

    /**
     * Returns the string that is composed using the given pattern, date and timezone.
     * @param pattern the date/time pattern
     * @param date the date
     * @param zone the timezone
     * @return the string that is composed using the given pattern, date and timezone
     */
    public static String dateToString(String pattern, Date date, TimeZone zone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(zone);
        return simpleDateFormat.format(date);
    }

    /**
     * Returns the amount of millis between the current and the given time.
     * @param t a time
     * @return the millis between now and the time
     */
    public static long getMillisBetweenNowAndTime(Time t) {
        Calendar now = Calendar.getInstance();

        Date currentTimeInUnixOriginDate = new GregorianCalendar(1970, 0, 1,
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            now.get(Calendar.SECOND)).getTime();

        long currentTimeInDayAfterUnixOriginDate = currentTimeInUnixOriginDate.getTime();
        long timeInUnixOrigin = t.getTime();
        return
            (timeInUnixOrigin - currentTimeInDayAfterUnixOriginDate + MS_IN_DAY)
                % MS_IN_DAY;
    }
}

