package com.plusone.pwms.enums;

/**
 * 盘点方式枚举
 *
 * @author wbx
 */
public enum EnuInvStatus {

    AVAILABLE("AVAILABLE","良品","#000"),
    UNAVAILABLE("UNAVAILABLE","不良品","#000"),
    QC("QC","待检","#000"),
    FREEZE("FREEZE","冻结","#000");

    private String code;

    private String name;

    private String color;

    private EnuInvStatus(String code, String name, String color) {
        this.code = code;
        this.name = name;
        this.color = color;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public static EnuInvStatus getEnum(String code) {
        switch (code) {
            case "AVAILABLE":
                return AVAILABLE;
            case "UNAVAILABLE":
                return UNAVAILABLE;
            case "QC":
                return QC;
            case "FREEZE":
                return FREEZE;
            default:
                return null;
        }
    }

    public static String getCodeByName(String name) {
        switch (name) {
            case "良品":
                return AVAILABLE.getCode();
            case "不良品":
                return UNAVAILABLE.getCode();
            case "待检":
                return QC.getCode();
            case "冻结":
                return FREEZE.getCode();
            default:
                return null;
        }
    }

}
