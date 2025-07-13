package com.Ruangfafa.model;

public class TaskTag {
    private String taskType, sellerId, cpId;

    public TaskTag(String taskType, String sellerId, String cpId) {
        this.taskType = taskType;
        this.sellerId = sellerId;
        this.cpId = cpId;
    }

    public String[] getTaskTag() {
        return new String[]{taskType, sellerId, cpId};
    }
}