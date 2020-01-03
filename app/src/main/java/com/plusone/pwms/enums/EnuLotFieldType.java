package com.plusone.pwms.enums;

/**
 * 任务类型枚举
 *
 * @author wbx
 */
public enum EnuLotFieldType {

    REQUIRED("REQUIRED", "必输", "#000"),
    OPTIONAL("OPTIONAL", "可选", "#000");

    private String code;

    private String name;

    private String color;

    private EnuLotFieldType(String code, String name, String color) {
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

    public static EnuLotFieldType getEnum(String code) {
        switch (code) {
            case "REQUIRED":
                return REQUIRED;
            case "OPTIONAL":
                return OPTIONAL;
            default:
                return null;
        }
    }

    public static String getCodeByName(String name) {
        switch (name) {
            case "必输":
                return REQUIRED.getCode();
            case "可选":
                return OPTIONAL.getCode();
            default:
                return null;
        }
    }

}
