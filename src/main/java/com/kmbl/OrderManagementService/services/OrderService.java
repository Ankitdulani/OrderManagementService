package com.kmbl.OrderManagementService.services;

import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;
import com.kmbl.OrderManagementService.models.Order;
import com.kmbl.OrderManagementService.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order updateOrder(Order order) throws ResourceNotFoundException {
        Order existingOrder = orderRepository.findById(order.getOrderId()).orElse(null);
        if (existingOrder == null) {
            throw new ResourceNotFoundException("Order Id : {} is not present", order.getOrderId());
        }
        return orderRepository.save(order);
    }

    public void deleteOrder(String orderID) {
        orderRepository.deleteById(orderID);
    }

    public Order getOrder(String orderID) throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderID).orElse(null);
        if(order == null) {
            throw new ResourceNotFoundException("Order Id : {} is not present", orderID);
        }
        return order;
    }

    public List<Order> getAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }
}
