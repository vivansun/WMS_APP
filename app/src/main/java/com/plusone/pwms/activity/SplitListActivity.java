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
import com.plusone.pwms.enums.CustomMsgEnum;
import com.plusone.pwms.model.Bin;
import com.plusone.pwms.model.ClientCountRecord;
import com.plusone.pwms.model.ClientPickTicketInWave;
import com.plusone.pwms.model.ClientPutAwayInfo;
import com.plusone.pwms.model.ClientPutAwayInfoDetail;
import com.plusone.pwms.model.ClientPutAwayInfos;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.ClientWaveInfo;
import com.plusone.pwms.model.ClientWaveInfos;
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

public class SplitListActivity extends Activity {

    /** 单据列表 */
    private Spinner spiltSpinner;
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
    /** 下拉框选中波次编码 */
    private String waveCode;
    /** 表格选中的记录 */
    private ClientPickTicketInWave currentClientPickTicketInWave;

    private SmartTable<ClientPickTicketInWave> splitSmartTable;
    private Integer currentPosition = 0;
    Column<Boolean> operations;
    Column<String> codes;
    Column<String> plants;
    Column<String> relatedBill1s;
    Column<String> stockBins;
    Column<String> docks;
    Column<Double> quantitys;

    /** 当前波次详情列表 */
    List<ClientPickTicketInWave> clientPickTicketInWaveList;

    private List<ClientWaveInfo> waveInfos;
    private ArrayAdapter<ClientWaveInfo> arr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_list);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        startButton = (Button) findViewById(R.id.confirm_button);
        homeButton = findViewById(R.id.home_icon);
        spiltSpinner = (Spinner) findViewById(R.id.splitSpinner);
        splitSmartTable = findViewById(R.id.table);

        //加载数据
        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplitListActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(SplitListActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //开始按钮单击事件
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentClientPickTicketInWave != null && !TextUtils.isEmpty(waveCode)){
                    // 跳转到分拣确认页面
                    Intent confirmIntent = new Intent(SplitListActivity.this, SplitDetailActivity.class);
                    confirmIntent.putExtra("waveCode", waveCode);
                    confirmIntent.putExtra("currentClientPickTicketInWave", currentClientPickTicketInWave);
                    startActivityForResult(confirmIntent,16);
                }else {
                    ToastUtil.show(SplitListActivity.this, "请先选择一条记录");
                }
            }
        });
    }

    @Override
    //调用onActivityResult()方法来回调数据
    //onActivityResult()方法有三个参数，分别是requestCode,resultCode,data
    //分别对应的是请求码，处理结果和数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //这里是一个switch()方法，内置的变量是requestCode,即请求码
        switch (requestCode) {
            //第一个case是1，即对应之前startActivityForResult()方法当中的请求码
            case 16:
                if (resultCode == RESULT_OK){
                    loadData();
                }
                break;
        }
    }


    private void bindSpinnerItems(){
        if (waveInfos != null && waveInfos.size() != 0){
            //适配器
            arr_adapter= new ArrayAdapter<ClientWaveInfo>(this, R.layout.spinner_item, waveInfos);
            //设置样式
            arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            //加载适配器
            spiltSpinner.setAdapter(arr_adapter);
            //添加事件Spinner事件监听
            spiltSpinner.setOnItemSelectedListener(new SplitListActivity.SpinnerSelectedListener());
        }
    }

    private void InitTable(){
        //表格
        int size = DensityUtils.dp2px(this,16); //指定图标大小
        operations = new Column<>("勾选", "operation",new ImageResDrawFormat<Boolean>(size,size) {
            @Override
            protected Context getContext() {
                return SplitListActivity.this;
            }

            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if(isCheck){
                    return R.mipmap.check;
                }
                return R.mipmap.uncheck;
            }
        });

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
                splitSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                splitSmartTable.invalidate();
            }
        });
        
        codes = new Column<>("发货单号", "code");
        codes.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                splitSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                splitSmartTable.invalidate();
            }
        });

        plants = new Column<>("货主", "plant");
        plants.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                splitSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                splitSmartTable.invalidate();
            }
        });

        relatedBill1s = new Column<>("客户单号", "relatedBill1");
        relatedBill1s.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                splitSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                splitSmartTable.invalidate();
            }
        });

        stockBins = new Column<>("分拣库位", "stockBin");
        stockBins.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                splitSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                splitSmartTable.invalidate();
            }
        });

        docks = new Column<>("目标月台", "dock");
        docks.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                splitSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                splitSmartTable.invalidate();
            }
        });

        quantitys = new Column<>("待分拣数(EA)", "quantity");
        quantitys.setOnColumnItemClickListener(new OnColumnItemClickListener<Double>() {
            @Override
            public void onClick(Column<Double> column, String value, Double s, int position) {
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
                splitSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                splitSmartTable.invalidate();
            }
        });

        final TableData<ClientPickTicketInWave> tableData = new TableData<>("测试标题",clientPickTicketInWaveList, operations, codes, plants, relatedBill1s, stockBins, docks, quantitys);
        splitSmartTable.getConfig().setShowTableTitle(false);
        splitSmartTable.getConfig().setShowXSequence(false);
        splitSmartTable.setTableData(tableData);

        splitSmartTable.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(SplitListActivity.this, R.color.colorGrey);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
//        splitSmartTable.getConfig().setMinTableWidth(1024);   //设置最小宽度
    }

    /**
     * 勾选库位编码，并实时更新
     * @param position  被选择记录的行数，根据行数用来找到其他列中该行记录对应的信息
     * @param selectedState   当前的操作状态：选中 || 取消选中
     */
    private void radioCheck(int position, boolean selectedState){
        if(position >-1) {
            ClientPickTicketInWave rotorTemp = clientPickTicketInWaveList.get(position);
            if (selectedState && currentClientPickTicketInWave != rotorTemp) {
                currentClientPickTicketInWave = rotorTemp;
            } else if (!selectedState && currentClientPickTicketInWave == rotorTemp) {
                currentClientPickTicketInWave = null;
            }
        }
    }
    
    private void loadData(){
        new SplitListAsync().execute();
    }

    private class SplitListAsync extends AsyncTask<Long, Integer, Response<ClientWaveInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientWaveInfos> result) {
            super.onPostExecute(result);

            if ("M".equals(result.getSeverityMsgType())) {
                waveInfos = result.getResults().getWaveInfos();
                bindSpinnerItems();
            } else {
                if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                    ToastUtil.show(SplitListActivity.this, result.getSeverityMsg());
                    finish();
                }else {
                    ToastUtil.show(SplitListActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientWaveInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(SplitListActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getSplitList";
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientWaveInfos>>() {
            }.getType());
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientWaveInfo currentItem = arr_adapter.getItem(arg2);
            waveCode = currentItem.getCode();
            clientPickTicketInWaveList = currentItem.getDetails();
            arg0.setVisibility(View.VISIBLE);
            if (clientPickTicketInWaveList != null){
                InitTable();
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
