package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.plusone.pwms.model.Bin;
import com.plusone.pwms.model.Bins;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.model.UserAndWarehouse;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BlankBinsActivity extends Activity {

    /** 库位表格 */
    private SmartTable<Bin> binSmartTable;
    Column<String> binCodes;
    Column<Boolean> operations;
    List<Bin> binList = new ArrayList<>();
    /** 确认按钮 */
    private Button confirmButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;
    /** 全局中的用户与仓库列表 */
    private UserAndWarehouse userAndWarehouse;
    /** 选中行的库位编码 */
    private String selectedBinCode = "";
    /** 前页面传来的值 */
    private Long taskId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank_bins);

        application = (MyApplication) this.getApplication();
        userAndWarehouse = application.getUserAndWarehouse();
        user = userAndWarehouse.getUser();
        clientWarehouse = application.getClientWarehouse();
        confirmButton = (Button) findViewById(R.id.confirm_button);
        homeButton = findViewById(R.id.home_icon);
        binSmartTable = (SmartTable<Bin>)findViewById(R.id.table);
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        //接数据
        Intent intent = getIntent();
        taskId = (Long) intent.getLongExtra("taskId",0);
        //加载数据
        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlankBinsActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(BlankBinsActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回上架确认画面
                Intent backIntent = new Intent(BlankBinsActivity.this, UpperShelfDetailActivity.class);
                backIntent.putExtra("binCode", selectedBinCode);
                setResult(RESULT_OK,backIntent);
                finish();
            }
        });
    }

    private void loadData(){
        new BlankBinsActivity.GetBinsAsync().execute();
    }

    private void InitTable(){
        //表格
        binCodes = new Column<>("库位编码", "binCode");
        binCodes.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                binSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                binSmartTable.invalidate();
            }
        });

        int size = DensityUtils.dp2px(this,16); //指定图标大小
        operations = new Column<>("勾选", "operation",new ImageResDrawFormat<Boolean>(size,size) {
            @Override
            protected Context getContext() {
                return BlankBinsActivity.this;
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
                binSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                binSmartTable.invalidate();
            }
        });

        final TableData<Bin> tableData = new TableData<>("测试标题",binList, operations, binCodes);
        binSmartTable.getConfig().setShowTableTitle(false);
        binSmartTable.getConfig().setShowXSequence(false);
        binSmartTable.setTableData(tableData);

        binSmartTable.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(BlankBinsActivity.this, R.color.colorGrey);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
//        binSmartTable.getConfig().setMinTableWidth(1024);   //设置最小宽度
    }

    /**
     * 勾选库位编码，并实时更新
     * @param position  被选择记录的行数，根据行数用来找到其他列中该行记录对应的信息
     * @param selectedState   当前的操作状态：选中 || 取消选中
     */
    private void radioCheck(int position, boolean selectedState){
        List<String> rotorIdList = binCodes.getDatas();
        if(position >-1) {
            String rotorTemp = rotorIdList.get(position);
            if (selectedState && !selectedBinCode.equals(rotorTemp)) {
                selectedBinCode = rotorTemp;
            } else if (!selectedState && selectedBinCode.equals(rotorTemp)) {
                selectedBinCode = "";
            }
        }
    }


    private class GetBinsAsync extends AsyncTask<Long, Integer, Response<Bins>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<Bins> result) {
            super.onPostExecute(result);

            if ("M".equals(result.getSeverityMsgType())) {
                List<String> list = result.getResults().getBinInfos();
                Iterator<String> it = list.iterator();
                while(it.hasNext()) {
                    Bin newBin = new Bin();
                    newBin.setOperation(false);
                    newBin.setBinCode(it.next());
                    binList.add(newBin);
                }
                if (binList != null){
                    InitTable();
                }
            } else {
                ToastUtil.show(BlankBinsActivity.this, result.getSeverityMsg());
            }
        }


        @Override
        protected Response<Bins> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(BlankBinsActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getBlankBins";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("taskId", taskId);

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);


            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<Bins>>() {
            }.getType());
        }
    }
}
