package com.kmbl.OrderManagementService.models.kafka;

import com.kmbl.OrderManagementService.models.Order;

import java.util.List;

public class PaymentFailureMessage  extends InventoryReleaseMessage{

  public PaymentFailureMessage(Order order){
      super(order);
  }
}
