package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.model.ClientPackageInfo;
import com.plusone.pwms.model.ClientTaskInfo;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.EMDKConfigure;
import com.plusone.pwms.model.MoveTaskDetail;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.DialogUtil;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScannerResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveTaskDetailActivity extends Activity {

    /** 包装列表 */
    private Spinner packageSpinner;
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
    /** 物料编码 */
    private EditText skuCodeET;
    /** 物料名称 */
    private EditText skuNameET;
    /** 计划包装及折算 */
    private EditText planPkgAndCoefficientET;
    /** 批次号 */
    private EditText lotSequenceET;
    /** 批次信息 */
    private EditText dispLotET;
    /** 原始库位 */
    private EditText srcBinET;
    /** 目标库位 */
    private EditText destBinET;
    /** 托盘号 */
    private EditText palletSeqET;
    /** 将执行包装数量 */
    private EditText executePackQtyET;
    private String executePackQty;
    /** 包装数量 */
    private EditText unExecutePackQtyET;
    /** 输入的目标库位 */
    private EditText binScanConfirmET;
    private String destBinCode;
    /** 计划包装 */
    private Long planPkg;
    /** 前页面对象 */
    private ClientTaskInfo clientTaskInfo;
    /** 任务id */
    private Long taskId;
    /** 下拉框选中包装id */
    private Long selectedpkgId;

    private List<ClientPackageInfo> clientPackageInfos;
    private ArrayAdapter<ClientPackageInfo> arr_adapter;

//    private EMDKResults results = null;
//
//    //后台切回前台
//    @Override
//    protected void startScanChange() {
//        if (results == null) {
//            results = EMDKManager.getEMDKManager(getApplicationContext(), this);
//        }
//    }
//
//    //前台切到后台
//    @Override
//    protected void stopScanChange() {
//        results = null;
//    }


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
        setContentView(R.layout.activity_move_task_detail);

//        results = EMDKManager.getEMDKManager(getApplicationContext(), this);
//        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
//            ToastUtil.show(getApplicationContext(), "EMDKManager object request failed!");
//            return;
//        }

        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();

        //接收传递的数据
        Intent intent = getIntent();
        clientTaskInfo = (ClientTaskInfo) intent.getSerializableExtra("clientTaskInfo");
        taskId = clientTaskInfo.getTaskId();

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        homeButton = findViewById(R.id.home_icon);
        skuCodeET = findViewById(R.id.sku_code);
        skuNameET = findViewById(R.id.sku_name);
        lotSequenceET = findViewById(R.id.lotSequence);
        dispLotET = findViewById(R.id.dispLot);
        srcBinET = findViewById(R.id.srcBin);
        destBinET = findViewById(R.id.destBin);
        palletSeqET = findViewById(R.id.palletSeq);
        planPkgAndCoefficientET = findViewById(R.id.set_plan_package_and_coefficient);
        unExecutePackQtyET = findViewById(R.id.unExecutePackQty);
        packageSpinner = (Spinner) findViewById(R.id.packageSpinner);
        executePackQtyET = findViewById(R.id.executePackQty);
        binScanConfirmET = findViewById(R.id.bin_scan_confirm);
        skuCodeET.setEnabled(false);
        skuNameET.setEnabled(false);
        planPkgAndCoefficientET.setEnabled(false);
        unExecutePackQtyET.setEnabled(false);
        lotSequenceET.setEnabled(false);
        dispLotET.setEnabled(false);
        srcBinET.setEnabled(false);
        destBinET.setEnabled(false);
        palletSeqET.setEnabled(false);

        //加载数据
        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveTaskDetailActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(MoveTaskDetailActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(MoveTaskDetailActivity.this, "asdasd");
                if (TextUtils.isEmpty(executePackQtyET.getText())){
                    ToastUtil.show(MoveTaskDetailActivity.this, "执行包装数量不能为空");
                }else if (TextUtils.isEmpty(binScanConfirmET.getText())){
                    ToastUtil.show(MoveTaskDetailActivity.this, "库位扫描确认不能为空");
                }else if (1 != 1){   //此处为未执行数量（A） 与 executePackQty（B） * 折算（C） 的大小判断   须修改条件   可能要在  A < C*(B+1)时提示  不能超出计划数量来移位
                    //TODO

                }else{
                    executePackQty = executePackQtyET.getText().toString();
                    destBinCode = binScanConfirmET.getText().toString();
                    new MoveConfirmAsync().execute();
                }
            }
        });
    }

