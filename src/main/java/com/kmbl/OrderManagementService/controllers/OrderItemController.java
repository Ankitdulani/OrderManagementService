package com.kmbl.OrderManagementService.controllers;

import com.kmbl.OrderManagementService.models.OrderItem;
import com.kmbl.OrderManagementService.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/OrderItem")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping(value = "/{orderItemID}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OrderItem> getOrder(@PathVariable("orderItemID") String orderItemID) {
        OrderItem item = orderItemService.getOrderItem(orderItemID);
        if (item == null) {
            return new ResponseEntity<>(item, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createOrderDetails(@RequestBody OrderItem orderItem) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderItemID}")
    public ResponseEntity<OrderItem> deleteOrderDetails(@PathVariable("OrderItemID") String id) {
        orderItemService.deleteOrderItem(id);
        OrderItem existingItem = orderItemService.getOrderItem(id);
        if (existingItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        orderItemService.deleteOrderItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{OrderItemID}")
    public ResponseEntity<HttpStatus> updateOrderItem(@PathVariable("id") String orderItemID, @RequestBody OrderItem orderItem) {
        orderItemService.updateOrderItem(orderItemID, orderItem);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
