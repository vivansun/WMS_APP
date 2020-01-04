package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.plusone.pwms.enums.CustomMsgEnum;
import com.plusone.pwms.model.ClientBolInfo;
import com.plusone.pwms.model.ClientBolInfos;
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

public class BolConfirmListActivity extends Activity {

    public static BolConfirmListActivity instance = null;
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

    private SmartTable<ClientBolInfo> bolConfirmSmartTable;
    private Integer currentPosition = 0;
    Column<Boolean> operations;
    Column<String> plants;
    Column<String> codes;
    Column<String> relatedBill1s;
    Column<String> etds;
    Column<String> docks;
    Column<Double> quantitys;


    private List<ClientBolInfo> bolInfos;
    private ClientBolInfo currentClientBolInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolconfirm_list);
        instance = this;

        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        startButton = (Button) findViewById(R.id.confirm_button);
        bolConfirmSmartTable = findViewById(R.id.table);
        homeButton = findViewById(R.id.home_icon);


        //加载数据
//        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BolConfirmListActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(BolConfirmListActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //开始按钮单击事件
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentClientBolInfo != null){
                    // 跳转到复核确认页面
                    Intent confirmIntent = new Intent(BolConfirmListActivity.this, BolConfirmDetailActivity.class);
                    confirmIntent.putExtra("currentClientBolInfo", currentClientBolInfo);
                    startActivityForResult(confirmIntent,16);
                }else {
                    ToastUtil.show(BolConfirmListActivity.this, "请先选择一条记录");
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

    private void InitTable(){
        //表格
        int size = DensityUtils.dp2px(this,16); //指定图标大小
        operations = new Column<>("勾选", "operation",new ImageResDrawFormat<Boolean>(size,size) {
            @Override
            protected Context getContext() {
                return BolConfirmListActivity.this;
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
                bolConfirmSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                bolConfirmSmartTable.invalidate();
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
                bolConfirmSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                bolConfirmSmartTable.invalidate();
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
                bolConfirmSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                bolConfirmSmartTable.invalidate();
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
                bolConfirmSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                bolConfirmSmartTable.invalidate();
            }
        });

        etds = new Column<>("预计发运日期", "etd");
        etds.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                bolConfirmSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                bolConfirmSmartTable.invalidate();
            }
        });

        docks = new Column<>("发货月台", "dock");
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
                bolConfirmSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                bolConfirmSmartTable.invalidate();
            }
        });

        quantitys = new Column<>("拣选数量", "quantity");
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
                bolConfirmSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                bolConfirmSmartTable.invalidate();
            }
        });

        final TableData<ClientBolInfo> tableData = new TableData<>("测试标题",bolInfos, operations, plants, codes, relatedBill1s, etds, docks, quantitys);
        bolConfirmSmartTable.getConfig().setShowTableTitle(false);
        bolConfirmSmartTable.getConfig().setShowXSequence(false);
        bolConfirmSmartTable.setTableData(tableData);

        bolConfirmSmartTable.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(BolConfirmListActivity.this, R.color.colorGrey);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
//        bolConfirmSmartTable.getConfig().setMinTableWidth(1024);   //设置最小宽度
    }

    /**
     * 勾选库位编码，并实时更新
     * @param position  被选择记录的行数，根据行数用来找到其他列中该行记录对应的信息
     * @param selectedState   当前的操作状态：选中 || 取消选中
     */
    private void radioCheck(int position, boolean selectedState){
        if(position >-1) {
            ClientBolInfo rotorTemp = bolInfos.get(position);
            if (selectedState && currentClientBolInfo != rotorTemp) {
                currentClientBolInfo = rotorTemp;
            } else if (!selectedState && currentClientBolInfo == rotorTemp) {
                currentClientBolInfo = null;
            }
        }
    }
    
    private void loadData(){
        new BolComfirmListAsync().execute();
    }

    private class BolComfirmListAsync extends AsyncTask<Long, Integer, Response<ClientBolInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientBolInfos> result) {
            super.onPostExecute(result);

            if ("M".equals(result.getSeverityMsgType())) {
                bolInfos = result.getResults().getBolInfos();
                if (bolInfos != null){
                    InitTable();
                }
            } else {
                if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                    ToastUtil.show(BolConfirmListActivity.this, result.getSeverityMsg());
                    finish();
                }else {
                    ToastUtil.show(BolConfirmListActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientBolInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(BolConfirmListActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getBolConfirmList";
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientBolInfos>>() {
            }.getType());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadData();
    }
}
