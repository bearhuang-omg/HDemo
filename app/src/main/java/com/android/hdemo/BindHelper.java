package com.android.hdemo;

import android.app.Activity;

import com.android.hbind.ClassElementsInfo;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangbei on 20-1-16.
 */

public class BindHelper {

    static final Map<Class<?>,Constructor<?>> Bindings = new HashMap<>();

    public static void inject(Activity activity){
        String classFullName = activity.getClass().getName() + ClassElementsInfo.classSuffix;
        try{
            Constructor constructor = Bindings.get(activity.getClass());
            if(constructor == null){
                Class proxy = Class.forName(classFullName);
                constructor = proxy.getDeclaredConstructor(activity.getClass());
                Bindings.put(activity.getClass(),constructor);
            }
            constructor.setAccessible(true);
            constructor.newInstance(activity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
