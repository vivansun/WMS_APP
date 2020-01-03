package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientCountRecord implements Serializable {

    //唯一识别号
    private Long countRecordId;
    //批次号
    private Long quantId;
    private String lotSequence;
    //批次信息
    private String dispLot;
    //物料信息、编码名称规格
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String skuSpec;
    //托盘号
    private String palletSeq;
    //库存包装
    private String planPackage;
    private Long planPackageId;
    //入库日期
    private String inboundDate;
    //库存状态
    private String invStatus;
    //入库单号
    private String trackSeq;
    //库存包装数量  --- 盲盘时，显示**
    private Object invPackQty = 0D;
    //库存数量（EA）
    private Object invBaseQty = 0D;
    /** 用于记录是否被选中的状态 */
    private Boolean operation = false;
    //实盘数量
    private Double countNum;

    public Long getCountRecordId() {
        return countRecordId;
    }

    public void setCountRecordId(Long countRecordId) {
        this.countRecordId = countRecordId;
    }

    public String getLotSequence() {
        return lotSequence;
    }

    public void setLotSequence(String lotSequence) {
        this.lotSequence = lotSequence;
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

    public Object getInvPackQty() {
        return invPackQty;
    }

    public void setInvPackQty(Object invPackQty) {
        this.invPackQty = invPackQty;
    }

    public Object getInvBaseQty() {
        return invBaseQty;
    }

    public void setInvBaseQty(Object invBaseQty) {
        this.invBaseQty = invBaseQty;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }

    public Double getCountNum() {
        return countNum;
    }

    public void setCountNum(Double countNum) {
        this.countNum = countNum;
    }

    public Long getPlanPackageId() {
        return planPackageId;
    }

    public void setPlanPackageId(Long planPackageId) {
        this.planPackageId = planPackageId;
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
}
