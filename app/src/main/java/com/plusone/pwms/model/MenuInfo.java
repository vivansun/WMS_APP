package com.plusone.pwms.model;

import java.io.Serializable;

public class MenuInfo implements Serializable {

    private String menuCode;

    private String menuTip;

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuTip() {
        return menuTip;
    }

    public void setMenuTip(String menuTip) {
        this.menuTip = menuTip;
    }
}
