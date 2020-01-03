package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientInvInfos implements Serializable {

    List<ClientInvInfo> invInfos;

    public List<ClientInvInfo> getInvInfos() {
        return invInfos;
    }

    public void setInvInfos(List<ClientInvInfo> invInfos) {
        this.invInfos = invInfos;
    }
}
