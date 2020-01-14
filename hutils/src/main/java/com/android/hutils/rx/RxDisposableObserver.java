package com.android.hutils.rx;


import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class RxDisposableObserver<T> extends DisposableObserver<T> {

    private static final String TAG = "RxDisposableObserver";

    private RxTaskInfo taskInfo;

    public RxDisposableObserver() {
    }

    public RxDisposableObserver(RxTaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    @Override
    public void onNext(@NonNull T t) {
        Log.d(TAG, "onNext(): name='" + name(t));
    }

    @Override
    public void onError(@NonNull Throwable e) {
        Log.e(TAG, "onError(): name='" + name(null) + "', throwable='" + e + "'");
    }

    @Override
    public void onComplete() {
        Log.i(TAG, "onComplete(): name='" + name(null) + "'");
        // Auto dispose when onComplete (it's ok to dispose ahead of time)
        dispose();
    }

    private String name(T t) {
        return taskInfo != null ? taskInfo.toString() : t != null ? t.toString() : "missing name";
    }

    @Override
    public String toString() {
        return "RxDisposableObserver{" +
                "taskInfo='" + taskInfo + '\'' +
                '}';
    }
}
