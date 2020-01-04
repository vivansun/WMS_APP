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
import com.plusone.pwms.enums.EnuCountMethod;
import com.plusone.pwms.model.Bin;
import com.plusone.pwms.model.ClientCountRecord;
import com.plusone.pwms.model.ClientCountResult;
import com.plusone.pwms.model.ClientPackageInfo;
import com.plusone.pwms.model.ClientQuantInfo;
import com.plusone.pwms.model.ClientSkuInfo;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.CountDetail;
import com.plusone.pwms.model.EMDKConfigure;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.PickDetail;
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

public class CountDetailActivity extends Activity {

    /** 确认按钮 */
    private Button confirmButton;
    /** 下一个库位按钮 */
    private Button nextBinBtn;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 盘点登记按钮 */
    private Button inventoryRegistrationBtn;
    /** 提交库位按钮 */
    private Button submitBinBtn;
    /** 全局变量 */
    private MyApplication application;
    /** 用户 */
    private User user;
    /** 仓库 */
    private ClientWarehouse clientWarehouse;
    /** 库位编码 */
    private EditText binCodeET;
    /** 盘点详情 */
    private EditText countDetailET;
    /** 计划包装单位 */
//    private EditText planPkgET;
    /** 实盘数量 */
    private EditText countNumET;
    /** 前页面当前位置 */
    private String binCode;
    /** 前页面盘点方式 */
    private String countMethod;
    /** 前页面盘点单id */
    private Long countPlanId;

    private Boolean blindCount;

    private List<ClientCountResult> countResults = new ArrayList<>();

    private List<ClientCountRecord> countDetailInfos;
    private SmartTable<ClientCountRecord> countSmartTable;
    private Integer currentPosition = 0;
    private ClientCountRecord currentRecord;
    Column<Boolean> operations;
    Column<String> skuCodes;
    Column<String> skuNames;
    Column<String> skuSpecs;
    Column<String> lotSequences;
    Column<String> palletSeqs;
    Column<String> planPackages;
    Column<Object> invPackQtys;
    Column<Object> invBaseQtys;
    Column<Double> countNums;


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
        setContentView(R.layout.activity_count_detail);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        nextBinBtn = findViewById(R.id.next_bin_btn);
        homeButton = findViewById(R.id.home_icon);
        inventoryRegistrationBtn = findViewById(R.id.inventory_registration);
        submitBinBtn = (Button) findViewById(R.id.submit_bin_btn);
        countSmartTable = findViewById(R.id.table);

        binCodeET = findViewById(R.id.bin_code);
        countDetailET = findViewById(R.id.countDetail);
//        planPkgET = findViewById(R.id.plan_package);
        countNumET = findViewById(R.id.count_num);
        binCodeET.setEnabled(false);
        binCodeET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        countDetailET.setEnabled(false);
        countDetailET.setBackgroundColor(getResources().getColor(R.color.colorGrey));
//        planPkgET.setEnabled(false);

        //接收传递的数据
        Intent intent = getIntent();
        binCode = (String) intent.getSerializableExtra("binCode");
        if (binCode == null){
            binCode = "";
        }
        countMethod = (String) intent.getSerializableExtra("countMethod");
        if (EnuCountMethod.BIN.getName().equals(countMethod) || EnuCountMethod.MOVED_BIN.getName().equals(countMethod)){    //绑定按钮事件时也判断了一次
            inventoryRegistrationBtn.setEnabled(true);
        }else {
            inventoryRegistrationBtn.setEnabled(false);
            inventoryRegistrationBtn.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        }
        countPlanId = (Long) intent.getSerializableExtra("countPlanId");
        blindCount = (Boolean) intent.getSerializableExtra("blindCount");

