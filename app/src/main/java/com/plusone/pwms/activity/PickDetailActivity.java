package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
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
import com.plusone.pwms.enums.CustomMsgEnum;
import com.plusone.pwms.model.ClientPackageInfo;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.PickDetail;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickDetailActivity extends Activity {

    /** 包装列表 */
    private Spinner packageSpinner;
    /** 确认按钮 */
    private Button confirmButton;
    /** 下一条按钮 */
    private Button nextButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;
    /** 作业单号 */
    private EditText taskIdET;
    /** 托盘号 */
    private EditText palletSeqET;
    /** 原始库位 */
    private EditText srcBinET;
    /** 目标库位 */
    private EditText destBinET;
    /** 库位扫描确认 */
    private EditText binScanConfirmET;
    /** 物料编码 */
    private EditText skuCodeET;
    /** 物料名称 */
    private EditText skuNameET;
    /** 计划包装及折算 */
    private EditText planPkgAndCoefficientET;
    /** 计划包装数量/EA数 */
    private EditText unExecutePackQtyAndEAET;
    /** 批次号 */
    private EditText lotSequenceET;
    /** 批次信息 */
    private EditText dispLotET;
    /** 将执行包装数量 */
    private EditText unExecutePackQtyET;
    /** 将执行包装数量 */
    private Double executePackQty;
    /** 未执行数量 */
    private Double unExecuteEaQty;
    /** 计划包装 */
    private Long planPkg;
    /** 前页面收货追溯号 */
    private String selectedBusinessType;
    /** 前页面收货月台Id */
    private Long selectedDocId;
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
        setContentView(R.layout.activity_pick_detail);

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
        selectedBusinessType = (String) intent.getSerializableExtra("selectedBusinessType");
        selectedDocId = (Long) intent.getSerializableExtra("selectedDocId");

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        nextButton = (Button) findViewById(R.id.next);
        homeButton = findViewById(R.id.home_icon);
        taskIdET = findViewById(R.id.task_id);
        palletSeqET = findViewById(R.id.palletSeq);
        srcBinET = findViewById(R.id.srcBin);
        destBinET = findViewById(R.id.destBin);
        binScanConfirmET = findViewById(R.id.bin_scan_confirm);
        skuCodeET = findViewById(R.id.sku_code);
        skuNameET = findViewById(R.id.sku_name);
        planPkgAndCoefficientET = findViewById(R.id.set_plan_package_and_coefficient);
        unExecutePackQtyAndEAET = findViewById(R.id.unExecutePackQtyAndEA);
        lotSequenceET = findViewById(R.id.lotSequence);
        dispLotET = findViewById(R.id.dispLot);
        packageSpinner = (Spinner) findViewById(R.id.packageSpinner);
        unExecutePackQtyET = findViewById(R.id.unExecutePackQty);
        taskIdET.setEnabled(false);
        palletSeqET.setEnabled(false);
        srcBinET.setEnabled(false);
        destBinET.setEnabled(false);
        skuCodeET.setEnabled(false);
        skuNameET.setEnabled(false);
        planPkgAndCoefficientET.setEnabled(false);
        unExecutePackQtyAndEAET.setEnabled(false);
        lotSequenceET.setEnabled(false);
        dispLotET.setEnabled(false);

        //加载数据
        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickDetailActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(PickDetailActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(PickDetailActivity.this, "asdasd");
                if (TextUtils.isEmpty(binScanConfirmET.getText())){
                    ToastUtil.show(PickDetailActivity.this, "库位扫描确认为空");
                }else if (!binScanConfirmET.getText().toString().equals(srcBinET.getText().toString())){
                    ToastUtil.show(PickDetailActivity.this, "库位扫描确认与计划库位不相同");
                }else{
                    executePackQty = Double.parseDouble(unExecutePackQtyET.getText().toString());
                    new PickDetailActivity.PickConfirmAsync().execute();
                }
            }
        });

        //下一条按钮单击事件
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(PickDetailActivity.this, "zxczxc");
                loadData();
            }
        });
    }

    //获取扫描枪扫描数据
