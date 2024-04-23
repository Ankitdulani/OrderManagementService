package com.kmbl.OrderManagementService.services;

import com.kmbl.OrderManagementService.models.Order;
import com.kmbl.OrderManagementService.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private  OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }



    public void createOrder(Order order) {
       orderRepository.save(order);

    }


    public void updateOrder(String orderID,Order order) {
        Order existingOrder = orderRepository.findById(orderID).orElse(null);
        if(existingOrder == null){

            return ;
        }

        existingOrder=order;
        orderRepository.save(existingOrder);

    }


    public void deleteOrder(String orderID) {
        orderRepository.deleteById(orderID);

    }


    public Order getOrder(String orderID) {
        return orderRepository.findById(orderID).orElse(null);
    }


    public List<Order> getAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }

}
