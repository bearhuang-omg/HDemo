package com.android.hutils.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
    private Object mObj;
    private Class mClass;

    public ClassInfo(Object obj) {
        mObj = obj;
        mClass = obj.getClass();
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
                for (Field field : getFields()) {
                    if (field.getName().equals(name)) {
                        return field;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setFieldValue(String fieldName, Object fieldValue) {
        Field field = getField(fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(mObj, fieldValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setFieldValue(Field field, Object fieldValue) {
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(mObj, fieldValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Object getFieldValue(Field field) {
        try {
            field.setAccessible(true);
            return field.get(mObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getFieldValue(String fieldName) {
        try {
            Field field = getField(fieldName);
            return getFieldValue(field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public Method getMethod(String methodName, Class... parameterTypes) {
        try {
            if (mClass != null) {
                for (Method method : getMethods()) {
                    if (method.getName().equals(methodName)) {
                        Parameter[] parameters = method.getParameters();
                        if ((parameterTypes == null || parameterTypes.length == 0) && (parameters == null || parameters.length == 0)) {
                            return method;
                        } else if (parameters.length != parameterTypes.length) {
                            return null;
                        } else {
                            for (int i = 0; i < parameters.length; i++) {
                                if (!parameters[i].getType().equals(parameterTypes[i])) {
                                    return null;
                                }
                            }
                        }
                        return method;
                    }
                }
                return mClass.getMethod(methodName, parameterTypes);
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
     * @param params
     * @return
     */
    public Object executeMethod(Method method,Object... params) {
        if (method != null) {
            try {
                method.setAccessible(true);
                if(method.getParameterCount() == 0){
                    return method.invoke(mObj);
                }else {
                    return method.invoke(mObj, params);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Object executeMethod(String methodName, Object... params) {
        if (params == null || params.length == 0) {
            Method method = getMethod(methodName);
            if(method != null){
                return executeMethod(method);
            }
        } else {
            Method method = getMethod(methodName);
            return executeMethod(method, params);
        }
        return null;
    }

    public Class getClassInfoClass() {
        return mClass;
    }
}
