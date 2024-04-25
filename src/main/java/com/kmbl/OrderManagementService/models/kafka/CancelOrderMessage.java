package com.kmbl.OrderManagementService.models.kafka;

import com.kmbl.OrderManagementService.models.Order;

public class CancelOrderMessage extends InventoryReleaseMessage{

    public CancelOrderMessage(Order order){
        super(order);
    }
}
