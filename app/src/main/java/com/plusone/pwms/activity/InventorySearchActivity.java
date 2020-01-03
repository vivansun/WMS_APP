package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.model.ClientInvInfo;
import com.plusone.pwms.model.ClientInvInfos;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventorySearchActivity extends Activity {

    /** 清空按钮 */
    private Button clearButton;
    /** 搜索按钮 */
    private Button searchButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;
    /** 批次号 */
    private EditText lotSequenceET;
    /** 库位号 */
    private EditText binCodeET;
    /** 物料编码 */
    private EditText skuCodeET;
    /** 批次号 */
    private String lotSequence;
    /** 库位号 */
    private String binCode;
    /** 物料编码 */
    private String skuCode;

    private List<ClientInvInfo> invInfos;

    //当前点击的列表位置
    private int currentPosition;

//    private ClientInvInfo clientInvInfo;

    private SwipeRecyclerView listView;

    private LinearLayoutManager layoutManager;

//    private SwipeRefreshLayout mRefreshLayout;

    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_search);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        homeButton = findViewById(R.id.home_icon);
        clearButton = findViewById(R.id.clear);
        searchButton = findViewById(R.id.search);
        lotSequenceET = findViewById(R.id.lotSequence);
        binCodeET = findViewById(R.id.binCode);
        skuCodeET = findViewById(R.id.skuCode);

//        mRefreshLayout = findViewById(R.id.refresh_layout);
//        mRefreshLayout.setRefreshing(true);
//        mRefreshLayout.setOnRefreshListener(mRefreshListener); // 刷新监听。

        // 创建菜单：
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem showItem = new SwipeMenuItem(getApplicationContext());
                showItem.setBackground(new ColorDrawable(Color.parseColor("#3CADE7")));//设置背景
                showItem.setWidth(130);//设置滑出 项 宽度
                showItem.setText("移位");
                showItem.setTextColor(Color.parseColor("#ffffff"));
                showItem.setHeight(ViewGroup.MarginLayoutParams.MATCH_PARENT);
                showItem.setTextSize(14);
                // add to menu
                rightMenu.addMenuItem(showItem);
            }
        };

        OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int position) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();
                currentPosition = position;
                final ClientInvInfo clientInvInfo = invInfos.get(position);
                // 左侧还是右侧菜单：
                int direction = menuBridge.getDirection();
                if (direction == -1) {
                    // 菜单在Item中的Position：
                    switch (menuBridge.getPosition()) {
                        //移位按钮
                        case 0:
                            // 跳转到移位确认页面
                            Intent intent = new Intent(InventorySearchActivity.this, MoveTaskConfirmActivity.class);
                            intent.putExtra("clientInvInfo", clientInvInfo);
                            intent.putExtra("binCode",binCode);
                            intent.putExtra("lotSequence",lotSequence);
                            intent.putExtra("skuCode",skuCode);
                            startActivityForResult(intent, 14);
                            break;
                    }
                }
            }
        };


        listView = (SwipeRecyclerView) findViewById(R.id.listView1);
        //        就是 recyclerview 的显示效果
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.setOnItemMenuClickListener(mItemMenuClickListener);
        listView.setSwipeMenuCreator(mSwipeMenuCreator); // 须在适配器上面
        myAdapter = new MyAdapter(InventorySearchActivity.this);
        listView.setAdapter(myAdapter);


        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventorySearchActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(InventorySearchActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //清空按钮单击事件
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lotSequenceET.setText("");
                binCodeET.setText("");
                skuCodeET.setText("");
                lotSequence = "";
                binCode = "";
                skuCode = "";
            }
        });

        //查询按钮单击事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lotSequence = lotSequenceET.getText().toString();
                binCode = binCodeET.getText().toString();
                skuCode = skuCodeET.getText().toString();
                if(TextUtils.isEmpty(lotSequence) && TextUtils.isEmpty(binCode) && TextUtils.isEmpty(skuCode)){
                    ToastUtil.show(InventorySearchActivity.this, "查询条件不能同时为空");
                }else {
                    loadData();
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_inventory_list_item, viewGroup, false);
            MyHolder myHolder = new MyAdapter.MyHolder(view);
            return myHolder;
        }

        @Override
        public int getItemCount() {
            if (invInfos != null && invInfos.size() != 0){
                return invInfos.size();
            }else {
                return 0;
            }
        }

        // 绑定类
        public class MyHolder extends RecyclerView.ViewHolder {

            TextView contentTv;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                contentTv = itemView.findViewById(R.id.content);
            }
        }

        //   实际绑定 item 操作
        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {

            if (invInfos != null && invInfos.size() != 0){
                ClientInvInfo clientInvInfo = invInfos.get(position);
                String s = "";
                if (!TextUtils.isEmpty(clientInvInfo.getSkuCode())){
                    s+=clientInvInfo.getSkuCode();
                    s+="/";
                }
                if (!TextUtils.isEmpty(clientInvInfo.getSkuName())){
                    s+=clientInvInfo.getSkuName();
                    s+="/";
                }
                if (!TextUtils.isEmpty(clientInvInfo.getInvStatus())){
                    s+=clientInvInfo.getInvStatus();
                    s+="/";
                }
                if (!TextUtils.isEmpty(clientInvInfo.getLotSequence())){
                    s+=clientInvInfo.getLotSequence();
                    s+="/";
                }
                if (!TextUtils.isEmpty(clientInvInfo.getDispLot())){
                    s+=clientInvInfo.getDispLot();
                    s+="/";
                }
                if (!TextUtils.isEmpty(clientInvInfo.getPalletSeq())){
                    s+=clientInvInfo.getPalletSeq();
                    s+="/";
                }
                if (!TextUtils.isEmpty(clientInvInfo.getPlanPackage())){
                    s+=clientInvInfo.getPlanPackage();
                    s+="/";
                }
                if (clientInvInfo.getInvPackQty() != null){
                    s+=clientInvInfo.getInvPackQty().toString();
                    s+="/";
                }
                if (clientInvInfo.getInvBaseQty() != null){
                    s+=clientInvInfo.getInvBaseQty().toString();
                    s+="/";
                }
                if (!TextUtils.isEmpty(clientInvInfo.getTrackSeq())){
                    s+=clientInvInfo.getTrackSeq();
                    s+="/";
                }
                if (!TextUtils.isEmpty(clientInvInfo.getInboundDate())){
                    s+=clientInvInfo.getInboundDate();
                }
                myHolder.contentTv.setText(s);
            }
        }
    }