//    //获取扫描枪扫描数据
//    @Override
//    public void init(ScanDataCollection scanDataCollection) {
//        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
//            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
//            for (ScanDataCollection.ScanData data : scanData) {
//                String dataString = data.getData();
//                MoveTaskDetailActivity.AsyncDataUpdate asyncDataUpdate = new MoveTaskDetailActivity.AsyncDataUpdate();
//                asyncDataUpdate.execute(dataString);
//            }
//        }
//    }
//
//
//    private class AsyncDataUpdate extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            return params[0];
//        }
//
//        protected void onPostExecute(String result) {
//            if (result != null) {
//                binScanConfirmET.setText(result);
//            }
//        }
//    }

    private void bindSpinnerItems(){
        if (clientPackageInfos != null && clientPackageInfos.size() != 0){
            //适配器
            arr_adapter= new ArrayAdapter<ClientPackageInfo>(this, R.layout.spinner_item, clientPackageInfos);
            //设置样式
            arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            //加载适配器
            packageSpinner.setAdapter(arr_adapter);
            //添加事件Spinner事件监听
            packageSpinner.setOnItemSelectedListener(new MoveTaskDetailActivity.SpinnerSelectedListener());
            //设置默认值
            if(planPkg != null){
                for (int i = 0;i < arr_adapter.getCount();i++){
                    if(planPkg == arr_adapter.getItem(i).getPackageId()){
                        packageSpinner.setSelection(i,true);

                    }
                }
            }
        }
    }

    private void loadData(){
        new MoveTaskDetailActivity.MoveTaskDetailAsync().execute();
    }

    private class MoveTaskDetailAsync extends AsyncTask<Long, Integer, Response<MoveTaskDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<MoveTaskDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    MoveTaskDetail moveTaskDetail= result.getResults();
                    skuCodeET.setText(moveTaskDetail.getSkuCode());
                    skuNameET.setText(moveTaskDetail.getSkuName());
                    lotSequenceET.setText(moveTaskDetail.getLotSequence());
                    dispLotET.setText(moveTaskDetail.getDispLot());
                    srcBinET.setText(moveTaskDetail.getSrcBin());
                    destBinET.setText(moveTaskDetail.getDestBin());
                    palletSeqET.setText(moveTaskDetail.getPalletSeq());
                    unExecutePackQtyET.setText(moveTaskDetail.getUnExecutePackQty().toString());
                    planPkg = moveTaskDetail.getPlanPkg();
                    clientPackageInfos = moveTaskDetail.getPkgInfos();
                    taskId = moveTaskDetail.getTaskId();
                    if (clientPackageInfos != null && clientPackageInfos.size() > 0){
                        for (int i = 0; i<clientPackageInfos.size();i++){
                            if (clientPackageInfos.get(i).getPackageId() == planPkg){
                                planPkgAndCoefficientET.setText(clientPackageInfos.get(i).getPackageName()+"/"+clientPackageInfos.get(i).getCoefficient());
                            }
                        }
                    }
                    bindSpinnerItems();
                } else {
                    ToastUtil.show(MoveTaskDetailActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<MoveTaskDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(MoveTaskDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getMoveTaskDetail";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            if(taskId != null){
                parameters.put("taskId", taskId);
            }
            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<MoveTaskDetail>>() {
            }.getType());
        }
    }

    private class MoveConfirmAsync extends AsyncTask<Long, Integer, Response<MoveTaskDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<MoveTaskDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    MoveTaskDetail moveTaskDetail= result.getResults();
                    if (moveTaskDetail.getUnExecutePackQty()>0){
                        unExecutePackQtyET.setText(moveTaskDetail.getUnExecutePackQty().toString());
                    }else {
                        DialogUtil.showNormalDialogNoCancle(MoveTaskDetailActivity.this, "操作完成", "确认后返回任务列表画面",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //关闭扫描
//                                        onClosed();
                                        MoveTaskDetailActivity.this.finish();
                                    }
                                });
                    }
                } else {
                    ToastUtil.show(MoveTaskDetailActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<MoveTaskDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(MoveTaskDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "moveConfirm";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("taskId", taskId);
            parameters.put("pkgId", selectedpkgId);
            parameters.put("executePackQty", executePackQty);
            parameters.put("destBinCode", taskId);

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<MoveTaskDetail>>() {
            }.getType());
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientPackageInfo currentItem = arr_adapter.getItem(arg2);
            selectedpkgId = (Long) currentItem.getPackageId();
            arg0.setVisibility(View.VISIBLE);

        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
