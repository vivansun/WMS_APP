package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientPackInfos implements Serializable {

    List<ClientPackInfo> packInfos;

    public List<ClientPackInfo> getPackInfos() {
        return packInfos;
    }

    public void setPackInfos(List<ClientPackInfo> packInfos) {
        this.packInfos = packInfos;
    }
}
