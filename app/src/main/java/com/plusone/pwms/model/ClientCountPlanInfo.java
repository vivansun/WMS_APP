package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientCountPlanInfo implements Serializable {

    /**
     * 盘点计划ID
     */
    private Long countPlanId;

    //货主
    private String plant;
    /**
     * 盘点单号
     */
    private String code;
    /**
     * 盘点日期
     */
    private String countDate;
    /**
     * 品项数
     */
    private Long skuCount = 0L;

    /**
     * 批次数
     */
    private Long quantCount= 0L;
    /**
     * 盘点方式
     */
    private String countMethod;
    /**
     * 是否盲盘
     */
    private Boolean blindCount;

    public Long getCountPlanId() {
        return countPlanId;
    }

    public void setCountPlanId(Long countPlanId) {
        this.countPlanId = countPlanId;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountDate() {
        return countDate;
    }

    public void setCountDate(String countDate) {
        this.countDate = countDate;
    }

    public Long getSkuCount() {
        return skuCount;
    }

    public void setSkuCount(Long skuCount) {
        this.skuCount = skuCount;
    }

    public Long getQuantCount() {
        return quantCount;
    }

    public void setQuantCount(Long quantCount) {
        this.quantCount = quantCount;
    }

    public String getCountMethod() {
        return countMethod;
    }

    public void setCountMethod(String countMethod) {
        this.countMethod = countMethod;
    }

    public Boolean getBlindCount() {
        return blindCount;
    }

    public void setBlindCount(Boolean blindCount) {
        this.blindCount = blindCount;
    }

    @Override
    public String toString() {
        return code;
    }
}
