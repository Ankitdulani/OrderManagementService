package com.kmbl.OrderManagementService.services;

import com.kmbl.OrderManagementService.models.OrderItem;
import com.kmbl.OrderManagementService.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }


    public void createOrder(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }


    public void updateOrderItem(String orderItemID, OrderItem orderItem) {
        OrderItem existingOrderItem = orderItemRepository.findById(orderItemID).orElse(null);
        if (existingOrderItem == null) {
            return;
        }
        existingOrderItem = orderItem;
        orderItemRepository.save(existingOrderItem);
    }


    public void deleteOrderItem(String orderItemID) {
        orderItemRepository.deleteById(orderItemID);
    }


    public OrderItem getOrderItem(String orderItemID) {
        return orderItemRepository.findById(orderItemID).orElse(null);
    }


    public List<OrderItem> getAllOrderItems() {
        return (List<OrderItem>) orderItemRepository.findAll();
    }
}
