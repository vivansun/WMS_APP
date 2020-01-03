package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class MobileLotInfo implements Serializable {

    /** 控件Title */
    private String title;
    /** 控件类型 */
    private String lotType;
    /** 控件格式 */
    private String inputType;
    /** 控件编码 */
    private String enuCode;
    /** 控件ID，返回提交用 */
    private String propertyId;
    /** 默认显示Value */
    private String defaultValue;
    /** 下拉列表内容 */
    private List<String> enuList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLotType() {
        return lotType;
    }

    public void setLotType(String lotType) {
        this.lotType = lotType;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getEnuCode() {
        return enuCode;
    }

    public void setEnuCode(String enuCode) {
        this.enuCode = enuCode;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<String> getEnuList() {
        return enuList;
    }

    public void setEnuList(List<String> enuList) {
        this.enuList = enuList;
    }
}
