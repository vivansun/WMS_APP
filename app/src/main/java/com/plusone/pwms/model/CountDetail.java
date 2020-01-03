package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class CountDetail implements Serializable {

    private List<ClientCountRecord> countDetailInfos;

    private String binCode;

    public List<ClientCountRecord> getCountDetailInfos() {
        return countDetailInfos;
    }

    public void setCountDetailInfos(List<ClientCountRecord> countDetailInfos) {
        this.countDetailInfos = countDetailInfos;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }
}
