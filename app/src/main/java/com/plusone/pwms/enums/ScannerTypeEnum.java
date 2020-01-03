package com.plusone.pwms.enums;

/**
 * 任务类型枚举
 *
 * @author wbx
 */
public enum ScannerTypeEnum {

    AlreadyOperated("20", "已操作", "#0f8fea"),
    NoOperation("10", "未操作", "#f80000");

    private String code;

    private String name;

    private String color;

    private ScannerTypeEnum(String code, String name, String color) {
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

    public static ScannerTypeEnum getEnum(String code) {
        switch (code) {
            case "10":
                return NoOperation;
            case "20":
                return AlreadyOperated;
            default:
                return null;
        }
    }

    public static String getCodeByName(String name) {
        switch (name) {
            case "未操作":
                return NoOperation.getCode();
            case "已操作":
                return AlreadyOperated.getCode();
            default:
                return null;
        }
    }

}