        //加载数据
        loadData();

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountDetailActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(CountDetailActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(CountDetailActivity.this, "asdasd");
                if (currentPosition != null){
                    if (TextUtils.isEmpty(countNumET.getText())){
                        ToastUtil.show(CountDetailActivity.this, "实盘数量不能为空");
                    }else {
                        countNums.getDatas().set(currentPosition,Double.parseDouble(countNumET.getText().toString()));
                        countDetailInfos.get(currentPosition).setCountNum(Double.parseDouble(countNumET.getText().toString()));
                        countNumET.setText("");
                        currentPosition += 1;
                        if (currentPosition<countDetailInfos.size()){
                            checkInit();
                        }else {
                            currentPosition -= 1;
                            countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                            countSmartTable.invalidate();
                            ToastUtil.show(CountDetailActivity.this, "已经录入完毕，是否提交库位。");
                        }
                    }
                }else {
                    ToastUtil.show(CountDetailActivity.this, "请先选择一条记录");
                }
            }
        });

        //下一库位按钮单击事件
        nextBinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(CountDetailActivity.this, "zxczxc");
                loadData();
            }
        });

        //提交库位按钮单击事件
        submitBinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(CountDetailActivity.this, "zxczxc");
                for(int i=0;i<countDetailInfos.size();i++){
                    ClientCountRecord currentCountRecord = countDetailInfos.get(i);
                    if (currentCountRecord.getCountNum() != null){
                        ClientCountResult clientCountResult = new ClientCountResult();
                        clientCountResult.setClass99("com.plusone.pwms.server.mobile.support.ClientCountResult");
                        if (currentCountRecord.getCountRecordId() != null){
                            clientCountResult.setCountRecordId(currentCountRecord.getCountRecordId());
                            clientCountResult.setInvPackQty(currentCountRecord.getCountNum());//传实盘数量   还是包装数量
                            countResults.add(clientCountResult);
                        }else {
                            clientCountResult.setInvPackQty(currentCountRecord.getCountNum());
                            clientCountResult.setInboundDate(currentCountRecord.getInboundDate());
                            clientCountResult.setInvStatus(currentCountRecord.getInvStatus());
                            clientCountResult.setPackDetailId(currentCountRecord.getPlanPackageId());
                            clientCountResult.setPalletSeq(currentCountRecord.getPalletSeq());
                            clientCountResult.setQuantId(currentCountRecord.getQuantId());
                            clientCountResult.setSkuId(currentCountRecord.getSkuId());
                            clientCountResult.setTrackSeq(currentCountRecord.getTrackSeq());
                            countResults.add(clientCountResult);
                        }
                    }else {
                        ToastUtil.show(CountDetailActivity.this, "有记录还未填写实盘数量。");
                        countResults.clear();
                        break;
                    }
                }
                if (countResults != null && countResults.size() != 0){
                    new CountConfirmAsync().execute();
                }
            }
        });

        //盘点登记按钮单击事件
        inventoryRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EnuCountMethod.BIN.getName().equals(countMethod) || EnuCountMethod.MOVED_BIN.getName().equals(countMethod)){
//                    ToastUtil.show(CountDetailActivity.this, "zxczxc");
                    // 跳转到盘点登记页面
                    Intent newIntent = new Intent(CountDetailActivity.this, CountDetailRegisterActivity.class);
                    newIntent.putExtra("countPlanId",countPlanId);
                    newIntent.putExtra("binCode",binCode);
                    startActivityForResult(newIntent,14);
                }else {
                    ToastUtil.show(CountDetailActivity.this, "盘点方式不是库位或库位动碰，不能登记");
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //第一个case是1，即对应之前startActivityForResult()方法当中的请求码
                case 14:
                    ClientCountRecord clientCountRecord = (ClientCountRecord) data.getSerializableExtra("clientCountRecord");
                    if (blindCount){
                        clientCountRecord.setInvBaseQty("**");
                        clientCountRecord.setInvPackQty("**");
                    }
                    List<ClientCountRecord> ClientCountRecordList = new ArrayList<>();
                    ClientCountRecordList.add(clientCountRecord);
//                    countDetailInfos.add(clientCountRecord);
                    countSmartTable.addData(ClientCountRecordList,true);
                    countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                    countSmartTable.invalidate();
                    break;
            }
        }
    }

    private void InitTable(){
        //表格
        int size = DensityUtils.dp2px(this,16); //指定图标大小
        operations = new Column<>("勾选", "operation",new ImageResDrawFormat<Boolean>(size,size) {
            @Override
            protected Context getContext() {
                return CountDetailActivity.this;
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });

        skuCodes = new Column<>("物料编码", "skuCode");
        skuCodes.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });

        skuNames = new Column<>("物料名称", "skuName");
        skuNames.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });

        skuSpecs = new Column<>("规格", "skuSpec");
        skuSpecs.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });

        lotSequences = new Column<>("批次", "lotSequence");
        lotSequences.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });

        palletSeqs = new Column<>("托盘", "palletSeq");
        palletSeqs.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });

        planPackages = new Column<>("包装", "planPackage");
        planPackages.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });

        invPackQtys = new Column<>("包装数量", "invPackQty");
        invPackQtys.setOnColumnItemClickListener(new OnColumnItemClickListener<Object>() {
            @Override
            public void onClick(Column<Object> column, String value, Object s, int position) {
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });

        invBaseQtys = new Column<>("库存数量", "invBaseQty");
        invBaseQtys.setOnColumnItemClickListener(new OnColumnItemClickListener<Object>() {
            @Override
            public void onClick(Column<Object> column, String value, Object s, int position) {
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });

        countNums = new Column<>("实盘数量", "countNum");
        countNums.setOnColumnItemClickListener(new OnColumnItemClickListener<Double>() {
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
                countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                countSmartTable.invalidate();
            }
        });


        final TableData<ClientCountRecord> tableData = new TableData<>("测试标题",countDetailInfos, operations, skuCodes,skuNames,skuSpecs,lotSequences,palletSeqs,planPackages,invPackQtys,invBaseQtys,countNums);
        countSmartTable.getConfig().setShowTableTitle(false);
        countSmartTable.getConfig().setShowXSequence(false);
        countSmartTable.setTableData(tableData);

        countSmartTable.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(CountDetailActivity.this, R.color.colorGrey);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
//        countSmartTable.getConfig().setMinTableWidth(1024);   //设置最小宽度

        checkInit();
    }

    private void checkInit(){
        new CountDetailActivity.CheckInit().execute();

    }
    private class CheckInit extends AsyncTask<Long, Integer, Response<Void>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<Void> result) {
            radioCheck(currentPosition,true);
            for (int i = 0;i < operations.getDatas().size();i++){
                operations.getDatas().set(i,false);
            }
            operations.getDatas().set(currentPosition,true);
            countSmartTable.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
            countSmartTable.invalidate();
        }

        @Override
        protected Response<Void> doInBackground(Long... longs) {
            return null;
        }
    }

    /**
     * 勾选库位编码，并实时更新
     * @param position  被选择记录的行数，根据行数用来找到其他列中该行记录对应的信息
     * @param selectedState   当前的操作状态：选中 || 取消选中
     */
    private void radioCheck(int position, boolean selectedState){
//        List<String> rotorIdList = binCodes.getDatas();
        if(position >-1) {
            ClientCountRecord rotorTemp = countDetailInfos.get(position);
            if (selectedState && currentRecord != rotorTemp) {
                currentRecord = rotorTemp;
                currentPosition = position;
                String s = "商品编号: ";
                if (!TextUtils.isEmpty(currentRecord.getSkuCode())){
                    s+=currentRecord.getSkuCode();
                }
                s+="/商品名称: ";
                if (!TextUtils.isEmpty(currentRecord.getSkuName())){
                    s+=currentRecord.getSkuName();
                }
                s+="/批次号: ";
                if (!TextUtils.isEmpty(currentRecord.getLotSequence())){
                    s+=currentRecord.getLotSequence();
                }
                s+="/批次信息: ";
                if (!TextUtils.isEmpty(currentRecord.getDispLot())){
                    s+=currentRecord.getDispLot();
                }
                s+="/托盘: ";
                if (!TextUtils.isEmpty(currentRecord.getPalletSeq())){
                    s+=currentRecord.getPalletSeq();
                }
                s+="/包装: ";
                if (!TextUtils.isEmpty(currentRecord.getPlanPackage())){
                    s+=currentRecord.getPlanPackage();
                }
                s+="/包装数量: ";
                if (currentRecord.getInvPackQty() != null){
                    s+=currentRecord.getInvPackQty().toString();
                }
                s+="/EA数量: ";
                if (currentRecord.getInvBaseQty() != null){
                    s+=currentRecord.getInvBaseQty().toString();
                }
                s+="/入库单号: ";
                if (!TextUtils.isEmpty(currentRecord.getTrackSeq())){
                    s+=currentRecord.getTrackSeq();
                }
                s+="/入库日期: ";
                if (!TextUtils.isEmpty(currentRecord.getInboundDate())){
                    s+=currentRecord.getInboundDate();
                }
                countDetailET.setText(s);
                currentRecord = countDetailInfos.get(position);
            } else if (!selectedState && currentRecord == rotorTemp) {
                currentRecord = null;
                currentPosition = null;
                countDetailET.setText("");
            }
        }
    }

    private void loadData(){
        new CountDetailActivity.CountDetailAsync().execute();
    }

    private class CountDetailAsync extends AsyncTask<Long, Integer, Response<CountDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<CountDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    if (CustomMsgEnum.INPUT.getName().equals(result.getSeverityMsg())){
                        DialogUtil.showNormalDialogNoCancle(CountDetailActivity.this, "操作完成", result.getSeverityMsg(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                    }else {
                        countDetailInfos = result.getResults().getCountDetailInfos();
                        if (blindCount){
                            for(ClientCountRecord ccr : countDetailInfos){
                                ccr.setInvBaseQty("**");
                                ccr.setInvPackQty("**");
                            }
                        }
//                    currentRecord = countDetailInfos.get(currentPosition);
                        binCodeET.setText(result.getResults().getBinCode());
                        binCode = binCodeET.getText().toString();
                        countNumET.setText("");
                        InitTable();
                    }
                } else {
                    ToastUtil.show(CountDetailActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<CountDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(CountDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "getCountDetail";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();


            parameters.put("binCode", binCode);
            parameters.put("countPlanId", countPlanId);

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<CountDetail>>() {
            }.getType());
        }
    }


    private class CountConfirmAsync extends AsyncTask<Long, Integer, Response<CountDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<CountDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    currentPosition = 0;
                    countDetailInfos = result.getResults().getCountDetailInfos();
                    if (blindCount){
                        for(ClientCountRecord ccr : countDetailInfos){
                            ccr.setInvBaseQty("**");
                            ccr.setInvPackQty("**");
                        }
                    }
//                    currentRecord = countDetailInfos.get(currentPosition);
                    binCodeET.setText(result.getResults().getBinCode());
                    binCode = binCodeET.getText().toString();
                    countNumET.setText("");
                    InitTable();
                    countResults.clear();
                } else {
                    countResults.clear();
                    ToastUtil.show(CountDetailActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<CountDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(CountDetailActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            String methodName = "countConfirm";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();


            parameters.put("binCode", binCode);
            parameters.put("countPlanId", countPlanId);
            parameters.put("countResults", countResults);


            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<CountDetail>>() {
            }.getType());
        }
    }
}
