package com.android.hutils;

public abstract class ThreadSingleton<T> {

    private ThreadLocal<T> instance = new ThreadLocal<T>() {
        @Override
        protected T initialValue() {
            return create();
        }
    };

    public abstract T create();

    public final T get() {
        return instance.get();
    }

}
