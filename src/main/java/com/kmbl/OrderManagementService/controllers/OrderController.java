package com.kmbl.OrderManagementService.controllers;


import com.kmbl.OrderManagementService.models.Order;
import com.kmbl.OrderManagementService.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/{orderID}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Order> getOrder(@PathVariable("orderID") String orderID) {
        Order item = orderService.getOrder(orderID);
        if (item == null) {
            return new ResponseEntity<>(item, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Order>> getAllOrderItems() {
        List<Order> orderItems = orderService.getAllOrders();
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createOrderDetails(@RequestBody Order order) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderID}")
    public ResponseEntity<Order> deleteSellerDetails(@PathVariable("OrderID") String id) {
        orderService.deleteOrder(id);
        Order existingItem = orderService.getOrder(id);
        if (existingItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{OrderID}")
    public ResponseEntity<HttpStatus> updateSeller(@PathVariable("id") String orderID, @RequestBody Order order) {
        orderService.updateOrder(orderID, order);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
