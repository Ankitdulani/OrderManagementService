package com.kmbl.OrderManagementService.models;

import java.util.HashMap;
import java.util.Map;

public class OrderStatus {

    private static Map<String,Status> map = new HashMap<>();

    static {
        map.put("pending", Status.PENDING);
        map.put("payment_due", Status.PAYMENT_DUE);
        map.put("payment_successful", Status.PAYMENT_SUCCESSFUL);
        map.put("payment_failed", Status.PAYMENT_FAILED);
        map.put("order_completed",Status.ORDER_COMPLETED);
        map.put("order_cancelled",Status.ORDER_CANCELLED);
    }

    public static Status getStatus(String status) {
        return map.get(status);
    }

    public enum Status {
        PENDING("pending"),
        PAYMENT_DUE("payment_due"),
        PAYMENT_SUCCESSFUL("payment_successful"),
        PAYMENT_FAILED("payment_failed"),
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


