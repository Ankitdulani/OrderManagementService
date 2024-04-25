package com.kmbl.OrderManagementService.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kmbl.OrderManagementService.exceptions.KafkaProcessingException;
import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;
import com.kmbl.OrderManagementService.models.Order;
import com.kmbl.OrderManagementService.models.OrderStatus;
import com.kmbl.OrderManagementService.models.kafka.CancelOrderMessage;
import com.kmbl.OrderManagementService.models.kafka.PaymentFailureMessage;
import com.kmbl.OrderManagementService.models.kafka.PaymentMessage;
import com.kmbl.OrderManagementService.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaMessageProducer messagingService;
    private final DynamoDBMapper mapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, KafkaMessageProducer kafkaMessageProducer, DynamoDBMapper mapper) {
        this.orderRepository = orderRepository;
        this.messagingService = kafkaMessageProducer;
        this.mapper = mapper;
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
        if (order == null) {
            throw new ResourceNotFoundException("Order Id : {} is not present", orderID);
        }
        return order;
    }

    public List<Order> getAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }

    public void cancelOrder(Order order) {
        //TODO: validation if already cancelled dont update inventory to ensure idempotency
        CancelOrderMessage message = new CancelOrderMessage(order);
        messagingService.publish(message);
        order.getOrderItems().stream().forEach(
                orderItem -> {
                    orderItem.setStatus(OrderStatus.Status.ORDER_CANCELLED.getValue());
                }
        );
        orderRepository.save(order);
    }

    public void paymentProcessing(PaymentMessage message) throws KafkaProcessingException, ResourceNotFoundException {
        if (message.getOrderId() == null || message.getOrderId().isEmpty()) {
            throw new KafkaProcessingException(KafkaProcessingException.KafkaException.INVALID_MESSAGE, "Order Id missing");
        }
        Order order = orderRepository
                .findById(message.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Order Id : {} is not present", message.getOrderId())));

        //In case of Failure publishing Message
        if (message.isPaymentFailed()) {
            PaymentFailureMessage inventoryMessage = new PaymentFailureMessage(order);
            messagingService.publish(inventoryMessage);
        }

        OrderStatus.Status status =
                message.isPaymentSuccessful() ?
                        OrderStatus.Status.PAYMENT_SUCCESSFUL : OrderStatus.Status.PAYMENT_FAILED;

        order.getOrderItems().stream().forEach(
                orderItem -> orderItem.setStatus(status.getValue()));

        orderRepository.save(order);

    }
}
