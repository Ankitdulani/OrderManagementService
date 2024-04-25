package com.kmbl.OrderManagementService.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;
import com.kmbl.OrderManagementService.exceptions.ServiceExceptions;
import com.kmbl.OrderManagementService.models.Customer;
import com.kmbl.OrderManagementService.models.Order;
import com.kmbl.OrderManagementService.models.OrderItem;
import com.kmbl.OrderManagementService.models.OrderStatus;
import com.kmbl.OrderManagementService.models.kafka.CancelOrderMessage;
import com.kmbl.OrderManagementService.repositories.CustomerRepository;
import com.kmbl.OrderManagementService.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final KafkaMessagingService messagingService;
    private final DynamoDBMapper mapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, KafkaMessagingService kafkaMessagingService,DynamoDBMapper mapper,CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.messagingService = kafkaMessagingService;
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    public Order createOrder(Order order) throws ServiceExceptions,ResourceNotFoundException{
        validateOrder(order);
        Customer customer = customerRepository
                                .findById(order.getCustomerId())
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("Customer : { %s }  doesn't Exist", order.getCustomerId()));
        // TODO:: Validate the inventory if Products exist
        Order entity = orderRepository.save(order);
        customer.addToOrderHistory(entity.getOrderId());
        customerRepository.save(customer);
        return order;
    }

    public Order updateOrder(Order order) throws ResourceNotFoundException {
        Order existingOrder = orderRepository.findById(order.getOrderId()).orElse(null);
        if (existingOrder == null) {
            throw new ResourceNotFoundException("Order Id : {%s} is not present", order.getOrderId());
        }
        return orderRepository.save(order);
    }

    public void deleteOrder(String orderID) {
        orderRepository.deleteById(orderID);
    }

    public Order getOrder(String orderID) throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderID).orElse(null);
        if(order == null) {
            throw new ResourceNotFoundException("Order Id : {%s} is not present", orderID);
        }
        return order;
    }

    public List<Order> getAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }

    public void cancelOrder(Order order) {
        //TODO: Audit Logging to check if event is already processed
        CancelOrderMessage message = new CancelOrderMessage(order);
        messagingService.publish(message);
        order.getOrderItems().stream().forEach(
                orderItem -> { orderItem.setStatus(OrderStatus.Status.ORDER_CANCELLED.getValue());}
        );
    }

    private void validateOrder(Order order) throws ServiceExceptions{
        if(order.getCustomerId() == null || order.getCustomerId().isEmpty()) {
            throw new ServiceExceptions(ServiceExceptions.Type.VALIDATION_FAILURE,"customerId cannot be empty");
        }
        if(order.getOrderItems().isEmpty()) {
            throw new ServiceExceptions(ServiceExceptions.Type.VALIDATION_FAILURE,"orderItems cannot be empty");
        }
        for(OrderItem item : order.getOrderItems()) {
            if(!validateOrderItem(item)) throw new ServiceExceptions( ServiceExceptions.Type.VALIDATION_FAILURE,"orderItem are invalid");
        }
    }

    private boolean validateOrderItem(OrderItem orderItem) {
        if(orderItem.getProductId() == null || orderItem.getProductId().isEmpty()) { return false; }
        if(orderItem.getQuantity() <= 0) { return false; }
        if(orderItem.getSellerId()== null || orderItem.getSellerId().isEmpty()) { return false; }
        return true;
    }
}
