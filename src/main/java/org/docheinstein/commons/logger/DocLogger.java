package org.docheinstein.commons.logger;


import org.docheinstein.commons.time.TimeUtil;
import org.docheinstein.commons.types.StringUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;

/**
 * Represents a logger associated with a tag (usually a class) that
 * is able to print messages with different log levels.
 */
public class DocLogger implements LoggerCapable {

    /** Level of logging. */
    public enum LogLevel {
        Debug("D", System.out),
        Verbose("V", System.out),
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

        // Flush before quit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            BufferedWriter writer = getLoggingFileWriter();
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
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
     * This is actually created by making all upper case,
     * separating the sequence of upper case characters with underscores.
     * @param clazz the class to use for define the tag
     * @return the tag associated with the given class
     */
    public static String getTagForClass(Class clazz) {
        String tag = clazz.getSimpleName().replaceAll(
            "([A-Z]*)([A-Z])([a-z]|[0-9])?",
            "$1_$2$3"
        ).toUpperCase();

        return tag.startsWith("_") ?
            "{" + tag.substring(1) + "}" :
            "{" + tag + "}";
    }

    /**
     * Enables/disables the given log level for stream and files logging.
     * @param level the level
     * @param enable whether the level should be enabled for both streams and files
     * @see #enableLogLevel(LogLevel, boolean, boolean)
     */
    public static void enableLogLevel(LogLevel level,
                                      boolean enable) {
        enableLogLevel(level, enable, enable);
    }

    /**
     * Enables/disables the given log level for stream and/or files logging.
     * @param level the level
     * @param enableOnStream whether the level should be enabled on streams
     * @param enableOnFile whether the level should be enabled on files
     */
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

    /**
     * Returns whether the given log level is enabled for be printed on stdout/stderr.
     * @param level the level to check
     * @return whether the messages of the given log level are printed on streams.
     */
    public static boolean isLogLevelEnabledOnStream(LogLevel level) {
        return sEnabledLogLevelsOnStream.contains(level);
    }

    /**
     * Returns whether the given log level is enabled for be printed on files.
     * @param level the level to check
     * @return whether the messages of the given log level are printed on files.
     */
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
     * Adds the listener that will be notified when a new message is produced
     * by this logger.
     * @param listener the listener
     */
    public static void addListener(DocLoggerListener listener) {
        if (listener != null)
            sListeners.add(listener);
    }

    /**
     * Removes the listener from the listener set of this logger's messages.
     * @param listener the listener
     */
    public static void removeListener(DocLoggerListener listener) {
        sListeners.remove(listener);
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

    private static Set<DocLoggerListener> sListeners = new CopyOnWriteArraySet<>();

    /** The tag of this logger. */
    private final String mTag;

    /**
     * Creates a logger for the given tag
     * @param tag the tag of the logger
     */
    private DocLogger(String tag) {
        mTag = tag;
    }

    // Static log methods

    /**
     * Prints the given message as a debug message.
     * @param tag the tag of the entity that produces this message
     * @param message the message
     */
    public static void debug(String tag, String message) {
        log(tag, LogLevel.Debug, message);
    }

    /**
     * Prints the given message as a verbose message.
     * @param tag the tag of the entity that produces this message
     * @param message the message
     */
    public static void verbose(String tag, String message) {
        log(tag, LogLevel.Verbose, message);
    }

    /**
     * Prints the given message as an info message.
     * @param tag the tag of the entity that produces this message
     * @param message the message
     */
    public static void info(String tag, String message) {
        log(tag, LogLevel.Info, message);
    }

    /**
     * Prints the given message as a warn message.
     * @param tag the tag of the entity that produces this message
     * @param message the message
     */
    public static void warn(String tag, String message) {
        log(tag, LogLevel.Warn, message);
    }

    /**
     * Prints the given message as an error message.
     * @param tag the tag of the entity that produces this message
     * @param message the message
     */
    public static void error(String tag, String message) {
        log(tag, LogLevel.Error, message);
    }

    /**
     * Prints the given message as a warn message.
     * @param message the message
     * @param e the exception to print
     */
    public static void warn(String tag, String message, Exception e) {
        log(tag, LogLevel.Warn, message + "\n" + StringUtil.toString(e));
    }

    /**
     * Prints the given message as an error message.
     * @param tag the tag of the entity that produces this message
     * @param message the message
     * @param e the exception to print
     */
    public static void error(String tag, String message, Exception e) {
        log(tag, LogLevel.Error, message + "\n" + StringUtil.toString(e));
    }

    /**
     * Flushes the file log right now.
     */
    public static void flush() {
        if (sWriter != null) {
            try {
                sWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Basic LoggerCapable log methods

    /**
     * Prints the given message as a debug message.
     * @param message the message
     */
    @Override
    public void debug(String message) {
        debug(mTag, message);
    }

    /**
     * Prints the given message as a verbose message.
     * @param message the message
     */
    @Override
    public void verbose(String message) {
        verbose(mTag, message);
    }

    /**
     * Prints the given message as an info message.
     * @param message the message
     */
    @Override
    public void info(String message) {
        info(mTag, message);
    }

    /**
     * Prints the given message as a warn message.
     * @param message the message
     */
    @Override
    public void warn(String message) {
        warn(mTag, message);
    }

    /**
     * Prints the given message as an error message.
     * @param message the message
     */
    @Override
    public void error(String message) {
        error(mTag, message);
    }

    // Advanced log methods

    /**
     * Prints the given message and exception as a warn message.
     * @param message the message
     * @param e the exception
     */
    public void warn(String message, Exception e) {
        warn(mTag, message, e);
    }

    /**
     * Prints the given message and exception as an error message.
     * @param message the message
     * @param e the exception
     */
    public void error(String message, Exception e) {
        error(mTag, message, e);
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
        boolean onStream = isLogLevelEnabledOnStream(lv);
        boolean onFile = isLoggingOnFilesEnabled() && isLogLevelEnabledOnFile(lv);

        if (!onStream && !onFile)
            // Nothing to print
            return;

        String logDate = TimeUtil.millisToString(
            TimeUtil.Patterns.DATE_TIME_SLASH,
            System.currentTimeMillis()
        );

        String logMessageNoLevel = logDate + " " + tag + " " + message;
        String logMessage = "[" + lv.identifier + "] " + logMessageNoLevel;

        // Logging on stream

        if (onStream)
            lv.stream.println(logMessage);

        // Logging on files

        if (onFile) {
            handleLoggingFileRoll();

            try {
                sWriter.write(logMessage);
                sWriter.newLine();
                if (sFlush)
                    sWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Notify listeners

        for (DocLoggerListener listener : sListeners)
            listener.onLoggerMessage(lv, logMessageNoLevel);
    }

    /**
     * Initializes the writer responsible for prints the log messages to the
     * logging file provided by {@link #sLoggingFileNameSupplier}.
     */
    private static void initLoggingFileWriter() {
        try {
            sCurrentLoggingFileName = sLoggingFileNameSupplier.get();
            FileWriter fw = new FileWriter(
                new File(sLogsFolder, sCurrentLoggingFileName), true);
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

    private static BufferedWriter getLoggingFileWriter() {
        return sWriter;
    }
}

