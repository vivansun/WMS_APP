/*
 * Copyright 2016 licensed to mbpsoft Co, Ltd;
 * You may not use this file except in compliance with the 'License' from mbpsoft company
 * Please contact at info@mbpsoft.com
 */
package com.plusone.pwms.utils;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Properties - 工具类.
 *
 * @author MBP.Jagger
 */
public class PropertiesUtil {

    private static final String FILE_SUFFIX = ".properties";

    /**
     * 保存获取应用的data/data的files目录下 Properties文件内容
     *
     * @param context
     * @param fileName   文件名
     * @param properties
     */
    public static void saveProperties(Context context, String fileName, Properties properties) {

        StringBuffer fileNameBuffer = new StringBuffer();
        fileNameBuffer.append(fileName);
        fileNameBuffer.append(FILE_SUFFIX);

        try {
            OutputStream out = context.openFileOutput(fileNameBuffer.toString(), Context.MODE_PRIVATE);
            properties.store(out, null);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    /**
     * 获取应用的data/data的files目录下 Properties文件内容
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Properties getProperties(Context context, String fileName) {

        StringBuffer fileNameBuffer = new StringBuffer();
        fileNameBuffer.append(fileName);
        fileNameBuffer.append(FILE_SUFFIX);

        Properties properties = new Properties();

        try {
            properties.load(context.openFileInput(fileNameBuffer.toString()));
        } catch (Exception e) {
        }

        return properties;
    }

    /**
     * 获取在res目录下raw资源文件夹 Properties文件内容
     *
     * @param context
     * @param fileId
     * @return
     */
    public static Properties getProperties(Context context, int fileId) {

        Properties properties = new Properties();

        try {
            properties.load(context.getResources().openRawResource(fileId));
        } catch (Exception e) {
        }

        return properties;
    }
}
