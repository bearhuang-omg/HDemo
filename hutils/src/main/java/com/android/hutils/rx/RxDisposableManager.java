package com.android.hutils.rx;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class RxDisposableManager {

    private static final String TAG = "RxDisposableManager";

    // Warning: should not be submitted with true
    private static final boolean DEBUG = false;

    private static class RxDisposableManagerImpl {

        private static class SingletonHolder {
            private static final RxDisposableManagerImpl INSTANCE = new RxDisposableManagerImpl();
        }

        private static RxDisposableManagerImpl getInstance() {
            return SingletonHolder.INSTANCE;
        }

        private RxDisposableManagerImpl() {}

        private Map<String, CompositeDisposable> mTaggedCompositeDisposables = new ConcurrentHashMap<>();
        private Map<String, List<Disposable>> mTaggedDisposables = new ConcurrentHashMap<>();

        private void add(String tag, Disposable disposable) {
            Log.d(TAG, "[ ADD ]: tag=" + tag + ", disposable=" + disposable);
            CompositeDisposable compositeDisposable = mTaggedCompositeDisposables.get(tag);
            if (compositeDisposable == null) {
                compositeDisposable = new CompositeDisposable();
                mTaggedCompositeDisposables.put(tag, compositeDisposable);
            }
            compositeDisposable.add(disposable);

            if (DEBUG) {
                List<Disposable> disposables = mTaggedDisposables.get(tag);
                if (disposables == null) {
                    disposables = new ArrayList<>();
                    mTaggedDisposables.put(tag, disposables);
                }
                disposables.add(disposable);
            }
        }

        private void dispose(String tag) {
            Log.d(TAG, "[ DISPOSE ]: tag=" + tag);
            CompositeDisposable compositeDisposable = mTaggedCompositeDisposables.get(tag);
            if (compositeDisposable != null) {
                compositeDisposable.clear();
                mTaggedCompositeDisposables.remove(tag);
            }

            if (DEBUG) {
                List<Disposable> disposables = mTaggedDisposables.get(tag);
                if (disposables != null) {
                    disposables.clear();
                }
            }
        }

        private void dump() {
            if (DEBUG) {
                for (String tag : mTaggedDisposables.keySet()) {
                    List<Disposable> disposables = mTaggedDisposables.get(tag);
                    Log.d(TAG, "===== Disposables for " + tag + "=====");
                    for (Disposable disposable : disposables) {
                        if (!disposable.isDisposed()) {
                            Log.d(TAG, "disposable: " + disposable);
                        }
                    }
                }
            }
        }
    }

    public static void add(String tag, Disposable disposable) {
        RxDisposableManagerImpl.getInstance().add(tag, disposable);
    }

    public static void dispose(String tag) {
        RxDisposableManagerImpl.getInstance().dispose(tag);
    }

    public static void submitRunnable(String tag, RxTaskInfo taskInfo, Runnable background) {
        add(tag, Observable
                .fromCallable(() -> {
                    background.run();
                    return true;
                })
                .compose(RxSchedulers.applySchedulers(taskInfo))
                .subscribeWith(new RxDisposableObserver<>(taskInfo)));
    }

    public static void submitRunnable(String tag, RxTaskInfo taskInfo,
                                      Runnable background, Runnable complete) {
        add(tag,Observable
                .fromCallable(() -> {
                    background.run();
                    return true;
                })
                .compose(RxSchedulers.applySchedulers(taskInfo))
                .subscribeWith(new RxDisposableObserver<Boolean>(taskInfo) {
                    @Override
                    public void onComplete() {
                        complete.run();
                        super.onComplete();
                    }
                }));
    }

    public static void dump() {
        RxDisposableManagerImpl.getInstance().dump();
    }

    public static String createRxTag(Object obj) {
        return String.format("%s_%d", obj.getClass().getSimpleName(), obj.hashCode());
    }
}