package org.docheinstein.commons.utils.arguments;

/**
 * Represents an argument.
 */
public interface Argument {

    /**
     * The type of parameter of an argument.
     */
    enum  ParameterType {
        /** The argument doesn't have any parameter. */
        None,

        /** The argument has just an argument. */
        Single,

        /** The argument has an undefined number of arguments. */
        Multiple
    }

    /**
     * Returns the type of the parameter(s) expected by this argument.
     * @return the type of parameter(s)
     */
    ParameterType getParameterType();

    /**
     * Returns the default value(s) that should be used for this argument
     * if not provided.
     * @return the default value(s) of this argument
     */
    String[] getDefaultValues();

    /**
     * Returns the name(s) used to identify this argument
     * @return the aliases of this argument
     */
    String[] getAliases();

    /**
     * Whether this argument is mandatory in the arguments set.
     * @return whether this argument is mandatory
     */
    boolean isMandatory();
}
