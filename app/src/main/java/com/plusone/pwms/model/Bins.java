package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class Bins implements Serializable {
    List<String> binInfos;

    public List<String> getBinInfos() {
        return binInfos;
    }

    public void setBinInfos(List<String> binInfos) {
        this.binInfos = binInfos;
    }
}
