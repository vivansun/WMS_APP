package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientSkuInfos implements Serializable {

    List<ClientSkuInfo> skuInfos;

    public List<ClientSkuInfo> getSkuInfos() {
        return skuInfos;
    }

    public void setSkuInfos(List<ClientSkuInfo> skuInfos) {
        this.skuInfos = skuInfos;
    }
}
