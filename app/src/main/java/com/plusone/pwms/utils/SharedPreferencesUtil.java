/*
 * Copyright 2016 licensed to mbpsoft Co, Ltd;
 * You may not use this file except in compliance with the 'License' from mbpsoft company
 * Please contact at info@mbpsoft.com
 */
package com.plusone.pwms.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences - 工具类.
 *
 * @author MBP.Jagger
 */
public class SharedPreferencesUtil {

    private static final String NAME = "WINCEWMS";
    private static SharedPreferencesUtil instance;

    static {
        instance = new SharedPreferencesUtil();
    }

    public static SharedPreferencesUtil getInstance() {
        if (instance == null) {
            instance = new SharedPreferencesUtil();
        }
        return instance;
    }

    public static SharedPreferences getSharePerference(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharePerference(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static boolean isFirst(SharedPreferences sp) {
        return sp.getBoolean("isFirst", true);
    }

    public static void setStringSharedPerference(SharedPreferences sp, String key, String value) {
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setBooleanSharedPerference(SharedPreferences sp, String key, boolean value) {
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
