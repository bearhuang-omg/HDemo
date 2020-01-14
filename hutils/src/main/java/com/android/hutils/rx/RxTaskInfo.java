package com.android.hutils.rx;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

public final class RxTaskInfo{

    private static final String TAG = "RxTaskInfo";

    public static final int PRIORITY_IMMEDIATE = 10;
    public static final int PRIORITY_HIGH = 7;
    public static final int PRIORITY_NORMAL = 5;
    public static final int PRIORITY_LOW = 3;
    public static final int PRIORITY_BACKGROUND = 1;

    private final String name;
    private final int priority;
    private final long time;
    private final LifecycleOwner lifecycleOwner;
    private final Lifecycle.Event event;
    private List<LifecycleObserver> lifecycleObserverList;

    private RxTaskInfo(Builder builder) {
        name = builder.name;
        priority = builder.priority;
        time =  builder.time;
        lifecycleOwner = builder.lifecycleOwner;
        event = builder.event;
        lifecycleObserverList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public long getTime() {
        return time;
    }

    public Lifecycle.Event getEvent() {
        return event;
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    public static RxTaskInfo immediate(String name) {
        return new Builder().name(name).priority(PRIORITY_IMMEDIATE)
                .time(System.currentTimeMillis()).build();
    }

    public static RxTaskInfo high(String name) {
        return new Builder().name(name).priority(PRIORITY_HIGH)
                .time(System.currentTimeMillis()).build();
    }

    public static RxTaskInfo normal(String name) {
        return new Builder().name(name).priority(PRIORITY_NORMAL)
                .time(System.currentTimeMillis()).build();
    }

    public static RxTaskInfo normal(String name,LifecycleOwner lifecycleOwner,Lifecycle.Event event) {
        return new Builder().name(name).priority(PRIORITY_NORMAL).lifecycleOwner(lifecycleOwner).event(event)
                .time(System.currentTimeMillis()).build();
    }

    public static RxTaskInfo low(String name) {
        return new Builder().name(name).priority(PRIORITY_LOW)
                .time(System.currentTimeMillis()).build();
    }

    public static RxTaskInfo background(String name) {
        return new Builder().name(name).priority(PRIORITY_BACKGROUND)
                .time(System.currentTimeMillis()).build();
    }

    public static final class Builder {
        private String name;
        private int priority;
        private long time;
        private LifecycleOwner lifecycleOwner;
        private Lifecycle.Event event;

        public Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder priority(int val) {
            priority = val;
            return this;
        }

        public Builder time(long val) {
            time = val;
            return this;
        }

        public Builder lifecycleOwner(LifecycleOwner val){
            lifecycleOwner = val;
            return this;
        }

        public Builder event(Lifecycle.Event val){
            event = val;
            return this;
        }

        public RxTaskInfo build() {
            return new RxTaskInfo(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RxTaskInfo that = (RxTaskInfo) o;

        if (priority != that.priority) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + priority;
        return result;
    }

    @Override
    public String toString() {
        return "RxTaskInfo{" +
                "name='" + name + '\'' +
                ", priority=" + priority +
                ", time=" + time +
                '}';
    }

    public void addLifecycleObserver(LifecycleObserver lifecycleObserver){
        if(this.lifecycleOwner != null){
            lifecycleObserverList.add(lifecycleObserver);
            this.lifecycleOwner.getLifecycle().addObserver(lifecycleObserver);
        }
    }

    public void removeLifecycleObserver(){
        if(this.lifecycleOwner != null){
            for(LifecycleObserver observer : lifecycleObserverList){
                lifecycleOwner.getLifecycle().removeObserver(observer);
            }
        }
    }
}