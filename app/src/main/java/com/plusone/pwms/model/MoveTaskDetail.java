package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class MoveTaskDetail implements Serializable {

    /** 任务id */
    private Long taskId;
    /** 物料编码 */
    private String skuCode;
    /** 物料名称 */
    private String skuName;
    /** 托盘号 */
    private String palletSeq;
    /** 计划包装 */
    private Long planPkg;
    /** 包装列表 */
    private List<ClientPackageInfo> pkgInfos;
    /** 批次号 */
    private String lotSequence;
    /** 批次信息 */
    private String dispLot;
    /** 原始库位 */
    private String srcBin;
    /** 目标库位 */
    private String destBin;
    /** 未执行包装数量 */
    private Double unExecutePackQty;
    /** 未执行数量 */
    private Double unExecuteEaQty;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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

    public String getPalletSeq() {
        return palletSeq;
    }

    public void setPalletSeq(String palletSeq) {
        this.palletSeq = palletSeq;
    }

    public Long getPlanPkg() {
        return planPkg;
    }

    public void setPlanPkg(Long planPkg) {
        this.planPkg = planPkg;
    }

    public List<ClientPackageInfo> getPkgInfos() {
        return pkgInfos;
    }

    public void setPkgInfos(List<ClientPackageInfo> pkgInfos) {
        this.pkgInfos = pkgInfos;
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

    public Double getUnExecutePackQty() {
        return unExecutePackQty;
    }

    public void setUnExecutePackQty(Double unExecutePackQty) {
        this.unExecutePackQty = unExecutePackQty;
    }

    public Double getUnExecuteEaQty() {
        return unExecuteEaQty;
    }

    public void setUnExecuteEaQty(Double unExecuteEaQty) {
        this.unExecuteEaQty = unExecuteEaQty;
    }
}
