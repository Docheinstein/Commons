package org.docheinstein.commons.utils.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Contains utilities for time. */
public class TimeUtil {

    public static final int HOURS_IN_DAY = 24;
    public static final int MIN_IN_DAY = HOURS_IN_DAY * 60;
    public static final int SEC_IN_DAY = MIN_IN_DAY * 60;
    public static final int MS_IN_DAY = SEC_IN_DAY * 1000;

    public static final int MIN_IN_HOUR = 60;
    public static final int SEC_IN_MIN = 60;
    public static final int MS_IN_SEC = 1000;


    public static final int MS_IN_MIN = SEC_IN_MIN * MS_IN_SEC;
    public static final int MS_IN_HOUR = MIN_IN_HOUR * MS_IN_MIN;
    /**
     * Contains some common patterns that can be used
     * for the methods of this class.
     */
    public static class Patterns {
        public static final String DATE_TIME_SLASH = "dd/MM/yyyy HH:mm:ss";
        public static final String DATE_TIME_DASH = "dd-MM-yyyy HH:mm:ss";
        public static final String TIME = "HH:mm:ss";

        public static final String DATE_CHRONOLOGICALLY_SORTABLE = "yyyy_MM_dd";
    }

    /**
     * Simple entity that represents a time as it is in the form HH:mm:ss[.ms]
     * (without zone, locale or other similar stuff).
     */
    public static class TimeStruct {
        public int hour;
        public int minute;
        public int second;
        public int millis;

        public TimeStruct(int h, int m, int s, int ms) {
            hour = h;
            minute = m;
            second = s;
            millis = ms;
        }


        /**
         * Converts this time to a string formatted 'HH:mm:ss[.ms]'
         * The milliseconds field is omitted if it is 0.
         * @return this time as string
         */
        @Override
        public String toString() {
            return
                String.format("%02d", hour) + ":" +
                String.format("%02d", minute) + ":" +
                String.format("%02d", second) +
                    (millis > 0 ? String.format(".%02d", millis ) : "")
                ;
        }
    }

    /**
     * Returns the string that is composed using the given millis and
     * the given pattern.
     *
     * @param pattern the date/time pattern
     * @param millis  the amount of millis the pattern is created with
     * @return the string that uses the millis for createRequest the given pattern
     * @see #datetimeToString(String, LocalDateTime)
     * @see #nowToString(String)
     */
    public static String millisToString(String pattern, long millis) {
        return datetimeToString(
            pattern,
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(millis),
                ZoneId.systemDefault()));
    }

    /**
     * Returns the string that is composed using the given pattern,
     * the default timezone and the current date.
     *
     * @param pattern the date/time pattern
     * @return the string that is composed using the given pattern,
     * the default timezone and the current date
     * @see #millisToString(String, long)
     * @see #datetimeToString(String, LocalDateTime)
     */
    public static String nowToString(String pattern) {
        return datetimeToString(pattern, LocalDateTime.now());
    }

    /**
     * Returns the string that is composed using the given pattern, date.
     *
     * @param pattern  the date/time pattern
     * @param datetime the datetime
     * @return the string that is composed using the given pattern, date
     */
    public static String datetimeToString(String pattern, LocalDateTime datetime) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        return datetime.format(f);
    }

    /**
     * Converts an amount of millis to a time struct.
     * @param millis the amount of millis
     * @return the time struct associated with the given amount
     */
    public static TimeStruct millisToTime(long millis) {
        return new TimeStruct(
            (int) (millis / MS_IN_HOUR),
            (int) (millis % MS_IN_HOUR / MS_IN_MIN),
            (int) (millis % MS_IN_MIN / MS_IN_SEC),
            (int) (millis % MS_IN_SEC)
        );
    }

    /**
     * Converts an amount of seconds to a time struct.
     * @param seconds the amount of millis
     * @return the time struct associated with the given amount
     *
     * @see #millisToTime(long)
     */
    public static TimeStruct secondsToTime(long seconds) {
        return millisToTime(seconds * 1000);
    }
}

