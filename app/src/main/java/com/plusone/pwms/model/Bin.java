package com.plusone.pwms.model;

import java.io.Serializable;

public class Bin implements Serializable {
    /** 库位代码 */
    private String binCode;
    /** 用于记录是否被选中的状态 */
    private Boolean operation = false;

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }
}
