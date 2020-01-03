package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientInvInfo implements Serializable {

    //唯一识别号
    private Long invId;
    //批次号
    private String lotSequence;
    //库位代码
    private String binCode;
    //批次信息
    private String dispLot;
    //物料信息、编码名称规格
    private String skuCode;
    private String skuName;
    private String skuSpec;
    //托盘号
    private String palletSeq;
    //库存包装
    private String planPackage;
    //入库日期
    private String inboundDate;
    //库存状态
    private String invStatus;
    //入库单号
    private String trackSeq;
    //库存包装数量  --- 盲盘时，显示**
    private Double invPackQty = 0D;
    //库存数量（EA）
    private Double invBaseQty = 0D;


    public Long getInvId() {
        return invId;
    }

    public void setInvId(Long invId) {
        this.invId = invId;
    }

    public String getLotSequence() {
        return lotSequence;
    }

    public void setLotSequence(String lotSequence) {
        this.lotSequence = lotSequence;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public String getDispLot() {
        return dispLot;
    }

    public void setDispLot(String dispLot) {
        this.dispLot = dispLot;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuSpec() {
        return skuSpec;
    }

    public void setSkuSpec(String skuSpec) {
        this.skuSpec = skuSpec;
    }

    public String getPalletSeq() {
        return palletSeq;
    }

    public void setPalletSeq(String palletSeq) {
        this.palletSeq = palletSeq;
    }

    public String getPlanPackage() {
        return planPackage;
    }

    public void setPlanPackage(String planPackage) {
        this.planPackage = planPackage;
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

    public Double getInvBaseQty() {
        return invBaseQty;
    }

    public void setInvBaseQty(Double invBaseQty) {
        this.invBaseQty = invBaseQty;
    }
}
