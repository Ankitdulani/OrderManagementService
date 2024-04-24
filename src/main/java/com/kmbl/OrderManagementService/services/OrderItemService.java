package com.kmbl.OrderManagementService.services;

import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;
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

    public OrderItem createOrder(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public OrderItem updateOrderItem(String orderItemID, OrderItem orderItem) throws ResourceNotFoundException {
        OrderItem existingOrderItem = orderItemRepository.findById(orderItemID).orElse(null);
        if (existingOrderItem == null) {
            throw new ResourceNotFoundException("OrderItemId : {} is not present", orderItemID);
        }
        orderItem.setOrderItemId(orderItemID);
        return orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(String orderItemID) {
        orderItemRepository.deleteById(orderItemID);
    }

    public OrderItem getOrderItem(String orderItemID) throws ResourceNotFoundException {
        OrderItem orderItem = orderItemRepository.findById(orderItemID).orElse(null);
        if(orderItem == null) {
            throw new ResourceNotFoundException("OrderItemId : {} is not present", orderItemID);
        }
        return orderItem;
    }

    public List<OrderItem> getAllOrderItems() {
        return (List<OrderItem>) orderItemRepository.findAll();
    }
}
