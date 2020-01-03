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

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.model.ClientInvInfo;
import com.plusone.pwms.model.ClientInvInfos;
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

public class MoveTaskConfirmActivity extends Activity {

    /** 确认按钮 */
    private Button confirmButton;
    /** 全局变量 */
    private MyApplication application;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;
    /** 当前库位 */
    private EditText binCodeET;
    /** 托盘号 */
    private EditText palletSeqET;
    /** 物料编码 */
    private EditText skuCodeET;
    /** 物料名称 */
    private EditText skuNameET;
    /** 批次号 */
    private EditText lotSequenceET;
    /** 批次信息 */
    private EditText dispLotET;
    /** 计划包装 */
    private EditText planPkgET;
    /** 包装数量 */
    private EditText packQtyAndEAET;
    /** 入库日期 */
    private EditText inboundDateET;
    /** 追溯号 */
    private EditText trackSeqET;
    /** 将执行包装数量 */
    private EditText packQtyET;
    private String packQty;
    /** 输入的目标库位 */
    private EditText destBinCodeET;
    private String destBinCode;
    /** 输入的目标托盘号 */
    private EditText destPalletSeqET;
    private String destPalletSeq;
    /** 库存id */
    private Long invId;
    /** 前页面对象 */
    private ClientInvInfo clientInvInfo;
    /** 判断焦点 */
    private Boolean isPackQtyET;
    private Boolean isDestBinCodeET;
    private Boolean isDestPalletSeqET;
    /** 查询条件 */
    private String lastBinCode;
    private String lastLotSequence;
    private String lastSkuCode;

    private LoadingDailog loadingDailog;

    private LoadingDailog.Builder builder = new LoadingDailog.Builder(MoveTaskConfirmActivity.this)
            .setMessage("正在确认中...");

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
        setContentView(R.layout.activity_move_task_confirm);

//        results = EMDKManager.getEMDKManager(getApplicationContext(), this);
//        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
//            ToastUtil.show(getApplicationContext(), "EMDKManager object request failed!");
//            return;
//        }

        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();
        loadingDailog = builder.create();

        //接收传递的数据
        Intent intent = getIntent();
        clientInvInfo = (ClientInvInfo) intent.getSerializableExtra("clientInvInfo");
        lastBinCode = intent.getStringExtra("binCode");
        lastLotSequence = intent.getStringExtra("lotSequence");
        lastSkuCode = intent.getStringExtra("skuCode");
        invId = clientInvInfo.getInvId();

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        homeButton = findViewById(R.id.home_icon);

        binCodeET = findViewById(R.id.binCode);
        palletSeqET = findViewById(R.id.palletSeq);
        skuCodeET = findViewById(R.id.sku_code);
        skuNameET = findViewById(R.id.sku_name);
        lotSequenceET = findViewById(R.id.lotSequence);
        dispLotET = findViewById(R.id.dispLot);
        planPkgET = findViewById(R.id.set_plan_package);
        packQtyAndEAET = findViewById(R.id.packQtyAndEA);
        inboundDateET = findViewById(R.id.inbound_date);
        trackSeqET = findViewById(R.id.trackSeq);
        packQtyET = findViewById(R.id.executePackQty);
        destBinCodeET = findViewById(R.id.bin_scan_confirm);
        destPalletSeqET = findViewById(R.id.palletSeq_scan_confirm);

        binCodeET.setEnabled(false);
        palletSeqET.setEnabled(false);
        skuCodeET.setEnabled(false);
        skuNameET.setEnabled(false);
        lotSequenceET.setEnabled(false);
        dispLotET.setEnabled(false);
        planPkgET.setEnabled(false);
        packQtyAndEAET.setEnabled(false);
        inboundDateET.setEnabled(false);
        trackSeqET.setEnabled(false);

