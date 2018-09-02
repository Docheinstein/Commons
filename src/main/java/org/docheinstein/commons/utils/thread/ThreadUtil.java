package org.docheinstein.commons.utils.thread;

import org.docheinstein.commons.internal.DocCommonsLogger;

public class ThreadUtil {
    private static final DocCommonsLogger L = DocCommonsLogger.createForTag("{THREAD_UTIL}");

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            L.out("InterruptedException occurred" + e.getMessage());
        }
    }

    public static void start(Runnable runnable) {
        new Thread(runnable).start();
    }
}
