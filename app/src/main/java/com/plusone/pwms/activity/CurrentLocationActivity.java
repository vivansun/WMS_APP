package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.plusone.pwms.R;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.EMDKConfigure;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.User;
import com.plusone.pwms.model.UserAndWarehouse;
import com.plusone.pwms.utils.ToastUtil;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScannerResults;

import java.util.ArrayList;
import java.util.List;

public class CurrentLocationActivity extends Activity {

    /** 当前位置 */
    private TextView currentLocationTv;
    /** 确认按钮 */
    private Button confirmButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 全局中的用户与仓库列表 */
    private UserAndWarehouse userAndWarehouse;
    /** 前页面盘点单id */
    private Long countPlanId;
    /** 前页面盘点方式 */
    private String countMethod;

    private Boolean blindCount;
    /** 当前位置 */
    private String binCode = "";

//    private EMDKResults results = null;
//
//    //后台切回前台
//    @Override
//    protected void startScanChange() {
//        if (results == null) {
//            results = EMDKManager.getEMDKManager(getApplicationContext(), this);
//        }
//    }
//
//    //前台切到后台
//    @Override
//    protected void stopScanChange() {
//        results = null;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

//        results = EMDKManager.getEMDKManager(getApplicationContext(), this);
//        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
//            ToastUtil.show(getApplicationContext(), "EMDKManager object request failed!");
//            return;
//        }

        application = (MyApplication) this.getApplication();
        userAndWarehouse = application.getUserAndWarehouse();
        user = userAndWarehouse.getUser();
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        homeButton = findViewById(R.id.home_icon);
        currentLocationTv = findViewById(R.id.current_location);

        Intent intent = getIntent();
        countPlanId = (Long) intent.getSerializableExtra("selectedCountPlanId");
        countMethod = (String) intent.getSerializableExtra("selectedCountMethod");
        blindCount = (Boolean) intent.getSerializableExtra("selectedBlindCount");


        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentLocationActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(CurrentLocationActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(CurrentLocationActivity.this, CountDetailActivity.class);
                newIntent.putExtra("countPlanId",countPlanId);
                newIntent.putExtra("countMethod",countMethod);
                newIntent.putExtra("blindCount",blindCount);
                if(!TextUtils.isEmpty(currentLocationTv.getText())){
                    binCode = currentLocationTv.getText().toString();
                    newIntent.putExtra("binCode",binCode);
                }
                startActivity(newIntent);
            }
        });
    }

//    //获取扫描枪扫描数据
//    @Override
//    public void init(ScanDataCollection scanDataCollection) {
//
//        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
//            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
//            for (ScanDataCollection.ScanData data : scanData) {
//                String dataString = data.getData();
//                CurrentLocationActivity.AsyncDataUpdate asyncDataUpdate = new CurrentLocationActivity.AsyncDataUpdate();
//                asyncDataUpdate.execute(dataString);
//            }
//        }
//    }
//
//
//    private class AsyncDataUpdate extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            return params[0];
//        }
//
//        protected void onPostExecute(String result) {
//            if (result != null) {
//                currentLocationTv.setText(result);
//            }
//        }
//    }
}
