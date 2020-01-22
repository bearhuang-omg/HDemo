package com.android.hdemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.hdemo.loader.MyClassLoader;
import com.android.hutils.reflect.ClassInfo;

import java.lang.reflect.Field;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        hookClassLoader();
    }

    private void hookClassLoader(){
        ClassLoader classLoader = getClassLoader();
        MyClassLoader myClassLoader = new MyClassLoader(classLoader);
        Context mBase = getBaseContext();
        ClassInfo mBaseClassInfo = new ClassInfo(mBase.getClass());
        Field loadApkField = mBaseClassInfo.getField("mPackageInfo");
        loadApkField.setAccessible(true);
        try {
            Object loadApk = loadApkField.get(mBase);
            ClassInfo loadApkClassInfo = new ClassInfo(loadApk.getClass());
            Field classLoaderField = loadApkClassInfo.getField("mClassLoader");
            classLoaderField.setAccessible(true);
            Object classLoaderObj = classLoaderField.get(loadApk);
            loadApkClassInfo.setField(classLoaderField,loadApk,myClassLoader);
            Log.i("测试","hook成功！");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
