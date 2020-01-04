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
import com.plusone.pwms.enums.CustomMsgEnum;
import com.plusone.pwms.model.ClientPackageInfo;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.PutAwayDetail;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.DialogUtil;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpperShelfDetailActivity extends Activity {

    /** 包装列表 */
    private Spinner packageSpinner;
    /** 确认按钮 */
    private Button confirmButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 下一条按钮 */
    private Button nextButton;
    /** 新库位按钮 */
    private Button newBinButton;
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
    /** 计划包装 */
    private EditText planPkgET;
    /** 待上架 */
    private EditText unUpperET;
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
    /** 包装折算 */
    private EditText coefficientET;
    /** 将执行包装数量 */
    private EditText unExecutePackQtyET;
    private Double unExecutePackQty;
    /** 新库位 */
    private EditText newBinET;
    /** 未执行数量 */
    private Double unExecuteEaQty;
    /** EA数量 */
    private Double EANum;
    /** 计划包装 */
    private Long planPkg;
    /** 前页面收货追溯号 */
    private String selectedInboundTrackSeq;
    /** 前页面收货月台Id */
    private Long selectedBinId;
    /** 任务id */
    private Long taskId;
    /** 下拉框选中包装id */
    private Long selectedpkgId;
    /** 库位选择画面返回的库位编码 */
    private String binCode;

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
        setContentView(R.layout.activity_upper_shelf_detail);

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
        selectedInboundTrackSeq = (String) intent.getSerializableExtra("selectedInboundTrackSeq");
        selectedBinId = (Long) intent.getSerializableExtra("selectedBinId");

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        nextButton = (Button) findViewById(R.id.next);
        homeButton = findViewById(R.id.home_icon);
        newBinButton = (Button) findViewById(R.id.newBinBtn);
        skuCodeET = findViewById(R.id.sku_code);
        skuNameET = findViewById(R.id.sku_name);
        planPkgET = findViewById(R.id.set_plan_package);
        unUpperET = findViewById(R.id.unUpper);
        lotSequenceET = findViewById(R.id.lotSequence);
        dispLotET = findViewById(R.id.dispLot);
        srcBinET = findViewById(R.id.srcBin);
        destBinET = findViewById(R.id.destBin);
        palletSeqET = findViewById(R.id.palletSeq);
        coefficientET = findViewById(R.id.coefficient);
        packageSpinner = (Spinner) findViewById(R.id.packageSpinner);
        unExecutePackQtyET = findViewById(R.id.unExecutePackQty);
        newBinET = findViewById(R.id.newBin);
        skuCodeET.setEnabled(false);
        skuCodeET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        skuNameET.setEnabled(false);
        skuNameET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        planPkgET.setEnabled(false);
        planPkgET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        unUpperET.setEnabled(false);
        unUpperET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        lotSequenceET.setEnabled(false);
        lotSequenceET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        dispLotET.setEnabled(false);
        dispLotET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        srcBinET.setEnabled(false);
        srcBinET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        destBinET.setEnabled(false);
        destBinET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        palletSeqET.setEnabled(false);
        palletSeqET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        coefficientET.setEnabled(false);
        coefficientET.setBackgroundColor(getResources().getColor(R.color.colorGrey));


        //加载数据
        loadData();

        unExecutePackQtyET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    unUpperET.setText(((Double)(Double.parseDouble(coefficientET.getText().toString()) * Double.parseDouble(unExecutePackQtyET.getText().toString()))).toString()+"/"+unExecuteEaQty);
                }
            }
        });

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpperShelfDetailActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(UpperShelfDetailActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //新库位按钮单击事件
        newBinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBin();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(((ClientPackageInfo)packageSpinner.getSelectedItem()).getPackageName()) || TextUtils.isEmpty(unExecutePackQtyET.getText()) || TextUtils.isEmpty(newBinET.getText())){
                    ToastUtil.show(UpperShelfDetailActivity.this, "包装/数量/库位 必输");
                }else {
                    binCode = newBinET.getText().toString();
                    unExecutePackQty = Double.parseDouble(unExecutePackQtyET.getText().toString());
                    if (!newBinET.getText().toString().equals(destBinET.getText().toString())){
                        DialogUtil.showNormalDialog(UpperShelfDetailActivity.this, "库位改变", "新库位与原目标库位不一致，是否确认",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new UpperShelfDetailActivity.PutAwayConfirmAsync().execute();
                                    }
                                });
                    }else {
                        new UpperShelfDetailActivity.PutAwayConfirmAsync().execute();
                    }
                }
            }
        });

        //下一条按钮单击事件
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    private void bindSpinnerItems(){
        if (clientPackageInfos != null && clientPackageInfos.size() != 0){
            //适配器
            arr_adapter= new ArrayAdapter<ClientPackageInfo>(this, R.layout.spinner_item, clientPackageInfos);
            //设置样式
            arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            //加载适配器
            packageSpinner.setAdapter(arr_adapter);
            //添加事件Spinner事件监听
            packageSpinner.setOnItemSelectedListener(new UpperShelfDetailActivity.SpinnerSelectedListener());
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

//    //获取扫描枪扫描数据
//    @Override
//    public void init(ScanDataCollection scanDataCollection) {
//        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
//            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
//            for (ScanDataCollection.ScanData data : scanData) {
//                String dataString = data.getData();
//                UpperShelfDetailActivity.AsyncDataUpdate asyncDataUpdate = new UpperShelfDetailActivity.AsyncDataUpdate();
//                asyncDataUpdate.execute(dataString);
//            }
//        }
//    }
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
//                newBinET.setText(result);
//            }
//        }
//    }

    private void loadData(){
        new UpperShelfDetailActivity.PutAwayDetailAsync().execute();
    }

    private void newBin(){
        // 跳转到新库位页面
        Intent confirmIntent = new Intent(UpperShelfDetailActivity.this, BlankBinsActivity.class);
        confirmIntent.putExtra("taskId", taskId);
        startActivityForResult(confirmIntent,14);
    }

    @Override
    //调用onActivityResult()方法来回调数据
    //onActivityResult()方法有三个参数，分别是requestCode,resultCode,data
    //分别对应的是请求码，处理结果和数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //这里是一个switch()方法，内置的变量是requestCode,即请求码
        switch (requestCode) {
            //第一个case是1，即对应之前startActivityForResult()方法当中的请求码
            case 14:
                if (resultCode == RESULT_OK){
                    newBinET.setText(data.getStringExtra("binCode"));
                }
                break;
        }
    }

    private class PutAwayDetailAsync extends AsyncTask<Long, Integer, Response<PutAwayDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<PutAwayDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    PutAwayDetail putAwayDetail= result.getResults();
                    if (!TextUtils.isEmpty(putAwayDetail.getSkuCode())){
                        skuCodeET.setText(putAwayDetail.getSkuCode());
                    }
                    if (!TextUtils.isEmpty(putAwayDetail.getSkuName())){
                        skuNameET.setText(putAwayDetail.getSkuName());
                    }
                    if (!TextUtils.isEmpty(putAwayDetail.getLotSequence())){
                        lotSequenceET.setText(putAwayDetail.getLotSequence());
                    }
                    if (!TextUtils.isEmpty(putAwayDetail.getDispLot())){
                        dispLotET.setText(putAwayDetail.getDispLot());
                    }
                    if (!TextUtils.isEmpty(putAwayDetail.getSrcBin())){
                        srcBinET.setText(putAwayDetail.getSrcBin());
                    }
                    if (!TextUtils.isEmpty(putAwayDetail.getDestBin())){
                        destBinET.setText(putAwayDetail.getDestBin());
                    }
                    if (!TextUtils.isEmpty(putAwayDetail.getPalletSeq())){
                        palletSeqET.setText(putAwayDetail.getPalletSeq());
                        packageSpinner.setEnabled(false);
                        unExecutePackQtyET.setEnabled(false);
                    }
                    if (putAwayDetail.getUnExecutePackQty() != null ){
                        unExecutePackQtyET.setText(putAwayDetail.getUnExecutePackQty().toString());
                    }
                    if (putAwayDetail.getUnExecuteEaQty() != null){
                        unExecuteEaQty = putAwayDetail.getUnExecuteEaQty();
                    }
                    if (putAwayDetail.getPlanPkg() != null){
                        planPkg = putAwayDetail.getPlanPkg();
                    }
                    if (putAwayDetail.getPkgInfos() != null ){
                        clientPackageInfos = putAwayDetail.getPkgInfos();
                    }
                    if (putAwayDetail.getTaskId() != null ){
                        taskId = putAwayDetail.getTaskId();
                    }
                    bindSpinnerItems();
                } else {
                    if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                        DialogUtil.showNormalDialogNoCancle(UpperShelfDetailActivity.this, "操作完成", "全部执行完成，点确认后返回",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                    }else {
                        ToastUtil.show(UpperShelfDetailActivity.this, result.getSeverityMsg());
                    }
                }
            }
        }


        @Override
        protected Response<PutAwayDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(UpperShelfDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getPutAwayDetail";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();


            parameters.put("inboundTrackSeq", selectedInboundTrackSeq);
            parameters.put("binId", selectedBinId);

            if(taskId != null){
                parameters.put("taskId", taskId);
            }
            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<PutAwayDetail>>() {
            }.getType());
        }
    }

    private class PutAwayConfirmAsync extends AsyncTask<Long, Integer, Response<PutAwayDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<PutAwayDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                        DialogUtil.showNormalDialogNoCancle(UpperShelfDetailActivity.this, "操作完成", result.getSeverityMsg(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                    }else {
                        PutAwayDetail putAwayDetail= result.getResults();
                        if (!TextUtils.isEmpty(putAwayDetail.getSkuCode())){
                            skuCodeET.setText(putAwayDetail.getSkuCode());
                        }
                        if (!TextUtils.isEmpty(putAwayDetail.getSkuName())){
                            skuNameET.setText(putAwayDetail.getSkuName());
                        }
                        if (!TextUtils.isEmpty(putAwayDetail.getLotSequence())){
                            lotSequenceET.setText(putAwayDetail.getLotSequence());
                        }
                        if (!TextUtils.isEmpty(putAwayDetail.getDispLot())){
                            dispLotET.setText(putAwayDetail.getDispLot());
                        }
                        if (!TextUtils.isEmpty(putAwayDetail.getSrcBin())){
                            srcBinET.setText(putAwayDetail.getSrcBin());
                        }
                        if (!TextUtils.isEmpty(putAwayDetail.getDestBin())){
                            destBinET.setText(putAwayDetail.getDestBin());
                        }
                        if (!TextUtils.isEmpty(putAwayDetail.getPalletSeq())){
                            palletSeqET.setText(putAwayDetail.getPalletSeq());
                            packageSpinner.setEnabled(false);
                            unExecutePackQtyET.setEnabled(false);
                        }
                        if (putAwayDetail.getUnExecutePackQty() != null ){
                            unExecutePackQtyET.setText(putAwayDetail.getUnExecutePackQty().toString());
                        }
                        if (putAwayDetail.getUnExecuteEaQty() != null){
                            unExecuteEaQty = putAwayDetail.getUnExecuteEaQty();
                        }
                        if (putAwayDetail.getPlanPkg() != null){
                            planPkg = putAwayDetail.getPlanPkg();
                        }
                        if (putAwayDetail.getPkgInfos() != null ){
                            clientPackageInfos = putAwayDetail.getPkgInfos();
                        }
                        if (putAwayDetail.getTaskId() != null ){
                            taskId = putAwayDetail.getTaskId();
                        }
                        bindSpinnerItems();
                    }
                } else {
                    if (CustomMsgEnum.NORESULT.getName().equals(result.getSeverityMsg())){
                        DialogUtil.showNormalDialogNoCancle(UpperShelfDetailActivity.this, "操作完成", "全部执行完成，点确认后返回",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                    }else {
                        ToastUtil.show(UpperShelfDetailActivity.this, result.getSeverityMsg());
                    }
                }
            }
        }


        @Override
        protected Response<PutAwayDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(UpperShelfDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "putAwayConfirm";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();


            parameters.put("taskId", taskId);
            parameters.put("pkgId", selectedpkgId);
            parameters.put("executePackQty", unExecutePackQty);
            parameters.put("destBinCode", binCode);


            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<PutAwayDetail>>() {
            }.getType());
        }
    }


    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientPackageInfo currentItem = arr_adapter.getItem(arg2);
            selectedpkgId = (Long) currentItem.getPackageId();
            arg0.setVisibility(View.VISIBLE);
            planPkgET.setText(currentItem.getPackageName());
            coefficientET.setText(currentItem.getCoefficient().toString());
            unUpperET.setText(((Double)(currentItem.getCoefficient() * Double.parseDouble(unExecutePackQtyET.getText().toString()))).toString()+"/"+unExecuteEaQty);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
