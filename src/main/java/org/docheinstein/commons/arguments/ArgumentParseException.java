package org.docheinstein.commons.arguments;

/**
 * An exception thrown when an error occurs while trying to parse
 * the application's argument list.
 */
public class ArgumentParseException extends Exception {

    /**
     * The reason of this failure.
     */
    public enum FailReason {
        /**
         * A mandatory parameter is not provided.
         */
        MandatoryNotProvided,

        /**
         * The argument has less parameters then the expected number.
         */
        MissingParams,

        /**
         * The arguments has invalid parameters.
         */
        InvalidParams
    }

    private Argument mFailedArgument;
    private FailReason mFailReason;

    public ArgumentParseException(Argument failedArg, FailReason failReason) {
        mFailedArgument = failedArg;
        mFailReason = failReason;
    }

    /**
     * Returns the argument that caused this exception.
     * @return the failed argument
     */
    public Argument getFailedArgument() {
        return mFailedArgument;
    }

    /**
     * Returns the reason of the failure
     * @return the fail reason
     */
    public FailReason getFailReason() {
        return mFailReason;
    }
}
