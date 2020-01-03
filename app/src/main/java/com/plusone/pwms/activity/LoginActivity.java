package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.BuildConfig;
import com.plusone.pwms.R;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.User;
import com.plusone.pwms.model.UserAndWarehouse;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends Activity {

    /** 账户 */
    private EditText loginAccount;
    /** 密码 */
    private EditText loginPassword;
    /** 系统设置 */
    private ImageButton systemSetup;
    /** 登录按钮 */
    private Button loginButton;
    /** 全局变量 */
    private MyApplication application;

    private LoadingDailog loadingDailog;

    private SharedPreferences sp;

    private TextView versionView;

    private LoadingDailog.Builder builder = new LoadingDailog.Builder(LoginActivity.this)
            .setMessage("正在登陆中...");

    private class Login extends AsyncTask<String, Integer, Response<UserAndWarehouse>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<UserAndWarehouse> result) {
            super.onPostExecute(result);
            if ("M".equals(result.getSeverityMsgType())) {
                User user = new User();
                user.setLaborName(result.getResults().getLaborName());
                user.setLaborId(result.getResults().getLaborId());
                result.getResults().setUser(user);
                application.setUserAndWarehouse(result.getResults());
                // 跳转到选择仓库页面
                Intent warehouseIntent = new Intent(LoginActivity.this, WarehouseActivity.class);
                startActivity(warehouseIntent);
                loadingDailog.dismiss();
                finish();
            } else {
                loadingDailog.dismiss();
                ToastUtil.show(LoginActivity.this, result.getSeverityMsg());
            }
        }


        @Override
        protected Response<UserAndWarehouse> doInBackground(String... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(LoginActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "login";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("laborCode", params[0]);
            parameters.put("password", params[1]);
            paramMap.put("parameters",parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<UserAndWarehouse>>() {
            }.getType());
        }
    }

    //设置返回按钮：不应该退出程序---而是返回桌面
    //复写onKeyDown事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferencesUtil.setStringSharedPerference(SharedPreferencesUtil.getSharePerference(LoginActivity.this,"systemSetting"),"prefix",com.plusone.pwms.BuildConfig.SERVER_URL);
        application = (MyApplication) this.getApplication();
        loginButton = (Button) findViewById(R.id.login_button);
        loginAccount = (EditText) findViewById(R.id.login_account);
        loginPassword = (EditText) findViewById(R.id.login_password);
        systemSetup = findViewById(R.id.setting_icon);
        versionView = findViewById(R.id.version);
        versionView.setText("v"+ BuildConfig.VERSION_NAME);

        loadingDailog = builder.create();
        if (sp.getString("loginAccount", null) != null) {
            loginAccount.setText(sp.getString("loginAccount", null));
            loginPassword.requestFocus();
        }

        //系统设置的单击事件
        systemSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到系统设置页面
                Intent systemSetupIntent = new Intent(LoginActivity.this, SysSettingActivity.class);
                startActivity(systemSetupIntent);
            }
        });

        //登陆按钮单击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String accountValue = loginAccount.getText().toString();
                final String passwordValue = loginPassword.getText().toString();

                if (TextUtils.isEmpty(loginAccount.getText())) {
                    Toast.makeText(LoginActivity.this, "用户名为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(loginPassword.getText())) {
                    Toast.makeText(LoginActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                //显示加载框
                loadingDailog.show();
                //将用户名密码保存本地，下次进入app时自动填充用户名密码
                sp.edit()
                        .putString("loginAccount", accountValue)
                        .apply();
                //调用登陆异步任务
                Login login = new Login();
                login.execute(accountValue, passwordValue);
            }
        });
    }
}
