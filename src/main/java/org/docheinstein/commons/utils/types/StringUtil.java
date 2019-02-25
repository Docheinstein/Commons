package org.docheinstein.commons.utils.types;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Provides utilities for strings.
 */
public class StringUtil {
    /**
     * Returns whether all the given strings are valid.
     * @param strs the string to check
     * @return whether the strings are valid
     */
    public static boolean areValid(String... strs) {
        for (String str : strs) {
            if (!isValid(str))
                return false;
        }
        return true;
    }

    /**
     * Returns whether at least one of the given strings is valid.
     * @param strs the strings to check
     * @return whether at least one string is valid
     */
    public static boolean atLeastOneIsValid(String... strs) {
        for (String str : strs) {
            if (isValid(str))
                return true;
        }
        return false;
    }

    /**
     * Returns whether the string is valid, i.e. not null nor empty.
     * @param str the string to check
     * @return whether the string is valid
     */
    public static boolean isValid(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * Returns the given string if it is valid or "" if it is null.
     * @param str the string to make empty if null
     * @return the given string if it is valid or "" if it is null
     */
    public static String toEmptyIfNull(String str) {
        return str == null ? "" : str;
    }

    /**
     * Returns the stack trace of the exception as a string.
     * @param e an exception
     * @return the stack trace of the exception
     */
    public static String toString(Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }


}
