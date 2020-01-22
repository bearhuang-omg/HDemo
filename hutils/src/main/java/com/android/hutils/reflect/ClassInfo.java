package com.android.hutils.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangbei on 20-1-15.
 */

public class ClassInfo {

    private Constructor[] mConstructors;
    private List<Field> mFields;
    private List<Method> mMethods;
    private Annotation[] mClassAnnotations;
    private Class mClass;

    public ClassInfo(Class cls){
        mClass = cls;
        mConstructors = mClass.getDeclaredConstructors();
        mClassAnnotations = mClass.getAnnotations();
    }

    public Constructor[] getConstructors() {
        return mConstructors;
    }

    public List<Field> getFields() {
        if (mFields == null) {
            mFields = new ArrayList<>();
            try {
                Class cls = mClass;
                for (; cls != Object.class; cls = cls.getSuperclass()) {
                    for (Field field : cls.getDeclaredFields()) {
                        mFields.add(field);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mFields;
    }

    public Field getField(String name) {
        try {
            if (mClass != null) {
                for(Field field : getFields()){
                    if(field.getName().equals(name)){
                        return field;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setField(String fieldName,Object obj,Object fieldValue){
        Field field = getField(fieldName);
        if(field != null){
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setField(Field field,Object obj,Object fieldValue){
        if(field != null){
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public List<Method> getMethods() {
        if(mMethods == null){
            mMethods = new ArrayList<>();
            try {
                Class cls = mClass;
                for(;cls != Object.class;cls = cls.getSuperclass()){
                    for(Method method : cls.getDeclaredMethods()){
                        mMethods.add(method);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return mMethods;
    }

    public Method getMethod(String name, Class... parameterTypes) {
        try {
            if (mClass != null) {
                return mClass.getMethod(name, parameterTypes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Annotation[] getAnnotations(){
        return mClassAnnotations;
    }

    public Annotation getClassAnnotation(Class cls){
        try{
            if(mClass != null){
                return mClass.getAnnotation(cls);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行无参和有参的method
     * @param method
     * @param obj
     * @param params
     * @return
     */
    public Object executeMethod(Method method, Object obj, Object... params) {
        if (method != null) {
            try {
                method.setAccessible(true);
                if(method.getParameterCount() == 0){
                    return method.invoke(obj);
                }else {
                    return method.invoke(obj, params);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 执行无参的method
     * @param methodName
     * @param obj
     * @return
     */
    public Object executeMethod(String methodName, Object obj) {
        try {
            Method method = getMethod(methodName);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class getClassInfoClass() {
        return mClass;
    }
}
