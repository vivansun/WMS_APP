package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.tu.loadingdialog.LoadingDailog;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.table.TableData;
import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.model.ClientPickTicketInWave;
import com.plusone.pwms.model.ClientTaskInfo;
import com.plusone.pwms.model.ClientTaskInfos;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.ClientWaveInfos;
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

public class SplitDetailActivity extends Activity {

    /** 确认按钮 */
    private Button confirmButton;
    /** 返回按钮 */
    private Button cancelButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;
    /** 单号 */
    private TextView codeTv;
    /** 客户单号 */
    private TextView relatedBill1Tv;
    /** 分拣库位 */
    private TextView stockBinTv;
    /** 目标月台 */
    private TextView dockTv;
    /** 前页面波次号 */
    private String waveCode;
    /** 前页面对象 */
    private ClientPickTicketInWave clientPickTicketInWave;

    private List<ClientTaskInfo> clientTaskInfoList;

    private SmartTable<ClientTaskInfo> splitDetailSmartTable;
    private Integer currentPosition = 0;
    Column<String> skuCodes;
    Column<String> skuNames;
    Column<String> lotSequences;
    Column<String> inboundDates;
    Column<String> trackSeqs;
    Column<String> packageNames;
    Column<Double> planPackQtys;

    private LoadingDailog loadingDailog;

    private LoadingDailog.Builder builder = new LoadingDailog.Builder(SplitDetailActivity.this)
            .setMessage("正在确认中...");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_detail);

        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        loadingDailog = builder.create();

        //接收传递的数据
        Intent intent = getIntent();
        clientPickTicketInWave = (ClientPickTicketInWave) intent.getSerializableExtra("currentClientPickTicketInWave");
        waveCode = intent.getStringExtra("waveCode");

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        cancelButton = (Button) findViewById(R.id.cancel_btn);
        homeButton = findViewById(R.id.home_icon);
        codeTv = findViewById(R.id.code);
        relatedBill1Tv = findViewById(R.id.relatedBill1);
        stockBinTv = findViewById(R.id.stockBin);
        dockTv = findViewById(R.id.dock);

        codeTv.setText(clientPickTicketInWave.getCode());
        relatedBill1Tv.setText(clientPickTicketInWave.getRelatedBill1());
        stockBinTv.setText(clientPickTicketInWave.getStockBin());
        dockTv.setText(clientPickTicketInWave.getDock());

        //加载数据
        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplitDetailActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(SplitDetailActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(SplitDetailActivity.this, "asdasd");
                //显示加载框
                loadingDailog.show();
                new SplitConfirmAsync().execute();
            }
        });

        //返回按钮单击事件
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplitDetailActivity.this.finish();
            }
        });
    }

    private void InitTable(){

        skuCodes = new Column<>("物料编码", "skuCode");

        skuNames = new Column<>("物料名称", "skuName");

        lotSequences = new Column<>("批次号", "lotSequence");

        inboundDates = new Column<>("入库日期", "inboundDate");

        trackSeqs = new Column<>("追溯号", "trackSeq");

        packageNames = new Column<>("包装", "packageName");

        planPackQtys = new Column<>("数量", "planPackQty");

        final TableData<ClientTaskInfo> tableData = new TableData<>("测试标题",clientTaskInfoList, skuCodes, skuNames, lotSequences, inboundDates, trackSeqs, packageNames, planPackQtys);
        splitDetailSmartTable.getConfig().setShowTableTitle(false);
        splitDetailSmartTable.getConfig().setShowXSequence(false);
        splitDetailSmartTable.setTableData(tableData);

        splitDetailSmartTable.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(SplitDetailActivity.this, R.color.colorGrey);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
//        splitSmartTable.getConfig().setMinTableWidth(1024);   //设置最小宽度
    }

    private void loadData(){
        new SplitDetailActivity.SplitDetailAsync().execute();
    }

    private class SplitDetailAsync extends AsyncTask<Long, Integer, Response<ClientTaskInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientTaskInfos> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    clientTaskInfoList= result.getResults().getTaskInfos();
                    if (clientTaskInfoList != null){
                        InitTable();
                    }
                } else {
                    ToastUtil.show(SplitDetailActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientTaskInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(SplitDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getSplitDetails";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("waveCode", waveCode);
            parameters.put("pickTicketId", clientPickTicketInWave.getPickTicketId());

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientTaskInfos>>() {
            }.getType());
        }
    }

    private class SplitConfirmAsync extends AsyncTask<Long, Integer, Response<ClientWaveInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientWaveInfos> result) {
            super.onPostExecute(result);

            if ("M".equals(result.getSeverityMsgType())) {
                loadingDailog.dismiss();
                Intent backIntent = new Intent(SplitDetailActivity.this, SplitListActivity.class);
                setResult(RESULT_OK,backIntent);
                finish();
            } else {
                loadingDailog.dismiss();
                ToastUtil.show(SplitDetailActivity.this, result.getSeverityMsg());
            }
        }


        @Override
        protected Response<ClientWaveInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(SplitDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "splitConfirm";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("waveCode", waveCode);
            parameters.put("pickTicketId", clientPickTicketInWave.getPickTicketId());

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientWaveInfos>>() {
            }.getType());
        }
    }
}
