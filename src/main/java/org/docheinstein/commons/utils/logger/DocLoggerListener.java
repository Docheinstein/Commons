package org.docheinstein.commons.utils.logger;

/**
 * Interface that can be implement for listen to messages produced
 * by {@link DocLogger}.
 */
public interface DocLoggerListener {
    void onLoggerMessage(DocLogger.LogLevel level, String message);
}
