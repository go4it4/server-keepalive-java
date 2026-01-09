package com.coding.util;

import jakarta.annotation.Nonnull;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * thread util
 */
public class ThreadUtil {

    private ThreadUtil() {
        throw new UnsupportedOperationException(this + "cannot be instantiated");
    }

    private static class ThreadUtilThreadFactory implements ThreadFactory {

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public ThreadUtilThreadFactory() {
            group = Thread.currentThread().getThreadGroup();
            namePrefix = "thread-util-";
        }

        public Thread newThread(@Nonnull Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    private static class SingletonHolder {
        private static final ExecutorService INSTANCE = new ThreadPoolExecutor(1, 2,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(64),
                new ThreadUtilThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * 获取线程池, 得到线程池单例对象
     *
     * @return ExecutorService
     */
    public static ExecutorService getExecutor() {
        return SingletonHolder.INSTANCE;
    }

    public static ScheduledThreadPoolExecutor newScheduledExecutor(int corePoolSize, int maximumPoolSize,
                                                                   ThreadFactory threadFactory) {
        ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(
                corePoolSize, threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
        scheduledThreadPool.setMaximumPoolSize(maximumPoolSize);
        return scheduledThreadPool;
    }

    public static <U> U executeTimeout(Supplier<U> supplier, Duration timeout) {
        CompletableFuture<U> future = CompletableFuture.supplyAsync(supplier, getExecutor());
        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
            return null;
        }
    }
}
