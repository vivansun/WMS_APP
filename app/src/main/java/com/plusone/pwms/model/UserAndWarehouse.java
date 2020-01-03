package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class UserAndWarehouse implements Serializable {

    /** 工人 */
    private User user;

    private String laborName;

    private Long laborId;


    /** 可用仓库 */
    private List<ClientWarehouse> whInfo;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ClientWarehouse> getWhInfo() {
        return whInfo;
    }

    public void setWhInfo(List<ClientWarehouse> whInfo) {
        this.whInfo = whInfo;
    }

    public String getLaborName() {
        return laborName;
    }

    public void setLaborName(String laborName) {
        this.laborName = laborName;
    }

    public Long getLaborId() {
        return laborId;
    }

    public void setLaborId(Long laborId) {
        this.laborId = laborId;
    }
}
