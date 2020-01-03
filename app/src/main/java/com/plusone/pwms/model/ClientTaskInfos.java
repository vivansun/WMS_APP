package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.List;

public class ClientTaskInfos implements Serializable {

    List<ClientTaskInfo> taskInfos;

    public List<ClientTaskInfo> getTaskInfos() {
        return taskInfos;
    }

    public void setTaskInfos(List<ClientTaskInfo> taskInfos) {
        this.taskInfos = taskInfos;
    }
}
