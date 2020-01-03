package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientTaskInfo implements Serializable {
    //任务ID
    private Long taskId;
    //原始库位
    private String srcBin;
    //目标库位
    private String destBin;
    //物料编码
    private String skuCode;
    //物料名称
    private String skuName;
    //物料规格
    private String skuSpec;
    //托盘号
    private String palletSeq;
    //包装名称
    private String packageName;
    //批次号
    private String lotSequence;
    //批次信息
    private String dispLot;
    //包装折算
    private Double coefficient;
    //计划执行包装数量
    private Double planPackQty= 0.0D;
    //执行包装数量
    private Double executePackQty= 0.0D;
    //入库日期
    private String inboundDate;
    //库存状态
    private String invStatus;
    //追溯单号
    private String trackSeq;


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getSrcBin() {
        return srcBin;
    }

    public void setSrcBin(String srcBin) {
        this.srcBin = srcBin;
    }

    public String getDestBin() {
        return destBin;
    }

    public void setDestBin(String destBin) {
        this.destBin = destBin;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Double getPlanPackQty() {
        return planPackQty;
    }

    public void setPlanPackQty(Double planPackQty) {
        this.planPackQty = planPackQty;
    }

    public Double getExecutePackQty() {
        return executePackQty;
    }

    public void setExecutePackQty(Double executePackQty) {
        this.executePackQty = executePackQty;
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
}
