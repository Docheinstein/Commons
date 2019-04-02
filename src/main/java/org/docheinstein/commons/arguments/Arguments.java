package org.docheinstein.commons.arguments;

import org.docheinstein.commons.internal.DocCommonsLogger;

import java.util.*;

public class Arguments {
    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{ARGUMENTS}");

    // Maps known arguments' aliases to associated arguments
    private final Map<String, Argument> mKnownArguments = new HashMap<>();

    // Maps found arguments to associated given params
    private Map<Argument, List<String>> mArgs = new HashMap<>();

    /**
     * Creates an arguments object for the given application's argument list
     * and known argument list.
     * @param args the application argument list of strings
     * @param knownArguments the known argument list
     * @param throwIfMandatoryIsMissing throws an exception if a mandatory parameter
     *                                is missing; this can also be done manually by
     *                                calling {@link #throwIfMandatoryArgumentIsMissing()}
     * @throws ArgumentParseException if an exception occurs while parsing the argument list
     */
    public Arguments(String[] args, Argument[] knownArguments,
                     boolean throwIfMandatoryIsMissing) throws ArgumentParseException {
        createKnownArgumentsMap(knownArguments);
        parse(args, throwIfMandatoryIsMissing);
    }

    /**
     * Returns the parameter list for the given argument or null if the
     * argument was not present in the argument list.
     * @param argument the argument for which retrieve the params
     * @return the parameters of the argument
     */
    public List<String> getParameters(Argument argument) {
        return mArgs.getOrDefault(
            argument,
            argument.getDefaultValues() == null ?
                new ArrayList<>() :
                Arrays.asList(argument.getDefaultValues())
        );
    }

    /**
     * Returns the first parameter for the given argument or null if the
     * argument was not present in the argument list
     * @param argument the argument for which retrieve the param
     * @return the (first) parameter of the argument
     */
    public String getParameter(Argument argument) {
        List<String> params = getParameters(argument);
        if (params.size() > 0)
            return params.get(0);
        return null;
    }

    /**
     * Returns true if the arguments list contains the given argument,
     * false otherwise
     * @param argument the argument to check
     * @return whether the given argument is present in the argument list
     */
    public boolean contains(Argument argument) {
        return mArgs.containsKey(argument);
    }

    /**
     * Throws an exception if a mandatory argument has not been provided.
     * @throws ArgumentParseException if a mandatory argument is not provided
     */
    public void throwIfMandatoryArgumentIsMissing() throws ArgumentParseException {
        for (Argument arg : mKnownArguments.values()) {
            if (arg.isMandatory() && !mArgs.containsKey(arg))
                throw new ArgumentParseException(
                    arg,
                    ArgumentParseException.FailReason.MandatoryNotProvided
                );
        }
    }


    /**
     * Creats a map that associated the arguments' aliases to the arguments.
     * @param knownArguments the known argument list for which create the map
     */
    private void createKnownArgumentsMap(Argument[] knownArguments) {
        for (Argument arg : knownArguments) {
            for (String alias : arg.getAliases())
                mKnownArguments.put(alias, arg);
        }
    }

    /**
     * Parses the arguments list and creates the map that will contain
     * the arguments' params.
     * @param args the argument list
     * @param throwIfMandatoryIsMissing throws an exception if a mandatory parameter
     *                                is missing; this can also be done manually by
     *                                calling {@link #throwIfMandatoryArgumentIsMissing()}
     * @throws ArgumentParseException if an exception occurs while parsing the argument list
     *                                or if a mandatory parameter is missing (if
     *                                throwIfMandatoryIsMissing is true)
     */
    private void parse(String[] args, boolean throwIfMandatoryIsMissing) throws ArgumentParseException {
        mArgs = new HashMap<>();

        // For each given argument check if it matches one of the
        // known one and take the parameters after it based on its type
        for (int i = 0; i < args.length; i++) {
            String currentArgString = args[i];

            Argument currentArg = mKnownArguments.get(currentArgString);
            if (currentArg == null) {
                L.out("Unknown argument found: " + currentArgString + " skipping it");
                continue;
            }

            L.out("Found known argument: " + currentArgString);

            switch (currentArg.getParameterType()) {
                case None:
                    mArgs.put(currentArg, null);
                    break;
                case Single:
                    List<String> singleParam = new ArrayList<>();
                    i++;

                    // Ensure that the next token is a valid param
                    if ((i >= args.length || mKnownArguments.containsKey(args[i]))) {
                        L.out("Found a 'single param' argument with no param");
                        throw new ArgumentParseException(
                            currentArg,
                            ArgumentParseException.FailReason.MissingParams
                        );
                    }

                    L.out("|__ param: " + args[i]);
                    singleParam.add(args[i]);
                    mArgs.put(currentArg, singleParam);
                    break;
                case Multiple:
                    // Consider each token after this arg as a param till
                    // a known arg is found (or there are no more arg to parse).
                    // In order not to scan every parameter, only '-' is checked
                    // at the begin of the token.
                    List<String> multipleParams = new ArrayList<>();

                    i++;

                    while (i < args.length && !mKnownArguments.containsKey(args[i])) {
                        L.out("|__ param: " + args[i]);
                        multipleParams.add(args[i]);
                        i++;
                    }

                    // Note that for 'Multiple' is allowed to have 0 params

                    mArgs.put(currentArg, multipleParams);

                    if (i < args.length /* ==> args[i].charAt(0) is '-' */) {
                        i--;
                        // We have read a valid argument instead of a param;
                        // Decrease in order to reconsider it during the next cycle.
                    }
                    break;
            }
        }

        if (throwIfMandatoryIsMissing) {
            // Check that every mandatory parameter is provided
             throwIfMandatoryArgumentIsMissing();
        }
    }
}
