package com.kmbl.OrderManagementService.models.kafka;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kmbl.OrderManagementService.exceptions.ServiceExceptions;
import com.kmbl.OrderManagementService.models.Order;
import com.kmbl.OrderManagementService.models.OrderItem;
import com.kmbl.OrderManagementService.services.utils.ServiceUtils;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelOrderMessage implements Message {

    private String orderId;
    private Date createdAt;
    private List<OrderItems> orderItems;

    public CancelOrderMessage(Order order) {
        setOrderId(order.getOrderId());
        createdAt = new Date();
        orderItems = order.getOrderItems().stream().map(OrderItems::new).collect(Collectors.toList());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class OrderItems {
        private String productId;
        private int quantity;
        private String sellerId;
        private Date date;

        public OrderItems(OrderItem item) {
            setProductId(item.getProductId());
            setQuantity(item.getQuantity());
            setSellerId(item.getSellerId());
            setDate(new Date());
        }
    }

    @Override
    @JsonIgnore
    public String getMessage() throws ServiceExceptions {
        return ServiceUtils.convertToString(this);
    }

    @Override
    @JsonIgnore
    public KafkaMessagesType getType() {
        return KafkaMessagesType.DELETED_ORDER;
    }
}
