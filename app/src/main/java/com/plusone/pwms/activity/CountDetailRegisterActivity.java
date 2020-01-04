package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import com.plusone.pwms.enums.EnuInvStatus;
import com.plusone.pwms.model.ClientCountRecord;
import com.plusone.pwms.model.ClientPackageInfo;
import com.plusone.pwms.model.ClientQuantInfo;
import com.plusone.pwms.model.ClientSkuInfo;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.EMDKConfigure;
import com.plusone.pwms.model.InvStatus;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.PickDetail;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.GsonUtil;
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

public class CountDetailRegisterActivity extends Activity {

    /** 包装列表 */
    private Spinner packageSpinner;
    /** 包装列表 */
    private Spinner statusSpinner;
    /** 确认按钮 */
    private Button confirmButton;
    /** 取消按钮 */
    private Button cancelButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 全局变量 */
    private MyApplication application;
//    /** 用户 */
//    private User user;
//    /** 仓库 */
//    private ClientWarehouse clientWarehouse;
    /** 托盘号 */
    private EditText palletSeqET;
    /** 库位 */
    private EditText binCodeET;
    /** 物料编码 */
    private EditText skuCodeET;
    /** 包装折算 */
    private EditText coefficientET;
    /** EA数 */
    private EditText EANumET;
    /** 批次号 */
    private EditText lotSequenceET;
    /** 批次信息 */
    private EditText dispLotET;
    /** 包装数量 */
    private EditText packQtyET;
    /** 入库日期 */
    private EditText inboundDateET;
    /** 入库单号 */
    private EditText trackSeqET;
    /** 前页面库位编码 */
    private String binCode;
    /** 前页面盘点单id */
    private Long countPlanId;
    /** 下拉框选中包装id */
    private Long selectedpkgId;
    /** 物料编码框是否聚焦 */
    private Boolean isSkuCodeET;
    /** 批次号框是否聚焦 */
    private Boolean isLotSequenceET;
    /** 物料编码 */
    private String skuCode;
    private String selectedStatusCode;
    private List<ClientPackageInfo> clientPackageInfos = new ArrayList<>();
    private List<InvStatus> invStatusList = new ArrayList<>();
    private ArrayAdapter<ClientPackageInfo> arr_adapter;
    private ArrayAdapter<InvStatus> status_adapter;


    private ClientSkuInfo currentSkuInfo;
    private ClientQuantInfo currentLotInfo;

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
        setContentView(R.layout.activity_count_detail_register);

//        results = EMDKManager.getEMDKManager(getApplicationContext(), this);
//        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
//            ToastUtil.show(getApplicationContext(), "EMDKManager object request failed!");
//            return;
//        }

        application = (MyApplication) this.getApplication();
//        user = application.getUserAndWarehouse().getUser();
//        clientWarehouse = application.getClientWarehouse();

        //接收传递的数据
        Intent intent = getIntent();
        binCode = (String) intent.getSerializableExtra("binCode");
        countPlanId = (Long) intent.getSerializableExtra("countPlanId");

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        cancelButton = (Button) findViewById(R.id.cancel_btn);
        homeButton = findViewById(R.id.home_icon);
        skuCodeET = findViewById(R.id.sku_code);
        binCodeET = findViewById(R.id.bin_code);
        packageSpinner = (Spinner) findViewById(R.id.packageSpinner);
        coefficientET = findViewById(R.id.coefficient);
        packQtyET = findViewById(R.id.pkg_num);
        EANumET = findViewById(R.id.EANum);
        lotSequenceET = findViewById(R.id.lotSequence);
        dispLotET = findViewById(R.id.dispLot);
        inboundDateET = findViewById(R.id.inbound_date);
        trackSeqET = findViewById(R.id.trackSeq);
        statusSpinner = findViewById(R.id.bin_status);
        palletSeqET = findViewById(R.id.palletSeq);

        binCodeET.setEnabled(false);
        binCodeET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        coefficientET.setEnabled(false);
        coefficientET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        EANumET.setEnabled(false);
        EANumET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        dispLotET.setEnabled(false);
        dispLotET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        palletSeqET.setEnabled(false);
        palletSeqET.setBackgroundColor(getResources().getColor(R.color.colorGrey));

        binCodeET.setText(binCode);

