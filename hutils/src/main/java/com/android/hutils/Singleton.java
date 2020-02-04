package com.android.hutils;

public abstract class Singleton<T> {

    private volatile T instance;

    public abstract T create();

    public final T get() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = create();
                }
            }
        }
        return instance;
    }

}
