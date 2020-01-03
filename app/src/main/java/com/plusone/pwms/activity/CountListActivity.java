package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.enums.CustomMsgEnum;
import com.plusone.pwms.model.ClientCountPlanInfo;
import com.plusone.pwms.model.ClientCountPlanInfos;
import com.plusone.pwms.model.ClientPickInfo;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountListActivity extends Activity {

    /** 单据列表 */
    private Spinner countSpinner;
    /** 开始按钮 */
    private Button startButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;

    private TextView countDateTv;
    private TextView skuCountTv;
    private TextView quantCountTv;
    private TextView blindCountTv;
    private TextView countMethodTv;

    /** 下拉框选中id */
    private Long selectedCountPlanId;
    /** 下拉框选中盘点方式 */
    private String selectedCountMethod;
    /** 下拉框选中是否盲盘 */
    private Boolean selectedBlindCount;

    private List<ClientCountPlanInfo> countInfos;
    private ArrayAdapter<ClientCountPlanInfo> arr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_list);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        countSpinner = (Spinner) findViewById(R.id.countSpinner);
        startButton = findViewById(R.id.confirm_button);
        homeButton = findViewById(R.id.home_icon);
        countDateTv = (TextView) findViewById(R.id.countDate);
        skuCountTv = (TextView) findViewById(R.id.skuCount);
        quantCountTv = (TextView) findViewById(R.id.quantCount);
        blindCountTv = (TextView) findViewById(R.id.blindCount);
        countMethodTv = (TextView) findViewById(R.id.countMethod);

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountListActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(CountListActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //开始盘点按钮单击事件
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCountPlanId != null){
                    // 跳转到拣货确认页面
                    Intent confirmIntent = new Intent(CountListActivity.this, CurrentLocationActivity.class);
                    confirmIntent.putExtra("selectedCountPlanId", selectedCountPlanId);
                    confirmIntent.putExtra("selectedCountMethod", selectedCountMethod);
                    confirmIntent.putExtra("selectedBlindCount", selectedBlindCount);
                    startActivity(confirmIntent);
                }else {
                    ToastUtil.show(CountListActivity.this, "请先选择一条单号");
                }

            }
        });
    }


    private void bindSpinnerItems(){
        if (countInfos != null && countInfos.size() != 0){
            //适配器
            arr_adapter= new ArrayAdapter<ClientCountPlanInfo>(this, R.layout.spinner_item, countInfos);
            //设置样式
            arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            //加载适配器
            countSpinner.setAdapter(arr_adapter);
            //添加事件Spinner事件监听
            countSpinner.setOnItemSelectedListener(new CountListActivity.SpinnerSelectedListener());
        }
    }

    private void loadData(){
        new CountListAsync().execute();
    }

    private class CountListAsync extends AsyncTask<Long, Integer, Response<ClientCountPlanInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientCountPlanInfos> result) {
            super.onPostExecute(result);
            if ("M".equals(result.getSeverityMsgType())) {
                countInfos = result.getResults().getCountInfos();
                bindSpinnerItems();
            } else {
                if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                    ToastUtil.show(CountListActivity.this, result.getSeverityMsg());
                    finish();
                }else {
                    ToastUtil.show(CountListActivity.this, result.getSeverityMsg());
                }
            }
        }

        @Override
        protected Response<ClientCountPlanInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(CountListActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getCountList";
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientCountPlanInfos>>() {
            }.getType());
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientCountPlanInfo currentItem = arr_adapter.getItem(arg2);
            selectedCountPlanId = currentItem.getCountPlanId();
            arg0.setVisibility(View.VISIBLE);
            countDateTv.setText(currentItem.getCountDate());
            skuCountTv.setText(currentItem.getSkuCount().toString());
            quantCountTv.setText(currentItem.getQuantCount().toString());
            selectedBlindCount = currentItem.getBlindCount();
            if (currentItem.getBlindCount()){
                blindCountTv.setText("盲盘");
            }else {
                blindCountTv.setText("明盘");
            }
            selectedCountMethod = currentItem.getCountMethod();
            countMethodTv.setText(currentItem.getCountMethod());
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadData();
    }
}
