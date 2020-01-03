package com.plusone.pwms.enums;

/**
 * 盘点方式枚举
 *
 * @author wbx
 */
public enum EnuCountMethod {

    BIN("BIN","库位","#000"),
    SKU("SKU","货品","#000"),
    MOVED_BIN("MOVED_BIN","库位动碰","#000"),
    MOVED_SKU("MOVED_SKU","货品动碰","#000"),
    ABC("ABC","ABC分类","#000");

    private String code;

    private String name;

    private String color;

    private EnuCountMethod(String code, String name, String color) {
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

    public static EnuCountMethod getEnum(String code) {
        switch (code) {
            case "BIN":
                return BIN;
            case "SKU":
                return SKU;
            case "MOVED_BIN":
                return MOVED_BIN;
            case "MOVED_SKU":
                return MOVED_SKU;
            case "ABC":
                return ABC;
            default:
                return null;
        }
    }

    public static String getCodeByName(String name) {
        switch (name) {
            case "库位":
                return BIN.getCode();
            case "货品":
                return SKU.getCode();
            case "库位动碰":
                return MOVED_BIN.getCode();
            case "货品动碰":
                return MOVED_SKU.getCode();
            case "ABC分类":
                return ABC.getCode();
            default:
                return null;
        }
    }

}
