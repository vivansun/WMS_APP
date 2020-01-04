package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import com.plusone.pwms.model.ClientQuantInfo;
import com.plusone.pwms.model.ClientQuantInfos;
import com.plusone.pwms.model.ClientSkuInfo;
import com.plusone.pwms.model.ClientWarehouse;
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

public class LotSelectActivity extends Activity {

    /** 确认按钮 */
    private Button confirmButton;
    /** 取消按钮 */
    private Button cancelButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 清空按钮 */
    private Button clearButton;
    /** 搜索按钮 */
    private Button searchButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;
    /** 批次号 */
    private EditText lotSequenceET;
    /** 批次属性 */
    private EditText dispLotET;
    /** 批次号 */
    private String lotSequence;
    private String lastLotSequence;
    /** 批次属性 */
    private String dispLot;
    /** 前页面盘点单id */
    private Long countPlanId;
    /** 前页面物料id */
    private Long skuId;

    private List<ClientQuantInfo> lotInfos;

    private ClientQuantInfo currentLotInfo;

    /** 库位表格 */
    private SmartTable<ClientQuantInfo> lotSmartTable;
    Column<Boolean> operations;
    Column<String> lotSequences;
    Column<String> dispLots;

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
        setContentView(R.layout.activity_lot_select);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        cancelButton = (Button) findViewById(R.id.cancel_btn);
        clearButton = findViewById(R.id.clear);
        searchButton = findViewById(R.id.search);
        homeButton = findViewById(R.id.home_icon);
        lotSmartTable = (SmartTable<ClientQuantInfo>)findViewById(R.id.table);
        lotSequenceET = findViewById(R.id.lotSequence);
        dispLotET = findViewById(R.id.dispLot);

        //接收传递的数据
        Intent intent = getIntent();
        skuId = (Long) intent.getSerializableExtra("skuId");
        countPlanId = (Long) intent.getSerializableExtra("countPlanId");
        lastLotSequence = (String) intent.getSerializableExtra("lotSequence");

        lotSequenceET.setText(lastLotSequence);

        //加载数据
        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LotSelectActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(LotSelectActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLotInfo != null){
                    // 返回上一画面
                    Intent backIntent = new Intent(LotSelectActivity.this, CountDetailRegisterActivity.class);
                    backIntent.putExtra("currentLotInfo", currentLotInfo);
                    setResult(RESULT_OK,backIntent);
                    finish();
                }else {
                    ToastUtil.show(LotSelectActivity.this, "请选择一条物料记录");
                }
            }
        });

        //取消按钮单击事件
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LotSelectActivity.this.finish();
            }
        });

        //清空按钮单击事件
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lotSequenceET.setText("");
                dispLotET.setText("");
                lotSequence = "";
                dispLot = "";
            }
        });

        //查询按钮单击事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lotSequence = lotSequenceET.getText().toString();
                dispLot = dispLotET.getText().toString();
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
                return LotSelectActivity.this;
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
                lotSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                lotSmartTable.invalidate();
            }
        });
        
        lotSequences = new Column<>("批次号", "lotSequence");
        lotSequences.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                lotSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                lotSmartTable.invalidate();
            }
        });

        dispLots = new Column<>("批次属性", "dispLot");
        dispLots.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                lotSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                lotSmartTable.invalidate();
            }
        });


        final TableData<ClientQuantInfo> tableData = new TableData<>("测试标题",lotInfos, operations, lotSequences,dispLots);
        lotSmartTable.getConfig().setShowTableTitle(false);
        lotSmartTable.getConfig().setShowXSequence(false);
        lotSmartTable.setTableData(tableData);

        lotSmartTable.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(LotSelectActivity.this, R.color.colorGrey);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
//        lotSmartTable.getConfig().setMinTableWidth(1024);   //设置最小宽度
    }

    /**
     * 勾选库位编码，并实时更新
     * @param position  被选择记录的行数，根据行数用来找到其他列中该行记录对应的信息
     * @param selectedState   当前的操作状态：选中 || 取消选中
     */
    private void radioCheck(int position, boolean selectedState){
        if(position >-1) {
            ClientQuantInfo rotorTemp = lotInfos.get(position);
            if (selectedState && currentLotInfo != rotorTemp) {
                currentLotInfo = rotorTemp;
            } else if (!selectedState && currentLotInfo == rotorTemp) {
                currentLotInfo = null;
            }
        }
    }
    
    private void loadData(){
        new LotListAsync().execute();
    }

    private class LotListAsync extends AsyncTask<Long, Integer, Response<ClientQuantInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientQuantInfos> result) {
            super.onPostExecute(result);
            if ("M".equals(result.getSeverityMsgType())) {
                lotInfos = result.getResults().getQuantInfos();
                if(lotInfos != null)
                InitTable();
            } else {
                ToastUtil.show(LotSelectActivity.this, result.getSeverityMsg());
            }
        }


        @Override
        protected Response<ClientQuantInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(LotSelectActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getQuantList";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("countPlanId", countPlanId);
            parameters.put("skuId", skuId.toString());
            parameters.put("lotSequence", lotSequence);
            parameters.put("dispLot", dispLot);

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientQuantInfos>>() {
            }.getType());
        }
    }

}
