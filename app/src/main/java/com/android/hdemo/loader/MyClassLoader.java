package com.android.hdemo.loader;

import android.util.Log;


public class MyClassLoader extends ClassLoader {

    public MyClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Log.i("测试","loadclass:"+name);
        return super.loadClass(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Log.i("测试","loadclass:"+name+",resolve:"+resolve);
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Log.i("测试","findclass:"+name);
        return super.findClass(name);
    }
}
