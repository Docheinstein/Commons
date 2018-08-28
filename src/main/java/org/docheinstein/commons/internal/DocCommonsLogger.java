package org.docheinstein.commons.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Internal logger used for debug.
 */
public class DocCommonsLogger {
    private static final Set<LoggerListener> sListeners = new CopyOnWriteArraySet<>();
    private static boolean sEnabled = false;
    private String mTag;

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

    public static DocCommonsLogger createForTag(String tag) {
        return new DocCommonsLogger(tag);
    }

    private DocCommonsLogger(String tag) {
        mTag = tag;
    }

    public void out(String message) {
        if (sEnabled)
            sListeners.forEach(ll -> ll.onLoggerMessage(mTag + " " + message));
    }
}
