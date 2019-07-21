package org.docheinstein.commons.thread;

import org.docheinstein.commons.internal.DocCommonsLogger;
import org.docheinstein.commons.types.StringUtil;

/**
 * Provides utilities for threads
 */
public class ThreadUtil {
    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{THREAD_UTIL}");

    public interface ThreadFailObserver {
        void onThreadException(Throwable throwable);
    }

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
     * @param runnable the runnable to run
     */
    public static void start(Runnable runnable) {
        startSafe(runnable, null);
    }

    /**
     * Starts a new runnable task
     * <p>
     * Actually this is a convenient way to call new Thread(runnable).start().
     * @param runnable the runnable to run
     * @param observer the optional observer that will be notified
     *                 if an exception occurs within the thread
     */
    public static void startSafe(Runnable runnable, ThreadFailObserver observer) {
        Thread t = new Thread(runnable);
        // Even if observer is null, print the thread fail as library message
        t.setUncaughtExceptionHandler((thread, exception) -> {
            L.out("Uncaught exception occurred in thread " + thread.getName() + "\n" +
                StringUtil.toString(exception));
            if (observer != null)
                observer.onThreadException(exception);
        });
        t.start();
    }
}
