package org.docheinstein.commons.utils.logger;


import org.docheinstein.commons.utils.time.TimeUtil;
import org.docheinstein.commons.utils.types.StringUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Represents a logger associated with a tag (usually a class) that
 * is able to print messages with different log levels.
 */
public class DocLogger implements LoggerCapable {

    /** Level of logging. */
    public enum LogLevel {
        Verbose("V", System.out),
        Debug("D", System.out),
        Info("I", System.out),
        Warn("W", System.err),
        Error("E", System.err)
        ;

        LogLevel(String identifier, PrintStream stream) {
            this.identifier = identifier;
            this.stream = stream;
        }

        private String identifier;
        private PrintStream stream;
    }

    static {
        sEnabledLogLevelsOnStream = new HashSet<>();
        sEnabledLogLevelsOnFile = new HashSet<>();

        // Enables all the log level by default
        for (LogLevel lv : LogLevel.values())
            enableLogLevel(lv, true);
    }

    /**
     * Creates a logger and uses the class name for build the logger's tag.
     * @param clazz the class to use for define the logger tag
     * @return a logger with a tag associated with the given class
     *
     * @see #createForTag(String)
     */
    public static DocLogger createForClass(Class clazz) {
        return createForTag(getTagForClass(clazz));
    }

    /**
     * Creates a logger with the given tag.
     * @param tag the tag of the logger
     * @return a logger with the given tag
     *
     * @see #createForClass(Class)
     */
    public static DocLogger createForTag(String tag) {
        return new DocLogger(tag);
    }

    /**
     * Returns a tag for the given class.
     * <p>
     * This is actually created by making all upper case, separating the
     * upper case characters with underscores.
     * @param clazz the class to use for define the tag
     * @return the tag associated with the given class
     */
    public static String getTagForClass(Class clazz) {
        return "{" +
            clazz.getSimpleName().replaceAll(
                "(.)([A-Z]+)",
                "$1_$2").toUpperCase() +
            "}";
    }

    public static void enableLogLevel(LogLevel level,
                                      boolean enable) {
        enableLogLevel(level, enable, enable);
    }

    public static void enableLogLevel(LogLevel level,
                                      boolean enableOnStream,
                                      boolean enableOnFile) {
        if (enableOnStream)
            sEnabledLogLevelsOnStream.add(level);
        else
            sEnabledLogLevelsOnStream.remove(level);

        if (enableOnFile)
            sEnabledLogLevelsOnFile.add(level);
        else
            sEnabledLogLevelsOnFile.remove(level);
    }

    public static boolean isLogLevelEnabledOnStream(LogLevel level) {
        return sEnabledLogLevelsOnStream.contains(level);
    }

    public static boolean isLogLevelEnabledOnFile(LogLevel level) {
        return sEnabledLogLevelsOnFile.contains(level);
    }

    /**
     * Enables logging on files.
     * @param folder the folder where the logs files will be saved to
     * @param fileNameSupplier the supplier of the current log file name
     * @param flush whether the log file should be flushed after each write
     */
    public static void enableLoggingOnFiles(File folder,
                                            Supplier<String> fileNameSupplier,
                                            boolean flush) {
        sLogsFolder = folder;
        sLoggingFileNameSupplier = fileNameSupplier;
        sFlush = flush;
        initLoggingFileWriter();
    }

    /** Disables logging on files. */
    public static void disableLoggingOnFiles() {
        sLogsFolder = null;
        sLoggingFileNameSupplier = null;
    }

    /**
     * Returns whether the logging on files is enabled
     * @return whether this logger should write to file
     */
    public static boolean isLoggingOnFilesEnabled() {
        return sLogsFolder != null;
    }

    /**
     * Returns the global logger that is not associated with any tag
     * or class.
     * <p>
     * The use of this logger is not encouraged since the information
     * about the entity that prints the messages is lost.
     * @return the application global logger
     */
    public static DocLogger global() {
        return GLOBAL_LOGGER;
    }

