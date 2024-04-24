package com.kmbl.OrderManagementService.models;

import java.util.HashMap;
import java.util.Map;

public class OrderStatus {

    private static Map<String,Status> map = new HashMap<>();

    static {
        map.put("pending", Status.PENDING);
        map.put("pre_booked", Status.PAYMENT_DUE);
        map.put("order_completed",Status.ORDER_COMPLETED);
        map.put("order_cancelled",Status.ORDER_CANCELLED);
    }

    public static Status getStatus(String status) {
        return map.get(status);
    }

    public enum Status {
        PENDING("pending"),
        PAYMENT_DUE("payment_due"),
        ORDER_COMPLETED("order_completed"),
        ORDER_CANCELLED("order_cancelled");
        private String value;
        Status(String value) {
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
    }
}


