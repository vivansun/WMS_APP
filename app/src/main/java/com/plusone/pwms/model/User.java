package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

    /** 工人id */
    private Long laborId;

    /** 工人名 */
    private String laborName;

    public Long getLaborId() {
        return laborId;
    }

    public void setLaborId(Long laborId) {
        this.laborId = laborId;
    }

    public String getLaborName() {
        return laborName;
    }

    public void setLaborName(String laborName) {
        this.laborName = laborName;
    }
}
