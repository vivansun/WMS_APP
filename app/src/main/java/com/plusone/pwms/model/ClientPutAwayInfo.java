package com.plusone.pwms.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientPutAwayInfo implements Serializable {

    //上架追溯号
    private String inboundTrackSeq;
    //收货月台
    private String dock;
    //待上架汇总信息
    List<ClientPutAwayInfoDetail> details = new ArrayList<ClientPutAwayInfoDetail>();
    //收货月台
    private Long binId;


    public String getInboundTrackSeq() {
        return inboundTrackSeq;
    }

    public void setInboundTrackSeq(String inboundTrackSeq) {
        this.inboundTrackSeq = inboundTrackSeq;
    }

    public String getDock() {
        return dock;
    }

    public void setDock(String dock) {
        this.dock = dock;
    }

    public List<ClientPutAwayInfoDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ClientPutAwayInfoDetail> details) {
        this.details = details;
    }

    public Long getBinId() {
        return binId;
    }

    public void setBinId(Long binId) {
        this.binId = binId;
    }

    @Override
    public String toString() {
        String s = "";
        if (!TextUtils.isEmpty(inboundTrackSeq)){
            s+=inboundTrackSeq;
            s+="/";
        }
        if (!TextUtils.isEmpty(dock)){
            s+=dock;
        }
        return s;
    }
}
