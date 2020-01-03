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
import com.plusone.pwms.model.ClientPutAwayInfo;
import com.plusone.pwms.model.ClientPutAwayInfoDetail;
import com.plusone.pwms.model.ClientPutAwayInfos;
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

public class UpperShelfListActivity extends Activity {

    /** 单据列表 */
    private Spinner upperShelfSpinner;
    /** 开始上架按钮 */
    private Button startUpperShelfButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;
    /** 下拉框选中收货追溯号 */
    private String selectedInboundTrackSeq;
    /** 下拉框选中月台id */
    private Long selectedBinId;

    private SwipeRecyclerView listView;

    private LinearLayoutManager layoutManager;

    private MyAdapter myAdapter;

    /** 当前单据详情列表 */
    List<ClientPutAwayInfoDetail> currentClientPutAwayInfoDetailList;

    private List<ClientPutAwayInfo> putAwayInfos;
    private ArrayAdapter<ClientPutAwayInfo> arr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upper_shelf_list);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        startUpperShelfButton = (Button) findViewById(R.id.confirm_button);
        homeButton = findViewById(R.id.home_icon);
        upperShelfSpinner = (Spinner) findViewById(R.id.upperShelfSpinner);


        //加载数据
//        loadData();

        listView = (SwipeRecyclerView) findViewById(R.id.listView1);
        //        就是 recyclerview 的显示效果
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        listView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter(UpperShelfListActivity.this);
        listView.setAdapter(myAdapter);

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpperShelfListActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(UpperShelfListActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //开始上架按钮单击事件
        startUpperShelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBinId != null && !TextUtils.isEmpty(selectedInboundTrackSeq)){
                    // 跳转到上架确认页面
                    Intent confirmIntent = new Intent(UpperShelfListActivity.this, UpperShelfDetailActivity.class);
                    confirmIntent.putExtra("selectedInboundTrackSeq", selectedInboundTrackSeq);
                    confirmIntent.putExtra("selectedBinId", selectedBinId);
                    startActivity(confirmIntent);
                }else {
                    ToastUtil.show(UpperShelfListActivity.this, "请先选择一条单号");
                }

            }
        });
    }

    //任务列表自定义适配器
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        private Context context;   // 方便视图的操作

        public MyAdapter(Context context) {
            this.context = context;
        }

        // 获取 item 的布局
        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_upper_shelf_list_item, viewGroup, false);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }

        @Override
        public int getItemCount() {
            if (currentClientPutAwayInfoDetailList != null && currentClientPutAwayInfoDetailList.size() != 0){
                return currentClientPutAwayInfoDetailList.size();
            }else {
                return 0;
            }
        }

        // 绑定类
        public class MyHolder extends RecyclerView.ViewHolder {

            TextView skuCodeTv;
            TextView skuNameTv;
            TextView skuSpecTv;
            TextView planQtyTv;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                skuCodeTv = (TextView) itemView.findViewById(R.id.sku_code);
                skuNameTv = (TextView) itemView.findViewById(R.id.sku_name);
                skuSpecTv = (TextView) itemView.findViewById(R.id.sku_spec);
                planQtyTv = (TextView) itemView.findViewById(R.id.plan_qty);
            }
        }

        //   实际绑定 item 操作
        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {

            if (currentClientPutAwayInfoDetailList != null && currentClientPutAwayInfoDetailList.size() != 0){
                ClientPutAwayInfoDetail clientPutAwayInfoDetail = currentClientPutAwayInfoDetailList.get(position);
                myHolder.skuCodeTv.setText(clientPutAwayInfoDetail.getSkuCode());
                myHolder.skuNameTv.setText(clientPutAwayInfoDetail.getSkuName());
                myHolder.skuSpecTv.setText(clientPutAwayInfoDetail.getSkuSpec());
                myHolder.planQtyTv.setText(clientPutAwayInfoDetail.getPlanQty().toString());
            }
        }
    }

    private void bindSpinnerItems(){
        if (putAwayInfos != null && putAwayInfos.size() != 0){
            //适配器
            arr_adapter= new ArrayAdapter<ClientPutAwayInfo>(this, R.layout.spinner_item, putAwayInfos);
            //设置样式
            arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            //加载适配器
            upperShelfSpinner.setAdapter(arr_adapter);
            //添加事件Spinner事件监听
            upperShelfSpinner.setOnItemSelectedListener(new UpperShelfListActivity.SpinnerSelectedListener());
        }
    }

    private void loadData(){
        new PutAwayListAsync().execute();
    }

    private class PutAwayListAsync extends AsyncTask<Long, Integer, Response<ClientPutAwayInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientPutAwayInfos> result) {
            super.onPostExecute(result);
            if ("M".equals(result.getSeverityMsgType())) {
                putAwayInfos = result.getResults().getPutAwayInfos();
                bindSpinnerItems();
            } else {
                if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                    ToastUtil.show(UpperShelfListActivity.this, result.getSeverityMsg());
                    finish();
                }else {
                    ToastUtil.show(UpperShelfListActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientPutAwayInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(UpperShelfListActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getPutAwayList";
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientPutAwayInfos>>() {
            }.getType());
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientPutAwayInfo currentItem = arr_adapter.getItem(arg2);
            selectedInboundTrackSeq = currentItem.getInboundTrackSeq();
            selectedBinId = currentItem.getBinId();
            currentClientPutAwayInfoDetailList = currentItem.getDetails();
            arg0.setVisibility(View.VISIBLE);
            myAdapter.notifyDataSetChanged();
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
