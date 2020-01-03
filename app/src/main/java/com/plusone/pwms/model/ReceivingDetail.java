package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ReceivingDetail implements Serializable {

    /** 行id，和传入asnDetailId相同 */
    private Long detailId;
    /** 物料编码 */
    private String skuCode;
    /** 物料名称 */
    private String skuName;
    /** 收货库位 */
    private String dock;
    /** 待收数量（EA） */
    private Double unRecieveQty;
    /** 待收包装数量 */
    private Double unRecievePackQty;
    /** 计划包装 */
    private Long planPkg;
    /** 包装列表 */
    private List<ClientPackageInfo> pkgInfos;
    /** 批次属性列表 */
    private List<MobileLotInfo> lotInfos;


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

    public String getDock() {
        return dock;
    }

    public void setDock(String dock) {
        this.dock = dock;
    }

    public Double getUnRecieveQty() {
        return unRecieveQty;
    }

    public void setUnRecieveQty(Double unRecieveQty) {
        this.unRecieveQty = unRecieveQty;
    }

    public Double getUnRecievePackQty() {
        return unRecievePackQty;
    }

    public void setUnRecievePackQty(Double unRecievePackQty) {
        this.unRecievePackQty = unRecievePackQty;
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

    public List<MobileLotInfo> getLotInfos() {
        return lotInfos;
    }

    public void setLotInfos(List<MobileLotInfo> lotInfos) {
        this.lotInfos = lotInfos;
    }
}
