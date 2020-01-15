package com.android.hutils.bind.runtime;

import android.app.Activity;
import android.view.View;

import com.android.hutils.reflect.ClassInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射方式实现注解
 */

public class BindApi {

    public static void bindId(Activity obj){
        ClassInfo clsInfo = new ClassInfo(obj.getClass());
        //处理类
        if(obj.getClass().isAnnotationPresent(RuntimeBindView.class)) {
            RuntimeBindView bindView = (RuntimeBindView)clsInfo.getClassAnnotation(RuntimeBindView.class);
            int id = bindView.value();
            clsInfo.executeMethod(clsInfo.getMethod("setContentView",int.class),obj,id);
        }

        //处理类成员
        for(Field field : clsInfo.getFields()){
            if(field.isAnnotationPresent(RuntimeBindView.class)){
                RuntimeBindView bindView = field.getAnnotation(RuntimeBindView.class);
                int id = bindView.value();
                Object view = clsInfo.executeMethod(clsInfo.getMethod("findViewById",int.class),obj,id);
                clsInfo.setField(field,obj,view);
            }
        }

        //处理点击事件
        for (Method method : clsInfo.getMethods()) {
            if (method.isAnnotationPresent(RuntimeBindClick.class)) {
                int[] values = method.getAnnotation(RuntimeBindClick.class).value();
                for (int id : values) {
                    View view = (View) clsInfo.executeMethod(clsInfo.getMethod("findViewById", int.class), obj, id);
                    view.setOnClickListener(v -> {
                        try {
                            method.invoke(obj, v);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

}
