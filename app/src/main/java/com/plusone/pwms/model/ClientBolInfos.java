package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientBolInfos implements Serializable {

    List<ClientBolInfo>  bolInfos;

    public List<ClientBolInfo> getBolInfos() {
        return bolInfos;
    }

    public void setBolInfos(List<ClientBolInfo> bolInfos) {
        this.bolInfos = bolInfos;
    }
}
