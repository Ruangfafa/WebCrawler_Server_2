package com.Ruangfafa.model;

public class TaskTag {
    private String pageType, sellerId, cpId;

    public TaskTag(String taskType, String sellerId, String cpId) {
        this.pageType = taskType;
        this.sellerId = sellerId;
        this.cpId = cpId;
    }

    public String[] getTaskTag() {
        return new String[]{pageType, sellerId, cpId};
    }
}