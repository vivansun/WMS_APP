package com.plusone.pwms.model;

import android.text.TextUtils;

import java.io.Serializable;

public class ClientPickInfo implements Serializable {

    //作业方式：1：按订单拣选 2：波次拣选
    private String businessType ;
    //订单号
    private String orderSeq;
    //客户单号
    private String refBill1;
    //ID
    private Long docId;

    //以下为明细部分显示信息
    //备货日期
    private String stockDate;
    //计划数量
    private Double planQty;
    //已拣选数量
    private String executedQty;
    //集货库位
    private String binCode;
    /** 包含订单数量 */
    private Double orderQty = 0D;
    /** 包含品项数 */
    private Double skuCount = 0D;
    //波次执行方式
    private String workMode;

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(String orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getRefBill1() {
        return refBill1;
    }

    public void setRefBill1(String refBill1) {
        this.refBill1 = refBill1;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public String getStockDate() {
        return stockDate;
    }

    public void setStockDate(String stockDate) {
        this.stockDate = stockDate;
    }

    public Double getPlanQty() {
        return planQty;
    }

    public void setPlanQty(Double planQty) {
        this.planQty = planQty;
    }

    public String getExecutedQty() {
        return executedQty;
    }

    public void setExecutedQty(String executedQty) {
        this.executedQty = executedQty;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public Double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Double orderQty) {
        this.orderQty = orderQty;
    }

    public Double getSkuCount() {
        return skuCount;
    }

    public void setSkuCount(Double skuCount) {
        this.skuCount = skuCount;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    @Override
    public String toString() {
        String s = "";
        if (!TextUtils.isEmpty(orderSeq)){
            s+=orderSeq;
            s+="/";
        }
        if (!TextUtils.isEmpty(refBill1)){
            s+=refBill1;
            s+="/";
        }
        if (docId != null){
            s+=docId.toString();
        }
        return s;
    }
}
