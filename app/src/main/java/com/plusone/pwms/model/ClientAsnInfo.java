package com.plusone.pwms.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

public class ClientAsnInfo implements Serializable {

    /** ASN单的ID */
    private Long asnId;
    /** ASN单号 */
    private String code;
    /** 客户单号*/
    private String relatedBill;
    /** 货主 */
    private String plant;
    /** 收货月台 */
    private String dock;
    /** ASN单详情 */
    private List<ClientAsnDetailInfo> details;

    public Long getAsnId() {
        return asnId;
    }

    public void setAsnId(Long asnId) {
        this.asnId = asnId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRelatedBill() {
        return relatedBill;
    }

    public void setRelatedBill(String relatedBill) {
        this.relatedBill = relatedBill;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getDock() {
        return dock;
    }

    public void setDock(String dock) {
        this.dock = dock;
    }

    public List<ClientAsnDetailInfo> getDetails() {
        return details;
    }

    public void setDetails(List<ClientAsnDetailInfo> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        String s = "";
        if (asnId != null){
            s+=asnId.toString();
            s+="/";
        }
        if (!TextUtils.isEmpty(code)){
            s+=code;
            s+="/";
        }
        if (!TextUtils.isEmpty(relatedBill)){
            s+=relatedBill;
            s+="/";
        }
        if (!TextUtils.isEmpty(plant)){
            s+=plant;
            s+="/";
        }
        if (!TextUtils.isEmpty(dock)){
            s+=dock;
        }
        return s;
    }
}
