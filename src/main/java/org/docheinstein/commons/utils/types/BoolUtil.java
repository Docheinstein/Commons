package org.docheinstein.commons.utils.types;

/**
 * Provides utilities for booleans.
 */
public class BoolUtil {
    /**
     * Returns whether the given boolean object is not null and true.
     * @param booleanMaybeNull the boolean object
     * @return whether the given boolean object is not null and true
     */
    public static boolean unwrapFalseIfNull(Boolean booleanMaybeNull) {
        return booleanMaybeNull != null && booleanMaybeNull;
    }

    /**
     * Returns whether the given boolean object is null or true.
     * @param booleanMaybeNull the boolean object
     * @return whether the given boolean object is null or true
     */
    public static boolean unwrapTrueIfNull(Boolean booleanMaybeNull) {
        return booleanMaybeNull == null || booleanMaybeNull;
    }

    /**
     * Returns 'YES' if true, or 'NO' if false.
     * @param b the boolean
     * @return the appropriate string
     */
    public static String toYesNo(boolean b) {
        return toString(b, "YES", "NO");
    }

    /**
     * Returns the yes string if true, or the no string if false.
     * @param b the boolean
     * @return the appropriate string
     */
    public static String toString(boolean b, String yes, String no) {
        return b ? yes : no;
    }
}
