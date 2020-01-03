package com.plusone.pwms.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.plusone.pwms.R;
import com.plusone.pwms.enums.EnuLotFieldType;
import com.plusone.pwms.enums.EnuLotFormat;
import com.plusone.pwms.model.ClientAsnDetailInfo;
import com.plusone.pwms.model.ClientAsnInfo;
import com.plusone.pwms.model.ClientAsnInfos;
import com.plusone.pwms.model.ClientPackageInfo;
import com.plusone.pwms.model.ClientPutAwayInfoDetail;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.MobileLotInfo;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.ReceivingDetail;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.SharedPreferencesUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiveGoodsConfirmActivity extends Activity {

    /** 包装列表 */
    private Spinner packageSpinner;
    /** 确认按钮 */
    private Button confirmButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;
    /** 下一物料按钮 */
    private Button nextButton;
    /** 获取托盘号按钮 */
    private Button getPalletSeqButton;
    /** 清除按钮 */
    private Button clearButton;
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
    /** 待收数量（EA） */
    private EditText unReceiveQtyET;
    /** 收货暂存 */
    private EditText dockET;
    /** 包装折算 */
    private EditText coefficientET;
    /** 待收包装数量 */
    private EditText unRecievePackQtyET;
    private Double receivePackQty;
    /** EA数量 */
    private EditText EANumET;
    /** 托盘号 */
    private EditText palletSeqET;
    private String palletSeq;
    /** 计划包装 */
    private Long planPkg;
    /** 当前明细列表的id */
    private Long asnDetailId;
    private Integer currentPosition = 0;
    /** 前页面明细列表的id列表 */
    private List<Long> detailIds;
    /** 前页面明细列表的asnId */
    private Long selectedAsnInfoId;

    /** 下拉框选中包装id */
    private Long selectedpkgId;
    private String selectedpkgLevel;

//    private SwipeRecyclerView listView;
//
//    private LinearLayoutManager layoutManager;
//
//    private MyAdapter myAdapter;

//    private List<String> lotList = new ArrayList<>(10);

    private LinearLayout LOTS;
    private LinearLayout LOT1;
    private LinearLayout LOT2;
    private LinearLayout LOT3;
    private LinearLayout LOT4;
    private LinearLayout LOT5;
    private LinearLayout LOT6;
    private LinearLayout LOT7;
    private LinearLayout LOT8;
    private LinearLayout LOT9;
    private LinearLayout LOT10;

    private EditText lot1TitleET;
    private EditText lot2TitleET;
    private EditText lot3TitleET;
    private EditText lot4TitleET;
    private EditText lot5TitleET;
    private EditText lot6TitleET;
    private EditText lot7TitleET;
    private EditText lot8TitleET;
    private EditText lot9TitleET;
    private EditText lot10TitleET;

    private EditText lot1CharET;
    private EditText lot2CharET;
    private EditText lot3CharET;
    private EditText lot4CharET;
    private EditText lot5CharET;
    private EditText lot6CharET;
    private EditText lot7CharET;
    private EditText lot8CharET;
    private EditText lot9CharET;
    private EditText lot10CharET;

    private EditText lot1NumET;
    private EditText lot2NumET;
    private EditText lot3NumET;
    private EditText lot4NumET;
    private EditText lot5NumET;
    private EditText lot6NumET;
    private EditText lot7NumET;
    private EditText lot8NumET;
    private EditText lot9NumET;
    private EditText lot10NumET;

    private EditText lot1DateET;
    private EditText lot2DateET;
    private EditText lot3DateET;
    private EditText lot4DateET;
    private EditText lot5DateET;
    private EditText lot6DateET;
    private EditText lot7DateET;
    private EditText lot8DateET;
    private EditText lot9DateET;
    private EditText lot10DateET;

    private Spinner lot1ListSpinner;
    private Spinner lot2ListSpinner;
    private Spinner lot3ListSpinner;
    private Spinner lot4ListSpinner;
    private Spinner lot5ListSpinner;
    private Spinner lot6ListSpinner;
    private Spinner lot7ListSpinner;
    private Spinner lot8ListSpinner;
    private Spinner lot9ListSpinner;
    private Spinner lot10ListSpinner;

    private Boolean lot1Required = false;
    private Boolean lot2Required = false;
    private Boolean lot3Required = false;
    private Boolean lot4Required = false;
    private Boolean lot5Required = false;
    private Boolean lot6Required = false;
    private Boolean lot7Required = false;
    private Boolean lot8Required = false;
    private Boolean lot9Required = false;
    private Boolean lot10Required = false;

    private String LOT_1;
    private String LOT_2;
    private String LOT_3;
    private String LOT_4;
    private String LOT_5;
    private String LOT_6;
    private String LOT_7;
    private String LOT_8;
    private String LOT_9;
    private String LOT_10;


    /** 批次属性列表 */
    private List<MobileLotInfo> lotInfos;


    private List<ClientPackageInfo> clientPackageInfos;
    private ArrayAdapter<ClientPackageInfo> arr_adapter;

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
        setContentView(R.layout.activity_receiving_detail);


        application = (MyApplication) this.getApplication();
        user = application.getUserAndWarehouse().getUser();
        clientWarehouse = application.getClientWarehouse();

        //接收传递的数据
        Intent intent = getIntent();
        detailIds = (List<Long>) intent.getSerializableExtra("detailIds");
        selectedAsnInfoId = (Long) intent.getSerializableExtra("selectedAsnInfoId");

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        nextButton = (Button) findViewById(R.id.next_sku);
        homeButton = findViewById(R.id.home_icon);
        getPalletSeqButton = findViewById(R.id.getPalletSeq);
        clearButton = findViewById(R.id.clear);
        skuCodeET = findViewById(R.id.sku_code);
        skuCodeET.setEnabled(false);
        skuNameET = findViewById(R.id.sku_name);
        skuNameET.setEnabled(false);
        unReceiveQtyET = findViewById(R.id.unreceive_qty);
        unReceiveQtyET.setEnabled(false);
        dockET = findViewById(R.id.dock);
        dockET.setEnabled(false);
        packageSpinner = (Spinner) findViewById(R.id.packageSpinner);
        coefficientET = findViewById(R.id.coefficient);
        coefficientET.setEnabled(false);
        unRecievePackQtyET = findViewById(R.id.unRecievePackQty);
        EANumET = findViewById(R.id.EANum);
        EANumET.setEnabled(false);
        palletSeqET = findViewById(R.id.palletSeq);
        palletSeqET.setEnabled(false);

        //属性
        LOTS = findViewById(R.id.LOTS);
        LOT1 = findViewById(R.id.LOT_1);
        LOT2 = findViewById(R.id.LOT_2);
        LOT3 = findViewById(R.id.LOT_3);
        LOT4 = findViewById(R.id.LOT_4);
        LOT5 = findViewById(R.id.LOT_5);
        LOT6 = findViewById(R.id.LOT_6);
        LOT7 = findViewById(R.id.LOT_7);
        LOT8 = findViewById(R.id.LOT_8);
        LOT9 = findViewById(R.id.LOT_9);
        LOT10 = findViewById(R.id.LOT_10);

        lot1TitleET = findViewById(R.id.LOT_1_lot_title);
        lot2TitleET = findViewById(R.id.LOT_2_lot_title);
        lot3TitleET = findViewById(R.id.LOT_3_lot_title);
        lot4TitleET = findViewById(R.id.LOT_4_lot_title);
        lot5TitleET = findViewById(R.id.LOT_5_lot_title);
        lot6TitleET = findViewById(R.id.LOT_6_lot_title);
        lot7TitleET = findViewById(R.id.LOT_7_lot_title);
        lot8TitleET = findViewById(R.id.LOT_8_lot_title);
        lot9TitleET = findViewById(R.id.LOT_9_lot_title);
        lot10TitleET = findViewById(R.id.LOT_10_lot_title);

        lot1CharET = findViewById(R.id.LOT_1_lot_char_value);
        lot2CharET = findViewById(R.id.LOT_2_lot_char_value);
        lot3CharET = findViewById(R.id.LOT_3_lot_char_value);
        lot4CharET = findViewById(R.id.LOT_4_lot_char_value);
        lot5CharET = findViewById(R.id.LOT_5_lot_char_value);
        lot6CharET = findViewById(R.id.LOT_6_lot_char_value);
        lot7CharET = findViewById(R.id.LOT_7_lot_char_value);
        lot8CharET = findViewById(R.id.LOT_8_lot_char_value);
        lot9CharET = findViewById(R.id.LOT_9_lot_char_value);
        lot10CharET = findViewById(R.id.LOT_10_lot_char_value);

        lot1NumET = findViewById(R.id.LOT_1_lot_num_value);
        lot2NumET = findViewById(R.id.LOT_2_lot_num_value);
        lot3NumET = findViewById(R.id.LOT_3_lot_num_value);
        lot4NumET = findViewById(R.id.LOT_4_lot_num_value);
        lot5NumET = findViewById(R.id.LOT_5_lot_num_value);
        lot6NumET = findViewById(R.id.LOT_6_lot_num_value);
        lot7NumET = findViewById(R.id.LOT_7_lot_num_value);
        lot8NumET = findViewById(R.id.LOT_8_lot_num_value);
        lot9NumET = findViewById(R.id.LOT_9_lot_num_value);
        lot10NumET = findViewById(R.id.LOT_10_lot_num_value);

        lot1DateET = findViewById(R.id.LOT_1_lot_date_value);
        lot2DateET = findViewById(R.id.LOT_2_lot_date_value);
        lot3DateET = findViewById(R.id.LOT_3_lot_date_value);
        lot4DateET = findViewById(R.id.LOT_4_lot_date_value);
        lot5DateET = findViewById(R.id.LOT_5_lot_date_value);
        lot6DateET = findViewById(R.id.LOT_6_lot_date_value);
        lot7DateET = findViewById(R.id.LOT_7_lot_date_value);
        lot8DateET = findViewById(R.id.LOT_8_lot_date_value);
        lot9DateET = findViewById(R.id.LOT_9_lot_date_value);
        lot10DateET = findViewById(R.id.LOT_10_lot_date_value);

        lot1ListSpinner = findViewById(R.id.LOT_1_lot_list_value);
        lot2ListSpinner = findViewById(R.id.LOT_2_lot_list_value);
        lot3ListSpinner = findViewById(R.id.LOT_3_lot_list_value);
        lot4ListSpinner = findViewById(R.id.LOT_4_lot_list_value);
        lot5ListSpinner = findViewById(R.id.LOT_5_lot_list_value);
        lot6ListSpinner = findViewById(R.id.LOT_6_lot_list_value);
        lot7ListSpinner = findViewById(R.id.LOT_7_lot_list_value);
        lot8ListSpinner = findViewById(R.id.LOT_8_lot_list_value);
        lot9ListSpinner = findViewById(R.id.LOT_9_lot_list_value);
        lot10ListSpinner = findViewById(R.id.LOT_10_lot_list_value);

        //加载数据
        loadData();

//        listView = (SwipeRecyclerView) findViewById(R.id.listView1);
//        //        就是 recyclerview 的显示效果
//        layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(RecyclerView.VERTICAL);
//        listView.setLayoutManager(layoutManager);
//        myAdapter = new MyAdapter(ReceiveGoodsConfirmActivity.this);
//        listView.setAdapter(myAdapter);

        unRecievePackQtyET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    EANumET.setText(((Double)(Double.parseDouble(coefficientET.getText().toString()) * Double.parseDouble(unRecievePackQtyET.getText().toString()))).toString());
                }
            }
        });

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiveGoodsConfirmActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(ReceiveGoodsConfirmActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //获取托盘号按钮单击事件
        getPalletSeqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPalletSeq();
            }
        });

        //清除按钮单击事件
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                palletSeqET.setText("");
            }
        });

        //确认按钮单击事件
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(ReceiveGoodsConfirmActivity.this, "asdasd");
                if (!TextUtils.isEmpty(palletSeqET.getText()) && "PK4000".equals(selectedpkgLevel)){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "托盘号不为空，不能使用托盘包装");
                }else if (lot1Required && TextUtils.isEmpty(lot1CharET.getText()) && TextUtils.isEmpty(lot1NumET.getText()) && TextUtils.isEmpty(lot1DateET.getText()) && TextUtils.isEmpty((String)lot1ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot1TitleET.getText()+"必输");
                }else if (lot2Required && TextUtils.isEmpty(lot2CharET.getText()) && TextUtils.isEmpty(lot2NumET.getText()) && TextUtils.isEmpty(lot2DateET.getText()) && TextUtils.isEmpty((String)lot2ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot2TitleET.getText()+"必输");
                }else if (lot3Required && TextUtils.isEmpty(lot3CharET.getText()) && TextUtils.isEmpty(lot3NumET.getText()) && TextUtils.isEmpty(lot3DateET.getText()) && TextUtils.isEmpty((String)lot3ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot3TitleET.getText()+"必输");
                }else if (lot4Required && TextUtils.isEmpty(lot4CharET.getText()) && TextUtils.isEmpty(lot4NumET.getText()) && TextUtils.isEmpty(lot4DateET.getText()) && TextUtils.isEmpty((String)lot4ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot4TitleET.getText()+"必输");
                }else if (lot5Required && TextUtils.isEmpty(lot5CharET.getText()) && TextUtils.isEmpty(lot5NumET.getText()) && TextUtils.isEmpty(lot5DateET.getText()) && TextUtils.isEmpty((String)lot5ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot5TitleET.getText()+"必输");
                }else if (lot6Required && TextUtils.isEmpty(lot6CharET.getText()) && TextUtils.isEmpty(lot6NumET.getText()) && TextUtils.isEmpty(lot6DateET.getText()) && TextUtils.isEmpty((String)lot6ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot6TitleET.getText()+"必输");
                }else if (lot7Required && TextUtils.isEmpty(lot7CharET.getText()) && TextUtils.isEmpty(lot7NumET.getText()) && TextUtils.isEmpty(lot7DateET.getText()) && TextUtils.isEmpty((String)lot7ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot7TitleET.getText()+"必输");
                }else if (lot8Required && TextUtils.isEmpty(lot8CharET.getText()) && TextUtils.isEmpty(lot8NumET.getText()) && TextUtils.isEmpty(lot8DateET.getText()) && TextUtils.isEmpty((String)lot8ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot8TitleET.getText()+"必输");
                }else if (lot9Required && TextUtils.isEmpty(lot9CharET.getText()) && TextUtils.isEmpty(lot9NumET.getText()) && TextUtils.isEmpty(lot9DateET.getText()) && TextUtils.isEmpty((String)lot9ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot9TitleET.getText()+"必输");
                }else if (lot10Required && TextUtils.isEmpty(lot10CharET.getText()) && TextUtils.isEmpty(lot10NumET.getText()) && TextUtils.isEmpty(lot10DateET.getText()) && TextUtils.isEmpty((String)lot10ListSpinner.getSelectedItem())){
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "批次属性:"+lot10TitleET.getText()+"必输");
                }else{
//                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "asdasd");
                    if (!TextUtils.isEmpty(unRecievePackQtyET.getText())){
                        receivePackQty = Double.parseDouble(unRecievePackQtyET.getText().toString());
                    }
                    if (!TextUtils.isEmpty(palletSeqET.getText())){
                        palletSeq = palletSeqET.getText().toString();
                    }
                    if(LOT1.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot1CharET.getText())){
                            LOT_1 = lot1CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot1NumET.getText())){
                            LOT_1 = lot1NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot1DateET.getText())){
                            LOT_1 = lot1DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot1ListSpinner.getSelectedItem())){
                            LOT_1 = (String)lot1ListSpinner.getSelectedItem();
                        }
                    }
                    if(LOT2.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot2CharET.getText())){
                            LOT_2 = lot2CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot2NumET.getText())){
                            LOT_2 = lot2NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot2DateET.getText())){
                            LOT_2 = lot2DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot2ListSpinner.getSelectedItem())){
                            LOT_2 = (String)lot2ListSpinner.getSelectedItem();
                        }
                    }
                    if(LOT3.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot3CharET.getText())){
                            LOT_3 = lot3CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot3NumET.getText())){
                            LOT_3 = lot3NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot3DateET.getText())){
                            LOT_3 = lot3DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot3ListSpinner.getSelectedItem())){
                            LOT_3 = (String)lot3ListSpinner.getSelectedItem();
                        }
                    }
                    if(LOT4.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot4CharET.getText())){
                            LOT_4 = lot4CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot4NumET.getText())){
                            LOT_4 = lot4NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot4DateET.getText())){
                            LOT_4 = lot4DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot4ListSpinner.getSelectedItem())){
                            LOT_4 = (String)lot4ListSpinner.getSelectedItem();
                        }
                    }
                    if(LOT5.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot5CharET.getText())){
                            LOT_5 = lot5CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot5NumET.getText())){
                            LOT_5 = lot5NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot5DateET.getText())){
                            LOT_5 = lot5DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot5ListSpinner.getSelectedItem())){
                            LOT_5 = (String)lot5ListSpinner.getSelectedItem();
                        }
                    }
                    if(LOT6.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot6CharET.getText())){
                            LOT_6 = lot6CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot6NumET.getText())){
                            LOT_6 = lot6NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot6DateET.getText())){
                            LOT_6 = lot6DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot6ListSpinner.getSelectedItem())){
                            LOT_6 = (String)lot6ListSpinner.getSelectedItem();
                        }
                    }
                    if(LOT7.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot7CharET.getText())){
                            LOT_7 = lot7CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot7NumET.getText())){
                            LOT_7 = lot7NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot7DateET.getText())){
                            LOT_7 = lot7DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot7ListSpinner.getSelectedItem())){
                            LOT_7 = (String)lot7ListSpinner.getSelectedItem();
                        }
                    }
                    if(LOT8.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot8CharET.getText())){
                            LOT_8 = lot8CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot8NumET.getText())){
                            LOT_8 = lot8NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot8DateET.getText())){
                            LOT_8 = lot8DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot8ListSpinner.getSelectedItem())){
                            LOT_8 = (String)lot8ListSpinner.getSelectedItem();
                        }
                    }
                    if(LOT9.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot9CharET.getText())){
                            LOT_9 = lot9CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot9NumET.getText())){
                            LOT_9 = lot9NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot9DateET.getText())){
                            LOT_9 = lot9DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot9ListSpinner.getSelectedItem())){
                            LOT_9 = (String)lot9ListSpinner.getSelectedItem();
                        }
                    }
                    if(LOT10.getVisibility() == View.VISIBLE){
                        if(!TextUtils.isEmpty(lot10CharET.getText())){
                            LOT_10 = lot10CharET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot10NumET.getText())){
                            LOT_10 = lot10NumET.getText().toString();
                        }else if (!TextUtils.isEmpty(lot10DateET.getText())){
                            LOT_10 = lot10DateET.getText().toString();
                        }else if (!TextUtils.isEmpty((String)lot10ListSpinner.getSelectedItem())){
                            LOT_10 = (String)lot10ListSpinner.getSelectedItem();
                        }
                    }
                    new ReceiveGoodsConfirmActivity.ReceivingConfirmAsync().execute();
                }
            }
        });

        //下一物料按钮单击事件
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition += 1;
                if (currentPosition < detailIds.size()){
                    asnDetailId = detailIds.get(currentPosition);
                    loadData();
                }else {
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, "没有下一物料");
                }
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
            packageSpinner.setOnItemSelectedListener(new ReceiveGoodsConfirmActivity.SpinnerSelectedListener());
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

    private void bindLotSpinnerItems(List<String> list,Spinner spinner,String defaultValue){
        if (list != null && list.size() != 0){
            //适配器
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list);
            //设置样式
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            //加载适配器
            spinner.setAdapter(adapter);
            //添加事件Spinner事件监听
//            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                    String currentItem = adapter.getItem(arg2);
//                    arg0.setVisibility(View.VISIBLE);
//                }
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                    // TODO Auto-generated method stub
//                }
//            });
            //设置默认值
            if(defaultValue != null){
                for (int i = 0;i < adapter.getCount();i++){
                    if(defaultValue.equals(adapter.getItem(i))){
                        spinner.setSelection(i,true);
                    }
                }
            }
        }
    }

    private void lotClear(){
        LOTS.setVisibility(View.GONE);
        LOT1.setVisibility(View.GONE);
        LOT2.setVisibility(View.GONE);
        LOT3.setVisibility(View.GONE);
        LOT4.setVisibility(View.GONE);
        LOT5.setVisibility(View.GONE);
        LOT6.setVisibility(View.GONE);
        LOT7.setVisibility(View.GONE);
        LOT8.setVisibility(View.GONE);
        LOT9.setVisibility(View.GONE);
        LOT10.setVisibility(View.GONE);
    }


    private void lotInfosInit(){
        lotClear();
        if (lotInfos != null && lotInfos.size() != 0){
            LOTS.setVisibility(View.VISIBLE);
            for (int i=0;i<lotInfos.size();i++){
                MobileLotInfo mobileLotInfo = lotInfos.get(i);
                if ("LOT_1".equals(mobileLotInfo.getPropertyId())){
                    LOT1.setVisibility(View.VISIBLE);
                    lot1TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot1Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot1CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot1CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot1NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot1NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot1DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot1DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot1ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot1ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }else if ("LOT_2".equals(mobileLotInfo.getPropertyId())){
                    LOT2.setVisibility(View.VISIBLE);
                    lot2TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot2Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot2CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot2CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot2NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot2NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot2DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot2DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot2ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot2ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }else if ("LOT_3".equals(mobileLotInfo.getPropertyId())){
                    LOT3.setVisibility(View.VISIBLE);
                    lot3TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot3Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot3CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot3CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot3NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot3NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot3DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot3DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot3ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot3ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }else if ("LOT_4".equals(mobileLotInfo.getPropertyId())){
                    LOT4.setVisibility(View.VISIBLE);
                    lot4TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot4Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot4CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot4CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot4NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot4NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot4DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot4DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot4ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot4ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }else if ("LOT_5".equals(mobileLotInfo.getPropertyId())){
                    LOT5.setVisibility(View.VISIBLE);
                    lot5TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot5Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot5CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot5CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot5NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot5NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot5DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot5DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot5ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot5ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }else if ("LOT_6".equals(mobileLotInfo.getPropertyId())){
                    LOT6.setVisibility(View.VISIBLE);
                    lot6TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot6Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot6CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot6CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot6NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot6NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot6DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot6DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot6ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot6ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }else if ("LOT_7".equals(mobileLotInfo.getPropertyId())){
                    LOT7.setVisibility(View.VISIBLE);
                    lot7TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot7Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot7CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot7CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot7NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot7NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot7DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot7DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot7ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot7ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }else if ("LOT_8".equals(mobileLotInfo.getPropertyId())){
                    LOT8.setVisibility(View.VISIBLE);
                    lot8TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot8Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot8CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot8CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot8NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot8NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot8DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot8DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot8ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot8ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }else if ("LOT_9".equals(mobileLotInfo.getPropertyId())){
                    LOT9.setVisibility(View.VISIBLE);
                    lot9TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot9Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot9CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot9CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot9NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot9NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot9DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot9DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot9ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot9ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }else if ("LOT_10".equals(mobileLotInfo.getPropertyId())){
                    LOT10.setVisibility(View.VISIBLE);
                    lot10TitleET.setText(mobileLotInfo.getTitle());
                    if (mobileLotInfo.getInputType().equals(EnuLotFieldType.REQUIRED.getCode())){
                        lot10Required = true;
                    }
                    if (mobileLotInfo.getLotType().equals(EnuLotFormat.CHAR.getCode())){
                        lot10CharET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot10CharET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.NUMERIC.getCode())){
                        lot10NumET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot10NumET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.DATE.getCode())){
                        lot10DateET.setVisibility(View.VISIBLE);
                        if (mobileLotInfo.getDefaultValue() != null ){
                            lot10DateET.setText(mobileLotInfo.getDefaultValue());
                        }
                    }else if (mobileLotInfo.getLotType().equals(EnuLotFormat.LIST.getCode())){
                        lot10ListSpinner.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mobileLotInfo.getEnuCode())){
                            bindLotSpinnerItems(mobileLotInfo.getEnuList(),lot10ListSpinner,mobileLotInfo.getEnuCode());
                        }
                    }
                }
            }
        }
    }

    private void loadData(){
        new ReceiveGoodsConfirmActivity.ReceivingDetailAsync().execute();
    }

    private void getPalletSeq(){
        new ReceiveGoodsConfirmActivity.PalletSeqAsync().execute();
    }

    private class ReceivingDetailAsync extends AsyncTask<Long, Integer, Response<ReceivingDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ReceivingDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    if(result.getResults() != null){
                        ReceivingDetail receivingDetail= result.getResults();
                        asnDetailId = receivingDetail.getDetailId();
                        skuCodeET.setText(receivingDetail.getSkuCode());
                        skuNameET.setText(receivingDetail.getSkuName());
                        if (receivingDetail.getUnRecieveQty() != null){
                            unReceiveQtyET.setText(receivingDetail.getUnRecieveQty().toString());
                        }
                        dockET.setText(receivingDetail.getDock());
                        if (receivingDetail.getUnRecievePackQty() != null){
                            unRecievePackQtyET.setText(receivingDetail.getUnRecievePackQty().toString());
                        }
                        planPkg = receivingDetail.getPlanPkg();
                        clientPackageInfos = receivingDetail.getPkgInfos();
                        lotInfos = receivingDetail.getLotInfos();
                        bindSpinnerItems();
                        lotInfosInit();
                    }
                } else {
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ReceivingDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(ReceiveGoodsConfirmActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            //接口名拼写错误 将错就错
            String methodName = "getRecievingDetail";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();


            parameters.put("asnId", selectedAsnInfoId);
            if(asnDetailId != null){
                parameters.put("asnDetailId", asnDetailId);
            }else {
                parameters.put("asnDetailId", null);
            }
            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ReceivingDetail>>() {
            }.getType());
        }
    }

    private class PalletSeqAsync extends AsyncTask<Long, Integer, Response<Map<String,String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<Map<String,String>> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    palletSeqET.setText(result.getResults().get("palletSeq"));
                } else {
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<Map<String,String>> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(ReceiveGoodsConfirmActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            //接口名拼写错误 将错就错
            String methodName = "getPalletSeq";
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<Map<String,String>>>() {
            }.getType());
        }
    }

    private class ReceivingConfirmAsync extends AsyncTask<Long, Integer, Response<ReceivingDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response<ReceivingDetail> result) {
            super.onPostExecute(result);

            if(result != null){
                if ("M".equals(result.getSeverityMsgType())) {
                    if ("作业完成，没待执行信息，请确认。".equals(result.getSeverityMsg())){
                        ToastUtil.show(ReceiveGoodsConfirmActivity.this, result.getSeverityMsg());
                    }else {
                        ReceivingDetail receivingDetail= result.getResults();
                        if (receivingDetail.getUnRecievePackQty() <= 0){
                            ToastUtil.show(ReceiveGoodsConfirmActivity.this, "商品收货完毕，继续下一条或返回前画面");
                        }else {
                            skuCodeET.setText(receivingDetail.getSkuCode());
                            skuNameET.setText(receivingDetail.getSkuName());
                            unReceiveQtyET.setText(receivingDetail.getUnRecieveQty().toString());
                            dockET.setText(receivingDetail.getDock());
                            unRecievePackQtyET.setText(receivingDetail.getUnRecievePackQty().toString());
                            planPkg = receivingDetail.getPlanPkg();
                            clientPackageInfos = receivingDetail.getPkgInfos();
                            lotInfos = receivingDetail.getLotInfos();
                            bindSpinnerItems();
                            lotInfosInit();
                        }
                    }
                } else {
                    ToastUtil.show(ReceiveGoodsConfirmActivity.this, result.getSeverityMsg());
                }
            }
        }


        @Override
        protected Response<ReceivingDetail> doInBackground(Long... params) {
            String httpUrl = SharedPreferencesUtil.getSharePerference(ReceiveGoodsConfirmActivity.this,"systemSetting").getString("prefix","") + "MobileService?wsdl";
            String namespace = "http://impl.mobile.server.pwms.plusone.com";
            //接口名拼写错误 将错就错
            String methodName = "recieveConfirm";
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Map<String, Object> parameters = new HashMap<String, Object>();


            parameters.put("asnId", selectedAsnInfoId);
            parameters.put("receivePackQty", receivePackQty);
            if(asnDetailId != null){
                parameters.put("asnDetailId", asnDetailId);
            }else {
                parameters.put("asnDetailId", null);
            }
            parameters.put("pkgId", selectedpkgId);

            if (!TextUtils.isEmpty(palletSeq)){
                parameters.put("palletSeq", palletSeq);
            }
            if (!TextUtils.isEmpty(LOT_1)){
                parameters.put("LOT_1", LOT_1);
            }
            if (!TextUtils.isEmpty(LOT_2)){
                parameters.put("LOT_2", LOT_2);
            }
            if (!TextUtils.isEmpty(LOT_3)){
                parameters.put("LOT_3", LOT_3);
            }
            if (!TextUtils.isEmpty(LOT_4)){
                parameters.put("LOT_4", LOT_4);
            }
            if (!TextUtils.isEmpty(LOT_5)){
                parameters.put("LOT_5", LOT_5);
            }
            if (!TextUtils.isEmpty(LOT_6)){
                parameters.put("LOT_6", LOT_6);
            }
            if (!TextUtils.isEmpty(LOT_7)){
                parameters.put("LOT_7", LOT_7);
            }
            if (!TextUtils.isEmpty(LOT_8)){
                parameters.put("LOT_8", LOT_8);
            }
            if (!TextUtils.isEmpty(LOT_9)){
                parameters.put("LOT_9", LOT_9);
            }
            if (!TextUtils.isEmpty(LOT_10)){
                parameters.put("LOT_10", LOT_10);
            }

            paramMap.put("userId", user.getLaborId());
            paramMap.put("whId", clientWarehouse.getWhId());
            paramMap.put("parameters", parameters);

            String result = WebServiceUtil.call(httpUrl, namespace, methodName, paramMap);

            return GsonUtil.toBeanByTypeToken(result, new TypeToken<Response<ReceivingDetail>>() {
            }.getType());
        }
    }


    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ClientPackageInfo currentItem = arr_adapter.getItem(arg2);
            selectedpkgId = (Long) currentItem.getPackageId();
            selectedpkgLevel = currentItem.getPackageLevel();
            arg0.setVisibility(View.VISIBLE);
            coefficientET.setText(currentItem.getCoefficient().toString());
            EANumET.setText(((Double)(currentItem.getCoefficient() * Double.parseDouble(unRecievePackQtyET.getText().toString()))).toString());
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
