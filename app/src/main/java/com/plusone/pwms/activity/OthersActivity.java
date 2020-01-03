package com.plusone.pwms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.plusone.pwms.model.ClientPickTicketInWave;
import com.plusone.pwms.model.ClientWarehouse;
import com.plusone.pwms.model.ClientWaveInfo;
import com.plusone.pwms.model.ClientWaveInfos;
import com.plusone.pwms.model.MyApplication;
import com.plusone.pwms.model.Response;
import com.plusone.pwms.model.User;
import com.plusone.pwms.utils.GsonUtil;
import com.plusone.pwms.utils.ToastUtil;
import com.plusone.pwms.utils.WebServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OthersActivity extends Activity {

    /** 开始按钮 */
    private Button invSearchButton;
    /** 返回导航页按钮 */
    private ImageButton homeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        invSearchButton = (Button) findViewById(R.id.invSearch);
        homeButton = findViewById(R.id.home_icon);

        //返回按钮
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OthersActivity.this.finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回导航画面
                Intent backIntent = new Intent(OthersActivity.this, MenuActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        //库存查询按钮单击事件
        invSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到库存查询页面
                Intent newIntent = new Intent(OthersActivity.this, InventorySearchActivity.class);
                startActivity(newIntent);
            }
        });
    }
}
