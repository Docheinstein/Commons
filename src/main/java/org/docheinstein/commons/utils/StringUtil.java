package org.docheinstein.commons.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Provides utilities for Strings.
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
     * Returns whether the string is valid, i.e. not null nor empty.
     * @param str the string to check
     * @return whether the string is valid
     */
    public static boolean isValid(String str) {
        return str != null && !str.isEmpty();
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