//    /**
//     * 刷新。
//     */
//    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
//        @Override
//        public void onRefresh() {
//            loadData();
//        }
//    };

    @Override
    //调用onActivityResult()方法来回调数据
    //onActivityResult()方法有三个参数，分别是requestCode,resultCode,data
    //分别对应的是请求码，处理结果和数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //这里是一个switch()方法，内置的变量是requestCode,即请求码
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //第一个case是1，即对应之前startActivityForResult()方法当中的请求码
                case 14:
                    binCodeET.setText(data.getStringExtra("binCode"));
                    lotSequenceET.setText(data.getStringExtra("lotSequence"));
                    skuCodeET.setText(data.getStringExtra("skuCode"));
                    binCode = binCodeET.getText().toString();
                    lotSequence = lotSequenceET.getText().toString();
                    skuCode = skuCodeET.getText().toString();
                    loadData();
                    break;
            }
        }
    }

    private void loadData(){
        new InventoryAsync().execute();
    }

    private class InventoryAsync extends AsyncTask<Long, Integer, Response<ClientInvInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientInvInfos> result) {
            super.onPostExecute(result);
            if ("M".equals(result.getSeverityMsgType())) {
                invInfos = result.getResults().getInvInfos();
                myAdapter.notifyDataSetChanged();
            } else {
                ToastUtil.show(InventorySearchActivity.this, result.getSeverityMsg());
            }
        }


        @Override
        protected Response<ClientInvInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(InventorySearchActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getInvInfo";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("binCode", binCode);
            parameters.put("skuCode", skuCode);
            parameters.put("lotSequence", lotSequence);

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientInvInfos>>() {
            }.getType());
        }
    }

}
