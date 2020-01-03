package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientQuantInfos implements Serializable {

    List<ClientQuantInfo> quantInfos;

    public List<ClientQuantInfo> getQuantInfos() {
        return quantInfos;
    }

    public void setQuantInfos(List<ClientQuantInfo> quantInfos) {
        this.quantInfos = quantInfos;
    }
}
