package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.draw.ImageResDrawFormat;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.bin.david.form.utils.DensityUtils;
import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.model.Bin;
import com.plusone.pwms.model.ClientPickInfo;
import com.plusone.pwms.model.ClientSkuInfo;
import com.plusone.pwms.model.ClientSkuInfos;
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

public class SkuSelectActivity extends Activity {

    /** 确认按钮 */
    private Button confirmButton;
    /** 取消按钮 */
    private Button cancelButton;
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
    /** 物料编码 */
    private EditText skuCodeET;
    /** 物料名称 */
    private EditText skuNameET;
    /** 物料规格 */
    private EditText skuSpecET;
    /** 前页面传的物料编码 */
    private String skuCode;
    /** 物料名称 */
    private String skuName;
    /** 物料规格 */
    private String skuSpec;
    /** 前页面盘点单id */
    private Long countPlanId;

    private List<ClientSkuInfo> skuInfos;

    private ClientSkuInfo currentSkuInfo;

    /** 库位表格 */
    private SmartTable<ClientSkuInfo> skuSmartTable;
    Column<Boolean> operations;
    Column<String> skuCodes;
    Column<String> skuNames;
    Column<String> skuSpecs;

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
        setContentView(R.layout.activity_sku_select);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        cancelButton = (Button) findViewById(R.id.cancel_btn);
        homeButton = findViewById(R.id.home_icon);
        clearButton = findViewById(R.id.clear);
        searchButton = findViewById(R.id.search);
        skuSmartTable = (SmartTable<ClientSkuInfo>)findViewById(R.id.table);
        skuCodeET = findViewById(R.id.sku_code);
        skuNameET = findViewById(R.id.sku_name);
        skuSpecET = findViewById(R.id.sku_spec);

        //接收传递的数据
        Intent intent = getIntent();
        skuCode = (String) intent.getSerializableExtra("skuCode");
        countPlanId = (Long) intent.getSerializableExtra("countPlanId");

        skuCodeET.setText(skuCode);

        //加载数据
        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkuSelectActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(SkuSelectActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSkuInfo != null) {
                    // 返回上一画面
                    Intent backIntent = new Intent(SkuSelectActivity.this, CountDetailRegisterActivity.class);
                    backIntent.putExtra("currentSkuInfo", currentSkuInfo);
                    setResult(RESULT_OK,backIntent);
                    finish();
                }else {
                    ToastUtil.show(SkuSelectActivity.this, "请选择一条物料记录");
                }

            }
        });

        //取消按钮单击事件
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkuSelectActivity.this.finish();
            }
        });

        //清空按钮单击事件
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skuCodeET.setText("");
                skuNameET.setText("");
                skuSpecET.setText("");
                skuCode = "";
                skuName = "";
                skuSpec = "";
            }
        });

        //查询按钮单击事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skuCode = skuCodeET.getText().toString();
                skuName = skuNameET.getText().toString();
                skuSpec = skuSpecET.getText().toString();
                loadData();
            }
        });
    }

    private void InitTable(){
        //表格

        int size = DensityUtils.dp2px(this,16); //指定图标大小
        operations = new Column<>("勾选", "operation",new ImageResDrawFormat<Boolean>(size,size) {
            @Override
            protected Context getContext() {
                return SkuSelectActivity.this;
            }

            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if(isCheck){
                    return R.mipmap.check;
                }
                return R.mipmap.uncheck;
            }
        });
        operations.setFixed(true);
        operations.setOnColumnItemClickListener(new OnColumnItemClickListener<Boolean>() {
            @Override
            public void onClick(Column<Boolean> column, String value, Boolean s, int position) {
                if(operations.getDatas().get(position)){
                    radioCheck(position, false);
                    operations.getDatas().set(position,false);
                }else{
                    radioCheck(position, true);
                    for (int i = 0;i < operations.getDatas().size();i++){
                        operations.getDatas().set(i,false);
                    }
                    operations.getDatas().set(position,true);
                }
                skuSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                skuSmartTable.invalidate();
            }
        });
        
        skuCodes = new Column<>("商品编号", "skuCode");
        skuCodes.setFast(true);
        skuCodes.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                if(operations.getDatas().get(position)){
                    radioCheck(position, false);
                    operations.getDatas().set(position,false);
                }else{
                    radioCheck(position, true);
                    for (int i = 0;i < operations.getDatas().size();i++){
                        operations.getDatas().set(i,false);
                    }
                    operations.getDatas().set(position,true);
                }
                skuSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                skuSmartTable.invalidate();
            }
        });

        skuNames = new Column<>("商品名称", "skuName");
        skuNames.setFast(true);
        skuNames.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                if(operations.getDatas().get(position)){
                    radioCheck(position, false);
                    operations.getDatas().set(position,false);
                }else{
                    radioCheck(position, true);
                    for (int i = 0;i < operations.getDatas().size();i++){
                        operations.getDatas().set(i,false);
                    }
                    operations.getDatas().set(position,true);
                }
                skuSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                skuSmartTable.invalidate();
            }
        });

        skuSpecs = new Column<>("规格", "skuSpec");
        skuSpecs.setFast(true);
        skuSpecs.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                if(operations.getDatas().get(position)){
                    radioCheck(position, false);
                    operations.getDatas().set(position,false);
                }else{
                    radioCheck(position, true);
                    for (int i = 0;i < operations.getDatas().size();i++){
                        operations.getDatas().set(i,false);
                    }
                    operations.getDatas().set(position,true);
                }
                skuSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                skuSmartTable.invalidate();
            }
        });

        final TableData<ClientSkuInfo> tableData = new TableData<>("测试标题",skuInfos, operations, skuCodes,skuNames,skuSpecs);
        skuSmartTable.getConfig().setShowTableTitle(false);
        skuSmartTable.getConfig().setShowXSequence(false);
        skuSmartTable.setTableData(tableData);


        skuSmartTable.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(SkuSelectActivity.this, R.color.colorGrey);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
//        skuSmartTable.getConfig().setMinTableWidth(1024);   //设置最小宽度
    }

    /**
     * 勾选库位编码，并实时更新
     * @param position  被选择记录的行数，根据行数用来找到其他列中该行记录对应的信息
     * @param selectedState   当前的操作状态：选中 || 取消选中
     */
    private void radioCheck(int position, boolean selectedState){
        if(position >-1) {
            ClientSkuInfo rotorTemp = skuInfos.get(position);
            if (selectedState && currentSkuInfo != rotorTemp) {
                currentSkuInfo = rotorTemp;
            } else if (!selectedState && currentSkuInfo == rotorTemp) {
                currentSkuInfo = null;
            }
        }
    }
    
    private void loadData(){
        new SkuListAsync().execute();
    }

    private class SkuListAsync extends AsyncTask<Long, Integer, Response<ClientSkuInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientSkuInfos> result) {
            super.onPostExecute(result);
            if ("M".equals(result.getSeverityMsgType())) {
                skuInfos = result.getResults().getSkuInfos();
                if (skuInfos != null){
                    InitTable();
                }
            } else {
                ToastUtil.show(SkuSelectActivity.this, result.getSeverityMsg());
            }
        }


        @Override
        protected Response<ClientSkuInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(SkuSelectActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getSkuList";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("countPlanId", countPlanId);
            parameters.put("skuCode", skuCode);
            parameters.put("skuName", skuName);
            parameters.put("skuSpec", skuSpec);

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientSkuInfos>>() {
            }.getType());
        }
    }

}
