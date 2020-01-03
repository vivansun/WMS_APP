package com.plusone.pwms.model;

import java.io.Serializable;

public class ClientPackageInfo implements Serializable {

    /** 包装ID */
    private Long packageId;
    /** 包装名称 */
    private String packageName;
    /** 包装代码 */
    private String packageCode;
    /** 包装级别 */
    private String packageLevel;
    /** 包装折算 */
    private Double coefficient;

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getPackageLevel() {
        return packageLevel;
    }

    public void setPackageLevel(String packageLevel) {
        this.packageLevel = packageLevel;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public String toString() {
        return packageName;
    }
}
