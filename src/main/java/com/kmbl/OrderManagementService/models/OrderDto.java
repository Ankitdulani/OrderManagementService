package com.kmbl.OrderManagementService.models;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String orderId;
    private String customerId;
    private String deliveryAddress;
    private List<OrderItemDto> orderItems;
    private Date createDate;
    private Date updateDate;


    public OrderDto(Order order) {
        setOrderId(order.getOrderId());
        setCustomerId(order.getCustomerId());
        setDeliveryAddress(order.getDeliveryAddress());
        setCreateDate(order.getCreatedAt());
        setUpdateDate(order.getUpdatedAt());
        setOrderItems(order.getOrderItems().stream().map(OrderItemDto::new).collect(Collectors.toList()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class OrderItemDto {

        private String productId;
        private Integer quantity;
        private String status=OrderStatus.Status.PAYMENT_DUE.getValue();
        private String sellerId;

        public OrderItemDto(OrderItem orderItem) {
            setProductId(orderItem.getProductId());
            setQuantity(orderItem.getQuantity());
            setSellerId(orderItem.getSellerId());
            setStatus(orderItem.getStatus());
        }
    }
}
