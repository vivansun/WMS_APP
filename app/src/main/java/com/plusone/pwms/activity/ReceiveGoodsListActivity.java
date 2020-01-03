package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.plusone.pwms.model.ClientAsnDetailInfo;
import com.plusone.pwms.model.ClientAsnInfo;
import com.plusone.pwms.model.ClientAsnInfos;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiveGoodsListActivity extends Activity {

    /** 单据列表 */
    private Spinner asnInfoSpinner;
    /** 开始收货按钮 */
    private Button startReceiveGoodsButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;
    /** 下拉框选中单据id */
    private Long selectedAsnInfoId;

    private SwipeRecyclerView listView;

    private LinearLayoutManager layoutManager;

    private MyAdapter myAdapter;

    /** 当前单据详情列表 */
    List<ClientAsnDetailInfo> currentClientAsnDetailInfoList;

    List<Long> detailIds = new ArrayList<>();

    private List<ClientAsnInfo> asnInfos;
    private ArrayAdapter<ClientAsnInfo> arr_adapter;

    //使editText点击外部时候失去焦点
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {//点击editText控件外部
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    if (editText != null) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        editText.clearFocus();
                    }
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    EditText editText = null;

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            editText = (EditText) v;
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_list);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        startReceiveGoodsButton = (Button) findViewById(R.id.confirm_button);
        homeButton = findViewById(R.id.home_icon);
        asnInfoSpinner = (Spinner) findViewById(R.id.receiptSpinner);


        //加载数据
//        loadData();

        listView = (SwipeRecyclerView) findViewById(R.id.listView1);
        //        就是 recyclerview 的显示效果
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        listView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter(ReceiveGoodsListActivity.this);
        listView.setAdapter(myAdapter);

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiveGoodsListActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(ReceiveGoodsListActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //开始收货按钮单击事件
        startReceiveGoodsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAsnInfoId != null){
                    // 跳转到收货确认页面
                    Intent confirmIntent = new Intent(ReceiveGoodsListActivity.this, ReceiveGoodsConfirmActivity.class);
                    confirmIntent.putExtra("detailIds", (Serializable)detailIds);
                    confirmIntent.putExtra("selectedAsnInfoId", selectedAsnInfoId);
                    startActivity(confirmIntent);
                }else {
                    ToastUtil.show(ReceiveGoodsListActivity.this, "请先选择一条单据");
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_receiving_list_item, viewGroup, false);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }

        @Override
        public int getItemCount() {
            if (currentClientAsnDetailInfoList != null && currentClientAsnDetailInfoList.size() != 0){
                return currentClientAsnDetailInfoList.size();
            }else {
                return 0;
            }
        }

        // 绑定类
        public class MyHolder extends RecyclerView.ViewHolder {

            TextView skuCodeTv;
            TextView skuNameTv;
            TextView skuSpecTv;
            TextView planPackageTv;
            TextView planQtyTv;
            TextView unRecieveQtyTv;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                skuCodeTv = (TextView) itemView.findViewById(R.id.sku_code);
                skuNameTv = (TextView) itemView.findViewById(R.id.sku_name);
                skuSpecTv = (TextView) itemView.findViewById(R.id.sku_spec);
                planPackageTv = (TextView) itemView.findViewById(R.id.set_plan_package);
                planQtyTv = (TextView) itemView.findViewById(R.id.plan_qty);
                unRecieveQtyTv = (TextView) itemView.findViewById(R.id.unreceive_qty);
            }
        }

        //   实际绑定 item 操作
        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {

            if (currentClientAsnDetailInfoList != null && currentClientAsnDetailInfoList.size() != 0){
                ClientAsnDetailInfo clientAsncDetailInfo = currentClientAsnDetailInfoList.get(position);
                myHolder.skuCodeTv.setText(clientAsncDetailInfo.getSkuCode());
                myHolder.skuNameTv.setText(clientAsncDetailInfo.getSkuName());
                myHolder.skuSpecTv.setText(clientAsncDetailInfo.getSkuSpec());
                myHolder.planPackageTv.setText(clientAsncDetailInfo.getPlanPackage());
                myHolder.planQtyTv.setText(clientAsncDetailInfo.getPlanQty().toString());
                myHolder.unRecieveQtyTv.setText(clientAsncDetailInfo.getUnRecieveQty().toString());
            }
        }
    }

    private void bindSpinnerItems(){
        if (asnInfos != null && asnInfos.size() != 0){
            //适配器
            arr_adapter= new ArrayAdapter<ClientAsnInfo>(this, R.layout.spinner_item, asnInfos);
            //设置样式
            arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            //加载适配器
            asnInfoSpinner.setAdapter(arr_adapter);
            //添加事件Spinner事件监听
            asnInfoSpinner.setOnItemSelectedListener(new ReceiveGoodsListActivity.SpinnerSelectedListener());
        }
    }

    private void loadData(){
        new ReceivingListAsync().execute();
    }

    private class ReceivingListAsync extends AsyncTask<Long, Integer, Response<ClientAsnInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientAsnInfos> result) {
            super.onPostExecute(result);

            if ("M".equals(result.getSeverityMsgType())) {
                asnInfos = result.getResults().getAsnInfos();
                bindSpinnerItems();
            } else {
                if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                    ToastUtil.show(ReceiveGoodsListActivity.this, result.getSeverityMsg());
                    finish();
                }else {
                    ToastUtil.show(ReceiveGoodsListActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientAsnInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(ReceiveGoodsListActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            //接口名拼写错误 将错就错
            String methodName = "getRecievingList";
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientAsnInfos>>() {
            }.getType());
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientAsnInfo currentItem = arr_adapter.getItem(arg2);
            selectedAsnInfoId = (Long) currentItem.getAsnId();
            arg0.setVisibility(View.VISIBLE);
            currentClientAsnDetailInfoList = currentItem.getDetails();
            detailIds.clear();
            for (int i = 0;i < currentClientAsnDetailInfoList.size();i++){
                detailIds.add(currentClientAsnDetailInfoList.get(i).getDetailId());
            }
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
