package com.plusone.pwms.enums;

/**
 * 枚举
 *
 * @author wbx
 */
public enum CustomMsgEnum {

    NORESULT("NORESULT","没有找到待执行的信息，请刷新后再尝试。","#000"),
    COMPLETENORESULT("COMPLETENORESULT","作业完成，没待执行信息，请确认。","#000");


    private String code;

    private String name;

    private String color;

    private CustomMsgEnum(String code, String name, String color) {
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

    public static CustomMsgEnum getEnum(String code) {
        switch (code) {
            case "NORESULT":
                return NORESULT;
            case "COMPLETENORESULT":
                return COMPLETENORESULT;
            default:
                return null;
        }
    }

    public static String getCodeByName(String name) {
        switch (name) {
            case "没有找到待执行的信息，请刷新后再尝试。":
                return NORESULT.getCode();
            case "作业完成，没待执行信息，请确认。":
                return COMPLETENORESULT.getCode();
            default:
                return null;
        }
    }

}