        binCodeET.setText(clientInvInfo.getBinCode());
        palletSeqET.setText(clientInvInfo.getPalletSeq());
        skuCodeET.setText(clientInvInfo.getSkuCode());
        skuNameET.setText(clientInvInfo.getSkuName());
        lotSequenceET.setText(clientInvInfo.getLotSequence());
        dispLotET.setText(clientInvInfo.getDispLot());
        planPkgET.setText(clientInvInfo.getPlanPackage());
        packQtyAndEAET.setText(clientInvInfo.getInvPackQty()+"/"+clientInvInfo.getInvBaseQty());
        inboundDateET.setText(clientInvInfo.getInboundDate());
        trackSeqET.setText(clientInvInfo.getTrackSeq());
        if (!TextUtils.isEmpty(clientInvInfo.getPalletSeq())){
            destPalletSeqET.setText(clientInvInfo.getPalletSeq());
        }

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveTaskConfirmActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(MoveTaskConfirmActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(packQtyET.getText())){
                    ToastUtil.show(MoveTaskConfirmActivity.this, "实际包装数不能为空");
                }else if (TextUtils.isEmpty(destBinCodeET.getText())){
                    ToastUtil.show(MoveTaskConfirmActivity.this, "目标库位不能为空");
                }else if (1 != 1){   //此处为未执行数量（A） 与 executePackQty（B） * 折算（C） 的大小判断   须修改条件   可能要在  A < C*(B+1)时提示  不能超出计划数量来移位
                    //TODO

                }else {
                    packQty = packQtyET.getText().toString();
                    destBinCode = destBinCodeET.getText().toString();
                    destPalletSeq = destPalletSeqET.getText().toString();
                    loadingDailog.show();
                    new MoveConfirmAsync().execute();
                }
            }
        });

        packQtyET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    isPackQtyET = true;
                    isDestBinCodeET = false;
                    isDestPalletSeqET = false;
                } else {
                    // 此处为失去焦点时的处理内容
                    isPackQtyET = false;
                }
            }
        });

        destBinCodeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    isPackQtyET = false;
                    isDestBinCodeET = true;
                    isDestPalletSeqET = false;
                } else {
                    // 此处为失去焦点时的处理内容
                    isDestBinCodeET = false;
                }
            }
        });

        destPalletSeqET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    isPackQtyET = false;
                    isDestBinCodeET = false;
                    isDestPalletSeqET = true;
                } else {
                    // 此处为失去焦点时的处理内容
                    isDestPalletSeqET = false;
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
//                MoveTaskConfirmActivity.AsyncDataUpdate asyncDataUpdate = new MoveTaskConfirmActivity.AsyncDataUpdate();
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
//                if (isPackQtyET){
//                    packQtyET.setText(result);
//                }else if (isDestBinCodeET){
//                    destBinCodeET.setText(result);
//                }else if (isDestPalletSeqET){
//                    destPalletSeqET.setText(result);
//                }
//            }
//        }
//    }

    private class MoveConfirmAsync extends AsyncTask<Long, Integer, Response<ClientInvInfos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ClientInvInfos> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    //关闭扫描
//                    onClosed();
                    loadingDailog.dismiss();
                    //跳回前页面
                    Intent backIntent = new Intent(MoveTaskConfirmActivity.this, InventorySearchActivity.class);
                    backIntent.putExtra("binCode",lastBinCode);
                    backIntent.putExtra("lotSequence",lastLotSequence);
                    backIntent.putExtra("skuCode",lastSkuCode);
                    setResult(RESULT_OK,backIntent);
                    finish();
                } else {
                    loadingDailog.dismiss();
                    ToastUtil.show(MoveTaskConfirmActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ClientInvInfos> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(MoveTaskConfirmActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "confirmInvMove";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("binCode", lastBinCode);
            parameters.put("lotSequence", lastLotSequence);
            parameters.put("skuCode", lastSkuCode);
            parameters.put("invId", invId);
            parameters.put("packQty", packQty);
            parameters.put("destBinCode", destBinCode);
            parameters.put("destPalletSeq", destPalletSeq);

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ClientInvInfos>>() {
            }.getType());
        }
    }
}
