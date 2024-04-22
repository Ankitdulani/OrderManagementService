package com.kmbl.OrderManagementService.models;

public enum UserType {

    CUSTOMER("customer"), ADMIN("admin");
    private String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
