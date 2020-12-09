package com.android.hutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class Sp {

    private static volatile SharedPreferences SP;
    private static volatile SharedPreferences.Editor EDITOR;

    public static void init(Context context, String tag) {
        if (EDITOR == null) {
            synchronized (Sp.class) {
                if (SP == null) {
                    SP = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
                    EDITOR = SP.edit();
                }
            }
        }
    }

    public static void putString(String key, String value) {
        EDITOR.putString(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        return SP.getString(key, defValue);
    }

    public static void putInt(String key, int value) {
        EDITOR.putInt(key, value).apply();
    }

    public static int getInt(String key, int defValue) {
        return SP.getInt(key, defValue);
    }

    public static void putFloat(String key, float value) {
        EDITOR.putFloat(key, value).apply();
    }

    public static float getFloat(String key, float defValue) {
        return SP.getFloat(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        EDITOR.putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return SP.getBoolean(key, defValue);
    }

    public static void putLong(String key, long value) {
        EDITOR.putLong(key, value).apply();
    }

    public static long getLong(String key, long defValue) {
        return SP.getLong(key, defValue);
    }

    public static void apply() {
        if (Build.VERSION.SDK_INT >= 9) {
            EDITOR.apply();
        } else {
            EDITOR.commit();
        }
    }
}