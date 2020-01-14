package com.android.hutils.rx;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public final class RxSchedulers {

    private RxSchedulers() {}

    public static <T> ObservableTransformer<T, T> applySchedulers(RxTaskInfo taskInfo) {
        if (taskInfo.getPriority() > RxTaskInfo.PRIORITY_IMMEDIATE
                || taskInfo.getPriority() < RxTaskInfo.PRIORITY_BACKGROUND) {
            throw new IllegalArgumentException("Priority should be between" +
                    " RxTaskInfo.PRIORITY_BACKGROUND and RxTaskInfo.PRIORITY_IMMEDIATE!");
        }
        // Only tasks with immediate priority should fire new threads.
        // Other tasks may be long running and block the thread pool execution,
        // even the high priority tasks will be waiting for the submitted ones to finish.
        if (taskInfo.getPriority() == RxTaskInfo.PRIORITY_IMMEDIATE) {
            return upstream -> upstream.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
		// TODO 此处暂未实现优先级schedulers

            PublishSubject<Lifecycle.Event> publishSubject = PublishSubject.create();
            if(taskInfo.getLifecycleOwner() != null){
                taskInfo.addLifecycleObserver(new LifecycleObserver() {
                    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
                    public void onAny(LifecycleOwner source, Lifecycle.Event event) { //两个参数
                        Log.i("测试", "Observer【onAny】" + event.name());
                        if(taskInfo.getEvent() == event){
                            Log.i("测试", "dispose" + event.name());
                            publishSubject.onNext(event);
                            taskInfo.removeLifecycleObserver();
                        }
                    }
                });
            }

//            return upstream -> upstream.takeUntil(publishSubject)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.io());

            return new ObservableTransformer<T, T>() {
                @Override
                public ObservableSource<T> apply(Observable<T> upstream) {
                    Observable observable = upstream.subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io()).takeUntil(publishSubject);
                    return observable;
                }
            };
//            return upstream -> upstream
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread());
        }
    }
}
