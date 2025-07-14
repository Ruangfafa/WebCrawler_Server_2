package com.Ruangfafa.model;

public class TaskProduct {
    private String pageType, productId;

    public TaskProduct(String pageType, String productId) {
        this.pageType = pageType;
        this.productId = productId;
    }

    public String[] getTaskProduct() {
        return new String[]{pageType, productId};
    }
}
