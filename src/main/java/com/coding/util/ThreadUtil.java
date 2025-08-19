package com.coding.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * thread util
 */
public class ThreadUtil {

    private ThreadUtil() {
        throw new UnsupportedOperationException(this + "cannot be instantiated");
    }

    public static ScheduledThreadPoolExecutor newScheduledExecutor(int corePoolSize, int maximumPoolSize,
                                                                   ThreadFactory threadFactory) {
        ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(
                corePoolSize, threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
        scheduledThreadPool.setMaximumPoolSize(maximumPoolSize);
        return scheduledThreadPool;
    }

}
