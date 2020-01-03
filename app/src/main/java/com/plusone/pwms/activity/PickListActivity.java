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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.enums.CustomMsgEnum;
import com.plusone.pwms.model.ClientPickInfo;
import com.plusone.pwms.model.ClientPickInfos;
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

public class PickListActivity extends Activity {

    /** 单据列表 */
    private Spinner pickSpinner;
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
    /** 下拉框选中作业方式 */
    private String selectedBusinessType;
    /** 下拉框选中id */
    private Long selectedDocId;

    private SwipeRecyclerView listView;

    private LinearLayoutManager layoutManager;

    private MyAdapter myAdapter;

    private List<ClientPickInfo> pickInfos;
    private ArrayAdapter<ClientPickInfo> arr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upper_shelf_list);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("拣货单选择");
        startButton = (Button) findViewById(R.id.confirm_button);
        startButton.setText("开始拣选");
        homeButton = findViewById(R.id.home_icon);
        pickSpinner = (Spinner) findViewById(R.id.upperShelfSpinner);


        //加载数据
//        loadData();

        listView = (SwipeRecyclerView) findViewById(R.id.listView1);
        //        就是 recyclerview 的显示效果
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        listView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter(PickListActivity.this);
        listView.setAdapter(myAdapter);

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickListActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(PickListActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //开始上架按钮单击事件
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDocId != null && !TextUtils.isEmpty(selectedBusinessType)){
                    // 跳转到拣货确认页面
                    Intent confirmIntent = new Intent(PickListActivity.this, PickDetailActivity.class);
                    confirmIntent.putExtra("selectedBusinessType", selectedBusinessType);
                    confirmIntent.putExtra("selectedDocId", selectedDocId);
                    startActivity(confirmIntent);
                }else {
                    ToastUtil.show(PickListActivity.this, "请先选择一条单号");
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_pick_list_item, viewGroup, false);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }

        @Override
        public int getItemCount() {
            if (pickInfos != null && pickInfos.size() != 0){
                return pickInfos.size();
            }else {
                return 0;
            }
        }

        // 绑定类
        public class MyHolder extends RecyclerView.ViewHolder {

            TextView biztypeTv;
            TextView stockDateTv;
            TextView planQtyTv;
            TextView executedQtyTv;
            TextView binCodeTv;
            TextView orderQtyTv;
            TextView skuCountTv;
            TextView workModeTv;
            LinearLayout LinearLayout1;
            LinearLayout LinearLayout2;
            LinearLayout LinearLayout3;


            public MyHolder(@NonNull View itemView) {
                super(itemView);
                biztypeTv = (TextView) itemView.findViewById(R.id.biztype);
                stockDateTv = (TextView) itemView.findViewById(R.id.stockDate);
                planQtyTv = (TextView) itemView.findViewById(R.id.planQty);
                executedQtyTv = (TextView) itemView.findViewById(R.id.executedQty);
                binCodeTv = (TextView) itemView.findViewById(R.id.binCode);
                orderQtyTv = itemView.findViewById(R.id.orderQty);
                skuCountTv = itemView.findViewById(R.id.skuCount);
                workModeTv = itemView.findViewById(R.id.workMode);
                LinearLayout1 = itemView.findViewById(R.id.LinearLayout1);
                LinearLayout2 = itemView.findViewById(R.id.LinearLayout2);
                LinearLayout3 = itemView.findViewById(R.id.LinearLayout3);
            }
        }

        //   实际绑定 item 操作
        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {

            if (pickInfos != null && pickInfos.size() != 0){
                ClientPickInfo clientPickInfo = pickInfos.get(position);
                if (clientPickInfo.getBusinessType().equals("2")){
                    myHolder.LinearLayout1.setVisibility(View.VISIBLE);
                    myHolder.LinearLayout2.setVisibility(View.VISIBLE);
                    myHolder.LinearLayout3.setVisibility(View.VISIBLE);
                    myHolder.orderQtyTv.setText(clientPickInfo.getOrderQty().toString());
                    myHolder.skuCountTv.setText(clientPickInfo.getSkuCount().toString());
                    myHolder.workModeTv.setText(clientPickInfo.getWorkMode());
                }
                myHolder.biztypeTv.setText(clientPickInfo.getBusinessType());
                myHolder.stockDateTv.setText(clientPickInfo.getStockDate());
                myHolder.planQtyTv.setText(clientPickInfo.getPlanQty().toString());
                myHolder.executedQtyTv.setText(clientPickInfo.getExecutedQty());
                myHolder.binCodeTv.setText(clientPickInfo.getBinCode());
            }
        }
    }

    private void bindSpinnerItems(){
        if (pickInfos != null && pickInfos.size() != 0){
            //适配器
            arr_adapter= new ArrayAdapter<ClientPickInfo>(this, R.layout.spinner_item, pickInfos);
            //设置样式
            arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            //加载适配器
            pickSpinner.setAdapter(arr_adapter);
            //添加事件Spinner事件监听
            pickSpinner.setOnItemSelectedListener(new PickListActivity.SpinnerSelectedListener());
        }
    }

    private void loadData(){
        new PickListAsync().execute();
    }

    private class PickListAsync extends AsyncTask<Long, Integer, Response<ClientPickInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientPickInfos> result) {
            super.onPostExecute(result);
            if ("M".equals(result.getSeverityMsgType())) {
                pickInfos = result.getResults().getPickInfos();
                bindSpinnerItems();
            } else {
                if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                    ToastUtil.show(PickListActivity.this, result.getSeverityMsg());
                    finish();
                }else {
                    ToastUtil.show(PickListActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientPickInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(PickListActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getPickList";
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientPickInfos>>() {
            }.getType());
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientPickInfo currentItem = arr_adapter.getItem(arg2);
            selectedBusinessType = currentItem.getBusinessType();
            selectedDocId = currentItem.getDocId();
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
