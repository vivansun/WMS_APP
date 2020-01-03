package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class MenuInfos implements Serializable {

    private List<MenuInfo> menuInfos;

    public List<MenuInfo> getMenuInfos() {
        return menuInfos;
    }

    public void setMenuInfos(List<MenuInfo> menuInfos) {
        this.menuInfos = menuInfos;
    }
}
