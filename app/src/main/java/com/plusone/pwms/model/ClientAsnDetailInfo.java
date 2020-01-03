package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientAsnDetailInfo implements Serializable {

    /** 明细id */
    private Long detailId;
    /** 物料编码 */
    private String skuCode;
    /** 物料名称 */
    private String skuName;
    /** 物料规格 */
    private String skuSpec;
    /** 计划数量（EA） */
    private String planPackage;
    /** 计划包装 */
    private Double planQty=0.0D;
    /** 待收数量（EA） */
    private Double unRecieveQty=0.0D;

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
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

    public Double getPlanQty() {
        return planQty;
    }

    public void setPlanQty(Double planQty) {
        this.planQty = planQty;
    }

    public Double getUnRecieveQty() {
        return unRecieveQty;
    }

    public void setUnRecieveQty(Double unRecieveQty) {
        this.unRecieveQty = unRecieveQty;
    }
}
