package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientPutAwayInfoDetail implements Serializable {

    //物料编码
    private String skuCode;
    //物料名称
    private String skuName;
    //物料规格
    private String skuSpec;
    //待上架计划数量（EA）
    private Double planQty=0.0D;


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

    public Double getPlanQty() {
        return planQty;
    }

    public void setPlanQty(Double planQty) {
        this.planQty = planQty;
    }
}
