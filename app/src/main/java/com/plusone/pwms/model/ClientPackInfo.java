package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientPackInfo implements Serializable {

    //包装号
    private String packNo;
    //批次号
    private String lotSequence;
    //批次信息
    private String dispLot;
    //物料信息、编码名称规格
    private String skuCode;
    private String skuName;
    private String skuSpec;
    //库存包装(EA)
    private String planPackage;
    //库存数量（EA）
    private Double invBaseQty = 0D;

    public String getPackNo() {
        return packNo;
    }

    public void setPackNo(String packNo) {
        this.packNo = packNo;
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

    public String getPlanPackage() {
        return planPackage;
    }

    public void setPlanPackage(String planPackage) {
        this.planPackage = planPackage;
    }

    public Double getInvBaseQty() {
        return invBaseQty;
    }

    public void setInvBaseQty(Double invBaseQty) {
        this.invBaseQty = invBaseQty;
    }
}
