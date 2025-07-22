package com.Ruangfafa.model;

public class TaskTag {
    private String pageType, sellerId, cId, pId;

    public TaskTag(String taskType, String sellerId, String cId, String pId) {
        this.pageType = taskType;
        this.sellerId = sellerId;
        this.cId = cId;
        this.pId = pId;
    }

    public String[] getTaskTag() {
        return new String[]{pageType, sellerId, cId, pId};
    }
}