        InvStatus invStatus1 = new InvStatus();
        InvStatus invStatus2 = new InvStatus();
        InvStatus invStatus3 = new InvStatus();
        InvStatus invStatus4 = new InvStatus();
        invStatus1.setCode(EnuInvStatus.AVAILABLE.getCode());
        invStatus1.setName(EnuInvStatus.AVAILABLE.getName());
        invStatusList.add(invStatus1);
        invStatus2.setCode(EnuInvStatus.UNAVAILABLE.getCode());
        invStatus2.setName(EnuInvStatus.UNAVAILABLE.getName());
        invStatusList.add(invStatus2);
        invStatus3.setCode(EnuInvStatus.QC.getCode());
        invStatus3.setName(EnuInvStatus.QC.getName());
        invStatusList.add(invStatus3);
        invStatus4.setCode(EnuInvStatus.FREEZE.getCode());
        invStatus4.setName(EnuInvStatus.FREEZE.getName());
        invStatusList.add(invStatus4);
        bindStatusSpinnerItems();


        
        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountDetailRegisterActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(CountDetailRegisterActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        skuCodeET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 监听到回车键，会执行2次该方法。按下与松开
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        //按下事件
                        Intent newIntent = new Intent(CountDetailRegisterActivity.this, SkuSelectActivity.class);
                        newIntent.putExtra("countPlanId",countPlanId);
                        newIntent.putExtra("skuCode",skuCodeET.getText().toString());
                        startActivityForResult(newIntent,14);
                    }
                }
                return false;
            }
        });

        skuCodeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    isSkuCodeET = true;
                    isLotSequenceET = false;
                } else {
                    // 此处为失去焦点时的处理内容
                    isSkuCodeET = false;
                    if (!skuCodeET.getText().toString().equals(skuCode)){
                        skuCodeET.setText(skuCodeET.getText().toString());
                        clientPackageInfos.clear();
                        coefficientET.setText("");
                        packQtyET.setText("");
                        EANumET.setText("");
                        lotSequenceET.setText("");
                        dispLotET.setText("");
                        inboundDateET.setText("");
                        trackSeqET.setText("");
                        palletSeqET.setText("");
                    }
                }
            }
        });

        lotSequenceET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 监听到回车键，会执行2次该方法。按下与松开
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        //按下事件
                        if (!TextUtils.isEmpty(skuCodeET.getText())){
                            Intent newIntent = new Intent(CountDetailRegisterActivity.this, LotSelectActivity.class);
                            newIntent.putExtra("countPlanId",countPlanId);
                            newIntent.putExtra("skuId",currentSkuInfo.getSkuId());
                            newIntent.putExtra("lotSequence",lotSequenceET.getText().toString());
                            startActivityForResult(newIntent,15);
                        }else {
                            ToastUtil.show(CountDetailRegisterActivity.this, "请先输入物料编码！");
                        }
                    }
                }
                return false;
            }
        });

        lotSequenceET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    isLotSequenceET= true;
                    isSkuCodeET = false;
                } else {
                    // 此处为失去焦点时的处理内容
                    isLotSequenceET = false;
                }
            }
        });

        packQtyET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {
                    // 此处为失去焦点时的处理内容
                    if(TextUtils.isEmpty(packQtyET.getText())){
                        EANumET.setText("0");
                    }else {
                        EANumET.setText(((Double)(Double.parseDouble(coefficientET.getText().toString()) * Double.parseDouble(packQtyET.getText().toString()))).toString());
                    }
                }
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(CountDetailRegisterActivity.this, "asdasd");
                if (getValidate()){
                    ClientCountRecord clientCountRecord = getClientCountRecord();
                    Intent backIntent = new Intent(CountDetailRegisterActivity.this, CountDetailActivity.class);
                    backIntent.putExtra("clientCountRecord", clientCountRecord);
                    setResult(RESULT_OK,backIntent);
                    finish();
                }
            }
        });

        //取消单击事件
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountDetailRegisterActivity.this.finish();
            }
        });
    }

    private Boolean getValidate(){
        Boolean flag = false;
        if (TextUtils.isEmpty(skuCodeET.getText())){
            ToastUtil.show(getApplicationContext(), "物料编码不能为空");
        }else if(selectedpkgId == null){
            ToastUtil.show(getApplicationContext(), "包装不能为空");
        }else if(TextUtils.isEmpty(coefficientET.getText())){
            ToastUtil.show(getApplicationContext(), "包装折算不能为空");
        }else if(TextUtils.isEmpty(packQtyET.getText())){
            ToastUtil.show(getApplicationContext(), "包装数量不能为空");
        }else if(TextUtils.isEmpty(lotSequenceET.getText())){
            ToastUtil.show(getApplicationContext(), "批次号不能为空");
        }
        else if(TextUtils.isEmpty(selectedStatusCode)){
            ToastUtil.show(getApplicationContext(), "库存状态不能为空");
        }
        else {
            flag = true;
        }

        return flag;
    }

    private ClientCountRecord getClientCountRecord(){

        ClientCountRecord clientCountRecord = new ClientCountRecord();
        clientCountRecord.setSkuCode(skuCode);
        if (currentSkuInfo != null){
            clientCountRecord.setSkuId(currentSkuInfo.getSkuId());
            clientCountRecord.setSkuName(currentSkuInfo.getSkuName());
            clientCountRecord.setSkuSpec(currentSkuInfo.getSkuSpec());
            List<ClientPackageInfo> packageInfos =  currentSkuInfo.getPackageInfos();
            String planPackage = "";
            for (int i = 0;i < packageInfos.size();i++){
                if (currentSkuInfo.getPackageInfoId() == packageInfos.get(i).getPackageId()){
                    planPackage = packageInfos.get(i).getPackageName();
                    break;
                }
            }
            clientCountRecord.setPlanPackage(planPackage);
            clientCountRecord.setPlanPackageId(currentSkuInfo.getPackageInfoId());
        }
        if (currentLotInfo != null){
            clientCountRecord.setLotSequence(currentLotInfo.getLotSequence());
            clientCountRecord.setDispLot(currentLotInfo.getDispLot());
            clientCountRecord.setInboundDate(currentLotInfo.getInboundDate());
            clientCountRecord.setTrackSeq(currentLotInfo.getTrackSeq());
            clientCountRecord.setQuantId(currentLotInfo.getQuantId());
        }
        if (!TextUtils.isEmpty(palletSeqET.getText())){
            clientCountRecord.setPalletSeq(palletSeqET.getText().toString());
        }
        if (!TextUtils.isEmpty(packQtyET.getText())){
            clientCountRecord.setInvPackQty(Double.parseDouble(packQtyET.getText().toString()));
            clientCountRecord.setCountNum(Double.parseDouble(packQtyET.getText().toString()));
        }
        if (!TextUtils.isEmpty(EANumET.getText())){
            clientCountRecord.setInvBaseQty(Double.parseDouble(EANumET.getText().toString()));
        }
        clientCountRecord.setInvStatus(selectedStatusCode);

        return clientCountRecord;
    }

