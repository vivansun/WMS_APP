package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientPickTicketInWave implements Serializable {

    //发货单ID
    private Long pickTicketId;
    //发货单代码
    private String code;
    //发货单客户单号
    private String relatedBill1;
    //发货月台
    private String dock;
    //分拣库位
    private String stockBin;
    //货主
    private String plant;
    //待分拣数（EA）
    private Double quantity;
    /** 用于记录是否被选中的状态 */
    private Boolean operation = false;

    public Long getPickTicketId() {
        return pickTicketId;
    }

    public void setPickTicketId(Long pickTicketId) {
        this.pickTicketId = pickTicketId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRelatedBill1() {
        return relatedBill1;
    }

    public void setRelatedBill1(String relatedBill1) {
        this.relatedBill1 = relatedBill1;
    }

    public String getDock() {
        return dock;
    }

    public void setDock(String dock) {
        this.dock = dock;
    }

    public String getStockBin() {
        return stockBin;
    }

    public void setStockBin(String stockBin) {
        this.stockBin = stockBin;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }
}
