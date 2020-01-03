package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientWaveInfos implements Serializable {

    List<ClientWaveInfo> waveInfos;

    public List<ClientWaveInfo> getWaveInfos() {
        return waveInfos;
    }

    public void setWaveInfos(List<ClientWaveInfo> waveInfos) {
        this.waveInfos = waveInfos;
    }
}
