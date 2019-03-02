package org.docheinstein.commons.utils.time;

import org.docheinstein.commons.utils.types.StringUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Contains utilities for time. */
public class TimeUtil {

    public static final int MS_IN_SEC = 1000;
    public static final int SEC_IN_MIN = 60;
    public static final int MIN_IN_HOUR = 60;
    public static final int HOURS_IN_DAY = 24;

    public static final int MS_IN_MIN = MS_IN_SEC * SEC_IN_MIN;
    public static final int MS_IN_HOUR = MS_IN_MIN * MIN_IN_HOUR;
    public static final int MS_IN_DAY = MS_IN_HOUR * HOURS_IN_DAY;

    public static final int SEC_IN_HOUR = SEC_IN_MIN * MIN_IN_HOUR;
    public static final int SEC_IN_DAY = SEC_IN_HOUR * HOURS_IN_DAY;

    public static final int MIN_IN_DAY = MIN_IN_HOUR * HOURS_IN_DAY;

    /**
     * Contains some common patterns that can be used
     * for the methods of this class.
     */
    public static class Patterns {
        public static final String DATE_TIME_SLASH = "dd/MM/yyyy HH:mm:ss";
        public static final String DATE_TIME_DASH = "dd-MM-yyyy HH:mm:ss";
        public static final String TIME = "HH:mm:ss";
        public static final String DATE_SLASH = "dd/MM/yyyy";
        public static final String DATE_DASH = "dd-MM-yyyy";

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
         * Converts an amount of seconds to a time struct.
         * @param seconds the amount of millis
         * @return the time struct associated with the given amount
         *
         * @see #fromMillis(long)
         */
        public static TimeStruct fromSeconds(long seconds) {
            return fromMillis(seconds * 1000);
        }

        /**
         * Converts an amount of millis to a time struct.
         * @param millis the amount of millis
         * @return the time struct associated with the given amount
         */
        public static TimeStruct fromMillis(long millis) {
            return new TimeStruct(
                (int) (millis / MS_IN_HOUR),
                (int) (millis % MS_IN_HOUR / MS_IN_MIN),
                (int) (millis % MS_IN_MIN / MS_IN_SEC),
                (int) (millis % MS_IN_SEC)
            );
        }

        /**
         * Converts a time string formatted as 'HH:mm:ss[.ms]' to a time struct.
         * @param time the time string
         * @return the time struct associated with the given string
         */
        public static TimeStruct fromString(String time) {
            if (!StringUtil.isValid(time))
                return null;

            String[] components = time.split(":");
            if (components.length < 3)
                return null;

            String s_components[] = components[2].split("\\.");

            return new TimeStruct(
                Integer.parseInt(components[0]),
                Integer.parseInt(components[1]),
                Integer.parseInt(s_components[0]),
                s_components.length < 2 ? 0 : (int) (Double.parseDouble("." + s_components[1]) * 1000)
            );
        }

        /**
         * Converts this time to an amount of millis by summing the hours,
         * minutes, seconds and millis.
         * @return the amount of millis associated with this time
         */
        public long toMillis() {
            return
                millis +
                second * MS_IN_SEC +
                minute * MS_IN_MIN +
                hour * MS_IN_HOUR;
        }

        /**
         * Converts this time to an amount of seconds by summing the hours,
         * minutes and seconds
         * @return the amount of seconds associated with this time
         */
        public long toSeconds() {
            return
                second +
                minute * SEC_IN_MIN +
                hour * SEC_IN_HOUR;
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
                    (millis > 0 ? String.format(".%03d", millis ) : "")
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
     * Returns the string that is composed using the given pattern and date.
     *
     * @param pattern  the date/time pattern
     * @param datetime the datetime
     * @return the string that is composed using the given pattern and date
     */
    public static String datetimeToString(String pattern, LocalDateTime datetime) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        return datetime.format(f);
    }

    /**
     * Returns the string that is composed using the given pattern and date.
     * @param pattern the date pattern
     * @param date the date
     * @return the string that is composed using the given pattern and date
     */
    public static String dateToString(String pattern, LocalDate date) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        return date.format(f);
    }
}

