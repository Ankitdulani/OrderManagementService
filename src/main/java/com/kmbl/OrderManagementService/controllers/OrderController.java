package com.kmbl.OrderManagementService.controllers;


import com.kmbl.OrderManagementService.models.Order;
import com.kmbl.OrderManagementService.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/{orderID}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Order> getOrder(@PathVariable("orderID") String orderID) {
        try {
            Order order = orderService.getOrder(orderID);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Order>> getAllOrderItems() {
        List<Order> orderItems = orderService.getAllOrders();
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Order> createOrderDetails(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PostMapping(value = "/cancel/{orderId}")
    public ResponseEntity<Order> cancel(@PathVariable("orderId") String orderId) {
        try{
            Order existingItem = orderService.getOrder(orderId);
            orderService.cancelOrder(existingItem);
            return new ResponseEntity<>(existingItem,HttpStatus.OK);
        }catch (ResourceNotFoundException ex){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{orderID}")
    public ResponseEntity<Order> deleteOrderDetails(@PathVariable("orderID") String orderID) {
        try {
          Order order = orderService.getOrder(orderID);
          orderService.deleteOrder(order.getOrderId());
          return new ResponseEntity<>(order, HttpStatus.NO_CONTENT);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{orderID}")
    public ResponseEntity<Order> updateOrder(@PathVariable("orderID") String orderID, @RequestBody Order order) {
        try {
            order.setOrderId(orderID);
            Order updatedOrder = orderService.updateOrder(order);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
