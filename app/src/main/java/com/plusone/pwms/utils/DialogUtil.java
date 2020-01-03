package com.plusone.pwms.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {
    /**
     *  普通弹出框(包含确认与取消按钮)
     *
     * @param context
     * @param title  标题
     * @param message 消息
     * @param listener 确认按钮单击事件
     * @return
     */
    public static void showNormalDialog(Context context, String title, String message, final DialogInterface.OnClickListener listener){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle(title);
        normalDialog.setMessage(message);
        normalDialog.setPositiveButton((Html.fromHtml("<font color=\"blue\">确认</font>")),listener);
        normalDialog.setNegativeButton((Html.fromHtml("<font color=\"blue\">取消</font>")),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     *  普通弹出框(包含确认按钮)
     *
     * @param context
     * @param title  标题
     * @param message 消息
     * @param listener 确认按钮单击事件
     * @return
     */
    public static void showNormalDialogNoCancle(Context context, String title, String message, final DialogInterface.OnClickListener listener){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle(title);
        normalDialog.setCancelable(false);
        normalDialog.setMessage(message);
        normalDialog.setPositiveButton((Html.fromHtml("<font color=\"blue\">确认</font>")),listener);
        // 显示
        normalDialog.show();
    }
}
