package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.MenuInfos;
import com.plusone.pwms.model.User;
import com.plusone.pwms.model.UserAndWarehouse;
import com.plusone.pwms.model.MenuInfo;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseActivity extends Activity {

    /** 仓库列表 */
    private Spinner warehouseSpinner;
    /** 确认按钮 */
    private Button confirmButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 全局中的用户与仓库列表 */
    private UserAndWarehouse userAndWarehouse;
    /** 下拉框选中库id */
    private Long selectedWhId;
    /** 下拉框选中库名 */
    private String selectedWhName;

    private List<ClientWarehouse> whInfo = new ArrayList<ClientWarehouse>();
    private ArrayAdapter<ClientWarehouse> arr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);

        application = (MyApplication) this.getApplication();
        userAndWarehouse = application.getUserAndWarehouse();
        user = userAndWarehouse.getUser();
        confirmButton = (Button) findViewById(R.id.confirm_button);
        warehouseSpinner = (Spinner) findViewById(R.id.warehouseSpinner);

        //数据
        whInfo = userAndWarehouse.getWhInfo();
        //适配器
        arr_adapter= new ArrayAdapter<ClientWarehouse>(this, R.layout.spinner_item, whInfo);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //加载适配器
        warehouseSpinner.setAdapter(arr_adapter);
        //添加事件Spinner事件监听
        warehouseSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        if(application.getClientWarehouse() != null){
            for (int i = 0;i < arr_adapter.getCount();i++){
                if(application.getClientWarehouse().getWhId() == arr_adapter.getItem(i).getWhId()){
                    warehouseSpinner.setSelection(i,true);
                }
            }
        }

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToMenuActivity();
            }
        });
    }

    private void ToMenuActivity() {
        ClientWarehouse clientWarehouse = new ClientWarehouse();
        clientWarehouse.setWhId(selectedWhId);
        clientWarehouse.setWhName(selectedWhName);
        application.setClientWarehouse(clientWarehouse);

        Intent menuIntent = new Intent(WarehouseActivity.this, MenuActivity.class);
        startActivity(menuIntent);
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            selectedWhId = (Long) arr_adapter.getItem(arg2).getWhId();
            selectedWhName = (String) arr_adapter.getItem(arg2).getWhName();
            arg0.setVisibility(View.VISIBLE);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
