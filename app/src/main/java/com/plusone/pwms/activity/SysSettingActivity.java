package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.BuildConfig;
import com.plusone.pwms.R;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.model.UserAndWarehouse;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;

import java.util.HashMap;
import java.util.Map;

public class SysSettingActivity extends Activity {

    /** 登录按钮 */
    private Button confirmButton;
    /** 全局变量 */
    private MyApplication application;

    private EditText systemSddressET;

    private String oldPrefix;
    private String newPrefix;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_setting);


        application = (MyApplication) this.getApplication();
        oldPrefix = SharedPreferencesUtil.getSharePerference(SysSettingActivity.this,"systemSetting").getString("prefix","");
        systemSddressET = findViewById(R.id.system_address);
        confirmButton = findViewById(R.id.confirm_button);

        systemSddressET.setText(oldPrefix);

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPrefix = systemSddressET.getText().toString();
                getSharedPreferences("systemSetting", Context.MODE_PRIVATE);
                SharedPreferencesUtil.setStringSharedPerference(SharedPreferencesUtil.getSharePerference(SysSettingActivity.this,"systemSetting"),"prefix",newPrefix);
                finish();
            }
        });
    }
}
