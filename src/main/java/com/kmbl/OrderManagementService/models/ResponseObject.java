package com.kmbl.OrderManagementService.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject {
    private String orderItemStatus;
    private OrderItem data;
    private int fullfillCount;
}
