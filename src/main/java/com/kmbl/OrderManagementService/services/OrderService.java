package com.kmbl.OrderManagementService.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kmbl.OrderManagementService.exceptions.KafkaProcessingException;
import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;
import com.kmbl.OrderManagementService.models.IMSRequestBody;
import com.kmbl.OrderManagementService.models.IMSResponseObject;
import com.kmbl.OrderManagementService.models.Order;
import com.kmbl.OrderManagementService.models.OrderStatus;
import com.kmbl.OrderManagementService.models.ResponseObject;
import com.kmbl.OrderManagementService.models.kafka.CancelOrderMessage;
import com.kmbl.OrderManagementService.models.kafka.PaymentFailureMessage;
import com.kmbl.OrderManagementService.models.kafka.PaymentMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmbl.OrderManagementService.models.OrderItem;
import com.kmbl.OrderManagementService.repositories.OrderRepository;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

@Service
public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    @Value("${inventoryManagementService.endpoint}")
    private String inventoryManagementServiceEndpoint;
    
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final KafkaMessageProducer messagingService;
    private final DynamoDBMapper mapper;
    
    @Autowired
    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder, ObjectMapper objectMapper, KafkaMessageProducer kafkaMessageProducer,DynamoDBMapper mapper) {
        this.orderRepository = orderRepository;
        this.webClient = webClientBuilder.baseUrl(inventoryManagementServiceEndpoint).build();
        this.objectMapper = objectMapper;
        this.messagingService = kafkaMessageProducer;
        this.mapper = mapper;
    }


    public Order createOrder(Order order) {
        try {
            List<OrderItem> orderItems = order.getOrderItems();
            List<IMSRequestBody> imsRequestBodyList = new ArrayList<>();
    
            // Convert OrderItems to IMSRequestBody
            for (OrderItem orderItem : orderItems) {
                IMSRequestBody imsRequestBody = new IMSRequestBody();
                imsRequestBody.setProductID(orderItem.getProductId());
                imsRequestBody.setSellerID(orderItem.getSellerId());
                imsRequestBody.setCount(orderItem.getQuantity());
                imsRequestBodyList.add(imsRequestBody);
            }
    
            // Make POST request with IMSRequestBody
            Mono<String> data = webClient.post()
                    .uri("/api/inventory/updateinventory")
                    .body(BodyInserters.fromValue(imsRequestBodyList))
                    .retrieve()
                    .bodyToMono(String.class);
    
            // Blocking call to get the JSON response
            String jsonResult = data.block();
    
            // Deserialize JSON to IMSResponseObject
            IMSResponseObject imsResponseObject = objectMapper.readValue(jsonResult, IMSResponseObject.class);
            String status = imsResponseObject.getInventoryOrderStatus();
            logger.info("Status from IMS: " + status);
    
            // Handle different statuses
            if ("PARTIAL_ORDER".equals(status) || "COMPLETE_ORDER".equals(status)) {

                List<ResponseObject> responseObjects = imsResponseObject.getResponseObjects();

                List<OrderItem> updatedOrderItems = new ArrayList<>();

                for (ResponseObject responseObject : responseObjects) {

                    String orderItemstatus = responseObject.getStatus();
                    Integer count = responseObject.getCount();
                    OrderItem orderItemRes = responseObject.getData();

                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(orderItemRes.getProductId());
                    orderItem.setSellerId(orderItem.getSellerId());
                    orderItem.setQuantity(count);
                    orderItem.setStatus(orderItemstatus);

                    updatedOrderItems.add(orderItem);
                    
                }

                order.setOrderItems(updatedOrderItems);
                Order savedOrder = orderRepository.save(order);
                logger.info("Order saved: " + savedOrder);
                return savedOrder;
            } else if ("FAILED_ORDER".equals(status)) {
                logger.info("Order rejected by IMS.");
                return null; // Order not created
            } else {
                logger.error("Unexpected status received from IMS: " + status);
                
                return null;
            }
        } catch (Exception e) {
            logger.error("Error occurred while creating order: " + e.getMessage());
            return null;
        }
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
