package org.docheinstein.commons.utils.thread;

import org.docheinstein.commons.internal.DocCommonsLogger;

/**
 * Provides utilities for threads
 */
public class ThreadUtil {
    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{THREAD_UTIL}");

    /**
     * Sleeps for a certain amount of ms.
     * <p>
     * Actually this is a convenient way to call {@link Thread#sleep(long)}
     * without a try/catch block
     * @param ms the millis amount
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            L.out("InterruptedException occurred" + e.getMessage());
        }
    }

    /**
     * Starts a new runnable task
     * <p>
     * Actually this is a convenient way to call new Thread(runnable).start().
     * @param runnable
     */
    public static void start(Runnable runnable) {
        new Thread(runnable).start();
    }
}
