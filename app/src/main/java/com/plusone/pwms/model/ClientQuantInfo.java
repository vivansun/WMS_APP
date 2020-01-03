package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientQuantInfo implements Serializable {

    private Long quantId;
    /**
     * 批次号信息
     */
    private String lotSequence;

    /**
     /**
     * 收货日期
     */
    private String inboundDate;

    /**
     * 追溯单号
     */
    private String trackSeq;

    /**
     * 批次属性在表格内显示值
     */
    private String dispLot;

    /** 用于记录是否被选中的状态 */
    private Boolean operation = false;


    public Long getQuantId() {
        return quantId;
    }

    public void setQuantId(Long quantId) {
        this.quantId = quantId;
    }

    public String getLotSequence() {
        return lotSequence;
    }

    public void setLotSequence(String lotSequence) {
        this.lotSequence = lotSequence;
    }

    public String getInboundDate() {
        return inboundDate;
    }

    public void setInboundDate(String inboundDate) {
        this.inboundDate = inboundDate;
    }

    public String getTrackSeq() {
        return trackSeq;
    }

    public void setTrackSeq(String trackSeq) {
        this.trackSeq = trackSeq;
    }

    public String getDispLot() {
        return dispLot;
    }

    public void setDispLot(String dispLot) {
        this.dispLot = dispLot;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }
}