    /** Application's global logger. */
    private static final DocLogger GLOBAL_LOGGER = createForTag("{GLOBAL}");

    /** Enabled log levels ofr logging via stdout/stderr */
    private static final Set<LogLevel> sEnabledLogLevelsOnStream;

    /** Enabled log levels for logging on files */
    private static final Set<LogLevel> sEnabledLogLevelsOnFile;

    /** Folder of the log files */
    private static File sLogsFolder;

    /** Supplier of the current logging file. */
    private static Supplier<String> sLoggingFileNameSupplier;

    /** Current logging file name the writer writes to. */
    private static String sCurrentLoggingFileName;

    /** The writer responsible for write the message to the logging file. */
    private static BufferedWriter sWriter;

    /** Whether the logging file should be flushed after each write */
    private static boolean sFlush;

    /** The tag of this logger. */
    private final String mTag;

    /**
     * Creates a logger for the given tag
     * @param tag the tag of the logger
     */
    private DocLogger(String tag) {
        mTag = tag;
    }

    // Basic LoggerCapable log methods

    /**
     * Prints the given message as a verbose message.
     * @param message the message
     */
    @Override
    public void verbose(String message) {
        log(mTag, LogLevel.Verbose, message);
    }

    /**
     * Prints the given message as a debug message.
     * @param message the message
     */
    @Override
    public void debug(String message) {
        log(mTag, LogLevel.Debug, message);
    }

    /**
     * Prints the given message as an info message.
     * @param message the message
     */
    @Override
    public void info(String message) {
        log(mTag, LogLevel.Info, message);
    }

    /**
     * Prints the given message as a warn message.
     * @param message the message
     */
    @Override
    public void warn(String message) {
        log(mTag, LogLevel.Warn, message);
    }

    /**
     * Prints the given message as an error message.
     * @param message the message
     */
    @Override
    public void error(String message) {
        log(mTag, LogLevel.Error, message);
    }

    // Advanced log methods

    /**
     * Prints the given message and exception as a warn message.
     * @param message the message
     * @param e the exception
     */
    public void warn(String message, Exception e) {
        warn(message + "\n" + StringUtil.toString(e));
    }

    /**
     * Prints the given message and exception as an error message.
     * @param message the message
     * @param e the exception
     */
    public void error(String message, Exception e) {
        error(message + "\n" + StringUtil.toString(e));
    }

    /**
     * Prints the given message for the given log level using the given tag.
     * <p>
     * Eventually prints the message to the logging file, if logging on files
     * is enabled.
     * @param tag the tag
     * @param lv the log level
     * @param message the message
     */
    private static void log(String tag, LogLevel lv, String message) {
        String logTime = TimeUtil.millisToString(
            TimeUtil.Patterns.DATE_TIME,
            System.currentTimeMillis()
        );

        String logMessage = "[" + lv.identifier + "] " + logTime + " " + tag + " " + message;

        // Logging on stream

        if (isLogLevelEnabledOnStream(lv))
            lv.stream.println(logMessage);

        // Logging on files

        if (isLoggingOnFilesEnabled() && isLogLevelEnabledOnFile(lv)) {
            handleLoggingFileRoll();

            try {
                sWriter.write(logMessage);
                if (sFlush)
                    sWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes the writer responsible for prints the log messages to the
     * logging file provided by {@link #sLoggingFileNameSupplier}.
     */
    private static void initLoggingFileWriter() {
        try {
            sCurrentLoggingFileName = sLoggingFileNameSupplier.get();
            FileWriter fw = new FileWriter(new File(sLogsFolder, sCurrentLoggingFileName), true);
            if (sWriter != null)
                sWriter.close();
            sWriter = new BufferedWriter(fw, 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the logging file name is changed; if so initializes
     * the writer responsible for print the messages to the logging file with
     * the new logging file.
     */
    private static void handleLoggingFileRoll() {
        String newLoggingFileName = sLoggingFileNameSupplier.get();

        if (!newLoggingFileName.equals(sCurrentLoggingFileName))
            initLoggingFileWriter();
    }
}

