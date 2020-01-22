package com.android.hdemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.hdemo.loader.MyClassLoader;
import com.android.hutils.reflect.ClassInfo;


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

        try {
            ClassInfo mBaseClassInfo = new ClassInfo(mBase);
            Object loadApk = mBaseClassInfo.getFieldValue("mPackageInfo");
            ClassInfo loadApkClassInfo = new ClassInfo(loadApk);
            Object classLoaderObj = loadApkClassInfo.getFieldValue("mClassLoader");
            loadApkClassInfo.setFieldValue("mClassLoader",myClassLoader);
            Log.i("测试","hook成功！");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
