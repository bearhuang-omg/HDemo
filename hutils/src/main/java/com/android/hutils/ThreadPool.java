package com.android.hutils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {

    private static volatile ExecutorService cachedThreadPool = null;
    private static volatile ThreadPoolExecutor normalThreadPool = null;
    private static volatile ExecutorService singleThreadPool = null;
    private static volatile ScheduledExecutorService scheduledThreadPool = null;


    public static ExecutorService cachedPool() {
        if (cachedThreadPool == null) {
            synchronized (ThreadPool.class) {
                if (cachedThreadPool == null) {
                    cachedThreadPool = Executors.newCachedThreadPool();
                }
            }
        }
        return cachedThreadPool;
    }

    public static ThreadPoolExecutor normalPool() {
        if (normalThreadPool == null) {
            synchronized (ThreadPool.class) {
                if (normalThreadPool == null) {
                    int CPU_COUNT = Runtime.getRuntime().availableProcessors();
                    int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
                    int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
                    int KEEP_ALIVE_SECONDS = 30;
                    BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
                    ThreadFactory sThreadFactory = new ThreadFactory() {
                        private final AtomicInteger mCount = new AtomicInteger(1);

                        public Thread newThread(Runnable r) {
                            return new Thread(r, "threadPool #" + mCount.getAndIncrement());
                        }
                    };
                    normalThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
                            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
                    normalThreadPool.allowCoreThreadTimeOut(true);
                }
            }
        }
        return normalThreadPool;
    }

    public static ExecutorService singlePool() {
        if (singleThreadPool == null) {
            synchronized (ThreadPool.class) {
                if (singleThreadPool == null) {
                    singleThreadPool = Executors.newSingleThreadExecutor();
                }
            }
        }
        return singleThreadPool;
    }

    public static ScheduledExecutorService scheduledPool() {
        if (scheduledThreadPool == null) {
            synchronized (ThreadPool.class) {
                if (scheduledThreadPool == null) {
                    int CPU_COUNT = Runtime.getRuntime().availableProcessors();
                    int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
                    scheduledThreadPool = Executors.newScheduledThreadPool(CORE_POOL_SIZE);
                }
            }
        }
        return scheduledThreadPool;
    }

}
