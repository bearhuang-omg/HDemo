package com.android.hluncher;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.support.annotation.NonNull;
import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskPool {

    private static TaskPool instance = null;
    private ExecutorService cachedThreadPool = null;
    private Handler mainHandler = null;
    private ThreadPoolExecutor normalThreadPool = null;


    private TaskPool() {

    }

    private static TaskPool getInstance() {
        if (instance == null) {
            synchronized (TaskPool.class) {
                if (instance == null) {
                    instance = new TaskPool();
                }
            }
        }
        return instance;
    }

    public static Disposable execute(Task task) {
        if (task == null) {
            return new Disposable(null);
        }
        switch (task.getPriority()) {
            case Immediately:
                return new Disposable(getInstance().getCachedThreadPool().submit(task));
            case Normal:
                return new Disposable(getInstance().getNomalPool().submit(task));
            case Idle:
                if (VERSION.SDK_INT >= VERSION_CODES.M) {
                    return new Disposable(getInstance().idleRun(task));
                } else {
                    return new Disposable(getInstance().getNomalPool().submit(task));
                }
            case Main:
                return new Disposable(getInstance().getMainHandler().post(task));
            default:
                return new Disposable(null);
        }
    }

    public static Disposable execute(final Runnable runnable) {
        Task task = new Task() {

            @Override
            public void runTask() {
                runnable.run();
            }

            @Override
            public Priority getPriority() {
                return Priority.Immediately;
            }
        };
        return execute(task);
    }

    private IdleHandler idleRun(final Task task) {
        IdleHandler idleHandler = new IdleHandler() {
            @Override
            public boolean queueIdle() {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            Looper.getMainLooper().getQueue().addIdleHandler(idleHandler);
        }
        return idleHandler;
    }

    private Handler getMainHandler() {
        if (mainHandler == null) {
            synchronized (TaskPool.class) {
                if (mainHandler == null) {
                    mainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mainHandler;
    }

    public static class Disposable {

        private WeakReference<Object> object;

        private Disposable(Object obj) {
            this.object = new WeakReference<>(obj);
        }

        public void dispose() {
            try {
                if (object != null && object.get() != null) {
                    if (object.get() instanceof Runnable) {
                        getInstance().getMainHandler().removeCallbacks((Runnable) object.get());
                    } else if (object.get() instanceof Future) {
                        ((Future) object.get()).cancel(true);
                    } else if (object.get() instanceof IdleHandler) {
                        if (VERSION.SDK_INT >= VERSION_CODES.M) {
                            Looper.getMainLooper().getQueue().removeIdleHandler((IdleHandler) object.get());
                        }
                    }
                    object = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //自动释放
        public void autoDispose(Lifecycle lifecycle) {
            if (lifecycle != null) {
                lifecycle.addObserver(new GenericLifecycleObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            dispose();
                        }
                    }
                });
            }
        }
    }

    private ExecutorService getCachedThreadPool() {
        if (cachedThreadPool == null) {
            synchronized (TaskPool.class) {
                if (cachedThreadPool == null) {
                    cachedThreadPool = Executors.newCachedThreadPool();
                }
            }
        }
        return cachedThreadPool;
    }

    private ThreadPoolExecutor getNomalPool() {
        if (normalThreadPool == null) {
            synchronized (TaskPool.class) {
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
}