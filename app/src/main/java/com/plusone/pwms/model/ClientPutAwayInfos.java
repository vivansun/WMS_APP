package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientPutAwayInfos implements Serializable {

    List<ClientPutAwayInfo> putAwayInfos = new ArrayList<ClientPutAwayInfo>();

    public List<ClientPutAwayInfo> getPutAwayInfos() {
        return putAwayInfos;
    }

    public void setPutAwayInfos(List<ClientPutAwayInfo> putAwayInfos) {
        this.putAwayInfos = putAwayInfos;
    }
}
