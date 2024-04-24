package com.kmbl.OrderManagementService.controllers;

import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;
import com.kmbl.OrderManagementService.models.OrderItem;
import com.kmbl.OrderManagementService.models.OrderStatus;
import com.kmbl.OrderManagementService.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping(value = "/{orderItemID}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OrderItem> getOrder(@PathVariable("orderItemID") String orderItemID) {
        try {
            OrderItem orderItem = orderItemService.getOrderItem(orderItemID);
            return new ResponseEntity<>(orderItem, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrderItem> createOrderDetails(@RequestBody OrderItem orderItem) {
        orderItem.setStatus(OrderStatus.PRE_BOOKED);
        OrderItem createdOrderItem = orderItemService.createOrder(orderItem);
        return new ResponseEntity<>(createdOrderItem, HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderItemID}")
    public ResponseEntity<OrderItem> deleteOrderDetails(@PathVariable("orderItemID") String orderItemID) {
        try {
            OrderItem orderItem = orderItemService.getOrderItem(orderItemID);
            orderItemService.deleteOrderItem(orderItemID);
            return new ResponseEntity<>(orderItem, HttpStatus.NO_CONTENT);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{orderItemID}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable("orderItemID") String orderItemID, @RequestBody OrderItem orderItem) {
        try {
            OrderItem updatedOrderItem = orderItemService.updateOrderItem(orderItemID, orderItem);
            return new ResponseEntity<>(updatedOrderItem, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
