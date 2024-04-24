package com.kmbl.OrderManagementService.models;

public class ResponseObject {
    private String status;
    private OrderItem data;
    private int count;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public OrderItem getData() {
        return data;
    }

    public void setData(OrderItem data) {
        this.data = data;
    }

}
