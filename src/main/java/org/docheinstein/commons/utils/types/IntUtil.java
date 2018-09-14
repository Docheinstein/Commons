package org.docheinstein.commons.utils.types;

/**
 * Provides utilities for integers.
 */
public class IntUtil {

    /**
     * Returns 0 if the integer number is null, or the number itself otherwise.
     * @param integerMaybeNull the integer object
     * @return the given number or 0 if it is null
     */
    public static int unwrap0IfNull(Integer integerMaybeNull) {
        return unwrapIfNull(integerMaybeNull, 0);
    }

    /**
     * Returns 0 if the integer number is null or negative,
     * or the number itself otherwise.
     * @param integerMaybeNull the integer object
     * @return the given number or 0 if it is null or negative
     */
    public static int unwrap0IfNullOrNegative(Integer integerMaybeNull) {
        return unwrapIfNullOrNegative(integerMaybeNull, 0);
    }

    /**
     * Returns 0 if the integer number is null or negative,
     * or the number itself otherwise.
     * @param integerMaybeNull the integer object
     * @param valueIfNullOrNegative the value to return if the number is null or negative
     * @return the given number or 0 if it is null or negative
     */
    public static int unwrapIfNullOrNegative(Integer integerMaybeNull, int valueIfNullOrNegative) {
        return (integerMaybeNull == null || integerMaybeNull < 0) ? valueIfNullOrNegative : integerMaybeNull;
    }

    /**
     * Returns 0 if the integer number is null, or the number itself otherwise.
     * @param integerMaybeNull the integer object
     * @param valueIfNullOrNegative the value to return if the number is null
     * @return the given number or 0 if it is null
     */
    public static int unwrapIfNull(Integer integerMaybeNull, int valueIfNullOrNegative) {
        return integerMaybeNull == null ? valueIfNullOrNegative : integerMaybeNull;
    }
}