//    @Override
//    public void init(ScanDataCollection scanDataCollection) {
//
//        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
//            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
//            for (ScanDataCollection.ScanData data : scanData) {
//                String dataString = data.getData();
//                AsyncDataUpdate asyncDataUpdate = new AsyncDataUpdate();
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
            packageSpinner.setOnItemSelectedListener(new PickDetailActivity.SpinnerSelectedListener());
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
        new PickDetailActivity.PickDetailAsync().execute();
    }

    private class PickDetailAsync extends AsyncTask<Long, Integer, Response<PickDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<PickDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    PickDetail pickDetail= result.getResults();
                    skuCodeET.setText(pickDetail.getSkuCode());
                    skuNameET.setText(pickDetail.getSkuName());
                    lotSequenceET.setText(pickDetail.getLotSequence());
                    dispLotET.setText(pickDetail.getDispLot());
                    srcBinET.setText(pickDetail.getSrcBin());
                    destBinET.setText(pickDetail.getDestBin());
                    palletSeqET.setText(pickDetail.getPalletSeq());
                    if (pickDetail.getUnExecutePackQty() != null){
                        unExecutePackQtyET.setText(pickDetail.getUnExecutePackQty().toString());
                    }
                    unExecuteEaQty = pickDetail.getUnExecuteEaQty();
                    planPkg = pickDetail.getPlanPkg();
                    if (pickDetail.getPkgInfos() != null){
                        clientPackageInfos = pickDetail.getPkgInfos();
                    }
                    taskId = pickDetail.getTaskId();
                    if (pickDetail.getTaskId() != null){
                        taskIdET.setText(pickDetail.getTaskId().toString());
                    }
                    bindSpinnerItems();
                } else {
                    if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                        ToastUtil.show(PickDetailActivity.this, "全部执行完成，点确认后返回");
                    }else {
                        ToastUtil.show(PickDetailActivity.this, result.getSeverityMsg());
                    }
                }
            }
        }


        @Override
        protected Response<PickDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(PickDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getPickDetail";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();


            parameters.put("businessType", selectedBusinessType);
            parameters.put("docId", selectedDocId);

            if(taskId != null){
                parameters.put("taskId", taskId.toString());
            }
            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<PickDetail>>() {
            }.getType());
        }
    }

    private class PickConfirmAsync extends AsyncTask<Long, Integer, Response<PickDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<PickDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    if (CustomMsgEnum.COMPLETENORESULT.getName().equals(result.getSeverityMsg())){
                        ToastUtil.show(PickDetailActivity.this, result.getSeverityMsg());
                        finish();
                    }
                    PickDetail pickDetail= result.getResults();
                    skuCodeET.setText(pickDetail.getSkuCode());
                    skuNameET.setText(pickDetail.getSkuName());
                    lotSequenceET.setText(pickDetail.getLotSequence());
                    dispLotET.setText(pickDetail.getDispLot());
                    srcBinET.setText(pickDetail.getSrcBin());
                    destBinET.setText(pickDetail.getDestBin());
                    palletSeqET.setText(pickDetail.getPalletSeq());
                    if (pickDetail.getUnExecutePackQty() != null){
                        unExecutePackQtyET.setText(pickDetail.getUnExecutePackQty().toString());
                    }
                    unExecuteEaQty = pickDetail.getUnExecuteEaQty();
                    planPkg = pickDetail.getPlanPkg();
                    if (pickDetail.getPkgInfos() != null){
                        clientPackageInfos = pickDetail.getPkgInfos();
                    }
                    taskId = pickDetail.getTaskId();
                    if (pickDetail.getTaskId() != null){
                        taskIdET.setText(pickDetail.getTaskId().toString());
                    }
                    bindSpinnerItems();
                } else {
                    if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                        ToastUtil.show(PickDetailActivity.this, "全部执行完成，点确认后返回");
                    }else {
                        ToastUtil.show(PickDetailActivity.this, result.getSeverityMsg());
                    }
                }
            }
        }


        @Override
        protected Response<PickDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(PickDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "pickConfirm";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("taskId", taskId.toString());
            parameters.put("businessType", selectedBusinessType);
            parameters.put("docId", selectedDocId);
            parameters.put("executePackQty", executePackQty.toString());
            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<PickDetail>>() {
            }.getType());
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientPackageInfo currentItem = arr_adapter.getItem(arg2);
            selectedpkgId = (Long) currentItem.getPackageId();
            arg0.setVisibility(View.VISIBLE);
            planPkgAndCoefficientET.setText(currentItem.getPackageName()+"/"+currentItem.getCoefficient().toString());
            unExecutePackQtyAndEAET.setText(unExecutePackQtyET.getText().toString()+"/"+unExecuteEaQty);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
