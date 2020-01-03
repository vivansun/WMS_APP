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
import com.plusone.pwms.enums.CustomMsgEnum;
import com.plusone.pwms.model.ClientBolInfo;
import com.plusone.pwms.model.ClientBolInfos;
import com.plusone.pwms.model.ClientPackInfo;
import com.plusone.pwms.model.ClientPackInfos;
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

public class BolConfirmDetailActivity extends Activity {

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
    /** 目标月台 */
    private TextView dockTv;
    /** 前页面对象 */
    private ClientBolInfo clientBolInfo;

    private List<ClientPackInfo> clientPackInfoList;

    private SmartTable<ClientPackInfo> bolConfirmDetailSmartTable;
    private Integer currentPosition = 0;
    Column<String> packNos;
    Column<String> skuCodes;
    Column<String> skuNames;
    Column<String> lotSequences;
    Column<String> planPackages;
    Column<Double> invBaseQtys;

    private LoadingDailog loadingDailog;

    private LoadingDailog.Builder builder = new LoadingDailog.Builder(BolConfirmDetailActivity.this)
            .setMessage("正在确认中...");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolconfirm_detail);

        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        loadingDailog = builder.create();

        //接收传递的数据
        Intent intent = getIntent();
        clientBolInfo = (ClientBolInfo) intent.getSerializableExtra("currentClientBolInfo");

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_btn);
        homeButton = findViewById(R.id.home_icon);
        bolConfirmDetailSmartTable = findViewById(R.id.table);
        codeTv = findViewById(R.id.code);
        dockTv = findViewById(R.id.dock);

        codeTv.setText(clientBolInfo.getCode());
        dockTv.setText(clientBolInfo.getDock());

        //加载数据
        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BolConfirmDetailActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(BolConfirmDetailActivity.this, MenuActivity.class);
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
                new BolConfirmAsync().execute();
            }
        });

        //返回按钮单击事件
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BolConfirmDetailActivity.this.finish();
            }
        });
    }

    private void InitTable(){

        packNos = new Column<>("包装号", "packNo");
        skuCodes = new Column<>("物料编码", "skuCode");
        skuNames = new Column<>("物料名称", "skuName");
        lotSequences = new Column<>("批次号", "lotSequence");
        planPackages = new Column<>("包装", "planPackage");
        invBaseQtys = new Column<>("数量", "invBaseQty");

        final TableData<ClientPackInfo> tableData = new TableData<>("测试标题",clientPackInfoList, packNos, skuCodes, skuNames, lotSequences, planPackages, invBaseQtys);
        bolConfirmDetailSmartTable.getConfig().setShowTableTitle(false);
        bolConfirmDetailSmartTable.getConfig().setShowXSequence(false);
        bolConfirmDetailSmartTable.setTableData(tableData);

        bolConfirmDetailSmartTable.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(BolConfirmDetailActivity.this, R.color.colorGrey);
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
//        splitSmartTable.getConfig().setMinTableWidth(1024);   //设置最小宽度
    }

    private void loadData(){
        new BolConfirmDetailActivity.BolConfirmDetailAsync().execute();
    }

    private class BolConfirmDetailAsync extends AsyncTask<Long, Integer, Response<ClientPackInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientPackInfos> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    clientPackInfoList= result.getResults().getPackInfos();
                    if (clientPackInfoList != null){
                        InitTable();
                    }
                } else {
                    ToastUtil.show(BolConfirmDetailActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientPackInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(BolConfirmDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getBolConfirmDetail";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("pickTicketId", clientBolInfo.getPickTicketId());

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientPackInfos>>() {
            }.getType());
        }
    }

    private class BolConfirmAsync extends AsyncTask<Long, Integer, Response<ClientBolInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientBolInfos> result) {
            super.onPostExecute(result);

            if ("M".equals(result.getSeverityMsgType())) {
                loadingDailog.dismiss();
                Intent backIntent = new Intent(BolConfirmDetailActivity.this, SplitListActivity.class);
                setResult(RESULT_OK,backIntent);
                finish();
            } else {
                loadingDailog.dismiss();
                if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())) {
                    ToastUtil.show(BolConfirmDetailActivity.this, "确认完毕");
                    Intent menuIntent = new Intent(BolConfirmDetailActivity.this, MenuActivity.class);
                    startActivity(menuIntent);
                    if (BolConfirmListActivity.instance != null){
                        BolConfirmListActivity.instance.finish();
                    }
                    finish();
                }else {
                    ToastUtil.show(BolConfirmDetailActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientBolInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(BolConfirmDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "bolConfirm";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("pickTicketId", clientBolInfo.getPickTicketId());

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientBolInfos>>() {
            }.getType());
        }
    }
}