//    //获取扫描枪扫描数据
//    @Override
//    public void init(ScanDataCollection scanDataCollection) {
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
//                if(isSkuCodeET){
//                    skuCodeET.setText(result);
//                }else if (isLotSequenceET){
//                    lotSequenceET.setText(result);
//                }
//            }
//        }
//    }

    @Override
    //调用onActivityResult()方法来回调数据
    //onActivityResult()方法有三个参数，分别是requestCode,resultCode,data
    //分别对应的是请求码，处理结果和数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //这里是一个switch()方法，内置的变量是requestCode,即请求码
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //第一个case是1，即对应之前startActivityForResult()方法当中的请求码
                case 14:
                    currentSkuInfo = (ClientSkuInfo) data.getSerializableExtra("currentSkuInfo");
                    skuCode = currentSkuInfo.getSkuCode();
                    refresh();
//                    skuCodeET.setText(skuCode);
                    clientPackageInfos = currentSkuInfo.getPackageInfos();
                    bindSpinnerItems();
                    break;
                case 15:
                    currentLotInfo = (ClientQuantInfo) data.getSerializableExtra("currentLotInfo");
                    lotSequenceET.setText(currentLotInfo.getLotSequence());
                    dispLotET.setText(currentLotInfo.getDispLot());
                    inboundDateET.setText(currentLotInfo.getInboundDate());
                    trackSeqET.setText(currentLotInfo.getTrackSeq());
                    break;
            }
        }
    }

    private void refresh(){
        if (!skuCodeET.getText().toString().equals(skuCode)){
//            currentSkuInfo = null;
//            currentLotInfo = null;
            skuCodeET.setText(skuCode);
            clientPackageInfos.clear();
            coefficientET.setText("");
            packQtyET.setText("");
            EANumET.setText("");
            lotSequenceET.setText("");
            dispLotET.setText("");
            inboundDateET.setText("");
            trackSeqET.setText("");
            palletSeqET.setText("");
        }
    }

    private void bindSpinnerItems(){

        //适配器
        arr_adapter= new ArrayAdapter<ClientPackageInfo>(this, R.layout.spinner_item, clientPackageInfos);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //加载适配器
        packageSpinner.setAdapter(arr_adapter);
        //添加事件Spinner事件监听
        packageSpinner.setOnItemSelectedListener(new CountDetailRegisterActivity.SpinnerSelectedListener());
        //设置默认值
        if (clientPackageInfos != null && clientPackageInfos.size() != 0){
            if(currentSkuInfo.getPackageInfoId() != null){
                for (int i = 0;i < arr_adapter.getCount();i++){
                    if(currentSkuInfo.getPackageInfoId() == arr_adapter.getItem(i).getPackageId()){
                        packageSpinner.setSelection(i,true);

                    }
                }
            }
        }
    }

    private void bindStatusSpinnerItems(){

        //适配器
        status_adapter= new ArrayAdapter<InvStatus>(this, R.layout.spinner_item, invStatusList);
        //设置样式
        status_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //加载适配器
        statusSpinner.setAdapter(status_adapter);
        //添加事件Spinner事件监听
        statusSpinner.setOnItemSelectedListener(new CountDetailRegisterActivity.StatusSpinnerSelectedListener());
    }




    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientPackageInfo currentItem = arr_adapter.getItem(arg2);
            selectedpkgId = (Long) currentItem.getPackageId();
            arg0.setVisibility(View.VISIBLE);
            coefficientET.setText(currentItem.getCoefficient().toString());
            if(TextUtils.isEmpty(packQtyET.getText())){
                EANumET.setText("0");
            }else {
                EANumET.setText(((Double)(currentItem.getCoefficient() * Double.parseDouble(packQtyET.getText().toString()))).toString());
            }

        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    class StatusSpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            InvStatus currentItem = status_adapter.getItem(arg2);
            selectedStatusCode = currentItem.getCode();
            arg0.setVisibility(View.VISIBLE);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
