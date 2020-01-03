package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientAsnInfos implements Serializable {

    private List<ClientAsnInfo> asnInfos;

    public List<ClientAsnInfo> getAsnInfos() {
        return asnInfos;
    }

    public void setAsnInfos(List<ClientAsnInfo> asnInfos) {
        this.asnInfos = asnInfos;
    }
}
