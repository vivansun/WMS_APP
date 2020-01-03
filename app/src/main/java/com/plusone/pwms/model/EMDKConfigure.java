package com.plusone.pwms.model;

import android.app.ActivityManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.plusone.pwms.utils.ToastUtil;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.BarcodeManager.ScannerConnectionListener;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.StatusData;

import java.util.Iterator;
import java.util.List;

/**
 * EMDK 配置抽象类
 *
 */
public abstract class EMDKConfigure extends AppCompatActivity implements DataListener, EMDKListener, ScannerConnectionListener, StatusListener{
    public static boolean isActive; //全局变量
    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;
    private boolean bContinuousMode = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        onClosed();

        // Remove connection listener
        if (barcodeManager != null) {
            barcodeManager.removeConnectionListener(this);
            barcodeManager = null;
        }

        // Release all the resources
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;

        }

    }

    @Override
    protected void onResume() {
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
            startScanChange();
        }
        super.onResume();
    }



    @Override
    protected void onStop() {
        if (!isAppOnForeground()) {
            //app 进入后台
            isActive = false;//记录当前已经进入后台
            onClosed();
            stopScanChange();
        }
        super.onStop();
    }

    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }


    /**
     * 打开EMDK服务
     */
    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;

        // 获取条形码管理器资源
        barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

        if (barcodeManager != null) {
            barcodeManager.addConnectionListener(this);
            startScan();
        }
    }

    /**
     * 关闭EMDK服务
     */
    @Override
    public void onClosed() {
        if (emdkManager != null) {
            // 移除连接监听器
            if (barcodeManager != null) {
                barcodeManager.removeConnectionListener(this);
                barcodeManager = null;
            }
            // 释放所有资源
            emdkManager.release();
            emdkManager = null;
            deInitScanner();
            stopScan();
        }
    }

    /**
     * 连接状态改变
     */
    @Override
    public void onConnectionChange(ScannerInfo scannerInfo, BarcodeManager.ConnectionState connectionState) {

        switch (connectionState) {
            case CONNECTED:
                deInitScanner();
                initScanner();
                break;
            case DISCONNECTED:
                deInitScanner();
                break;
        }
    }

    /**
     * 扫描状态
     */
    @Override
    public void onStatus(StatusData statusData) {

        StatusData.ScannerStates state = statusData.getState();

        switch (state) {
            case IDLE:
                // 空闲
                if (bContinuousMode) {
                    try {
                        // An attempt to use the scanner continuously and rapidly (with a delay < 100 ms between scans)
                        // may cause the scanner to pause momentarily before resuming the scanning.
                        // Hence add some delay (>= 100ms) before submitting the next read.
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        scanner.read();
                    } catch (ScannerException e) {
                    }
                }
                break;
            case WAITING:
                // 等待
                break;
            case SCANNING:
                // 扫描中
                break;
            case DISABLED:
                // 禁用
                break;
            case ERROR:
                // 错误
                break;
            default:
                break;
        }
    }

    /**
     * 初始化扫描
     */
    private void initScanner() {

        if (scanner == null) {

            List<ScannerInfo> deviceList = barcodeManager.getSupportedDevicesInfo();

            if ((deviceList != null) && (deviceList.size() != 0)) {

                Iterator<ScannerInfo> it = deviceList.iterator();
                while (it.hasNext()) {
                    ScannerInfo scnInfo = it.next();

                    if (scnInfo.isDefaultScanner()) {
                        scanner = barcodeManager.getDevice(scnInfo);
                    }
                }
            }

            if (scanner != null) {
                scanner.addDataListener(this);
                scanner.addStatusListener(this);

                scanner.triggerType = Scanner.TriggerType.HARD;

                try {
                    scanner.enable();
                } catch (ScannerException e) {
                }
            } else {
            }
        }
    }

    /**
     * 注销扫描
     */
    protected void deInitScanner() {

        if (scanner != null) {
            try {
                scanner.cancelRead();
                scanner.disable();
            } catch (ScannerException e) {
            }

            scanner.removeDataListener(this);
            scanner.removeStatusListener(this);

            try {
                scanner.release();
            } catch (ScannerException e) {
            }

            scanner = null;
        }
    }

    /**
     * 启动扫描
     */
    private void startScan() {
        if (scanner == null) {
            initScanner();
        }

        if (scanner != null) {
            if (scanner.isEnabled()) {

                try {
                    scanner.read();
                    bContinuousMode = true;
                } catch (ScannerException e) {
                    ToastUtil.show(getApplicationContext(), "read异常");
                }
            }
        }
    }

    /**
     * 停止扫描
     */
    private void stopScan() {

        if (scanner != null) {

            try {

                // Reset continuous flag
                bContinuousMode = false;
                // Cancel the pending read.
                scanner.cancelRead();

            } catch (ScannerException e) {

                ToastUtil.show(getApplicationContext(), e.getMessage());
            }
        }
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        init(scanDataCollection);
    }

    protected abstract void init(ScanDataCollection scanDataCollection);
    protected abstract void startScanChange();
    protected abstract void stopScanChange();

}
