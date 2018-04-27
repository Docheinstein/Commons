package org.docheinstein.commons.utils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Internal logger used for debug the commons stuff.
 */
public class Logger {
    private static final Set<LoggerListener> sListeners = new CopyOnWriteArraySet<>();
    private static boolean sEnabled = false;

    public interface LoggerListener {
        void onLoggerMessage(String message);
    }

    public static void addListener(LoggerListener ll) {
        if (ll != null)
            sListeners.add(ll);
    }

    public static void removeListener(LoggerListener ll) {
        if (ll != null)
            sListeners.remove(ll);
    }

    public static void enable(boolean enabled) {
        sEnabled = enabled;
    }

    public static boolean isEnabled() {
        return sEnabled;
    }

    private String mTag;

    static Logger createForTag(String tag) {
        return new Logger(tag);
    }

    private Logger(String tag) {
        mTag = tag;
    }

    void out(String message) {
        if (sEnabled)
            sListeners.forEach(ll -> ll.onLoggerMessage(mTag + " " + message));
    }
}
