package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientSkuInfo implements Serializable {

    private Long skuId;
    private String skuCode;
    private String skuName;
    private String skuSpec;
    private Long packageInfoId;
    /** 用于记录是否被选中的状态 */
    private Boolean operation = false;

    private List<ClientPackageInfo> packageInfos;


    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public Long getPackageInfoId() {
        return packageInfoId;
    }

    public void setPackageInfoId(Long packageInfoId) {
        this.packageInfoId = packageInfoId;
    }

    public List<ClientPackageInfo> getPackageInfos() {
        return packageInfos;
    }

    public void setPackageInfos(List<ClientPackageInfo> packageInfos) {
        this.packageInfos = packageInfos;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }
}
