package com.kmbl.OrderManagementService.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kmbl.OrderManagementService.configuration.Constants;
import com.kmbl.OrderManagementService.exceptions.KafkaProcessingException;
import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;
import com.kmbl.OrderManagementService.exceptions.ServiceExceptions;
import com.kmbl.OrderManagementService.models.*;
import com.kmbl.OrderManagementService.models.kafka.CancelOrderMessage;
import com.kmbl.OrderManagementService.models.kafka.PaymentFailureMessage;
import com.kmbl.OrderManagementService.models.kafka.PaymentMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmbl.OrderManagementService.repositories.OrderRepository;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final CustomerService customerService;
    private final ObjectMapper objectMapper;
    private final KafkaMessageProducer messagingService;
    private final DynamoDBMapper mapper;
    
    @Autowired
    public OrderService(OrderRepository orderRepository, WebClient webClient, ObjectMapper objectMapper, KafkaMessageProducer kafkaMessageProducer,DynamoDBMapper mapper,CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.messagingService = kafkaMessageProducer;
        this.customerService = customerService;
        this.mapper = mapper;
    }


    public OrderDto createOrder(Order order) {
        try {
            validateOrderRequest(order);
            List<OrderItem> orderItems = order.getOrderItems();
            Customer customer = customerService.getCustomerById(order.getCustomerId());
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
                    .uri("/api/inventory/updateInventory")
                    .body(BodyInserters.fromValue(imsRequestBodyList))
                    .retrieve()
                    .bodyToMono(String.class);
    
            // Blocking call to get the JSON response
            String jsonResult = data.block();
    
            // Deserialize JSON to IMSResponseObject
            IMSResponseObject imsResponseObject = objectMapper.readValue(jsonResult, IMSResponseObject.class);
            String status = imsResponseObject.getOrderStatus();
            logger.info("Status from IMS: " + status);
            logger.info("Data from IMS: " + imsResponseObject.getOrderItemStatus());
    
            // Handle different statuses
            if (Constants.ORDER_RESPONSE_STATUS_PARTIAL.equals(status) || Constants.ORDER_RESPONSE_STATUS_COMPLETE.equals(status)) {
                List<ResponseObject> responseObjects = imsResponseObject.getOrderItemStatus();
                List<OrderItem> updatedOrderItems = new ArrayList<>();

                for (ResponseObject responseObject : responseObjects) {
                    logger.info("Data from IMS: responseObject" + responseObject.getData());
                    String orderItemstatus = responseObject.getOrderItemStatus();
                    Integer count = responseObject.getFullfillCount();
                    OrderItem orderItemRes = responseObject.getData();
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(orderItemRes.getProductId());
                    orderItem.setSellerId(orderItemRes.getSellerId());
                    orderItem.setQuantity(count);
                    if( Constants.ORDER_RESPONSE_STATUS_FAILED.equals(orderItemstatus)) orderItem.setStatus(OrderStatus.Status.OUT_OF_STOCK.getValue());
                    updatedOrderItems.add(orderItem);
                }
                order.setOrderItems(updatedOrderItems);
                OrderDto orderDto = new OrderDto(order);
                List<OrderItem> filteredOrderItem = order.getOrderItems().stream()
                        .filter( item -> !item.getStatus().equals(OrderStatus.Status.OUT_OF_STOCK.getValue())).collect(Collectors.toList());
                order.setOrderItems(filteredOrderItem);

                Order savedOrder = orderRepository.save(order);
                //TODO:: Transactionality is an issue due to multiple table updates.
                customer.addToOrderHistory(order.getOrderId());
                customerService.updateCustomer(customer);
                logger.info("Order saved: " + savedOrder);
                orderDto.setOrderId(savedOrder.getOrderId());
                orderDto.setUpdateDate(savedOrder.getUpdatedAt());
                orderDto.setCreateDate(savedOrder.getCreatedAt());
                return orderDto;
            } else if (Constants.ORDER_RESPONSE_STATUS_FAILED.equals(status)) {
                logger.info("Order rejected by IMS.");
                return null; // Order not created
            } else {
                logger.error("Unexpected status received from IMS: " + status);
                return null;
            }
        }
        catch (Exception e) {
            logger.error("Error occurred while creating order: " + e.getMessage());
            return null;
        }
    }

    private void validateOrderRequest(Order order) throws ServiceExceptions{
        if(order.getCustomerId() == null || order.getCustomerId().isEmpty()) throw new ServiceExceptions(ServiceExceptions.Type.BAD_REQUEST,"customer id is empty");
        if(order.getOrderItems().size() <= 0 ) throw new ServiceExceptions(ServiceExceptions.Type.BAD_REQUEST, "orderItem List cannot be empty");
        for(OrderItem item : order.getOrderItems()) {
            if(item.getSellerId() == null || item.getSellerId().isEmpty()) throw new ServiceExceptions(ServiceExceptions.Type.BAD_REQUEST,"sellerId is empty");
            if(item.getProductId() == null || item.getProductId().isEmpty()) throw new ServiceExceptions(ServiceExceptions.Type.BAD_REQUEST,"productId is empty");
            if(item.getQuantity() == null || item.getQuantity() <=0 ) throw new ServiceExceptions(ServiceExceptions.Type.BAD_REQUEST,"quantity cannot be zero or null");
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
