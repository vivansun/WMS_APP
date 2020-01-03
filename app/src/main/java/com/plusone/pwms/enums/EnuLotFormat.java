package com.plusone.pwms.enums;

/**
 * 控件类型枚举
 *
 * @author lm
 */
public enum EnuLotFormat {


    CHAR("CHAR", "字符", "#000"),
    NUMERIC("NUMERIC", "数字", "#000"),
    DATE("DATE", "日期", "#000"),
    LIST("LIST", "选择框", "#000");

    private String code;

    private String name;

    private String color;

    private EnuLotFormat(String code, String name, String color) {
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

    public static EnuLotFormat getEnum(String code) {
        switch (code) {
            case "CHAR":
                return CHAR;
            case "NUMERIC":
                return NUMERIC;
            case "DATE":
                return DATE;
            case "LIST":
                return LIST;
            default:
                return null;
        }
    }

    public static String getCodeByName(String name) {
        switch (name) {
            case "字符":
                return CHAR.getCode();
            case "数字":
                return NUMERIC.getCode();
            case "日期":
                return DATE.getCode();
            case "选择框":
                return LIST.getCode();
            default:
                return null;
        }
    }

}
