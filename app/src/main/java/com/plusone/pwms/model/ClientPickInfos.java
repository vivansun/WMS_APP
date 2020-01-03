package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientPickInfos implements Serializable {

    List<ClientPickInfo> pickInfos;

    public List<ClientPickInfo> getPickInfos() {
        return pickInfos;
    }

    public void setPickInfos(List<ClientPickInfo> pickInfos) {
        this.pickInfos = pickInfos;
    }
}
