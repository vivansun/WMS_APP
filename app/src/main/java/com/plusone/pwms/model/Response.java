package com.plusone.pwms.model;

public class Response<T> {

    // 消息类型
    // S:消息Message.
    // E:报错Error.
    // W:警告消息Warning.
    // C:确认信息Confirm.
    private String severityMsgType;

    private String clazz;

    private T results;

    private String severityMsg;

    private Integer targetPageId;

//    private String message;

//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getMessage() {
//        if(message == null || message == ""){
//            message = "代码异常";
//        }
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public T getResult() {
//        return result;
//    }
//
//    public void setResult(T result) {
//        this.result = result;
//    }


    public String getSeverityMsgType() {
        return severityMsgType;
    }

    public void setSeverityMsgType(String severityMsgType) {
        this.severityMsgType = severityMsgType;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    public String getSeverityMsg() {
        return severityMsg;
    }

    public void setSeverityMsg(String severityMsg) {
        this.severityMsg = severityMsg;
    }

    public Integer getTargetPageId() {
        return targetPageId;
    }

    public void setTargetPageId(Integer targetPageId) {
        this.targetPageId = targetPageId;
    }
}
