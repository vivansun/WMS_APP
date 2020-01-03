package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientCountPlanInfos implements Serializable {

    List<ClientCountPlanInfo> countInfos;

    public List<ClientCountPlanInfo> getCountInfos() {
        return countInfos;
    }

    public void setCountInfos(List<ClientCountPlanInfo> countInfos) {
        this.countInfos = countInfos;
    }
}
