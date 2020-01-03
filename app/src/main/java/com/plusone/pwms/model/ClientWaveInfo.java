package com.plusone.pwms.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

public class ClientWaveInfo implements Serializable {

    //波次代码
    private String code;
    //波次分拣库位
    private String dock;
    //波次明细
    private List<ClientPickTicketInWave> details;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDock() {
        return dock;
    }

    public void setDock(String dock) {
        this.dock = dock;
    }

    public List<ClientPickTicketInWave> getDetails() {
        return details;
    }

    public void setDetails(List<ClientPickTicketInWave> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        String s = "";
        if (!TextUtils.isEmpty(code)){
            s+=code;
            s+="/";
        }
        if (!TextUtils.isEmpty(dock)){
            s+=dock;
        }
        return s;
    }
}
