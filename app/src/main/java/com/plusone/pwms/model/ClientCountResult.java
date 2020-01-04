package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientCountResult implements Serializable {

    //唯一识别号
    private Long countRecordId;
    //批次号
    private Long quantId;
    //物料ID
    private Long skuId;
    //包装id
    private Long packDetailId;
    //托盘号
    private String palletSeq;
    //入库日期  YYYY-MM-DD
    private String inboundDate;
    //库存状态
    private String invStatus;
    //入库单号
    private String trackSeq;
    //库存包装数量
    private Double invPackQty = 0D;

    private String class99;

    public Long getCountRecordId() {
        return countRecordId;
    }

    public void setCountRecordId(Long countRecordId) {
        this.countRecordId = countRecordId;
    }

    public Long getQuantId() {
        return quantId;
    }

    public void setQuantId(Long quantId) {
        this.quantId = quantId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getPackDetailId() {
        return packDetailId;
    }

    public void setPackDetailId(Long packDetailId) {
        this.packDetailId = packDetailId;
    }

    public String getPalletSeq() {
        return palletSeq;
    }

    public void setPalletSeq(String palletSeq) {
        this.palletSeq = palletSeq;
    }

    public String getInboundDate() {
        return inboundDate;
    }

    public void setInboundDate(String inboundDate) {
        this.inboundDate = inboundDate;
    }

    public String getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(String invStatus) {
        this.invStatus = invStatus;
    }

    public String getTrackSeq() {
        return trackSeq;
    }

    public void setTrackSeq(String trackSeq) {
        this.trackSeq = trackSeq;
    }

    public Double getInvPackQty() {
        return invPackQty;
    }

    public void setInvPackQty(Double invPackQty) {
        this.invPackQty = invPackQty;
    }

    public String getClass99() {
        return class99;
    }

    public void setClass99(String class99) {
        this.class99 = class99;
    }
}
