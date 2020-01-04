package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.enums.CustomMsgEnum;
import com.plusone.pwms.model.ClientTaskInfo;
import com.plusone.pwms.model.ClientTaskInfos;
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

public class MoveTaskListActivity extends Activity {

    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;

    private SwipeRecyclerView listView;

    private LinearLayoutManager layoutManager;

//    private SwipeRefreshLayout mRefreshLayout;

    private MyAdapter myAdapter;

    //当前点击的列表位置
    private int currentPosition;

    /** 移位列表 */
    private List<ClientTaskInfo> taskInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_task_list);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        homeButton = findViewById(R.id.home_icon);


//        mRefreshLayout = findViewById(R.id.refresh_layout);
//        mRefreshLayout.setRefreshing(true);
//        mRefreshLayout.setOnRefreshListener(mRefreshListener); // 刷新监听。

        //加载数据
//        loadData();

        // 创建菜单：
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem showItem = new SwipeMenuItem(getApplicationContext());
                showItem.setBackground(new ColorDrawable(Color.parseColor("#3CADE7")));//设置背景
                showItem.setWidth(130);//设置滑出 项 宽度
                showItem.setText("开始作业");
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
                final ClientTaskInfo clientTaskInfo = taskInfos.get(position);
                // 左侧还是右侧菜单：
                int direction = menuBridge.getDirection();
                if (direction == -1) {
                    // 菜单在Item中的Position：
                    switch (menuBridge.getPosition()) {
                        //查看按钮
                        case 0:
                            // 跳转到任务信息页面
                            Intent intent = new Intent(MoveTaskListActivity.this, MoveTaskDetailActivity.class);
                            intent.putExtra("clientTaskInfo", clientTaskInfo);
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
        myAdapter = new MyAdapter(MoveTaskListActivity.this);
        listView.setAdapter(myAdapter);


        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveTaskListActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(MoveTaskListActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_move_task_list_item, viewGroup, false);
            MyHolder myHolder = new MyAdapter.MyHolder(view);
            return myHolder;
        }

        @Override
        public int getItemCount() {
            if (taskInfos != null && taskInfos.size() != 0){
                return taskInfos.size();
            }else {
                return 0;
            }
        }

        // 绑定类
        public class MyHolder extends RecyclerView.ViewHolder {

            TextView srcBinTv;
            TextView destBinTv;
            TextView skuCodeTv;
            TextView skuNameTv;
            TextView palletSeqTv;
            TextView pkgNameTv;
            TextView planPackQtyTv;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                srcBinTv = itemView.findViewById(R.id.srcBin);
                destBinTv = itemView.findViewById(R.id.destBin);
                skuCodeTv = (TextView) itemView.findViewById(R.id.sku_code);
                skuNameTv = (TextView) itemView.findViewById(R.id.sku_name);
                palletSeqTv = itemView.findViewById(R.id.palletSeq);
                pkgNameTv = itemView.findViewById(R.id.pkgName);
                planPackQtyTv = (TextView) itemView.findViewById(R.id.planPackQty);
            }
        }

        //   实际绑定 item 操作
        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {

            if (taskInfos != null && taskInfos.size() != 0){
                ClientTaskInfo clientTaskInfo = taskInfos.get(position);
                myHolder.srcBinTv.setText(clientTaskInfo.getSrcBin());
                myHolder.destBinTv.setText(clientTaskInfo.getDestBin());
                myHolder.skuCodeTv.setText(clientTaskInfo.getSkuCode());
                myHolder.skuNameTv.setText(clientTaskInfo.getSkuName());
                myHolder.palletSeqTv.setText(clientTaskInfo.getPalletSeq());
                myHolder.pkgNameTv.setText(clientTaskInfo.getPackageName());
                myHolder.planPackQtyTv.setText(clientTaskInfo.getPlanPackQty().toString());
            }
        }
    }

    private void loadData(){
        new MoveTaskListAsync().execute();
    }

    /**
     * 刷新。
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData();
        }
    };

    private class MoveTaskListAsync extends AsyncTask<Long, Integer, Response<ClientTaskInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientTaskInfos> result) {
            super.onPostExecute(result);
            if ("M".equals(result.getSeverityMsgType())) {
                taskInfos = result.getResults().getTaskInfos();
                myAdapter.notifyDataSetChanged();
            } else {
                if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                    ToastUtil.show(MoveTaskListActivity.this, result.getSeverityMsg());
                    finish();
                }else {
                    ToastUtil.show(MoveTaskListActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientTaskInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(MoveTaskListActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getMoveTaskList";
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientTaskInfos>>() {
            }.getType());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadData();
    }
}
