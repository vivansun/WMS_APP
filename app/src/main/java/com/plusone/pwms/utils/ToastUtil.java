/*
 * Copyright 2016 licensed to mbpsoft Co, Ltd;
 * You may not use this file except in compliance with the 'License' from mbpsoft company
 * Please contact at info@mbpsoft.com
 */
package com.plusone.pwms.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 提示框 - 工具类.
 *
 * @author MBP.Jagger
 */
public class ToastUtil {

	public static void toastshow(Context context, String showString, int X, int Y) {
		// X= 0;
		// Y=400;
		Toast toast = Toast.makeText(context, showString, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP | Gravity.LEFT, X, Y);
		toast.show();
	}
	public static void toastshow(Context context, String showString) {

		Toast toast = Toast.makeText(context, showString, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	/**
	 * 
	 * @param context
	 * @param x
	 *            0-1 f
	 * @param y
	 *            0-1 f
	 * @param showString
	 */
	public static void toastshow(Context context, float x, float y, String showString) {
		Toast toast = Toast.makeText(context, showString, Toast.LENGTH_LONG);
		toast.setMargin(x, y);
		toast.show();
	}

	public static void show(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static void show(Context context, int info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}
}
