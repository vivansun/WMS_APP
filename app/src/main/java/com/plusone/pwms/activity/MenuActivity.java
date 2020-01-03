package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.MenuInfo;
import com.plusone.pwms.model.MenuInfos;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends Activity {

    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;

    private Button changeWHButton;
    private Button receiveGoodsButton;
    private Button upperShelfButton;
    private Button pickingButton;
    private Button inventoryButton;
    private Button moveTaskButton;
    private Button splitButton;
    private Button bolConfirmButton;
    private Button othersButton;
    private ImageButton refreshButton;

    private Map<String,String> menuInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();

        changeWHButton = (Button) findViewById(R.id.toolbar_warehouse);
        receiveGoodsButton = (Button) findViewById(R.id.receive_goods);
        upperShelfButton = (Button) findViewById(R.id.upper_shelf);
        pickingButton = (Button) findViewById(R.id.picking);
        inventoryButton = (Button) findViewById(R.id.inventory);
        moveTaskButton = (Button) findViewById(R.id.move_task);
        splitButton = (Button) findViewById(R.id.split);
        bolConfirmButton = (Button) findViewById(R.id.review);
        othersButton = (Button) findViewById(R.id.others);
        refreshButton = findViewById(R.id.refresh_icon);

//        loadData();

        changeWHButton.setText(clientWarehouse.getWhName());


        changeWHButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到选择仓库页面
                Intent warehouseIntent = new Intent(MenuActivity.this, WarehouseActivity.class);
                startActivity(warehouseIntent);
                finish();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearData();
                loadData();
            }
        });

        receiveGoodsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到收货页面
                Intent newIntent = new Intent(MenuActivity.this, ReceiveGoodsListActivity.class);
                startActivity(newIntent);
            }
        });

        upperShelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到上架页面
                Intent newIntent = new Intent(MenuActivity.this, UpperShelfListActivity.class);
                startActivity(newIntent);
            }
        });

        pickingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到拣选页面
                Intent newIntent = new Intent(MenuActivity.this, PickListActivity.class);
                startActivity(newIntent);
            }
        });

        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到盘点页面
                Intent newIntent = new Intent(MenuActivity.this, CountListActivity.class);
                startActivity(newIntent);
            }
        });

        moveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到移位页面
                Intent newIntent = new Intent(MenuActivity.this, MoveTaskListActivity.class);
                startActivity(newIntent);
            }
        });

        splitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到分拣页面
                Intent newIntent = new Intent(MenuActivity.this, SplitListActivity.class);
                startActivity(newIntent);
            }
        });

        bolConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到复核页面
                Intent newIntent = new Intent(MenuActivity.this, BolConfirmListActivity.class);
                startActivity(newIntent);
            }
        });

        othersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到其他页面
                Intent newIntent = new Intent(MenuActivity.this, OthersActivity.class);
                startActivity(newIntent);
            }
        });
    }
    private void clearData(){
        receiveGoodsButton.setText("1.收货");
        upperShelfButton.setText("2.上架");
        pickingButton.setText("3.拣选");
        inventoryButton.setText("4.盘点");
        moveTaskButton.setText("5.移位");
        splitButton.setText("6.分拣");
        bolConfirmButton.setText("7.复核");
        othersButton.setText("8.其他");
    }

    private void loadData(){
        new MenuActivity.InitMenu().execute();
    }

    private class InitMenu extends AsyncTask<Long, Integer, Response<Map<String,String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<Map<String,String>> result) {
            super.onPostExecute(result);
            if ("M".equals(result.getSeverityMsgType())) {
                menuInfoList = result.getResults();
                if (menuInfoList != null){
                    if (menuInfoList.get("1") != null){
                        receiveGoodsButton.setText("1.收货("+menuInfoList.get("1")+")");
                    }
                    if (menuInfoList.get("2") != null){
                        upperShelfButton.setText("2.上架("+menuInfoList.get("2")+")");
                    }
                    if (menuInfoList.get("3") != null){
                        pickingButton.setText("3.拣选("+menuInfoList.get("3")+")");
                    }
                    if (menuInfoList.get("4") != null){
                        inventoryButton.setText("4.盘点("+menuInfoList.get("4")+")");
                    }
                    if (menuInfoList.get("5") != null){
                        moveTaskButton.setText("5.移位("+menuInfoList.get("5")+")");
                    }
                    if (menuInfoList.get("6") != null){
                        splitButton.setText("6.分拣("+menuInfoList.get("6")+")");
                    }
                    if (menuInfoList.get("7") != null){
                        bolConfirmButton.setText("7.复核("+menuInfoList.get("7")+")");
                    }
                    if (menuInfoList.get("8") != null){
                        othersButton.setText("8.其他("+menuInfoList.get("8")+")");
                    }
                }
            } else {
                ToastUtil.show(MenuActivity.this, result.getSeverityMsg());
            }
        }


        @Override
        protected Response<Map<String,String>> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(MenuActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "initMenu";
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<Map<String,String>>>() {
            }.getType());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        clearData();
        loadData();
    }
}
