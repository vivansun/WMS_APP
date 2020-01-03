package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientWarehouse implements Serializable {

    private Long whId;
    private String whName;

    public Long getWhId() {
        return whId;
    }

    public void setWhId(Long whId) {
        this.whId = whId;
    }

    public String getWhName() {
        return whName;
    }

    public void setWhName(String whName) {
        this.whName = whName;
    }

    @Override
    public String toString() {
        return whName;
    }
}
